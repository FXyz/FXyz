/**
 * CapsuleMesh.java
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
import javafx.scene.DepthTest;
import javafx.scene.shape.TriangleMesh;
import org.fxyz3d.geometry.Face3;
import org.fxyz3d.geometry.Point3D;

/**
 *
 * @author Dub
 */
public class CapsuleMesh extends TexturedMesh{
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
        setDivisions(divisions);
        setRadius(radius);
        setHeight(height);
        updateMesh();
        setDepthTest(DepthTest.ENABLE);
    }

    /*
    Methods
     */
    private static int correctDivisions(int div) {
        return ((div + 3) / 4) * 4;
    }
    
    public TriangleMesh createCapsule(int sphereDivisions, float radius, float height) {
        listVertices.clear();
        listTextures.clear();
        listFaces.clear();  
        
        sphereDivisions = correctDivisions(sphereDivisions);
        //Add the primary end point
        Point3D capPoint1 = new Point3D(
            (float) 0,
            (float) -radius,
            (float) 0);
        listVertices.add(capPoint1);                
        
        final int halfDivisions = sphereDivisions / 2;
        final float fDivisions = 1.f / sphereDivisions;
        //create vertex points 
        for (int i = 0; i < halfDivisions; ++i) {
            float va = fDivisions * (i + 1 - halfDivisions / 2) * 2 * (float) Math.PI;
            float hdY = (float) Math.sin(va);
            float hdX = (float) Math.cos(va);
            //inner loop wraps around circumference of capsule
            for (int point = 0; point < sphereDivisions+1; ++point) {
                double localTheta = fDivisions * point * 2 * (float) Math.PI;
                float ly = (float) Math.sin(localTheta);
                float lx = (float) Math.cos(localTheta);
                //how far around the circumference are we? Are we rising or falling? 
                if(i >= (halfDivisions - 1) / 2){
                    Point3D ta = new Point3D(
                        (float) (ly * hdX * radius), //X
                        (float) (hdY * radius + height), //Y
                        (float) (lx * hdX * radius)); //Z
                    listVertices.add(ta);                
                } else {
                    Point3D ta = new Point3D(
                        (float) (ly * hdX * radius), //X
                        (float) (hdY * radius), //Y
                        (float) (lx * hdX * radius)); //Z
                    listVertices.add(ta);                
                }
            }
        }
        //add the final end point
        Point3D capPoint2 = new Point3D(
            (float) 0,
            (float) radius + height,
            (float) 0);
        listVertices.add(capPoint2);                
        // Create texture coordinates
        createTexCoords(sphereDivisions, sphereDivisions);
        //Wind the top end cap as a triangle fan
        for(int topCapIndex = 0; topCapIndex < sphereDivisions; ++topCapIndex){
            listFaces.add(new Face3(0, topCapIndex + 2, topCapIndex + 1)); //triangle
            listTextures.add(new Face3(0, topCapIndex + 2, topCapIndex + 1));
        }

        //Proceed to wind the capsule using triangle quad strips
        for (int i = 0; i < halfDivisions - 2; i++) {
            //calculate our "starting" index for the sub loop
            int startIndex = (i * sphereDivisions) + i;  //gotta add our index to account for the widening gap
            if(i==0) //cannot start at 0 because that is the "top cap point"
                startIndex++;
            int finishIndex = sphereDivisions + startIndex + i; //calculate our "finishing" index for the sub loop
            //wrap around the capsule from the "starting" index to the "finishing" index
            for (int j = startIndex; j < finishIndex; j++) {
                listFaces.add(new Face3(j, j + 1, j + sphereDivisions + 1)); //lower triangle
                listTextures.add(new Face3(j, j + 1, j + sphereDivisions + 1));
                listFaces.add(new Face3(j + sphereDivisions + 1, j + 1, j + sphereDivisions + 2)); //upper triangle
                listTextures.add(new Face3(j + sphereDivisions + 1, j + 1, j + sphereDivisions + 2));
            }
        }    
        //Wind the bottom end cap as a triangle fan
        int finalPoint = listVertices.size() - 1;
        for(int bottomCapIndex = finalPoint; bottomCapIndex >= finalPoint - sphereDivisions; bottomCapIndex--){
            listFaces.add(new Face3(finalPoint, bottomCapIndex - sphereDivisions - 2 , bottomCapIndex - sphereDivisions - 1)); //triangle
            listTextures.add(new Face3(finalPoint, bottomCapIndex - sphereDivisions - 2, bottomCapIndex - sphereDivisions - 1));
        }
        return createMesh();
    }


    /*
        Properties
    */
    
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
    
    private final DoubleProperty height = new SimpleDoubleProperty(DEFAULT_HEIGHT){
        @Override
        protected void invalidated() {
            updateMesh();
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

    @Override
    protected final void updateMesh() {
        setMesh(null);
        mesh = createCapsule(getDivisions(),(float)getRadius(), (float)getHeight());
        setMesh(mesh);
    }
}