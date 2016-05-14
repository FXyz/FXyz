/**
 * ShapeBaseSample.java
 * 
* Copyright (c) 2013-2015, F(X)yz All rights reserved.
 * 
* Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the organization nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.fxyz.samples.shapes;

import com.sun.javafx.geom.Vec3d;
import java.text.NumberFormat;
import java.util.concurrent.CountDownLatch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
import javafx.geometry.Side;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.controlsfx.control.HiddenSidesPane;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;
import org.fxyz.client.ModelInfoTracker;
import org.fxyz.controls.ControlPanel;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.samples.FXyzSample;
import org.fxyz.scene.Skybox;
import org.fxyz.shapes.primitives.Text3DMesh;
import org.fxyz.shapes.primitives.TexturedMesh;
import org.fxyz.utils.CameraTransformer;

/**
 * + mainPane resizable StackPane ++ subScene SubScene, with camera +++ root
 * Group ++++ group Group created in the subclass with the 3D Shape
 *
 * @author jpereda
 * @param <T>
 */
public abstract class ShapeBaseSample<T extends Node> extends FXyzSample {

    protected abstract void createMesh();

    protected abstract void addMeshAndListeners();

    private final double sceneWidth = 600;
    private final double sceneHeight = 600;
    protected long lastEffect;

    protected PointLight sceneLight1;
    protected PointLight sceneLight2;
    private Group light1Group;
    private Group light2Group;
    private Group lightingGroup;

    protected PerspectiveCamera camera;
    private CameraTransformer cameraTransform;
    protected Rotate rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
    protected SubScene subScene;
    protected Group root;
    protected Group group;
    protected StackPane mainPane;
    protected HiddenSidesPane parentPane;

    protected T model;
    protected ModelInfoTracker modelInfo;
    protected PhongMaterial material = new PhongMaterial();
    protected Button exportButton;

    private Service<Void> service;
    private ProgressBar progressBar;
    private long time;

    private final BooleanProperty isActive = new SimpleBooleanProperty(this, "activeSample", false);
    protected final BooleanProperty useSkybox = new SimpleBooleanProperty(this, "SkyBox Enabled", false);
    private final BooleanProperty isPicking = new SimpleBooleanProperty(this, "isPicking");
    protected final Property<DrawMode> drawMode = new SimpleObjectProperty<>(model, "drawMode", DrawMode.FILL);
    protected final Property<CullFace> culling = new SimpleObjectProperty<>(model, "culling", CullFace.BACK);

    private final CountDownLatch latch = new CountDownLatch(1);
    private final NumberFormat numberFormat = NumberFormat.getInstance();

    private Vec3d vecIni, vecPos;
    private double distance;
    private Sphere sphere;

    //Bindings..
    Subscription modelWidthBinder, modelHeightBinder, modelDepthBinder;
    Subscription sceneActiveBinder;

    public ShapeBaseSample() {
        numberFormat.setMaximumFractionDigits(1);
        
        initSample();
    }

    private void initSample() {
        buildCamera();
        buildRootNode();
        buildSkybox();
        buildModelContainer();
        buildSubScene();
        buildSubScenePane();
        buildParentPane();

        createListeners();        
    }

    private void releaseBinders() {
        if(modelWidthBinder != null){
            modelWidthBinder.unsubscribe();
            modelHeightBinder.unsubscribe();
            modelDepthBinder.unsubscribe();
        }
    }

    private void attachBinders() {
        // should be conditional on scene not being null
        modelWidthBinder = EasyBind.subscribe(model.boundsInParentProperty(), 
                (s) -> modelInfo.getBoundsWidth().setText(numberFormat.format(s.getWidth())));
        modelHeightBinder = EasyBind.subscribe(model.boundsInParentProperty(), 
                (s) -> modelInfo.getBoundsHeight().setText(numberFormat.format(s.getHeight())));
        modelDepthBinder = EasyBind.subscribe(model.boundsInParentProperty(), 
                (s) -> modelInfo.getBoundsDepth().setText(numberFormat.format(s.getDepth())));
    }

