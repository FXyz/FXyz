package org.fxyz.shapes.primitives;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Point3D;

/**
 * SegmentedTorusMesh is based in TorusMesh, but allows cutting the torus in two 
 * directions, in order to have a banner parallel to an uncut torus.
 * Based on a regular 2D TriangleMesh, mapped to a 3D mesh with the torus parametric equations
 * Crop allows cutting/cropping the 2D mesh on the borders
 * If crop ==0  then  a regular torus is formed (thought with slight differences from 
 * TorusMesh)
 */
public class SegmentedTorusMesh extends TexturedMesh {

    private static final int DEFAULT_MAJOR_DIVISIONS = 64;
    private static final int DEFAULT_MINOR_DIVISIONS = 64;
    private static final int DEFAULT_MAJOR_CROP = 0;
    private static final double DEFAULT_MAJOR_RADIUS = 12.5D;
    private static final double DEFAULT_MINOR_RADIUS = 5.0D;
    private static final double DEFAULT_START_ANGLE = 0.0D;
    private static final double DEFAULT_X_OFFSET = 0.0D;
    private static final double DEFAULT_Y_OFFSET = 0.0D;
    private static final double DEFAULT_Z_OFFSET = 1.0D;
    
    public SegmentedTorusMesh() {
        this(DEFAULT_MAJOR_DIVISIONS, DEFAULT_MINOR_DIVISIONS, DEFAULT_MAJOR_CROP, DEFAULT_MAJOR_RADIUS, DEFAULT_MINOR_RADIUS);
    }

    public SegmentedTorusMesh(double majorRadius, double minorRadius) {
        this(DEFAULT_MAJOR_DIVISIONS, DEFAULT_MINOR_DIVISIONS, DEFAULT_MAJOR_CROP, majorRadius, minorRadius);
    }

    public SegmentedTorusMesh(int rDivs, int tDivs, int crop, double majorRadius, double minorRadius) {
        setMajorRadiusDivisions(rDivs);
        setMinorRadiusDivisions(tDivs);
        setMajorRadiusCrop(crop);
        setMajorRadius(majorRadius);
        setMinorRadius(minorRadius);
        
        updateMesh();
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
    }

    @Override
    protected final void updateMesh(){       
        setMesh(null);
        mesh=createTorus(
            getMajorRadiusDivisions(), 
            getMinorRadiusDivisions(), 
            getMajorRadiusCrop(),
            (float) getMajorRadius(), 
            (float) getMinorRadius(), 
            (float) getTubeStartAngleOffset(), 
            (float)getxOffset(),
            (float)getyOffset(), 
            (float)getzOffset());
        setMesh(mesh);
    }
    
    private final IntegerProperty majorRadiusDivisions = new SimpleIntegerProperty(DEFAULT_MAJOR_DIVISIONS) {

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };

    public final int getMajorRadiusDivisions() {
        return majorRadiusDivisions.get();
    }

    public final void setMajorRadiusDivisions(int value) {
        majorRadiusDivisions.set(value);
    }

    public IntegerProperty majorRadiusDivisionsProperty() {
        return majorRadiusDivisions;
    }

    private final IntegerProperty minorRadiusDivisions = new SimpleIntegerProperty(DEFAULT_MINOR_DIVISIONS) {

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };

    public final int getMinorRadiusDivisions() {
        return minorRadiusDivisions.get();
    }

    public final void setMinorRadiusDivisions(int value) {
        minorRadiusDivisions.set(value);
    }

    public IntegerProperty minorRadiusDivisionsProperty() {
        return minorRadiusDivisions;
    }

    private final IntegerProperty majorRadiusCrop = new SimpleIntegerProperty(DEFAULT_MAJOR_CROP) {

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };
    public final int getMajorRadiusCrop() {
        return majorRadiusCrop.get();
    }

    public final void setMajorRadiusCrop(int value) {
        majorRadiusCrop.set(value);
    }

