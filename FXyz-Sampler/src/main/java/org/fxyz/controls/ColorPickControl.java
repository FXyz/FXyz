/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ColorPickControl extends ControlBase{

    @FXML
    private ColorPicker colors;
    @FXML
    private Label title;
    
    public ColorPickControl(Property<Color> prop, String name) {
        super("ColorControl.fxml", prop);
        colors.valueProperty().addListener(new WeakInvalidationListener(e->{
            prop.setValue(colors.getValue());
        }));        
        title.setText(name);
    }
    
}
