package com.tajlpawa.ageofexpansion.display.client;
import com.tajlpawa.ageofexpansion.display.DisplayDocument;
import java.util.*;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;

// 先测量字宽再排版，缩小搜索只扫描数值数组，不重复创建文本对象。
public record DisplayLayout(List<Line> lines,float scale,float top,DisplayDocument document,float width,float height){
    public static final float WIDTH=120,HEIGHT=60,LINE_HEIGHT=10;
    public record Line(int start,int end,float width,float[] offsets){}
    public static final class Job{
        private final DisplayDocument doc;private final float width,height;private final boolean scrolling;
        private final float[] advances;private int measured;private final Map<Long,Float> widths=new HashMap<>();
        public Job(DisplayDocument doc,float width,float height,boolean scrolling){this.doc=doc;this.width=width;this.height=height;this.scrolling=scrolling;advances=new float[doc.glyphs().size()];}
        public DisplayLayout step(Font font,int budget){
            int end=Math.min(advances.length,measured+budget);
            for(;measured<end;measured++){
                var glyph=doc.glyphs().get(measured);
                if(glyph.codepoint()=='\n'){advances[measured]=-1;continue;}
                long key=glyph.codepoint()|((long)(glyph.ink().flags()&1)<<21);
                // 保留原版字距的小数部分，避免中文加粗逐字向上取整造成累计偏移。
                advances[measured]=widths.computeIfAbsent(key,k->font.getSplitter().stringWidth(glyph.component()));
            }
            return measured==advances.length?finish(doc,width,height,scrolling,advances):null;
        }
    }
    public static DisplayLayout create(Font font,DisplayDocument doc){return create(font,doc,WIDTH,HEIGHT,false);}
    public static DisplayLayout create(Font font,DisplayDocument doc,float width,float height,boolean scrolling){return new Job(doc,width,height,scrolling).step(font,Integer.MAX_VALUE);}
    private static DisplayLayout finish(DisplayDocument doc,float width,float height,boolean scrolling,float[] advance){
        float scale=scrolling?1:doc.size()/100f;
        if(!scrolling&&!fits(advance,width,height,scale)){
            float low=.000001f,high=scale;
            for(int i=0;i<22;i++){float mid=(low+high)/2;if(fits(advance,width,height,mid))low=mid;else high=mid;}scale=low;
        }
        List<Line> lines=new ArrayList<>();int start=0;float used=0;float max=width/scale;
        for(int i=0;i<advance.length;i++){
            if(advance[i]<0){lines.add(line(start,i,advance));start=i+1;used=0;}
            else{
                if(used+advance[i]>max&&i>start){lines.add(line(start,i,advance));start=i;used=0;}
                used+=advance[i];
            }
        }
        lines.add(line(start,advance.length,advance));
        float top=Math.max(0,height-lines.size()*LINE_HEIGHT*scale)*doc.vertical()/2;
        return new DisplayLayout(List.copyOf(lines),scale,top,doc,width,height);
    }
    private static boolean fits(float[] advances,float width,float height,float scale){
        float max=width/scale,used=0;int rows=1;
        for(float advance:advances){
            if(advance<0){rows++;used=0;}
            else{if(advance>max)return false;if(used+advance>max&&used>0){rows++;used=0;}used+=advance;}
        }
        return rows*LINE_HEIGHT*scale<=height;
    }
    private static Line line(int start,int end,float[] advances){
        float[] offsets=new float[end-start+1];for(int i=start;i<end;i++)offsets[i-start+1]=offsets[i-start]+Math.max(0,advances[i]);
        return new Line(start,end,offsets[offsets.length-1],offsets);
    }
    public float left(Line line){return (width-line.width*scale)*document.alignment()/2;}
    public float x(Line line,int index){return left(line)+line.offsets[Math.clamp(index-line.start,0,line.offsets.length-1)]*scale;}
    public float y(int row){return top+row*LINE_HEIGHT*scale;}
    // 原版字距含1像素尾间隔，字形高8像素；在10像素编辑行框中均分留白。
    // 修正放在字体缩放之前，预览与世界渲染共用，光标仍位于字符单元边界。
    public float drawX(Line line){return left(line)+.5f*scale;}
    public float drawY(int row){return y(row)+(LINE_HEIGHT-8)*.5f*scale;}
    public float contentHeight(){return top+lines.size()*LINE_HEIGHT*scale;}
    public int rowFor(int index){
        int low=0,high=lines.size()-1;while(low<high){int mid=(low+high+1)/2;if(lines.get(mid).start<=index)low=mid;else high=mid-1;}return low;
    }
    public int hit(float x,float y){
        int row=Math.clamp((int)Math.floor((y-top)/(LINE_HEIGHT*scale)),0,lines.size()-1);Line line=lines.get(row);
        int low=line.start,high=line.end;
        while(low<high){int mid=(low+high)/2;if(x<(x(line,mid)+x(line,mid+1))/2)high=mid;else low=mid+1;}return low;
    }
    public FormattedCharSequence sequence(Line line){
        return sink->{for(int i=line.start;i<line.end;i++){var g=document.glyphs().get(i);if(!sink.accept(i-line.start,g.ink().style(),g.codepoint()))return false;}return true;};
    }
}

