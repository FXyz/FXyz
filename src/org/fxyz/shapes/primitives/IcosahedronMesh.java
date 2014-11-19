/*
 * Copyright (C) 2013-2014 F(X)yz, 
 * Sean Phillips, Jason Pollastrini
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.DepthTest;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Point3D;
import org.fxyz.utils.FloatCollector;
import org.fxyz.utils.Palette;

/**
 *
 * @author jpereda
 */
public class IcosahedronMesh extends MeshView {
    private final static int DEFAULT_COLORS = 16;
    private final static int DEFAULT_LEVEL = 1;
    private final static float SPHERE_DIAMETER =  1f;
    private final static Density DEFAULT_DENSITY= p->0;
    
    private double min = 0d;
    private double max = 1d;
    
    private Palette palette;
    private TriangleMesh mesh;
    
    @FunctionalInterface
    public interface Density {
        double eval(Point3D p);
    }
    
    public IcosahedronMesh(){
        this(DEFAULT_LEVEL,DEFAULT_COLORS,SPHERE_DIAMETER,DEFAULT_DENSITY);
    }
    public IcosahedronMesh(int level, int colors){
        this(level,colors,SPHERE_DIAMETER,DEFAULT_DENSITY);
    }
    public IcosahedronMesh(float diameter){
        this(DEFAULT_LEVEL,DEFAULT_COLORS,diameter,DEFAULT_DENSITY);
    }
    public IcosahedronMesh(int level, int colors, float diameter){
        this(level, colors, diameter, DEFAULT_DENSITY);
    }
    public IcosahedronMesh(int level, int colors, float diameter, Density density){
        setLevel(level);
        setColors(colors);
        setDiameter(diameter);
        setDensity(density);
        
        createPalette(colors);
        mesh=createSphere(level, diameter);
        setMesh(mesh);
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
        
        diameterProperty().addListener((obs,f0,f1)->{
            if(mesh!=null && f0!=null && f1!=null && f0.floatValue()>0 && f1.floatValue()>0){
                updateVertices(f1.floatValue()/f0.floatValue());
            }
        });
        
        levelProperty().addListener((obs,i0,i1)->{
            if(mesh!=null && i1!=null && i1.intValue()>=0){
                mesh=null;
                setMesh(null);
                mesh=createSphere(getLevel(), getDiameter());
                setMesh(mesh);
            }
        });
    }
    
    
    private final FloatProperty diameter = new SimpleFloatProperty(SPHERE_DIAMETER);

    public final float getDiameter() {
        return diameter.get();
    }

    public final void setDiameter(float value) {
        diameter.set(value);
    }

    public final FloatProperty diameterProperty() {
        return diameter;
    }
    
    private final IntegerProperty level = new SimpleIntegerProperty(DEFAULT_LEVEL);

    public final int getLevel() {
        return level.get();
    }

    public final void setLevel(int value) {
        level.set(value);
    }

    public final IntegerProperty levelProperty() {
        return level;
    }
    
    private final IntegerProperty colors = new SimpleIntegerProperty(DEFAULT_COLORS){

        @Override
        protected void invalidated() {
            createPalette(getColors());
            updateTexture();
            updateTextureOnFaces();
        }
        
    };

    public final int getColors() {
        return colors.get();
    }

    public final void setColors(int value) {
        colors.set(value);
    }

    public IntegerProperty colorsProperty() {
        return colors;
    }
    
    private final ObjectProperty<Density> density = new SimpleObjectProperty<Density>(DEFAULT_DENSITY){
        
        @Override
        protected void invalidated() {
            updateTextureOnFaces();
        }
    };
    
    public final Density getDensity(){
        return density.get();
    }
    
    public final void setDensity(Density value){
        this.density.set(value);
    }
    
    public final ObjectProperty<Density> densityProperty() {
        return density;
    }

