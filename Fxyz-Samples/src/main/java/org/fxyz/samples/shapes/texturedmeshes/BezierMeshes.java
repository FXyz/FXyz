package org.fxyz.samples.shapes.texturedmeshes;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.fxyz.samples.shapes.ShapeBaseSample;
import org.fxyz.controls.CheckBoxControl;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.ControlPanel;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.BezierMesh;
import org.fxyz.shapes.primitives.PrismMesh;
import org.fxyz.shapes.primitives.helper.BezierHelper;
import org.fxyz.shapes.primitives.helper.InterpolateBezier;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.TextureType;

/**
 *
 * @author jpereda
 */
public class BezierMeshes extends ShapeBaseSample {

    public static void main(String[] args){
        launch(args);
    }
    
    private final BooleanProperty showKnots = new SimpleBooleanProperty(this, "Show Knots");
    private final BooleanProperty showControlPoints = new SimpleBooleanProperty(this, "Show Control Points");

    private final DoubleProperty wireRad = new SimpleDoubleProperty(this, "Wire Radius");
    private final DoubleProperty _x = new SimpleDoubleProperty(this, "X Offset");
    private final DoubleProperty _y = new SimpleDoubleProperty(this, "Y Offset");
    private final DoubleProperty _z = new SimpleDoubleProperty(this, "Z Offset");
    private final DoubleProperty _angle = new SimpleDoubleProperty(this, "Tube Angle Offset");

    private final IntegerProperty colors = new SimpleIntegerProperty(this, "Wire Divisions");
    private final IntegerProperty lenDivs = new SimpleIntegerProperty(this, "Length Divisions");
    private final IntegerProperty wireCrop = new SimpleIntegerProperty(this, "Wire Crop");
    private final IntegerProperty lenCrop = new SimpleIntegerProperty(this, "Length Crop");
    
    private final Property<TextureType> texType = new SimpleObjectProperty<>(this, "Texture Type");

    private List<BezierMesh> beziers;
    private List<BezierHelper> splines;

    @Override
    protected void createMesh() {
        System.err.println(showKnots.getClass().getSimpleName());
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
            return bezier;
        }).collect(Collectors.toList());
    }

    @Override
    protected void addMeshAndListeners() {
        Group bez = new Group();
        bez.getChildren().addAll(beziers);
        beziers.forEach(bezier -> {
            bezier.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);
        });
        model = bez;
        Function<Point3D,Double> dens = p -> (double) p.f;
        wireRad.addListener(i -> {
            beziers.forEach(bm -> {
                bm.setWireRadius(wireRad.doubleValue());
            });
        });
        colors.addListener(i -> {
            beziers.forEach(bm -> {
                bm.setColors(colors.intValue());
            });
        });
        showKnots.addListener((obs, b, b1) -> splines.forEach(spline -> {
            if (showKnots.get()) {
                Point3D k0 = spline.getPoints().get(0);
                Point3D k3 = spline.getPoints().get(3);
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
            if (showControlPoints.get()) {
                Point3D k0 = spline.getPoints().get(0);
                Point3D k1 = spline.getPoints().get(1);
                Point3D k2 = spline.getPoints().get(2);
                Point3D k3 = spline.getPoints().get(3);
                PrismMesh c = new PrismMesh(0.03d, 1d, 1, k0, k1);
                c.setTextureModeNone(Color.GREEN);
                c.setId("Control");
                group.getChildren().add(c);

                c = new PrismMesh(0.03d, 1d, 1, k1, k2);
                c.setTextureModeNone(Color.GREEN);
                c.setId("Control");
                group.getChildren().add(c);

                c = new PrismMesh(0.03d, 1d, 1, k2, k3);
                c.setTextureModeNone(Color.GREEN);
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
    }

    @Override
    public String getSampleDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nBezierMesh:\nAllows for a Tubular mesh to be built using a BezierCurve method ")
                .append("allowing the use of control points in 3D space.");
        return sb.toString();
    }

    @Override
    protected Node buildControlPanel() {
        DropShadow is = new DropShadow();
        is.setBlurType(BlurType.GAUSSIAN);
        is.setColor(Color.GAINSBORO);
        is.setHeight(1.5);
        is.setWidth(0.5);
        is.setOffsetY(1);
        is.setRadius(5);
        
        CheckBoxControl chkKnots = ControlFactory.buildCheckBoxControl(showKnots);
//        chkKnots.setEffect(is);
        CheckBoxControl chkPnts = ControlFactory.buildCheckBoxControl(showControlPoints);
//        chkPnts.setEffect(is);
        
        NumberSliderControl radSlider = ControlFactory.buildNumberSlider(wireRad, 0.1D, 0.5D);
        radSlider.getSlider().setMinorTickCount(4);
        radSlider.getSlider().setMajorTickUnit(0.5);
        radSlider.getSlider().setBlockIncrement(0.1d);
        radSlider.getSlider().setSnapToTicks(true);
//        radSlider.setEffect(is);
                
        NumberSliderControl wDivSlider = ControlFactory.buildNumberSlider(colors, 8.0D, 255.0D);
        wDivSlider.getSlider().setBlockIncrement(1);
        wDivSlider.getSlider().setMajorTickUnit(63);
        wDivSlider.getSlider().setMinorTickCount(254);
        wDivSlider.getSlider().setSnapToTicks(true);
//        wDivSlider.setEffect(is);
        
        NumberSliderControl wCropSlider = ControlFactory.buildNumberSlider(wireCrop, 1, 30.0D);
        wCropSlider.getSlider().setBlockIncrement(1);
        wCropSlider.getSlider().setMajorTickUnit(28);
        wCropSlider.getSlider().setMinorTickCount(29);
        wCropSlider.getSlider().setSnapToTicks(true);
//        wCropSlider.setEffect(is);
        
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(chkKnots, chkPnts, wDivSlider, radSlider, wCropSlider);
        geomControls.setExpanded(true);
//        geomControls.setEffect(is);
//        geomControls.getContent().setEffect(is);

        ControlPanel cPanel = ControlFactory.buildControlPanel(geomControls);
        cPanel.setExpandedPane(geomControls);       

        return cPanel;
    }

}
