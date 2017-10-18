/**
 * SVG3DHelper.java
 *
 * Copyright (c) 2013-2016, F(X)yz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of F(X)yz, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL F(X)yz BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package org.fxyz3d.shapes.primitives.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import org.fxyz3d.geometry.Point3D;

/**
 *
 * @author José Pereda 
 */
public class SVG3DHelper {
    
    private final static int POINTS_CURVE = 20;
    
    private final SVGPath svg;
    private List<Point3D> list;
    private Point3D p0;
    private final List<LineSegment> polis=new ArrayList<>();
    private final AtomicInteger index = new AtomicInteger();
   
    public SVG3DHelper(SVGPath svg){
        this.svg=svg;
        list=new ArrayList<>();
        
        // Convert svg to Path
        Path subtract = (Path)(Shape.subtract(svg, new Rectangle(0, 0)));
        // Convert Path elements into lists of points defining the perimeter (exterior or interior)
        subtract.getElements().forEach(this::getPoints);
        
        // Group exterior polygons with their interior polygons
        polis.stream().filter(LineSegment::isHole).forEach(hole->{
            polis.stream().filter(poly->!poly.isHole())
                    .filter(poly->!((Path)Shape.intersect(poly.getPath(), hole.getPath())).getElements().isEmpty())
                    .filter(poly->poly.getPath().contains(new Point2D(hole.getOrigen().x,hole.getOrigen().y)))
                    .forEach(poly->poly.addHole(hole));
        });        
        polis.removeIf(LineSegment::isHole);                
    }
    
    public List<LineSegment> getLineSegment() {        
        return polis; 
    }
    
    public List<Point3D> getOffset(){
        return polis.stream().sorted((p1,p2)->(int)(p1.getOrigen().x-p2.getOrigen().x))
                .map(LineSegment::getOrigen).collect(Collectors.toList());
    }
    
    private void getPoints(PathElement elem){
        if(elem instanceof MoveTo){
            list=new ArrayList<>();
            p0=new Point3D((float)((MoveTo)elem).getX(),(float)((MoveTo)elem).getY(),0f);
            list.add(p0);
        } else if(elem instanceof LineTo){
            list.add(new Point3D((float)((LineTo)elem).getX(),(float)((LineTo)elem).getY(),0f));
        } else if(elem instanceof CubicCurveTo){
            Point3D ini = (list.size()>0?list.get(list.size()-1):p0);
            IntStream.rangeClosed(1, POINTS_CURVE).forEach(i->list.add(evalCubicBezier((CubicCurveTo)elem, ini, ((double)i)/POINTS_CURVE)));
        } else if(elem instanceof QuadCurveTo){
            Point3D ini = (list.size()>0?list.get(list.size()-1):p0);
            IntStream.rangeClosed(1, POINTS_CURVE).forEach(i->list.add(evalQuadBezier((QuadCurveTo)elem, ini, ((double)i)/POINTS_CURVE)));
        } else if(elem instanceof ClosePath){
            list.add(p0);
            // Every closed path is a polygon (exterior or interior==hole)
            // the text, the list of points and a new path between them are
            // stored in a LineSegment: a continuous line that can change direction
            if(Math.abs(getArea())>0.001){
                LineSegment line = new LineSegment(Integer.toString(index.getAndIncrement()));
                line.setHole(isHole());
                line.setPoints(list);
                line.setPath(generatePath());
                line.setOrigen(p0);
                polis.add(line);
            }
        } 
    }
    
    private Point3D evalCubicBezier(CubicCurveTo c, Point3D ini, double t){
        Point3D p=new Point3D((float)(Math.pow(1-t,3)*ini.x+
                3*t*Math.pow(1-t,2)*c.getControlX1()+
                3*(1-t)*t*t*c.getControlX2()+
                Math.pow(t, 3)*c.getX()),
                (float)(Math.pow(1-t,3)*ini.y+
                3*t*Math.pow(1-t, 2)*c.getControlY1()+
                3*(1-t)*t*t*c.getControlY2()+
                Math.pow(t, 3)*c.getY()),
                0f);
        return p;
    }
    
    private Point3D evalQuadBezier(QuadCurveTo c, Point3D ini, double t){
        Point3D p=new Point3D((float)(Math.pow(1-t,2)*ini.x+
                2*(1-t)*t*c.getControlX()+
                Math.pow(t, 2)*c.getX()),
                (float)(Math.pow(1-t,2)*ini.y+
                2*(1-t)*t*c.getControlY()+
                Math.pow(t, 2)*c.getY()),
                0f);
        return p;
    }
    
    private double getArea(){
        DoubleProperty res=new SimpleDoubleProperty();
        IntStream.range(0, list.size()-1)
                .forEach(i->res.set(res.get()+list.get(i).crossProduct(list.get(i+1)).z));
        // System.out.println("path: "+res.doubleValue()/2);
        
        return res.doubleValue()/2d;
    }
    
    private boolean isHole(){
        // area>0 -> the path is a hole, clockwise (y up)
        // area<0 -> the path is a polygon, counterclockwise (y up)
        return getArea()>0;
    }
    
    private Path generatePath(){
        Path path = new Path(new MoveTo(list.get(0).x,list.get(0).y));
        list.stream().skip(1).forEach(p->path.getElements().add(new LineTo(p.x,p.y)));
        path.getElements().add(new ClosePath());
        path.setStroke(Color.GREEN);
        // Path must be filled to allow Shape.intersect
        path.setFill(Color.RED);
        return path;
    }

}
