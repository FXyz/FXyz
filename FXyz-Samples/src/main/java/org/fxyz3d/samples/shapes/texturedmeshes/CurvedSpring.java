/**
 * CurvedSpring.java
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
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.fxyz3d.controls.ControlCategory;
import org.fxyz3d.controls.NumberSliderControl;
import org.fxyz3d.controls.factory.ControlFactory;
import org.fxyz3d.samples.shapes.TexturedMeshSample;
import org.fxyz3d.shapes.primitives.CurvedSpringMesh;

/**
 *
 * @author jpereda
 */
public class CurvedSpring extends TexturedMeshSample {
    
    public static void main(String[] args){
        CurvedSpring.launch(args);
    }
    
    private final DoubleProperty majorRadius = new SimpleDoubleProperty(model, "Major Radius", 6d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CurvedSpringMesh)model).setMajorRadius(majorRadius.get());
            }
        }
    };
    
    private final DoubleProperty minorRadius = new SimpleDoubleProperty(model, "Minor Radius", 2d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CurvedSpringMesh)model).setMinorRadius(minorRadius.get());
            }
        }
    };
    
    private final DoubleProperty wireRadius = new SimpleDoubleProperty(model, "Wire Radius", 0.4d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CurvedSpringMesh)model).setWireRadius(wireRadius.get());
            }
        }
    };
    
    private final DoubleProperty pitch = new SimpleDoubleProperty(model, "Pitch", 25d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CurvedSpringMesh)model).setPitch(pitch.get());
            }
        }
    };
    
    private final DoubleProperty length = new SimpleDoubleProperty(model, "Length", 12.5d* 2d * Math.PI) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CurvedSpringMesh)model).setLength(length.get());
            }
        }
    };
    
    private final IntegerProperty wireDivs = new SimpleIntegerProperty(model, "Wire Divisions", 60) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CurvedSpringMesh)model).setWireDivisions(wireDivs.get());
            }
        }
    };
    private final IntegerProperty lenDivs = new SimpleIntegerProperty(model, "Length Divisions", 500) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CurvedSpringMesh)model).setLengthDivisions(lenDivs.get());
            }
        }
    };
    private final IntegerProperty wireCrop = new SimpleIntegerProperty(model, "Wire Crop", 0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CurvedSpringMesh)model).setWireCrop((int)Math.min(wireCrop.get(),wireDivs.intValue()/2));
            }
        }
    };
    private final IntegerProperty lenCrop = new SimpleIntegerProperty(model, "Length Crop", 0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CurvedSpringMesh)model).setLengthCrop((int)Math.min(lenCrop.intValue(),lenDivs.intValue()/2));
            }
        }

    };
    
    @Override
    public void createMesh() {        
//        model = new CurvedSpringMesh(6d, 2d, 0.4d, 25d, 12.5d * 2d * Math.PI,
//                1000, 60, 0, 0);
        model = new CurvedSpringMesh(majorRadius.get(),minorRadius.get(),wireRadius.get(),pitch.get(),
                length.get(),lenDivs.get(),wireDivs.get(),lenCrop.get(),wireCrop.get());
        model.setTextureModeNone(Color.ROYALBLUE);
        model.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);
    }

    @Override
    protected void addMeshAndListeners() {
    }
    
    @Override
    protected Node buildControlPanel() {
        NumberSliderControl majRadSlider = ControlFactory.buildNumberSlider(this.majorRadius, .01D, 200D);
        majRadSlider.getSlider().setMinorTickCount(4);
        majRadSlider.getSlider().setMajorTickUnit(25);
        majRadSlider.getSlider().setBlockIncrement(1);

        NumberSliderControl minRadSlider = ControlFactory.buildNumberSlider(this.minorRadius, .01D, 200D);
        minRadSlider.getSlider().setMinorTickCount(4);
        minRadSlider.getSlider().setMajorTickUnit(25);
        minRadSlider.getSlider().setBlockIncrement(1);

        NumberSliderControl tRadSlider = ControlFactory.buildNumberSlider(this.wireRadius, 0.01D, 25D);
        tRadSlider.getSlider().setMinorTickCount(4);
        tRadSlider.getSlider().setMajorTickUnit(10);
        tRadSlider.getSlider().setBlockIncrement(0.5d);

        NumberSliderControl pitchSlider = ControlFactory.buildNumberSlider(this.pitch, 0.01D, 100D);
        pitchSlider.getSlider().setMinorTickCount(4);
        pitchSlider.getSlider().setMajorTickUnit(25);
        pitchSlider.getSlider().setBlockIncrement(0.5d);

        NumberSliderControl lengthSlider = ControlFactory.buildNumberSlider(this.length, 0.01D, 1000D);
        lengthSlider.getSlider().setMinorTickCount(4);
        lengthSlider.getSlider().setMajorTickUnit(50);
        lengthSlider.getSlider().setBlockIncrement(2d);

        NumberSliderControl wDivSlider = ControlFactory.buildNumberSlider(this.wireDivs, 2, 500);
        wDivSlider.getSlider().setMinorTickCount(4);
        wDivSlider.getSlider().setMajorTickUnit(25);
        wDivSlider.getSlider().setBlockIncrement(1);
        wDivSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl mCropSlider = ControlFactory.buildNumberSlider(this.wireCrop, 0l, 248);
        mCropSlider.getSlider().setMinorTickCount(4);
        mCropSlider.getSlider().setMajorTickUnit(25);
        mCropSlider.getSlider().setBlockIncrement(1);
        mCropSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl lDivSlider = ControlFactory.buildNumberSlider(this.lenDivs, 4l, 1000);
        lDivSlider.getSlider().setMinorTickCount(4);
        lDivSlider.getSlider().setMajorTickUnit(25);
        lDivSlider.getSlider().setBlockIncrement(1);
        lDivSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl lCropSlider = ControlFactory.buildNumberSlider(this.lenCrop, 0l, 498);
        lCropSlider.getSlider().setMinorTickCount(4);
        lCropSlider.getSlider().setMajorTickUnit(25);
        lCropSlider.getSlider().setBlockIncrement(1);
        lCropSlider.getSlider().setSnapToTicks(true);
        
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(majRadSlider, minRadSlider,
                tRadSlider, pitchSlider, lengthSlider,
                lDivSlider, wDivSlider, lCropSlider, mCropSlider);

        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.drawMode,
                        this.culling
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(this.textureType, 
                        this.colors, this.sectionType, 
                        this.textureImage,
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
