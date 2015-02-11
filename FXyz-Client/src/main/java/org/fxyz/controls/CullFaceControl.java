/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.shape.CullFace;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class CullFaceControl extends ControlBase{
    @FXML
    private ComboBox<CullFace> selection;

    public CullFaceControl(final Property<CullFace> prop) {        
        super("CullFaceControl.fxml", prop);        
        prop.bind(selection.getSelectionModel().selectedItemProperty());
        selection.getSelectionModel().select(0);
    }

       
    
}
