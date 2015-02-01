package org.fxyz.samples;

import java.io.File;
import java.io.FileInputStream;
import javafx.beans.WeakInvalidationListener;
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
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import org.fxyz.ShapeBaseSample;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.geometry.DensityFunction;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.KnotMesh;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.SectionType;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.TextureType;

/**
 *
 * 
 * 
     * TextureType.NONE: no texture applied, so diffuseColor can be used.
     Arguments:
     -setTextureModeNone() None (default color)
     -setTextureModeNone(Color color):  The color for diffuseColor.

     * TextureType.IMAGE: an image is required for the texture (diffuseMap), the user should be able to upload one, and we should provide one by default (like those for material tab)
     -setTextureModeImage(String image): the image for diffuseMap.

     * TextureType.PATTERN: the only pattern for now is the carbon pattern. So we can only play with the scale
     - setTextureModePattern(double scale):  scale of the pattern

     * TextureType.COLORED_VERTICES_3D: We provide a maximum number of colors (using the HSB palette, maximum is 1530 (6x255), that should be our default), and a density function of the type p->f(p.x,p.y,p.z)
     - setTextureModeVertices3D(int colors, DensityFunction<Point3D> dens) : I'm not sure how you will approach this as we need the user to enter a valid function. Max(f) and Min(f) are used to scale the funcion in the range of colors of the palette.
     - setTextureModeVertices3D(int colors, DensityFunction<Point3D> dens, double min, double max): Max and min are used to scale the function.

     * TextureType.COLORED_VERTICES_1D: We provide a maximum number of colors (using the HSB palette, maximum is 1530 (6x255), that should be our default), and a density function of the type x->f(x)
     - setTextureModeVertices1D(int colors, DensityFunction<Double> function) :The user has to enter a valid function. Max(f) and Min(f) are used to scale the funcion in the range of colors of the palette.
     - setTextureModeVertices1D(int colors, DensityFunction<Double> function, double min, double max): Max and min are used to scale the function.

     * TextureType.COLORED_FACES: We provide a maximum number of colors (using the HSB palette, maximum is 1530 (6x255), that should be our default)
     - setTextureModeFaces(int colors)

     In all these cases, colors is a property to indicate the maximum number of colors to generate the palette, and it should go inside the texture type control.
    
     KnotMesh(double majorRadius, double minorRadius, double wireRadius, double p, double q, 
     int rDivs, int tDivs, int lengthCrop, int wireCrop)
     
 * 
 * 
 * @author jpereda
 */
public class Knots extends ShapeBaseSample<KnotMesh> {

