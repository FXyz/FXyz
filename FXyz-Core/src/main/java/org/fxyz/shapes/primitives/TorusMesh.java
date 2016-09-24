/**
 * TorusMesh.java
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
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author jDub1581
 */
public class TorusMesh extends MeshView {

    private static final int DEFAULT_DIVISIONS = 64;
    private static final int DEFAULT_T_DIVISIONS = 64;
    private static final double DEFAULT_RADIUS = 12.5D;
    private static final double DEFAULT_T_RADIUS = 5.0D;
    private static final double DEFAULT_START_ANGLE = 0.0D;
    private static final double DEFAULT_X_OFFSET = 0.0D;
    private static final double DEFAULT_Y_OFFSET = 0.0D;
    private static final double DEFAULT_Z_OFFSET = 1.0D;

    public TorusMesh() {
        this(DEFAULT_DIVISIONS, DEFAULT_T_DIVISIONS, DEFAULT_RADIUS, DEFAULT_T_RADIUS);
    }

    public TorusMesh(double radius, double tRadius) {
        this(DEFAULT_DIVISIONS, DEFAULT_T_DIVISIONS, radius, tRadius);
    }

    public TorusMesh(int rDivs, int tDivs, double radius, double tRadius) {
        setRadiusDivisions(rDivs);
        setTubeDivisions(tDivs);
        setRadius(radius);
        setTubeRadius(tRadius);
        
        setDepthTest(DepthTest.ENABLE);
        updateMesh();
    }

    private void updateMesh(){       
        setMesh(createTorus(
            getRadiusDivisions(), 
            getTubeDivisions(), 
            (float) getRadius(), 
            (float) getTubeRadius(), 
            (float) getTubeStartAngleOffset(), 
            (float)getxOffset(),
            (float)getyOffset(), 
            (float)getzOffset()));     
    }
    
