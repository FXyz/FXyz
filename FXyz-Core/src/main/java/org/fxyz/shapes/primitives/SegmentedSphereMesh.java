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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.fxyz.geometry.Face3;
import org.fxyz.geometry.Point3D;

/**
 * SegmentedTorusMesh is based in TorusMesh, but allows cutting the torus in two 
 * directions, in order to have a banner parallel to an uncut torus.
 * Based on a regular 2D TriangleMesh, mapped to a 3D mesh with the torus parametric equations
 * Crop allows cutting/cropping the 2D mesh on the borders
 * If crop ==0  then  a regular torus is formed (thought with slight differences from 
 * TorusMesh)
 */
public class SegmentedSphereMesh extends TexturedMesh {

    private static final int DEFAULT_DIVISIONS = 64;
    private static final int DEFAULT_CROP_Y = 0;
    private static final int DEFAULT_CROP_X = 0;
    private static final double DEFAULT_RADIUS = 5.0D;
    private static final double DEFAULT_START_ANGLE = 0.0D;
    private static final double DEFAULT_X_OFFSET = 0.0D;
    private static final double DEFAULT_Y_OFFSET = 0.0D;
    private static final double DEFAULT_Z_OFFSET = 1.0D;
    private final static Point3D DEFAULT_CENTER = new Point3D(0f,0f,0f);

    public SegmentedSphereMesh() {
        this(DEFAULT_DIVISIONS, DEFAULT_CROP_X, DEFAULT_CROP_Y, DEFAULT_RADIUS,null);
    }

    public SegmentedSphereMesh(double radius) {
        this(DEFAULT_DIVISIONS, DEFAULT_CROP_X, DEFAULT_CROP_Y, radius,null);
    }

    public SegmentedSphereMesh(int tDivs, int cropX, int cropY, double radius, Point3D center) {
        setRadiusDivisions(tDivs);
        setRadiusCropX(cropX);
        setRadiusCropY(cropY);
        setRadius(radius);
        setzOffset(1);
        setCenter(center);
        
        updateMesh();
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
    }

    @Override
    protected final void updateMesh(){       
        setMesh(null);
        mesh=createSegmentedSphere(
            getRadiusDivisions(), 
            getRadiusCropX(),
            getRadiusCropY(),
            (float) getRadius(), 
            (float) getTubeStartAngleOffset(), 
            (float)getxOffset(),
            (float)getyOffset(), 
            (float)getzOffset());
        setMesh(mesh);
    }
    
    private final IntegerProperty radiusDivisions = new SimpleIntegerProperty(DEFAULT_DIVISIONS) {

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };

    public final int getRadiusDivisions() {
        return radiusDivisions.get();
    }

    public final void setRadiusDivisions(int value) {
        radiusDivisions.set(value);
    }

    public IntegerProperty radiusDivisionsProperty() {
        return radiusDivisions;
    }

    private final IntegerProperty radiusCropX = new SimpleIntegerProperty(DEFAULT_CROP_X) {

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };
    public final int getRadiusCropX() {
        return radiusCropX.get();
    }

    public final void setRadiusCropX(int value) {
        radiusCropX.set(value);
    }

    public IntegerProperty radiusCropXProperty() {
        return radiusCropX;
    }

    private final IntegerProperty radiusCropY = new SimpleIntegerProperty(DEFAULT_CROP_Y) {

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }

    };
    public final int getRadiusCropY() {
        return radiusCropY.get();
    }

    public final void setRadiusCropY(int value) {
        radiusCropY.set(value);
    }

    public IntegerProperty radiusCropYProperty() {
        return radiusCropY;
    }

    private final DoubleProperty radius = new SimpleDoubleProperty(DEFAULT_RADIUS) {

        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
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

    public final void setzOffset(double value) {
        zOffset.set(value);
    }

    public DoubleProperty zOffsetProperty() {
        return zOffset;
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
    
    private Transform a = new Affine();
    
    private TriangleMesh createSegmentedSphere(int subDivY, int cropX, int cropY,
            float radius, float tubeStartAngle, float xOffset, float yOffset, float zOffset) {
 
        listVertices.clear();
        listTextures.clear();
        listFaces.clear();
        
        int subDivX=subDivY;
        
        int numDivX = subDivX + 1-2*cropX;
        float pointX, pointY, pointZ;
        
        areaMesh.setWidth((1-2*cropX/subDivX)*2d*Math.PI*radius);
        areaMesh.setHeight((1-2*cropY/subDivY)*2d*Math.PI*radius);
        a = new Affine();
        if(center.get()!=null){            
            a=a.createConcatenation(new Translate(center.get().x,center.get().y,center.get().z));
        }
        // Create points
        for (int y = cropY; y <= subDivY-cropY; y++) {
            float dy = (float) y / subDivY;
            for (int x = cropX; x <= subDivX-cropX; x++) {
                float dx = (float) x / subDivX;
                if(cropX>0 || (cropX==0 && x<subDivX)){
                    pointX = (float) ((radius*Math.sin((-1d+dy)*Math.PI))*(Math.cos((-1d+2d*dx)*Math.PI)+ xOffset));
                    pointZ = (float) ((radius*Math.sin((-1d+dy)*Math.PI))*(Math.sin((-1d+2d*dx)*Math.PI)+ yOffset));
                    pointY = (float) (radius*Math.cos((-1d+dy)*Math.PI)*zOffset);
                    Point3D ta = transform(pointX, pointY, pointZ);
                    listVertices.add(ta);
                }
            }
        }
        // Create texture coordinates
//        if(exterior.get()){
            createTexCoords(subDivX-2*cropX,subDivY-2*cropY);
//        } else {
//            createReverseTexCoords(subDivX-2*crop,subDivY-2*crop);
//        }
        
        // Create textures indices
        for (int y = cropY; y < subDivY-cropY; y++) {
            for (int x = cropX; x < subDivX-cropX; x++) {
                int p00 = (y-cropY) * numDivX + (x-cropX);
                int p01 = p00 + 1;
                int p10 = p00 + numDivX;
                int p11 = p10 + 1;
                if(y<subDivY-1){
                    listTextures.add(new Face3(p00,p10,p11));   
                }             
                if(y>0){
                    listTextures.add(new Face3(p11,p01,p00));
                }
            }
        }
        // Create faces indices
        for (int y = cropY; y < subDivY-cropY; y++) {
            for (int x = cropX; x < subDivX-cropX; x++) {
                int p00 = (y-cropY) * ((cropX>0)?numDivX:numDivX-1) + (x-cropX);
                int p01 = p00 + 1;
                if(cropX==0 && x==subDivX-1){
                    p01-=subDivX;
                }
                int p10 = p00 + ((cropX>0)?numDivX:numDivX-1);
//                if(cropY==0 && y==subDivY-1){
//                    p10-=subDivY*((cropX>0)?numDivX:numDivX-1);
//                }
                int p11 = p10 + 1;
                if(cropX==0 && x==subDivX-1){
                    p11-=subDivX;
                }                
                if(y<subDivY-1){
                    listFaces.add(new Face3(p00,p10,p11));   
                }
                if(y>0){
                    listFaces.add(new Face3(p11,p01,p00));
                }
            }
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
}
