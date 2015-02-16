/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.samples.shapes;

import com.sun.javafx.geom.Vec3d;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
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
import javafx.scene.input.PickResult;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.fxyz.controls.ControlPanel;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.samples.FXyzSample;
import org.fxyz.utils.CameraTransformer;

/**
 * + mainPane resizable StackPane ++ subScene SubScene, with camera +++ root
 * Group ++++ group Group created in the subclass with the 3D Shape
 *
 * @author jpereda
 * @param <T>
 */
public abstract class ShapeBaseSample<T extends Node> extends FXyzSample {

    private final double sceneWidth = 600;
    private final double sceneHeight = 600;
    protected long lastEffect;

    protected PointLight sceneLight1;
    protected PointLight sceneLight2;

    private Group light1Group;
    private Group light2Group;
    private Group lightingGroup;
    protected SubScene subScene;
    private Group root;
    protected Group group;
    protected StackPane mainPane;

    protected T model;

    protected PerspectiveCamera camera;
    private CameraTransformer cameraTransform;
    protected Rotate rotateY;

    private Service<Void> service;
    private ProgressBar progressBar;
    private long time;

    protected Scene getScene() {
        return subScene.getScene();
    }

    protected PhongMaterial material = new PhongMaterial();

    protected abstract void createMesh();

    protected abstract void addMeshAndListeners();

    private final BooleanProperty onService = new SimpleBooleanProperty();

    private final BooleanProperty isPicking=new SimpleBooleanProperty();
    
    private Vec3d vecIni, vecPos;
    private double distance;
    private Sphere s;

    @Override
    public Node getSample() {
        if (!onService.get()) {
            cameraTransform = new CameraTransformer();
            camera = new PerspectiveCamera(true);
            camera.setNearClip(0.1);
            camera.setFarClip(100000.0);
            camera.setTranslateZ(-50);
            camera.setVerticalFieldOfView(false);
            camera.setFieldOfView(42);

            //setup camera transform for rotational support
            cameraTransform.setTranslate(0, 0, 0);
            cameraTransform.getChildren().add(camera);
            cameraTransform.ry.setAngle(-45.0);
            cameraTransform.rx.setAngle(-10.0);

            //add a Point Light for better viewing of the grid coordinate system
            PointLight light = new PointLight(Color.GAINSBORO);
            AmbientLight amb = new AmbientLight(Color.WHITE);
            amb.getScope().add(cameraTransform);
            cameraTransform.getChildren().addAll(light);

            light.translateXProperty().bind(camera.translateXProperty());
            light.translateYProperty().bind(camera.translateYProperty());
            light.translateZProperty().bind(camera.translateZProperty());

            //==========================================================
            // Need a scene control panel to allow alterations to properties
            sceneLight1 = new PointLight();
            sceneLight1.setTranslateX(500);

            sceneLight2 = new PointLight();
            sceneLight2.setTranslateX(-500);

            light1Group = new Group(sceneLight1);
            light2Group = new Group(sceneLight2);

            lightingGroup = new Group(light1Group, light2Group);
            //==========================================================

            root = new Group(lightingGroup);

            sceneLight1.getScope().add(root);
            sceneLight2.getScope().add(root);

            subScene = new SubScene(root, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
            subScene.setFill(Color.TRANSPARENT);//Color.web("#0d0d0d"));        
            subScene.setCamera(camera);
            subScene.setFocusTraversable(false);

            rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);

            mainPane = new StackPane();
            mainPane.setPrefSize(sceneWidth, sceneHeight);
            mainPane.setMaxSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
            mainPane.setMinSize(sceneWidth, sceneHeight);
            mainPane.getChildren().add(subScene);
            mainPane.setPickOnBounds(false);

            service = new Service<Void>() {

                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {

                        @Override
                        protected Void call() throws Exception {
                            createMesh();
                            return null;
                        }

                    };
                }

                @Override
                protected void failed() {
                    super.failed();
                    getException().printStackTrace(System.err);
                }
            };

            progressBar = new ProgressBar();
            progressBar.prefWidthProperty().bind(mainPane.widthProperty().divide(2d));
            progressBar.setProgress(-1);
            mainPane.getChildren().add(progressBar);

            group = new Group();
            group.getChildren().add(cameraTransform);
            root.getChildren().add(group);

            service.setOnSucceeded(e -> {
                onService.set(false);
                System.out.println("time: " + (System.currentTimeMillis() - time));
                addMeshAndListeners();
                mainPane.getChildren().remove(progressBar);

                if (model !=null && model instanceof Shape3D) {
                    material = (PhongMaterial) ((Shape3D) model).getMaterial();
                } else {
                    if (model!=null && model instanceof Group) {
                        material = (PhongMaterial) ((Shape3D) ((Group)model).getChildren().filtered(t-> t instanceof Shape3D).get(0)).getMaterial();
                    }
                }
                
                if (model != null) {
                    group.getChildren().add(model);
                } else {
                    throw new UnsupportedOperationException("Model returned Null ... ");
                }
                
                if (controlPanel != null && ((ControlPanel) controlPanel).getPanes().filtered(t -> t.getText().contains("lighting")).isEmpty()) {
                    ((ControlPanel) controlPanel).getPanes().add(0, ControlFactory.buildSceneAndLightCategory(
                            mainPane.visibleProperty(),
                            sceneLight1.lightOnProperty(), sceneLight2.lightOnProperty(),
                            sceneLight1.colorProperty(), sceneLight2.colorProperty(),
                            sceneLight1.translateXProperty(), sceneLight2.translateXProperty(),
                            light1Group.rotateProperty(), light2Group.rotateProperty(),
                            light1Group.rotationAxisProperty(), light1Group.rotationAxisProperty()
                    ));
                }
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
                PickResult pr = me.getPickResult();
                if(pr!=null && pr.getIntersectedNode() != null && 
                   pr.getIntersectedNode() instanceof Sphere && 
                   pr.getIntersectedNode().getId().equals("knot")){
                    distance=pr.getIntersectedDistance();
                    s = (Sphere) pr.getIntersectedNode();
                    isPicking.set(true);
                    vecIni = unProjectDirection(mousePosX, mousePosY, subScene.getWidth(),subScene.getHeight());
                }
            });
            subScene.setOnMouseDragged((MouseEvent me) -> {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);
                if(isPicking.get()){
                    double modifier = (me.isControlDown()?0.01:me.isAltDown()?1.0:0.1)*(30d/camera.getFieldOfView());
                    modifier *=(30d/camera.getFieldOfView());
                    vecPos = unProjectDirection(mousePosX, mousePosY, subScene.getWidth(),subScene.getHeight());
                    Point3D p=new Point3D(distance*(vecPos.x-vecIni.x),
                                distance*(vecPos.y-vecIni.y),distance*(vecPos.z-vecIni.z));
                    s.getTransforms().add(new Translate(modifier*p.getX(),modifier*p.getY(),modifier*p.getZ()));
                    vecIni=vecPos;

                } else {

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
                }
            });
            subScene.setOnMouseReleased((MouseEvent me)->{
                if(isPicking.get()){
                    isPicking.set(false);
                }
            });

