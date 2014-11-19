/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.tests;

import java.util.Random;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.fxyz.cameras.AdvancedCamera;
import org.fxyz.cameras.controllers.FPSController;
import org.fxyz.shapes.Cone;

/**
 *
 * @author Birdasaur
 * @adapted Dub's Capsule Test merged with my original Cone Test
 */
public class ConeTest extends Application {
        
    private AdvancedCamera camera;
    private FPSController controller;
    
    private final Group root = new Group();
   
    @Override
    public void start(Stage stage) {
        
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
                
        camera = new AdvancedCamera();
        controller = new FPSController();
        
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setFieldOfView(42);
        camera.setController(controller);
        
        Scene scene = new Scene(new StackPane(root), 1024, 668, true, SceneAntialiasing.BALANCED);
        scene.setCamera(camera);
        scene.setFill(Color.BLACK);
        
        controller.setScene(scene);
                
        stage.setTitle("Random Cones!");
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(false);
        stage.setFullScreenExitHint("");
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}