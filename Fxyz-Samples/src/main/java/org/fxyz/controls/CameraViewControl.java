/**
 * CameraViewControl.java
 *
 * Copyright (c) 2013-2016, F(X)yz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of F(X)yz, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL F(X)yz BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
        super("/org/fxyz/controls/CameraViewControl.fxml", enabled);
        
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
