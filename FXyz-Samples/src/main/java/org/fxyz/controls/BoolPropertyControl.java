/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class BoolPropertyControl extends StackPane{

    private final BooleanProperty property;
    
    public BoolPropertyControl(BooleanProperty prop) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BoolPropController.fxml"));
            loader.setRoot(BoolPropertyControl.this);
            loader.setController(BoolPropertyControl.this);
            loader.load();
        } catch (IOException ex) {
            
            Logger.getLogger(BoolPropertyControl.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.property = prop;
        
    }

    @FXML
    private CheckBox checkBox;
    @FXML
    private StackPane spacer;
    @FXML
    private Label boolPropTitle;
    
    public void loadBindings() {
        property.bind(checkBox.selectedProperty());
        boolPropTitle.setText(!property.getName().isEmpty() ? property.getName() : "propName");
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (property.isBound() || boolPropTitle.textProperty().isBound()) {
                property.unbind();
                boolPropTitle.textProperty().unbind();
            }
            checkBox = null;
            spacer = null;
            boolPropTitle = null;
        } finally {
            super.finalize(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    
}
