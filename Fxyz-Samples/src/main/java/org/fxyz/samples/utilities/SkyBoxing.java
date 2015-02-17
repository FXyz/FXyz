/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.samples.utilities;

import javafx.scene.Node;
import javafx.scene.image.Image;
import org.fxyz.samples.shapes.ShapeBaseSample;
import org.fxyz.scene.Skybox;

/**
 *
 * @author Dub
 */
public class SkyBoxing extends ShapeBaseSample {

    public static void main(String[] args){SkyBoxing.launch(args);}

    @Override
    protected void createMesh() {
        final Image top = new Image(SkyBoxing.class.getResource("/org/fxyz/samples/res/top.png").toExternalForm()),
                bottom = new Image(SkyBoxing.class.getResource("/org/fxyz/samples/res/bottom.png").toExternalForm()),
                left = new Image(SkyBoxing.class.getResource("/org/fxyz/samples/res/left.png").toExternalForm()),
                right = new Image(SkyBoxing.class.getResource("/org/fxyz/samples/res/right.png").toExternalForm()),
                front = new Image(SkyBoxing.class.getResource("/org/fxyz/samples/res/front.png").toExternalForm()),
                back = new Image(SkyBoxing.class.getResource("/org/fxyz/samples/res/back.png").toExternalForm());


        // Load Skybox AFTER camera is initialized
        double size = 100000D;
        model = new Skybox(
                top,
                bottom,
                left,
                right,
                front,
                back,
                size,
                camera
        );
       
    }

    @Override
    protected void addMeshAndListeners() {
    }
    
    @Override
    protected Node buildControlPanel() {
        return null;
    }

}
