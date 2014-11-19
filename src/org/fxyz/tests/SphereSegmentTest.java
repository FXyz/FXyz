/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.tests;

import java.util.Random;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.fxyz.shapes.SphereSegment;

/**
 *
 * @author Sean
 */
public class SphereSegmentTest extends Application {
    private PerspectiveCamera camera;
    private final double sceneWidth = 600;
    private final double sceneHeight = 600;
    private double cameraDistance = 5000;
    private double scenex, sceney, scenez = 0;
    private double fixedXAngle, fixedYAngle, fixedZAngle = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);    
    private final DoubleProperty angleZ = new SimpleDoubleProperty(0);    

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group sceneRoot = new Group();
        Scene scene = new Scene(sceneRoot, sceneWidth, sceneHeight);
        scene.setFill(Color.BLACK);
        camera = new PerspectiveCamera(true);        
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-1000);
        scene.setCamera(camera);
        //Make a bunch of semi random sphere segments and stuff
        Group sphereGroup = new Group();
        for(int i=0;i<30;i++){
            Random r = new Random();
            //A lot of magic numbers in here that just artificially constrain the math
            float randomRadius = (float) ((r.nextFloat()*150) + 10);
            float randomThetaMax = (float) ((r.nextFloat()*360)+ 1);
            float randomThetaMin = (float) ((r.nextFloat())+ 1);
            if(randomThetaMin > randomThetaMax) {
                float swap = randomThetaMin;
                randomThetaMin = randomThetaMax;
                randomThetaMax = swap;
            }
            float randomPolarMax = (float) ((r.nextFloat()*90)+ 1);
            float randomPolarMin = (float) ((r.nextFloat())+ 1);
            if(randomPolarMin > randomPolarMax) {
                float swap = randomPolarMin;
                randomPolarMin = randomPolarMax;
                randomPolarMax = swap;
            }            
            int randomSegments = (int) ((r.nextFloat()*15) + 5);
            Color randomColor = new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble());
            boolean ambientRandom = r.nextBoolean();
            boolean fillRandom = r.nextBoolean();
            
            SphereSegment sphereSegment = new SphereSegment(randomRadius, randomColor,
                Math.toRadians(0), Math.toRadians(360),
                Math.toRadians(randomPolarMin), Math.toRadians(randomPolarMax),
                randomSegments,ambientRandom,fillRandom);

                    
            double translationX = Math.random()*sceneWidth/2;
            if(Math.random() >= 0.5) translationX *= -1;
            double translationY = Math.random()*sceneWidth/2;
            if(Math.random() >= 0.5) translationY *= -1;
            double translationZ = Math.random()*sceneWidth/2;
            if(Math.random() >= 0.5) translationZ *= -1;
            Translate translate = new Translate(translationX, translationY, translationZ);
            Rotate rotateX = new Rotate(Math.random()*360, Rotate.X_AXIS);
            Rotate rotateY = new Rotate(Math.random()*360, Rotate.Y_AXIS);
            Rotate rotateZ = new Rotate(Math.random()*360, Rotate.Z_AXIS);
            
            sphereSegment.getTransforms().addAll(translate,rotateX,rotateY,rotateZ);
            sphereSegment.getTransforms().add(translate);
            sphereGroup.getChildren().add(sphereSegment);

        }
        sceneRoot.getChildren().addAll(sphereGroup);        

        scene.setOnKeyPressed(event -> {
            double change = 10.0;
            //Add shift modifier to simulate "Running Speed"
            if(event.isShiftDown()) { change = 50.0; }
            //What key did the user press?
            KeyCode keycode = event.getCode();
            //Step 2c: Add Zoom controls
            if(keycode == KeyCode.W) { camera.setTranslateZ(camera.getTranslateZ() + change); }
            if(keycode == KeyCode.S) { camera.setTranslateZ(camera.getTranslateZ() - change); }
            //Step 2d:  Add Strafe controls
            if(keycode == KeyCode.A) { camera.setTranslateX(camera.getTranslateX() - change); }
            if(keycode == KeyCode.D) { camera.setTranslateX(camera.getTranslateX() + change); }
        });        
        
        //Add a Mouse Handler for Rotations
        Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
        Rotate zRotate = new Rotate(0, Rotate.Z_AXIS);
        
        sphereGroup.getTransforms().addAll(xRotate, yRotate);
        //Use Binding so your rotation doesn't have to be recreated
        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);
        zRotate.angleProperty().bind(angleZ);
        
        //Start Tracking mouse movements only when a button is pressed
        scene.setOnMousePressed(event -> {
            scenex = event.getSceneX();
            sceney = event.getSceneY();
            fixedXAngle = angleX.get();
            fixedYAngle = angleY.get();
            if(event.isMiddleButtonDown()) {
                scenez = event.getSceneX();
                fixedZAngle = angleZ.get();
            }
            
        });
        //Angle calculation will only change when the button has been pressed
        scene.setOnMouseDragged(event -> {
            if(event.isMiddleButtonDown()) 
                angleZ.set(fixedZAngle - (scenez - event.getSceneY()));
            else
                angleX.set(fixedXAngle - (scenex - event.getSceneY()));
            angleY.set(fixedYAngle + sceney - event.getSceneX());
        });        
        
        primaryStage.setTitle("F(X)yz SphereSegmentTest");
        primaryStage.setScene(scene);
        primaryStage.show();        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }    
}
