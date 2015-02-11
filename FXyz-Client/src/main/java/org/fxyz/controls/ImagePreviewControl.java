/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

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
    private final Property<Image> image;

    public ImagePreviewControl(final Property<Boolean> prop, final Property<? extends Image> img, String name) {
        super("ImageMapPreview.fxml", prop);
        assert img != null;
        this.image = new SimpleObjectProperty("image");
        useImage.setText(name);
        image.bindBidirectional((Property<Image>) img);
        preview.imageProperty().bind(image);
        controlledProperty.bind(useImage.selectedProperty());
    }

}
