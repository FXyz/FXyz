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
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.fxyz.cameras.AdvancedCamera;
import org.fxyz.cameras.controllers.FPSController;
import org.fxyz.shapes.Torus;

/**
 *
 * @author Dub
 */
public class FPSControllerTest extends Application {
    
    @Override
    public void start(Stage stage) {
        Group root = new Group();
        //Make a bunch of semi random Torusesessses(toroids?) and stuff : from torustest
        Group torusGroup = new Group();
        for (int i = 0; i < 30; i++) {
            Random r = new Random();
            //A lot of magic numbers in here that just artificially constrain the math
            float randomRadius = (float) ((r.nextFloat() * 300) + 50);
            float randomTubeRadius = (float) ((r.nextFloat() * 100) + 1);
            int randomTubeDivisions = (int) ((r.nextFloat() * 64) + 1);
            int randomRadiusDivisions = (int) ((r.nextFloat() * 64) + 1);
            Color randomColor = new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble());

            Torus torus = new Torus(randomTubeDivisions, randomRadiusDivisions, randomRadius, randomTubeRadius, randomColor);
            torus.setEmissiveLightingColor(randomColor);
            torus.setEmissiveLightingOn(r.nextBoolean());
            
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

            torus.getTransforms().addAll(translate, rotateX, rotateY, rotateZ);
            //torus.getTransforms().add(translate);
            torusGroup.getChildren().add(torus);

        }
        root.getChildren().add(torusGroup);
        
        
        Scene scene = new Scene(root, 1400, 1000, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);
        
        stage.setTitle("SimpleFPSControllerTest");
        stage.setScene(scene);
        stage.show();
        //stage.setMaximized(true);
        
        FPSController controller = new FPSController();
        controller.setScene(scene);
        controller.setMouseLookEnabled(true);
        
        AdvancedCamera camera = new AdvancedCamera();
        camera.setController(controller);
        
        root.getChildren().add(camera);
        
        scene.setCamera(camera);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