    /*
        ICOSAHEDRON 
    */
    private final List<Float> baseVertices = Arrays.asList(
        -0.525731f,  0.850651f, 0.f,
         0.525731f,  0.850651f, 0.f, 
        -0.525731f, -0.850651f, 0.f,
         0.525731f, -0.850651f, 0.f, 
        0.f, -0.525731f,  0.850651f, 
        0.f,  0.525731f,  0.850651f, 
        0.f, -0.525731f, -0.850651f, 
        0.f,  0.525731f, -0.850651f, 
         0.850651f, 0.f, -0.525731f, 
         0.850651f, 0.f,  0.525731f, 
        -0.850651f, 0.f, -0.525731f, 
        -0.850651f, 0.f,  0.525731f
     );
    
    private final List<Integer> baseFaces = Arrays.asList(
            0,11,5,             0,5,1,             0,1,7,             0,7,10,
            0,10,11,            1,5,9,             5,11,4,            11,10,2,
            10,7,6,             7,1,8,             3,9,4,             3,4,2,
            3,2,6,              3,6,8,             3,8,9,             4,9,5,
            2,4,11,             6,2,10,            8,6,7,             9,8,1
    );
    
    /*
        ICOSPHERE
    */
    private final List<Point3D> points2 = new ArrayList<>();
    private final List<Point3D> faces2 = new ArrayList<>();
    private int numVertices, numFaces;
    private float[] points0;
    private int[] faces0;
    
    private TriangleMesh createSphere(int level, float diameter) {
        TriangleMesh m0=null;
        if(level>0){
            m0= createSphere(level-1, diameter);
        }
        
        // read vertices from level-1
        if(level==0){
            points0 = IntStream.range(0, baseVertices.size()/3)
                        .mapToObj(i -> new Point3D(baseVertices.get(3*i), baseVertices.get(3*i+1), baseVertices.get(3*i+2)))
                        .flatMapToDouble(p->p.getCoordinates(diameter))
                        .collect(FloatCollector::new, FloatCollector::add, FloatCollector::join)
                        .toArray(); 
            numVertices=baseVertices.size()/3;
        } else if(m0!=null) {
            points0=new float[numVertices*m0.getPointElementSize()];
            m0.getPoints().toArray(points0);
        }

        List<Point3D> points1 = IntStream.range(0, numVertices)
                        .mapToObj(i -> new Point3D(points0[3*i], points0[3*i+1], points0[3*i+2]))
                        .collect(Collectors.toList());

        // read faces from level -1
        if(level==0){
            faces0 = IntStream.range(0, baseFaces.size()/3)
                        .mapToObj(i->IntStream.of(baseFaces.get(3*i), 0, baseFaces.get(3*i+1), 0, baseFaces.get(3*i+2), 0))
                        .flatMapToInt(i->i).toArray();
            numFaces=baseFaces.size()/3;
        } else if(m0!=null){
            faces0=new int[numFaces*m0.getFaceElementSize()];
            m0.getFaces().toArray(faces0);
        }
        List<Point3D> faces1 = IntStream.range(0, numFaces)
                    .mapToObj(i -> new Point3D(faces0[6*i], faces0[6*i+2], faces0[6*i+4]))
                    .collect(Collectors.toList());

        index.set(points1.size());
        map.clear();
        faces2.clear();
        points2.clear();
        points2.addAll(points1);

        faces1.forEach(face->{
            int v1=(int)face.x;
            int v2=(int)face.y;
            int v3=(int)face.z;
            if(level>0){
                int a = getMiddle(v1,points1.get(v1),v2,points1.get(v2));
                int b = getMiddle(v2,points1.get(v2),v3,points1.get(v3));
                int c = getMiddle(v3,points1.get(v3),v1,points1.get(v1));

                faces2.add(new Point3D(v1,a,c));
                faces2.add(new Point3D(v2,b,a));
                faces2.add(new Point3D(v3,c,b));
                faces2.add(new Point3D(a,b,c));
            } else {
                faces2.add(new Point3D(v1,v2,v3));
            }
        });
        numVertices=points2.size();
        numFaces=faces2.size();
        if(level==getLevel()){
            System.out.println("level: "+level+", v: "+numVertices+", f: "+numFaces);
        }
        // new mesh
        TriangleMesh m = new TriangleMesh();
        // vertices for level
        long time=System.currentTimeMillis();
        float[] vertexArray = points2.stream()
            .flatMapToDouble(Point3D::getCoordinates)
            .collect(()->new FloatCollector(points2.size()*3), FloatCollector::add, FloatCollector::join).toArray();
        if(level==getLevel()){
            System.out.println("t0: "+(System.currentTimeMillis()-time));
        }
        m.getPoints().setAll(vertexArray);
        
        if(level==getLevel()){
            // textures for level
            float[] textureArray = IntStream.range(0,getColors()).boxed()
                .flatMapToDouble(i -> palette.getTextureLocation(i))
                .collect(()->new FloatCollector(getColors()*2), FloatCollector::add, FloatCollector::join)
                .toArray();
            m.getTexCoords().setAll(textureArray);
            
            updateExtremes();
        }
              
        // faces for level
        int[] faces = faces2.parallelStream().map(f->{
                int p0=(int)f.x; int p1=(int)f.y; int p2=(int)f.z;
                int t0=0, t1=0, t2=0;
                if(level==getLevel()){
                    t0=mapDensity(points2.get(p0));
                    t1=mapDensity(points2.get(p1));
                    t2=mapDensity(points2.get(p2));
                }
                return IntStream.of(p0, t0, p1, t1, p2, t2);
            }).flatMapToInt(i->i).toArray();
         m.getFaces().setAll(faces);
         
        return m;
    }
    
