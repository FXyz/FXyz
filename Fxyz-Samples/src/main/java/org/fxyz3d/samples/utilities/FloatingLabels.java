/**
 * RayIntersections.java
 *
 * Copyright (c) 2013-2016, F(X)yz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of F(X)yz, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL F(X)yz BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package org.fxyz3d.samples.utilities;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneUtils;
import java.util.HashMap;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxyz3d.geometry.MathUtils;
import org.fxyz3d.samples.FXyzSample;

/**
 *
 * @author sphillips
 */
public class FloatingLabels extends FXyzSample {

    public static void main(String[] args){
        launch(args);
    }
    //We'll use custom Rotate transforms to manage the coordinate conversions
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    Group sceneRoot;
    Group nodeGroup;  //all 3D nodes in scene    
    Group labelGroup; //all generic 3D labels     
    //For each label you'll need some Shape3D to derive a point3d from. 
    //For this we will use simple spheres.  These can be optionally invisible.
    private Sphere xSphere = new Sphere(5);
    private Sphere ySphere = new Sphere(5);
    private Sphere zSphere = new Sphere(5);
    private Label xLabel = new Label("X Axis");
    private Label yLabel = new Label("Y Axis");
    private Label zLabel = new Label("Z Axis");
    //All shapes and labels linked via hash for easy update during camera movement
    HashMap<Shape3D, Label> shape3DToLabel = new HashMap<>();
    SubScene subScene;    
    
    @Override
    public Node getSample() {

        PerspectiveCamera camera;
        final double sceneWidth = 600;
        final double sceneHeight = 600;
        //global groups that contain our nodes and labels and get added to the scene
        nodeGroup = new Group();
        labelGroup = new Group();
        //add our nodes to the group that will later be added to the 3D scene 
        nodeGroup.getChildren().addAll(xSphere, ySphere, zSphere);
        //attach our custom rotation transforms so we can update the labels dynamically
        nodeGroup.getTransforms().addAll(rotateX, rotateY, rotateZ);        
        //add our labels to the group that will be added to the StackPane
        labelGroup.getChildren().addAll(xLabel, yLabel, zLabel);
        //Add to hashmap so updateLabels() can manage the label position
        shape3DToLabel.put(xSphere, xLabel);
        shape3DToLabel.put(ySphere, yLabel);
        shape3DToLabel.put(zSphere, zLabel);
        //Customize the 3D nodes a bit
        xSphere.setTranslateX(50);
        xSphere.setMaterial(new PhongMaterial(Color.YELLOW));
        ySphere.setTranslateY(50);
        ySphere.setMaterial(new PhongMaterial(Color.SKYBLUE));
        zSphere.setTranslateZ(50);
        zSphere.setMaterial(new PhongMaterial(Color.LIGHTGREEN));
        //customize the labels to match
        xLabel.setTextFill(Color.YELLOW);
        Font font = new Font("calibri", 20);
        xLabel.setFont(font);
        yLabel.setTextFill(Color.SKYBLUE);
        yLabel.setFont(new Font("calibri", 20));
        zLabel.setTextFill(Color.LIGHTGREEN);
        zLabel.setFont(new Font("calibri", 20));
        //have some fun, just one example of what you can do with the 2D node 
        //in parallel to the 3D transformation. Be careful when you manipulate 
        //the position of the 2D label as putting it off screen can mess with
        //your 2D layout.  See the clipping logic in updateLabels() for details
        FadeTransition fader = new FadeTransition(Duration.seconds(5), zLabel);
        fader.setFromValue(1.0);
        fader.setToValue(0.1);
        fader.setCycleCount(Timeline.INDEFINITE);
        fader.setAutoReverse(true);
        fader.play();        
        
        sceneRoot = new Group();
        subScene = new SubScene(nodeGroup, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.web("#303030"));
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-400);
        subScene.setCamera(camera);
        sceneRoot = new Group(subScene);

        sceneRoot.getChildren().add(labelGroup);
        
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
            updateLabels();
        });

        subScene.setOnScroll((ScrollEvent event) -> {
            double modifier = 50.0;
            double modifierFactor = 0.1;
            if (event.isControlDown()) {
                modifier = 1;
            }
            if (event.isShiftDown()) {
                modifier = 100.0;
            }
            double z = camera.getTranslateZ();
            double newZ = z + event.getDeltaY() * modifierFactor * modifier;
            camera.setTranslateZ(newZ);
            updateLabels(); //testing labels
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
                modifier = 1;
            }
            if (me.isShiftDown()) {
                modifier = 50.0;
            }
            if (me.isPrimaryButtonDown()) {
                if(me.isAltDown()) { //roll
                    rotateZ.setAngle(((rotateZ.getAngle() + mouseDeltaX * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // +
                } else {
                    rotateY.setAngle(((rotateY.getAngle() + mouseDeltaX * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // +

                    rotateX.setAngle(
                            MathUtils.clamp( 
                            (((rotateX.getAngle() - mouseDeltaY * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180),
                            -60, 60)); // - 
                }
            }
            updateLabels();    
        });

        //add to the 2D portion of this component
        StackPane sp = new StackPane();
        sp.setPrefSize(sceneWidth, sceneHeight);
        sp.setMaxSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        sp.setMinSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        sp.setBackground(Background.EMPTY);
        sp.getChildren().add(sceneRoot);
        sp.setPickOnBounds(false);
        
        subScene.widthProperty().bind(sp.widthProperty());
        subScene.heightProperty().bind(sp.heightProperty());
        Platform.runLater(()-> updateLabels());
        return (sp);
    }
    private void updateLabels() {
        shape3DToLabel.forEach((node, label) -> {
            javafx.geometry.Point3D coordinates = node.localToScene(javafx.geometry.Point3D.ZERO);
            //@DEBUG SMP useful debugging print
            //System.out.println("localToScene Coordinates: " + coordinates.toString());
            SubScene oldSubScene = NodeHelper.getSubScene(node);
            coordinates = SceneUtils.subSceneToScene(oldSubScene, coordinates);
            //@DEBUG SMP  useful debugging print
            //System.out.println("subSceneToScene Coordinates: " + coordinates.toString());
            //Clipping Logic
            //if coordinates are outside of the scene it could
            //stretch the screen so don't transform them 
            double x = coordinates.getX();
            double y = coordinates.getY();
            //is it left of the view?
            if(x < 0) {
                x = 0;
            }
            //is it right of the view?
            if((x+label.getWidth()+5) > subScene.getWidth()) {
                x = subScene.getWidth() - (label.getWidth()+5);
            }
            //is it above the view?
            if(y < 0) {
                y = 0;
            }
            //is it below the view
            if((y+label.getHeight()) > subScene.getHeight())
                y = subScene.getHeight() - (label.getHeight()+5);
            //@DEBUG SMP  useful debugging print
            //System.out.println("clipping Coordinates: " + x + ", " + y);
            //update the local transform of the label.
            label.getTransforms().setAll(new Translate(x, y));
        });
    }  
    @Override
    public Node getPanel(Stage stage) {
        return getSample();
    }

    @Override
    public String getJavaDocURL() {
        return null;
    }
    @Override
    protected Node buildControlPanel() {
        return null;
    }
}