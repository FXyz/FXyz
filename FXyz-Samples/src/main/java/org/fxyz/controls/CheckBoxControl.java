/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class CheckBoxControl extends ControlBase<Property<Boolean>>{

    
    public CheckBoxControl(Property<Boolean> prop) {
        super("CheckBoxControl.fxml", prop);
        loadBindings();
    }

    @FXML
    private CheckBox checkBox;
    @FXML
    private StackPane spacer;
    @FXML
    private Label boolPropTitle;
    public final void loadBindings() {
        controlledProperty.bind(checkBox.selectedProperty());
        boolPropTitle.setText(!controlledProperty.getName().isEmpty() ? controlledProperty.getName() : "Empty Name");
    }
    
}
