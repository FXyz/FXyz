package org.fxyz.samples;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.fxyz.ShapeBaseSample;
import org.fxyz.controls.BoolPropertyControl;
import org.fxyz.controls.ControlFactory;
import org.fxyz.geometry.DensityFunction;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.BezierMesh;
import org.fxyz.shapes.primitives.helper.BezierHelper;
import org.fxyz.shapes.primitives.helper.InterpolateBezier;

/**
 *
 * @author jpereda
 */
public class BezierMeshes extends ShapeBaseSample {

    long lastEffect;

    private final BooleanProperty showKnots = new SimpleBooleanProperty(this,"Show Knots");
    private final BooleanProperty showControlPoints = new SimpleBooleanProperty(this,"Show Control Points");
    
    private List<BezierMesh> beziers;
    private List<BezierHelper> splines;

    @Override
    protected void createMesh() {
        
        List<Point3D> knots = Arrays.asList(new Point3D(3f, 0f, 0f), new Point3D(0.77171f, 1.68981f, 0.989821f),
                new Point3D(-0.681387f, 0.786363f, -0.281733f), new Point3D(-2.31757f, -0.680501f, -0.909632f),
                new Point3D(-0.404353f, -2.81233f, 0.540641f), new Point3D(1.1316f, -0.727237f, 0.75575f),
                new Point3D(1.1316f, 0.727237f, -0.75575f), new Point3D(-0.404353f, 2.81233f, -0.540641f),
                new Point3D(-2.31757f, 0.680501f, 0.909632f), new Point3D(-0.681387f, -0.786363f, 0.281733f),
                new Point3D(0.77171f, -1.68981f, -0.989821f), new Point3D(3f, 0f, 0f));
        InterpolateBezier interpolate = new InterpolateBezier(knots);
        splines = interpolate.getSplines();
        beziers = splines.parallelStream().map(spline -> {
            BezierMesh bezier = new BezierMesh(spline, 0.1d, 200, 10, 0, 0);
            bezier.setTextureModeNone(Color.ROYALBLUE);
//            CountDownLatch latch=new CountDownLatch(1);
//            Platform.runLater(()->{group.getChildren().add(bezier);latch.countDown();});
//            try {
//                latch.await();
//            } catch (InterruptedException ex) {}
            return bezier;
        }).collect(Collectors.toList());
    }
    
    @Override
    protected void addMeshAndListeners() {  
        System.out.println("building");
        
        group.getChildren().addAll(beziers);
        beziers.forEach(bezier->bezier.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY));
        
        DensityFunction<Point3D> dens = p -> (double) p.f;

        showKnots.addListener((obs, b, b1) -> splines.forEach(spline -> {
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
        showControlPoints.addListener((obs, b, b1) -> splines.forEach(spline -> {
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

        
        
//            bezier.setDrawMode(DrawMode.LINE);
//            bezier.setCullFace(CullFace.NONE);
//            bezier.setSectionType(SectionType.TRIANGLE);

            // NONE
//            bezier.setTextureModeNone(Color.hsb(360d*mainPane.getAndIncrement()/interpolate.getSplines().size(), 1, 1));
            // IMAGE
//            bezier.setTextureModeImage(getClass().getResource("res/LaminateSteel.jpg").toExternalForm());
            // PATTERN
//           bezier.setTextureModePattern(3d);
            // FUNCTION
//            bezier.setTextureModeVertices1D(256*256,t->spline.getKappa(t));
            // DENSITY
//            bezier.setTextureModeVertices3D(256*256,dens);
            // FACES
//            bezier.setTextureModeFaces(256 * 256);
        
//        beziers.forEach(b->b.setTextureModeFaces(256 * 256));
        
        

        lastEffect = System.nanoTime();
        AtomicInteger count = new AtomicInteger();
        AnimationTimer timerEffect = new AnimationTimer() {

            @Override
            public void handle(long now) {
                
                if (now > lastEffect + 5_000_000_000l && getScene() != null) {
                    
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
    }

    @Override
    public Node getControlPanel() {
        
        // Accordian Will also be replaced with FXML controls
        // including TitledPanes allowing for SubSections
        // This is just a demo of the ControlerFactory / BooleanControl
        
        Accordion accordion = new Accordion();
        TitledPane tpFrame = new TitledPane();
        tpFrame.setText("Frame");        
        VBox fr = new VBox(); 
        fr.setSpacing(3);
        fr.setFillWidth(true);
        fr.setAlignment(Pos.TOP_LEFT);
        fr.setPadding(new Insets(3,3,3,3));
        
        //######################################################################
        
        BoolPropertyControl chkKnots = ControlFactory.buildBooleanControl(showKnots);
        chkKnots.loadBindings();
        BoolPropertyControl chkPnts = ControlFactory.buildBooleanControl(showControlPoints);
        chkPnts.loadBindings();
        
        //######################################################################
        
        fr.getChildren().addAll(chkKnots,chkPnts);
        tpFrame.setContent(fr);
        accordion.getPanes().addAll(tpFrame);
        accordion.setExpandedPane(tpFrame);
        return accordion;
    }

    @Override
    public String getSampleDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nBezierMesh:\nAllows for a Tubular mesh to be built using a BezierCurve method ")
                .append("allowing the use of control points in 3D space.");
        return sb.toString();
    }

}