    private final DoubleProperty pattScale = new SimpleDoubleProperty(this, "Pattern Scale: ", 1.0d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setTextureModePattern(pattScale.doubleValue());
            }
        }
    };
    private DensityFunction<Point3D> dens = p -> (double) p.x;
    private final DoubleProperty densMax = new SimpleDoubleProperty(this, "Density Scale: ", 100.0d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setDensity(dens);
            }
        }
    };
    //standard
    private final StringProperty diffMapPath = new SimpleStringProperty(this, "imagePath", "");
    private final Property<Boolean> useDiffMap = new SimpleBooleanProperty(this, "Use PhongMaterial", false) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                if (diffMapPath.get().isEmpty()) {
                    //load default
                    material.setDiffuseMap(new Image(getClass().getResourceAsStream("res/LaminateSteel.jpg")));
                    //model.setTextureModeImage(STYLESHEET_MODENA);
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

    private final Property<DrawMode> drawMode = new SimpleObjectProperty<DrawMode>(model, "drawMode", DrawMode.FILL) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setDrawMode(drawMode.getValue());
            }
        }
    };
    private final Property<CullFace> culling = new SimpleObjectProperty<CullFace>(model, "culling", CullFace.BACK) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setCullFace(culling.getValue());
            }
        }
    };
    //specific
    private final Property<SectionType> sectionType = new SimpleObjectProperty<SectionType>(model, "secType", SectionType.TRIANGLE) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setSectionType(sectionType.getValue());
            }
        }
    };
    private final Property<TextureType> textureType = new SimpleObjectProperty<TextureType>(model, "texType", TextureType.NONE) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setTextureType(textureType.getValue());
            }
        }
    };

    private final DoubleProperty majRad = new SimpleDoubleProperty(model, "Major Radius", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setMajorRadius(majRad.get());
            }
        }
    };
    private final DoubleProperty minRad = new SimpleDoubleProperty(model, "Minor Radius", 1) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setMinorRadius(minRad.get());
            }
        }
    };
    private final DoubleProperty wireRad = new SimpleDoubleProperty(model, "Wire Radius", 0.2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setWireRadius(wireRad.get());
            }
        }
    };
    private final DoubleProperty wireLen = new SimpleDoubleProperty(model, "Wire Length") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setLength(wireLen.get());
            }
        }
    };

    private final DoubleProperty _p = new SimpleDoubleProperty(model, "P Value", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setP(_p.doubleValue());
            }
        }
    };
    private final DoubleProperty _q = new SimpleDoubleProperty(model, "Q Value", 3) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setQ(_q.doubleValue());
            }
        }
    };

    private final DoubleProperty _x = new SimpleDoubleProperty(model, "X Offset") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setxOffset(_x.doubleValue());
            }
        }
    };
    private final DoubleProperty _y = new SimpleDoubleProperty(model, "Y Offset") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setyOffset(_y.doubleValue());
            }
        }
    };
    private final DoubleProperty _z = new SimpleDoubleProperty(model, "Z Offset") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setzOffset(_z.doubleValue());
            }
        }
    };
    private final DoubleProperty _angle = new SimpleDoubleProperty(model, "Tube Angle Offset") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setTubeStartAngleOffset(_angle.doubleValue());
            }
        }
    };

    private final ObjectProperty<Color> colorBinding = new SimpleObjectProperty<Color>(Color.BROWN) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setDiffuseColor(colorBinding.get());
            }
        }
    };
    private final IntegerProperty colors = new SimpleIntegerProperty(model, "Color :", 1530) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                colorBinding.set(Color.hsb(360 * (1d - colors.get() / 1530d), 1, 1));
            }
        }
    };
    private final IntegerProperty wireDivs = new SimpleIntegerProperty(model, "Wire Divisions", 100) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setWireDivisions(wireDivs.get());
            }
        }
    };
    private final IntegerProperty lenDivs = new SimpleIntegerProperty(model, "Length Divisions", 500) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setLengthDivisions(lenDivs.get());
            }
        }
    };
    private final IntegerProperty wireCrop = new SimpleIntegerProperty(model, "Wire Crop", 0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setWireCrop(wireCrop.get());
            }
        }
    };
    private final IntegerProperty lenCrop = new SimpleIntegerProperty(model, "Length Crop", 0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setLengthCrop(lenCrop.get());
            }
        }

    };

    @Override
    protected void createMesh() {
        //model = new KnotMesh(2d, 1d, 0.4d, 2d, 3d, 1000, 60, 0, 0);
        model = new KnotMesh(
                majRad.get(),
                minRad.get(),
                wireRad.get(),
                _p.get(),
                _q.get(),
                wireDivs.get(),
                lenDivs.get(),
                lenCrop.get(),
                wireCrop.get()
        );
        model.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);
        model.setTextureModeNone(colorBinding.get());
        
        buildControlPanel();
    }

    @Override
    protected void addMeshAndListeners() {

        useDiffMap.addListener(new WeakInvalidationListener(i -> {
            if (useDiffMap.getValue()) {
                model.setMaterial(material);
            } else if (!useDiffMap.getValue()) {
                model.setMaterial(null);
            }
        }));

        // IMAGE
        //model.setTextureModeImage(getClass().getResource("res/LaminateSteel.jpg").toExternalForm());
        // PATTERN
        //model.setTextureModePattern(3d);
        // FUNCTION
        //model.setTextureModeVertices1D(256 * 256, t -> t * t);
        // DENSITY
        //model.setTextureModeVertices3D(256 * 256, dens);
        // FACES
        //model.setTextureModeFaces(256 * 256);
    }

    @Override
    public String getSampleDescription() {
        return "Knots, they tie things together ;)";
    }

    @Override
    protected Node buildControlPanel() {
        NumberSliderControl majRadSlider = ControlFactory.buildNumberSlider(majRad, 1D, 200D);
        majRadSlider.getSlider().setMinorTickCount(10);
        majRadSlider.getSlider().setMajorTickUnit(0.5);
        majRadSlider.getSlider().setBlockIncrement(0.1d);

        NumberSliderControl minRadSlider = ControlFactory.buildNumberSlider(minRad, 1D, 200D);
        minRadSlider.getSlider().setMinorTickCount(10);
        minRadSlider.getSlider().setMajorTickUnit(0.5);
        minRadSlider.getSlider().setBlockIncrement(0.1d);

        NumberSliderControl tRadSlider = ControlFactory.buildNumberSlider(wireRad, 0.01D, 25D);
        tRadSlider.getSlider().setMinorTickCount(1);
        tRadSlider.getSlider().setMajorTickUnit(0.1);
        tRadSlider.getSlider().setBlockIncrement(0.1d);

        NumberSliderControl wDivSlider = ControlFactory.buildNumberSlider(wireDivs, 2, 100);
        wDivSlider.getSlider().setMinorTickCount(25);
        wDivSlider.getSlider().setMajorTickUnit(99);
        wDivSlider.getSlider().setBlockIncrement(1);
        wDivSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl mCropSlider = ControlFactory.buildNumberSlider(wireCrop, 0l, 98);
        mCropSlider.getSlider().setMinorTickCount(48);
        mCropSlider.getSlider().setMajorTickUnit(49);
        mCropSlider.getSlider().setBlockIncrement(1);
        mCropSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl lDivSlider = ControlFactory.buildNumberSlider(lenDivs, 4l, 250);
        lDivSlider.getSlider().setMinorTickCount(50);
        lDivSlider.getSlider().setMajorTickUnit(250);
        lDivSlider.getSlider().setBlockIncrement(1);

        NumberSliderControl lCropSlider = ControlFactory.buildNumberSlider(lenCrop, 0l, 200);
        lCropSlider.getSlider().setMinorTickCount(0);
        lCropSlider.getSlider().setMajorTickUnit(0.5);
        lCropSlider.getSlider().setBlockIncrement(1);

        NumberSliderControl pSlider = ControlFactory.buildNumberSlider(_p, 0.01d, 10.0D);
        pSlider.getSlider().setMinorTickCount(99);
        pSlider.getSlider().setMajorTickUnit(100);
        pSlider.getSlider().setBlockIncrement(0.01);

        NumberSliderControl qSlider = ControlFactory.buildNumberSlider(_q, 0.0d, 50.0D);
        qSlider.getSlider().setMinorTickCount(49);
        qSlider.getSlider().setMajorTickUnit(50);
        qSlider.getSlider().setBlockIncrement(0.1);

        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(
                majRadSlider, minRadSlider,
                tRadSlider, wDivSlider, mCropSlider,
                lDivSlider, lCropSlider,
                pSlider, qSlider
        );

        controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        useDiffMap,
                        drawMode,
                        culling,
                        material.diffuseColorProperty(),
                        material.specularColorProperty()
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(textureType, colors, sectionType, useDiffMap, material.diffuseMapProperty(), pattScale, densMax)
        );

        return controlPanel;
    }

}