    private TriangleMesh createTorus(
            int radiusDivisions,
            int tubeDivisions,
            float radius,
            float tRadius,
            float tubeStartAngle,
            float xOffset,
            float yOffset,
            float zOffset) {

        int numVerts = tubeDivisions * radiusDivisions;
        int faceCount = numVerts * 2;
        float[] points = new float[numVerts * 3],
                texCoords = new float[numVerts * 2];
        int[] faces = new int[faceCount * 6];

        int pointIndex = 0, texIndex = 0, faceIndex = 0;
        float tubeFraction = 1.0f / tubeDivisions;
        float radiusFraction = 1.0f / radiusDivisions;
        float x, y, z;

        int p0 = 0, p1 = 0, p2 = 0, p3 = 0, t0 = 0, t1 = 0, t2 = 0, t3 = 0;

        // create points
        for (int tubeIndex = 0; tubeIndex < tubeDivisions; tubeIndex++) {

            float radian = tubeStartAngle + tubeFraction * tubeIndex * 2.0f * 3.141592653589793f;

            for (int radiusIndex = 0; radiusIndex < radiusDivisions; radiusIndex++) {

                float localRadian = radiusFraction * (radiusIndex) * 2.0f * 3.141592653589793f;

                points[pointIndex + 0] = x = (radius + tRadius * ((float) Math.cos(radian))) * ((float) Math.cos(localRadian) + xOffset);
                points[pointIndex + 1] = y = (radius + tRadius * ((float) Math.cos(radian))) * ((float) Math.sin(localRadian) + yOffset);
                points[pointIndex + 2] = z = (tRadius * (float) Math.sin(radian) * zOffset);

                pointIndex += 3;

                float r = radiusIndex < tubeDivisions ? tubeFraction * radiusIndex * 2.0F * 3.141592653589793f : 0.0f;
                texCoords[texIndex] = (0.5F + (float) (Math.sin(r) * 0.5D));
                texCoords[texIndex + 1] = ((float) (Math.cos(r) * 0.5D) + 0.5F);

                texIndex += 2;

            }

        }
        //create faces        
        for (int point = 0; point < (tubeDivisions); point++) {
            for (int crossSection = 0; crossSection < (radiusDivisions); crossSection++) {
                p0 = point * radiusDivisions + crossSection;
                p1 = p0 >= 0 ? p0 + 1 : p0 - (radiusDivisions);
                p1 = p1 % (radiusDivisions) != 0 ? p0 + 1 : p0 - (radiusDivisions - 1);
                p2 = (p0 + radiusDivisions) < ((tubeDivisions * radiusDivisions)) ? p0 + radiusDivisions : p0 - (tubeDivisions * radiusDivisions) + radiusDivisions;
                p3 = p2 < ((tubeDivisions * radiusDivisions) - 1) ? p2 + 1 : p2 - (tubeDivisions * radiusDivisions) + 1;
                p3 = p3 % (radiusDivisions) != 0 ? p2 + 1 : p2 - (radiusDivisions - 1);

                t0 = point * (radiusDivisions) + crossSection;
                t1 = t0 >= 0 ? t0 + 1 : t0 - (radiusDivisions);
                t1 = t1 % (radiusDivisions) != 0 ? t0 + 1 : t0 - (radiusDivisions - 1);
                t2 = (t0 + radiusDivisions) < ((tubeDivisions * radiusDivisions)) ? t0 + radiusDivisions : t0 - (tubeDivisions * radiusDivisions) + radiusDivisions;
                t3 = t2 < ((tubeDivisions * radiusDivisions) - 1) ? t2 + 1 : t2 - (tubeDivisions * radiusDivisions) + 1;
                t3 = t3 % (radiusDivisions) != 0 ? t2 + 1 : t2 - (radiusDivisions - 1);

                try {
                    faces[faceIndex] = (p2);
                    faces[faceIndex + 1] = (t3);
                    faces[faceIndex + 2] = (p0);
                    faces[faceIndex + 3] = (t2);
                    faces[faceIndex + 4] = (p1);
                    faces[faceIndex + 5] = (t0);

                    faceIndex += 6;

                    faces[faceIndex] = (p2);
                    faces[faceIndex + 1] = (t3);
                    faces[faceIndex + 2] = (p1);
                    faces[faceIndex + 3] = (t0);
                    faces[faceIndex + 4] = (p3);
                    faces[faceIndex + 5] = (t1);
                    faceIndex += 6;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        TriangleMesh localTriangleMesh = new TriangleMesh();
        localTriangleMesh.getPoints().setAll(points);
        localTriangleMesh.getTexCoords().setAll(texCoords);
        localTriangleMesh.getFaces().setAll(faces);

        return localTriangleMesh;
    }

    private final IntegerProperty radiusDivisions = new SimpleIntegerProperty(DEFAULT_DIVISIONS) {

        @Override
        protected void invalidated() {
            setMesh(createTorus(
                getRadiusDivisions(), 
                getTubeDivisions(), 
                (float) getRadius(), 
                (float) getTubeRadius(), 
                (float) getTubeStartAngleOffset(), 
                (float)getxOffset(),
                (float)getyOffset(), 
                (float)getzOffset()));
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

    private final IntegerProperty tubeDivisions = new SimpleIntegerProperty(DEFAULT_T_DIVISIONS) {

        @Override
        protected void invalidated() {
            updateMesh();
        }

    };

    public final int getTubeDivisions() {
        return tubeDivisions.get();
    }

    public final void setTubeDivisions(int value) {
        tubeDivisions.set(value);
    }

    public IntegerProperty tubeDivisionsProperty() {
        return tubeDivisions;
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

    public DoubleProperty radiusProperty() {
        return radius;
    }

    private final DoubleProperty tubeRadius = new SimpleDoubleProperty(DEFAULT_T_RADIUS) {

        @Override
        protected void invalidated() {
            updateMesh();
        }

    };

    public final double getTubeRadius() {
        return tubeRadius.get();
    }

    public final void setTubeRadius(double value) {
        tubeRadius.set(value);
    }

    public DoubleProperty tubeRadiusProperty() {
        return tubeRadius;
    }

    private final DoubleProperty tubeStartAngleOffset = new SimpleDoubleProperty(DEFAULT_START_ANGLE) {

        @Override
        protected void invalidated() {
            updateMesh();
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
            updateMesh();
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
            updateMesh();
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
            updateMesh();
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

}
