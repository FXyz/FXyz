/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ControlBasePane extends Accordion{
    
    public ControlBasePane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ControlBasePane.fxml"));
            loader.setRoot(ControlBasePane.this);
            loader.setController(ControlBasePane.this);
            loader.load();
        } catch (IOException ex) {
            
            Logger.getLogger(CheckBoxControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
