/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.tests;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import org.fxyz.shapes.composites.Histogram;

/**
 *
 * @author Sean
 */
public class HistogramTest extends Application {

    private PerspectiveCamera camera;
    private final double sceneWidth = 600;
    private final double sceneHeight = 600;
    private double cameraDistance = 5000;
    private Histogram histogram;
    private double scenex, sceney, scenez = 0;
    private double fixedXAngle, fixedYAngle, fixedZAngle = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);
    private final DoubleProperty angleZ = new SimpleDoubleProperty(0);

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group sceneRoot = new Group();
        Scene scene = new Scene(sceneRoot, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-1000);
        scene.setCamera(camera);

        histogram = new Histogram(1000, 1, true);
        sceneRoot.getChildren().addAll(histogram);

        int size = 30;
        float[][] arrayY = new float[2 * size][2 * size];
        for (int i = -size; i < size; i++) {
            for (int j = -size; j < size; j++) {
                //Transcedental Gradient
                double xterm = (Math.cos(Math.PI * i / size) * Math.cos(Math.PI * i / size));
                double yterm = (Math.cos(Math.PI * j / size) * Math.cos(Math.PI * j / size));
                arrayY[i + size][j + size] = (float) (10 * ((xterm + yterm) * (xterm + yterm)));
            }
        }
        histogram.setHeightData(arrayY, 1, 4, Color.SKYBLUE, false, true);

        scene.setOnKeyPressed(event -> {
            double change = 10.0;
            //Add shift modifier to simulate "Running Speed"
            if (event.isShiftDown()) {
                change = 50.0;
            }
            //What key did the user press?
            KeyCode keycode = event.getCode();
            //Step 2c: Add Zoom controls
            if (keycode == KeyCode.W) {
                camera.setTranslateZ(camera.getTranslateZ() + change);
            }
            if (keycode == KeyCode.S) {
                camera.setTranslateZ(camera.getTranslateZ() - change);
            }
            //Step 2d:  Add Strafe controls
            if (keycode == KeyCode.A) {
                camera.setTranslateX(camera.getTranslateX() - change);
            }
            if (keycode == KeyCode.D) {
                camera.setTranslateX(camera.getTranslateX() + change);
            }
        });

        //Add a Mouse Handler for Rotations
        Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
        Rotate zRotate = new Rotate(0, Rotate.Z_AXIS);

        histogram.getTransforms().addAll(xRotate, yRotate);
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
            if (event.isMiddleButtonDown()) {
                scenez = event.getSceneX();
                fixedZAngle = angleZ.get();
            }

        });
        //Angle calculation will only change when the button has been pressed
        scene.setOnMouseDragged(event -> {
            if (event.isMiddleButtonDown()) {
                angleZ.set(fixedZAngle - (scenez - event.getSceneY()));
            } else {
                angleX.set(fixedXAngle - (scenex - event.getSceneY()));
            }

            angleY.set(fixedYAngle + sceney - event.getSceneX());
        });

        primaryStage.setTitle("F(X)yz HistogramTest");
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
