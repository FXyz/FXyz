/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 *
 * @author José Pereda Llamas
 * Created on 22-feb-2015 - 15:08:23
 */
public class TextureImage {
    
    private final ObjectProperty<Image> image;
    private final StringProperty name;

    TextureImage(String imageSrc, String name) {
        this.image=new SimpleObjectProperty<>(new Image(imageSrc));
        this.name=new SimpleStringProperty(name);
    }

    public Image getImage() {
        return image.get();
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }
    
    public ObjectProperty<Image> imageProperty(){
        return image;
    }
    
    public StringProperty nameProperty(){
        return name;
    }

    @Override
    public String toString() {
        return name.get(); 
    }
    
}