    public IntegerProperty majorRadiusCropProperty() {
        return majorRadiusCrop;
    }

    private final DoubleProperty majorRadius = new SimpleDoubleProperty(DEFAULT_MAJOR_RADIUS) {

        @Override
        protected void invalidated() {
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

    public DoubleProperty radiusMajorProperty() {
        return majorRadius;
    }

    private final DoubleProperty minorRadius = new SimpleDoubleProperty(DEFAULT_MINOR_RADIUS) {

        @Override
        protected void invalidated() {
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

    private final DoubleProperty tubeStartAngleOffset = new SimpleDoubleProperty(DEFAULT_START_ANGLE) {

        @Override
        protected void invalidated() {
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
    private final DoubleProperty xOffset = new SimpleDoubleProperty(DEFAULT_X_OFFSET) {

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
    private final DoubleProperty yOffset = new SimpleDoubleProperty(DEFAULT_Y_OFFSET) {

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
    private final DoubleProperty zOffset = new SimpleDoubleProperty(DEFAULT_Z_OFFSET) {

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
    
    private TriangleMesh createTorus(int subDivX, int subDivY, int crop, float meanRadius,
            float minorRadius, float tubeStartAngle, float xOffset, float yOffset, float zOffset) {
 
        listVertices.clear();
        listTextures.clear();
        listFaces.clear();
        
        int numDivX = subDivX + 1-2*crop;
        float pointX, pointY, pointZ;
        
        areaMesh.setWidth(2d*Math.PI*(meanRadius+minorRadius));
        areaMesh.setHeight(2d*Math.PI*minorRadius);
        
        // Create points
        for (int y = crop; y <= subDivY-crop; y++) {
            float dy = (float) y / subDivY;
            for (int x = crop; x <= subDivX-crop; x++) {
                float dx = (float) x / subDivX;
                if(crop>0 || (crop==0 && x<subDivX && y<subDivY)){
                    pointX = (float) ((meanRadius+minorRadius*Math.cos((-1d+2d*dy)*Math.PI))*(Math.cos((-1d+2d*dx)*Math.PI)+ xOffset));
                    pointZ = (float) ((meanRadius+minorRadius*Math.cos((-1d+2d*dy)*Math.PI))*(Math.sin((-1d+2d*dx)*Math.PI)+ yOffset));
                    pointY = (float) (minorRadius*Math.sin((-1d+2d*dy)*Math.PI)*zOffset);
                    listVertices.add(new Point3D(pointX, pointY, pointZ));
                }
            }
        }
        // Create texture coordinates
        createTexCoords(subDivX-2*crop,subDivY-2*crop);
        
        // Create textures
        for (int y = crop; y < subDivY-crop; y++) {
            for (int x = crop; x < subDivX-crop; x++) {
                int p00 = (y-crop) * numDivX + (x-crop);
                int p01 = p00 + 1;
                int p10 = p00 + numDivX;
                int p11 = p10 + 1;
                listTextures.add(new Point3D(p00,p10,p11));                
                listTextures.add(new Point3D(p11,p01,p00));
            }
        }
        // Create faces
        for (int y = crop; y < subDivY-crop; y++) {
            for (int x = crop; x < subDivX-crop; x++) {
                int p00 = (y-crop) * ((crop>0)?numDivX:numDivX-1) + (x-crop);
                int p01 = p00 + 1;
                if(crop==0 && x==subDivX-1){
                    p01-=subDivX;
                }
                int p10 = p00 + ((crop>0)?numDivX:numDivX-1);
                if(crop==0 && y==subDivY-1){
                    p10-=subDivY*((crop>0)?numDivX:numDivX-1);
                }
                int p11 = p10 + 1;
                if(crop==0 && x==subDivX-1){
                    p11-=subDivX;
                }                
                listFaces.add(new Point3D(p00,p10,p11));                
                listFaces.add(new Point3D(p11,p01,p00));
            }
        }
        return createMesh();
    }

}
