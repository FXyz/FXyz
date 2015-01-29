/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.pending;

import java.util.Random;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.fxyz.FXyzSample;
import org.fxyz.shapes.SphereSegment;
import org.fxyz.utils.CameraTransformer;

/**
 *
 * @author Sean
 */
public class SphereSegments extends FXyzSample {

    @Override
    public Node getSample() {

        PerspectiveCamera camera = new PerspectiveCamera(true);
        final double sceneWidth = 800;
        final double sceneHeight = 600;
        final CameraTransformer cameraTransform = new CameraTransformer();

        Group sceneRoot = new Group();
        SubScene scene = new SubScene(sceneRoot, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);

        //setup camera transform for rotational support
        cameraTransform.setTranslate(0, 0, 0);
        cameraTransform.getChildren().add(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-2000);
        cameraTransform.ry.setAngle(-45.0);
        cameraTransform.rx.setAngle(-10.0);
        //add a Point Light for better viewing of the grid coordinate system
        PointLight light = new PointLight(Color.WHITE);
        cameraTransform.getChildren().add(light);
        cameraTransform.getChildren().add(new AmbientLight(Color.WHITE));
        light.setTranslateX(camera.getTranslateX());
        light.setTranslateY(camera.getTranslateY());
        light.setTranslateZ(camera.getTranslateZ());
        scene.setCamera(camera);

        //Make a bunch of semi random sphere segments and stuff
        Group sphereGroup = new Group();
        for (int i = 0; i < 30; i++) {
            Random r = new Random();
            //A lot of magic numbers in here that just artificially constrain the math
            float randomRadius = (float) ((r.nextFloat() * 150) + 10);
            float randomThetaMax = (float) ((r.nextFloat() * 360) + 1);
            float randomThetaMin = (float) ((r.nextFloat()) + 1);
            if (randomThetaMin > randomThetaMax) {
                float swap = randomThetaMin;
                randomThetaMin = randomThetaMax;
                randomThetaMax = swap;
            }
            float randomPolarMax = (float) ((r.nextFloat() * 90) + 1);
            float randomPolarMin = (float) ((r.nextFloat()) + 1);
            if (randomPolarMin > randomPolarMax) {
                float swap = randomPolarMin;
                randomPolarMin = randomPolarMax;
                randomPolarMax = swap;
            }
            int randomSegments = (int) ((r.nextFloat() * 15) + 5);
            Color randomColor = new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble());
            boolean ambientRandom = r.nextBoolean();
            boolean fillRandom = r.nextBoolean();

            SphereSegment sphereSegment = new SphereSegment(randomRadius, randomColor,
                    Math.toRadians(0), Math.toRadians(360),
                    Math.toRadians(randomPolarMin), Math.toRadians(randomPolarMax),
                    randomSegments, ambientRandom, fillRandom);

            double translationX = Math.random() * sceneWidth / 2;
            if (Math.random() >= 0.5) {
                translationX *= -1;
            }
            double translationY = Math.random() * sceneWidth / 2;
            if (Math.random() >= 0.5) {
                translationY *= -1;
            }
            double translationZ = Math.random() * sceneWidth / 2;
            if (Math.random() >= 0.5) {
                translationZ *= -1;
            }
            Translate translate = new Translate(translationX, translationY, translationZ);
            Rotate rotateX = new Rotate(Math.random() * 360, Rotate.X_AXIS);
            Rotate rotateY = new Rotate(Math.random() * 360, Rotate.Y_AXIS);
            Rotate rotateZ = new Rotate(Math.random() * 360, Rotate.Z_AXIS);

            sphereSegment.getTransforms().addAll(translate, rotateX, rotateY, rotateZ);
            sphereSegment.getTransforms().add(translate);
            sphereGroup.getChildren().add(sphereSegment);

        }
        sceneRoot.getChildren().addAll(sphereGroup);

        //First person shooter keyboard movement 
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

        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged((MouseEvent me) -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);

            double modifier = 10.0;
            double modifierFactor = 0.1;

            if (me.isControlDown()) {
                modifier = 0.1;
            }
            if (me.isShiftDown()) {
                modifier = 50.0;
            }
            if (me.isPrimaryButtonDown()) {
                cameraTransform.ry.setAngle(((cameraTransform.ry.getAngle() + mouseDeltaX * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180);  // +
                cameraTransform.rx.setAngle(((cameraTransform.rx.getAngle() - mouseDeltaY * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180);  // -
            } else if (me.isSecondaryButtonDown()) {
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX * modifierFactor * modifier;
                camera.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown()) {
                cameraTransform.t.setX(cameraTransform.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3);  // -
                cameraTransform.t.setY(cameraTransform.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3);  // -
            }
        });

        StackPane sp = new StackPane();
        sp.setPrefSize(sceneWidth, sceneHeight);
        sp.setMaxSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        sp.setMinSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        sp.setBackground(Background.EMPTY);
        sp.getChildren().add(scene);
        sp.setPickOnBounds(false);
        
        scene.widthProperty().bind(sp.widthProperty());
        scene.heightProperty().bind(sp.heightProperty());
        
        return (sp);
    }

    @Override
    public String getSampleName() {
        return getClass().getSimpleName().concat(" Sample");
    }

    @Override
    public Node getPanel(Stage stage) {
        return getSample();
    }

    @Override
    public String getJavaDocURL() {
        return null;
    }
}
