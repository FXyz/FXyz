/**
* TexturedMeshSample.java
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

package org.fxyz.samples.shapes;

import java.io.File;
import java.io.FileInputStream;
import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import org.fxyz.geometry.Point3D;
import org.fxyz.scene.paint.Patterns.CarbonPatterns;
import org.fxyz.shapes.primitives.TexturedMesh;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper;
import org.fxyz.tools.NormalMap;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public abstract class TexturedMeshSample extends ShapeBaseSample<TexturedMesh>{

    //specific
        
    protected final Property<TriangleMeshHelper.SectionType> sectionType = new SimpleObjectProperty<TriangleMeshHelper.SectionType>(model, "secType", TriangleMeshHelper.SectionType.CIRCLE) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setSectionType(sectionType.getValue());
            }
        }
    };
    protected final Property<TriangleMeshHelper.TextureType> textureType = new SimpleObjectProperty<TriangleMeshHelper.TextureType>(model, "texType", TriangleMeshHelper.TextureType.NONE) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                switch(getValue()){
                    case NONE:
                        model.setTextureModeNone(colorBinding.get());
                        break;
                    case IMAGE:
                        model.setTextureModeImage(diffMapPath.get());
                        break;
                    case PATTERN:
                        model.setTextureModePattern(patterns.get(),pattScale.getValue());
                        break;
                    case COLORED_VERTICES_1D:
                        model.setTextureModeVertices1D(1530, func.getValue());
                        break;
                    case COLORED_VERTICES_3D:
                        model.setTextureModeVertices3D(1530, dens.getValue());
                        break;
                    case COLORED_FACES:
                        model.setTextureModeFaces(1530);
                        break;
                }                
            }
        }
    };

    /*
     TriangleMeshHelper.TextureType.NONE 
    */
    protected final ObjectProperty<Color> colorBinding = new SimpleObjectProperty<Color>(Color.BROWN){
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null && model.getTextureType().equals(TriangleMeshHelper.TextureType.NONE)) {
                model.setDiffuseColor(get());
            }
        }
    };
    protected final IntegerProperty colors = new SimpleIntegerProperty(model, "Color :", 700) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                colorBinding.set(Color.hsb(360 * (1d - get() / 1530d), 1, 1));                
            }
        }
    };   
    
    /*
     TriangleMeshHelper.TextureType.IMAGE 
    */
    protected final StringProperty diffMapPath = new SimpleStringProperty(this, "imagePath", "");
    protected final Property<Boolean> useDiffMap = new SimpleBooleanProperty(this, "Use PhongMaterial", false) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null && model.getTextureType().equals(TriangleMeshHelper.TextureType.IMAGE)) {
                if (diffMapPath.get().isEmpty()) {
                    //load default
                    material.setDiffuseMap((new Image(getClass().getResource("samples/res/LaminateSteel.jpg").toExternalForm())));
                } else {
                    try { // should be given the string from filechooser
                        material.setDiffuseMap(new Image(new FileInputStream(new File(diffMapPath.get()))));
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        }
    };
    protected final ObjectProperty<CarbonPatterns> patterns = new SimpleObjectProperty<CarbonPatterns>(CarbonPatterns.DARK_CARBON){
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null && model.getTextureType().equals(TriangleMeshHelper.TextureType.PATTERN)) {
                model.setCarbonPattern(patterns.getValue());
            }
        }
    };
    
    /*
     TriangleMeshHelper.TextureType.PATTERN 
    */
    protected final DoubleProperty pattScale = new SimpleDoubleProperty(this, "Pattern Scale: ", 2.0d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null && model.getTextureType().equals(TriangleMeshHelper.TextureType.PATTERN)) {
                model.setPatternScale(pattScale.doubleValue());
            }
        }
    };
    
    /*
     TriangleMeshHelper.TextureType.COLORED_VERTICES_3D 
    */
    protected final DoubleProperty densMax = new SimpleDoubleProperty(this, "Density Scale: ");
    protected final Property<Function<Point3D,Number>> dens = new SimpleObjectProperty<Function<Point3D,Number>>(p -> p.x * p.y * p.z) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null && model.getTextureType().equals(TriangleMeshHelper.TextureType.COLORED_VERTICES_3D)) {
                model.setDensity(dens.getValue());
            }
        }
    };
    /*
     TriangleMeshHelper.TextureType.COLORED_VERTICES_1D 
    */
    protected final Property<Function<Number,Number>> func = new SimpleObjectProperty<Function<Number,Number>>(t->t) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null && model.getTextureType().equals(TriangleMeshHelper.TextureType.COLORED_VERTICES_1D)) {
                model.setFunction(func.getValue());
            }
        }
    };
    
    protected final Property<Boolean> invert = new SimpleBooleanProperty(this, "invertBump", false){
        @Override
        protected void invalidated() {
            if(model != null){
            }
        }
    };
    protected final DoubleProperty bumpScale = new SimpleDoubleProperty(this, "bumpScale", 27d) {
        @Override
        protected void invalidated() {
             if(model != null){
                 
             }
        }
    };
    protected final ObjectProperty<Image> bumpMap = new SimpleObjectProperty<Image>(this, "bumpMap", null){
        @Override
        protected void invalidated() {
             if(model != null){
             }
        }
    };
    protected final DoubleProperty bumpFineScale = new SimpleDoubleProperty(this, "bumpFineScale", 9d){
        @Override
        protected void invalidated() {
             if(model != null){
             }
        }
    };
    protected final BooleanProperty addNormalMap = new SimpleBooleanProperty(this, "useBump", false) {
        @Override
        protected void invalidated() {
            if (get()) {
                if (model != null) {
                    if (model.getMaterial() != null && ((PhongMaterial) model.getMaterial()).getDiffuseMap() != null) {
                        ((PhongMaterial) model.getMaterial()).setBumpMap(
                                new NormalMap(
                                        bumpScale.doubleValue(), bumpFineScale.doubleValue(),
                                        invert.getValue(), ((PhongMaterial) model.getMaterial()).getDiffuseMap()
                                )
                        );
                    }
                }
            } else {
                if (model != null) {
                    ((PhongMaterial) model.getMaterial()).setBumpMap(null);
                }
            }
        }

    };

    protected final DoubleProperty specularPower = new SimpleDoubleProperty(this, "specularPower"){
        @Override
        protected void invalidated() {
                if (model != null) {
                    if (model.getMaterial() != null) {
                        ((PhongMaterial)model.getMaterial()).setSpecularPower(specularPower.getValue());
                    }
                }
            }
    };
    
    protected final ObjectProperty<Color> specColor = new SimpleObjectProperty<Color>(this, "specColor", Color.TRANSPARENT){

        @Override
        protected void invalidated() {
            if (model != null) {
                    if (model.getMaterial() != null) {
                        ((PhongMaterial)model.getMaterial()).setSpecularColor(specColor.getValue());
                    }
                }
        }
    
    };
    
    
}
