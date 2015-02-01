/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.samples;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import org.fxyz.ShapeBaseSample;
import org.fxyz.controls.ComboBoxControl;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.ControlPanel;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.shapes.primitives.SpheroidMesh;
import org.fxyz.texture.NormalMap;

/**
 *
 * @author Dub
 */
public class Spheroids extends ShapeBaseSample {

    private final SpheroidMesh sm = new SpheroidMesh();

    private final DoubleProperty minRad = new SimpleDoubleProperty(this, "Minor Radius : ", 25.0);
    private final DoubleProperty majRad = new SimpleDoubleProperty(this, "Major Radius : ", 50.0);
    private final IntegerProperty divs = new SimpleIntegerProperty(this, "Divisions :", 64);

    private final Property<Color> diffColor = new SimpleObjectProperty<>(this, "Diffuse Color :", Color.GAINSBORO);
    private final Property<Color> specColor = new SimpleObjectProperty<>(this, "Specular Color :", Color.WHITE);

    private final Property<Image> diffuseImage = new SimpleObjectProperty<>(this, "Diffuse Map :", null);
    private final Property<NormalMap> bumpImage = new SimpleObjectProperty<>(this, "Normal Map :", null);
    private final Property<Image> specImage = new SimpleObjectProperty<>(this, "Specula Map :", null);
    private final Property<Image> illumImage = new SimpleObjectProperty<>(this, "Illumination Map :", null);

    private final Property<CullFace> culling = new SimpleObjectProperty<>(this, "Cull Mode :", CullFace.NONE);
    private final Property<DrawMode> wireMode = new SimpleObjectProperty<>(this, "Draw Mode :", DrawMode.FILL);

    private final BooleanProperty useDiffImage = new SimpleBooleanProperty(false);
    private final BooleanProperty useSpecImage = new SimpleBooleanProperty(false);
    private final BooleanProperty useNormImage = new SimpleBooleanProperty(false);
    private final BooleanProperty useIlluImage = new SimpleBooleanProperty(false);
    private final BooleanProperty useMaterial = new SimpleBooleanProperty(true);
    @Override
    protected void createMesh() {
        
        sm.setDepthTest(DepthTest.ENABLE);
        wireMode.bindBidirectional(sm.drawModeProperty());
        sm.setDivisions(divs.get());
        sm.setMinorRadius(minRad.doubleValue());
        sm.setMajorRadius(majRad.doubleValue());
        sm.setMaterial(material);

    }

    @Override
    protected void addMeshAndListeners() {
        try {
            final Image d = new Image(getClass().getResource("res/diffuse-map.jpg").openStream());
            final Image s = new Image(getClass().getResource("res/specular-map.jpg").openStream());
            final Image i = new Image(getClass().getResource("res/illumination-map.jpg").openStream());
            final NormalMap b = new NormalMap(5, 10, true, d);
            diffuseImage.setValue(d);
            bumpImage.setValue(b);
            specImage.setValue(s);
            illumImage.setValue(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        material.diffuseColorProperty().bindBidirectional(diffColor);
        material.specularColorProperty().bindBidirectional(specColor);
        
        useMaterial.addListener((e)->{
            if(useMaterial.get()){
                if(sm.getMaterial() != null){
                    sm.setMaterial(null);
                }else if(sm.getMaterial() == null){
                    sm.setMaterial(material);
                }
            }
        });
        group.getChildren().addAll(sm);
        camera.setTranslateZ(-250);

        minRad.addListener(i -> {
            sm.setMinorRadius(minRad.doubleValue());
        });
        majRad.addListener(i -> {
            sm.setMajorRadius(majRad.doubleValue());
        });
        divs.addListener(i -> {
            sm.setDivisions(divs.intValue());
        });
        useDiffImage.addListener(i -> {
            if (useDiffImage.get()) {
                material.setDiffuseMap(diffuseImage.getValue());
            } else if (!useDiffImage.get()) {
                material.setDiffuseMap(null);
            }
        });
        useSpecImage.addListener(i -> {
            if (useSpecImage.get()) {
                material.setSpecularMap(specImage.getValue());
            } else if (!useSpecImage.get()) {
                material.setSpecularMap(null);
            }
        });
        useNormImage.addListener(i -> {
            if (useNormImage.get()) {
                material.setBumpMap(bumpImage.getValue());
            } else if (!useNormImage.get()) {
                material.setBumpMap(null);
            }
        });
        useIlluImage.addListener(i -> {
        });

        culling.addListener(c -> {
            sm.setCullFace(culling.getValue());
        });
        
      
    }

    @Override
    public Node getControlPanel() {
        NumberSliderControl major = ControlFactory.buildNumberSlider(majRad, 10, 110);
        major.getSlider().setBlockIncrement(2);
        major.getSlider().setMinorTickCount(50);
        major.getSlider().setMajorTickUnit(1);

        NumberSliderControl minor = ControlFactory.buildNumberSlider(minRad, 10, 110);
        NumberSliderControl div = ControlFactory.buildNumberSlider(divs, 4, 64);

        ComboBoxControl cfc = ControlFactory.buildCullFaceControl(culling);

        ControlCategory geom = ControlFactory.buildCategory("Geometry");
        geom.addControls(div, minor, major, cfc, ControlFactory.buildDrawModeControl(wireMode));
        //==============================================================
        ControlCategory images = ControlFactory.buildMaterialMapCategory(
                diffuseImage, useDiffImage,
                bumpImage, useNormImage,
                specImage, useSpecImage,
                illumImage, useIlluImage
        );
        //==============================================================
        ControlPanel cPanel = ControlFactory.buildControlPanel(ControlFactory.buildMeshViewCategory(useMaterial, wireMode, culling, diffColor, specColor), geom);
        cPanel.getPanes().add(images);
        cPanel.setExpandedPane(geom);

        return cPanel;
    }

    @Override
    protected Node buildControlPanel() {
        return null;
    }

}
