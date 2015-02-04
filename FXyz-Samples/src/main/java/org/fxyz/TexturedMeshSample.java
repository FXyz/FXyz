/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz;

import java.io.File;
import java.io.FileInputStream;
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
import org.fxyz.geometry.DensityFunction;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.TexturedMesh;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public abstract class TexturedMeshSample extends ShapeBaseSample<TexturedMesh>{

    protected final DoubleProperty pattScale = new SimpleDoubleProperty(this, "Pattern Scale: ", 2.0d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setTextureModePattern(pattScale.doubleValue());
            }
        }
    };
    protected final Property<DensityFunction<Point3D>> dens = new SimpleObjectProperty<>(p -> (double)(p.x * p.y * p.z));
    protected final DoubleProperty densMax = new SimpleDoubleProperty(this, "Density Scale: ") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setDensity(dens.getValue());
            }
        }
    };
    //standard
    protected final StringProperty diffMapPath = new SimpleStringProperty(this, "imagePath", "");
    protected final Property<Boolean> useDiffMap = new SimpleBooleanProperty(this, "Use PhongMaterial", false) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                if (diffMapPath.get().isEmpty()) {
                    //load default
                    model.setTextureModeImage(getClass().getResource("samples/res/LaminateSteel.jpg").toExternalForm());
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
    protected final Property<TriangleMeshHelper.SectionType> sectionType = new SimpleObjectProperty<TriangleMeshHelper.SectionType>(model, "secType", TriangleMeshHelper.SectionType.TRIANGLE) {
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
                    case PATTERN:
                    model.setTextureModePattern(pattScale.getValue());
                        break;
                    case COLORED_VERTICES_1D:
                    model.setTextureModeVertices1D(colors.getValue() * colors.getValue(), t -> t * t);
                        break;
                    case COLORED_VERTICES_3D:
                    model.setTextureModeVertices3D(colors.getValue() * colors.getValue(), dens.getValue());
                        break;
                    case COLORED_FACES:
                    model.setTextureModeFaces(colors.getValue() * colors.getValue());
                        break;
                }                
            }
        }
    };

    protected final ObjectProperty<Color> colorBinding = new SimpleObjectProperty<>(Color.BROWN);
    protected final IntegerProperty colors = new SimpleIntegerProperty(model, "Color :", 1530) {
        @Override
        protected void invalidated() {
            super.invalidated();
            
            if (model != null) {
                colorBinding.set(Color.hsb(360 * (1d - colors.get() / 1530d), 1, 1));                
            }
        }
    };    
    
}
