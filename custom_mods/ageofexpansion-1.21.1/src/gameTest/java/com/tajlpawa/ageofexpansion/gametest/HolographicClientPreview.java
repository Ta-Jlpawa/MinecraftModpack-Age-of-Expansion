package com.tajlpawa.ageofexpansion.gametest;

import com.tajlpawa.ageofexpansion.display.*;
import com.tajlpawa.ageofexpansion.display.client.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;

public final class HolographicClientPreview extends Screen {
    private final List<List<DisplayBlockEntity>> screens=new ArrayList<>();
    private static DisplayBlock block(int kind){return (kind==0?DisplayContent.HOLOGRAPHIC:kind==1?DisplayContent.HOLOGRAPHIC_SQUARE:DisplayContent.HOLOGRAPHIC_MODULE).get();}
    public HolographicClientPreview(){
        super(Component.literal("Holographic displays / empty and text-only"));
        for(int row=0;row<2;row++)for(int kind=0;kind<3;kind++){
            int size=kind==2?2:1;UUID id=UUID.randomUUID();List<DisplayBlockEntity> members=new ArrayList<>();
            var doc=new DisplayDocument("全息文字\nHOLOGRAM".codePoints().mapToObj(cp->new DisplayDocument.Glyph(cp,new DisplayDocument.Ink(0x9fedff,1))).toList(),kind==2?180:100,1,1);
            for(int y=0;y<size;y++)for(int x=0;x<size;x++){
                int edges=(x>0?2:0)|(x<size-1?1:0)|(y>0?4:0)|(y<size-1?8:0);
                var be=new DisplayBlockEntity(new BlockPos(-x,-y,0),block(kind).defaultBlockState().setValue(DisplayBlock.LIT,row==1).setValue(DisplayBlock.CONNECTIONS,edges));
                if(kind==2){be.screenId=id;be.screenWidth=size;be.screenHeight=size;be.origin=BlockPos.ZERO;}else be.update(row==1?doc:DisplayDocument.EMPTY);
                members.add(be);
            }
            if(kind==2)DisplayNetwork.CLIENT_VIEWS.put(id,new DisplayNetwork.View(0,doc));screens.add(members);
        }
    }
    public static void verify() throws Exception {
        var mc=Minecraft.getInstance();var random=RandomSource.create(0);
        for(int kind=0;kind<3;kind++){
            for(var facing:Direction.Plane.HORIZONTAL)for(int edges=0;edges<(kind==2?16:1);edges++)for(boolean lit:new boolean[]{false,true}){
                var state=block(kind).defaultBlockState().setValue(DisplayBlock.FACING,facing).setValue(DisplayBlock.CONNECTIONS,edges).setValue(DisplayBlock.LIT,lit);
                var model=mc.getBlockRenderer().getBlockModel(state);int count=model.getQuads(state,null,random).size();for(var side:Direction.values())count+=model.getQuads(state,side,random).size();
                check(lit?count==0:count==6-Integer.bitCount(edges),"Empty/visible model and joined-side geometry: "+kind+"/"+edges+"/"+lit);
                if(!lit)check(model.getRenderTypes(state,random,ModelData.EMPTY).contains(RenderType.translucent()),"Fractional-alpha model uses translucent chunk layer");
            }
            var stack=block(kind).asItem().getDefaultInstance();var itemModel=mc.getItemRenderer().getModel(stack,null,null,0);
            check(!itemModel.getQuads(null,null,random).isEmpty(),"Holographic icon remains visible");
            var editor=new DisplayScreen(UUID.randomUUID(),DisplayDocument.EMPTY,new DisplayNetwork.Meta(kind,1,1,1024,BlockPos.ZERO,UUID.randomUUID(),0,25,400,true));
            check(editor.getTitle().getString().equals(block(kind).getName().getString()),"Correct holographic editor title");
            String id=net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block(kind)).getPath();
            try(var input=mc.getResourceManager().open(ResourceLocation.parse("ageofexpansion:textures/block/"+id+".png"));var image=com.mojang.blaze3d.platform.NativeImage.read(input)){
                int alpha=net.minecraft.util.FastColor.ABGR32.alpha(image.getPixelRGBA(16,16));check(alpha>=75&&alpha<=78,"Panel alpha is thirty percent");
                if(kind==2)for(int mask=0;mask<16;mask++){
                    int tx=(mask%4)*32,ty=(mask/4)*32;int[][] edges={{31,16,1},{0,16,2},{16,0,4},{16,31,8}};
                    for(int[] edge:edges){int a=net.minecraft.util.FastColor.ABGR32.alpha(image.getPixelRGBA(tx+edge[0],ty+edge[1]));check((mask&edge[2])!=0?a==alpha:a>alpha,"Connected atlas removes only interior borders");}
                }
            }
        }
        com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.info("HOLOGRAPHIC_CLIENT_PASSED: hidden quads, translucent layers, 16 connected variants, alpha, icons and titles");
    }
    private static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta){
        g.fill(0,0,width,height,0xff182229);g.drawCenteredString(font,title,width/2,7,0xffffff);DisplayRenderer.beginFrame();
        for(int row=0;row<2;row++)for(int kind=0;kind<3;kind++){
            int cx=(int)(width*(kind+.5)/3),cy=(int)(height*(row==0?.32:.72));float full=Math.min(width/3f-20,height*.31f),scale=kind==2?full/2:full;
            for(int y=-40;y<40;y+=8)for(int x=-48;x<48;x+=8)g.fill(cx+x,cy+y,cx+x+8,cy+y+8,((x+y)/8%2==0)?0xff384a52:0xff53636b);
            var pose=g.pose();pose.pushPose();pose.translate(cx,cy,100);pose.scale(scale,-scale,scale);pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));pose.translate(kind==2?0:-.5,kind==2?0:-.5,-.5);
            for(var be:screens.get(row*3+kind)){
                pose.pushPose();pose.translate(be.getBlockPos().getX(),be.getBlockPos().getY(),0);
                minecraft.getBlockRenderer().renderSingleBlock(be.getBlockState(),pose,g.bufferSource(),LightTexture.FULL_BRIGHT,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
                minecraft.getBlockEntityRenderDispatcher().getRenderer(be).render(be,delta,pose,g.bufferSource(),0,0);pose.popPose();
            }
            g.flush();pose.popPose();g.drawCenteredString(font,Component.literal((row==0?"EMPTY / ":"TEXT / ")+(kind==0?"wide":kind==1?"square":"2 x 2")),cx,cy+43,0xd0e9ed);
        }
    }
    @Override public void renderBackground(GuiGraphics graphics,int x,int y,float delta){}
}
