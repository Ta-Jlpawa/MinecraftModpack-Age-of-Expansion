package com.tajlpawa.ageofexpansion.display;
import java.io.*;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

// 每个码点和样式打包为long，元数据变更共享数组，保留旧版runs读取。
public record DisplayDocument(List<Glyph> glyphs,int size,int alignment,int vertical){
    public static final int LIMIT=204800;
    public static final DisplayDocument EMPTY=new DisplayDocument(List.of(),100,1,1);
    public record Ink(int color,int flags){
        public static final Ink WHITE=new Ink(0xffffff,0);
        public Ink{if(color<0||color>0xffffff||flags<0||flags>7)throw new IllegalArgumentException("Invalid style");}
        public Style style(){return Style.EMPTY.withColor(color).withBold((flags&1)!=0).withItalic((flags&2)!=0).withObfuscated((flags&4)!=0);}
    }
    public record Glyph(int codepoint,Ink ink){
        public Glyph{if(!validCodepoint(codepoint))throw new IllegalArgumentException("Invalid character");}
        public Component component(){return Component.literal(new String(Character.toChars(codepoint))).setStyle(ink.style());}
    }
    private static final class Packed extends AbstractList<Glyph> implements RandomAccess{
        final long[] values;
        final boolean hasText;
        Packed(long[] values){this.values=values;boolean text=false;for(long value:values){int cp=(int)(value&0x1fffff);if(!Character.isWhitespace(cp)&&!Character.isSpaceChar(cp)){text=true;break;}}hasText=text;}
        @Override public int size(){return values.length;}
        @Override public Glyph get(int index){return unpack(values[index]);}
    }
    public DisplayDocument(List<Glyph> glyphs,int size,int alignment){this(glyphs,size,alignment,1);}
    public DisplayDocument{
        if(glyphs.size()>LIMIT||size<1||size>1000||alignment<0||alignment>2||vertical<0||vertical>2)throw new IllegalArgumentException("Invalid document");
        if(!(glyphs instanceof Packed)){
            long[] values=new long[glyphs.size()];for(int i=0;i<values.length;i++)values[i]=pack(glyphs.get(i));glyphs=new Packed(values);
        }
    }
    public static boolean validCodepoint(int cp){return cp=='\n'||Character.isValidCodePoint(cp)&&!Character.isISOControl(cp)&&cp!=0xa7&&!(cp>=0xd800&&cp<=0xdfff);}
    private static long pack(Glyph g){return g.codepoint|((long)(g.ink.color|g.ink.flags<<24)<<21);}
    private static Glyph unpack(long v){return new Glyph((int)(v&0x1fffff),new Ink((int)(v>>>21)&0xffffff,(int)(v>>>45)));}
    public long packed(int index){return ((Packed)glyphs).values[index];}
    public boolean hasText(){return ((Packed)glyphs).hasText;}
    public String text(int start,int end){StringBuilder b=new StringBuilder();for(int i=start;i<end;i++)b.appendCodePoint((int)(packed(i)&0x1fffff));return b.toString();}
    public DisplayDocument metadata(int size,int alignment,int vertical){return new DisplayDocument(glyphs,size,alignment,vertical);}
    public DisplayDocument replace(int start,int end,List<Glyph> inserted){
        int count=glyphs.size()-(end-start)+inserted.size();if(count>LIMIT)throw new IllegalArgumentException("Too long");
        long[] values=new long[count],old=((Packed)glyphs).values;
        System.arraycopy(old,0,values,0,start);
        for(int i=0;i<inserted.size();i++)values[start+i]=pack(inserted.get(i));
        System.arraycopy(old,end,values,start+inserted.size(),old.length-end);
        return new DisplayDocument(new Packed(values),size,alignment,vertical);
    }
    public List<Glyph> slice(int start,int end){return new Packed(Arrays.copyOfRange(((Packed)glyphs).values,start,end));}
    @Override public boolean equals(Object obj){return obj instanceof DisplayDocument d&&size==d.size&&alignment==d.alignment&&vertical==d.vertical&&Arrays.equals(((Packed)glyphs).values,((Packed)d.glyphs).values);}
    @Override public int hashCode(){return Objects.hash(size,alignment,vertical,Arrays.hashCode(((Packed)glyphs).values));}
    public void write(RegistryFriendlyByteBuf b){b.writeVarInt(size);b.writeByte(alignment);b.writeByte(vertical);b.writeVarInt(glyphs.size());for(long v:((Packed)glyphs).values)b.writeLong(v);}
    public static DisplayDocument read(RegistryFriendlyByteBuf b){
        int size=b.readVarInt(),align=b.readUnsignedByte(),vertical=b.readUnsignedByte(),n=b.readVarInt();
        if(n<0||n>LIMIT)throw new IllegalArgumentException("Too long");
        long[] values=new long[n];for(int i=0;i<n;i++){values[i]=b.readLong();unpack(values[i]);}
        return new DisplayDocument(new Packed(values),size,align,vertical);
    }
    public byte[] bytes(){
        try{var bytes=new ByteArrayOutputStream();var out=new DataOutputStream(bytes);
            out.writeInt(size);out.writeByte(alignment);out.writeByte(vertical);out.writeInt(glyphs.size());
            for(long v:((Packed)glyphs).values)out.writeLong(v);return bytes.toByteArray();
        }catch(IOException e){throw new UncheckedIOException(e);}
    }
    public static DisplayDocument bytes(byte[] bytes){
        try{var in=new DataInputStream(new ByteArrayInputStream(bytes));int size=in.readInt(),align=in.readUnsignedByte(),vertical=in.readUnsignedByte(),n=in.readInt();
            if(n<0||n>LIMIT||bytes.length!=10+n*8)throw new IllegalArgumentException("Invalid document length");
            long[] values=new long[n];for(int i=0;i<n;i++){values[i]=in.readLong();unpack(values[i]);}
            return new DisplayDocument(new Packed(values),size,align,vertical);
        }catch(IOException e){throw new IllegalArgumentException(e);}
    }
    public CompoundTag save(){CompoundTag tag=new CompoundTag();tag.putByteArray("packed",bytes());return tag;}
    public static DisplayDocument load(CompoundTag tag){
        try{
            if(tag.contains("packed",Tag.TAG_BYTE_ARRAY))return bytes(tag.getByteArray("packed"));
            List<Glyph> glyphs=new ArrayList<>();ListTag runs=tag.getList("runs",Tag.TAG_COMPOUND);
            for(int i=0;i<runs.size();i++){var run=runs.getCompound(i);Ink ink=new Ink(run.getInt("color"),run.getInt("flags"));
                if(run.getString("text").length()>LIMIT*2)return EMPTY;
                for(int cp:run.getString("text").codePoints().toArray()){glyphs.add(new Glyph(cp,ink));if(glyphs.size()>LIMIT)return EMPTY;}
            }
            return new DisplayDocument(glyphs,tag.getInt("size"),tag.getInt("alignment"),tag.contains("vertical")?tag.getInt("vertical"):1);
        }catch(IllegalArgumentException e){return EMPTY;}
    }
}


