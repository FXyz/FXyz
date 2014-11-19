/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.tests;

import com.sun.javafx.Utils;
import java.util.Random;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.fxyz.cameras.CameraTransformer;
import org.fxyz.shapes.primitives.TorusMesh;
import org.fxyz.extras.BillboardBehavior;
import org.fxyz.tools.CameraView;
import org.fxyz.extras.Skybox;

/**
 *
 * @author Dub
 */
public class BillBoardBehaviorTest extends Application {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    //**************************************************************************
    
    private boolean active = false; //Flag for toggling behavior
    
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private final double cameraDistance = 5000;
    
    private StackPane rootPane = new StackPane();
    private SubScene subScene;
    private CameraView cameraView;
    private final Group root = new Group();
    private Skybox skyBox;
    private PerspectiveCamera camera;
    private final CameraTransformer cameraTransform = new CameraTransformer();
    private final BillBoard bill = new BillBoard();
    
    
    @Override
    public void start(Stage stage) throws NonInvertibleTransformException {
        
        createSubscene();        
        createCameraView();
        createOverlay();
        
        Scene scene = new Scene(rootPane, 1024, 668);          
        
        stage.setTitle("Billbording Test!");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();                        
        
    }

    
    private void createSubscene(){        
        subScene = new SubScene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        
        camera = new PerspectiveCamera(true);
        cameraTransform.setTranslate(0, 0, 0);
        cameraTransform.getChildren().addAll(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(100000.0);
        camera.setFieldOfView(35);
        camera.setTranslateZ(-cameraDistance);
        cameraTransform.ry.setAngle(-45.0);
        cameraTransform.rx.setAngle(-10.0);
        //add a Point Light for better viewing of the grid coordinate system
        PointLight light = new PointLight(Color.WHITE);
        
        cameraTransform.getChildren().add(light);
        light.setTranslateX(camera.getTranslateX());
        light.setTranslateY(camera.getTranslateY());
        light.setTranslateZ(camera.getTranslateZ());
                
        root.getChildren().add(cameraTransform);
        subScene.setCamera(camera);
        
        initFirstPersonControls(subScene);
        
        skyBox = new Skybox(new Image("http://www.zfight.com/misc/images/textures/envmaps/violentdays_large.jpg"), 100000, camera);      
       
        //Make a bunch of semi random Torusesessses(toroids?) and stuff : from torustest
        Group torusGroup = new Group();        
        for (int i = 0; i < 10; i++) {
            Random r = new Random();
            //A lot of magic numbers in here that just artificially constrain the math
            float randomRadius = (float) ((r.nextFloat() * 300) + 50);
            float randomTubeRadius = (float) ((r.nextFloat() * 100) + 1);
            int randomTubeDivisions = (int) ((r.nextFloat() * 64) + 1);
            int randomRadiusDivisions = (int) ((r.nextFloat() * 64) + 1);
            Color randomColor = new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble());
            boolean ambientRandom = r.nextBoolean();
            boolean fillRandom = r.nextBoolean();
            
            if(i == 0){                
                torusGroup.getChildren().add(bill);
            }
            TorusMesh torus = new TorusMesh(randomTubeDivisions, randomRadiusDivisions, randomRadius, randomTubeRadius);
            
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
        root.getChildren().addAll(skyBox, torusGroup);
        
        rootPane.getChildren().add(subScene);
        //Enable subScene resizing
        subScene.widthProperty().bind(rootPane.widthProperty());
        subScene.heightProperty().bind(rootPane.heightProperty());
        subScene.setFocusTraversable(true);
        
    }
       
    private void createOverlay(){
        Button b = new Button("Activate");
        b.setPrefSize(150, 40);
        b.setFocusTraversable(false);
        b.setOnAction(e->{
            if(!active){
                bill.startBillboardBehavior();
                active = true;
                b.setText("Disable");
            }else{
                bill.stopBillboardBehavior();
                active = false;
                b.setText("Enable");
            }
        });
        CheckBox c = new CheckBox("Toggle Cylindrical Mode");
        c.setFont(Font.font(24));
        c.setFocusTraversable(false);
        c.setOnAction(e->{
            if(c.isSelected()){
                bill.setBillboardMode(BillboardBehavior.BillboardMode.CYLINDRICAL);
            }else{
                bill.setBillboardMode(BillboardBehavior.BillboardMode.SPHERICAL);
            }
        });
        
        StackPane.setAlignment(b, Pos.TOP_LEFT);
        StackPane.setMargin(b, new Insets(10));
        
        StackPane.setAlignment(c, Pos.CENTER_LEFT);
        StackPane.setMargin(c, new Insets(10));
        
        rootPane.getChildren().addAll(b, c);
    }
    
    private void createCameraView(){
        cameraView = new CameraView(subScene);
        cameraView.setFitWidth(400);
        cameraView.setFitHeight(300);
        cameraView.setFirstPersonNavigationEabled(true);
        cameraView.setFocusTraversable(true);
        cameraView.getCamera().setTranslateZ(-2500);
        cameraView.getCamera().setTranslateX(500);
        
        StackPane.setAlignment(cameraView, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(cameraView, new Insets(10));
        
        rootPane.getChildren().add(cameraView);
        
        cameraView.startViewing();
    }
    
    private void initFirstPersonControls(SubScene scene){
        //make sure Subscene handles KeyEvents
        scene.setOnMouseEntered(e->{
            scene.requestFocus();
        });
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
            if(!scene.isFocused()){
                scene.requestFocus();
            }
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
                cameraTransform.rx.setAngle(
                        Utils.clamp(-60, 
                        (((cameraTransform.rx.getAngle() - mouseDeltaY * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180),
                        60)); // - 
                
            } else if (me.isSecondaryButtonDown()) {
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX * modifierFactor * modifier;
                camera.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown()) {
                cameraTransform.t.setX(cameraTransform.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3); // -
                cameraTransform.t.setY(cameraTransform.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3); // -
            }
        });
    }
    //******************            BillBoard            ***********************
    private class BillBoard extends ImageView implements BillboardBehavior<BillBoard>{      
        
        public BillBoard() {
            super();
            Image image = new Image("http://overshoot.tv/sites/overshoot.tv/files/Oak_Tree_0.png");
            setFitWidth(800);
            setPreserveRatio(true);
            setSmooth(true);
            setImage(image);  
            setDepthTest(DepthTest.ENABLE);
        }       
        
        @Override
        public BillBoard getBillboardNode() {
            return this;
        }

        @Override
        public Node getOther() {
            return camera;
        }    
    }
    
    
    
    
}
