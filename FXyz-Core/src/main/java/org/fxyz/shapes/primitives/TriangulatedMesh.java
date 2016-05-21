/*
 * Copyright (C) 2013-2015 F(X)yz, 
 * Sean Phillips, Jason Pollastrini and Jose Pereda
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fxyz.shapes.primitives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Face3;
import org.fxyz.geometry.Point3D;
import org.fxyz.collections.FloatCollector;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.geometry.polygon.PolygonSet;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

/**
 *
 * @author José Pereda 
 */
public class TriangulatedMesh extends TexturedMesh {

    private List<Point3D> pointsExterior;
    private List<List<Point3D>> pointsHoles;
    private final static int DEFAULT_LEVEL = 1;
    private final static double DEFAULT_HEIGHT = 1d;
    private final static double DEFAULT_HOLE_RADIUS = 0d;

    public TriangulatedMesh(List<Point3D> points) {
        this(points,DEFAULT_LEVEL,DEFAULT_HEIGHT,DEFAULT_HOLE_RADIUS);
    }
    public TriangulatedMesh(List<Point3D> points, List<List<Point3D>> pointsHole) {
        this(points,pointsHole,DEFAULT_LEVEL,DEFAULT_HEIGHT,DEFAULT_HOLE_RADIUS);
    }
    
    public TriangulatedMesh(List<Point3D> points, double height) {
        this(points,DEFAULT_LEVEL,height,DEFAULT_HOLE_RADIUS);
    }

    public TriangulatedMesh(List<Point3D> points, double height, double holeRadius) {
        this(points,DEFAULT_LEVEL,height,holeRadius);
    }

    public TriangulatedMesh(List<Point3D> points, int level, double height, double holeRadius) {
        this(points,null,level,height,holeRadius);
    }
    public TriangulatedMesh(List<Point3D> points, List<List<Point3D>> pointsHole, int level, double height, double holeRadius) {
        this(points,pointsHole,level,height,holeRadius,null);
    }
    public TriangulatedMesh(List<Point3D> points, List<List<Point3D>> pointsHole, int level, double height, double holeRadius, Bounds bounds) {
        this.pointsExterior=points;
        this.pointsHoles=pointsHole;
        setLevel(level);
        setHeight(height);
        setHoleRadius(holeRadius);
        setBounds(bounds);
        
        updateMesh();
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
    }
    
    @Override
    protected final void updateMesh(){   
        setMesh(null);
        mesh=createMesh(level.get());
        setMesh(mesh);
    }
    
    private final DoubleProperty height = new SimpleDoubleProperty(DEFAULT_HEIGHT){
        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };
    
    public double getHeight() {
        return height.get();
    }

    public final void setHeight(double value) {
        height.set(value);
    }

    public DoubleProperty heightProperty() {
        return height;
    }
    
