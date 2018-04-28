/**
 * SegmentedSphere.java
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

package org.fxyz3d.samples.shapes.texturedmeshes;

import static javafx.application.Application.launch;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import org.fxyz3d.controls.ControlCategory;
import org.fxyz3d.controls.NumberSliderControl;
import org.fxyz3d.controls.factory.ControlFactory;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.samples.shapes.TexturedMeshSample;
import org.fxyz3d.shapes.primitives.SegmentedSphereMesh;

/**
 *
 * @author Sean
 */
public class SegmentedSphere extends TexturedMeshSample {

    public static void main(String[] args){launch(args);}

    private final DoubleProperty radius = new SimpleDoubleProperty(model, "Radius", 50.0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedSphereMesh)model).setRadius(radius.get());
            }
        }
    };
    private final IntegerProperty cropX = new SimpleIntegerProperty(model, "Radius Crop X", 0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedSphereMesh)model).setRadiusCropX(cropX.get());
            }
        }
    };
    private final IntegerProperty cropY = new SimpleIntegerProperty(model, "Radius Crop Y", 0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedSphereMesh)model).setRadiusCropY(cropY.get());
            }
        }
    };
    private final IntegerProperty divisions = new SimpleIntegerProperty(model, "Radius Divisions", 20) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedSphereMesh)model).setRadiusDivisions(divisions.get());
            }
        }
    };

    
    @Override
    protected void createMesh() {
        model = new SegmentedSphereMesh(divisions.getValue(), cropX.getValue(), cropY.getValue(), radius.getValue(), new Point3D(0f, 0f, 0f));
    }

    @Override
    protected void addMeshAndListeners() {
        
    }

    @Override
    protected Node buildControlPanel() {

        NumberSliderControl radSlider = ControlFactory.buildNumberSlider(radius, .01D, 200D);
        radSlider.getSlider().setMinorTickCount(10);
        radSlider.getSlider().setMajorTickUnit(0.5);
        radSlider.getSlider().setBlockIncrement(0.01d);

        NumberSliderControl cropXSlider = ControlFactory.buildNumberSlider(cropX, 0, 5);
        cropXSlider.getSlider().setBlockIncrement(1);

        NumberSliderControl cropYSlider = ControlFactory.buildNumberSlider(cropY, 0, 5);
        cropYSlider.getSlider().setBlockIncrement(1);

        NumberSliderControl divsSlider = ControlFactory.buildNumberSlider(divisions, 5, 200);
        divsSlider.getSlider().setMinorTickCount(4);
        divsSlider.getSlider().setMajorTickUnit(5);
        divsSlider.getSlider().setBlockIncrement(5);
        divsSlider.getSlider().valueProperty().addListener((obs, ov, nv) -> {
            cropXSlider.getSlider().setValue(Math.min(cropXSlider.getSlider().getValue(), nv.intValue() / 2 - 1));
            cropXSlider.getSlider().setMax(nv.intValue() / 2 - 1);
            cropYSlider.getSlider().setValue(Math.min(cropYSlider.getSlider().getValue(), nv.intValue() / 2 - 1));
            cropYSlider.getSlider().setMax(nv.intValue() / 2 - 1);
        });
        
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(radSlider, cropXSlider, cropYSlider, divsSlider);

        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.drawMode,
                        this.culling
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(this.textureType, this.colors, 
                        null, this.textureImage,
                        this.useBumpMap, this.bumpScale,
                        this.bumpFineScale, this.invert,
                        this.patterns, this.pattScale, 
                        this.specColor, this.specularPower, 
                        this.dens, this.func
                )
        );
        
        return this.controlPanel;
    }
}