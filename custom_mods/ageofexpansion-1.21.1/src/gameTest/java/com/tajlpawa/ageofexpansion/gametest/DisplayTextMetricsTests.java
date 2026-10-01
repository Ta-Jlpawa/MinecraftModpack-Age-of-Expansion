package com.tajlpawa.ageofexpansion.gametest;

import com.tajlpawa.ageofexpansion.display.DisplayDocument;
import com.tajlpawa.ageofexpansion.display.client.DisplayLayout;
import net.minecraft.client.gui.Font;

// 使用原版浮点字距作为独立基准，验证换行、对齐和鼠标命中共享真实字宽。
public final class DisplayTextMetricsTests {
    public static void run(Font font) {
        int cases=0;
        for(String text:new String[]{"这是用于验证中文加粗自动换行与光标位置的显示屏测试文本。".repeat(8),"自动化ABC123，中文与English混排😀\n下一段文字继续自动换行。".repeat(5)})
            for(int flags:new int[]{0,1,3,7}) for(int alignment=0;alignment<3;alignment++)
                for(int size:new int[]{25,100,400}) for(int kind=0;kind<4;kind++) {
                    var ink=new DisplayDocument.Ink(0x75d7ac,flags);
                    var doc=new DisplayDocument(text.codePoints().mapToObj(cp->new DisplayDocument.Glyph(cp,ink)).toList(),size,alignment,1);
                    float width=kind==0?120:kind==1?108:kind==2?120:396;
                    float height=kind==0?60:kind==1?108:kind==2?120:252;
                    var layout=DisplayLayout.create(font,doc,width,height,kind==2);
                    for(int row=0;row<layout.lines().size();row++){
                        var line=layout.lines().get(row);
                        float rendered=font.getSplitter().stringWidth(layout.sequence(line));
                        near(line.width(),rendered,"Line width differs from vanilla draw advance; flags="+flags+", kind="+kind);
                        float expectedLeft=(width-rendered*layout.scale())*alignment/2;
                        near(layout.left(line),expectedLeft,"Alignment differs from rendered line");
                        float prefix=0;
                        for(int i=line.start();i<line.end();i++){
                            float advance=font.getSplitter().stringWidth(doc.glyphs().get(i).component());
                            near(layout.x(line,i),expectedLeft+prefix*layout.scale(),"Caret differs from glyph start");
                            if(advance>0){
                                float y=layout.y(row)+5*layout.scale();
                                int hit=layout.hit(expectedLeft+(prefix+advance*.25f)*layout.scale(),y);
                                if(hit!=i)throw new AssertionError("Mouse hit differs from actual glyph position: "+hit+" != "+i);
                            }
                            prefix+=advance;
                        }
                        near(layout.x(line,line.end()),expectedLeft+rendered*layout.scale(),"End caret differs from rendered end");
                    }
                    cases++;
                }
        com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.info("DISPLAY_TEXT_METRICS_PASSED: {} CJK/mixed, bold/italic/obfuscated, alignment, size and viewport cases",cases);
    }
    private static void near(float actual,float expected,String message){
        if(Math.abs(actual-expected)>.002f)throw new AssertionError(message+": "+actual+" != "+expected);
    }
    private DisplayTextMetricsTests(){}
}
