/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ColorPickControl extends ControlBase{

    @FXML
    private ColorPicker colors;
    
    public ColorPickControl(Property prop) {
        super("ColorControl.fxml", prop);
        prop.bindBidirectional(colors.valueProperty());
    }
    
}
