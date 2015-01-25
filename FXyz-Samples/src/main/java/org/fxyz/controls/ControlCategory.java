/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ControlCategory  extends TitledPane{
    
    @FXML
    private VBox controls;
    
    private final List<Node> controlItems;

    private ControlCategory() {
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ControlPanelTitlePane.fxml"));
            loader.setRoot(ControlCategory.this);
            loader.setController(ControlCategory.this);
            loader.load();
        } catch (IOException ex) {
            
            Logger.getLogger(ControlCategory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.controlItems = new ArrayList<>();
    }    
    
    public ControlCategory(String title) {
        this();
        setText(title);
        //EasyBind.listBind(controlItems, controls.getChildren());
    }
        
    public void addControl(ControlBase n){
        controls.getChildren().add(n);
    }
    public void addControls(ControlBase ... ctrls){
        controls.getChildren().addAll(ctrls);
    }
    public void removeControl(Node n){
        controls.getChildren().removeAll(n);
    }
    
    
    
}
