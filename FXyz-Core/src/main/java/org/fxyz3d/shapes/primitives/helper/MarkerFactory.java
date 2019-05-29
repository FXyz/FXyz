/**
 * MarkerFactory.java
 *
 * Copyright (c) 2018-2019, F(X)yz
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
package org.fxyz3d.shapes.primitives.helper;

import org.fxyz3d.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.shape.DrawMode;
import org.fxyz3d.shapes.primitives.*;

public class MarkerFactory {

    private interface Marker3D {
        TexturedMesh getMarker(String id, double size, int level, Point3D point3D);
    }

    public enum Marker implements Marker3D {
        TETRAHEDRA {
            @Override
            public TexturedMesh getMarker(String id, double size, int level, Point3D point3D) {
                TexturedMesh dot = new TetrahedraMesh(size, level, point3D);
                dot.setId(id);
                dot.setDrawMode(DrawMode.FILL);
                dot.setDepthTest(DepthTest.ENABLE);
                return dot;
            }
        },
        CUBE {
            @Override
            public TexturedMesh getMarker(String id, double size, int level, Point3D point3D) {
                TexturedMesh dot = new CuboidMesh(size, size, size, level, point3D);
                dot.setId(id);
                dot.setDrawMode(DrawMode.FILL);
                dot.setDepthTest(DepthTest.ENABLE);
                return dot;
            }
        },
        DIAMOND {
            @Override
            public TexturedMesh getMarker(String id, double size, int level, Point3D point3D) {
                TexturedMesh dot = new SegmentedSphereMesh(3 * (level * level + 1), 0, 0, size / 2d, point3D);
                dot.setId(id);
                dot.setDrawMode(DrawMode.FILL);
                dot.setDepthTest(DepthTest.ENABLE);
                return dot;
            }
        },
        SPHERE {
            @Override
            public TexturedMesh getMarker(String id, double size, int level, Point3D point3D) {
                TexturedMesh dot = new SegmentedSphereMesh(6 * (level * level + 1), 0, 0, size / 2d, point3D);
                dot.setId(id);
                dot.setDrawMode(DrawMode.FILL);
                dot.setDepthTest(DepthTest.ENABLE);
                return dot;
            }
        },
        CONE {
            @Override
            public TexturedMesh getMarker(String id, double size, int level, Point3D point3D) {
                TexturedMesh dot = new FrustumMesh(size / 3d, 0, size, level,
                        point3D != null ? point3D.add((float)(-size / 2d), 0f, 0f) : null,
                        point3D != null ? point3D.add((float)(size / 2d), 0f, 0f) : null);
                dot.setId(id);
                dot.setDrawMode(DrawMode.FILL);
                dot.setDepthTest(DepthTest.ENABLE);
                return dot;
            }
        };
    }

}
