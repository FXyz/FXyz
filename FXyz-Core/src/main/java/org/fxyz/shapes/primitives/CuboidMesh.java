/**
 * CuboidMesh.java
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

import java.util.Arrays;
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
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.fxyz.geometry.Point3D;
import org.fxyz.collections.FloatCollector;
import org.fxyz.geometry.Face3;

/**
 *
 * @author JosÃ© Pereda Llamas
 * Created on 22-dic-2014 - 21:51:51
 */
public class CuboidMesh extends TexturedMesh {

    private final static double DEFAULT_WIDTH = 10;
    private final static double DEFAULT_HEIGHT = 10;
    private final static double DEFAULT_DEPTH = 10;
    
    private final static int DEFAULT_LEVEL = 0;
    private final static Point3D DEFAULT_CENTER = new Point3D(0f,0f,0f);
    
    
    public CuboidMesh(){
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_DEPTH, DEFAULT_LEVEL,null);
    }
    public CuboidMesh(double width, double height, double depth){
        this(width, height, depth, DEFAULT_LEVEL, null);
    }
    
    public CuboidMesh(double width, double height, double depth, int level, Point3D center){
        setWidth(width);        
        setHeight(height);        
        setDepth(depth);        
        setLevel(level);
        setCenter(center);
        
        updateMesh();
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
    }
    
    private final DoubleProperty width = new SimpleDoubleProperty(DEFAULT_WIDTH){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };

    public double getWidth() {
        return width.get();
    }

    public final void setWidth(double value) {
        width.set(value);
    }

    public DoubleProperty widthProperty() {
        return width;
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
    private final DoubleProperty depth = new SimpleDoubleProperty(DEFAULT_DEPTH){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };

    public double getDepth() {
        return depth.get();
    }

    public final void setDepth(double value) {
        depth.set(value);
    }

    public DoubleProperty depthProperty() {
        return depth;
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
    
    private final ObjectProperty<Point3D> center = new SimpleObjectProperty<Point3D>(DEFAULT_CENTER){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };

    public Point3D getCenter() {
        return center.get();
    }

    public final void setCenter(Point3D value) {
        center.set(value);
    }

    public ObjectProperty<Point3D> centerProperty() {
        return center;
    }
    
    @Override
    protected final void updateMesh() {
        setMesh(null);
        mesh=createCube((float)getWidth(), (float)getHeight(), (float)getDepth(), getLevel());
        setMesh(mesh);
    }
    
    private int numVertices, numTexCoords, numFaces;
    private float[] points0, texCoord0;
    private int[] faces0;
    private List<Point2D> texCoord1;
    private Transform a = new Affine();
    
    private TriangleMesh createCube(float width, float height, float depth, 
            int level){
        
        TriangleMesh m0=null;
        if(level>0){
            m0= createCube(width, height, depth, level-1);
        }
        
        if(level==0){
            a = new Affine();
            float L=2f*width+2f*depth;
            float H=height+2f*depth;
            float hw=width/2f, hh=height/2f, hd=depth/2f;        
            if(center.get()!=null){
                a=a.createConcatenation(new Translate(center.get().x,center.get().y,center.get().z));
//                hw+=center.get().x;
//                hh+=center.get().y;
//                hd+=center.get().z;
            }
            final float[] baseVertices = new float[]{
                hw, hh, hd,             hw, hh, -hd,
                hw, -hh, hd,            hw, -hh, -hd,
                -hw, hh, hd,            -hw, hh, -hd,
                -hw, -hh, hd,           -hw, -hh, -hd
            };

            final float[] baseTexCoords = new float[]{
                depth/L, 0f,                       (depth+width)/L, 0f,
                0f, depth/H,                        depth/L, depth/H, 
                (depth+width)/L, depth/H,           (2f*depth+width)/L, depth/H,  
                1f, depth/H,                        0f, (depth+height)/H,    
                depth/L, (depth+height)/H,          (depth+width)/L, (depth+height)/H,  
                (2f*depth+width)/L, (depth+height)/H,  1f, (depth+height)/H,
                depth/L, 1f,                        (depth+width)/L, 1f        
            };

            final int[] baseTexture = new int[]{
                8,3,7,            3,2,7,            
                9,10,4,           4,10,5,            
                8,12,9,           9,12,13,            
                3,4,0,            0,4,1,            
                8,9,3,            3,9,4,            
                11,6,10,          10,6,5
            };

            final List<Integer> baseFaces = Arrays.asList(
                0,2,1,            2,3,1,            
                4,5,6,            6,5,7,            
                0,1,4,            4,1,5,            
                2,6,3,            3,6,7,            
                0,4,2,            2,4,6,            
                1,3,5,            5,3,7
            );
            
            for(int i=0; i<baseVertices.length/3; i++){
                Point3D ta = transform(baseVertices[3*i],baseVertices[3*i+1],baseVertices[3*i+2]);
                baseVertices[3*i]=ta.x;
                baseVertices[3*i+1]=ta.y;
                baseVertices[3*i+2]=ta.z;
            }
            points0 = baseVertices; 
            numVertices=baseVertices.length/3;
            
            texCoord0 = baseTexCoords;
            numTexCoords=baseTexCoords.length/2;
            
            faces0 = IntStream.range(0, baseFaces.size()/3)
                        .mapToObj(i->IntStream.of(baseFaces.get(3*i), baseTexture[3*i], 
                                baseFaces.get(3*i+1), baseTexture[3*i+1], 
                                baseFaces.get(3*i+2), baseTexture[3*i+2]))
                        .flatMapToInt(i->i).toArray();
            numFaces=baseFaces.size()/3;
        } else if(m0!=null) {
            points0=new float[numVertices*m0.getPointElementSize()];
            m0.getPoints().toArray(points0);
        }
        
        
        List<Point3D> points1 = IntStream.range(0, numVertices)
                        .mapToObj(i -> new Point3D(points0[3*i], points0[3*i+1], points0[3*i+2]))
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
            areaMesh.setWidth(2f*width+2f*depth);
            areaMesh.setHeight(height+2f*depth);
            
            // 1<<j -> bitset, 00100. Otherwise: 000111 will mean they are shared
            smoothingGroups=IntStream.range(0,listFaces.size()).map(i->1<<(i/(listFaces.size()/6))).toArray();
            // smoothing groups based on 3DViewer -> same result
//            float[] normals=new float[]{1,0,0,-1,0,0,0,1,0,0,-1,0,0,0,1,0,0,-1};
//            int[] newFaces = IntStream.range(0, listFaces.size())
//                        .mapToObj(i->IntStream.of((int)listFaces.get(i).x, (int)listFaces.get(i).x, 
//                                (int)listFaces.get(i).y, (int)listFaces.get(i).y, 
//                                (int)listFaces.get(i).z, (int)listFaces.get(i).z))
//                        .flatMapToInt(i->i).toArray();
//            int[] newFaceNormals = IntStream.range(0,listFaces.size()).mapToObj(i->{
//                int j=(i/(listFaces.size()/6));
//                return IntStream.of(j,j,j);
//            }).flatMapToInt(i->i).toArray();
//            smoothingGroups=SmoothingGroups.calcSmoothGroups(new TriangleMesh(), newFaces, newFaceNormals, normals);
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
}
