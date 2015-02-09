/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class FileLoadControl  extends StackPane{
    @FXML
    private Label prefixLb;
    @FXML
    private TextField imagePathTf;
  

    @FXML
    private void chooseImage(ActionEvent event) {
    }
    
}
