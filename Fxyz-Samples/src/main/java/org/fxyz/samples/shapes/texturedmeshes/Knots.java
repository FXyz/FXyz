package org.fxyz.samples.shapes.texturedmeshes;

import java.util.Objects;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.samples.shapes.TexturedMeshSample;
import org.fxyz.shapes.primitives.KnotMesh;

/**
 *
 *
 *
 * TextureType.NONE: no texture applied, so diffuseColor can be used. Arguments:
 * -setTextureModeNone() None (default color) -setTextureModeNone(Color color):
 * The color for diffuseColor.
 *
 * TextureType.IMAGE: an image is required for the texture (diffuseMap), the
 * user should be able to upload one, and we should provide one by default (like
 * those for material tab) -setTextureModeImage(String image): the image for
 * diffuseMap.
 *
 * TextureType.PATTERN: the only pattern for now is the carbon pattern. So we
 * can only play with the scale - setTextureModePattern(double scale): scale of
 * the pattern
 *
 * TextureType.COLORED_VERTICES_3D: We provide a maximum number of colors (using
 * the HSB palette, maximum is 1530 (6x255), that should be our default), and a
 * density function of the type p- f(p.x,p.y,p.z) - setTextureModeVertices3D(int
 * colors, DensityFunction(Point3D) dens) : I'm not sure how you will approach
 * this as we need the user to enter a valid function. Max(f) and Min(f) are
 * used to scale the funcion in the range of colors of the palette. -
 * setTextureModeVertices3D(int colors, DensityFunction(Point3D) dens, double
 * min, double max): Max and min are used to scale the function.
 *
 * TextureType.COLORED_VERTICES_1D: We provide a maximum number of colors (using
 * the HSB palette, maximum is 1530 (6x255), that should be our default), and a
 * density function of the type x- f(x) - setTextureModeVertices1D(int colors,
 * DensityFunction(Double) function) :The user has to enter a valid function.
 * Max(f) and Min(f) are used to scale the funcion in the range of colors of the
 * palette. - setTextureModeVertices1D(int colors, DensityFunction(Double)
 * function, double min, double max): Max and min are used to scale the
 * function.
 *
 * TextureType.COLORED_FACES: We provide a maximum number of colors (using the
 * HSB palette, maximum is 1530 (6x255), that should be our default) -
 * setTextureModeFaces(int colors)
 *
 * In all these cases, colors is a property to indicate the maximum number of
 * colors to generate the palette, and it should go inside the texture type
 * control.
 *
 * KnotMesh(double majorRadius, double minorRadius, double wireRadius, double p,
 * double q, int rDivs, int tDivs, int lengthCrop, int wireCrop)
 *
 *
 *
 * @author jpereda
 */
public class Knots extends TexturedMeshSample {
    
    public static void main(String[] args){Knots.launch(args);}
    