    private final AtomicInteger index = new AtomicInteger();
    private final HashMap<String, Integer> map = new HashMap<>();

    private int getMiddle(int v1, Point3D p1, int v2, Point3D p2){
        String key = ""+Math.min(v1,v2)+"_"+Math.max(v1,v2);
        if(map.get(key)!=null){
            return map.get(key);
        }

        points2.add(p1.add(p2).multiply(0.5f).normalize());

        map.put(key,index.get());
        return index.getAndIncrement();
    }

    private void createPalette(int colors) {
        palette=new Palette(colors);
        palette.createPalette(false);
        
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(palette.getImgPalette());
        setMaterial(mat);
    }
    
    private int mapDensity(Point3D p){
        int f=(int)((density.get().eval(p)-min)/(max-min)*getColors());
        if(f<0){
            f=0;
        }
        if(f>=getColors()){
            f=getColors()-1;
        }
        return f;
    }
    
    private void updateVertices(float factor){
        if(mesh!=null){
            float[] vertexArray = points2.stream()
                .flatMapToDouble(p->p.getCoordinates(factor))
                .collect(()->new FloatCollector(points2.size()*3), FloatCollector::add, FloatCollector::join)
                .toArray();       
            mesh.getPoints().setAll(vertexArray);
        
        }
    }
    private void updateTexture(){
        if(mesh!=null){
            float[] textureArray = IntStream.range(0,getColors()).boxed()
                .flatMapToDouble(i -> palette.getTextureLocation(i))
                .collect(()->new FloatCollector(getColors()*2), FloatCollector::add, FloatCollector::join)
                .toArray();
            mesh.getTexCoords().setAll(textureArray);
        }
    }
    
    private void updateExtremes(){
        max=points2.parallelStream().mapToDouble(p->density.get().eval(p)).max().orElse(1.0);
        min=points2.parallelStream().mapToDouble(p->density.get().eval(p)).min().orElse(0.0);
        if(max==min){
            max=1.0+min;
        }
//        System.out.println("Min: "+min+", max: "+max);  
    }
    
    private void updateTextureOnFaces(){
        // textures for level
        if(mesh!=null){
            updateExtremes();
            int[] faces = faces2.parallelStream().map(f->{
                int p0=(int)f.x; int p1=(int)f.y; int p2=(int)f.z;
                int t0=mapDensity(points2.get(p0));
                int t1=mapDensity(points2.get(p1));
                int t2=mapDensity(points2.get(p2));
                return IntStream.of(p0, t0, p1, t1, p2, t2);
            }).flatMapToInt(i->i).toArray();
            mesh.getFaces().setAll(faces);
        }
    }
    
}
