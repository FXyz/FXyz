/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.tests;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxyz.cameras.CameraTransformer;
import org.fxyz.shapes.primitives.TorusMesh;
import org.fxyz.tools.CameraView;

/**
 *
 * @author Dub
 */
public class CameraViewTest extends Application {

    private StackPane root = new StackPane();
    private Group worldRoot = new Group();
    private CameraView cameraView;
    private WritableImage image = new WritableImage(200, 150);
    
    private double cameraDistance = 5000;    
    private PerspectiveCamera camera;
    private CameraTransformer cameraTransform = new CameraTransformer();

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    
    @Override
    public void start(Stage stage) throws Exception {
        
        loadSubScene();
        root.setStyle("-fx-background-color: DEEPSKYBLUE;");
        Scene scene = new Scene(root, 810,610, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.TRANSPARENT);
               
        stage.setTitle("MiniMapTest");
        stage.setScene(scene);
        //stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        stage.setMaximized(true);
        cameraView.startViewing();
    }

    private void loadSubScene() throws NonInvertibleTransformException{
        camera = new PerspectiveCamera(true);
        cameraTransform.setTranslate(0, 0, -500);
        cameraTransform.getChildren().add(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-1500);
        cameraTransform.ry.setAngle(-45.0);
        cameraTransform.rx.setAngle(-10.0);
        //add a Point Light for better viewing of the grid coordinate system
        PointLight light = new PointLight(Color.WHITE);
        
        cameraTransform.getChildren().add(light);
        light.setTranslateX(camera.getTranslateX());
        light.setTranslateY(camera.getTranslateY());
        light.setTranslateZ(camera.getTranslateZ());
        worldRoot.getChildren().add(cameraTransform);
        
        SubScene scene = new SubScene(worldRoot, 800,600, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.DARKSLATEGRAY);
        scene.setCamera(camera);
        //First person shooter keyboard movement
        scene.setOnKeyPressed(event -> {
            double change = 10.0;
            //Add shift modifier to simulate "Running Speed"
            if(event.isShiftDown()) { change = 50.0; }
            //What key did the user press?
            KeyCode keycode = event.getCode();
            //Step 2c: Add Zoom controls
            if(keycode == KeyCode.W) { camera.setTranslateZ(camera.getTranslateZ() + change); }
            if(keycode == KeyCode.S) { camera.setTranslateZ(camera.getTranslateZ() - change); }
            //Step 2d: Add Strafe controls
            if(keycode == KeyCode.A) { camera.setTranslateX(camera.getTranslateX() - change); }
            if(keycode == KeyCode.D) { camera.setTranslateX(camera.getTranslateX() + change); }            
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
            } else if (me.isMiddleButtonDown() ) {
                cameraTransform.t.setX(cameraTransform.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3); // -
                cameraTransform.t.setY(cameraTransform.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3); // -
                
            }
        });
        root.getChildren().add(scene);  
        scene.widthProperty().bind(root.widthProperty());
        scene.heightProperty().bind(root.heightProperty());
               
        cameraView = new CameraView(scene);
        cameraView.setFirstPersonNavigationEabled(true);        
        cameraView.setFitWidth(350);
        cameraView.setFitHeight(225);
        cameraView.getRx().setAngle(-45);
        cameraView.getT().setZ(-1500);
        cameraView.getT().setY(-500);
        
        root.getChildren().add(cameraView);
        StackPane.setAlignment(cameraView, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(cameraView, new Insets(5));
        
        //Add an aritrary object to scene
        int rDivs = 32, tDivs = 32;
        double rad = 600, trad = 400;
        TorusMesh torus = new TorusMesh(rDivs, tDivs, rad, trad);
        torus.setDrawMode(DrawMode.LINE);
        PhongMaterial mat = new PhongMaterial(Color.BLUEVIOLET);        
        torus.setMaterial(mat);
        torus .setTranslateX(0);
        torus .setTranslateY(0);
        torus .setTranslateZ(0);       
        worldRoot.getChildren().add(torus);
        
        final Timeline t = new Timeline();
        t.getKeyFrames().addAll(new KeyFrame[]{
            new KeyFrame(Duration.seconds(5), new KeyValue[]{// Frame End                
                new KeyValue(torus.tubeStartAngleOffsetProperty(), torus.getTubeStartAngleOffset() - 10, Interpolator.EASE_BOTH),
                new KeyValue(torus.xOffsetProperty(), torus.getxOffset() + 0.5, Interpolator.EASE_BOTH),
                new KeyValue(torus.yOffsetProperty(), torus.getyOffset() + 0.5, Interpolator.EASE_BOTH),
                new KeyValue(torus.zOffsetProperty(), torus.getzOffset() + 2, Interpolator.EASE_BOTH),
                new KeyValue(torus.tubeDivisionsProperty(), 120, Interpolator.EASE_BOTH),
                new KeyValue(torus.radiusDivisionsProperty(), 120, Interpolator.EASE_BOTH),
                new KeyValue(torus.tubeRadiusProperty(), 400, Interpolator.EASE_BOTH),
            })
        });
        t.setCycleCount(Animation.INDEFINITE);
        t.setAutoReverse(true);
        t.playFromStart();
    }
    
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
