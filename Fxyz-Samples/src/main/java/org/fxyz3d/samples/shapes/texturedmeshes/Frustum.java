/**
 * Frustum.java
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
import org.fxyz3d.samples.shapes.TexturedMeshSample;
import org.fxyz3d.shapes.primitives.FrustumMesh;

/**
 *
 * @author jpereda
 */
public class Frustum extends TexturedMeshSample{
    public static void main(String[] args){launch(args);}
    
    private final DoubleProperty majorRadius = new SimpleDoubleProperty(model, "MajorRadius", 1d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((FrustumMesh)model).setMajorRadius(majorRadius.get());
            }
        }
    };
    private final DoubleProperty height = new SimpleDoubleProperty(model, "Height", 4d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((FrustumMesh)model).setHeight(height.get());
            }
        }
    };
    private final DoubleProperty minorRadius = new SimpleDoubleProperty(model, "MinorRadius", 0.5d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((FrustumMesh)model).setMinorRadius(minorRadius.get());
            }
        }
    };
    private final IntegerProperty level = new SimpleIntegerProperty(model, "Level", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((FrustumMesh)model).setLevel(level.get());
            }
        }
    };
    @Override
    protected void createMesh() {
        model = new FrustumMesh(this.majorRadius.get(), this.minorRadius.get(), this.height.get(), this.level.get());
//        model.setTextureModeNone(Color.ROYALBLUE);
    }


    @Override
    protected void addMeshAndListeners() {
    }

    @Override
    protected Node buildControlPanel() {
        NumberSliderControl majorRSlider = ControlFactory.buildNumberSlider(this.majorRadius, .01D, 1D);
        majorRSlider.getSlider().setMinorTickCount(10);
        majorRSlider.getSlider().setMajorTickUnit(0.5);
        majorRSlider.getSlider().setBlockIncrement(0.01d);

        NumberSliderControl heightSlider = ControlFactory.buildNumberSlider(this.height, .01D, 20D);
        heightSlider.getSlider().setMinorTickCount(10);
        heightSlider.getSlider().setMajorTickUnit(0.5);
        heightSlider.getSlider().setBlockIncrement(0.01d);
        
        NumberSliderControl minorRSlider = ControlFactory.buildNumberSlider(this.minorRadius, .0D, 1D);
        minorRSlider.getSlider().setMinorTickCount(10);
        minorRSlider.getSlider().setMajorTickUnit(0.5);
        minorRSlider.getSlider().setBlockIncrement(0.01d);
        
        NumberSliderControl levelSlider = ControlFactory.buildNumberSlider(this.level, 0, 8);
        levelSlider.getSlider().setMinorTickCount(0);
        levelSlider.getSlider().setMajorTickUnit(1);
        levelSlider.getSlider().setBlockIncrement(1);
        levelSlider.getSlider().setSnapToTicks(true);
        
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(majorRSlider,minorRSlider,heightSlider,levelSlider);

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
