/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import java.util.Collection;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ComboBoxControl<T> extends ControlBase<Property<T>>{

    public ComboBoxControl(final String lbl, final Property<T> p, final Collection<T> items) {
        super("ComboBoxControl.fxml", p);
        title.setText(lbl);
        selection.getItems().addAll(items);        
        controlledProperty.bind(selection.getSelectionModel().selectedItemProperty());
        selection.getSelectionModel().select(0);
    }   
    
    @FXML
    private ComboBox<T> selection;
    @FXML
    private Label title;

    public ComboBox getComboBox() {
        return selection;
    }

    
    
}
