/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.fxyz.scene.CameraView;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class CameraViewControl extends ControlBase<BooleanProperty>{
    @FXML
    private Label label;
    @FXML
    private CheckBox controlsEnabled;
    @FXML
    private VBox container;
    
    private final CameraView view;

    public CameraViewControl(BooleanProperty enabled, SubScene subScene, StackPane parentPane) {
        super("CameraViewControl.fxml", enabled);
        
        this.view = new CameraView(subScene);
        this.view.setFitWidth(200);
        this.view.setFitHeight(150);
        this.view.setSmooth(true);
        
        this.controlsEnabled.selectedProperty().addListener(l->{
            if(controlsEnabled.isSelected()){
                view.setFirstPersonNavigationEabled(true);
            }else if(!controlsEnabled.isSelected()){
                view.setFirstPersonNavigationEabled(false);
            }
        });
        
        container.getChildren().add(1, view);
        
        final ComboBox<Pos> positions = new ComboBox<>();
        positions.getItems().addAll(Pos.TOP_LEFT, Pos.TOP_RIGHT, Pos.BOTTOM_LEFT, Pos.BOTTOM_RIGHT);
        positions.getSelectionModel().selectLast();
        positions.valueProperty().addListener(l->{
            switch(positions.getValue()){
                case TOP_LEFT:
                    StackPane.setAlignment(CameraViewControl.this, Pos.TOP_LEFT);
                    break;
                case TOP_RIGHT:
                    StackPane.setAlignment(CameraViewControl.this, Pos.TOP_RIGHT);
                    break;
                case BOTTOM_LEFT:
                    StackPane.setAlignment(CameraViewControl.this, Pos.BOTTOM_LEFT);
                    break;
                case BOTTOM_RIGHT:
                    StackPane.setAlignment(CameraViewControl.this, Pos.BOTTOM_RIGHT);
                    break;
            }
        });
        positions.setPrefSize(USE_COMPUTED_SIZE, USE_PREF_SIZE);
        container.getChildren().add(positions);
        
        parentPane.getChildren().add(CameraViewControl.this);
        
        enabled.addListener(l->{
            if(enabled.getValue()){
                view.startViewing();
            }else{
                
            }
        });
        StackPane.setMargin(CameraViewControl.this, new Insets(120));
    }

       
    
}
