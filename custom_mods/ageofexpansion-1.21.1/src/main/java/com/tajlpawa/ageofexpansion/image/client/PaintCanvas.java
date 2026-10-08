package com.tajlpawa.ageofexpansion.image.client;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

// Pixel edits and selection edits share one transactional, spillable undo history.
public final class PaintCanvas implements AutoCloseable {
    public final BufferedImage image;
    private final int undoLimit;private final long memoryLimit;
    private final ArrayDeque<Edit> undo=new ArrayDeque<>();private long memory;
    private int[] before;private BitSet touched,selection,selectionBefore;private double lastX,lastY;
    private int diameter,color,opacity;private boolean erase;private Path directory;
    private long imageRevision,selectionRevision;private boolean lastUndoPixels;
    private int minX=Integer.MAX_VALUE,minY=Integer.MAX_VALUE,maxX=-1,maxY=-1;
    private static final class Edit {byte[] bytes;Path file;Edit(byte[] bytes){this.bytes=bytes;}}
    public PaintCanvas(BufferedImage source,int limit){this(source,limit,16L*1024*1024);}
    public PaintCanvas(BufferedImage source,int limit,long budget){image=copy(source);undoLimit=limit;memoryLimit=budget;}
    private static BufferedImage copy(BufferedImage source){var out=new BufferedImage(source.getWidth(),source.getHeight(),BufferedImage.TYPE_INT_ARGB);out.setRGB(0,0,source.getWidth(),source.getHeight(),source.getRGB(0,0,source.getWidth(),source.getHeight(),null,0,source.getWidth()),0,source.getWidth());return out;}
    private static BitSet cloneMask(BitSet mask){return mask==null?null:(BitSet)mask.clone();}
    public BitSet selection(){return cloneMask(selection);}
    public boolean hasSelection(){return selection!=null;}
    public boolean selected(int x,int y){return x>=0&&y>=0&&x<image.getWidth()&&y<image.getHeight()&&(selection==null||selection.get(y*image.getWidth()+x));}
    public long imageRevision(){return imageRevision;}
    public long selectionRevision(){return selectionRevision;}
    public boolean lastUndoPixels(){return lastUndoPixels;}
    public int[] takeDirty(){if(maxX<0)return null;int[] r={minX,minY,maxX,maxY};minX=minY=Integer.MAX_VALUE;maxX=maxY=-1;return r;}
    private void mark(int i){int x=i%image.getWidth(),y=i/image.getWidth();minX=Math.min(minX,x);maxX=Math.max(maxX,x);minY=Math.min(minY,y);maxY=Math.max(maxY,y);}
    public boolean painting(){return touched!=null;}
    public int undoCount(){return undo.size();}
    private void transaction(){if(painting())throw new IllegalStateException("stroke");before=new int[image.getWidth()*image.getHeight()];touched=new BitSet(before.length);selectionBefore=cloneMask(selection);}
    private void set(int i,int value){int old=image.getRGB(i%image.getWidth(),i/image.getWidth());if(old==value)return;if(!touched.get(i)){before[i]=old;touched.set(i);}image.setRGB(i%image.getWidth(),i/image.getWidth(),value);mark(i);}
    public void begin(double x,double y,int diameter,int color,int opacity,boolean erase){transaction();this.diameter=Math.clamp(diameter,1,512);this.color=color;this.opacity=Math.clamp(opacity,0,100);this.erase=erase;lastX=x;lastY=y;stamp(x,y);}
    public void drag(double x,double y){if(!painting())return;double distance=Math.hypot(x-lastX,y-lastY);int steps=Math.max(1,(int)Math.ceil(distance/Math.max(.5,diameter/4.0)));for(int n=1;n<=steps;n++)stamp(lastX+(x-lastX)*n/steps,lastY+(y-lastY)*n/steps);lastX=x;lastY=y;}
    private void stamp(double cx,double cy){double radius=diameter/2.0;int w=image.getWidth(),h=image.getHeight();if(diameter==1){pixel((int)Math.floor(cx),(int)Math.floor(cy));return;}
        for(int y=Math.max(0,(int)Math.floor(cy-radius));y<Math.min(h,(int)Math.ceil(cy+radius));y++)for(int x=Math.max(0,(int)Math.floor(cx-radius));x<Math.min(w,(int)Math.ceil(cx+radius));x++)if(Math.pow(x+.5-cx,2)+Math.pow(y+.5-cy,2)<=radius*radius)pixel(x,y);
    }
    public static int over(int source,int target){int sa=source>>>24,a=target>>>24,outA=sa+(a*(255-sa)+127)/255,result=outA<<24;if(sa==0)return target;for(int shift:new int[]{16,8,0}){int out=outA==0?0:(((source>>shift&255)*sa+(target>>shift&255)*a*(255-sa)/255)+outA/2)/outA;result|=Math.clamp(out,0,255)<<shift;}return result;}
    private void pixel(int x,int y){if(!selected(x,y))return;int i=y*image.getWidth()+x,old=touched.get(i)?before[i]:image.getRGB(x,y),a=old>>>24,sa=Math.round(opacity*255f/100);set(i,erase?((a*(255-sa)+127)/255)<<24|(old&0xffffff):over(sa<<24|(color&0xffffff),old));}
    public void fill(BitSet region,int color,int opacity,boolean erase)throws IOException{transaction();this.color=color;this.opacity=Math.clamp(opacity,0,100);this.erase=erase;try{for(int i=region.nextSetBit(0);i>=0&&i<before.length;i=region.nextSetBit(i+1))pixel(i%image.getWidth(),i/image.getWidth());end();}catch(Exception e){cancel();throw e;}}
    public void select(BitSet region,boolean subtract)throws IOException{if(subtract&&selection==null)return;transaction();selection=selection==null?new BitSet():cloneMask(selection);if(subtract)selection.andNot(region);else selection.or(region);selection.clear(image.getWidth()*image.getHeight(),Math.max(image.getWidth()*image.getHeight(),selection.length()));end();}
    public void clearSelection()throws IOException{if(selection==null)return;transaction();selection=null;end();}
    public record Move(BufferedImage background,BufferedImage foreground,BitSet mask,int[] bounds){}
    public Move prepareMove(){if(selection==null||selection.isEmpty())return null;var background=copy(image);int[] b=PaintRegion.bounds(selection,image.getWidth());var foreground=new BufferedImage(b[2],b[3],BufferedImage.TYPE_INT_ARGB);for(int i=selection.nextSetBit(0);i>=0;i=selection.nextSetBit(i+1)){int x=i%image.getWidth(),y=i/image.getWidth();foreground.setRGB(x-b[0],y-b[1],image.getRGB(x,y));background.setRGB(x,y,0);}return new Move(background,foreground,cloneMask(selection),b);}
    // Moving past the canvas crops both pixels and selection only at commit; undo retains the complete source.
    public void move(Move move,int dx,int dy)throws IOException{if(dx==0&&dy==0)return;transaction();try{int w=image.getWidth(),h=image.getHeight();selection=new BitSet(w*h);for(int i=move.mask.nextSetBit(0);i>=0;i=move.mask.nextSetBit(i+1))set(i,0);for(int i=move.mask.nextSetBit(0);i>=0;i=move.mask.nextSetBit(i+1)){int x=i%w,y=i/w;long targetX=(long)x+dx,targetY=(long)y+dy;if(targetX<0||targetY<0||targetX>=w||targetY>=h)continue;int xx=(int)targetX,yy=(int)targetY,j=yy*w+xx;selection.set(j);set(j,over(move.foreground.getRGB(x-move.bounds[0],y-move.bounds[1]),image.getRGB(xx,yy)));}end();}catch(Exception e){cancel();throw e;}}
    public void cancel(){if(!painting())return;for(int i=touched.nextSetBit(0);i>=0;i=touched.nextSetBit(i+1)){image.setRGB(i%image.getWidth(),i/image.getWidth(),before[i]);mark(i);}selection=selectionBefore;before=null;touched=null;selectionBefore=null;}
    public void end()throws IOException{if(!painting())return;try{for(int i=touched.nextSetBit(0);i>=0;i=touched.nextSetBit(i+1))if(before[i]==image.getRGB(i%image.getWidth(),i/image.getWidth()))touched.clear(i);boolean maskChanged=!Objects.equals(selection,selectionBefore);
        if(undoLimit>0&&(!touched.isEmpty()||maskChanged)){var out=new ByteArrayOutputStream();try(var data=new DataOutputStream(new DeflaterOutputStream(out))){long[] mask=selectionBefore==null?null:selectionBefore.toLongArray();data.writeInt(mask==null?-1:mask.length);if(mask!=null)for(long n:mask)data.writeLong(n);data.writeInt(touched.cardinality());for(int i=touched.nextSetBit(0);i>=0;i=touched.nextSetBit(i+1)){data.writeInt(i);data.writeInt(before[i]);}}var edit=new Edit(out.toByteArray());if(memory+edit.bytes.length>memoryLimit){if(directory==null)directory=com.tajlpawa.ageofexpansion.image.ImageStorage.undoDirectory();edit.file=Files.createTempFile(directory,"undo-",".bin");try{Files.write(edit.file,edit.bytes);edit.bytes=null;}catch(IOException e){Files.deleteIfExists(edit.file);throw e;}}undo.add(edit);if(edit.bytes!=null)memory+=edit.bytes.length;while(undo.size()>undoLimit){var expired=undo.removeFirst();try{dispose(expired);}catch(IOException e){com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.warn("Could not delete expired drawing undo",e);}}}

        if(!touched.isEmpty())imageRevision++;if(maskChanged)selectionRevision++;
    }catch(IOException e){cancel();throw e;}finally{before=null;touched=null;selectionBefore=null;}}
    public boolean undo()throws IOException{lastUndoPixels=false;if(painting()||undo.isEmpty())return false;var edit=undo.getLast();BitSet restored=null;int[] indices,colors;int maximum=image.getWidth()*image.getHeight();
        try(var raw=edit.bytes!=null?new ByteArrayInputStream(edit.bytes):Files.newInputStream(edit.file);var in=new DataInputStream(new InflaterInputStream(raw))){int size=in.readInt();if(size< -1||size>(maximum+63)/64)throw new IOException("undo mask");if(size>=0){long[] values=new long[size];for(int i=0;i<size;i++)values[i]=in.readLong();restored=BitSet.valueOf(values);}int count=in.readInt();if(count<0||count>maximum)throw new IOException("undo pixels");indices=new int[count];colors=new int[count];for(int n=0;n<count;n++){indices[n]=in.readInt();colors[n]=in.readInt();if(indices[n]<0||indices[n]>=maximum)throw new IOException("undo index");}}
        for(int n=0;n<indices.length;n++){int i=indices[n];image.setRGB(i%image.getWidth(),i/image.getWidth(),colors[n]);mark(i);}if(indices.length>0){lastUndoPixels=true;imageRevision++;}if(!Objects.equals(restored,selection)){selection=restored;selectionRevision++;}undo.removeLast();try{dispose(edit);}catch(IOException e){com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.warn("Could not delete consumed drawing undo",e);}return true;}
    private void dispose(Edit e)throws IOException{if(e.bytes!=null)memory-=e.bytes.length;if(e.file!=null)Files.deleteIfExists(e.file);}
    @Override public void close(){for(var e:undo)try{dispose(e);}catch(IOException ignored){}undo.clear();if(directory!=null)try{Files.deleteIfExists(directory);}catch(IOException ignored){}before=null;touched=null;}
}
