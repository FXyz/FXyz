/**
* GroupOfTexturedMeshSample.java
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
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import org.fxyz.controls.TextureImage;
import org.fxyz.geometry.Point3D;
import org.fxyz.scene.paint.Patterns;
import org.fxyz.scene.paint.Patterns.CarbonPatterns;
import org.fxyz.shapes.primitives.TexturedMesh;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.TextureType;
import org.fxyz.tools.NormalMap;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public abstract class GroupOfTexturedMeshSample extends ShapeBaseSample<Group>{

    public GroupOfTexturedMeshSample(){
        sectionType.addListener((obs,s0,s1)->{
            if (model != null) {
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .forEach(s->s.setSectionType(sectionType.getValue()));
            }
        });
        textureType.addListener((obs,t0,t1)->{
            if (model != null) {
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .forEach(s->{
                        switch(t1){
                            case NONE:
                                s.setTextureModeNone(colorBinding.get());
                                break;
                            case IMAGE:
                                s.setTextureModeImage(textureImage.getValue()==null?null:textureImage.getValue().getSource());
                                break;
                            case PATTERN:
                                s.setTextureModePattern(patterns.get(), pattScale.getValue());
                                break;
                            case COLORED_VERTICES_1D:
                                s.setTextureModeVertices1D(1540, func.getValue());
                                break;
                            case COLORED_VERTICES_3D:
                                s.setTextureModeVertices3D(1600, dens.getValue());
                                break;
                            case COLORED_FACES:
                                s.setTextureModeFaces(1550);
                                break;
                        }
                    });
            }
        });
        
        colorBinding.addListener((obs,c0,c1)->{
            if (model != null){
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .filter(s->s.getTextureType().equals(TextureType.NONE))
                    .forEach(s->s.setDiffuseColor(c1));
            }
        });
        pattScale.addListener((obs,p0,p1)->{
            if (model != null){
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .filter(s->s.getTextureType().equals(TextureType.PATTERN))
                    .forEach(s->s.setPatternScale(p1.doubleValue()));
            }
        });
        patterns.addListener((obs,c0,c1)->{
            if (model != null){
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .filter(s->s.getTextureType().equals(TextureType.PATTERN))
                    .forEach(s->{
                        s.setCarbonPattern(c1);
                        ((PhongMaterial)s.getMaterial()).setSpecularColor(specColorBinding.get());
                        ((PhongMaterial)s.getMaterial()).setSpecularPower(specularPower.doubleValue());
                        if (useBumpMap.get()) {
                            ((PhongMaterial)s.getMaterial()).setBumpMap(new NormalMap(
                                    bumpScale.doubleValue(), bumpFineScale.doubleValue(),
                                    invert.getValue(), ((PhongMaterial) s.getMaterial()).getDiffuseMap()
                            ));
                        }
                    });
            }
        });
        dens.addListener((obs,f0,f1)->{
            if (model != null){
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .filter(s->s.getTextureType().equals(TextureType.COLORED_VERTICES_3D))
                    .forEach(s->s.setDensity(f1));
            }
        });
        func.addListener((obs,f0,f1)->{
            if (model != null){
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .filter(s->s.getTextureType().equals(TextureType.COLORED_VERTICES_1D))
                    .forEach(s->s.setFunction(f1));
            }
        });
        textureImage.addListener((obs,f0,f1)->{
            if (model != null){
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .filter(s->s.getTextureType().equals(TextureType.IMAGE))
                    .forEach(s->{
                        s.setTextureModeImage(textureImage.getValue().getSource());
                        if (useBumpMap.getValue() || invert.getValue()) {
                            useBumpMap.setValue(false);
                            invert.setValue(false);
                        }
                    });
            }
        });
        
        invert.addListener((obs,b,b1)->updateGroupMaterial());
        bumpScale.addListener((obs,b,b1)->updateGroupMaterial());
        bumpFineScale.addListener((obs,b,b1)->updateGroupMaterial());
        useBumpMap.addListener((obs,b,b1)->{
            if (b1) {
                updateGroupMaterial();
            } else {
                if (model != null) {
                    model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                        .map(TexturedMesh.class::cast)
                        .forEach(s->((PhongMaterial)s.getMaterial()).setBumpMap(null));
                }
            }
        });
        specularPower.addListener((obs,n,n1)->{
            if (model != null) {
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .forEach(s->{
                        ((PhongMaterial)s.getMaterial()).setSpecularPower(n1.doubleValue());
                    });
            }
        });
        specColorBinding.addListener((obs,c,c1)->{
            if (model != null) {
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .forEach(s->{
                        ((PhongMaterial)s.getMaterial()).setSpecularColor(c1);
                    });
            }
        });
    }
    
    //specific
    protected final Property<TriangleMeshHelper.SectionType> sectionType = new SimpleObjectProperty<TriangleMeshHelper.SectionType>(model, "secType", TriangleMeshHelper.SectionType.CIRCLE) {};
    protected final Property<TriangleMeshHelper.TextureType> textureType = new SimpleObjectProperty<TriangleMeshHelper.TextureType>(model, "texType", TriangleMeshHelper.TextureType.NONE) {};
    protected final ObjectProperty<Color> colorBinding = new SimpleObjectProperty<Color>(Color.BROWN){};
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
    protected final Property<TextureImage> textureImage = new SimpleObjectProperty(this, "Texture");
    
    /*
     TriangleMeshHelper.TextureType.PATTERN 
    */
    protected final DoubleProperty pattScale = new SimpleDoubleProperty(this, "Pattern Scale: ", 2.0d) {};
    protected final ObjectProperty<CarbonPatterns> patterns = new SimpleObjectProperty<CarbonPatterns>(Patterns.CarbonPatterns.DARK_CARBON){};
    
    private void updateGroupMaterial(){
        if (model != null) {
            model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                .map(TexturedMesh.class::cast)
                .filter(s->((PhongMaterial)s.getMaterial()).getDiffuseMap()!=null)
                .forEach(s->{
                    ((PhongMaterial)s.getMaterial()).setBumpMap(
                        new NormalMap(bumpScale.doubleValue(), bumpFineScale.doubleValue(),
                                      invert.getValue(), ((PhongMaterial)s.getMaterial()).getDiffuseMap()));
                });
        }
    }
    protected final Property<Boolean> invert = new SimpleBooleanProperty(this, "Invert Bump Map", false);
    protected final DoubleProperty bumpScale = new SimpleDoubleProperty(this, "Bump Scale", 27d);
    protected final ObjectProperty<Image> bumpMap = new SimpleObjectProperty<>(this, "bumpMap", null);
    protected final DoubleProperty bumpFineScale = new SimpleDoubleProperty(this, "Bump Fine Scale", 9d);
    protected final BooleanProperty useBumpMap = new SimpleBooleanProperty(this, "Generate Bump Map", false);
    protected final DoubleProperty specularPower = new SimpleDoubleProperty(this, "Specular Power");
    protected final ObjectProperty<Color> specColorBinding = new SimpleObjectProperty<>(Color.BLACK);
    protected final IntegerProperty specColor = new SimpleIntegerProperty(this, "Specular Color", 1) {

        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                specColorBinding.set(Color.hsb(360 * (1d - get() / 1530d), 1, 1));
            }
        }

    };
    /*
     TriangleMeshHelper.TextureType.COLORED_VERTICES_3D 
    */
    protected final DoubleProperty densMax = new SimpleDoubleProperty(this, "Density Scale: ");
    protected final Property<Function<Point3D,Number>> dens = new SimpleObjectProperty<Function<Point3D,Number>>(p -> p.x * p.y * p.z) {};
    /*
     TriangleMeshHelper.TextureType.COLORED_VERTICES_1D 
    */
    protected final Property<Function<Number,Number>> func = new SimpleObjectProperty<Function<Number,Number>>(t->t) {};
    
    
}
