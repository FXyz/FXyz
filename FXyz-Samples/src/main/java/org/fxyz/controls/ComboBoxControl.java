/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import java.util.Collection;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ComboBoxControl<T> extends ControlBase<ObjectProperty<?>>{

    public ComboBoxControl(final ObjectProperty<T> p, final Collection<T> items) {
        super("ComboBoxControl.fxml", p);
        selection.getItems().addAll(items);
        selection.getSelectionModel().select(0);
    }   
    
    @FXML
    private ComboBox<T> selection;

    public ComboBox getComboBox() {
        return selection;
    }

    
    
}
