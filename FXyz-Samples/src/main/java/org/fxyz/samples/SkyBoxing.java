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
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.fxyz.FXyzSample;
import org.fxyz.scene.Skybox;
import org.fxyz.shapes.Torus;
import org.fxyz.utils.CameraTransformer;

/**
 *
 * @author Dub
 */
public class SkyBoxing extends FXyzSample {

    // Supply Image Paths or a NullPointer will occur
    @Override
    public Node getSample(){

        final double cameraDistance = 5000;

        Group root;
        Skybox skyBox;
        PerspectiveCamera camera;
        final CameraTransformer cameraTransform = new CameraTransformer();

        final Image top = new Image(SkyBoxing.class.getResource("res/top.png").toExternalForm()),
                bottom = new Image(SkyBoxing.class.getResource("res/bottom.png").toExternalForm()),
                left = new Image(SkyBoxing.class.getResource("res/left.png").toExternalForm()),
                right = new Image(SkyBoxing.class.getResource("res/right.png").toExternalForm()),
                front = new Image(SkyBoxing.class.getResource("res/front.png").toExternalForm()),
                back = new Image(SkyBoxing.class.getResource("res/back.png").toExternalForm());

        root = new Group();

        camera = new PerspectiveCamera(true);
        cameraTransform.setTranslate(0, 0, 0);
        cameraTransform.getChildren().addAll(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(1000000.0);
        camera.setFieldOfView(42);
        camera.setTranslateZ(-cameraDistance);
        //cameraTransform.ry.setAngle(-45.0);
        //cameraTransform.rx.setAngle(-10.0);
        //add a Point Light for better viewing of the grid coordinate system
        PointLight light = new PointLight(Color.WHITE);

        cameraTransform.getChildren().add(light);
        light.setTranslateX(camera.getTranslateX());
        light.setTranslateY(camera.getTranslateY());
        light.setTranslateZ(camera.getTranslateZ());

        root.getChildren().add(cameraTransform);

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

        //Make a bunch of semi random Torusesessses(toroids?) and stuff : from torustest
        Group torusGroup = new Group();
        torusGroup.getChildren().add(cameraTransform);
        for (int i = 0; i < 30; i++) {
            Random r = new Random();
            //A lot of magic numbers in here that just artificially constrain the math
            float randomRadius = (float) ((r.nextFloat() * 300) + 50);
            float randomTubeRadius = (float) ((r.nextFloat() * 100) + 1);
            int randomTubeDivisions = (int) ((r.nextFloat() * 64) + 1);
            int randomRadiusDivisions = (int) ((r.nextFloat() * 64) + 1);
            Color randomColor = new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble());

            Torus torus = new Torus(randomTubeDivisions, randomRadiusDivisions, randomRadius, randomTubeRadius, randomColor);

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

        SubScene scene = new SubScene(new Group(root), 1024, 668, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.TRANSPARENT);
        scene.setCamera(camera);

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
            //Step 2d: Add Strafe controls
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
                cameraTransform.ry.setAngle(((cameraTransform.ry.getAngle() + mouseDeltaX * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // +
                cameraTransform.rx.setAngle(((cameraTransform.rx.getAngle() - mouseDeltaY * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // - 

            } else if (me.isSecondaryButtonDown()) {
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX * modifierFactor * modifier;
                camera.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown()) {
                cameraTransform.t.setX(cameraTransform.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3); // -
                cameraTransform.t.setY(cameraTransform.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3); // -
            }
        });

        root.getChildren().addAll(skyBox, torusGroup);
        root.setAutoSizeChildren(true);
        
        
        StackPane sp = new StackPane();
        sp.setPrefSize(800, 600);
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
        return "";
    }

}
