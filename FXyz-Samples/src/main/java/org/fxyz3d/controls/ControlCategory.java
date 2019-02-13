/**
 * ControlCategory.java
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

package org.fxyz3d.controls;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ControlCategory  extends TitledPane{
    
    @FXML
    private ListView<StackPane> listView;
    
    private final ObservableList<StackPane> controlItems;

    private ControlCategory() {
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/fxyz3d/controls/ControlPanelTitlePane.fxml"));
            loader.setRoot(ControlCategory.this);
            loader.setController(ControlCategory.this);
            loader.load();
        } catch (IOException ex) {            
            Logger.getLogger(ControlCategory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.controlItems = FXCollections.observableArrayList();
        this.listView.setItems(controlItems);
        
        //this.getStyleClass().add("fxyz3d-control-category");
    }    
    
    public ControlCategory(String title) {
        this();
        this.setText(title);
        this.setFocusTraversable(false);
        //EasyBind.listBind(controlItems, controls.getChildren());       
        this.listView.setCellFactory(new Callback<ListView<StackPane>,ListCell<StackPane>>() {
            
            @Override
            public ListCell<StackPane> call(ListView<StackPane> param) {
                return new ListCell<StackPane>(){
                    {
                        this.setFocusTraversable(false);
                        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    }
                    @Override
                    public boolean isResizable() {
                        return false; //To change body of generated methods, choose Tools | Templates.
                    }
                    
                    @Override
                    public void updateSelected(boolean selected) {
                        //do nothing...
                    }
                    
                    @Override
                    protected void updateItem(StackPane item, boolean empty) {
                        if(item != null && !empty){
                            
                            super.updateItem(item, empty);                            
                            super.setGraphic(item);                            
                        }
                    }
                    
                };
            }
        });
    }
        
    public void addControl(StackPane n){
        if(!controlItems.contains(n)){
            controlItems.add(n);
        }
    }
    public void addControls(StackPane ... ctrls){
        if(!controlItems.containsAll(Arrays.asList(ctrls))){
            controlItems.addAll(ctrls);
        }
    }

    public void removeControl(Node ...  n){
        controlItems.removeAll(Arrays.asList(n));
    }
    
    public void removeIf(Predicate<StackPane> filter) {
        controlItems.removeIf(filter);
    }
    
}
