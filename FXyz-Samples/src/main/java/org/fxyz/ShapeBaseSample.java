/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import org.fxyz.utils.CameraTransformer;

/**
 * + mainPane     resizable StackPane
 * ++ subScene    SubScene, with camera 
 * +++ root       Group
 * ++++ group     Group created in the subclass with the 3D Shape
 * 
 * @author jpereda
 * @param <T> 
 */
public abstract class ShapeBaseSample<T extends Shape3D> extends FXyzSample {

    private final double sceneWidth = 800;
    private final double sceneHeight = 600;    
    
    protected SubScene subScene;
    private Group root;
    protected Group group;
    protected StackPane mainPane;
    
    protected T model; // added this for testing .. 
    
    protected PerspectiveCamera camera;
    private CameraTransformer cameraTransform;
    protected Rotate rotateY;
    
    private Service<Void> service;
    private ProgressBar progressBar;
    private long time;
        
    protected Scene getScene(){
        return subScene.getScene();
    }

    protected PhongMaterial material = new PhongMaterial();
    protected abstract void createMesh();
    protected abstract void addMeshAndListeners();
    
    private final BooleanProperty onService=new SimpleBooleanProperty();
       
    @Override
    public Node getSample() {        
        if(!onService.get()){
            cameraTransform = new CameraTransformer();
            camera = new PerspectiveCamera(true);
            camera.setNearClip(0.1);
            camera.setFarClip(100000.0);
            camera.setTranslateZ(-10);
            //camera.setVerticalFieldOfView(true);
            camera.setFieldOfView(42);

            //setup camera transform for rotational support
            cameraTransform.setTranslate(0, 0, 0);
            cameraTransform.getChildren().add(camera);            
            cameraTransform.ry.setAngle(-45.0);
            cameraTransform.rx.setAngle(-10.0);

            //add a Point Light for better viewing of the grid coordinate system
            PointLight light = new PointLight(Color.WHITE);
            AmbientLight amb = new AmbientLight(Color.WHITE);
            amb.getScope().add(camera);
            cameraTransform.getChildren().add(light);
            cameraTransform.getChildren().add(amb);
            light.setTranslateX(camera.getTranslateX());
            light.setTranslateY(camera.getTranslateY());
            light.setTranslateZ(camera.getTranslateZ());

            root = new Group();
            subScene = new SubScene(root, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
            subScene.setFill(Color.BLACK);        
            subScene.setCamera(camera);

            rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);

            mainPane = new StackPane();
            mainPane.setPrefSize(sceneWidth, sceneHeight);
            mainPane.setMaxSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
            mainPane.setMinSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
            mainPane.setBackground(Background.EMPTY);
            mainPane.getChildren().add(subScene);
            mainPane.setPickOnBounds(false);

            service = new Service<Void>(){

                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>(){

                        @Override
                        protected Void call() throws Exception {
                            createMesh();
                            return null;
                        }

                    };
                }
            };

            progressBar = new ProgressBar();
            progressBar.prefWidthProperty().bind(mainPane.widthProperty().divide(2d));
            progressBar.setProgress(-1);
            mainPane.getChildren().add(progressBar);

            group = new Group();
            group.getChildren().add(cameraTransform);        
            root.getChildren().add(group);

            service.setOnSucceeded(e->{
                onService.set(false);
                System.out.println("time: " + (System.currentTimeMillis() - time)); 
                Platform.runLater(()->{
                    addMeshAndListeners();
                    mainPane.getChildren().remove(progressBar);
                });
            });

            subScene.widthProperty().bind(mainPane.widthProperty());
            subScene.heightProperty().bind(mainPane.heightProperty());

            //First person shooter keyboard movement 
            subScene.setOnKeyPressed(event -> {

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

            subScene.setOnMousePressed((MouseEvent me) -> {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            });
            subScene.setOnMouseDragged((MouseEvent me) -> {
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
                    //cameraTransform.t.setX(cameraTransform.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3);  // -
                    //cameraTransform.t.setY(cameraTransform.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3);  // -
                }
            });

//            if(service.getState().equals(State.READY)){
                onService.set(true);
                System.out.println("start");
                time = System.currentTimeMillis();
                service.start();
//            }
        }        
        return mainPane;
    }
    
    @Override
    public double getControlPanelDividerPosition() {
        return 0.78D;
    }
}
