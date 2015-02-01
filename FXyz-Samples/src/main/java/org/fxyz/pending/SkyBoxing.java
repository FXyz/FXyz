/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.pending;

import javafx.scene.Node;
import javafx.scene.image.Image;
import org.fxyz.ShapeBaseSample;
import org.fxyz.scene.Skybox;

/**
 *
 * @author Dub
 */
public class SkyBoxing extends ShapeBaseSample {

    

    @Override
    protected void createMesh() {
        Skybox skyBox;
        
        final Image top = new Image(SkyBoxing.class.getResource("res/top.png").toExternalForm()),
                bottom = new Image(SkyBoxing.class.getResource("res/bottom.png").toExternalForm()),
                left = new Image(SkyBoxing.class.getResource("res/left.png").toExternalForm()),
                right = new Image(SkyBoxing.class.getResource("res/right.png").toExternalForm()),
                front = new Image(SkyBoxing.class.getResource("res/front.png").toExternalForm()),
                back = new Image(SkyBoxing.class.getResource("res/back.png").toExternalForm());


        // Load Skybox AFTER camera is initialized
        double size = 100000D;
        skyBox = new Skybox(
                top,
                bottom,
                left,
                right,
                front,
                back,
                size,
                camera
        );
        group.getChildren().add(skyBox);
       
    }

    @Override
    protected void addMeshAndListeners() {
    }
    
    @Override
    protected Node buildControlPanel() {
        return null;
    }

}