    private void createListeners() {
        drawMode.addListener((obs, b, b1) -> {
            if (model != null) {
                if (model instanceof Shape3D) {
                    ((Shape3D) model).setDrawMode(drawMode.getValue());
                } else if (model instanceof Group) {
                    ((Group) model).getChildren().filtered(Shape3D.class::isInstance)
                            .forEach(shape -> ((Shape3D) shape).setDrawMode(drawMode.getValue()));
                }
            }
        });

        culling.addListener((obs, b, b1) -> {
            if (model != null) {
                if (model instanceof Shape3D) {
                    ((Shape3D) model).setCullFace(culling.getValue());
                } else if (model instanceof Group) {
                    ((Group) model).getChildren().filtered(Shape3D.class::isInstance)
                            .forEach(shape -> ((Shape3D) shape).setCullFace(culling.getValue()));
                }
            }
        });
    }

    private void buildSkybox() {
        skyBox = new Skybox(
                top,
                bottom,
                left,
                right,
                front,
                back,
                100000,
                camera
        );
        skyBox.visibleProperty().bind(useSkybox);
        root.getChildren().add(0, skyBox);
    }

    private void buildCamera() {
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
        final PointLight light = new PointLight(Color.GAINSBORO);
        final AmbientLight amb = new AmbientLight(Color.WHITE);
        amb.getScope().add(cameraTransform);
        cameraTransform.getChildren().addAll(light);

        light.translateXProperty().bind(camera.translateXProperty());
        light.translateYProperty().bind(camera.translateYProperty());
        light.translateZProperty().bind(camera.translateZProperty());
    }

    private void buildSubScenePane() {
        mainPane = new StackPane();
        mainPane.setPrefSize(sceneWidth, sceneHeight);
        mainPane.setMaxSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        mainPane.setMinSize(sceneWidth, sceneHeight);
        mainPane.getChildren().add(subScene);
        mainPane.setPickOnBounds(false);
        subScene.widthProperty().bind(mainPane.widthProperty());
        subScene.heightProperty().bind(mainPane.heightProperty());
    }

    private void buildRootNode() {
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
    }

    private void buildModelContainer() {
        group = new Group();
        group.getChildren().add(cameraTransform);
        root.getChildren().add(group);
    }

