package org.fxyz.shapes.primitives;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Face3;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.helper.CurvedSpringHelper;

/**
 *  Spring based on this model:  http://math.stackexchange.com/a/461637
 *  Wrapped around a torus: http://math.stackexchange.com/a/324553
    *  Using Frenet-Serret trihedron: http://mathematica.stackexchange.com/a/18612
 */
public class CurvedSpringMesh extends TexturedMesh {

    private static final double DEFAULT_MAJOR_RADIUS = 10.0D;
    private static final double DEFAULT_MINOR_RADIUS = 2.0D;
    private static final double DEFAULT_WIRE_RADIUS = 0.2D;
    private static final double DEFAULT_PITCH = 5.0D;
    private static final double DEFAULT_LENGTH = 100.0D;
    
    private static final int DEFAULT_LENGTH_DIVISIONS = 200;
    private static final int DEFAULT_WIRE_DIVISIONS = 50;
    private static final int DEFAULT_LENGTH_CROP = 0;
    private static final int DEFAULT_WIRE_CROP = 0;
    
    private static final double DEFAULT_START_ANGLE = 0.0D;
    private static final double DEFAULT_X_OFFSET = 0.0D;
    private static final double DEFAULT_Y_OFFSET = 0.0D;
    private static final double DEFAULT_Z_OFFSET = 1.0D;
    
    private CurvedSpringHelper spring;
    
    public CurvedSpringMesh() {
        this(DEFAULT_MAJOR_RADIUS, DEFAULT_MINOR_RADIUS, DEFAULT_WIRE_RADIUS, DEFAULT_PITCH, DEFAULT_LENGTH,
             DEFAULT_LENGTH_DIVISIONS, DEFAULT_WIRE_DIVISIONS, DEFAULT_LENGTH_CROP, DEFAULT_WIRE_CROP);
    }

    public CurvedSpringMesh(double majorRadius, double minorRadius, double wireRadius, double pitch, double length) {
        this(majorRadius, minorRadius, wireRadius, pitch, length, 
             DEFAULT_LENGTH_DIVISIONS, DEFAULT_WIRE_DIVISIONS, DEFAULT_LENGTH_CROP, DEFAULT_WIRE_CROP);
    }

    public CurvedSpringMesh(double majorRadius, double minorRadius, double wireRadius, double pitch, double length, 
                      int rDivs, int tDivs, int lengthCrop, int wireCrop) {
        
        setMajorRadius(majorRadius);
        setMinorRadius(minorRadius);
        setWireRadius(wireRadius);
        setPitch(pitch);
        setLength(length);
        setLengthDivisions(rDivs);
        setWireDivisions(tDivs);
        setLengthCrop(lengthCrop);
        setWireCrop(wireCrop);
        
        updateMesh();
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
    }

    @Override
    protected final void updateMesh(){   
        setMesh(null);
        mesh=createSpring((float) getMajorRadius(), (float) getMinorRadius(), (float) getWireRadius(), (float) getPitch(), (float) getLength(),
            getLengthDivisions(), getWireDivisions(), getLengthCrop(), getWireCrop(),
            (float) getTubeStartAngleOffset(), (float)getxOffset(),(float)getyOffset(), (float)getzOffset());
        setMesh(mesh);
    }
    
