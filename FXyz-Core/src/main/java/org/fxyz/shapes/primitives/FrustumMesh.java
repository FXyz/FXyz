/**
 * FrustumMesh.java
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

package org.fxyz.shapes.primitives;

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
import javafx.geometry.Point2D;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.SectionType;
import org.fxyz.geometry.Face3;
import org.fxyz.collections.FloatCollector;

/**
 *
 * @author JosÃ© Pereda Llamas
 * Created on 22-dic-2014 - 21:51:51
 */
public class FrustumMesh extends TexturedMesh {

    private final static int DEFAULT_DIVISIONS = 20;
    private final static double DEFAULT_MAJOR_RADIUS = 1;
    private final static double DEFAULT_MINOR_RADIUS = 0;
    private final static double DEFAULT_HEIGHT = 10;
    
    private final static int DEFAULT_LEVEL = 1;
    
    public FrustumMesh(){
        this(DEFAULT_MAJOR_RADIUS, DEFAULT_MINOR_RADIUS, DEFAULT_HEIGHT, DEFAULT_LEVEL,null,null);
    }
    public FrustumMesh(double majorRadius, double minorRadius, double height){
        this(majorRadius, minorRadius, height, DEFAULT_LEVEL,null,null);
    }
    
    public FrustumMesh(double majorRadius, double minorRadius, double height, int level){
        this(majorRadius,minorRadius, height,level,null,null);
    }
    public FrustumMesh(double majorRadius, double minorRadius, double height, int level, Point3D pIni, Point3D pEnd){
        setAxisOrigin(pIni==null?new Point3D(0,(float)height/2f,0):pIni);
        setAxisEnd(pEnd==null?new Point3D(0,-(float)height/2f,0):pEnd);
        setMajorRadius(majorRadius);
        setMinorRadius(minorRadius);
        setLevel(level);
        
        updateMesh();
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
    }
    private final DoubleProperty majorRadius = new SimpleDoubleProperty(DEFAULT_MAJOR_RADIUS){
        
        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public double getMajorRadius() {
        return majorRadius.get();
    }

    public final void setMajorRadius(double value) {
        majorRadius.set(value);
    }

    public DoubleProperty majorRadiusProperty() {
        return majorRadius;
    }
    
    private final DoubleProperty minorRadius = new SimpleDoubleProperty(DEFAULT_MINOR_RADIUS){
        
        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public double getMinorRadius() {
        return minorRadius.get();
    }

    public final void setMinorRadius(double value) {
        minorRadius.set(value);
    }

    public DoubleProperty minorRadiusProperty() {
        return minorRadius;
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
    
    private final ObjectProperty<Point3D> axisOrigin = new SimpleObjectProperty<Point3D>(){
        @Override
        protected void invalidated() {
            if(getAxisOrigin()!=null && getAxisEnd()!=null){
                setHeight(getAxisEnd().substract(getAxisOrigin()).magnitude());
            }
        }
    };

    public Point3D getAxisOrigin() {
        return axisOrigin.get();
    }

    public final void setAxisOrigin(Point3D value) {
        axisOrigin.set(value);
    }

    public ObjectProperty axisOriginProperty() {
        return axisOrigin;
    }
    private final ObjectProperty<Point3D> axisEnd = new SimpleObjectProperty<Point3D>(){
        @Override
        protected void invalidated() {
            if(getAxisOrigin()!=null && getAxisEnd()!=null){
                setHeight(getAxisEnd().substract(getAxisOrigin()).magnitude());
            }
        }
    };

    public Point3D getAxisEnd() {
        return axisEnd.get();
    }

    public final void setAxisEnd(Point3D value) {
        axisEnd.set(value);
    }

    public ObjectProperty axisEndProperty() {
        return axisEnd;
    }
    
    @Override
    protected final void updateMesh() {
        setMesh(null);
        mesh=createFrustum((float)getMajorRadius(), (float)getMinorRadius(), (float)getHeight(), getLevel());
        setMesh(mesh);
    }
    
    private int numVertices, numTexCoords, numFaces;
    private float[] points0, texCoord0;
    private int[] faces0;
    private List<Point2D> texCoord1;
    
   /*
        cylinder mesh is generated on (0,h/2,0) -> (0,-h/2,0) local coordinates
        With Transform a we transform vertices coordinates to generate directly the cyilinder from
        pIni to pEnd in global coordinates:
            pIni == a.transform(0,h/2,0)
            pEnd == a.transform(0,-h/2,0)
    */
    private Transform a = new Affine();
    
    private TriangleMesh createFrustum(float majorRadius, float minorRadius, float height, int level){
        
        TriangleMesh m0=null;
        if(level>0){
            m0 = createFrustum(majorRadius, minorRadius, height, level-1);
        }
        
        if(level==0){
            a = new Affine();
            int div=DEFAULT_DIVISIONS>3?DEFAULT_DIVISIONS:3;
            if(getSectionType()!=TriangleMeshHelper.SectionType.CIRCLE){
                div=getSectionType().getSides()*((int)(div/getSectionType().getSides())+1);
            }
            
            if(getAxisOrigin()!=null && getAxisEnd()!=null){
                Point3D dir=getAxisEnd().substract(getAxisOrigin()).crossProduct(new Point3D(0,-1,0));
                double angle=Math.acos(getAxisEnd().substract(getAxisOrigin()).normalize().dotProduct(new Point3D(0,-1,0)));
                a=a.createConcatenation(new Translate(getAxisOrigin().x, getAxisOrigin().y-height/2d, getAxisOrigin().z))
                   .createConcatenation(new Rotate(-Math.toDegrees(angle), 0d,height/2d,0d,
                                                   new javafx.geometry.Point3D(dir.x,-dir.y,dir.z)));
            }
            int nPoints=2*div+2;
            float rBase=majorRadius;
            float rTop=minorRadius;
            float h=height;
            final float[] baseVertices = new float[nPoints*3];
            // base at y=h/2
            for(int i=0; i<div; i++){
                double ang=i*2d*Math.PI/div;
                double pol = polygonalSection(ang);
                Point3D ta = transform(rBase*pol*Math.cos(ang),h/2,rBase*pol*Math.sin(ang));
                baseVertices[3*i]=ta.x;
                baseVertices[3*i+1]=ta.y;
                baseVertices[3*i+2]=ta.z;
            }
            // top at y=-h/2
            for(int i=div; i<2*div; i++){
                double ang=i*2d*Math.PI/div;
                double pol = polygonalSection(ang);
                Point3D ta = transform(rTop*pol*Math.cos(ang),-h/2,rTop*pol*Math.sin(ang));
                baseVertices[3*i]=ta.x;
                baseVertices[3*i+1]=ta.y;
                baseVertices[3*i+2]=ta.z;
            }
            Point3D ta = transform(0,h/2,0);
            baseVertices[6*div]=ta.x;
            baseVertices[6*div+1]=ta.y;
            baseVertices[6*div+2]=ta.z;
            ta = transform(0,-h/2,0);
            baseVertices[6*div+3]=ta.x;
            baseVertices[6*div+4]=ta.y;
            baseVertices[6*div+5]=ta.z;
            
            int nTextCoords=div*4+6;
            float rectBase=(float)polygonalSize(rBase);
            float rectTop=(float)polygonalSize(rTop);
            float L=(float)(rBase+2d*Math.PI*rBase);
            float H=2f*(rBase+rTop)+h;
            final float[] baseTexCoords = new float[nTextCoords*2];
            // u right ,v up
            for(int i=0; i<=div; i++){
                baseTexCoords[2*i]=(float)(rBase+i*rectBase/div)/L;
                baseTexCoords[2*i+1]=(float)(2f*rTop+h)/H;
            }
            for(int i=0; i<=div; i++){
                baseTexCoords[2*div+2*i+2]=(float)(rBase+i*rectTop/div)/L;
                baseTexCoords[2*div+2*i+3]=(float)(2f*rTop)/H;
            }
            for(int i=0; i<=div; i++){
                double ang=i*2d*Math.PI/div;
                double pol = polygonalSection(ang);
                baseTexCoords[4*div+2*i+4]=(float)(rBase+rBase*pol*Math.sin(ang))/L;
                baseTexCoords[4*div+2*i+5]=(float)(2f*rTop+rBase+h-rBase*pol*Math.cos(ang))/H;
            }
            for(int i=0; i<=div; i++){
                double ang=i*2d*Math.PI/div;
                double pol = polygonalSection(ang);
                baseTexCoords[6*div+2*i+6]=(float)(rBase+rTop*pol*Math.sin(ang))/L;
                baseTexCoords[6*div+2*i+7]=(float)(rTop+rTop*pol*Math.cos(ang))/H;
            }
            baseTexCoords[8*div+8]=rBase/L;
            baseTexCoords[8*div+9]=(2f*rTop+rBase+h)/H;
            baseTexCoords[8*div+10]=rBase/L;
            baseTexCoords[8*div+11]=rTop/H;
            
            int nFaces=div*4;
            final int[] baseTexture = new int[nFaces*3];

            final int[] baseFaces = new int[nFaces*3];
            for(int i=0; i<div; i++){
                int p1=i+1;
                int p2=i+div;
                int p3=i+div+1;
                baseFaces[6*i]=i;
                baseFaces[6*i+1]=p1==div?0:p1;                
                baseFaces[6*i+2]=p2;
                baseFaces[6*i+3]=p3%div==0?p3-div:p3;
                baseFaces[6*i+4]=p2;
                baseFaces[6*i+5]=p1==div?0:p1;
                baseTexture[6*i]=i;
                baseTexture[6*i+1]=p1;                
                baseTexture[6*i+2]=p2+1;
                baseTexture[6*i+3]=p3+1;
                baseTexture[6*i+4]=p2+1;
                baseTexture[6*i+5]=p1;
            }
            for(int i=0; i<div; i++){
                int p1=div*2;
                int p2=i+1;
                baseFaces[6*div+3*i]=i;
                baseFaces[6*div+3*i+1]=p1;
                baseFaces[6*div+3*i+2]=p2==div?0:p2;
                baseTexture[6*div+3*i]=(div+1)*2+i;
                baseTexture[6*div+3*i+1]=(div+1)*4;
                baseTexture[6*div+3*i+2]=(div+1)*2+i+1;
            }
            for(int i=0; i<div; i++){
                int p1=div*2+1;
                int p2=i+1+div;
                baseFaces[9*div+3*i]=i+div;
                baseFaces[9*div+3*i+1]=p2%div==0?p2-div:p2; 
                baseFaces[9*div+3*i+2]=p1;
                baseTexture[9*div+3*i]=(div+1)*3+i;
                baseTexture[9*div+3*i+1]=(div+1)*3+i+1; 
                baseTexture[9*div+3*i+2]=(div+1)*4+1;
            }
            points0 = baseVertices; 
            numVertices=baseVertices.length/3;
            
            texCoord0 = baseTexCoords;
            numTexCoords=baseTexCoords.length/2;
            
            faces0 = IntStream.range(0, baseFaces.length/3)
                        .mapToObj(i->IntStream.of(baseFaces[3*i], baseTexture[3*i], 
                                baseFaces[3*i+1], baseTexture[3*i+1], 
                                baseFaces[3*i+2], baseTexture[3*i+2]))
                        .flatMapToInt(i->i).toArray();
            numFaces=baseFaces.length/3;
        } else if(m0!=null) {
            points0=new float[numVertices*m0.getPointElementSize()];
            m0.getPoints().toArray(points0);
        }
        
        final float h=height;
        List<Point3D> points1 = IntStream.range(0, numVertices)
                .mapToObj(i -> {
                    Point3D p=new Point3D(points0[3*i], points0[3*i+1], points0[3*i+2]);
                    // f = h of local cylinder from 0 on top (ini) to 1 on bottom (end)
                    p.f=(h/2-unTransform(p).y)/h;
                    return p;
                })
                .collect(Collectors.toList());
        
        if(level>0 && m0!=null){
            texCoord0=new float[numTexCoords*m0.getTexCoordElementSize()];
            m0.getTexCoords().toArray(texCoord0);
        }
        
        texCoord1 = IntStream.range(0, numTexCoords)
                    .mapToObj(i -> new Point2D(texCoord0[2*i], texCoord0[2*i+1]))
                    .collect(Collectors.toList());
        
        if(level>0 && m0!=null){
            faces0=new int[numFaces*m0.getFaceElementSize()];
            m0.getFaces().toArray(faces0);
        }
        
        List<Face3> faces1 = IntStream.range(0, numFaces)
                    .mapToObj(i -> new Face3(faces0[6*i], faces0[6*i+2], faces0[6*i+4]))
                    .collect(Collectors.toList());

        index.set(points1.size());
        map.clear();
        listVertices.clear();
        listFaces.clear();
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
        
        List<Face3> textures1;
        if(level==0){
            textures1= IntStream.range(0, faces0.length/6)
                    .mapToObj(i -> new Face3(faces0[6*i+1], faces0[6*i+3], faces0[6*i+5]))
                    .collect(Collectors.toList());
        } else {
            textures1 = listTextures.stream().map(t->t).collect(Collectors.toList());
        }

        index.set(texCoord1.size());
        listTextures.clear();
        AtomicInteger kk=new AtomicInteger();
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
            areaMesh.setWidth(majorRadius+2f*Math.PI*majorRadius);
            areaMesh.setHeight(height+2f*(minorRadius+majorRadius));
            smoothingGroups=IntStream.range(0,listFaces.size()).map(i->{
                if(getSectionType()!=TriangleMeshHelper.SectionType.CIRCLE){
                    return 0;
                }
                if(i<listFaces.size()/2){
                    return 1;
                } else if(i<3*listFaces.size()/4){
                    return 2;
                }
                return 4;
            }).toArray();
        }
        return createMesh();
    }

    private Point3D transform(Point3D p){
        javafx.geometry.Point3D ta = a.transform(p.x,p.y,p.z);
        return new Point3D((float)ta.getX(), (float)ta.getY(), (float)ta.getZ());        
    }
    private Point3D transform(double x, double y, double z){
        javafx.geometry.Point3D ta = a.transform(x,y,z);
        return new Point3D((float)ta.getX(), (float)ta.getY(), (float)ta.getZ());        
    }
    public Point3D unTransform(Point3D p){
        try {
            javafx.geometry.Point3D ta = a.inverseTransform(p.x,p.y,p.z);
            return new Point3D((float)ta.getX(), (float)ta.getY(), (float)ta.getZ());
        } catch (NonInvertibleTransformException ex) {
            System.out.println("p not invertible "+p);
        }
        return p;
    }
    
    private final AtomicInteger index = new AtomicInteger();
    private final HashMap<String, Integer> map = new HashMap<>();

    private int getMiddle(int v1, Point3D p1, int v2, Point3D p2){
        String key = ""+Math.min(v1,v2)+"_"+Math.max(v1,v2);
        if(map.get(key)!=null){
            return map.get(key);
        }

        Point3D p3 = p1.add(p2).multiply(0.5f);
        if(getSectionType().equals(SectionType.CIRCLE)){
            if(inCircle(p1) && inCircle(p2)){
                Point3D p4 = unTransform(p3);
                float fact=(float)(radius(p4.y)/Math.sqrt(p4.x*p4.x+p4.z*p4.z));
                p3=transform(fact*p4.x,p4.y,fact*p4.z);
                if(!inCircle(p3)){
                    System.out.println("p3: "+p3);
                }
            }
        }
        // f = h of local cylinder from 0 on top (ini) to 1 on bottom (end)
        p3.f=(float)((height.get()/2d-unTransform(p3).y)/height.get());
        listVertices.add(p3);
        
        map.put(key,index.get());
        return index.getAndIncrement();
    }
    
    private double radius(double y){
        return majorRadius.get()+(minorRadius.get()-majorRadius.get())*(height.get()/2d-y)/height.get();
    }
    private boolean inCircle(Point3D p){
        Point3D p2=unTransform(p);
        return p2.x*p2.x+p2.z*p2.z>0.99*Math.pow(radius(p2.y),2);
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
}
