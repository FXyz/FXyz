/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.fxyz.texture.NormalMap;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ImagePreviewControl extends ControlBase<Property<Boolean>> {

    @FXML
    private StackPane root;
    @FXML
    private ImageView preview;
    @FXML
    private CheckBox useImage;
    @FXML
    private CheckBox invert;

    //For use with Normal Map
    @FXML
    private Slider detailSlider;
    @FXML
    private Slider fineSlider;

    private final Image image;

    public ImagePreviewControl(final String fxml, final Property<Boolean> prop, final Image img) {
        super(fxml, prop);
        assert img != null;
        image = img;
        preview.setImage(image);
        if (fxml.equals("BumpMapPreview.fxml")) {
            detailSlider.setMin(0);
            detailSlider.setMax(50);
            detailSlider.setValue(25);

            fineSlider.setMin(0);
            fineSlider.setMax(10);
            fineSlider.setValue(5);
            try {
                if (img instanceof NormalMap) {
                    ((NormalMap) image).intensityProperty().bind(detailSlider.valueProperty());
                    ((NormalMap) image).intensityScaleProperty().bind(fineSlider.valueProperty());
                    ((NormalMap) image).invertNormalsProperty().bind(invert.selectedProperty());
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        
        controlledProperty.bindBidirectional(useImage.selectedProperty());
    }

}
