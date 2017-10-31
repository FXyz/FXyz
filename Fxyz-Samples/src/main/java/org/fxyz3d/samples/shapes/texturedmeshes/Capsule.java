/**
 * Capsules.java
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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import org.fxyz3d.controls.ControlCategory;
import org.fxyz3d.controls.NumberSliderControl;
import org.fxyz3d.controls.factory.ControlFactory;
import org.fxyz3d.samples.shapes.TexturedMeshSample;
import org.fxyz3d.shapes.primitives.CapsuleMesh;

/**
 *
 * @author Dub
 */
public class Capsule extends TexturedMeshSample{
    
    public static void main(String[] args){launch(args);}
        
    private final DoubleProperty radius = new SimpleDoubleProperty(model, "Radius", 50.0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CapsuleMesh)model).setRadius(radius.get());
            }
        }
    };
    private final DoubleProperty height = new SimpleDoubleProperty(model, "Height", 20.0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CapsuleMesh)model).setHeight(height.get());
            }
        }
    };
    
    private final IntegerProperty divisions = new SimpleIntegerProperty(model, "Divisions", 20) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CapsuleMesh)model).setDivisions(divisions.get());
            }
        }
    };    
   
    @Override
    protected void createMesh() {
        model = new CapsuleMesh(divisions.getValue(), radius.getValue(), height.getValue());
    }

    @Override
    protected void addMeshAndListeners() {

    }

    @Override
    protected Node buildControlPanel() {
        
        NumberSliderControl radSlider = ControlFactory.buildNumberSlider(radius, 5.0d, 150.0d);
        radSlider.getSlider().setMinorTickCount(10);
        radSlider.getSlider().setMajorTickUnit(25);
        radSlider.getSlider().setBlockIncrement(1);
        radSlider.getSlider().setSnapToTicks(true);       
        
        NumberSliderControl heightSlider = ControlFactory.buildNumberSlider(height, 10.0d, 400d);
        heightSlider.getSlider().setMinorTickCount(10);        
        heightSlider.getSlider().setMajorTickUnit(25);
        heightSlider.getSlider().setBlockIncrement(1);
        heightSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl divsSlider = ControlFactory.buildNumberSlider(divisions, 3D, 400D);
        divsSlider.getSlider().setMinorTickCount(5);
        divsSlider.getSlider().setMajorTickUnit(20);
        divsSlider.getSlider().setBlockIncrement(10d);
        divsSlider.getSlider().setSnapToTicks(true);

        
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(radSlider, heightSlider, divsSlider);
        geomControls.setExpanded(true);

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
