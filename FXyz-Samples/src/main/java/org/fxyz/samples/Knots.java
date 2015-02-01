package org.fxyz.samples;

import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
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
 * @author jpereda
 */
public class Knots extends ShapeBaseSample<KnotMesh> {

    private final DoubleProperty pattScale = new SimpleDoubleProperty(this, "Pattern Scale: ", 1.0d);
    private DensityFunction<Point3D> dens = p -> (double) p.x;
    private final DoubleProperty densMax = new SimpleDoubleProperty(this, "Density Scale: ", 100.0d);
    //standard
    private final Property<Boolean> useMaterial = new SimpleBooleanProperty(this, "Use PhongMaterial", false);
   
    private final Property<DrawMode> drawMode = new SimpleObjectProperty<>(DrawMode.FILL);
    private final Property<CullFace> culling = new SimpleObjectProperty<>(CullFace.BACK);
    //specific
    private final Property<SectionType> sectionType = new SimpleObjectProperty<>(model, "", SectionType.TRIANGLE);
    private final Property<TextureType> textureType = new SimpleObjectProperty<>(model, "", TextureType.NONE);
    /*
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
     */
    private final DoubleProperty majRad = new SimpleDoubleProperty(this, "Major Radius", 2);
    private final DoubleProperty minRad = new SimpleDoubleProperty(this, "Minor Radius", 1);
    private final DoubleProperty wireRad = new SimpleDoubleProperty(this, "Wire Radius", 0.2);
    private final DoubleProperty wireLen = new SimpleDoubleProperty(this, "Wire Length");

    private final DoubleProperty _p = new SimpleDoubleProperty(this, "P Value", 2);
    private final DoubleProperty _q = new SimpleDoubleProperty(this, "Q Value", 3);

    private final DoubleProperty _x = new SimpleDoubleProperty(this, "X Offset");
    private final DoubleProperty _y = new SimpleDoubleProperty(this, "Y Offset");
    private final DoubleProperty _z = new SimpleDoubleProperty(this, "Z Offset");
    private final DoubleProperty _angle = new SimpleDoubleProperty(this, "Tube Angle Offset");

    private final ObjectProperty<Color> colorBinding = new SimpleObjectProperty<Color>() {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setDiffuseColor(colorBinding.get());
            }
        }
    };
    private final IntegerProperty colors = new SimpleIntegerProperty(this, "Colors :", 1530) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                colorBinding.set(Color.hsb(360 * (1d - colors.get() / 1530d), 1, 1));
            }
        }
    };
    private final IntegerProperty wireDivs = new SimpleIntegerProperty(this, "Wire Divisions", 100) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setWireDivisions(wireDivs.get());
            }
        }
    };
    private final IntegerProperty lenDivs = new SimpleIntegerProperty(this, "Length Divisions", 500) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setLengthDivisions(lenDivs.get());
            }
        }
    };
    private final IntegerProperty wireCrop = new SimpleIntegerProperty(this, "Wire Crop", 0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                model.setWireCrop(wireCrop.get());
            }
        }
    };
    private final IntegerProperty lenCrop = new SimpleIntegerProperty(this, "Length Crop", 0) {
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

        model.setTextureModeNone(Color.BROWN);

        // DOES NOT work binding the other way prop->mesh
        model.drawModeProperty().bindBidirectional(drawMode);
        model.cullFaceProperty().bindBidirectional(culling);

        //model.textureTypeProperty().bindBidirectional(textureType);
        //model.sectionTypeProperty().bindBidirectional(sectionType);

        //model.majorRadiusProperty().bindBidirectional(majRad);
        //model.minorRadiusProperty().bindBidirectional(minRad);

        //model.wireRadiusProperty().bindBidirectional(wireRad);
        //model.wireDivisionsProperty().bindBidirectional(wireDivs);
        //model.wireCropProperty().bindBidirectional(wireCrop);

        //model.lengthProperty().bindBidirectional(wireLen);
        //model.lengthDivisionsProperty().bindBidirectional(lenDivs);
        //model.lengthCropProperty().bindBidirectional(lenCrop);

        //model.pProperty().bindBidirectional(_p);
        //model.qProperty().bindBidirectional(_q);
        //model.xOffsetProperty().bindBidirectional(_x);
        //model.yOffsetProperty().bindBidirectional(_y);
        //model.zOffsetProperty().bindBidirectional(_z);
        //model.tubeStartAngleOffsetProperty().bindBidirectional(_angle);
        buildControlPanel();
    }

    @Override
    protected void addMeshAndListeners() {

        useMaterial.addListener(new WeakInvalidationListener(i -> {
            if (useMaterial.getValue()) {
                model.setMaterial(material);
            } else if (!useMaterial.getValue()) {
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
        NumberSliderControl majRadSlider = ControlFactory.buildNumberSlider(majRad, 1D, 100D);
        majRadSlider.getSlider().setMinorTickCount(10);
        majRadSlider.getSlider().setMajorTickUnit(0.5);
        majRadSlider.getSlider().setBlockIncrement(0.1d);

        NumberSliderControl minRadSlider = ControlFactory.buildNumberSlider(minRad, 1D, 100D);
        minRadSlider.getSlider().setMinorTickCount(10);
        minRadSlider.getSlider().setMajorTickUnit(0.5);
        minRadSlider.getSlider().setBlockIncrement(0.1d);

        NumberSliderControl tRadSlider = ControlFactory.buildNumberSlider(wireRad, 0.01D, 10D);
        tRadSlider.getSlider().setMinorTickCount(1);
        tRadSlider.getSlider().setMajorTickUnit(0.1);
        tRadSlider.getSlider().setBlockIncrement(0.1d);

        NumberSliderControl wDivSlider = ControlFactory.buildNumberSlider(wireDivs, 2, 100l);
        wDivSlider.getSlider().setMinorTickCount(25);
        wDivSlider.getSlider().setMajorTickUnit(99);
        wDivSlider.getSlider().setBlockIncrement(1);
        wDivSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl mCropSlider = ControlFactory.buildNumberSlider(wireCrop, 0l, 98l);
        mCropSlider.getSlider().setMinorTickCount(48);
        mCropSlider.getSlider().setMajorTickUnit(49);
        mCropSlider.getSlider().setBlockIncrement(1);
        mCropSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl lDivSlider = ControlFactory.buildNumberSlider(lenDivs, 4l, 498l);
        lDivSlider.getSlider().setMinorTickCount(100);
        lDivSlider.getSlider().setMajorTickUnit(500);
        lDivSlider.getSlider().setBlockIncrement(1);

        NumberSliderControl lCropSlider = ControlFactory.buildNumberSlider(lenCrop, 0l, 250l);
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
                        useMaterial,
                        drawMode,
                        culling,
                        material.diffuseColorProperty(),
                        material.specularColorProperty()
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(textureType, colors, sectionType, useMaterial, material.diffuseMapProperty(), pattScale, densMax)
        );
        
        return controlPanel;
    }

}
