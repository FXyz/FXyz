/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.samples;

import java.util.Random;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.fxyz.shapes.Capsule;

/**
 *
 * @author Dub
 */
public class Capsules{// extends FXyzSample{
        
   
    //@Override
    public Node getSample() {
        final Group root = new Group();
        Group capsuleGroup = new Group();        
        for (int i = 0; i < 50; i++) {
            Random r = new Random();
            //A lot of magic numbers in here that just artificially constrain the math
            float randomRadius = (float) ((r.nextFloat() * 100) + 25);
            float randomHeight = (float) ((r.nextFloat() * 300) + 75);
            Color randomColor = new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble());
            
            Capsule cap = new Capsule(randomRadius, randomHeight, randomColor);               
            cap.setEmissiveLightingColor(randomColor);
            cap.setEmissiveLightingOn(r.nextBoolean());
            cap.setDrawMode(r.nextBoolean() ? DrawMode.FILL : DrawMode.LINE);
            
            double translationX = Math.random() * 1024 * 1.95;
            if (Math.random() >= 0.5) {
                translationX *= -1;
            }
            double translationY = Math.random() * 1024 * 1.95;
            if (Math.random() >= 0.5) {
                translationY *= -1;
            }
            double translationZ = Math.random() * 1024 * 1.95;
            if (Math.random() >= 0.5) {
                translationZ *= -1;
            }
            Translate translate = new Translate(translationX, translationY, translationZ);
            Rotate rotateX = new Rotate(Math.random() * 360, Rotate.X_AXIS);
            Rotate rotateY = new Rotate(Math.random() * 360, Rotate.Y_AXIS);
            Rotate rotateZ = new Rotate(Math.random() * 360, Rotate.Z_AXIS);

            cap.getTransforms().addAll(translate, rotateX, rotateY, rotateZ);
            
            capsuleGroup.getChildren().add(cap);
        }
        root.getChildren().add(capsuleGroup);
                
        
        SubScene scene = new SubScene(new StackPane(root), 800, 600, true, SceneAntialiasing.BALANCED);
        scene.setCamera(new PerspectiveCamera(true));
        scene.setFill(Color.BLACK);
        
        return scene;
    }
   
    
    //@Override
    public String getSampleName() {
        return getClass().getSimpleName().concat(" Sample");
    }

    //@Override
    public Node getPanel(Stage stage) {
        return getSample();
    }

    //@Override
    public String getJavaDocURL() {
        return null;
    }
    
}
