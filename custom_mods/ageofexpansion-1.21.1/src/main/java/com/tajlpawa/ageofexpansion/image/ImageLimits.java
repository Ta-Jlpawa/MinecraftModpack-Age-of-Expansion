package com.tajlpawa.ageofexpansion.image;
import com.tajlpawa.ageofexpansion.Config;
import net.minecraft.nbt.CompoundTag;
public record ImageLimits(int count,int uploadKB,int minX,int maxX,int minY,int maxY,int undo,int minScale,int maxScale,boolean admin){
    public ImageLimits{if(count<0||count>1024||uploadKB<16||uploadKB>32768||minX<16||maxX>2048||minX>maxX||minY<16||maxY>2048||minY>maxY||undo<0||undo>512||minScale<1||maxScale>1000||minScale>maxScale)throw new IllegalArgumentException("limits");}
    public static ImageLimits current(boolean admin){return new ImageLimits(admin?Config.ADMIN_IMAGES.get():Config.PLAYER_IMAGES.get(),Config.IMAGE_KB.get(),Config.CANVAS_MIN_X.get(),Math.max(Config.CANVAS_MIN_X.get(),Config.CANVAS_MAX_X.get()),Config.CANVAS_MIN_Y.get(),Math.max(Config.CANVAS_MIN_Y.get(),Config.CANVAS_MAX_Y.get()),Config.IMAGE_UNDO.get(),Config.IMAGE_MIN_SCALE.get(),Math.max(Config.IMAGE_MIN_SCALE.get(),Config.IMAGE_MAX_SCALE.get()),admin);}
    public CompoundTag save(){var t=new CompoundTag();t.putIntArray("limits",new int[]{count,uploadKB,minX,maxX,minY,maxY,undo,minScale,maxScale});t.putBoolean("admin",admin);return t;}
    public static ImageLimits load(CompoundTag t){int[] a=t.getIntArray("limits");if(a.length!=9)throw new IllegalArgumentException("limits");return new ImageLimits(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],t.getBoolean("admin"));}
}
