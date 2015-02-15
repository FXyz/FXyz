package org.fxyz.samples.shapes.texturedmeshes;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import static javafx.application.Application.launch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.fxyz.controls.ControlPanel;
import org.fxyz.controls.LightingControls;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.SectionLabel;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.geometry.Point3D;
import org.fxyz.samples.shapes.ShapeBaseSample;
import org.fxyz.shapes.primitives.BezierMesh;
import org.fxyz.shapes.primitives.PrismMesh;
import org.fxyz.shapes.primitives.TexturedMesh;
import org.fxyz.shapes.primitives.helper.BezierHelper;
import org.fxyz.shapes.primitives.helper.InterpolateBezier;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.TextureType;

/**
 *
 * @author jpereda
 */
public class BezierMeshes extends ShapeBaseSample {

    public static void main(String[] args) {
        launch(args);
    }

    private final BooleanProperty showKnots = new SimpleBooleanProperty(this, "Show Knots");
    private final BooleanProperty showControlPoints = new SimpleBooleanProperty(this, "Show Control Points");

    private final DoubleProperty wireRad = new SimpleDoubleProperty(this, "Wire Radius");
    protected final ObjectProperty<Color> colorBinding = new SimpleObjectProperty<Color>(Color.BROWN) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null && !(((Group) model).getChildren().filtered(t -> t instanceof TexturedMesh && ((TexturedMesh) t).getTextureType().equals(TextureType.NONE))).isEmpty()) {
                ((Group) model).getChildren().filtered(t -> t instanceof Shape3D)
                        .forEach(s -> {
                            ((PhongMaterial) ((Shape3D) s).getMaterial()).setDiffuseColor(getValue());
                        });
            }
        }
    };
    protected final IntegerProperty colors = new SimpleIntegerProperty(model, "Color :", 700) {
        @Override
        protected void invalidated() {
            super.invalidated();

            if (model != null) {
                colorBinding.set(Color.hsb(360 * (1d - get() / 1530d), 1, 1));
            }
        }
    };

    protected final Property<TriangleMeshHelper.TextureType> textureType = new SimpleObjectProperty<TriangleMeshHelper.TextureType>(model, "texType", TriangleMeshHelper.TextureType.NONE) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                switch (getValue()) {
                    case NONE:
                        if (model != null && !(((Group) model).getChildren().filtered(t -> t instanceof TexturedMesh && 
                                ((TexturedMesh) t).getTextureType().equals(TextureType.NONE))).isEmpty()) {
                            ((Group) model).getChildren().filtered(t -> t instanceof Shape3D)
                                    .forEach(s -> {
                                        ((TexturedMesh) s).setTextureModeNone(colorBinding.get());
                                    });
                        }

                        break;
                    case IMAGE:
                        break;/*
                     if (model != null && !(((Group) model).getChildren().filtered(t -> t instanceof TexturedMesh && ((TexturedMesh) t).getTextureType().equals(TextureType.NONE))).isEmpty()) {
                     ((Group) model).getChildren().filtered(t -> t instanceof Shape3D)
                     .forEach(s -> {
                     ((TexturedMesh) model).setTextureModeImage(diffMapPath.get());
                     });
                     }
                     break;*/

                    case PATTERN:
                        if (model != null && !(((Group) model).getChildren().filtered(t -> t instanceof TexturedMesh && 
                                ((TexturedMesh) t).getTextureType().equals(TextureType.NONE))).isEmpty()) {
                            ((Group) model).getChildren().filtered(t -> t instanceof Shape3D)
                                    .forEach(s -> {
                                        ((TexturedMesh) s).setTextureModePattern(pattScale.getValue());
                                    });
                        }
                        break;
                    case COLORED_VERTICES_1D:
                        if (model != null && !(((Group) model).getChildren().filtered(t -> t instanceof TexturedMesh && 
                                ((TexturedMesh) t).getTextureType().equals(TextureType.NONE))).isEmpty()) {
                            ((Group) model).getChildren().filtered(t -> t instanceof Shape3D)
                                    .forEach(s -> {
                                        ((TexturedMesh) s).setTextureModeVertices1D(1540, func.getValue());
                                    });
                        }
                        break;
                    case COLORED_VERTICES_3D:
                        if (model != null && !(((Group) model).getChildren().filtered(t -> t instanceof TexturedMesh && 
                                ((TexturedMesh) t).getTextureType().equals(TextureType.NONE))).isEmpty()) {
                            ((Group) model).getChildren().filtered(t -> t instanceof Shape3D)
                                    .forEach(s -> {
                                        ((TexturedMesh) s).setTextureModeVertices3D(1600, dens.getValue());
                                    });
                        }
                        break;
                    case COLORED_FACES:
                        if (model != null && !(((Group) model).getChildren().filtered(t -> t instanceof TexturedMesh && 
                                ((TexturedMesh) t).getTextureType().equals(TextureType.NONE))).isEmpty()) {
                            ((Group) model).getChildren().filtered(t -> t instanceof Shape3D)
                                    .forEach(s -> {
                                        ((TexturedMesh) s).setTextureModeFaces(1550);
                                    });
                        }

                        break;
                }
            }
        }
    };

    /*
     TriangleMeshHelper.TextureType.PATTERN 
     */
    protected final DoubleProperty pattScale = new SimpleDoubleProperty(this, "Pattern Scale: ", 2.0d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null && !(((Group) model).getChildren().filtered(t -> t instanceof TexturedMesh && 
                    ((TexturedMesh) t).getTextureType().equals(TextureType.PATTERN))).isEmpty()) {
                ((Group) model).getChildren().filtered(t -> t instanceof Shape3D)
                        .forEach(s -> {
                            ((TexturedMesh) s).setPatternScale(pattScale.doubleValue());
                        });
            }
        }
    };

    /*
     TriangleMeshHelper.TextureType.COLORED_VERTICES_3D 
     */
    protected final DoubleProperty densMax = new SimpleDoubleProperty(this, "Density Scale: ");
    protected final Property<Function<Point3D, Number>> dens = new SimpleObjectProperty<Function<Point3D, Number>>(p -> p.x * p.y * p.z) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null && !(((Group) model).getChildren().filtered(t -> t instanceof TexturedMesh && 
                    ((TexturedMesh) t).getTextureType().equals(TextureType.COLORED_VERTICES_3D))).isEmpty()) {
                ((Group) model).getChildren().filtered(t -> t instanceof Shape3D)
                        .forEach(s -> {
                            ((TexturedMesh) s).setDensity(dens.getValue());
                        });
            }
        }
    };
    /*
     TriangleMeshHelper.TextureType.COLORED_VERTICES_1D 
     */
    protected final Property<Function<Number, Number>> func = new SimpleObjectProperty<Function<Number, Number>>(t -> t) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null && !(((Group) model).getChildren().filtered(t -> t instanceof TexturedMesh && 
                    ((TexturedMesh) t).getTextureType().equals(TextureType.COLORED_VERTICES_1D))).isEmpty()) {
                ((Group) model).getChildren().filtered(t -> t instanceof Shape3D)
                        .forEach(s -> {
                            ((TexturedMesh) s).setFunction(func.getValue());
                        });
            }
        }
    };
    
    /*
     TriangleMeshHelper.TextureType.IMAGE 
    */
    protected final StringProperty diffMapPath = new SimpleStringProperty(this, "imagePath", "");
    protected final Property<Boolean> useDiffMap = new SimpleBooleanProperty(this, "Use PhongMaterial", false) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null && textureType.getValue().equals(TriangleMeshHelper.TextureType.IMAGE)) {
                if (diffMapPath.get().isEmpty()) {
                    //load default
                    material.setDiffuseMap((new Image(getClass().getResource("samples/res/LaminateSteel.jpg").toExternalForm())));
                } else {
                    try { // should be given the string from filechooser
                        material.setDiffuseMap(new Image(new FileInputStream(new File(diffMapPath.get()))));
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        }
    };

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
        Function<Point3D, Double> dens = p -> (double) p.f;
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

        NumberSliderControl radSlider = ControlFactory.buildNumberSlider(wireRad, 0.1D, 0.5D);
        radSlider.getSlider().setMinorTickCount(4);
        radSlider.getSlider().setMajorTickUnit(0.5);
        radSlider.getSlider().setBlockIncrement(0.1d);
        radSlider.getSlider().setSnapToTicks(true);

        ControlPanel panel = ControlFactory.buildRootControlPanel();
        panel.addToRoot(
                new SectionLabel("Scene And Lighting"),
                new LightingControls(
                        group.visibleProperty(),
                        sceneLight1.lightOnProperty(),
                        sceneLight1.colorProperty(),
                        sceneLight1.translateXProperty(),
                        sceneLight1.rotateProperty(),
                        sceneLight1.rotationAxisProperty()
                ),
                new SectionLabel("Light 2"),
                new LightingControls(
                        group.visibleProperty(),
                        sceneLight2.lightOnProperty(),
                        sceneLight2.colorProperty(),
                        sceneLight2.translateXProperty(),
                        sceneLight2.rotateProperty(),
                        sceneLight2.rotationAxisProperty()
                ),
                new SectionLabel("MeshView Properties"),
                ControlFactory.buildDrawModeControl(drawMode),
                ControlFactory.buildCullFaceControl(culling),
                new SectionLabel("Geometry Properties"),
                ControlFactory.buildCheckBoxControl(showKnots),
                ControlFactory.buildCheckBoxControl(showControlPoints),
                radSlider,
                
                new SectionLabel("TexturedMesh Properties"),
                ControlFactory.buildTextureTypeControl(textureType, colors, useDiffMap, material.diffuseMapProperty(), pattScale, dens, func)
        );

        return panel;
    }

}