    private final DoubleProperty majorRadius = new SimpleDoubleProperty(DEFAULT_MAJOR_RADIUS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getMajorRadius() {
        return majorRadius.get();
    }

    public final void setMajorRadius(double value) {
        majorRadius.set(value);
    }

    public DoubleProperty majorRadiusProperty() {
        return majorRadius;
    }

    private final DoubleProperty minorRadius = new SimpleDoubleProperty(DEFAULT_MINOR_RADIUS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getMinorRadius() {
        return minorRadius.get();
    }

    public final void setMinorRadius(double value) {
        minorRadius.set(value);
    }

    public DoubleProperty minorRadiusProperty() {
        return minorRadius;
    }
    
    private final DoubleProperty wireRadius = new SimpleDoubleProperty(DEFAULT_WIRE_RADIUS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getWireRadius() {
        return wireRadius.get();
    }

    public final void setWireRadius(double value) {
        wireRadius.set(value);
    }

    public DoubleProperty wireRadiusProperty() {
        return wireRadius;
    }

    private final DoubleProperty length = new SimpleDoubleProperty(DEFAULT_LENGTH){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getLength() {
        return length.get();
    }

    public final void setLength(double value) {
        length.set(value);
    }

    public DoubleProperty lengthProperty() {
        return length;
    }

    private final DoubleProperty pitch = new SimpleDoubleProperty(DEFAULT_PITCH){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getPitch() {
        return pitch.get();
    }

    public final void setPitch(double value) {
        pitch.set(value);
    }

    public DoubleProperty pitchProperty() {
        return pitch;
    }
    
    private final IntegerProperty lengthDivisions = new SimpleIntegerProperty(DEFAULT_LENGTH_DIVISIONS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final int getLengthDivisions() {
        return lengthDivisions.get();
    }

    public final void setLengthDivisions(int value) {
        lengthDivisions.set(value);
    }

    public IntegerProperty lengthDivisionsProperty() {
        return lengthDivisions;
    }

    private final IntegerProperty wireDivisions = new SimpleIntegerProperty(DEFAULT_WIRE_DIVISIONS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final int getWireDivisions() {
        return wireDivisions.get();
    }

    public final void setWireDivisions(int value) {
        wireDivisions.set(value);
    }

    public IntegerProperty wireDivisionsProperty() {
        return wireDivisions;
    }

    private final IntegerProperty lengthCrop = new SimpleIntegerProperty(DEFAULT_LENGTH_CROP){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };
    
    public final int getLengthCrop() {
        return lengthCrop.get();
    }

    public final void setLengthCrop(int value) {
        lengthCrop.set(value);
    }

    public IntegerProperty lengthCropProperty() {
        return lengthCrop;
    }

    private final IntegerProperty wireCrop = new SimpleIntegerProperty(DEFAULT_WIRE_CROP){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };
    
    public final int getWireCrop() {
        return wireCrop.get();
    }

    public final void setWireCrop(int value) {
        wireCrop.set(value);
    }

    public IntegerProperty wireCropProperty() {
        return wireCrop;
    }
    
    private final DoubleProperty tubeStartAngleOffset = new SimpleDoubleProperty(DEFAULT_START_ANGLE){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getTubeStartAngleOffset() {
        return tubeStartAngleOffset.get();
    }

    public void setTubeStartAngleOffset(double value) {
        tubeStartAngleOffset.set(value);
    }

    public DoubleProperty tubeStartAngleOffsetProperty() {
        return tubeStartAngleOffset;
    }
    private final DoubleProperty xOffset = new SimpleDoubleProperty(DEFAULT_X_OFFSET){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public final double getxOffset() {
        return xOffset.get();
    }

    public void setxOffset(double value) {
        xOffset.set(value);
    }

    public DoubleProperty xOffsetProperty() {
        return xOffset;
    }
    private final DoubleProperty yOffset = new SimpleDoubleProperty(DEFAULT_Y_OFFSET){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public final double getyOffset() {
        return yOffset.get();
    }

    public void setyOffset(double value) {
        yOffset.set(value);
    }

    public DoubleProperty yOffsetProperty() {
        return yOffset;
    }
    private final DoubleProperty zOffset = new SimpleDoubleProperty(DEFAULT_Z_OFFSET){

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
        
    };

    public final double getzOffset() {
        return zOffset.get();
    }

    public void setzOffset(double value) {
        zOffset.set(value);
    }

    public DoubleProperty zOffsetProperty() {
        return zOffset;
    }
    
    private TriangleMesh createSpring(float majorRadius, float minorRadius, float wireRadius, float pitch, float length, 
            int subDivLength, int subDivWire, int cropLength, int cropWire,
            float startAngle, float xOffset, float yOffset, float zOffset) {
 
        listVertices.clear();
        listTextures.clear();
        listFaces.clear();
        
        int numDivLength = subDivLength + 1-2*cropLength;
        int numDivWire = subDivWire + 1-2*cropWire;
        float pointX, pointY, pointZ;
        double arc=length/pitch;
        double a=wireRadius;
        
        spring = new CurvedSpringHelper(majorRadius, minorRadius, pitch);
        areaMesh.setWidth(spring.getLength(arc));
        areaMesh.setHeight(polygonalSize(wireRadius));
        
        spring.calculateTrihedron(subDivLength, arc);
        for (int t = cropLength; t <= subDivLength-cropLength; t++) {  // 0 - length
            for (int u = cropWire; u <= subDivWire-cropWire; u++) { // -Pi - +Pi
                if(cropWire>0 || (cropWire==0 && u<subDivWire)){
                    float du = (float) (((double)u)*2d*Math.PI / ((double)subDivWire));
                    double pol = polygonalSection(du);
                    float cu=(float)(a*pol*Math.cos(du)), su=(float)(a*pol*Math.sin(du)); 
                    listVertices.add(spring.getS(t, cu, su));
                }
            }
        }
        
        // Create texture coordinates
        createReverseTexCoords(subDivLength-2*cropLength,subDivWire-2*cropWire);
        
        // Create textures
        for (int t = cropLength; t < subDivLength-cropLength; t++) { // 0 - length
            for (int u = cropWire; u < subDivWire-cropWire; u++) { // -Pi - +Pi
                int p00 = (u-cropWire) + (t-cropLength)* numDivWire;
                int p01 = p00 + 1;
                int p10 = p00 + numDivWire;
                int p11 = p10 + 1;
                listTextures.add(new Face3(p00,p01,p11));
                listTextures.add(new Face3(p11,p10,p00));            
            }
        }
        // Create faces
        for (int t = cropLength; t < subDivLength-cropLength; t++) { // 0 - length
            for (int u = cropWire; u < subDivWire-cropWire; u++) { // -Pi - +Pi
                int p00 = (u-cropWire) + (t-cropLength)* (cropWire==0?subDivWire:numDivWire);
                int p01 = p00 + 1;
                int p10 = p00 + (cropWire==0?subDivWire:numDivWire);
                int p11 = p10 + 1;
                if(cropWire==0 && u==subDivWire-1){
                    p01-=subDivWire;
                    p11-=subDivWire;
                }
                listFaces.add(new Face3(p00,p01,p11));
                listFaces.add(new Face3(p11,p10,p00));            
            }
        }
        return createMesh();
    }
 
    public double getTau(double t){
        return spring.getTau(t);
    }
    public double getKappa(double t){
        return spring.getKappa(t);
    }
}
