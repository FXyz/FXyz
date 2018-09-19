/**
 * SegmentedTorus.java
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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import org.fxyz3d.controls.ControlCategory;
import org.fxyz3d.controls.factory.ControlFactory;
import org.fxyz3d.samples.shapes.TexturedMeshSample;
import org.fxyz3d.shapes.primitives.SegmentedTorusMesh;

/**
 *
 * @author jpereda
 */
public class SegmentedTorus extends TexturedMeshSample {
    public static void main(String[] args){SegmentedTorus.launch(args);}
    
    private final DoubleProperty majRad = new SimpleDoubleProperty(model, "Major Radius", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setMajorRadius(majRad.get());
            }
        }
    };
    private final DoubleProperty minRad = new SimpleDoubleProperty(model, "Minor Radius", 1) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setMinorRadius(minRad.get());
            }
        }
    };
    private final IntegerProperty majorDivs = new SimpleIntegerProperty(model, "Major Radius Divisions", 100) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setMajorRadiusDivisions(majorDivs.get());
            }
        }
    };
    private final IntegerProperty minorDivs = new SimpleIntegerProperty(model, "Minor Radius Divisions", 100) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setMinorRadiusDivisions(minorDivs.get());
            }
        }
    };
    private final DoubleProperty _x = new SimpleDoubleProperty(model, "X Offset") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setxOffset(_x.doubleValue());
            }
        }
    };
    private final DoubleProperty _y = new SimpleDoubleProperty(model, "Y Offset") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setyOffset(_y.doubleValue());
            }
        }
    };
    private final DoubleProperty _z = new SimpleDoubleProperty(model, "Z Offset",1d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setzOffset(_z.doubleValue());
            }
        }
    };

    private final IntegerProperty majRadCrop = new SimpleIntegerProperty(model, "Major Radius Crop") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setMajorRadiusCrop(majRadCrop.getValue());
            }
        }
    };
    
    
    @Override
    public void createMesh() {
//        model = new SegmentedTorusMesh(50, 42, 0, 100d, 25d);        
        model = new SegmentedTorusMesh(majorDivs.get(), minorDivs.get(), majRadCrop.get(), majRad.get(), minRad.get());        
        model.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);
    }
    
    @Override
    protected void addMeshAndListeners() {
    }
    
    @Override
    protected Node buildControlPanel() {
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(
                ControlFactory.buildNumberSlider(majRad, 1, 100),
                ControlFactory.buildNumberSlider(minRad, 1, 100),
                ControlFactory.buildNumberSlider(majorDivs, 8, 360),
                ControlFactory.buildNumberSlider(minorDivs, 8, 360),
                ControlFactory.buildNumberSlider(majRadCrop, 0, 50),
                ControlFactory.buildNumberSlider(_x, -1, 1),
                ControlFactory.buildNumberSlider(_y, -1, 1),
                ControlFactory.buildNumberSlider(_z, 0.01, 100)
                //ControlFactory.buildNumberSlider(_angle, 0.01, 359.89)                
        );

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
