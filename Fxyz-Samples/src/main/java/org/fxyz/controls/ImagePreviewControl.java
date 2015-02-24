/**
* ImagePreviewControl.java
*
* Copyright (c) 2013-2015, F(X)yz
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* * Neither the name of the organization nor the
* names of its contributors may be used to endorse or promote products
* derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.fxyz.controls;

import java.util.Collection;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ImagePreviewControl extends ControlBase<Property<TextureImage>> {

    @FXML
    private ImageView preview;
    @FXML
    private ComboBox<TextureImage> imageSelector;
    

    public ImagePreviewControl(final Property<TextureImage> img, String name, final Collection<TextureImage> items) {
        super("/org/fxyz/controls/ImageMapPreview.fxml", img);
       
        imageSelector.getItems().addAll(items);
        imageSelector.getSelectionModel().selectedItemProperty().addListener((obs,t,t1)->{
            preview.setImage(t1.getImage());
        });
        imageSelector.getSelectionModel().selectFirst();
        imageSelector.setCellFactory(new Callback<ListView<TextureImage>,ListCell<TextureImage>>() {
            
            @Override
            public ListCell<TextureImage> call(ListView<TextureImage> param) {
                return new ListCell<TextureImage>(){
                    {
                        this.setFocusTraversable(false);
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
                    protected void updateItem(TextureImage item, boolean empty) {
                        if(item != null && !empty){
                            super.updateItem(item, empty);  
                            final ImageView view = new ImageView(item.getImage());
                            view.setFitHeight(75);
                            view.setPreserveRatio(true);
                            view.setSmooth(true);
                            super.setGraphic(view);                            
                            super.setText(item.getName());
                        } else {
                            setGraphic(null);
                            setText(null);
                        }
                    }
                    
                };
            }
        });
        
//        preview.imageProperty().bind(imageSelector.getSelectionModel().getSelectedItem().imageProperty());
        if(controlledProperty!=null){
            controlledProperty.bind(imageSelector.valueProperty());
        }
        preview.setOnMouseClicked(e->imageSelector.show());
    }

    public final ComboBox<TextureImage> getImageSelector() {
        return imageSelector;
    }

    
}
