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
    protected P controlledProp;
    public ControlBase(String fxml, P prop) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setRoot(ControlBase.this);
            loader.setController(ControlBase.this);
            loader.load();
        } catch (IOException ex) {
            
            Logger.getLogger(BoolPropertyControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.controlledProp = prop;
    }

    private ControlBase() {
        this.controlledProp = null;
    }

    private ControlBase(Node... children) {
        super(children);
        this.controlledProp = null;
    }
    
}
