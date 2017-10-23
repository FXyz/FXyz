/**
 * SegmentedDome.java
 *
 * Copyright (c) 2013-2017, F(X)yz
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
import org.fxyz3d.shapes.primitives.SegmentedDomeMesh;

/**
 *
 * @author Sean
 */
public class SegmentedDome extends TexturedMeshSample {

    public static void main(String[] args){launch(args);}

    private final DoubleProperty radius = new SimpleDoubleProperty(model, "Radius", 50.0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedDomeMesh)model).setRadius(radius.get());
            }
        }
    };
    private final DoubleProperty phiMin = new SimpleDoubleProperty(model, "PhiMin", Math.toRadians(0)) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedDomeMesh)model).setPhimin(phiMin.get());
            }
        }
    };
    private final DoubleProperty phiMax = new SimpleDoubleProperty(model, "PhiMax", Math.toRadians(360)) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedDomeMesh)model).setPhimax(phiMax.get());
            }
        }
    };
    private final DoubleProperty thetaMin = new SimpleDoubleProperty(model, "ThetaMin", Math.toRadians(0)) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedDomeMesh)model).setThetamin(thetaMin.get());
            }
        }
    };
    private final DoubleProperty thetaMax = new SimpleDoubleProperty(model, "ThetaMax", Math.toRadians(90)) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedDomeMesh)model).setThetamax(thetaMax.get());
            }
        }
    };
    private final IntegerProperty divisions = new SimpleIntegerProperty(model, "Divisions", 20) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedDomeMesh)model).setDivisions(divisions.get());
            }
        }
    };

    
    @Override
    protected void createMesh() {
        model = new SegmentedDomeMesh(radius.getValue(), phiMin.getValue(), phiMax.getValue(),
            thetaMin.getValue(), thetaMax.getValue(), divisions.getValue());
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

        NumberSliderControl phiMinSlider = ControlFactory.buildNumberSlider(phiMin, Math.toRadians(0), Math.toRadians(360));
        radSlider.getSlider().setMinorTickCount(10);
        radSlider.getSlider().setMajorTickUnit(0.5);
        radSlider.getSlider().setBlockIncrement(0.01d);

        NumberSliderControl phiMaxSlider = ControlFactory.buildNumberSlider(phiMax, Math.toRadians(0), Math.toRadians(360));
        radSlider.getSlider().setMinorTickCount(10);
        radSlider.getSlider().setMajorTickUnit(0.5);
        radSlider.getSlider().setBlockIncrement(0.01d);
        
        NumberSliderControl thetaMinSlider = ControlFactory.buildNumberSlider(thetaMin, Math.toRadians(0), Math.toRadians(90));
        radSlider.getSlider().setMinorTickCount(10);
        radSlider.getSlider().setMajorTickUnit(0.5);
        radSlider.getSlider().setBlockIncrement(0.01d);

        NumberSliderControl thetaMaxSlider = ControlFactory.buildNumberSlider(thetaMax, Math.toRadians(0), Math.toRadians(90));
        radSlider.getSlider().setMinorTickCount(10);
        radSlider.getSlider().setMajorTickUnit(0.5);
        radSlider.getSlider().setBlockIncrement(0.01d);
        
        NumberSliderControl divsSlider = ControlFactory.buildNumberSlider(divisions, 5, 200D);
        divsSlider.getSlider().setMinorTickCount(4);
        divsSlider.getSlider().setMajorTickUnit(5);
        divsSlider.getSlider().setBlockIncrement(5);
        
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(radSlider, phiMinSlider, phiMaxSlider, 
            thetaMinSlider, thetaMaxSlider, divsSlider);

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