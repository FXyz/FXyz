/**
 * Surface3D.java
 *
 * Copyright (c) 2013-2021, F(X)yz
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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import org.fxyz3d.controls.ControlCategory;
import org.fxyz3d.controls.factory.ControlFactory;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.samples.shapes.TexturedMeshSample;
import org.fxyz3d.shapes.primitives.Surface3DMesh;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author jpereda
 */
public class Surface3D extends TexturedMeshSample {
    public static void main(String[] args){
        Surface3D.launch(args);}

    private final ObjectProperty<Function<Point2D, Number>> function2D =
        new SimpleObjectProperty<>(model,"Function F(P(x,y))",p->Math.sin(p.magnitude())){
            @Override
            protected void invalidated() {
                super.invalidated();
                if (model != null) {
                    ((Surface3DMesh)model).setSurfaceData(updateData());
                }
            }
        };

    private final DoubleProperty rangeX = new SimpleDoubleProperty(model, "Range X", 10) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((Surface3DMesh)model).setSurfaceData(updateData());
            }
        }
    };

    private final DoubleProperty rangeY = new SimpleDoubleProperty(model, "Range Y", 10) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((Surface3DMesh)model).setSurfaceData(updateData());
            }
        }
    };

    private final IntegerProperty divisionsX = new SimpleIntegerProperty(model, "Divisions X", 50) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((Surface3DMesh)model).setSurfaceData(updateData());
            }
        }
    };
    
    private final IntegerProperty divisionsY = new SimpleIntegerProperty(model, "Divisions Y", 50) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((Surface3DMesh)model).setSurfaceData(updateData());
            }
        }
    };
    
    private final DoubleProperty scale = new SimpleDoubleProperty(model, "Scale", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((Surface3DMesh)model).setSurfaceData(updateData());
            }
        }
    };

    
    @Override
    public void createMesh() {
        model = new Surface3DMesh(updateData());
        model.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);

    }

    private List<Point3D> updateData() {
        List<Point3D> data = new ArrayList<>();
        for (int y = 0; y <= divisionsY.get(); y++) {
            float dy = (float)(-rangeY.get()/2d + ((float)y /(float)divisionsY.get()) * rangeY.get());
            for (int x = 0; x <= divisionsX.get(); x++) {
                float dx = (float)(-rangeX.get()/2d + ((float)x /(float)divisionsX.get()) * rangeX.get());
                float pointY = (float)scale.get() * function2D.get().apply(new Point2D(dx,dy)).floatValue();
                data.add(new Point3D(dx, pointY, dy));
            }
        }
        return data;
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
                ControlFactory.buildTextureMeshCategory(this.textureType,
                        this.colors,
                        null,
                        this.textureImage,
                        this.useBumpMap, 
                        this.bumpScale,
                        this.bumpFineScale, 
                        this.invert,
                        this.patterns,
                        this.pattScale,
                        this.specColor, 
                        this.specularPower, 
                        this.dens,
                        this.func
                )
        );
        
        return this.controlPanel;
    }

    
    
}
