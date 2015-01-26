

package org.fxyz.shapes.primitives;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Dub
 */
public class CapsuleMesh extends MeshView{
    /*
        Field vars
    */
    public static final int DEFAULT_DIVISIONS = 64;    
    public static final double DEFAULT_RADIUS = 2.0D;
    public static final double DEFAULT_HEIGHT = 10.0D;
    /*
    Constructors
     */
    public CapsuleMesh() {
        this(DEFAULT_RADIUS, DEFAULT_HEIGHT);
    }
    
    public CapsuleMesh(double radius, double height){
        this(DEFAULT_DIVISIONS, radius, height);
    }
    
    public CapsuleMesh(int divisions, double radius, double height) {    
        setRadius(radius);
        setHeight(height);
        setMesh(createCapsule(DEFAULT_DIVISIONS, (float)getRadius(), (float)getHeight()));        
    }

    /*
    Methods
     */
    private static int correctDivisions(int div) {
        return ((div + 3) / 4) * 4;
    }
    
    static TriangleMesh createCapsule(int sphereDivisions, float radius, float height) {
        TriangleMesh m = new TriangleMesh();
        
        sphereDivisions = correctDivisions(sphereDivisions);
        
        final int halfDivisions = sphereDivisions / 2;
        final float fDivisions = 1.f / sphereDivisions;
        
        final int numPoints = sphereDivisions * (halfDivisions - 1) + 2;
        final int numTexCoords = (sphereDivisions + 1) * (halfDivisions - 1) + sphereDivisions * 2;
        final int numFaces = sphereDivisions * (halfDivisions - 2) * 2 + sphereDivisions * 2;

        

        float points[] = new float[numPoints * m.getPointElementSize()];
        float texCoords[] = new float[numTexCoords * m.getTexCoordElementSize()];
        int faces[] = new int[numFaces * m.getFaceElementSize()];

        int pointIndex = 0, texIndex = 0;
        float x, y, z;
        
        for (int i = 0; i < halfDivisions - 1; ++i) {
            float va = fDivisions * (i + 1 - halfDivisions / 2) * 2 * (float) Math.PI;
            float hdY = (float) Math.sin(va);
            float hdX = (float) Math.cos(va);

            float thetaY = 0.5f + hdY * 0.5f;

            for (int point = 0; point < sphereDivisions; ++point) {
                double localTheta = fDivisions * point * 2 * (float) Math.PI;
                float ly = (float) Math.sin(localTheta);
                float lx = (float) Math.cos(localTheta);
                if(i >= (halfDivisions - 1) / 2){
                    points[pointIndex + 0] = x = ly * hdX * (radius);    //X
                    points[pointIndex + 1] = y = hdY * (radius) * height;//Y
                    points[pointIndex + 2] = z = lx * hdX * radius;      //Z
                }else{
                    points[pointIndex + 0] = x = ly * hdX * (radius);   //X
                    points[pointIndex + 1] = y = hdY * (radius);        //Y
                    points[pointIndex + 2] = z = lx * hdX * radius;     //Z
                }
                texCoords[texIndex + 0] = 1 - fDivisions * point;
                texCoords[texIndex + 1] = thetaY;
                pointIndex += 3;
                texIndex += 2;                
                
            }
            texCoords[texIndex + 0] = 0;
            texCoords[texIndex + 1] = thetaY;
            texIndex += 2;
        }
        points[pointIndex + 0] = 0;
        points[pointIndex + 1] = -(radius);
        points[pointIndex + 2] = 0;
        points[pointIndex + 3] = 0;
        points[pointIndex + 4] = radius + height;
        points[pointIndex + 5] = 0;
        pointIndex += 6;

        int pS = (halfDivisions - 1) * sphereDivisions;

        float textureDelta = 1.f / 256;
        for (int i = 0; i < sphereDivisions; ++i) {
            texCoords[texIndex + 0] = fDivisions * (0.5f + i);
            texCoords[texIndex + 1] = textureDelta;
            texIndex += 2;
        }

        for (int i = 0; i < sphereDivisions; ++i) {
            texCoords[texIndex + 0] = fDivisions * (0.5f + i);
            texCoords[texIndex + 1] = 1 - textureDelta;
            texIndex += 2;
        }

        int faceIndex = 0;
        for (int i = 0; i < halfDivisions - 2; ++i) {
            for (int j = 0; j < sphereDivisions; ++j) {
                int p0 = i * sphereDivisions + j;
                int p1 = p0 + 1;
                int p2 = p0 + sphereDivisions;
                int p3 = p1 + sphereDivisions;

                int t0 = p0 + i;
                int t1 = t0 + 1;
                int t2 = t0 + (sphereDivisions + 1);
                int t3 = t1 + (sphereDivisions + 1);

                // add p0, p1, p2
                faces[faceIndex + 0] = p0;
                faces[faceIndex + 1] = t0;
                faces[faceIndex + 2] = p1 % sphereDivisions == 0 ? p1 - sphereDivisions : p1;
                faces[faceIndex + 3] = t1;
                faces[faceIndex + 4] = p2;
                faces[faceIndex + 5] = t2;
                faceIndex += 6;

                // add p3, p2, p1
                faces[faceIndex + 0] = p3 % sphereDivisions == 0 ? p3 - sphereDivisions : p3;
                faces[faceIndex + 1] = t3;
                faces[faceIndex + 2] = p2;
                faces[faceIndex + 3] = t2;
                faces[faceIndex + 4] = p1 % sphereDivisions == 0 ? p1 - sphereDivisions : p1;
                faces[faceIndex + 5] = t1;
                faceIndex += 6;
            }
        }

        int p0 = pS;
        int tB = (halfDivisions - 1) * (sphereDivisions + 1);
        for (int i = 0; i < sphereDivisions; ++i) {
            int p2 = i, p1 = i + 1, t0 = tB + i;
            faces[faceIndex + 0] = p0;
            faces[faceIndex + 1] = t0;
            faces[faceIndex + 2] = p1 == sphereDivisions ? 0 : p1;
            faces[faceIndex + 3] = p1;
            faces[faceIndex + 4] = p2;
            faces[faceIndex + 5] = p2;
            faceIndex += 6;
        }

        p0 = p0 + 1;
        tB = tB + sphereDivisions;
        int pB = (halfDivisions - 2) * sphereDivisions;

        for (int i = 0; i < sphereDivisions; ++i) {
            int p1 = pB + i, p2 = pB + i + 1, t0 = tB + i;
            int t1 = (halfDivisions - 2) * (sphereDivisions + 1) + i, t2 = t1 + 1;
            faces[faceIndex + 0] = p0;
            faces[faceIndex + 1] = t0;
            faces[faceIndex + 2] = p1;
            faces[faceIndex + 3] = t1;
            faces[faceIndex + 4] = p2 % sphereDivisions == 0 ? p2 - sphereDivisions : p2;
            faces[faceIndex + 5] = t2;
            faceIndex += 6;
        }
                
        m.getPoints().setAll(points);
        m.getTexCoords().setAll(texCoords);
        m.getFaces().setAll(faces);
        
        return m;
    }
    /*
        Properties
    */
    private final DoubleProperty radius = new SimpleDoubleProperty(DEFAULT_RADIUS){
        @Override
        protected void invalidated() {
            setMesh(createCapsule(DEFAULT_DIVISIONS, (float)getRadius(), (float)getHeight()));
        }        
    };

    public final double getRadius() {
        return radius.get();
    }

    public final void setRadius(double value) {
        radius.set(value);
    }

    public DoubleProperty radiusProperty() {
        return radius;
    }
    
    private final DoubleProperty height = new SimpleDoubleProperty(DEFAULT_HEIGHT){
        @Override
        protected void invalidated() {
            setMesh(createCapsule(DEFAULT_DIVISIONS, (float)getRadius(), (float)getHeight()));
        }        
    };

    
    public final double getHeight() {
        return height.get();
    }

    public final void setHeight(double value) {
        height.set(value);
    }

    public DoubleProperty heightProperty() {
        return height;
    }
    
    
         
}
