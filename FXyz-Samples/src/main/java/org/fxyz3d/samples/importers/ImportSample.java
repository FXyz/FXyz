/**
 * ImportSample.java
 *
 * Copyright (c) 2013-2019, F(X)yz
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

import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import org.fxyz3d.controls.ControlCategory;
import org.fxyz3d.controls.NumberSliderControl;
import org.fxyz3d.controls.factory.ControlFactory;
import org.fxyz3d.importers.Importer3D;
import org.fxyz3d.importers.Model3D;
import org.fxyz3d.samples.shapes.ShapeBaseSample;
import org.fxyz3d.shapes.polygon.PolygonMeshView;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JosePereda
 */
public abstract class ImportSample extends ShapeBaseSample<Group> {

    protected final IntegerProperty subdivision = new SimpleIntegerProperty(this, "Subdivision Level", 0);
    protected final ObjectProperty<URL> url = new SimpleObjectProperty<>(this, "URL");
    protected final ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>(this, "timeline");
    protected final ObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content");
    protected final BooleanProperty asPolygonMesh = new SimpleBooleanProperty(this, "asPolygonMesh", true);
    protected final BooleanProperty yUp = new SimpleBooleanProperty(this, "yUp", true);
    
    @Override
    protected void addMeshAndListeners() {
        drawMode.addListener((obs, b, b1) -> {
            if (model != null) {
                setDrawMode(model, b1);
            }
        });
        culling.addListener((obs, b, b1) -> {
            if (model != null) {
                setCullFace(model, b1);
            }
        });
        subdivision.addListener((obs, ov, nv) -> setSubdivisionLevel(model, nv.intValue()));
        url.addListener((obs, ov, nv) -> {
            try {
                Model3D model3D = asPolygonMesh.get() ?
                        Importer3D.loadAsPoly(url.get()) : Importer3D.load(url.get());
                model.getChildren().setAll(model3D.getRoot().getChildren());
                model3D.getTimeline().ifPresentOrElse(t -> {
                    model.sceneProperty().addListener(new InvalidationListener() {
                        @Override
                        public void invalidated(Observable observable) {
                            if (model.getScene() == null) {
                                model.sceneProperty().removeListener(this);
                                timeline.set(null);
                            }
                        }
                    });
                    timeline.set(t);
                }, () -> timeline.set(null));
                initModel();
            } catch (IOException e) {
                Logger.getLogger(ImportSample.class.getName()).log(Level.SEVERE,null, e);
            }
        });
        yUp.addListener((obs, ov, nv) -> {
            model.getTransforms().setAll(new Rotate(nv ? 180 : 0, Rotate.X_AXIS));
        });
        initModel();
    }

    private void initModel() {
        setCullFace(model, culling.getValue());
        setDrawMode(model, drawMode.getValue());
        setSubdivisionLevel(model, subdivision.getValue());
        double size = Math.max(Math.max(model.getBoundsInLocal().getWidth(),
                model.getBoundsInLocal().getHeight()), model.getBoundsInLocal().getDepth());
        model.getTransforms().setAll(new Rotate(yUp.get() ? 180 : 0, Rotate.X_AXIS));
        content.set(model);
        camera.setTranslateZ(- size * 3);
    }

    private void setDrawMode(Node node, DrawMode value) {
        if (node instanceof PolygonMeshView) {
            ((PolygonMeshView) node).setDrawMode(value);
        } else if (node instanceof MeshView) {
            ((MeshView) node).setDrawMode(value);
        } else if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().forEach(n -> setDrawMode(n, value));
        }
    }

    private void setCullFace(Node node, CullFace value) {
        if (node instanceof PolygonMeshView) {
            ((PolygonMeshView) node).setCullFace(value);
        } else if (node instanceof MeshView) {
            ((MeshView) node).setCullFace(value);
        } else if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().forEach(n -> setCullFace(n, value));
        }
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
        ControlCategory geomControls = ControlFactory.buildCategory("3D Settings");
        geomControls.addControls(ControlFactory.buildFileLoadControl(url),
                ControlFactory.buildCheckBoxControl(asPolygonMesh),
                ControlFactory.buildCheckBoxControl(yUp),
                subdivisionSlider,
                ControlFactory.buildHierarchyControl(content));
        
        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.drawMode,
                        this.culling
                ), geomControls,
                ControlFactory.buildAnimationCategory(timeline));
        return controlPanel;
    }
}
