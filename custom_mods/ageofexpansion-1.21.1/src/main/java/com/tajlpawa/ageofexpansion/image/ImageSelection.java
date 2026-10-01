package com.tajlpawa.ageofexpansion.image;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;

public record ImageSelection(UUID id,int scale,int horizontal,int vertical,boolean fit){
    public static final UUID NONE=new UUID(0,0);
    public static final ImageSelection EMPTY=new ImageSelection(NONE,100,1,1,false);
    public ImageSelection{if(id==null||scale<1||scale>1000||horizontal<0||horizontal>2||vertical<0||vertical>2)throw new IllegalArgumentException("selection");}
    public boolean present(){return !id.equals(NONE);}
    public CompoundTag save(){var n=new CompoundTag();n.putUUID("id",id);n.putInt("scale",scale);n.putInt("horizontal",horizontal);n.putInt("vertical",vertical);n.putBoolean("fit",fit);return n;}
    public static ImageSelection load(CompoundTag n){try{return n.hasUUID("id")?new ImageSelection(n.getUUID("id"),n.getInt("scale"),n.getInt("horizontal"),n.getInt("vertical"),n.getBoolean("fit")):EMPTY;}catch(IllegalArgumentException e){return EMPTY;}}
    // 统一计算可见矩形与UV；预览和世界使用相同的比例与裁剪。
    public record Rect(float x,float y,float width,float height,float u0,float v0,float u1,float v1){}
    public Rect layout(int imageW,int imageH,float screenW,float screenH){
        float factor=Math.min(screenW/imageW,screenH/imageH)*(fit?Math.min(scale,100):scale)/100f;
        float w=imageW*factor,h=imageH*factor,x=(screenW-w)*horizontal/2,y=(screenH-h)*vertical/2;
        float x0=Math.max(0,x),y0=Math.max(0,y),x1=Math.min(screenW,x+w),y1=Math.min(screenH,y+h);
        return new Rect(x0,y0,x1-x0,y1-y0,(x0-x)/w,(y0-y)/h,(x1-x)/w,(y1-y)/h);
    }
}
