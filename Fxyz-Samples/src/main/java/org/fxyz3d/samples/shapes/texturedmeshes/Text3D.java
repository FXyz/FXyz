/**
 * Text3D.java
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import org.fxyz3d.controls.ControlCategory;
import org.fxyz3d.controls.NumberSliderControl;
import org.fxyz3d.controls.factory.ControlFactory;
import org.fxyz3d.samples.shapes.GroupOfTexturedMeshSample;
import org.fxyz3d.shapes.primitives.Text3DMesh;

/**
 *
 * @author jpereda
 */
public class Text3D extends GroupOfTexturedMeshSample{
    
    public static void main(String[] args){launch(args);}
    
    private final DoubleProperty height = new SimpleDoubleProperty(model, "Height", 12d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((Text3DMesh)model).setHeight(get());
            }
        }
    };
    private final DoubleProperty gap = new SimpleDoubleProperty(model, "Gap", 0d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((Text3DMesh)model).setGap(get());
            }
        }
    };
    private final IntegerProperty level = new SimpleIntegerProperty(model, "Level", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((Text3DMesh)model).setLevel(get());
            }
        }
    };
    private final IntegerProperty fontSize = new SimpleIntegerProperty(model, "Font Size", 100) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((Text3DMesh)model).setFontSize(get());
            }
        }
    };
    private final BooleanProperty joinSegments = new SimpleBooleanProperty(model, "Join Segments", false) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((Text3DMesh)model).setJoinSegments(get());
            }
        }
    };
    private final StringProperty text3D = new SimpleStringProperty(model, "Text3D", "F(X)yz") {
        @Override
        protected void invalidated() {
            super.invalidated(); 
            System.out.println("text " + get());
            if (model != null) {
                ((Text3DMesh)model).setText3D(get());
            }
        }
    }; 
    private final StringProperty font = new SimpleStringProperty(model, "Font Family", "Arial") {
        @Override
        protected void invalidated() {
            super.invalidated(); 
            System.out.println("font " + get());
            if (model != null) {
                ((Text3DMesh)model).setFont(get());
            }
        }
    }; 
    
    @Override
    protected void createMesh() {
        model = new Text3DMesh(this.text3D.get(), this.height.get(), this.level.get());
//        model.setTextureModeNone(Color.ROYALBLUE);
    }


    @Override
    protected void addMeshAndListeners() {
    }

    @Override
    protected Node buildControlPanel() {
        NumberSliderControl heightSlider = ControlFactory.buildNumberSlider(this.height, .01D, 200D);
        heightSlider.getSlider().setMinorTickCount(10);
        heightSlider.getSlider().setMajorTickUnit(0.5);
        heightSlider.getSlider().setBlockIncrement(0.01d);
        
        NumberSliderControl gapSlider = ControlFactory.buildNumberSlider(this.gap, 0D, 100D);
        gapSlider.getSlider().setMinorTickCount(4);
        gapSlider.getSlider().setMajorTickUnit(5);
        gapSlider.getSlider().setBlockIncrement(1d);
        
        NumberSliderControl fontSizeSlider = ControlFactory.buildNumberSlider(this.fontSize, 1D, 400D);
        fontSizeSlider.getSlider().setMinorTickCount(1);
        fontSizeSlider.getSlider().setMajorTickUnit(10d);
        fontSizeSlider.getSlider().setBlockIncrement(1d);
        
        NumberSliderControl levelSlider = ControlFactory.buildNumberSlider(this.level, 0, 8);
        levelSlider.getSlider().setMinorTickCount(0);
        levelSlider.getSlider().setMajorTickUnit(1);
        levelSlider.getSlider().setBlockIncrement(1);
        levelSlider.getSlider().setSnapToTicks(true);
        
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(heightSlider, gapSlider, fontSizeSlider,levelSlider);

        ControlCategory text3DControls = ControlFactory.buildCategory("Text");
        text3DControls.addControls(ControlFactory.buildFontControl(this.font), 
                ControlFactory.buildTextFieldControl("Text", text3D), 
                ControlFactory.buildCheckBoxControl(joinSegments));

        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.drawMode,
                        this.culling
                ),
                geomControls, text3DControls,
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
