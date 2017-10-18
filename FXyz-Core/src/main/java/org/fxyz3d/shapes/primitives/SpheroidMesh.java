/**
 * SpheroidMesh.java
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

package org.fxyz3d.shapes.primitives;

import java.util.Objects;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Dub
 */
public class SpheroidMesh extends MeshView{
    
    private static final double DEFAULT_MAJOR_RADIUS = 50f;
    private static final double DEFAULT_MINOR_RADIUS = 12f;
    private static final int   DEFAULT_DIVISIONS = 64;

    public SpheroidMesh() {
        setMesh(createSpheroid(getDivisions(), getMajorRadius(), getMinorRadius()));
        setCullFace(CullFace.BACK);
        setDepthTest(DepthTest.ENABLE);
        
    }   

    /**
     * 
     * @param radius Creates a Sphere with the specified Radius
     */
    public SpheroidMesh(double radius) {
        this();
        setMajorRadius(radius);
        setMinorRadius(radius);
    }
    /**
     * 
     * @param majRad The major(horizontal) radius
     * @param minRad The minor(vertical) radius
     */
    public SpheroidMesh(double majRad, double minRad) {
        this();
        setMajorRadius(majRad);
        setMinorRadius(minRad);
    }
    
    /**
     * 
     * @param divs  Divisions for the Spheroid. Default is 64
     * @param majRad The major(horizontal) radius
     * @param minRad The minor(vertical) radius
     */
    public SpheroidMesh(int divs, double majRad, double minRad) {
        this();
        setDivisions(divs);
        setMajorRadius(majRad);
        setMinorRadius(minRad);
    }
    
    public boolean isSphere(){
        return Objects.equals(getMajorRadius(), getMinorRadius());
    }
    public boolean isOblateSpheroid(){
        return getMajorRadius() > getMinorRadius();
    }
    public boolean isProlateSpheroid(){
        return getMajorRadius() < getMinorRadius();
    }
    
