/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.pending;

import java.util.Random;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.fxyz.scene.SimpleFPSCamera;
import org.fxyz.shapes.Cone;

/**
 *
 * @author Birdasaur
 * adapted Dub's Capsule Test merged with my original Cone Test
 */
public class Cones{// extends FXyzSample {
    
    //@Override
    public Node getSample(){
        Group root = new Group();
        Group coneGroup = new Group();        
        for (int i = 0; i < 100; i++) {
            Random r = new Random();
            //A lot of magic numbers in here that just artificially constrain the math
            float randomRadius = (float) ((r.nextFloat()*100) + 25);
            float randomHeight = (float) ((r.nextFloat()*300)+ 75);
            int randomDivisions = (int) ((r.nextFloat()*50) + 5);
            Color randomColor = new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble());
            
            Cone cone = new Cone(randomDivisions, randomRadius, randomHeight, randomColor);               
            cone.setEmissiveLightingColor(randomColor);
            cone.setEmissiveLightingOn(r.nextBoolean());
            cone.setDrawMode(r.nextBoolean() ? DrawMode.FILL : DrawMode.LINE);
            
            double translationX = Math.random() * 1024;
            if (Math.random() >= 0.5) {
                translationX *= -1;
            }
            double translationY = Math.random() * 1024;
            if (Math.random() >= 0.5) {
                translationY *= -1;
            }
            double translationZ = Math.random() * 1024;
            if (Math.random() >= 0.5) {
                translationZ *= -1;
            }
            Translate translate = new Translate(translationX, translationY, translationZ);
            Rotate rotateX = new Rotate(Math.random() * 360, Rotate.X_AXIS);
            Rotate rotateY = new Rotate(Math.random() * 360, Rotate.Y_AXIS);
            Rotate rotateZ = new Rotate(Math.random() * 360, Rotate.Z_AXIS);

            cone.getTransforms().addAll(translate, rotateX, rotateY, rotateZ);
            
            coneGroup.getChildren().add(cone);
        }
        root.getChildren().add(coneGroup);
                
        SimpleFPSCamera camera = new SimpleFPSCamera();
        
        SubScene scene = new SubScene(new StackPane(root), 800, 600, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);
        scene.setCamera(camera.getCamera());
        
        camera.loadControlsForSubScene(scene);
        
        root.getChildren().add(camera);
        
        //testing for binding width and height
        if(scene.getParent() != null){
            scene.widthProperty().bind(((Pane)scene.getParent()).prefWidthProperty());
            scene.heightProperty().bind(((Pane)scene.getParent()).prefHeightProperty());
        }
        return new StackPane(scene);
    }

    //@Override
    public String getSampleName() {
        return getClass().getSimpleName().concat(" Sample");
    }

    //@Override
    public Node getPanel(Stage stage) {
        return null;
    }

    //@Override
    public String getJavaDocURL() {
        return "";
    }
}