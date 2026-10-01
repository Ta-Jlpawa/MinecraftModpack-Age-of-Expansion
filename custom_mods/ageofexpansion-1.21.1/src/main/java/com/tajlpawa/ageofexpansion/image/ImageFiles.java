package com.tajlpawa.ageofexpansion.image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;

// 解码前检查尺寸；磁盘和像素操作均由有界后台队列执行。
public final class ImageFiles {
    public static final int MAX_BYTES=32*1024*1024,MAX_SIDE=2048;
    public static final ThreadPoolExecutor WORK=new ThreadPoolExecutor(2,2,30,TimeUnit.SECONDS,new ArrayBlockingQueue<>(16),r->{var t=new Thread(r,"display-image-io");t.setDaemon(true);return t;},new ThreadPoolExecutor.AbortPolicy());
    public record Decoded(BufferedImage image,byte[] png,byte[] thumbnail,boolean visible){}
    public record Prepared(ImageAsset asset,byte[] full,byte[] thumbnail){}
    public static BufferedImage decodePixels(byte[] bytes)throws IOException{return readPixels(bytes,false);}
    private static BufferedImage readPixels(byte[] bytes,boolean normalize)throws IOException{
        if(bytes.length<8||bytes.length>MAX_BYTES)throw new IOException("file_size");
        try(var in=new MemoryCacheImageInputStream(new ByteArrayInputStream(bytes))){
            var readers=ImageIO.getImageReaders(in);if(!readers.hasNext())throw new IOException("format");var reader=readers.next();
            try{String format=reader.getFormatName();if(!format.equalsIgnoreCase("png")&&!format.equalsIgnoreCase("jpeg"))throw new IOException("format");reader.setInput(in,true,true);int w=reader.getWidth(0),h=reader.getHeight(0);if(w<1||h<1||w>MAX_SIDE||h>MAX_SIDE)throw new IOException("pixels");
                var source=reader.read(0);var image=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
                if(normalize){var g=image.createGraphics();g.drawImage(source,0,0,null);g.dispose();}else image.setRGB(0,0,w,h,source.getRGB(0,0,w,h,null,0,w),0,w);return image;
            }finally{reader.dispose();}
        }
    }
    public static boolean visible(BufferedImage image){for(int y=0;y<image.getHeight();y++)for(int x=0;x<image.getWidth();x++)if((image.getRGB(x,y)>>>24)!=0)return true;return false;}
    public static BufferedImage thumbnail(BufferedImage image){int w=image.getWidth(),h=image.getHeight();float ratio=Math.min(1,96f/Math.max(w,h));var result=new BufferedImage(Math.max(1,Math.round(w*ratio)),Math.max(1,Math.round(h*ratio)),BufferedImage.TYPE_INT_ARGB);var g=result.createGraphics();g.drawImage(image,0,0,result.getWidth(),result.getHeight(),null);g.dispose();return result;}
    public static Decoded decode(byte[] bytes)throws IOException{var image=readPixels(bytes,true);return new Decoded(image,png(image),png(thumbnail(image)),visible(image));}
    public static String hash(byte[] bytes){try{return java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(bytes));}catch(java.security.NoSuchAlgorithmException e){throw new AssertionError(e);}}
    public static String pixelHash(BufferedImage image){try{var digest=java.security.MessageDigest.getInstance("SHA-256");var row=java.nio.ByteBuffer.allocate(8+image.getWidth()*4);row.putInt(image.getWidth()).putInt(image.getHeight());digest.update(row.array(),0,8);for(int y=0;y<image.getHeight();y++){row.clear();for(int x=0;x<image.getWidth();x++)row.putInt(image.getRGB(x,y));digest.update(row.array(),0,image.getWidth()*4);}return java.util.HexFormat.of().formatHex(digest.digest());}catch(java.security.NoSuchAlgorithmException e){throw new AssertionError(e);}}
    public static ImageAsset.Variant describe(BufferedImage image,byte[] encoded,String codec){return new ImageAsset.Variant(pixelHash(image),hash(encoded),codec,image.getWidth(),image.getHeight(),encoded.length);}
    public static BufferedImage verify(ImageAsset.Variant v,byte[] bytes)throws IOException{
        if(bytes.length!=v.length()||!hash(bytes).equals(v.digest()))throw new IOException("image_integrity");var image=decodePixels(bytes);
        if(image.getWidth()!=v.width()||image.getHeight()!=v.height()||!pixelHash(image).equals(v.pixels()))throw new IOException("image_pixels");return image;
    }
    public static BufferedImage canonicalPixels(byte[] source,boolean uploaded)throws IOException{return readPixels(source,uploaded);}
    public static Prepared prepare(byte[] source,boolean uploaded)throws IOException{return prepare(readPixels(source,uploaded),source,uploaded);}
    static Prepared prepare(BufferedImage image,byte[] source,boolean uploaded)throws IOException{byte[] best=compactPng(image);String codec="png";
        if(uploaded&&(source[0]&255)==255&&(source[1]&255)==216){try{byte[] jpeg=sanitizeJpeg(source);if(jpeg.length<best.length&&pixelHash(decodePixels(jpeg)).equals(pixelHash(image))){best=jpeg;codec="jpeg";}}catch(IOException ignored){}}
        var thumb=thumbnail(image);byte[] preview=compactPng(thumb);return new Prepared(new ImageAsset(describe(image,best,codec),describe(thumb,preview,"png"),visible(image)),best,preview);
    }
    public static byte[] compactPng(BufferedImage image)throws IOException{
        byte[] best=png(image);String expected=pixelHash(image);var candidates=new java.util.ArrayList<BufferedImage>();candidates.add(image);boolean opaque=true;var colors=new java.util.LinkedHashMap<Integer,Integer>();
        for(int y=0;y<image.getHeight();y++)for(int x=0;x<image.getWidth();x++){int c=image.getRGB(x,y);if(c>>>24!=255)opaque=false;if(colors.size()<=256)colors.computeIfAbsent(c,k->colors.size());}
        if(opaque){var rgb=new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_RGB);rgb.setRGB(0,0,image.getWidth(),image.getHeight(),image.getRGB(0,0,image.getWidth(),image.getHeight(),null,0,image.getWidth()),0,image.getWidth());candidates.add(rgb);}
        if(colors.size()<=256){int n=colors.size();byte[] r=new byte[n],g=new byte[n],b=new byte[n],a=new byte[n];for(var e:colors.entrySet()){int c=e.getKey(),i=e.getValue();r[i]=(byte)(c>>16);g[i]=(byte)(c>>8);b[i]=(byte)c;a[i]=(byte)(c>>>24);}var palette=new java.awt.image.IndexColorModel(8,n,r,g,b,a);var indexed=new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_BYTE_INDEXED,palette);for(int y=0;y<image.getHeight();y++)for(int x=0;x<image.getWidth();x++)indexed.getRaster().setSample(x,y,0,colors.get(image.getRGB(x,y)));candidates.add(indexed);}
        for(var candidate:candidates){var out=new ByteArrayOutputStream();var writer=ImageIO.getImageWritersByFormatName("png").next();try(var stream=new javax.imageio.stream.MemoryCacheImageOutputStream(out)){writer.setOutput(stream);var params=writer.getDefaultWriteParam();params.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);params.setCompressionQuality(0);writer.write(null,new javax.imageio.IIOImage(candidate,null,null),params);}finally{writer.dispose();}byte[] encoded=out.toByteArray();if(encoded.length<best.length&&pixelHash(decodePixels(encoded)).equals(expected))best=encoded;}return best;
    }
    // 只保留JPEG解码必要的色彩标记；图像扫描码流不重新压缩。
    static byte[] sanitizeJpeg(byte[] source)throws IOException{
        var out=new ByteArrayOutputStream();out.write(255);out.write(216);int p=2;
        while(p<source.length){int start=p;if((source[p++]&255)!=255)throw new IOException("format");while(p<source.length&&(source[p]&255)==255)p++;if(p>=source.length)throw new IOException("format");int marker=source[p++]&255;
            if(marker==217){out.write(255);out.write(217);return out.toByteArray();}
            if(marker==0||marker==216||marker>=208&&marker<=215)throw new IOException("format");if(p+2>source.length)throw new IOException("format");int length=(source[p]&255)*256+(source[p+1]&255);if(length<2||p+length>source.length)throw new IOException("format");int data=p+2,end=p+length;
            boolean app=marker>=224&&marker<=239;boolean keep=!app&&marker!=254;
            if(marker==226&&end-data>=14&&new String(source,data,12,java.nio.charset.StandardCharsets.ISO_8859_1).equals("ICC_PROFILE\0"))keep=true;
            if(marker==238&&end-data==12&&new String(source,data,5,java.nio.charset.StandardCharsets.US_ASCII).equals("Adobe"))keep=true;
            if(marker==224&&end-data>=14&&source[data]=='J'&&source[data+1]=='F'&&source[data+2]=='I'&&source[data+3]=='F'&&source[data+4]==0){out.write(255);out.write(marker);out.write(0);out.write(16);out.write(source,data,12);out.write(0);out.write(0);}else if(keep)out.write(source,start,end-start);
            p=end;if(marker==218){int scan=p;while(p<source.length){if((source[p]&255)!=255){p++;continue;}int next=p+1;while(next<source.length&&(source[next]&255)==255)next++;if(next>=source.length)throw new IOException("format");int m=source[next]&255;if(m==0||m>=208&&m<=215){p=next+1;continue;}break;}out.write(source,scan,p-scan);}
        }throw new IOException("format");
    }
    public static byte[] png(BufferedImage image)throws IOException{var out=new ByteArrayOutputStream();if(!ImageIO.write(image,"png",out))throw new IOException("format");return out.toByteArray();}
    public static byte[] read(Path path,int limit)throws IOException{try(var in=Files.newInputStream(path)){byte[] data=in.readNBytes(limit+1);if(data.length>limit)throw new IOException("file_size");return data;}}
    public static void atomicWrite(Path file,byte[] bytes)throws IOException{Files.createDirectories(file.getParent());Path temp=Files.createTempFile(file.getParent(),"image-",".tmp");try{Files.write(temp,bytes);try{Files.move(temp,file,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}catch(AtomicMoveNotSupportedException e){Files.move(temp,file,StandardCopyOption.REPLACE_EXISTING);}}finally{Files.deleteIfExists(temp);}}
    private ImageFiles(){}
}