    private final IntegerProperty level = new SimpleIntegerProperty(DEFAULT_LEVEL){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public final int getLevel() {
        return level.get();
    }

    public final void setLevel(int value) {
        level.set(value);
    }

    public final IntegerProperty levelProperty() {
        return level;
    }
    private final DoubleProperty holeRadius = new SimpleDoubleProperty(DEFAULT_HOLE_RADIUS){
        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public double getHoleRadius() {
        return holeRadius.get();
    }

    public final void setHoleRadius(double value) {
        holeRadius.set(value);
    }

    public DoubleProperty holeRadiusProperty() {
        return holeRadius;
    }
    private final ObjectProperty<Bounds> bounds = new SimpleObjectProperty<Bounds>(){
        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public Bounds getBounds() {
        return bounds.get();
    }

    public final void setBounds(Bounds value) {
        bounds.set(value);
    }

    public ObjectProperty<Bounds> boundsProperty() {
        return bounds;
    }
    
    private int numVertices, numTexCoords, numFaces;
    private float[] points0, texCoord0;
    private int[] faces0;
    private List<Point2D> texCoord1;
    
    private List<TriangulationPoint> points1;
    private final List<List<PolygonPoint>> holes=new ArrayList<>();
    private List<TriangulationPoint> steiner;
    private int extPoints;
    private int steinerPoints=0*8;
    private int numHoles=0;
    private final List<Integer> holePoints=new ArrayList<>();
    private final double EPSILON = 0.001;
    private double maxX=0d, maxY=0d, minX=0d, minY=0d;
    
    private TriangleMesh createMesh(int level){
        TriangleMesh m0=null;
        if(level>0){
            m0 = createMesh(level-1);
        }
        
        if(level==0){
            //check for duplicates or too close
            List<Integer> duplicates=IntStream.range(0, pointsExterior.size()).boxed()
                .filter(i->pointsExterior.get(i).substract(pointsExterior.get(i==pointsExterior.size()-1?0:i+1)).magnitude()<100*EPSILON)
                .map(i->i).collect(Collectors.toList());
            duplicates.stream().sorted(Collections.reverseOrder()).forEach(i->pointsExterior.remove(i.intValue()));
            
            List<PolygonPoint> list = pointsExterior.stream().map(p->new PolygonPoint(p.x, p.y)).collect(Collectors.toList());
            Polygon poly=new Polygon(list);
            
            if(bounds.get()!=null){
                maxX=bounds.get().getMaxX();
                minX=bounds.get().getMinX();
                maxY=bounds.get().getMaxY();
                minY=bounds.get().getMinY();
            } else {
                maxX = pointsExterior.stream().mapToDouble(p->p.x).max().getAsDouble();
                maxY = pointsExterior.stream().mapToDouble(p->p.y).max().getAsDouble();
                minX = pointsExterior.stream().mapToDouble(p->p.x).min().getAsDouble();
                minY = pointsExterior.stream().mapToDouble(p->p.y).min().getAsDouble();
            }
            double rad = getHoleRadius();
            
            if(pointsHoles!=null){
                steinerPoints=0;
                numHoles=pointsHoles.size();
                
                // holes
                pointsHoles.forEach(pHole->{
                    // hole                
                    List<PolygonPoint> hole = pHole.stream().distinct()
                            .map(p->new PolygonPoint(p.x,p.y))
                            .collect(Collectors.toList());
                    holePoints.add(hole.size());
                    Polygon polyIn = new Polygon(hole);
                    poly.addHole(polyIn);
                    holes.add(hole);
                });
            } else if(rad>0d){
                steinerPoints=0;
                numHoles=1;
                int num=200;
                holePoints.add(num);
                
                // circular hole                
                List<PolygonPoint> hole = IntStream.range(0,num)
                        .mapToObj(i->new PolygonPoint((maxX+minX)/2d+rad*Math.cos((num-i)*2d*Math.PI/num),
                                                      (maxY+minY)/2d+rad*Math.sin((num-i)*2d*Math.PI/num)))
                        .collect(Collectors.toList());
                Polygon polyIn = new Polygon(hole);
                poly.addHole(polyIn);
                holes.add(hole);
            } else {
                double radSteiner = Math.sqrt(Math.pow(maxX-minX,2)+Math.pow(maxY-minY,2))/8d;
                // steiner points
                steiner = IntStream.range(0,steinerPoints)
                    .mapToObj(i->new PolygonPoint((maxX+minX)/2d+radSteiner*Math.cos(i*2d*Math.PI/steinerPoints),
                                                  (maxY+minY)/2d+radSteiner*Math.sin(i*2d*Math.PI/steinerPoints)))
                    .collect(Collectors.toList());
            
                poly.addSteinerPoints(steiner);
            }
            
            PolygonSet ps = new PolygonSet(poly);
            Poly2Tri.triangulate(ps);
            
            Polygon polRes = ps.getPolygons().get(0);
            List<DelaunayTriangle> tri = polRes.getTriangles();
            points1 = polRes.getPoints();
            extPoints=points1.size();
            if(pointsHoles!=null || rad>0d){
                holes.forEach(hole->hole.forEach(points1::add));
            } else {
                steiner.forEach(points1::add);
            }
            
            int totalHolePoints=holePoints.stream().reduce(0, Integer::sum);
            int numPoints=extPoints+steinerPoints+totalHolePoints;

            FloatCollector pointsBottom = points1.stream()
                    .flatMapToDouble(p->DoubleStream.of(p.getX(),p.getY(),0d))
                    .collect(()->new FloatCollector(points1.size()*3),FloatCollector::add,FloatCollector::join);
            FloatCollector pointsTop = points1.stream()
                    .flatMapToDouble(p->DoubleStream.of(p.getX(),p.getY(),height.get()))
                    .collect(()->new FloatCollector(points1.size()*3),FloatCollector::add,FloatCollector::join);
            pointsBottom.join(pointsTop);
            points0=pointsBottom.toArray();
            numVertices=points0.length/3;

            FloatCollector texBottom = points1.stream()
                    .flatMapToDouble(p->DoubleStream.of((p.getX()-minX)/(maxX-minX),(p.getY()-minY)/(maxY-minY)))
                    .collect(()->new FloatCollector(points1.size()*2),FloatCollector::add,FloatCollector::join);
            FloatCollector texTop = points1.stream()
                    .flatMapToDouble(p->DoubleStream.of((p.getX()-minX)/(maxX-minX),(p.getY()-minY)/(maxY-minY)))
                    .collect(()->new FloatCollector(points1.size()*2),FloatCollector::add,FloatCollector::join);
            texBottom.join(texTop);
            texCoord0=texBottom.toArray();
            numTexCoords=texCoord0.length/2;
            
            texCoord1 = IntStream.range(0, numTexCoords)
                    .mapToObj(i -> new Point2D(texCoord0[2*i], texCoord0[2*i+1]))
                    .collect(Collectors.toList());
            List<int[]> listIndices = tri.stream().map((DelaunayTriangle t)->{
                int[] pIndex=new int[3];
                for(int j=0; j<3; j++){
                    final TriangulationPoint dt = t.points[j];
                    int[] toArray = IntStream.range(0,points1.size())
                            .filter(i->points1.get(i).equals(dt))
                            .toArray();
                    if(toArray.length>0){
                        pIndex[j]=toArray[0];
                    } else {
                        System.out.println("Error "+points1);
                    }
                }
                return pIndex;
            }).collect(Collectors.toList());
            
            // faces
            
            // base
            IntStream streamBottom = listIndices.stream()
                    .map(i->IntStream.of(i[0], i[0], i[2], i[2], i[1], i[1]))
                    .flatMapToInt(i->i);
            // top
            IntStream streamTop = listIndices.stream()
                    .map(i->IntStream.of(numPoints+i[0], numPoints+i[0], numPoints+i[1], numPoints+i[1], numPoints+i[2], numPoints+i[2]))
                    .flatMapToInt(i->i);
            
            // vertical, exterior
            IntStream streamExtWalls = IntStream.range(0, extPoints-1)
                    .mapToObj(i->IntStream.of(i,i,i+1,i+1,i+1+numPoints,i+1+numPoints,
                                              i,i,i+1+numPoints,i+1+numPoints,i+numPoints,i+numPoints))
                    .flatMapToInt(i->i);
            // vertical, exterior, close polygon
            IntStream streamExtWallsClose = IntStream.of(extPoints-1,extPoints-1,0,0,0+numPoints,0+numPoints,
                    extPoints-1,extPoints-1,0+numPoints,0+numPoints,numPoints+extPoints-1,numPoints+extPoints-1);
            if(totalHolePoints>0){
                // vertical, interior
                // holes
                int acuHolePoints0=extPoints+steinerPoints, acuHolePoints1;
                IntStream streamIntWalls=IntStream.empty();
                for(List<PolygonPoint> hole:holes){
                    acuHolePoints1=acuHolePoints0+hole.size()-1;
                    IntStream streamIntWallsHole = IntStream.range(acuHolePoints0, acuHolePoints1)
                            .mapToObj(i->IntStream.of(i,i,i+1+numPoints,i+1+numPoints,i+1,i+1,
                                                      i,i,i+numPoints,i+numPoints,i+1+numPoints,i+1+numPoints))
                            .flatMapToInt(i->i);
                    streamIntWalls=IntStream.concat(streamIntWalls,streamIntWallsHole);
                    acuHolePoints0=acuHolePoints1+1;
                }
                
                // vertical, interior, close holes
                // holes
                acuHolePoints0=extPoints+steinerPoints;
                IntStream streamIntWallsClose=IntStream.empty();
                for(List<PolygonPoint> hole:holes){
                    acuHolePoints1=acuHolePoints0+hole.size()-1;
                    IntStream streamIntWallsCloseHole = IntStream.of(acuHolePoints1,acuHolePoints1,
                            numPoints+acuHolePoints0,numPoints+acuHolePoints0,
                            acuHolePoints0,acuHolePoints0,
                            acuHolePoints1,acuHolePoints1,
                            numPoints+acuHolePoints1,numPoints+acuHolePoints1,
                            numPoints+acuHolePoints0,numPoints+acuHolePoints0);
                    streamIntWallsClose=IntStream.concat(streamIntWallsClose,streamIntWallsCloseHole);
                    acuHolePoints0=acuHolePoints1+1;
                }
                faces0=IntStream.concat(streamBottom, 
                    IntStream.concat(streamTop,IntStream.concat(streamExtWalls,
                        IntStream.concat(streamExtWallsClose,IntStream.concat(streamIntWalls,streamIntWallsClose)
                                )))).toArray(); 
            } else {
                faces0=IntStream.concat(streamBottom, 
                    IntStream.concat(streamTop,IntStream.concat(streamExtWalls,streamExtWallsClose))).toArray();
            }
            
            numFaces=faces0.length/6;
        } else if(m0!=null) {
            points0=new float[numVertices*m0.getPointElementSize()];
            m0.getPoints().toArray(points0);
            texCoord0=new float[numTexCoords*m0.getTexCoordElementSize()];
            m0.getTexCoords().toArray(texCoord0);
            faces0=new int[numFaces*m0.getFaceElementSize()];
            m0.getFaces().toArray(faces0);
        }
        
        List<Point3D> points1 = IntStream.range(0, numVertices)
                        .mapToObj(i -> new Point3D(points0[3*i], points0[3*i+1], points0[3*i+2]))
                        .collect(Collectors.toList());
        
        texCoord1 = IntStream.range(0, numTexCoords)
                    .mapToObj(i -> new Point2D(texCoord0[2*i], texCoord0[2*i+1]))
                    .collect(Collectors.toList());
        
        List<Face3> faces1 = IntStream.range(0, numFaces)
                    .mapToObj(i -> new Face3(faces0[6*i], faces0[6*i+2], faces0[6*i+4]))
                    .collect(Collectors.toList());

        index.set(points1.size());
        map.clear();
        listFaces.clear();
        listVertices.clear();
        listVertices.addAll(points1);

        faces1.forEach(face->{
            int v1=face.p0;
            int v2=face.p1;
            int v3=face.p2;
            if(level>0){
                int a = getMiddle(v1,points1.get(v1),v2,points1.get(v2));
                int b = getMiddle(v2,points1.get(v2),v3,points1.get(v3));
                int c = getMiddle(v3,points1.get(v3),v1,points1.get(v1));

                listFaces.add(new Face3(v1,a,c));
                listFaces.add(new Face3(v2,b,a));
                listFaces.add(new Face3(v3,c,b));
                listFaces.add(new Face3(a,b,c));
            } else {
                listFaces.add(new Face3(v1,v2,v3));
            }
        });
        map.clear();
        numVertices=listVertices.size();
        numFaces=listFaces.size();
         
        List<Face3> textures1 = IntStream.range(0, faces0.length/6)
                    .mapToObj(i -> new Face3(faces0[6*i+1], faces0[6*i+3], faces0[6*i+5]))
                    .collect(Collectors.toList());

        index.set(texCoord1.size());
        listTextures.clear();
        textures1.forEach(face->{
            int v1=face.p0;
            int v2=face.p1;
            int v3=face.p2;
            if(level>0){
                int a = getMiddle(v1,texCoord1.get(v1),v2,texCoord1.get(v2));
                int b = getMiddle(v2,texCoord1.get(v2),v3,texCoord1.get(v3));
                int c = getMiddle(v3,texCoord1.get(v3),v1,texCoord1.get(v1));

                listTextures.add(new Face3(v1,a,c));
                listTextures.add(new Face3(v2,b,a));
                listTextures.add(new Face3(v3,c,b));
                listTextures.add(new Face3(a,b,c));
            } else {
                listTextures.add(new Face3(v1,v2,v3));
            }
        });
        map.clear();

        texCoord0=texCoord1.stream().flatMapToDouble(p->DoubleStream.of(p.getX(),p.getY()))
                .collect(()->new FloatCollector(texCoord1.size()*2), FloatCollector::add, FloatCollector::join).toArray();
        numTexCoords=texCoord0.length/2;
        textureCoords=texCoord0;
        if(level==getLevel()){
            areaMesh.setWidth(maxX-minX);
            areaMesh.setHeight(maxY-minY);
            rectMesh.setWidth((int)Math.sqrt(texCoord0.length));
            rectMesh.setHeight(texCoord0.length/((int)Math.sqrt(texCoord0.length)));
            
            smoothingGroups=getSmoothingGroups(listVertices, listFaces);
        }
        return createMesh();
    }
    
    private final AtomicInteger index = new AtomicInteger();
    private final HashMap<String, Integer> map = new HashMap<>();

    private int getMiddle(int v1, Point3D p1, int v2, Point3D p2){
        String key = ""+Math.min(v1,v2)+"_"+Math.max(v1,v2);
        if(map.get(key)!=null){
            return map.get(key);
        }

        listVertices.add(p1.add(p2).multiply(0.5f));

        map.put(key,index.get());
        return index.getAndIncrement();
    }
    
    private int getMiddle(int v1, Point2D p1, int v2, Point2D p2){
        String key = ""+Math.min(v1,v2)+"_"+Math.max(v1,v2);
        if(map.get(key)!=null){
            return map.get(key);
        }

        texCoord1.add(p1.add(p2).multiply(0.5f));

        map.put(key,index.get());
        return index.getAndIncrement();
    }
    
    private int[] getSmoothingGroups(List<Point3D> points, List<Face3> faces){
        return faces.stream().mapToInt(f->{
                Point3D a = points.get(f.p0);
                Point3D b = points.get(f.p1);
                Point3D c = points.get(f.p2);
                float nz= b.substract(a).crossProduct((c.substract(a))).normalize().z;
                return (nz<-0.99?1:nz>0.99?2:4);
            }).toArray();
    }
    
    
}
