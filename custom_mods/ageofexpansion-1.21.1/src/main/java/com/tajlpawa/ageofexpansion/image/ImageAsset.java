package com.tajlpawa.ageofexpansion.image;

import net.minecraft.nbt.CompoundTag;

// 逻辑图像ID保持独立；不可变内容描述用于磁盘、传输和纹理去重。
public record ImageAsset(Variant full,Variant thumbnail,boolean visible) {
    public record Variant(String pixels,String digest,String codec,int width,int height,int length) {
        public Variant {
            if(!pixels.matches("[0-9a-f]{64}")||!digest.matches("[0-9a-f]{64}")||!(codec.equals("png")||codec.equals("jpeg"))||width<1||height<1||width>2048||height>2048||length<8||length>ImageFiles.MAX_BYTES)throw new IllegalArgumentException("image_descriptor");
        }
        public CompoundTag save(){var n=new CompoundTag();n.putString("pixels",pixels);n.putString("digest",digest);n.putString("codec",codec);n.putInt("width",width);n.putInt("height",height);n.putInt("length",length);return n;}
        public static Variant load(CompoundTag n){return new Variant(n.getString("pixels"),n.getString("digest"),n.getString("codec"),n.getInt("width"),n.getInt("height"),n.getInt("length"));}
    }
    public Variant variant(boolean thumb){return thumb?thumbnail:full;}
    public CompoundTag save(){var n=new CompoundTag();n.putInt("version",2);n.put("full",full.save());n.put("thumbnail",thumbnail.save());n.putBoolean("visible",visible);return n;}
    public static ImageAsset load(CompoundTag n){return new ImageAsset(Variant.load(n.getCompound("full")),Variant.load(n.getCompound("thumbnail")),n.getBoolean("visible"));}
}
