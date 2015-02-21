/**
* Springs.java
*
* Copyright (c) 2013-2015, F(X)yz
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* * Neither the name of the organization nor the
* names of its contributors may be used to endorse or promote products
* derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.fxyz.samples.shapes.texturedmeshes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.samples.shapes.TexturedMeshSample;
import org.fxyz.shapes.primitives.SpringMesh;

/**
 *
 * @author jpereda
 */
public class Springs extends TexturedMeshSample {

    public static void main(String[] args) {
        Springs.launch(args);
    }

    private final DoubleProperty meanRadius = new SimpleDoubleProperty(model, "Mean Radius", 5d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SpringMesh)model).setMeanRadius(meanRadius.get());
            }
        }
    };
    
    private final DoubleProperty wireRadius = new SimpleDoubleProperty(model, "Wire Radius", 1d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SpringMesh)model).setWireRadius(wireRadius.get());
            }
        }
    };
    
    private final DoubleProperty pitch = new SimpleDoubleProperty(model, "Pitch", 2d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SpringMesh)model).setPitch(pitch.get());
            }
        }
    };
    
    private final DoubleProperty length = new SimpleDoubleProperty(model, "Length", 4d* 2d * Math.PI) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SpringMesh)model).setLength(length.get());
            }
        }
    };
    
    private final IntegerProperty wireDivs = new SimpleIntegerProperty(model, "Wire Divisions", 60) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SpringMesh)model).setWireDivisions(wireDivs.get());
            }
        }
    };
    private final IntegerProperty lenDivs = new SimpleIntegerProperty(model, "Length Divisions", 500) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SpringMesh)model).setLengthDivisions(lenDivs.get());
            }
        }
    };
    private final IntegerProperty wireCrop = new SimpleIntegerProperty(model, "Wire Crop", 0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SpringMesh)model).setWireCrop((int)Math.min(wireCrop.get(),wireDivs.intValue()/2));
            }
        }
    };
    private final IntegerProperty lenCrop = new SimpleIntegerProperty(model, "Length Crop", 0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SpringMesh)model).setLengthCrop((int)Math.min(lenCrop.intValue(),lenDivs.intValue()/2));
            }
        }

    };
    @Override
    protected void createMesh() {

        model = new SpringMesh(meanRadius.get(), wireRadius.get(), pitch.get(), length.get(),
                lenDivs.get(), wireDivs.get(), lenCrop.get(), wireCrop.get());
        model.setTextureModeNone(Color.ROYALBLUE);
        model.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);
    }

    @Override
    protected void addMeshAndListeners() {
    }

    @Override
    protected Node buildControlPanel() {
        NumberSliderControl meanRadSlider = ControlFactory.buildNumberSlider(this.meanRadius, .01D, 200D);
        meanRadSlider.getSlider().setMinorTickCount(4);
        meanRadSlider.getSlider().setMajorTickUnit(25);
        meanRadSlider.getSlider().setBlockIncrement(1d);

        NumberSliderControl tRadSlider = ControlFactory.buildNumberSlider(this.wireRadius, 0.01D, 25D);
        tRadSlider.getSlider().setMinorTickCount(4);
        tRadSlider.getSlider().setMajorTickUnit(5);
        tRadSlider.getSlider().setBlockIncrement(0.5d);

        NumberSliderControl pitchSlider = ControlFactory.buildNumberSlider(this.pitch, 0.01D, 20D);
        pitchSlider.getSlider().setMinorTickCount(4);
        pitchSlider.getSlider().setMajorTickUnit(5);
        pitchSlider.getSlider().setBlockIncrement(0.5d);

        NumberSliderControl lengthSlider = ControlFactory.buildNumberSlider(this.length, 0.01D, 1000D);
        lengthSlider.getSlider().setMinorTickCount(4);
        lengthSlider.getSlider().setMajorTickUnit(50);
        lengthSlider.getSlider().setBlockIncrement(2d);

        NumberSliderControl wDivSlider = ControlFactory.buildNumberSlider(this.wireDivs, 2, 100);
        wDivSlider.getSlider().setMinorTickCount(4);
        wDivSlider.getSlider().setMajorTickUnit(10);
        wDivSlider.getSlider().setBlockIncrement(1);
        wDivSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl mCropSlider = ControlFactory.buildNumberSlider(this.wireCrop, 0l, 98);
        mCropSlider.getSlider().setMinorTickCount(4);
        mCropSlider.getSlider().setMajorTickUnit(10);
        mCropSlider.getSlider().setBlockIncrement(1);
        mCropSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl lDivSlider = ControlFactory.buildNumberSlider(this.lenDivs, 4l, 1000);
        lDivSlider.getSlider().setMinorTickCount(4);
        lDivSlider.getSlider().setMajorTickUnit(10);
        lDivSlider.getSlider().setBlockIncrement(1);
        lDivSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl lCropSlider = ControlFactory.buildNumberSlider(this.lenCrop, 0l, 200);
        lCropSlider.getSlider().setMinorTickCount(4);
        lCropSlider.getSlider().setMajorTickUnit(10);
        lCropSlider.getSlider().setBlockIncrement(1);
        lCropSlider.getSlider().setSnapToTicks(true);

        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(meanRadSlider,
                tRadSlider, wDivSlider, pitchSlider, lengthSlider, mCropSlider,
                lDivSlider, lCropSlider);

        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.drawMode,
                        this.culling
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(
                        this.textureType, this.colors,
                        this.sectionType, this.useDiffMap,
                        this.material.diffuseMapProperty(),
                        this.patterns, this.pattScale, 
                        this.specColor, this.specularPower, 
                        this.dens, this.func
                )
        );

        return this.controlPanel;
    }

}
