/**
 * Surface3DMesh.java
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
package org.fxyz3d.shapes.primitives;

import org.fxyz3d.geometry.Face3;
import org.fxyz3d.shapes.primitives.helper.delaunay.DelaunayMesh;
import org.fxyz3d.shapes.primitives.helper.delaunay.Triangle3D;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import org.fxyz3d.geometry.Point3D;

import java.util.ArrayList;
import java.util.List;

public class Surface3DMesh extends TexturedMesh {

    public Surface3DMesh() {
        this(new ArrayList<>());
    }

    public Surface3DMesh(List<Point3D> dataPoints) {
        setSurfaceData(dataPoints);

        updateMesh();
        setCullFace(CullFace.NONE);
        setDrawMode(DrawMode.LINE);
        setDepthTest(DepthTest.ENABLE);
    }

    // surfaceData
    private final ObjectProperty<List<Point3D>> surfaceData = new SimpleObjectProperty<>(this, "surfaceData") {
        @Override
        protected void invalidated() {
            if (mesh != null) {
                updateMesh();
            }
        }
    };
    public final ObjectProperty<List<Point3D>> surfaceDataProperty() {
        return surfaceData;
    }
    public final List<Point3D> getSurfaceData() {
        return surfaceData.get();
    }
    public final void setSurfaceData(List<Point3D> value) {
        surfaceData.set(value);
    }

    @Override
    protected void updateMesh() {
        setMesh(null);
        mesh=createPlotMesh();
        setMesh(mesh);
    }

    private TriangleMesh createPlotMesh() {
        listVertices.clear();
        listTextures.clear();
        listFaces.clear();

        DelaunayMesh delaunayMesh = new DelaunayMesh(getSurfaceData());

        listVertices.addAll(getSurfaceData());

        textureCoords = new float[2 * delaunayMesh.getNormalizedPoints().size()];
        int counter = 0;
        for (Point3D point : delaunayMesh.getNormalizedPoints()) {
            textureCoords[counter] = point.getX();
            textureCoords[counter++] = point.getY();
        }

        //add texture and face indices
        List<Point3D> normalizedPoints = delaunayMesh.getNormalizedPoints();
        for (Triangle3D triangle : delaunayMesh.getTriangle3DList()) {
            int faceIndex1 = normalizedPoints.indexOf(triangle.getP0());
            int faceIndex2 = normalizedPoints.indexOf(triangle.getP1());
            int faceIndex3 = normalizedPoints.indexOf(triangle.getP2());
            listTextures.add(new Face3(faceIndex1, faceIndex2, faceIndex3));
            listFaces.add(new Face3(faceIndex1, faceIndex2, faceIndex3));
        }

        return createMesh();
    }

}