    private TriangleMesh createSpheroid(int divs, double major, double minor) {
        divs = correctDivisions(divs);
        TriangleMesh m = new TriangleMesh();
        
        final int divsHalf = divs / 2;

        final int numPoints = divs * (divsHalf - 1) + 2;
        final int numTexCoords = (divs + 1) * (divsHalf - 1) + divs * 2;
        final int numFaces = divs * (divsHalf - 2) * 2 + divs * 2;

        final float divf = 1.f / divs;

        float points[] = new float[numPoints * m.getPointElementSize()];
        float tPoints[] = new float[numTexCoords * m.getTexCoordElementSize()];
        int faces[] = new int[numFaces * m.getFaceElementSize()];

        int pPos = 0, tPos = 0;

        for (int lat = 0; lat < divsHalf-1 ; ++lat) {
            
            float latRad = divf * (lat + 1 - divsHalf / 2) * 2 * (float) Math.PI;
            float sin_v = (float) Math.sin(latRad);
            float cos_v = (float) Math.cos(latRad);

            float ty = 0.5f + sin_v * 0.5f;
            
            for (int lon = 0; lon < divs; ++lon) {
                
                double lonRad = divf * lon * 2 * (float) Math.PI;
                float sin_u = (float) Math.sin(lonRad);
                float cos_u = (float) Math.cos(lonRad);
                
                points[pPos + 0] = (float) (cos_v * cos_u * major); // x
                points[pPos + 2] = (float) (cos_v * sin_u * major); // z
                points[pPos + 1] = (float) (sin_v * minor);        // y up 
                
                tPoints[tPos + 0] = 1 - divf * lon;
                tPoints[tPos + 1] = ty;
                pPos += 3;
                tPos += 2;
            }
            tPoints[tPos + 0] = 0;
            tPoints[tPos + 1] = ty;
            tPos += 2;
        }

        points[pPos + 0] = 0;
        points[pPos + 1] = (float) -minor;
        points[pPos + 2] = 0;
        points[pPos + 3] = 0;
        points[pPos + 4] = (float) minor;
        points[pPos + 5] = 0;
        pPos += 6;

        int pS = (divsHalf - 1) * divs;

        float textureDelta = 1.f / 256;
        for (int i = 0; i < divs; ++i) {
            tPoints[tPos + 0] = divf * (0.5f + i);
            tPoints[tPos + 1] = textureDelta;
            tPos += 2;
        }

        for (int i = 0; i < divs; ++i) {
            tPoints[tPos + 0] = divf * (0.5f + i);
            tPoints[tPos + 1] = 1 - textureDelta;
            tPos += 2;
        }

        int fIndex = 0;
        for (int y = 0; y < divsHalf - 2; ++y) {
            for (int x = 0; x < divs; ++x) {
                int p0 = y * divs + x;
                int p1 = p0 + 1;
                int p2 = p0 + divs;
                int p3 = p1 + divs;

                int t0 = p0 + y;
                int t1 = t0 + 1;
                int t2 = t0 + (divs + 1);
                int t3 = t1 + (divs + 1);

                // add p0, p1, p2
                faces[fIndex + 0] = p0;
                faces[fIndex + 1] = t0;
                faces[fIndex + 2] = p1 % divs == 0 ? p1 - divs : p1;
                faces[fIndex + 3] = t1;
                faces[fIndex + 4] = p2;
                faces[fIndex + 5] = t2;
                fIndex += 6;

                // add p3, p2, p1
                faces[fIndex + 0] = p3 % divs == 0 ? p3 - divs : p3;
                faces[fIndex + 1] = t3;
                faces[fIndex + 2] = p2;
                faces[fIndex + 3] = t2;
                faces[fIndex + 4] = p1 % divs == 0 ? p1 - divs : p1;
                faces[fIndex + 5] = t1;
                fIndex += 6;
            }
        }

        int p0 = pS;
        int tB = (divsHalf - 1) * (divs + 1);
        for (int x = 0; x < divs; ++x) {
            int p2 = x, p1 = x + 1, t0 = tB + x;
            faces[fIndex + 0] = p0;
            faces[fIndex + 1] = t0;
            faces[fIndex + 2] = p1 == divs ? 0 : p1;
            faces[fIndex + 3] = p1;
            faces[fIndex + 4] = p2;
            faces[fIndex + 5] = p2;
            fIndex += 6;
        }

        p0 = p0 + 1;
        tB = tB + divs;
        int pB = (divsHalf - 2) * divs;

        for (int x = 0; x < divs; ++x) {
            int p1 = pB + x, p2 = pB + x + 1, t0 = tB + x;
            int t1 = (divsHalf - 2) * (divs + 1) + x, t2 = t1 + 1;
            faces[fIndex + 0] = p0;
            faces[fIndex + 1] = t0;
            faces[fIndex + 2] = p1;
            faces[fIndex + 3] = t1;
            faces[fIndex + 4] = p2 % divs == 0 ? p2 - divs : p2;
            faces[fIndex + 5] = t2;
            fIndex += 6;
        }
        
        m.getPoints().addAll(points);
        m.getTexCoords().addAll(tPoints);
        m.getFaces().addAll(faces);
        
        return m;
    }
    
    private int correctDivisions(int div) {
        return ((div + 3) / 4) * 4;
    }
    /*
    
    */
    private final DoubleProperty majorRadius = new SimpleDoubleProperty(this, "majorRadius", DEFAULT_MAJOR_RADIUS){

        @Override
        protected void invalidated() {
            setMesh(createSpheroid(getDivisions(), getMajorRadius(), getMinorRadius()));
        }
        
    };

    public final Double getMajorRadius() {
        return majorRadius.get();
    }

    public final void setMajorRadius(Double value) {
        majorRadius.set(value);
    }

    public DoubleProperty majorRadiusProperty() {
        return majorRadius;
    }
    
    
    private final DoubleProperty minorRadius = new SimpleDoubleProperty(this, "minorRadius", DEFAULT_MINOR_RADIUS){

        @Override
        protected void invalidated() {
            setMesh(createSpheroid(getDivisions(), getMajorRadius(), getMinorRadius()));
        }
        
    };

    public final Double getMinorRadius() {
        return minorRadius.get();
    }

    public final void setMinorRadius(double value) {
        minorRadius.set(value);
    }

    public DoubleProperty minorRadiusProperty() {
        return minorRadius;
    }
    
    
    private final IntegerProperty divisions = new SimpleIntegerProperty(this, "divisions", DEFAULT_DIVISIONS){

        @Override
        protected void invalidated() {
            setMesh(createSpheroid(getDivisions(), getMajorRadius(), getMinorRadius()));
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
    
    
}
