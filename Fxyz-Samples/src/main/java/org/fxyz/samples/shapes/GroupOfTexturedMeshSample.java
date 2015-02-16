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
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.TexturedMesh;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper;

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
                                s.setTextureModeImage(diffMapPath.get());
                                break;
                            case PATTERN:
                                s.setTextureModePattern(pattScale.getValue());
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
                    .forEach(s->{
                        if(s.getTextureType().equals(TriangleMeshHelper.TextureType.NONE)) {
                            s.setDiffuseColor(c1);
                        }
                    });
            }
        });
        pattScale.addListener((obs,p0,p1)->{
            if (model != null){
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .forEach(s->{
                        if(s.getTextureType().equals(TriangleMeshHelper.TextureType.PATTERN)) {
                            s.setPatternScale(p1.doubleValue());
                        }
                    });
            }
        });
        dens.addListener((obs,f0,f1)->{
            if (model != null){
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .forEach(s->{
                        if(s.getTextureType().equals(TriangleMeshHelper.TextureType.COLORED_VERTICES_3D)) {
                            s.setDensity(f1);
                        }
                    });
            }
        });
        func.addListener((obs,f0,f1)->{
            if (model != null){
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .forEach(s->{
                        if(s.getTextureType().equals(TriangleMeshHelper.TextureType.COLORED_VERTICES_1D)) {
                            s.setFunction(f1);
                        }
                    });
            }
        });
        useDiffMap.addListener((obs,b0,b1)->{
            if (model != null){
                model.getChildren().stream().filter(TexturedMesh.class::isInstance)
                    .map(TexturedMesh.class::cast)
                    .forEach(s->{
                        if(s.getTextureType().equals(TriangleMeshHelper.TextureType.IMAGE)) {
//                            if (diffMapPath.get().isEmpty()) {
                                //load default
                                diffMapPath.set(getClass().getResource("/org/fxyz/samples/res/LaminateSteel.jpg").toExternalForm());
//                            } else {
//                                try { // should be given the string from filechooser
//                                    material.setDiffuseMap(new Image(new FileInputStream(new File(diffMapPath.get()))));
//                                } catch (Exception e) {
//                                    e.printStackTrace(System.err);
//                                }
//                            }
                        }
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
    protected final StringProperty diffMapPath = new SimpleStringProperty(this, "imagePath", getClass().getResource("/org/fxyz/samples/res/LaminateSteel.jpg").toExternalForm());
    protected final Property<Boolean> useDiffMap = new SimpleBooleanProperty(this, "Use PhongMaterial", false) {};
    
    /*
     TriangleMeshHelper.TextureType.PATTERN 
    */
    protected final DoubleProperty pattScale = new SimpleDoubleProperty(this, "Pattern Scale: ", 2.0d) {};
    
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
