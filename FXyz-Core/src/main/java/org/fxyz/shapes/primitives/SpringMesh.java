/**
 * SpringMesh.java
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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Face3;
import org.fxyz.shapes.primitives.helper.SpringHelper;

/**
 *  Spring based on this model:  http://math.stackexchange.com/a/461637
 */
public class SpringMesh extends TexturedMesh {

    private static final double DEFAULT_MEAN_RADIUS = 10.0D;
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
    
    private SpringHelper spring;
    
    public SpringMesh() {
        this(DEFAULT_MEAN_RADIUS, DEFAULT_WIRE_RADIUS, DEFAULT_PITCH, DEFAULT_LENGTH,
             DEFAULT_LENGTH_DIVISIONS, DEFAULT_WIRE_DIVISIONS, DEFAULT_LENGTH_CROP, DEFAULT_WIRE_CROP);
    }

    public SpringMesh(double meanRadius, double wireRadius, double pitch, double length) {
        this(meanRadius, wireRadius, pitch, length, 
             DEFAULT_LENGTH_DIVISIONS, DEFAULT_WIRE_DIVISIONS, DEFAULT_LENGTH_CROP, DEFAULT_WIRE_CROP);
    }

    double factor=1d;
    
    public SpringMesh(double meanRadius, double wireRadius, double pitch, double length, 
                      int rDivs, int tDivs, int lengthCrop, int wireCrop) {
        
        setMeanRadius(meanRadius);
        setWireRadius(wireRadius);
        setPitch(pitch);
        setLength(length);
        factor=2*Math.PI;
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
        mesh=createSpring((float) getMeanRadius(), (float) getWireRadius(), (float) getPitch(), (float) getLength(),
            getLengthDivisions(), getWireDivisions(), getLengthCrop(), getWireCrop(),
            (float) getTubeStartAngleOffset(), (float)getxOffset(),(float)getyOffset(), (float)getzOffset());
        setMesh(mesh);
        
    }
    
    private final DoubleProperty meanRadius = new SimpleDoubleProperty(DEFAULT_MEAN_RADIUS){

        @Override protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public final double getMeanRadius() {
        return meanRadius.get();
    }

    public final void setMeanRadius(double value) {
        meanRadius.set(value);
    }

    public DoubleProperty meanRadiusProperty() {
        return meanRadius;
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
//                setPitch(length.get()/factor);
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
//                setLength(pitch.get()*factor);
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
    
    private TriangleMesh createSpring(float meanRadius, float wireRadius, float pitch, float length, 
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
        
        spring = new SpringHelper(meanRadius, pitch);
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
