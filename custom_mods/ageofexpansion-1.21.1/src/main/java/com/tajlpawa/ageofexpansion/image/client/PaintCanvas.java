package com.tajlpawa.ageofexpansion.image.client;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

// 每笔记录首次触碰前的像素，透明度按整笔计算；撤销差量超预算后落盘。
public final class PaintCanvas implements AutoCloseable {
    public final BufferedImage image;
    private final int undoLimit;
    private final ArrayDeque<Edit> undo=new ArrayDeque<>();
    private long memory;private final long memoryLimit;
    private int[] before;private BitSet touched;private double lastX,lastY;
    private int diameter,color,opacity;private boolean erase;
    private Path directory;
    private int minX=Integer.MAX_VALUE,minY=Integer.MAX_VALUE,maxX=-1,maxY=-1;
    public int[] takeDirty(){if(maxX<0)return null;int[] r={minX,minY,maxX,maxY};minX=minY=Integer.MAX_VALUE;maxX=maxY=-1;return r;}
    private static final class Edit {byte[] bytes;Path file;Edit(byte[] bytes){this.bytes=bytes;}}
    public PaintCanvas(BufferedImage source,int limit){this(source,limit,16L*1024*1024);}
    public PaintCanvas(BufferedImage source,int limit,long memoryLimit){image=new BufferedImage(source.getWidth(),source.getHeight(),BufferedImage.TYPE_INT_ARGB);image.setRGB(0,0,source.getWidth(),source.getHeight(),source.getRGB(0,0,source.getWidth(),source.getHeight(),null,0,source.getWidth()),0,source.getWidth());undoLimit=limit;this.memoryLimit=memoryLimit;}
    public boolean painting(){return touched!=null;}
    public int undoCount(){return undo.size();}
    public void begin(double x,double y,int diameter,int color,int opacity,boolean erase){if(painting())throw new IllegalStateException("stroke");this.diameter=Math.clamp(diameter,1,512);this.color=color;this.opacity=Math.clamp(opacity,0,100);this.erase=erase;before=new int[image.getWidth()*image.getHeight()];touched=new BitSet(before.length);lastX=x;lastY=y;stamp(x,y);}
    public void drag(double x,double y){if(!painting())return;double distance=Math.hypot(x-lastX,y-lastY);int steps=Math.max(1,(int)Math.ceil(distance/Math.max(.5,diameter/4.0)));for(int n=1;n<=steps;n++)stamp(lastX+(x-lastX)*n/steps,lastY+(y-lastY)*n/steps);lastX=x;lastY=y;}
    private void stamp(double cx,double cy){double radius=diameter/2.0;int w=image.getWidth(),h=image.getHeight();
        if(diameter==1){pixel((int)Math.floor(cx),(int)Math.floor(cy));return;}
        for(int y=Math.max(0,(int)Math.floor(cy-radius));y<Math.min(h,(int)Math.ceil(cy+radius));y++)for(int x=Math.max(0,(int)Math.floor(cx-radius));x<Math.min(w,(int)Math.ceil(cx+radius));x++)if(Math.pow(x+.5-cx,2)+Math.pow(y+.5-cy,2)<=radius*radius)pixel(x,y);
    }
    private void pixel(int x,int y){int w=image.getWidth();if(x<0||y<0||x>=w||y>=image.getHeight())return;int index=y*w+x;if(!touched.get(index)){before[index]=image.getRGB(x,y);touched.set(index);}int old=before[index],a=old>>>24,sa=Math.round(opacity*255f/100);int result;
        if(erase)result=((a*(255-sa)+127)/255)<<24|(old&0xffffff);
        else{int outA=sa+(a*(255-sa)+127)/255;result=outA<<24;for(int shift:new int[]{16,8,0}){int out=outA==0?0:(((color>>shift&255)*sa+(old>>shift&255)*a*(255-sa)/255)+outA/2)/outA;result|=Math.clamp(out,0,255)<<shift;}}
        image.setRGB(x,y,result);minX=Math.min(minX,x);maxX=Math.max(maxX,x);minY=Math.min(minY,y);maxY=Math.max(maxY,y);
    }
    public void end()throws IOException{if(!painting())return;try{if(undoLimit>0&&!touched.isEmpty()){
        var out=new ByteArrayOutputStream();try(var data=new DataOutputStream(new DeflaterOutputStream(out))){data.writeInt(touched.cardinality());for(int i=touched.nextSetBit(0);i>=0;i=touched.nextSetBit(i+1)){data.writeInt(i);data.writeInt(before[i]);}}
        var edit=new Edit(out.toByteArray());undo.add(edit);memory+=edit.bytes.length;while(undo.size()>undoLimit)dispose(undo.removeFirst());
        for(var e:undo)if(memory>memoryLimit&&e.bytes!=null){if(directory==null)directory=com.tajlpawa.ageofexpansion.image.ImageStorage.undoDirectory();e.file=Files.createTempFile(directory,"undo-",".bin");Files.write(e.file,e.bytes);memory-=e.bytes.length;e.bytes=null;}
    }}finally{before=null;touched=null;}}
    public boolean undo()throws IOException{if(painting()||undo.isEmpty())return false;var edit=undo.getLast();try(var raw=edit.bytes!=null?new ByteArrayInputStream(edit.bytes):Files.newInputStream(edit.file);var in=new DataInputStream(new InflaterInputStream(raw))){int count=in.readInt();for(int n=0;n<count;n++){int i=in.readInt(),pixel=in.readInt();image.setRGB(i%image.getWidth(),i/image.getWidth(),pixel);}}undo.removeLast();dispose(edit);return true;}
    private void dispose(Edit e)throws IOException{if(e.bytes!=null)memory-=e.bytes.length;if(e.file!=null)Files.deleteIfExists(e.file);}
    @Override public void close(){for(var e:undo)try{dispose(e);}catch(IOException ignored){}undo.clear();if(directory!=null)try{Files.deleteIfExists(directory);}catch(IOException ignored){}before=null;touched=null;}
}

