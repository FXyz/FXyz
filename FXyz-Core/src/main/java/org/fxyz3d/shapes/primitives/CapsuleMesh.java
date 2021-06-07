/**
 * CapsuleMesh.java
 *
 * Copyright (c) 2013-2021, F(X)yz
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

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 *
 * @author Dub, JPereda, SPhillips
 */
public class CapsuleMesh extends TexturedMesh {

    private static final int DEFAULT_DIVISIONS = 64;
    private static final double DEFAULT_RADIUS = 2.0D;
    private static final double DEFAULT_HEIGHT = 10.0D;

    public CapsuleMesh() {
        this(DEFAULT_RADIUS, DEFAULT_HEIGHT);
    }
    
    public CapsuleMesh(double radius, double height) {
        this(DEFAULT_DIVISIONS, radius, height);
    }
    
    public CapsuleMesh(int divisions, double radius, double height) {    
        setDivisions(divisions);
        setRadius(radius);
        setHeight(height);

        setDepthTest(DepthTest.ENABLE);
        updateMesh();
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

    public final IntegerProperty divisionsProperty() {
        return divisions;
    }        
    
    private final DoubleProperty radius = new SimpleDoubleProperty(DEFAULT_RADIUS) {
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

    public final DoubleProperty radiusProperty() {
        return radius;
    }
    
    private final DoubleProperty height = new SimpleDoubleProperty(DEFAULT_HEIGHT) {
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

    public final DoubleProperty heightProperty() {
        return height;
    }

    @Override
    protected final void updateMesh() {
        setMesh(null);
        mesh = createCapsule(getDivisions(), (float)getRadius(), (float)getHeight());
        setMesh(mesh);
    }

    private TriangleMesh createCapsule(int sphereDivisions, float radius, float height) {
        listVertices.clear();
        listTextures.clear();
        listFaces.clear();

        sphereDivisions = correctDivisions(sphereDivisions);

        //Add the primary end point
        listVertices.add(new Point3D(0, -radius - height / 2, 0));

        final float fDivisions = 1.f / sphereDivisions;

        //create vertex points
        for (int i = 0; i < 3 * sphereDivisions / 4; i++) {
            float hdY = 0;
            float hdX = 0;
            float hy = 0;

            if (i < sphereDivisions / 4) {
                float va = (i + 1 - sphereDivisions / 4) / (sphereDivisions / 4f) * (float) Math.PI / 2f;
                hdY = (float) sin(va) * radius;
                hdX = (float) cos(va) * radius;
                hy = - height / 2;

            } else if (i < sphereDivisions / 2) {
                hdY = - height / 2f + (i + 1 - sphereDivisions / 4f) / (sphereDivisions / 4f + 1) * height;
                hdX = radius;
                hy = 0;

            } else {
                float va = (i - sphereDivisions / 2) / (sphereDivisions / 4f) * (float) Math.PI / 2f;
                hdY = (float) sin(va) * radius;
                hdX = (float) cos(va) * radius;
                hy = height / 2;
            }

            //inner loop wraps around circumference of capsule
            for (int point = 0; point < sphereDivisions; point++) {
                double localTheta = fDivisions * point * 2f * (float) Math.PI;
                float lx = (float) sin(localTheta);
                float lz = (float) cos(localTheta);

                //how far around the circumference are we? Are we rising or falling?
                listVertices.add(new Point3D(lx * hdX, hdY + hy, lz * hdX));
            }
        }

        //add the final end point
        listVertices.add(new Point3D(0, radius + height / 2, 0));

        // Create texture coordinates
        createTexCoords(sphereDivisions, 3 * sphereDivisions / 4);

        //Wind the top end cap as a triangle fan
        for (int topCapIndex = 0; topCapIndex < sphereDivisions; topCapIndex++) {
            int next = topCapIndex == sphereDivisions - 1 ? 1 : topCapIndex + 2;

            listFaces.add(new Face3(0, next, topCapIndex + 1)); //triangle
            listTextures.add(new Face3(0, next, topCapIndex + 1));
        }

        //Proceed to wind the capsule using triangle quad strips
        for (int i = 0; i < 3 * sphereDivisions / 4 - 1; i++) {
            //calculate our "starting" index for the sub loop
            int startIndex = (i * sphereDivisions) + 1;  //gotta add our index to account for the widening gap
            int finishIndex = sphereDivisions + startIndex; //calculate our "finishing" index for the sub loop

            //wrap around the capsule from the "starting" index to the "finishing" index
            for (int j = startIndex; j < finishIndex; j++) {
                int next = j == finishIndex - 1 ? startIndex : j + 1;

                listFaces.add(new Face3(j, next, next + sphereDivisions)); //lower triangle
                listTextures.add(new Face3(j, next, next + sphereDivisions));
                listFaces.add(new Face3(next + sphereDivisions, j + sphereDivisions, j)); //upper triangle
                listTextures.add(new Face3(next + sphereDivisions, j + sphereDivisions, j));
            }
        }

        //Wind the bottom end cap as a triangle fan
        int finalPoint = listVertices.size() - 1;
        for(int bottomCapIndex = finalPoint - sphereDivisions; bottomCapIndex < finalPoint; bottomCapIndex++){
            int next = bottomCapIndex == finalPoint - 1 ? finalPoint - sphereDivisions : bottomCapIndex + 1;

            listFaces.add(new Face3(finalPoint, bottomCapIndex, next)); //triangle
            listTextures.add(new Face3(finalPoint, bottomCapIndex, next));
        }

        return createMesh();
    }

    private static int correctDivisions(int div) {
        return ((div + 3) / 4) * 4;
    }
}