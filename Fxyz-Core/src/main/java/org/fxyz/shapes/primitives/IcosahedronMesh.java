package org.fxyz.shapes.primitives;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Point3D;
import org.fxyz.collections.FloatCollector;
import org.fxyz.geometry.Face3;

/**
 *
 * @author jpereda
 */
public class IcosahedronMesh extends TexturedMesh {
    private final static int DEFAULT_LEVEL = 1;
    private final static float SPHERE_DIAMETER =  1f;
    
    public IcosahedronMesh(){
        this(DEFAULT_LEVEL,SPHERE_DIAMETER);
    }
    public IcosahedronMesh(int level){
        this(level,SPHERE_DIAMETER);
    }
    public IcosahedronMesh(float diameter){
        this(DEFAULT_LEVEL,diameter);
    }
    public IcosahedronMesh(int level, float diameter){
        setLevel(level);
        setDiameter(diameter);

        updateMesh();
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
                updateMesh();
            }
        });
    }
    
    @Override
    protected final void updateMesh(){       
        setMesh(null);
        mesh=createSphere(level.get(), diameter.get());
        setMesh(mesh);
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
    
    /*
        ICOSAHEDRON 
    */
    private final float[] baseVertices = new float[]{
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
    };
    
    private final float[] baseTexCoords = new float[]{
            0.181818f, 0f,             0.363636f, 0f, 
            0.545455f, 0f,             0.727273f, 0f, 
            0.909091f, 0f,             0.0909091f, 0.333333f,
            0.272727f, 0.333333f,      0.454545f, 0.333333f, 
            0.636364f, 0.333333f,      0.818182f, 0.333333f, 
            1f, 0.333333f,             0f, 0.666667f, 
            0.181818f, 0.666667f,      0.363636f, 0.666667f, 
            0.545455f, 0.666667f,      0.727273f, 0.666667f, 
            0.909091f, 0.666667f,      0.0909091f, 1f, 
            0.272727f, 1f,             0.454545f, 1f, 
            0.636364f, 1f,             0.818182f, 1f
    };
    
    private final int[] baseTexture = new int[]{
            5,11,12,            5,12,6,             5,6,0,             10,4,9,
            10,9,16,            6,12,13,            12,11,17,          16,9,15,
            9,3,8,              1,6,7,              14,13,19,          14,20,15,
            14,15,8,            14,8,7,             14,7,13,           18,13,12,
            15,21,16,           8,15,9,             7,8,2,             13,7,6
    };
    
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
    private int numVertices, numTexCoords, numFaces;
    private float[] points0, texCoord0;
    private int[] faces0;
    private List<Point2D> texCoord1;
    
    private TriangleMesh createSphere(int level, float diameter) {
        TriangleMesh m0=null;
        if(level>0){
            m0= createSphere(level-1, diameter);
        }
        
        // read vertices from level-1
        if(level==0){
            points0 = baseVertices; 
            numVertices=baseVertices.length/3;
        } else if(m0!=null) {
            points0=new float[numVertices*m0.getPointElementSize()];
            m0.getPoints().toArray(points0);
        }

        List<Point3D> points1 = IntStream.range(0, numVertices)
                        .mapToObj(i -> new Point3D(points0[3*i], points0[3*i+1], points0[3*i+2]))
                        .collect(Collectors.toList());

        // read textures from level -1
        if(level==0){
            texCoord0 = baseTexCoords;
            numTexCoords=baseTexCoords.length/2;
        } else if(m0!=null){
            texCoord0=new float[numTexCoords*m0.getTexCoordElementSize()];
            m0.getTexCoords().toArray(texCoord0);
        }

        texCoord1 = IntStream.range(0, numTexCoords)
                    .mapToObj(i -> new Point2D(texCoord0[2*i], texCoord0[2*i+1]))
                    .collect(Collectors.toList());
        
        // read faces from level -1
        if(level==0){
            faces0 = IntStream.range(0, baseFaces.size()/3)
                        .mapToObj(i->IntStream.of(baseFaces.get(3*i), baseTexture[3*i], 
                                baseFaces.get(3*i+1), baseTexture[3*i+1], 
                                baseFaces.get(3*i+2), baseTexture[3*i+2]))
                        .flatMapToInt(i->i).toArray();
            numFaces=baseFaces.size()/3;
        } else if(m0!=null){
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
            areaMesh.setWidth(Math.PI*diameter);
            areaMesh.setHeight(Math.PI*diameter);
            rectMesh.setWidth((int)Math.sqrt(texCoord0.length));
            rectMesh.setHeight(texCoord0.length/((int)Math.sqrt(texCoord0.length)));
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

        listVertices.add(p1.add(p2).multiply(0.5f).normalize());

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
