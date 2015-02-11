/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class SectionLabel extends StackPane{
    @FXML
    private Label sectionLabel;

    public SectionLabel(String text) {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("SectionLabel.fxml"));
            loader.setRoot(SectionLabel.this);
            loader.setController(SectionLabel.this);
            loader.load();
        } catch (IOException ex) {            
            Logger.getLogger(CheckBoxControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        sectionLabel.setText(text);
    }
    
    
}
