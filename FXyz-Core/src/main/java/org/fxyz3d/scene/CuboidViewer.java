/**
 * CuboidViewer.java
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
package org.fxyz3d.scene;

import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.fxyz3d.shapes.polygon.PolygonMesh;
import org.fxyz3d.shapes.polygon.PolygonMeshView;
import org.fxyz3d.utils.DataBox;

import java.util.logging.Logger;

/**
 *
 * @author JosePereda
 */
public class CuboidViewer extends Group {

    private static final Logger LOG = Logger.getLogger(CuboidViewer.class.getName());

    public static final double MARGIN_VALUE = 1.2;
    public static final double LOWER_VALUE = 0.4;

    private final PolygonMeshView backGridXY, frontGridXY;
    private final PolygonMeshView bottomGridXZ, topGridXZ;
    private final PolygonMeshView rightGridYZ, leftGridYZ;
    private final PolygonMeshView cuboidBox;

    private double delta;
    private double cameraRX, cameraRY, cameraRZ;

    public CuboidViewer() {
        backGridXY = new PolygonMeshView();
        frontGridXY = new PolygonMeshView();
        bottomGridXZ = new PolygonMeshView();
        topGridXZ = new PolygonMeshView();
        rightGridYZ = new PolygonMeshView();
        leftGridYZ = new PolygonMeshView();
        getChildren().addAll(backGridXY, frontGridXY, bottomGridXZ, topGridXZ, leftGridYZ, rightGridYZ);

        cuboidBox = new PolygonMeshView();
        getChildren().add(cuboidBox);

        getChildren().stream()
                .filter(PolygonMeshView.class::isInstance)
                .map(PolygonMeshView.class::cast)
                .forEach(m -> {
                    m.setDrawMode(DrawMode.LINE);
                    m.setCullFace(CullFace.NONE);
                    m.setDepthTest(DepthTest.ENABLE);
                    m.setMaterial(new PhongMaterial(Color.DARKGREY));
                    m.setOnMouseExited(t -> {
                        adjustPanelsByPos(cameraRX, cameraRY, cameraRZ);
                        t.consume();
                    });
                });
        setDepthTest(DepthTest.ENABLE);

        createCuboid(DataBox.getDefaultDataBox(), 10);
    }

