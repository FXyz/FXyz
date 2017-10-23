/**
 * SegmentedDomeMesh.java
 *
 * Copyright (c) 2013-2017, F(X)yz
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

package org.fxyz3d.shapes.primitives;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;
import org.fxyz3d.geometry.Face3;

/**
 *
 * @author Sean
 */
public class SegmentedDomeMesh extends TexturedMesh {
    /*
        Field vars
    */
    private static final double DEFAULT_RADIUS = 50.0D;
    private static final double DEFAULT_PHIMIN = Math.toRadians(0);
    private static final double DEFAULT_PHIMAX = Math.toRadians(360);
    private static final double DEFAULT_THETAMIN = Math.toRadians(0);
    private static final double DEFAULT_THETAMAX = Math.toRadians(90);
    private static final int DEFAULT_DIVISIONS = 20;    
    
    public SegmentedDomeMesh() {
        this(DEFAULT_RADIUS, DEFAULT_PHIMIN, DEFAULT_PHIMAX, 
            DEFAULT_THETAMIN, DEFAULT_THETAMAX, DEFAULT_DIVISIONS);
    }
    
    /**
     * @param radius radius of the sphere segment
     * @param phimin The starting azimutal angle [rad], 0-2*pi.
     * @param phimax The ending azimutal angle [rad], 0-2*pi, phimax &gt;
     * phimin.
     * @param thetamin The starting polar angle [rad], -pi/2-pi/2.
     * @param thetamax The ending polar angle [rad], -pi/2-pi/2, thetamax &gt;
     * thetamin.
     * @param divisions The number of segments of curves approximations,
     * granulariy &gt; 2.
     */
    public SegmentedDomeMesh(double radius,
            double phimin, double phimax, 
            double thetamin, double thetamax,
            int divisions) {

        setRadius(radius);
        setPhimin(phimin);
        setPhimax(phimax);
        setThetamin(thetamin);
        setThetamax(thetamax);
        setDivisions(divisions);
        updateMesh();
        setDepthTest(DepthTest.ENABLE);
        
    }
    private TriangleMesh createSegmentedDome(double radius,
            double phimin, double phimax, 
            double thetamin, double thetamax,
            int divisions){
        
        listVertices.clear();
        listTextures.clear();
        listFaces.clear();   

        //Is the areaMesh simply the rectangle representing the area of the 
        //texture size if the 3D object were flattened?
//        int subDivX=subDivY;
//        areaMesh.setWidth((1-2*cropX/subDivX)*2d*Math.PI*radius);
//        areaMesh.setHeight((1-2*cropY/subDivY)*2d*Math.PI*radius);

    //I don't think I need this transform for this algorithm
//        a = new Affine();
//        if(center.get()!=null){            
//            a=a.createConcatenation(new Translate(center.get().x,center.get().y,center.get().z));
//        }

        // Create Points
        double phi = phimin;
        double theta;

        for (int i = 0; i < divisions + 1; i++) {
            theta = thetamin;
            for (int j = 0; j < divisions + 1; j++) {
               org.fxyz3d.geometry.Point3D ta = new org.fxyz3d.geometry.Point3D(
                        (float) (radius * Math.cos(theta) * Math.sin(phi)),
                        (float) (radius * Math.cos(theta) * Math.cos(phi)),
                        (float) (radius * Math.sin(theta)));
                listVertices.add(ta);                
                theta += (thetamax - thetamin) / divisions;
            }
            phi += (phimax - phimin) / divisions;
        }        
        
        // Create texture coordinates
        createTexCoords(2*divisions,2*divisions);

        //Add the faces "winding" the points generally counter clock wise
        for (int i = 0; i < divisions; i++) {
            int multiplier = (i * divisions) + i;
            //Up the Outside
            for (int j = multiplier; j < divisions + multiplier; j++) {
                listFaces.add(new Face3(j, j + 1, j + divisions + 1)); //lower triangle
                listTextures.add(new Face3(j, j + 1, j + divisions + 1));
                listFaces.add(new Face3(j + divisions + 1, j + 1, j + divisions + 2)); //upper triangle
                listTextures.add(new Face3(j + divisions + 1, j + 1, j + divisions + 2));
            }
            //Down the Inside            
            for (int j = divisions + multiplier; j > multiplier; j--) {
                listFaces.add(new Face3(j, j - 1, j + divisions + 1)); ; //lower triangle
                listTextures.add(new Face3(j, j - 1, j + divisions + 1));
                listFaces.add(new Face3(j - 1, j + divisions, j + divisions + 1)); //upper triangle
                listTextures.add(new Face3(j - 1, j + divisions, j + divisions + 1));
            }
        }    
        
        return createMesh();
    }
    private TriangleMesh createSphereSegment(double radius,
            double phimin, double phimax, 
            double thetamin, double thetamax,
            int granularity) {
 

        mesh = new TriangleMesh();
        // Fill Points
        double phi = phimin;
        double theta;

        for (int i = 0; i < granularity + 1; i++) {
            theta = thetamin;
            for (int j = 0; j < granularity + 1; j++) {
                Point3D p3D = new Point3D((float) (radius * Math.cos(theta) * Math.sin(phi)),
                        (float) (radius * Math.cos(theta) * Math.cos(phi)),
                        (float) (radius * Math.sin(theta)));
                mesh.getPoints().addAll(new Float(p3D.getX()), new Float(p3D.getY()), new Float(p3D.getZ()));
                theta += (thetamax - thetamin) / granularity;
            }
            phi += (phimax - phimin) / granularity;
        }

        //for now we'll just make an empty texCoordinate group
        mesh.getTexCoords().addAll(0, 0);
        //Add the faces "winding" the points generally counter clock wise
        for (int i = 0; i < granularity; i++) {
            int multiplier = (i * granularity) + i;
            //Up the Outside
            for (int j = multiplier; j < granularity + multiplier; j++) {
                mesh.getFaces().addAll(j, 0, j + 1, 0, j + granularity + 1, 0); //lower triangle
                mesh.getFaces().addAll(j + granularity + 1, 0, j + 1, 0, j + granularity + 2, 0); //upper triangle
            }
            //Down the Inside            
            for (int j = granularity + multiplier; j > multiplier; j--) {
                mesh.getFaces().addAll(j, 0, j - 1, 0, j + granularity + 1, 0); //lower triangle
                mesh.getFaces().addAll(j - 1, 0, j + granularity, 0, j + granularity + 1, 0); //upper triangle
            }
        }
        return mesh;
    }
    /*
        Properties
    */
    private final DoubleProperty radius = new SimpleDoubleProperty(DEFAULT_RADIUS){
        @Override
        protected void invalidated() {
            updateMesh();
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
    
    private final DoubleProperty phimin = new SimpleDoubleProperty(DEFAULT_PHIMIN){
        @Override
        protected void invalidated() {
            updateMesh();
        }
    };
    public final double getPhimin() {
        return phimin.get();
    }
    public final void setPhimin(double value) {
        phimin.set(value);
    }
    public DoubleProperty phiminProperty() {
        return phimin;
    }

    private final DoubleProperty phimax = new SimpleDoubleProperty(DEFAULT_PHIMAX){
        @Override
        protected void invalidated() {
            updateMesh();
        }
    };
    public final double getPhimax() {
        return phimax.get();
    }
    public final void setPhimax(double value) {
        phimax.set(value);
    }
    public DoubleProperty phimaxProperty() {
        return phimax;
    }
    
    private final DoubleProperty thetamax = new SimpleDoubleProperty(DEFAULT_THETAMAX){
        @Override
        protected void invalidated() {
            updateMesh();
        }
    };
    public final double getThetamax() {
        return thetamax.get();
    }
    public final void setThetamax(double value) {
        thetamax.set(value);
    }
    public DoubleProperty thetamaxProperty() {
        return thetamax;
    }
    
    private final DoubleProperty thetamin = new SimpleDoubleProperty(DEFAULT_THETAMIN){
        @Override
        protected void invalidated() {
            updateMesh();
        }
    };
    public final double getThetamin() {
        return thetamin.get();
    }
    public final void setThetamin(double value) {
        thetamin.set(value);
    }
    public DoubleProperty thetaminProperty() {
        return thetamin;
    }    
    
    private final IntegerProperty divisions = new SimpleIntegerProperty(DEFAULT_DIVISIONS){
        @Override
        protected void invalidated() {
            updateMesh();
        }        
    };    
    public final int getDivisions() {
        return divisions.get();
    }
    public final void setDivisions(int value) {
        divisions.set(value);
    }
    public IntegerProperty divisionsProperty() {
        return divisions;
    }        
    
    @Override
    protected final void updateMesh() {
        setMesh(null);
//        mesh = createSphereSegment(getRadius(), getPhimin(), getPhimax(), getThetamin(), getThetamax(), getDivisions());
        mesh = createSegmentedDome(getRadius(), getPhimin(), getPhimax(), getThetamin(), getThetamax(), getDivisions());

        setMesh(mesh);
    }

}
