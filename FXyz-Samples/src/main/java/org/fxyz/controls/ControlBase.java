/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.Property;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 * @param <P>
 */
public abstract class ControlBase<P extends Property> extends StackPane{
    protected P controlledProperty;
    public ControlBase(final String fxml, final P prop) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setRoot(ControlBase.this);
            loader.setController(ControlBase.this);
            loader.load();
        } catch (IOException ex) {
            
            Logger.getLogger(CheckBoxControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.controlledProperty = prop;
    }

    private ControlBase() {
        throw new UnsupportedOperationException("Cannot assign");
    }

    private ControlBase(Node... children) {
        throw new UnsupportedOperationException("Cannot assign");
    }
    
}
