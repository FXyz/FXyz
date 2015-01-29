/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.scene;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.LightBase;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class EasySubScene extends StackPane {

    private final double sceneWidth = 800;
    private final double sceneHeight = 600;

    private final List<LightBase> lights;
    private final SubScene subScene;
    private final Group root;
    protected Group group;

    protected PerspectiveCamera camera;

    public EasySubScene() {
        this.root = new Group();
        this.lights = new ArrayList<>();

        this.camera = new PerspectiveCamera(true);
        this.camera.setNearClip(0.1);
        this.camera.setFarClip(100000.0);
        this.camera.setTranslateZ(-10);
        this.camera.setVerticalFieldOfView(true);
        this.camera.setFieldOfView(42);

        this.subScene = new SubScene(root, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
        this.subScene.setCamera(camera);
        this.subScene.widthProperty().bind(widthProperty());
        this.subScene.heightProperty().bind(heightProperty());
        this.subScene.setFill(Color.rgb(0, 0, 0, 0.5));
        
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateY(400);
        AmbientLight amb = new AmbientLight(Color.WHITE);
        amb.getScope().add(camera);

        this.getChildren().add(subScene);
    }

    public Group getGroup() {
        return group;
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    private final BooleanProperty active = new SimpleBooleanProperty(this, "active", false);

    public final boolean isActive() {
        return active.get();
    }

    public final void setActive(boolean value) {
        active.set(value);
    }

    public final ReadOnlyBooleanProperty activeProperty() {
        return ((ReadOnlyBooleanWrapper) active).getReadOnlyProperty();
    }
}
