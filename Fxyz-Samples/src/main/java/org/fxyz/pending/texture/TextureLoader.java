/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.pending.texture;

import java.io.IOException;
import javafx.scene.image.Image;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class TextureLoader {
    private TextureLoader(){}
    
    public static final Image loadTextureImage(Class<?> clazz, String path)throws IOException{
        //System.out.println(clazz.getClassLoader().getResourceAsStream(path));
        return new Image(clazz.getResource(path).toExternalForm());
    }
}
