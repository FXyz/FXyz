package org.fxyz.samples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.fxyz.FXyzSample;
import org.fxyz.geometry.DensityFunction;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.BezierMesh;
import org.fxyz.shapes.primitives.helper.InterpolateBezier;
import org.fxyz.utils.CameraTransformer;

/**
 *
 * @author jpereda
 */
public class BezierMeshes extends FXyzSample {

    long lastEffect;

    private final BooleanProperty showKnots = new SimpleBooleanProperty();
    private final BooleanProperty showControlPoints = new SimpleBooleanProperty();

    @Override
    public Node getSample() {
        PerspectiveCamera camera;
        final double sceneWidth = 800;
        final double sceneHeight = 600;
        final CameraTransformer cameraTransform = new CameraTransformer();

        ArrayList<BezierMesh> beziers;
        Rotate rotateY;
        DensityFunction<Point3D> dens = p -> (double) p.f;

        SubScene subScene;

        Group sceneRoot = new Group();
        subScene = new SubScene(sceneRoot, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        camera = new PerspectiveCamera(true);

        //setup camera transform for rotational support
        cameraTransform.setTranslate(0, 0, 0);
        cameraTransform.getChildren().add(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-10);
        cameraTransform.ry.setAngle(-45.0);
        cameraTransform.rx.setAngle(-10.0);
        //add a Point Light for better viewing of the grid coordinate system
        PointLight light = new PointLight(Color.WHITE);
        cameraTransform.getChildren().add(light);
        cameraTransform.getChildren().add(new AmbientLight(Color.WHITE));
        light.setTranslateX(camera.getTranslateX());
        light.setTranslateY(camera.getTranslateY());
        light.setTranslateZ(camera.getTranslateZ());
        subScene.setCamera(camera);

        rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        Group group = new Group();
        group.getChildren().add(cameraTransform);

//        List<Point3D> knots=Arrays.asList(new Point3D(0f,0f,0f),new Point3D(3f,0f,2f),
//                new Point3D(5f,2f,3f),new Point3D(7f,-3f,0f),new Point3D(6f,-1f,-4f));
        List<Point3D> knots = Arrays.asList(new Point3D(3f, 0f, 0f), new Point3D(0.77171f, 1.68981f, 0.989821f),
                new Point3D(-0.681387f, 0.786363f, -0.281733f), new Point3D(-2.31757f, -0.680501f, -0.909632f),
                new Point3D(-0.404353f, -2.81233f, 0.540641f), new Point3D(1.1316f, -0.727237f, 0.75575f),
                new Point3D(1.1316f, 0.727237f, -0.75575f), new Point3D(-0.404353f, 2.81233f, -0.540641f),
                new Point3D(-2.31757f, 0.680501f, 0.909632f), new Point3D(-0.681387f, -0.786363f, 0.281733f),
                new Point3D(0.77171f, -1.68981f, -0.989821f), new Point3D(3f, 0f, 0f));

        InterpolateBezier interpolate = new InterpolateBezier(knots);
        beziers = new ArrayList<>();
        showKnots.addListener((obs, b, b1) -> interpolate.getSplines().forEach(spline -> {
            Point3D k0 = spline.getPoints().get(0);
            Point3D k1 = spline.getPoints().get(1);
            Point3D k2 = spline.getPoints().get(2);
            Point3D k3 = spline.getPoints().get(3);
            if (showKnots.get()) {
                Sphere s = new Sphere(0.2d);
                s.setId("knot");
                s.getTransforms().add(new Translate(k0.x, k0.y, k0.z));
                s.setMaterial(new PhongMaterial(Color.GREENYELLOW));
                group.getChildren().add(s);
                s = new Sphere(0.2d);
                s.setId("knot");
                s.getTransforms().add(new Translate(k3.x, k3.y, k3.z));
                s.setMaterial(new PhongMaterial(Color.GREENYELLOW));
                group.getChildren().add(s);
            } else {
                group.getChildren().removeIf(s -> s.getId() != null && s.getId().equals("knot"));
            }
        }));
        showControlPoints.addListener((obs, b, b1) -> interpolate.getSplines().forEach(spline -> {
            Point3D k0 = spline.getPoints().get(0);
            Point3D k1 = spline.getPoints().get(1);
            Point3D k2 = spline.getPoints().get(2);
            Point3D k3 = spline.getPoints().get(3);
            if (showControlPoints.get()) {
                Point3D dir = k1.substract(k0).crossProduct(new Point3D(0, -1, 0));
                double angle = Math.acos(k1.substract(k0).normalize().dotProduct(new Point3D(0, -1, 0)));
                double h1 = k1.substract(k0).magnitude();
                Cylinder c = new Cylinder(0.03d, h1);
                c.getTransforms().addAll(new Translate(k0.x, k0.y - h1 / 2d, k0.z),
                        new Rotate(-Math.toDegrees(angle), 0d, h1 / 2d, 0d,
                                new javafx.geometry.Point3D(dir.x, -dir.y, dir.z)));
                c.setMaterial(new PhongMaterial(Color.GREEN));
                c.setId("Control");
                group.getChildren().add(c);

                dir = k2.substract(k1).crossProduct(new Point3D(0, -1, 0));
                angle = Math.acos(k2.substract(k1).normalize().dotProduct(new Point3D(0, -1, 0)));
                h1 = k2.substract(k1).magnitude();
                c = new Cylinder(0.03d, h1);
                c.getTransforms().addAll(new Translate(k1.x, k1.y - h1 / 2d, k1.z),
                        new Rotate(-Math.toDegrees(angle), 0d, h1 / 2d, 0d,
                                new javafx.geometry.Point3D(dir.x, -dir.y, dir.z)));
                c.setMaterial(new PhongMaterial(Color.GREEN));
                c.setId("Control");
                group.getChildren().add(c);

                dir = k3.substract(k2).crossProduct(new Point3D(0, -1, 0));
                angle = Math.acos(k3.substract(k2).normalize().dotProduct(new Point3D(0, -1, 0)));
                h1 = k3.substract(k2).magnitude();
                c = new Cylinder(0.03d, h1);
                c.getTransforms().addAll(new Translate(k2.x, k2.y - h1 / 2d, k2.z),
                        new Rotate(-Math.toDegrees(angle), 0d, h1 / 2d, 0d,
                                new javafx.geometry.Point3D(dir.x, -dir.y, dir.z)));
                c.setMaterial(new PhongMaterial(Color.GREEN));
                c.setId("Control");
                group.getChildren().add(c);

                Sphere s = new Sphere(0.1d);
                s.getTransforms().add(new Translate(k1.x, k1.y, k1.z));
                s.setMaterial(new PhongMaterial(Color.RED));
                s.setId("Control");
                group.getChildren().add(s);
                s = new Sphere(0.1d);
                s.getTransforms().add(new Translate(k2.x, k2.y, k2.z));
                s.setMaterial(new PhongMaterial(Color.RED));
                s.setId("Control");
                group.getChildren().add(s);
            } else {
                group.getChildren().removeIf(s -> s.getId() != null && s.getId().equals("Control"));
            }
        }));

        long time = System.currentTimeMillis();
        interpolate.getSplines().stream().forEach(spline -> {
            BezierMesh bezier = new BezierMesh(spline, 0.1d,
                    300, 20, 0, 0);
//            bezier.setDrawMode(DrawMode.LINE);
            bezier.setCullFace(CullFace.NONE);
//            bezier.setSectionType(SectionType.TRIANGLE);

            // NONE
//            bezier.setTextureModeNone(Color.hsb(360d*sp.getAndIncrement()/interpolate.getSplines().size(), 1, 1));
            // IMAGE
//            bezier.setTextureModeImage(getClass().getResource("res/LaminateSteel.jpg").toExternalForm());
            // PATTERN
//           bezier.setTextureModePattern(3d);
            // FUNCTION
//            bezier.setTextureModeVertices1D(256*256,t->spline.getKappa(t));
            // DENSITY
//            bezier.setTextureModeVertices3D(256*256,dens);
            // FACES
            bezier.setTextureModeFaces(256 * 256);

            bezier.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);
            beziers.add(bezier);
        });
        System.out.println("time: " + (System.currentTimeMillis() - time)); //43.815->25.606->15
        group.getChildren().addAll(beziers);

        sceneRoot.getChildren().addAll(group);

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
                cameraTransform.t.setX(cameraTransform.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3);  // -
                cameraTransform.t.setY(cameraTransform.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3);  // -
            }
        });

        lastEffect = System.nanoTime();
        AtomicInteger count = new AtomicInteger();
        AnimationTimer timerEffect = new AnimationTimer() {

            @Override
            public void handle(long now) {
                
                if (now > lastEffect + 5_000_000_000l && subScene.getScene() != null) {
                    System.out.println(subScene.getScene().getRoot().getClass().getName());
                    
//                    Point3D loc = knot.getPositionAt((count.get()%100)*2d*Math.PI/100d);
//                    Point3D dir = knot.getTangentAt((count.get()%100)*2d*Math.PI/100d);
//                    cameraTransform.t.setX(loc.x);
//                    cameraTransform.t.setY(loc.y);
//                    cameraTransform.t.setZ(-loc.z);
//                    javafx.geometry.Point3D axis = cameraTransform.rx.getAxis();
//                    javafx.geometry.Point3D cross = axis.crossProduct(-dir.x,-dir.y,-dir.z);
//                    double angle = axis.angle(-dir.x,-dir.y,-dir.z);
//                    cameraTransform.rx.setAngle(angle);
//                    cameraTransform.rx.setAxis(new javafx.geometry.Point3D(cross.getX(),-cross.getY(),cross.getZ()));
//                    dens = p->(float)(p.x*Math.cos(count.get()%100d*2d*Math.PI/50d)+p.y*Math.sin(count.get()%100d*2d*Math.PI/50d));
//                    beziers.forEach(b->b.setDensity(dens));
//                    knot.setP(1+(count.get()%5));
//                    knot.setQ(2+(count.get()%15));

//                    if(count.get()%100<50){
//                        knot.setDrawMode(DrawMode.LINE);
//                    } else {
//                        knot.setDrawMode(DrawMode.FILL);
//                    }
//                    beziers.forEach(b->b.setColors((int)Math.pow(2,count.get()%16)));
//                    beziers.forEach(b->b.setWireRadius(0.1d+(count.get()%6)/10d));
//                    beziers.forEach(b->b.setPatternScale(1d+(count.get()%10)*3d));
//                    beziers.forEach(b->b.setSectionType(SectionType.values()[count.get()%SectionType.values().length]));
                   // count.getAndIncrement();
                    //lastEffect = now;
                }
            }
        };
        
        //timerEffect.start();
        
        StackPane sp = new StackPane();
        sp.setPrefSize(sceneWidth, sceneHeight);
        sp.setMaxSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        sp.setMinSize(StackPane.USE_COMPUTED_SIZE, StackPane.USE_COMPUTED_SIZE);
        sp.setBackground(Background.EMPTY);
        sp.getChildren().add(subScene);
        sp.setPickOnBounds(false);
        
        subScene.widthProperty().bind(sp.widthProperty());
        subScene.heightProperty().bind(sp.heightProperty());
        
        return (sp);
    }

    @Override
    public String getSampleName() {
        return getClass().getSimpleName().concat(" Sample");
    }

    @Override
    public Node getPanel(Stage stage) {
        try {
            return getSample();    // does not work
            //return new Button("Adding root(group) doesn't work .. But this worked .. Looks like we need to change things to SubScene or some other Node");
        } catch (Exception ex) {
            Logger.getLogger(BezierMeshes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Node getControlPanel() {
        Accordion accordion = new Accordion();
        /**
         * **************************
         **** TITLEDPANE 1. FRAME ****
        ****************************
         */
        
        TitledPane tpFrame = new TitledPane();
        tpFrame.setText("Frame");
        // the result
        GridPane lGridPane = new GridPane();
        lGridPane.setVgap(2.0);
        lGridPane.setHgap(2.0);

        // setup the grid so all the labels will not grow, but the rest will
        ColumnConstraints lColumnConstraintsAlwaysGrow = new ColumnConstraints();
        lColumnConstraintsAlwaysGrow.setHgrow(Priority.ALWAYS);
        ColumnConstraints lColumnConstraintsNeverGrow = new ColumnConstraints();
        lColumnConstraintsNeverGrow.setHgrow(Priority.NEVER);
        lGridPane.getColumnConstraints().addAll(lColumnConstraintsNeverGrow, lColumnConstraintsAlwaysGrow);
        int lRowIdx = 0;

        CheckBox chkKnots = new CheckBox();
        chkKnots.setSelected(showKnots.get());
        Label lLabel = new Label("Show Knots: ");
        lLabel.setTooltip(new Tooltip("Select if the knots are visible or not"));
        lGridPane.add(lLabel, 0, lRowIdx);
        chkKnots.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            showKnots.set(t1);
        });
        lGridPane.add(chkKnots, 1, lRowIdx);
        lRowIdx++;

        CheckBox chkControl = new CheckBox();
        chkControl.setSelected(showControlPoints.get());
        Label lControl = new Label("Show Control Points: ");
        lControl.setTooltip(new Tooltip("Select if the control points are visible or not"));
        lGridPane.add(lControl, 0, lRowIdx);
        chkControl.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            showControlPoints.set(t1);
        });
        lGridPane.add(chkControl, 1, lRowIdx);
        lRowIdx++;

        tpFrame.setContent(lGridPane);
        
        accordion.getPanes().addAll(tpFrame);
        accordion.setExpandedPane(tpFrame);

        return accordion;
    }

    @Override
    public String getJavaDocURL() {
        return "";
    }

    @Override
    public String getSampleDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nBezierMesh:\nAllows for a Tubular mesh to be built using a BezierCurve method ")
                .append("allowing the use of control points in 3D space.");
        return sb.toString();
    }

    @Override
    public double getControlPanelDividerPosition() {
        return 0.8D;
    }

}
