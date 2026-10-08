package com.tajlpawa.ageofexpansion.image.client;

import java.util.*;

// All regions use pixel centres and even-odd scan conversion in canvas coordinates.
public final class PaintRegion {
    public record Point(double x,double y){}
    public static BitSet rectangle(int w,int h,Point a,Point b){
        var result=new BitSet(w*h);int x0=Math.max(0,(int)Math.floor(Math.min(a.x,b.x))),x1=Math.min(w-1,(int)Math.floor(Math.max(a.x,b.x)));
        int y0=Math.max(0,(int)Math.floor(Math.min(a.y,b.y))),y1=Math.min(h-1,(int)Math.floor(Math.max(a.y,b.y)));
        if(x0<=x1)for(int y=y0;y<=y1;y++)result.set(y*w+x0,y*w+x1+1);return result;
    }
    private record Edge(double low,double high,double x,double slope){}
    public static BitSet polygon(int w,int h,List<Point> points){
        var result=new BitSet(w*h);if(points.size()<3)return result;var edges=new ArrayList<Edge>();
        for(int i=0;i<points.size();i++){var a=points.get(i);var b=points.get((i+1)%points.size());if(a.y==b.y)continue;if(a.y>b.y){var temp=a;a=b;b=temp;}edges.add(new Edge(a.y,b.y,a.x,(b.x-a.x)/(b.y-a.y)));}
        edges.sort(Comparator.comparingDouble(Edge::low));var active=new ArrayList<Edge>();int next=0;double[] crossings=new double[edges.size()];
        for(int y=0;y<h;y++){double scan=y+.5;while(next<edges.size()&&edges.get(next).low<=scan)active.add(edges.get(next++));active.removeIf(e->e.high<=scan);int n=0;for(var e:active)crossings[n++]=e.x+(scan-e.low)*e.slope;Arrays.sort(crossings,0,n);
            for(int i=0;i+1<n;i+=2){int x0=Math.max(0,(int)Math.ceil(crossings[i]-.5)),x1=Math.min(w,(int)Math.ceil(crossings[i+1]-.5));if(x0<x1)result.set(y*w+x0,y*w+x1);}}
        return result;
    }
    public static int[] bounds(BitSet mask,int w){if(mask.isEmpty())return new int[]{0,0,0,0};int x0=w,x1=0,y0=mask.nextSetBit(0)/w,y1=(mask.length()-1)/w;for(int i=mask.nextSetBit(0);i>=0;i=mask.nextSetBit(i+1)){x0=Math.min(x0,i%w);x1=Math.max(x1,i%w);}return new int[]{x0,y0,x1-x0+1,y1-y0+1};}
    private PaintRegion(){}
}
