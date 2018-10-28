/**
 * ImportOBJ.java
 *
 * Copyright (c) 2013-2018, F(X)yz
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

package org.fxyz3d.samples.importers;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;
import org.fxyz3d.controls.factory.ControlFactory;
import org.fxyz3d.importers.Importer3D;
import org.fxyz3d.samples.shapes.ShapeBaseSample;
import org.fxyz3d.shapes.polygon.PolygonMeshView;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Parent;
import org.fxyz3d.controls.ControlCategory;
import org.fxyz3d.controls.NumberSliderControl;

/**
 *
 * @author JosePereda
 */
public class ImportOBJ extends ShapeBaseSample<Group> {

    private final IntegerProperty subdivision = new SimpleIntegerProperty(this, "Subdivision Level", 0);
    
    private boolean asPolygonMesh = true;

    public static void main(String[] args){launch(args);}
    
    @Override
    protected void createMesh() {
        try {
            model = (Group) Importer3D.load(
                    ImportOBJ.class.getResource("/org/fxyz3d/importers/DukeKing.obj").toExternalForm(),
                    asPolygonMesh);

        } catch (IOException ex) {
            Logger.getLogger(ImportOBJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected void addMeshAndListeners() {
        drawMode.addListener((obs, b, b1) -> {
            if (model != null) {
                if (asPolygonMesh) {
                    model.getChildren().stream()
                            .filter(PolygonMeshView.class::isInstance)
                            .map(PolygonMeshView.class::cast)
                            .forEach(pmv -> pmv.setDrawMode(b1));
                } else {
                    model.getChildren().stream()
                            .filter(MeshView.class::isInstance)
                            .map(MeshView.class::cast)
                            .forEach(pmv -> pmv.setDrawMode(b1));
                }
            }
        });
        culling.addListener((obs, b, b1) -> {
            if (model != null) {
                if (asPolygonMesh) {
                    model.getChildren().stream()
                            .filter(PolygonMeshView.class::isInstance)
                            .map(PolygonMeshView.class::cast)
                            .forEach(pmv -> pmv.setCullFace(b1));
                } else {
                    model.getChildren().stream()
                            .filter(MeshView.class::isInstance)
                            .map(MeshView.class::cast)
                            .forEach(pmv -> pmv.setCullFace(b1));
                }
            }
        });
        subdivision.addListener((obs, ov, nv) -> setSubdivisionLevel(model, nv.intValue()));
    }
    
    private void setSubdivisionLevel(Node node, int subdivisionLevel) {
        if (node instanceof PolygonMeshView) {
            ((PolygonMeshView) node).setSubdivisionLevel(subdivisionLevel);
        } else if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().forEach(n -> setSubdivisionLevel(n, subdivisionLevel));
        }
    }
    
    @Override
    protected Node buildControlPanel() {
        NumberSliderControl subdivisionSlider = ControlFactory.buildNumberSlider(subdivision, 0, 2);
        subdivisionSlider.getSlider().setMajorTickUnit(1);
        subdivisionSlider.getSlider().setMinorTickCount(0);
        subdivisionSlider.getSlider().setBlockIncrement(1);
        subdivisionSlider.getSlider().setSnapToTicks(true);
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(subdivisionSlider);
        
        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.drawMode,
                        this.culling
                ), geomControls);
        return controlPanel;
    }
}