    private final DoubleProperty majRad = new SimpleDoubleProperty(model, "Major Radius", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((KnotMesh)model).setMajorRadius(majRad.get());
            }
        }
    };
    private final DoubleProperty minRad = new SimpleDoubleProperty(model, "Minor Radius", 1) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((KnotMesh)model).setMinorRadius(minRad.get());
            }
        }
    };
    private final DoubleProperty wireRad = new SimpleDoubleProperty(model, "Wire Radius", 0.2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((KnotMesh)model).setWireRadius(wireRad.get());
            }
        }
    };
    private final DoubleProperty wireLen = new SimpleDoubleProperty(model, "Wire Length") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((KnotMesh)model).setLength(wireLen.get());
            }
        }
    };

    private final DoubleProperty _p = new SimpleDoubleProperty(model, "P Value", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((KnotMesh)model).setP(_p.doubleValue());
            }
        }
    };
    private final DoubleProperty _q = new SimpleDoubleProperty(model, "Q Value", 3) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((KnotMesh)model).setQ(_q.doubleValue());
            }
        }
    };
   
    private final IntegerProperty wireDivs = new SimpleIntegerProperty(model, "Wire Divisions", 100) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((KnotMesh)model).setWireDivisions(wireDivs.get());
            }
        }
    };
    private final IntegerProperty lenDivs = new SimpleIntegerProperty(model, "Length Divisions", 500) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((KnotMesh)model).setLengthDivisions(lenDivs.get());
            }
        }
    };
    private final IntegerProperty wireCrop = new SimpleIntegerProperty(model, "Wire Crop", 0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((KnotMesh)model).setWireCrop(wireCrop.get());
            }
        }
    };
    private final IntegerProperty lenCrop = new SimpleIntegerProperty(model, "Length Crop", 0) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((KnotMesh)model).setLengthCrop(lenCrop.get());
            }
        }

    };

    @Override
    protected void createMesh() {
        //model = new KnotMesh(2d, 1d, 0.4d, 2d, 3d, 1000, 60, 0, 0);
        this.model = new KnotMesh(
                this.majRad.get(),
                this.minRad.get(),
                this.wireRad.get(),
                this._p.get(),
                this._q.get(),
                this.wireDivs.get(),
                this.lenDivs.get(),
                this.lenCrop.get(),
                this.wireCrop.get()
        );
        this.model.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), this.rotateY);
        this.model.setTextureModeNone(this.colorBinding.get());
    }

    @Override
    protected void addMeshAndListeners() {

        this.useDiffMap.addListener(new WeakInvalidationListener(i -> {
            if (this.useDiffMap.getValue()) {
                this.model.setMaterial(this.material);
            } else if (!this.useDiffMap.getValue()) {
                this.model.setMaterial(null);
            }
        }));

    }

    @Override
    public String getSampleDescription() {
        return "Knots, they tie things together ;)";
    }

    @Override
    protected Node buildControlPanel() {
        NumberSliderControl majRadSlider = ControlFactory.buildNumberSlider(this.majRad, .01D, 200D);
        majRadSlider.getSlider().setMinorTickCount(10);
        majRadSlider.getSlider().setMajorTickUnit(0.5);
        majRadSlider.getSlider().setBlockIncrement(0.01d);

        NumberSliderControl minRadSlider = ControlFactory.buildNumberSlider(this.minRad, .01D, 200D);
        minRadSlider.getSlider().setMinorTickCount(10);
        minRadSlider.getSlider().setMajorTickUnit(0.5);
        minRadSlider.getSlider().setBlockIncrement(0.01d);

        NumberSliderControl tRadSlider = ControlFactory.buildNumberSlider(this.wireRad, 0.01D, 25D);
        tRadSlider.getSlider().setMinorTickCount(1);
        tRadSlider.getSlider().setMajorTickUnit(0.1);
        tRadSlider.getSlider().setBlockIncrement(0.01d);

        NumberSliderControl wDivSlider = ControlFactory.buildNumberSlider(this.wireDivs, 2, 100);
        wDivSlider.getSlider().setMinorTickCount(25);
        wDivSlider.getSlider().setMajorTickUnit(99);
        wDivSlider.getSlider().setBlockIncrement(1);
        wDivSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl mCropSlider = ControlFactory.buildNumberSlider(this.wireCrop, 0l, 98);
        mCropSlider.getSlider().setMinorTickCount(48);
        mCropSlider.getSlider().setMajorTickUnit(49);
        mCropSlider.getSlider().setBlockIncrement(1);
        mCropSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl lDivSlider = ControlFactory.buildNumberSlider(this.lenDivs, 4l, 250);
        lDivSlider.getSlider().setMinorTickCount(50);
        lDivSlider.getSlider().setMajorTickUnit(250);
        lDivSlider.getSlider().setBlockIncrement(1);

        NumberSliderControl lCropSlider = ControlFactory.buildNumberSlider(this.lenCrop, 0l, 200);
        lCropSlider.getSlider().setMinorTickCount(0);
        lCropSlider.getSlider().setMajorTickUnit(0.5);
        lCropSlider.getSlider().setBlockIncrement(1);

        NumberSliderControl pSlider = ControlFactory.buildNumberSlider(this._p, 0.01d, 10.0D);
        pSlider.getSlider().setMinorTickCount(99);
        pSlider.getSlider().setMajorTickUnit(100);
        pSlider.getSlider().setBlockIncrement(0.001);

        NumberSliderControl qSlider = ControlFactory.buildNumberSlider(this._q, 0.01d, 50.0D);
        qSlider.getSlider().setMinorTickCount(49);
        qSlider.getSlider().setMajorTickUnit(50);
        qSlider.getSlider().setBlockIncrement(0.001);

        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(
                majRadSlider, minRadSlider,
                tRadSlider, wDivSlider, mCropSlider,
                lDivSlider, lCropSlider,
                pSlider, qSlider
        );

        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.drawMode,
                        this.culling,
                        this.material.diffuseColorProperty(),
                        this.material.specularColorProperty()
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(
                        this.textureType, this.colors,
                        this.sectionType, this.useDiffMap,
                        this.material.diffuseMapProperty(), 
                        this.pattScale, this.dens, this.func
                )
        );

        return this.controlPanel;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.wireRad);
        hash = 61 * hash + Objects.hashCode(this.colorBinding);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Knots other = (Knots) obj;
        if (!Objects.equals(this.majRad, other.majRad)) {
            return false;
        }
        if (!Objects.equals(this.minRad, other.minRad)) {
            return false;
        }
        if (!Objects.equals(this.wireRad, other.wireRad)) {
            return false;
        }
        if (!Objects.equals(this.wireLen, other.wireLen)) {
            return false;
        }
        return true;
    }

}
