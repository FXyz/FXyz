/**
 * TorusMesh.java
 *
 * Copyright (c) 2013-2020, F(X)yz
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
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 *
 * @author jDub1581
 */
public class TorusMesh extends MeshView {

    private static final int DEFAULT_DIVISIONS = 64;
    private static final int DEFAULT_T_DIVISIONS = 64;
    private static final double DEFAULT_RADIUS = 12.5;
    private static final double DEFAULT_T_RADIUS = 5.0;
    private static final double DEFAULT_START_ANGLE = 0.0;
    private static final double DEFAULT_X_OFFSET = 0.0;
    private static final double DEFAULT_Y_OFFSET = 0.0;
    private static final double DEFAULT_Z_OFFSET = 1.0;

    public TorusMesh() {
        this(DEFAULT_DIVISIONS, DEFAULT_T_DIVISIONS, DEFAULT_RADIUS, DEFAULT_T_RADIUS);
    }

    public TorusMesh(double radius, double tubeRadius) {
        this(DEFAULT_DIVISIONS, DEFAULT_T_DIVISIONS, radius, tubeRadius);
    }

    public TorusMesh(int radiusDivisions, int tubeDivisions, double radius, double tubeRadius) {
        setRadiusDivisions(radiusDivisions);
        setTubeDivisions(tubeDivisions);
        setRadius(radius);
        setTubeRadius(tubeRadius);
        
        setDepthTest(DepthTest.ENABLE);
        updateMesh();
    }

    private void updateMesh(){       
        setMesh(
                createTorusMesh(
                        getRadiusDivisions(),
                        getTubeDivisions(),
                        (float) getRadius(),
                        (float) getTubeRadius(),
                        (float) getTubeStartAngleOffset(),
                        (float) getxOffset(),
                        (float) getyOffset(),
                        (float) getzOffset()
                )
        );
    }
    
    private TriangleMesh createTorusMesh(
            final int radiusDivisions,
            final int tubeDivisions,
            final float radius,
            final float tRadius,
            final float tubeStartAngle,
            final float xOffset,
            final float yOffset,
            final float zOffset) {

        final int numVerts = tubeDivisions * radiusDivisions;
        int faceCount = numVerts * 2;

        float[] points = new float[numVerts * 3];
        float[] texCoords = new float[numVerts * 2];
        int[] faces = new int[faceCount * 6];

        int pointIndex = 0;
        int texIndex = 0;
        int faceIndex = 0;

        float tubeFraction = 1.0f / tubeDivisions;
        float radiusFraction = 1.0f / radiusDivisions;

        float TWO_PI = (float) (2 * Math.PI);

        // create points
        // create texCoords
        for (int tubeIndex = 0; tubeIndex < tubeDivisions; tubeIndex++) {

            float radian = tubeStartAngle + tubeFraction * tubeIndex * TWO_PI;

            for (int radiusIndex = 0; radiusIndex < radiusDivisions; radiusIndex++) {

                float localRadian = radiusFraction * radiusIndex * TWO_PI;

                points[pointIndex + 0] = (radius + tRadius * ((float) cos(radian))) * ((float) cos(localRadian) + xOffset);
                points[pointIndex + 1] = (radius + tRadius * ((float) cos(radian))) * ((float) sin(localRadian) + yOffset);
                points[pointIndex + 2] = (tRadius * (float) sin(radian) * zOffset);

                pointIndex += 3;

                float r = radiusIndex < tubeDivisions ? tubeFraction * radiusIndex * TWO_PI : 0.0f;
                texCoords[texIndex + 0] = (float) (sin(r) * 0.5) + 0.5f;
                texCoords[texIndex + 1] = (float) (cos(r) * 0.5) + 0.5f;

                texIndex += 2;
            }
        }

        //create faces        
        for (int point = 0; point < tubeDivisions; point++) {
            for (int crossSection = 0; crossSection < radiusDivisions; crossSection++) {

                final int p0 = point * radiusDivisions + crossSection;

                int p1 = p0 >= 0 ? p0 + 1 : p0 - radiusDivisions;
                p1 = p1 % radiusDivisions != 0 ? p0 + 1 : p0 + 1 - radiusDivisions;

                final int p0r = p0 + radiusDivisions;

                final int p2 = p0r < numVerts ? p0r : p0r - numVerts;

                int p3 = p2 < (numVerts - 1) ? p2 + 1 : p2 + 1 - numVerts;
                p3 = p3 % radiusDivisions != 0 ? p2 + 1 : p2 + 1 - radiusDivisions;

                try {
                    faces[faceIndex + 0] = p2;
                    faces[faceIndex + 1] = p3;
                    faces[faceIndex + 2] = p0;
                    faces[faceIndex + 3] = p2;
                    faces[faceIndex + 4] = p1;
                    faces[faceIndex + 5] = p0;

                    faceIndex += 6;

                    faces[faceIndex + 0] = p2;
                    faces[faceIndex + 1] = p3;
                    faces[faceIndex + 2] = p1;
                    faces[faceIndex + 3] = p0;
                    faces[faceIndex + 4] = p3;
                    faces[faceIndex + 5] = p1;

                    faceIndex += 6;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);

        return mesh;
    }

    private final IntegerProperty radiusDivisions = new SimpleIntegerProperty(DEFAULT_DIVISIONS) {
        @Override
        protected void invalidated() {
            updateMesh();
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