/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.samples;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import org.fxyz.ShapeBaseSample;
import org.fxyz.controls.ColorPickControl;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.ControlPanel;
import org.fxyz.controls.CullFaceControl;
import org.fxyz.controls.ImagePreviewControl;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.shapes.primitives.SpheroidMesh;
import org.fxyz.texture.NormalMap;

/**
 *
 * @author Dub
 */
public class Spheroids extends ShapeBaseSample {

    private final PhongMaterial material = new PhongMaterial();
    private final SpheroidMesh sm = new SpheroidMesh();

    private final DoubleProperty minRad = new SimpleDoubleProperty(this, "Minor Radius : ", 25.0);
    private final DoubleProperty majRad = new SimpleDoubleProperty(this, "Major Radius : ", 50.0);
    private final IntegerProperty divs = new SimpleIntegerProperty(this, "Divisions :", 64);

    private final ObjectProperty<Color> diffColor = new SimpleObjectProperty<>(this, "Diffuse Color :", Color.AQUAMARINE);
    private final ObjectProperty<Color> specColor = new SimpleObjectProperty<>(this, "Specular Color :", Color.TRANSPARENT);

    private final ObjectProperty<Image> diffuseImage = new SimpleObjectProperty<>(this, "Diffuse Map :", null);
    private final ObjectProperty<Image> bumpImage = new SimpleObjectProperty<>(this, "Normal Map :", null);
    private final ObjectProperty<Image> specImage = new SimpleObjectProperty<>(this, "Specula Map :", null);
    private final ObjectProperty<Image> illumImage = new SimpleObjectProperty<>(this, "Illumination Map :", null);

    private final ObjectProperty<CullFace> culling = new SimpleObjectProperty<>(this, "Cull Mode :", CullFace.BACK);
    private final ObjectProperty<DrawMode> wireMode = new SimpleObjectProperty<>(this, "Draw Mode :", DrawMode.FILL);

    private final BooleanProperty useDiffImage = new SimpleBooleanProperty(false);
    private final BooleanProperty useSpecImage = new SimpleBooleanProperty(false);
    private final BooleanProperty useNormImage = new SimpleBooleanProperty(false);
    private final BooleanProperty useIlluImage = new SimpleBooleanProperty(false);

    @Override
    protected void createMesh() {
        try{
        final Image d = new Image(getClass().getResourceAsStream("res/diffuse-map.jpg")),
              b = new NormalMap(new Image(getClass().getResourceAsStream("res/diffuse-map.jpg"))),
              s = new Image(getClass().getResourceAsStream("res/specular-map.jpg")),
              i = new Image(getClass().getResourceAsStream("res/illumination-map.jpg"));
        
              diffuseImage.set(d);
              bumpImage.set(b);
              specImage.set(s);
              illumImage.set(i);
        }catch(Exception e){
        
        }
        sm.setDepthTest(DepthTest.ENABLE);
        sm.setCullFace(CullFace.BACK);
        sm.setDivisions(divs.get());
        sm.setMinorRadius(minRad.doubleValue());
        sm.setMajorRadius(majRad.doubleValue());
        sm.setMaterial(material);
        
        material.diffuseColorProperty().bindBidirectional(diffColor);
        material.specularColorProperty().bindBidirectional(specColor);
    }

    @Override
    protected void addMeshAndListeners() {
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
        useDiffImage.addListener(i->{
            if(useDiffImage.get()){
                material.setDiffuseMap(diffuseImage.get());
            }else if(!useDiffImage.get()){                
                material.setDiffuseMap(null);
            }
        });
        useSpecImage.addListener(i->{
            if(useSpecImage.get()){
                material.setSpecularMap(specImage.get());
            }else if(!useSpecImage.get()){                
                material.setSpecularMap(null);
            }
        });
        useNormImage.addListener(i->{
            if(useNormImage.get()){
                material.setBumpMap(bumpImage.get());
            }else if(!useNormImage.get()){                
                material.setBumpMap(null);
            }
        });
        useIlluImage.addListener(i->{
        });
        
        culling.addListener(c->{
            sm.setCullFace(culling.get());
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

        CullFaceControl cfc = ControlFactory.buildCullFaceControl(culling);
        ColorPickControl difColor = ControlFactory.buildColorControl(diffColor);
        ColorPickControl spcColor = ControlFactory.buildColorControl(specColor);
        
        ControlCategory geom = ControlFactory.buildCategory("Geometry");
        geom.addControls(div, minor, major, cfc, difColor, spcColor);
        //==============================================================
        ControlCategory mat = ControlFactory.buildCategory("Materials");        
        ImagePreviewControl diffPrev = ControlFactory.buildImageToggle("DiffMapPreview.fxml", useDiffImage, diffuseImage.get());
        ImagePreviewControl specPrev = ControlFactory.buildImageToggle("SpecMapPreview.fxml", useSpecImage, specImage.get());
        ImagePreviewControl bumpPrev = ControlFactory.buildImageToggle("BumpMapPreview.fxml", useNormImage, bumpImage.get());
        mat.addControls(diffPrev, specPrev, bumpPrev);
        //==============================================================
        ControlPanel cPanel = ControlFactory.buildControlPanel(geom);
        cPanel.getPanes().add(mat);
        cPanel.setExpandedPane(geom);

        return cPanel;
    }

}
