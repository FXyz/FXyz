/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.samples.shapes;

import java.io.File;
import java.io.FileInputStream;
import java.util.function.Function;
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
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.TexturedMesh;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public abstract class TexturedMeshSample extends ShapeBaseSample<TexturedMesh>{

    protected final Property<DrawMode> drawMode = new SimpleObjectProperty<DrawMode>(model, "drawMode", DrawMode.FILL) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setDrawMode(drawMode.getValue());
            }
        }
    };
    protected final Property<CullFace> culling = new SimpleObjectProperty<CullFace>(model, "culling", CullFace.BACK) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setCullFace(culling.getValue());
            }
        }
    };
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
                        model.setTextureModePattern(pattScale.getValue());
                        break;
                    case COLORED_VERTICES_1D:
                        model.setTextureModeVertices1D(1540, func.getValue());
                        break;
                    case COLORED_VERTICES_3D:
                        model.setTextureModeVertices3D(1600, dens.getValue());
                        break;
                    case COLORED_FACES:
                        model.setTextureModeFaces(1550);
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
    
    
}