            onService.set(true);
            System.out.println("start");
            time = System.currentTimeMillis();
            service.start();
        }

        mainPane.sceneProperty().addListener(i -> {
            if (mainPane.getScene() != null) {
                mainPane.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                    if (e.getPickResult() != null) {
                        System.out.println(e.getPickResult().getIntersectedNode() + " : " + e.getPickResult().getIntersectedNode().getTypeSelector());
                    }
                });
            }
        });
        //mainPane.sceneProperty().addListener(l -> {          });
        //mainPane.getChildren().add(sceneControls);
        return mainPane;
    }
    
    protected BooleanProperty pickingProperty() { return isPicking; }

    @Override
    public Node getControlPanel() {
        return buildControlPanel() != null ? buildControlPanel() : null;
    }

    @Override
    public double getControlPanelDividerPosition() {
        //SplitPane.setResizableWithParent(controlPanel, Boolean.TRUE);
        return -1;
    }

    public <T> T lookup(Node parent, String id, Class<T> clazz) {
        for (Node node : parent.lookupAll(id)) {
            if (node.getClass().isAssignableFrom(clazz)) {
                return (T) node;
            }
        }
        throw new IllegalArgumentException("Parent " + parent + " doesn't contain node with id " + id);
    }

    protected final Property<DrawMode> drawMode = new SimpleObjectProperty<DrawMode>(model, "drawMode", DrawMode.FILL) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                if (model instanceof Shape3D) {
                    ((Shape3D) model).setDrawMode(drawMode.getValue());
                }else if(model instanceof Group){
                    ((Group)model).getChildren().filtered(t-> t instanceof Shape3D)
                            .forEach(s->{
                                ((Shape3D)s).setDrawMode(drawMode.getValue());
                            });
                }
            }
        }
    };
    protected final Property<CullFace> culling = new SimpleObjectProperty<CullFace>(model, "culling", CullFace.BACK) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                if (model instanceof Shape3D) {
                    ((Shape3D) model).setCullFace(culling.getValue());
                }else if(model instanceof Group){
                    ((Group)model).getChildren().filtered(t-> t instanceof Shape3D)
                            .forEach(s->{
                                ((Shape3D)s).setCullFace(culling.getValue());
                            });
                }
            }
        }
    };

    /*
     From fx83dfeatures.Camera3D
     http://hg.openjdk.java.net/openjfx/8u-dev/rt/file/f4e58490d406/apps/toys/FX8-3DFeatures/src/fx83dfeatures/Camera3D.java
     */
    /*
     * returns 3D direction from the Camera position to the mouse
     * in the Scene space 
     */
    public Vec3d unProjectDirection(double sceneX, double sceneY, double sWidth, double sHeight) {
        double tanHFov = Math.tan(Math.toRadians(camera.getFieldOfView()) * 0.5f);
        Vec3d vMouse = new Vec3d(2 * sceneX / sWidth - 1, 2 * sceneY / sWidth - sHeight / sWidth, 1);
        vMouse.x *= tanHFov;
        vMouse.y *= tanHFov;

        Vec3d result = localToSceneDirection(vMouse, new Vec3d());
        result.normalize();
        return result;
    }

    public Vec3d localToScene(Vec3d pt, Vec3d result) {
        Point3D res = camera.localToParentTransformProperty().get().transform(pt.x, pt.y, pt.z);
        if (camera.getParent() != null) {
            res = camera.getParent().localToSceneTransformProperty().get().transform(res);
        }
        result.set(res.getX(), res.getY(), res.getZ());
        return result;
    }

    public Vec3d localToSceneDirection(Vec3d dir, Vec3d result) {
        localToScene(dir, result);
        result.sub(localToScene(new Vec3d(0, 0, 0), new Vec3d()));
        return result;
    }

}
