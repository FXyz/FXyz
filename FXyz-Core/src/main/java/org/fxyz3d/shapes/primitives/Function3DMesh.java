/**
 * Function3DMesh.java
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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.shapes.polygon.PolygonMesh;
import org.fxyz3d.shapes.polygon.PolygonMeshView;

public class Function3DMesh extends Group {

    public Function3DMesh(SurfacePlotMesh surfacePlotMesh, boolean wireframe) {
        setWireframe(wireframe);
        setSurface(surfacePlotMesh);
        idProperty().addListener((obs, ov, nv) -> {
            if (getSurface() != null) {
                getSurface().setId(nv);
            }
        });
    }

    // surface
    private final ObjectProperty<SurfacePlotMesh> surface = new SimpleObjectProperty<SurfacePlotMesh>() {
        @Override
        protected void invalidated() {
            getChildren().setAll(get());
            if (isWireframe()) {
                addWireframe();
            }
            getSurface().setId(getId());
            function3DData.setAll(getSurface().listVertices);
        }
    };
    public final SurfacePlotMesh getSurface() { return surface.get(); }
    public final void setSurface(SurfacePlotMesh value) { surface.set(value); }
    public final ObjectProperty<SurfacePlotMesh> surfaceProperty() { return surface; }

    // wireframe
    private final BooleanProperty wireframe = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            getChildren().removeIf(PolygonMeshView.class::isInstance);
            if (get()) {
                addWireframe();
            }
        }
    };
    public final boolean isWireframe() { return wireframe.get(); }
    public final void setWireframe(boolean value) { wireframe.set(value); }
    public final BooleanProperty wireframeProperty() { return wireframe; }

    private final ObservableList<Point3D> function3DData = FXCollections.observableArrayList();

    public ObservableList<Point3D> getFunction3DData() {
        return function3DData;
    }

    private void addWireframe() {
        if (getSurface() == null) {
            return;
        }
        PolygonMesh polygonMesh = getSurface().getPolygonMesh();
        PolygonMeshView polygonMeshView = new PolygonMeshView(polygonMesh);
        polygonMeshView.setMaterial(new PhongMaterial(Color.BLACK));
        polygonMeshView.setDrawMode(DrawMode.LINE);
        polygonMeshView.setCullFace(CullFace.NONE);
        polygonMeshView.setId(getId());
        getChildren().addAll(polygonMeshView);
    }

}