    public final void createCuboid(DataBox dataBox, double delta) {
        this.delta = (delta < dataBox.getMaxSize() / 10d) ? dataBox.getMaxSize() / 10d : delta;
        double sizeX = dataBox.getSizeX() * MARGIN_VALUE;
        double cenX = dataBox.getCenterX();
        double sizeY = dataBox.getSizeY() * MARGIN_VALUE;
        double cenY = dataBox.getCenterY();
        double sizeZ = dataBox.getSizeZ() * MARGIN_VALUE;
        double cenZ = dataBox.getCenterZ();
        double min = LOWER_VALUE * dataBox.getMaxSize() * MARGIN_VALUE;
        if (sizeX < min) {
            sizeX = min;
        }
        if (sizeY < min) {
            sizeY = min;
        }
        if (sizeZ < min) {
            sizeZ = min;
        }

        final PolygonMesh planeXY = createQuadrilateralMesh((float) sizeX, (float) sizeY, (int) (sizeX / this.delta), (int) (sizeY / this.delta));

        backGridXY.setMesh(planeXY);
        backGridXY.getTransforms().setAll(new Translate(cenX, cenY, cenZ + sizeZ / 2d));

        frontGridXY.setMesh(planeXY);
        frontGridXY.getTransforms().setAll(new Translate(cenX, cenY, cenZ - sizeZ / 2d));

        final PolygonMesh planeXZ = createQuadrilateralMesh((float) sizeX, (float) sizeZ, (int) (sizeX / this.delta), (int) (sizeZ / this.delta));

        bottomGridXZ.setMesh(planeXZ);
        bottomGridXZ.getTransforms().setAll(new Translate(cenX, cenY + sizeY / 2d, cenZ));
        bottomGridXZ.getTransforms().add(new Rotate(90, 0, 0, 0, Rotate.X_AXIS));

        topGridXZ.setMesh(planeXZ);
        topGridXZ.getTransforms().setAll(new Translate(cenX, cenY - sizeY / 2d, cenZ));
        topGridXZ.getTransforms().add(new Rotate(90, 0, 0, 0, Rotate.X_AXIS));

        final PolygonMesh planeYZ = createQuadrilateralMesh((float) sizeZ, (float) sizeY, (int) (sizeZ / this.delta), (int) (sizeY / this.delta));

        rightGridYZ.setMesh(planeYZ);
        rightGridYZ.getTransforms().setAll(new Translate(cenX + sizeX / 2d, cenY, cenZ));
        rightGridYZ.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));

        leftGridYZ.setMesh(planeYZ);
        leftGridYZ.getTransforms().setAll(new Translate(cenX - sizeX / 2d, cenY, cenZ));
        leftGridYZ.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));

        final PolygonMesh cuboid = createCuboidMesh((float) sizeX, (float) sizeY, (float) sizeZ);
        cuboidBox.setMesh(cuboid);
        cuboidBox.getTransforms().setAll(new Translate(cenX, cenY, cenZ));

        adjustPanelsByPos(0, 0, 0);
    }

    public void adjustPanelsByPos(double rx, double ry, double rz) {
        cameraRX = rx;
        cameraRY = ry;
        cameraRZ = rz;

        if ((Math.abs(rx) <= 90 && (-85 < ry && ry < 85)) ||
                (Math.abs(rx) > 90 && ((95 < ry && ry < 180) || (-180 < ry && ry < -95)))) {
            frontGridXY.setVisible(false);
        } else {
            frontGridXY.setVisible(true);
        }
        if ((Math.abs(rx) <= 90 && ((95 < ry && ry < 180) || (-180 < ry && ry < -95))) ||
                (Math.abs(rx) > 90 && (-85 < ry && ry < 85))) {
            backGridXY.setVisible(false);
        } else {
            backGridXY.setVisible(true);
        }

        leftGridYZ.setVisible(! (5 < ry && ry < 175));
        rightGridYZ.setVisible(! (-175 < ry && ry < -5));

        topGridXZ.setVisible(rx > 0);
        bottomGridXZ.setVisible(! topGridXZ.isVisible());
    }

    private PolygonMesh createQuadrilateralMesh(float width, float height, int subDivX, int subDivY) {
        final float minX = - width / 2f;
        final float minY = - height / 2f;
        final float maxX = width / 2f;
        final float maxY = height / 2f;

        final int pointSize = 3;
        final int texCoordSize = 2;
        // 4 point indices and 4 texCoord indices per face
        final int faceSize = 8;
        int numDivX = subDivX + 1;
        int numVerts = (subDivY + 1) * numDivX;
        float points[] = new float[numVerts * pointSize];
        float texCoords[] = new float[numVerts * texCoordSize];
        int faceCount = subDivX * subDivY;
        int faces[][] = new int[faceCount][faceSize];

        // Create points and texCoords
        for (int y = 0; y <= subDivY; y++) {
            float dy = (float) y / subDivY;
            double fy = (1 - dy) * minY + dy * maxY;

            for (int x = 0; x <= subDivX; x++) {
                float dx = (float) x / subDivX;
                double fx = (1 - dx) * minX + dx * maxX;

                int index = y * numDivX * pointSize + (x * pointSize);
                points[index] = (float) fx;
                points[index + 1] = (float) fy;
                points[index + 2] = 0.0f;

                index = y * numDivX * texCoordSize + (x * texCoordSize);
                texCoords[index] = dx;
                texCoords[index + 1] = dy;
            }
        }

        // Create faces
        int index = 0;
        for (int y = 0; y < subDivY; y++) {
            for (int x = 0; x < subDivX; x++) {
                int p00 = y * numDivX + x;
                int p01 = p00 + 1;
                int p10 = p00 + numDivX;
                int p11 = p10 + 1;
                int tc00 = y * numDivX + x;
                int tc01 = tc00 + 1;
                int tc10 = tc00 + numDivX;
                int tc11 = tc10 + 1;

                faces[index][0] = p00;
                faces[index][1] = tc00;
                faces[index][2] = p10;
                faces[index][3] = tc10;
                faces[index][4] = p11;
                faces[index][5] = tc11;
                faces[index][6] = p01;
                faces[index++][7] = tc01;
            }
        }

        int[] smooth = new int[faceCount];

        PolygonMesh mesh = new PolygonMesh(points, texCoords, faces);
        mesh.getFaceSmoothingGroups().addAll(smooth);
        return mesh;
    }

    private PolygonMesh createCuboidMesh(float width, float height, float depth) {
        float L = 2f * width + 2f * depth;
        float H = height + 2f * depth;
        float hw = width/2f, hh = height/2f, hd = depth/2f;

        float[] points = new float[] {
                hw, hh, hd,             hw, hh, -hd,
                hw, -hh, hd,            hw, -hh, -hd,
                -hw, hh, hd,            -hw, hh, -hd,
                -hw, -hh, hd,           -hw, -hh, -hd
        };

        float[] texCoords = new float[] {
                depth / L, 0f,                              (depth + width) / L, 0f,
                0f, depth / H,                              depth / L, depth / H,
                (depth + width) / L, depth / H,             (2f * depth + width) / L, depth/H,
                1f, depth / H,                              0f, (depth + height) / H,
                depth / L, (depth + height)/H,              (depth + width) / L, (depth + height) / H,
                (2f * depth + width) / L, (depth + height) / H,  1f, (depth + height) / H,
                depth / L, 1f,                              (depth + width) / L, 1f
        };

        int[][] faces = new int[][] {
                {0, 8, 2, 3, 3, 2, 1, 7},
                {4, 9, 5, 10, 7, 5, 6, 4},
                {0, 8, 1, 12, 5, 13, 4, 9},
                {2, 3, 6, 4, 7, 1, 3, 0},
                {0, 8, 4, 9, 6, 4, 2, 3},
                {1, 11, 3, 6, 7, 5, 5, 10}
        };

        int[] smooth = new int[] {
                1, 2, 3, 4, 5, 6
        };

        PolygonMesh mesh = new PolygonMesh(points, texCoords, faces);
        mesh.getFaceSmoothingGroups().addAll(smooth);
        return mesh;
    }

    public double getDelta() {
        return delta;
    }

    public double getCameraRX() {
        return cameraRX;
    }

    public double getCameraRY() {
        return cameraRY;
    }

    public double getCameraRZ() {
        return cameraRZ;
    }
}
