package com.tajlpawa.ageofexpansion;
import net.neoforged.neoforge.common.ModConfigSpec;
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue MAX_CHAR = BUILDER.comment("电子显示屏与方形电子显示屏最大字符数").defineInRange("setMaxChar",1024,1,2048);
    public static final ModConfigSpec.IntValue PER_BLOCK = BUILDER.comment("每个模块提供的字符容量").defineInRange("setPerBlockMaxChar",1024,1,2048);
    public static final ModConfigSpec.IntValue SIZE_X = BUILDER.comment("面向屏幕时的最大横向模块数").defineInRange("setScreenMaxSizeX",5,1,10);
    public static final ModConfigSpec.IntValue SIZE_Y = BUILDER.comment("最大竖向模块数").defineInRange("setScreenMaxSizeY",4,1,10);
    public static final ModConfigSpec.IntValue MIN_FONT_SIZE = BUILDER.comment("显示屏允许的最小字号（百分比），不得大于最大字号").defineInRange("setMinFontSize",25,1,1000);
    public static final ModConfigSpec.IntValue MAX_FONT_SIZE = BUILDER.comment("显示屏允许的最大字号（百分比）；小于最小字号时自动修正为最小字号").defineInRange("setMaxFontSize",400,1,1000);
    public static final ModConfigSpec.IntValue PLAYER_IMAGES=BUILDER.comment("普通玩家图像数量，0禁止新增").defineInRange("setMaxPlayerImages",10,0,1024);
    public static final ModConfigSpec.IntValue ADMIN_IMAGES=BUILDER.comment("管理员图像数量，0禁止新增").defineInRange("setMaxAdminImages",10,0,1024);
    public static final ModConfigSpec.IntValue IMAGE_KB=BUILDER.comment("上传原文件大小上限，KB；不限制转码后PNG缓存的大小").defineInRange("setMaxImageUploadKB",1024,16,32768);
    public static final ModConfigSpec.IntValue CANVAS_MIN_X=BUILDER.defineInRange("setMinCanvasSizeX",16,16,2048),CANVAS_MAX_X=BUILDER.defineInRange("setMaxCanvasSizeX",1024,16,2048);
    public static final ModConfigSpec.IntValue CANVAS_MIN_Y=BUILDER.defineInRange("setMinCanvasSizeY",16,16,2048),CANVAS_MAX_Y=BUILDER.defineInRange("setMaxCanvasSizeY",1024,16,2048);
    public static final ModConfigSpec.IntValue IMAGE_UNDO=BUILDER.defineInRange("setMaxImageUndoSteps",32,0,512);
    public static final ModConfigSpec.IntValue IMAGE_MIN_SCALE=BUILDER.defineInRange("setMinImageScale",25,1,1000),IMAGE_MAX_SCALE=BUILDER.defineInRange("setMaxImageScale",400,1,1000);
    public static final ModConfigSpec SPEC = BUILDER.build();
    public static int maxChar(){return SPEC.isLoaded()?MAX_CHAR.get():1024;}
    public static int perBlock(){return SPEC.isLoaded()?PER_BLOCK.get():1024;}
    public static int sizeX(){return SPEC.isLoaded()?SIZE_X.get():5;}
    public static int sizeY(){return SPEC.isLoaded()?SIZE_Y.get():4;}
    public static int minFontSize(){return SPEC.isLoaded()?MIN_FONT_SIZE.get():25;}
    public static int maxFontSize(){return Math.max(minFontSize(),SPEC.isLoaded()?MAX_FONT_SIZE.get():400);}
    public static boolean allowsFontSize(int size){return size>=minFontSize()&&size<=maxFontSize();}
    public static int clampFontSize(int size){return Math.clamp(size,minFontSize(),maxFontSize());}
    public static void validateFontRange(net.neoforged.fml.event.config.ModConfigEvent event){
        if(event.getConfig().getSpec()!=SPEC||!SPEC.isLoaded())return;
        normalizeFontRange();
        for(var pair:java.util.List.of(new ModConfigSpec.IntValue[]{CANVAS_MIN_X,CANVAS_MAX_X},new ModConfigSpec.IntValue[]{CANVAS_MIN_Y,CANVAS_MAX_Y},new ModConfigSpec.IntValue[]{IMAGE_MIN_SCALE,IMAGE_MAX_SCALE})){if(pair[0].get()>pair[1].get()){pair[1].set(pair[0].get());SPEC.save();}}
    }
    public static void normalizeFontRange(){
        if(!SPEC.isLoaded())return;
        if(MAX_FONT_SIZE.get()<MIN_FONT_SIZE.get()){
            AgeofExpansion.LOGGER.warn("setMaxFontSize ({}) is below setMinFontSize ({}); correcting maximum",MAX_FONT_SIZE.get(),MIN_FONT_SIZE.get());
            MAX_FONT_SIZE.set(MIN_FONT_SIZE.get());SPEC.save();
        }
    }
    private Config(){}
}