    private void buildSubScene() {

        subScene = new SubScene(root, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.TRANSPARENT);//Color.web("#0d0d0d"));        
        subScene.setCamera(camera);
        subScene.setFocusTraversable(false);

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
            if (pr != null && pr.getIntersectedNode() != null
                    && pr.getIntersectedNode() instanceof Sphere
                    && pr.getIntersectedNode().getId().equals("knot")) {
                distance = pr.getIntersectedDistance();
                sphere = (Sphere) pr.getIntersectedNode();
                isPicking.set(true);
                vecIni = unProjectDirection(mousePosX, mousePosY, subScene.getWidth(), subScene.getHeight());
            }
        });
        subScene.setOnMouseDragged((MouseEvent me) -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);
            if (isPicking.get()) {
                double modifier = (me.isControlDown() ? 0.01 : me.isAltDown() ? 1.0 : 0.1) * (30d / camera.getFieldOfView());
                modifier *= (30d / camera.getFieldOfView());
                vecPos = unProjectDirection(mousePosX, mousePosY, subScene.getWidth(), subScene.getHeight());
                Point3D p = new Point3D(distance * (vecPos.x - vecIni.x),
                        distance * (vecPos.y - vecIni.y), distance * (vecPos.z - vecIni.z));
                sphere.getTransforms().add(new Translate(modifier * p.getX(), modifier * p.getY(), modifier * p.getZ()));
                vecIni = vecPos;

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
        subScene.setOnMouseReleased((MouseEvent me) -> {
            if (isPicking.get()) {
                isPicking.set(false);
            }
        });

    }

    private void buildParentPane() {
        parentPane = new HiddenSidesPane();
        modelInfo = new ModelInfoTracker(parentPane);

        parentPane.setContent(mainPane);
        parentPane.setBottom(modelInfo);
        parentPane.setTriggerDistance(20);
        parentPane.setAnimationDelay(Duration.ONE);
        parentPane.setPinnedSide(Side.BOTTOM);
    }

    private void loadSample() {
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

            @Override
            protected void succeeded() {
                addMeshAndListeners();
                attachBinders();
                
                mainPane.getChildren().remove(progressBar);

                if (model != null && model instanceof MeshView) {
                    material = (PhongMaterial) ((MeshView) model).getMaterial();
                } else {
                    if (model != null && model instanceof Group) {
                        if (!((Group) model).getChildren().filtered(isShape -> isShape instanceof MeshView).isEmpty()) {
                            material = (PhongMaterial) ((MeshView) ((Group) model).getChildren().filtered(t -> t instanceof MeshView).get(0)).getMaterial();
                        }
                    }
                }

                if (model != null) {
                    group.getChildren().add(model);
                } else {
                    throw new UnsupportedOperationException("Model returned Null ... ");
                }
                camera.setTranslateZ(-3d * Math.max(model.getBoundsInParent().getWidth(), model.getBoundsInParent().getHeight()));
                if (controlPanel != null && ((ControlPanel) controlPanel).getPanes().filtered(t -> t.getText().contains("lighting")).isEmpty()) {
                    ((ControlPanel) controlPanel).getPanes().add(0, ControlFactory.buildSceneAndLightCategory(
                            useSkybox,
                            sceneLight1.lightOnProperty(), sceneLight2.lightOnProperty(),
                            sceneLight1.colorProperty(), sceneLight2.colorProperty(),
                            sceneLight1.translateXProperty(), sceneLight2.translateXProperty(),
                            light1Group.rotateProperty(), light2Group.rotateProperty(),
                            light1Group.rotationAxisProperty(), light1Group.rotationAxisProperty()
                    ));
                    exportButton = new Button("Export Mesh");
                    exportButton.setFocusTraversable(false);
                    exportButton.visibleProperty().addListener(l -> {
                        if (exportButton.isVisible()) {
                            if (exportButton.getParent() != null) {
                                exportButton.setMinWidth(((VBox) exportButton.getParent()).getPrefWidth());
                                exportButton.autosize();
                            }
                        }
                    });
                    //HBox expContainer = new HBox(exportButton);
                    //expContainer.setPrefSize(USE_COMPUTED_SIZE, USE_PREF_SIZE);
                    //HBox.setHgrow(exportButton, Priority.ALWAYS);

                    ((VBox) controlPanel).getChildren().add(exportButton);
                    ((ControlPanel) controlPanel).getPanes().get(0).setExpanded(true);

                    // setup model information
                    modelInfo.getTimeToBuild().setText(String.valueOf(System.currentTimeMillis() - time) + "ms");
                    if (model instanceof Text3DMesh) {
                        modelInfo.getNodeCount().setText(String.valueOf(((Group) model).getChildren().size()));
                        modelInfo.getPoints().textProperty().bind(((Text3DMesh) model).vertCountBinding());
                        modelInfo.getFaces().textProperty().bind(((Text3DMesh) model).faceCountBinding());
                    } else if (model instanceof Group) {
                        modelInfo.getNodeCount().setText(String.valueOf(((Group) model).getChildren().filtered(t -> t instanceof Shape3D).size()));
                        modelInfo.getPoints().setText("");
                        modelInfo.getFaces().setText("");
                    } else if (model instanceof Shape3D) {
                        modelInfo.getNodeCount().setText("1");
                        modelInfo.getPoints().textProperty().bind(((TexturedMesh) model).vertCountBinding());
                        modelInfo.getFaces().textProperty().bind(((TexturedMesh) model).faceCountBinding());

                    }

                    modelInfo.getSampleTitle().setText(getSampleName());
                    
                }
            }
        };
        service.setExecutor(serviceExecutor);
        time = System.currentTimeMillis();
        service.start();
    }

    @Override
    public Node getSample() {
        loadSample();

        progressBar = new ProgressBar();
        progressBar.setPrefSize(mainPane.getPrefWidth() * 0.5, USE_PREF_SIZE);
        progressBar.setProgress(-1);
        mainPane.getChildren().add(progressBar);
        
        parentPane.parentProperty().addListener(l->{
            if(parentPane.getScene() != null){
                if(model != null){
                    attachBinders();
                }
            }else{
                releaseBinders();
            }
        });
        
        return parentPane;
    }

    protected BooleanProperty pickingProperty() {
        return isPicking;
    }

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

    protected Scene getScene() {
        return subScene.getScene();
    }

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
