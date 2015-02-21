/**
* SurfacePlot.java
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

import java.util.function.Function;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.samples.shapes.TexturedMeshSample;
import org.fxyz.shapes.primitives.SurfacePlotMesh;

/**
 *
 * @author jpereda
 */
public class SurfacePlot extends TexturedMeshSample {
    public static void main(String[] args){SurfacePlot.launch(args);}
    
    //private static final Image image = new Image(SurfacePlot.class.getResourceAsStream(".../res/top.png"));
    private final ObjectProperty<Function<Point2D, Number>> function2D = 
            new SimpleObjectProperty<Function<Point2D, Number>>(model,"Function F(P(x,y))",p->Math.sin(p.magnitude())/p.magnitude()){
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SurfacePlotMesh)model).setFunction2D(function2D.get());
            }
        }
    };
    private final DoubleProperty rangeX = new SimpleDoubleProperty(model, "Range X", 20) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SurfacePlotMesh)model).setRangeX(rangeX.get());
            }
        }
    };

    private final DoubleProperty rangeY = new SimpleDoubleProperty(model, "Range Y", 20) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SurfacePlotMesh)model).setRangeY(rangeY.get());
            }
        }
    };

    private final IntegerProperty divisionsX = new SimpleIntegerProperty(model, "Divisions X", 100) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SurfacePlotMesh)model).setDivisionsX(divisionsX.get());
            }
        }
    };
    
    private final IntegerProperty divisionsY = new SimpleIntegerProperty(model, "Divisions Y", 100) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SurfacePlotMesh)model).setDivisionsY(divisionsY.get());
            }
        }
    };
    
    private final DoubleProperty scale = new SimpleDoubleProperty(model, "Scale", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SurfacePlotMesh)model).setScale(scale.get());
            }
        }
    };

    
    @Override
    public void createMesh() {
        model = new SurfacePlotMesh(function2D.get(),rangeX.get(),rangeY.get(),divisionsX.get(),divisionsY.get(),scale.get());        
        model.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);
        model.sceneProperty().addListener(e->{
            if(model.getScene()!= null){
                //material.setDiffuseMap(image);
            }
        });
        
    }
    
    @Override
    protected void addMeshAndListeners() {
    }
    
    @Override
    protected Node buildControlPanel() {
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(
                ControlFactory.buildScriptFunction2DControl(function2D),
                ControlFactory.buildNumberSlider(rangeX, 0, 100),
                ControlFactory.buildNumberSlider(rangeY, 0, 100),
                ControlFactory.buildNumberSlider(divisionsX, 1, 1000),
                ControlFactory.buildNumberSlider(divisionsY, 1, 1000),
                ControlFactory.buildNumberSlider(scale, 0.01, 100)
        );

        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.drawMode,
                        this.culling
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(
                        this.textureType, this.colors,
                        null, this.useDiffMap,
                        this.material.diffuseMapProperty(),
                        this.patterns, this.pattScale,
                        this.specColor, this.specularPower, 
                        this.dens, this.func
                )
        );
        
        return this.controlPanel;
    }

    
    
}
