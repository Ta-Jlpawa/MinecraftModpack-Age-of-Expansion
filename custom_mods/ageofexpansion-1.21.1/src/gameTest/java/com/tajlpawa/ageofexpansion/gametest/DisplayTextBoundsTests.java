package com.tajlpawa.ageofexpansion.gametest;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tajlpawa.ageofexpansion.display.DisplayDocument;
import com.tajlpawa.ageofexpansion.display.client.DisplayLayout;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;

// 捕获原版真实绘制顶点，避免仅比较字距而漏掉字形相对选区的偏移。
public final class DisplayTextBoundsTests {
    public static void run(Font font) {
        int cases=0;
        for(int flags:new int[]{0,1,3}) for(int size:new int[]{1,25,100,400,1000})
            for(int alignment=0;alignment<3;alignment++) for(int vertical=0;vertical<3;vertical++)
            for(int kind=0;kind<4;kind++) for(String text:new String[]{"中文","中文".repeat(30),"中文\n文字"}) {
            var ink=new DisplayDocument.Ink(0xffffff,flags);
            var doc=new DisplayDocument(text.codePoints().mapToObj(cp->new DisplayDocument.Glyph(cp,ink)).toList(),size,alignment,vertical);
            float width=kind==0?120:kind==1?108:kind==2?120:396,height=kind==0?60:kind==1?108:kind==2?120:252;
            var layout=DisplayLayout.create(font,doc,width,height,kind==2);
            for(int row=0;row<layout.lines().size();row++){
                var line=layout.lines().get(row);Bounds bounds=new Bounds();
                var pose=new Matrix4f().translate(layout.drawX(line),layout.drawY(row),0).scale(layout.scale());
                font.drawInBatch(layout.sequence(line),0,0,-1,false,pose,type->bounds,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);
                float selectionCenterX=(layout.x(line,line.start())+layout.x(line,line.end()))/2;
                float selectionCenterY=layout.y(row)+DisplayLayout.LINE_HEIGHT*layout.scale()/2;
                near((bounds.minX+bounds.maxX)/2,selectionCenterX,"Rendered text X differs from selection, size="+size+", flags="+flags);
                near((bounds.minY+bounds.maxY)/2,selectionCenterY,"Rendered text Y differs from selection, size="+size+", flags="+flags);
                if(alignment==1)near((bounds.minX+bounds.maxX)/2,width/2,"Horizontal visual centering");
                if(vertical==1&&layout.lines().size()==1)near((bounds.minY+bounds.maxY)/2,height/2,"Vertical visual centering");
            }
            cases++;
        }
        com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.info("DISPLAY_TEXT_BOUNDS_PASSED: {} real glyph/selection/centering cases",cases);
    }
    private static void near(float actual,float expected,String message){if(Math.abs(actual-expected)>.002f)throw new AssertionError(message+": "+actual+" != "+expected);}
    private static final class Bounds implements VertexConsumer {
        float minX=Float.POSITIVE_INFINITY,minY=Float.POSITIVE_INFINITY,maxX=Float.NEGATIVE_INFINITY,maxY=Float.NEGATIVE_INFINITY;
        public VertexConsumer addVertex(float x,float y,float z){minX=Math.min(minX,x);maxX=Math.max(maxX,x);minY=Math.min(minY,y);maxY=Math.max(maxY,y);return this;}
        public VertexConsumer setColor(int r,int g,int b,int a){return this;}
        public VertexConsumer setUv(float u,float v){return this;}
        public VertexConsumer setUv1(int u,int v){return this;}
        public VertexConsumer setUv2(int u,int v){return this;}
        public VertexConsumer setNormal(float x,float y,float z){return this;}
    }
}

