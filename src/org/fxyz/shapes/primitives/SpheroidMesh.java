/*
 * Copyright (C) 2013-2014 F(X)yz, 
 * Sean Phillips, Jason Pollastrini
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

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
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
    
    private static final float DEFAULT_MAJOR_RADIUS = 50f;
    private static final float DEFAULT_MINOR_RADIUS = 12f;
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
    public SpheroidMesh(float radius) {
        this();
        setMajorRadius(radius);
        setMinorRadius(radius);
    }
    /**
     * 
     * @param majRad The major(horizontal) radius
     * @param minRad The minor(vertical) radius
     */
    public SpheroidMesh(float majRad, float minRad) {
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
    public SpheroidMesh(int divs, float majRad, float minRad) {
        this();
        setDivisions(divs);
        setMajorRadius(majRad);
        setMinorRadius(minRad);
    }
    
    public boolean isSphere(){
        return getMajorRadius() == getMinorRadius();
    }
    public boolean isOblateSpheroid(){
        return getMajorRadius() > getMinorRadius();
    }
    public boolean isProlateSpheroid(){
        return getMajorRadius() < getMinorRadius();
    }
    
    private TriangleMesh createSpheroid(int divs, float major, float minor) {
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
                
                points[pPos + 0] = cos_v * cos_u * major; // x
                points[pPos + 2] = cos_v * sin_u * major; // z
                points[pPos + 1] = sin_v * minor;        // y up 
                
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
        points[pPos + 1] = -minor;
        points[pPos + 2] = 0;
        points[pPos + 3] = 0;
        points[pPos + 4] = minor;
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
    private final FloatProperty majorRadius = new SimpleFloatProperty(DEFAULT_MAJOR_RADIUS){

        @Override
        protected void invalidated() {
            setMesh(createSpheroid(getDivisions(), getMajorRadius(), getMinorRadius()));
        }
        
    };

    public final float getMajorRadius() {
        return majorRadius.get();
    }

    public final void setMajorRadius(float value) {
        majorRadius.set(value);
    }

    public FloatProperty majorRadiusProperty() {
        return majorRadius;
    }
    private final FloatProperty minorRadius = new SimpleFloatProperty(DEFAULT_MINOR_RADIUS){

        @Override
        protected void invalidated() {
            setMesh(createSpheroid(getDivisions(), getMajorRadius(), getMinorRadius()));
        }
        
    };

    public final float getMinorRadius() {
        return minorRadius.get();
    }

    public final void setMinorRadius(float value) {
        minorRadius.set(value);
    }

    public FloatProperty minorRadiusProperty() {
        return minorRadius;
    }
    private final IntegerProperty divisions = new SimpleIntegerProperty(DEFAULT_DIVISIONS){

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
