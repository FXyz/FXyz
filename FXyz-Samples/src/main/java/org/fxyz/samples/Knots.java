package org.fxyz.samples;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
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
import org.fxyz.controls.ControlPanel;
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

    private DensityFunction<Point3D> dens = p -> (double) p.x;
    //standard
    private final Property<Boolean> useMaterial = new SimpleBooleanProperty(this, "Use PhongMaterial", false);
    private final Property<DrawMode> drawMode = new SimpleObjectProperty<>(DrawMode.FILL);
    private final Property<CullFace> culling = new SimpleObjectProperty<>(CullFace.BACK);
    //specific
    private final Property<SectionType> sectionType = new SimpleObjectProperty<>(model, "", SectionType.TRIANGLE);
    private final Property<TextureType> textureType = new SimpleObjectProperty<>(model, "", TextureType.NONE);    
    /*
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

    private final IntegerProperty colors = new SimpleIntegerProperty(this, "Colors", 16);
    private final IntegerProperty wireDivs = new SimpleIntegerProperty(this, "Wire Divisions", 100);
    private final IntegerProperty lenDivs = new SimpleIntegerProperty(this, "Length Divisions", 500);
    private final IntegerProperty wireCrop = new SimpleIntegerProperty(this, "Wire Crop", 0);
    private final IntegerProperty lenCrop = new SimpleIntegerProperty(this, "Length Crop", 0);

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
        model.setTextureModeNone(Color.BROWN);
        
        model.drawModeProperty().bindBidirectional(drawMode); // DOES NOT work binding the other way prop->mesh
        model.cullFaceProperty().bindBidirectional(culling);

        model.textureTypeProperty().bindBidirectional(textureType); // Having NPE's thrown
        model.sectionTypeProperty().bindBidirectional(sectionType);

        model.majorRadiusProperty().bindBidirectional(majRad);
        model.minorRadiusProperty().bindBidirectional(minRad);
        
        model.wireRadiusProperty().bindBidirectional(wireRad);
        model.wireDivisionsProperty().bindBidirectional(wireDivs);
        model.wireCropProperty().bindBidirectional(wireCrop);

        model.lengthProperty().bindBidirectional(wireLen);
        model.lengthDivisionsProperty().bindBidirectional(lenDivs);
        model.lengthCropProperty().bindBidirectional(lenCrop);
        
        model.pProperty().bindBidirectional(_p);
        model.qProperty().bindBidirectional(_q);
        //model.xOffsetProperty().bindBidirectional(_x);
        //model.yOffsetProperty().bindBidirectional(_y);
        //model.zOffsetProperty().bindBidirectional(_z);
        //model.tubeStartAngleOffsetProperty().bindBidirectional(_angle);

        model.colorsProperty().bindBidirectional(colors);
    }

    @Override
    protected void addMeshAndListeners() {
        
        useMaterial.addListener(i -> {
            if (useMaterial.getValue()) {
                model.setMaterial(material);
            } else if (!useMaterial.getValue()) {
                model.setMaterial(null);
            }
        });

        model.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);
        
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
        
        group.getChildren().add(model);
    }

    @Override
    public String getSampleDescription() {
        return "Knots, they tie things together ;)";
    }

    @Override
    public Node getControlPanel() {
        NumberSliderControl majRadSlider = ControlFactory.buildNumberSlider(majRad, 1D, 10D);        
        majRadSlider.getSlider().setMinorTickCount(10);
        majRadSlider.getSlider().setMajorTickUnit(0.5);
        majRadSlider.getSlider().setBlockIncrement(0.1d);
        majRadSlider.getSlider().setSnapToTicks(true);
        
        NumberSliderControl minRadSlider = ControlFactory.buildNumberSlider(minRad, 1D, 10D);
        minRadSlider.getSlider().setMinorTickCount(10);
        minRadSlider.getSlider().setMajorTickUnit(0.5);
        minRadSlider.getSlider().setBlockIncrement(0.1d);
        minRadSlider.getSlider().setSnapToTicks(true);
        
        NumberSliderControl tRadSlider = ControlFactory.buildNumberSlider(wireRad, 0.01D, 1D);
        tRadSlider.getSlider().setMinorTickCount(1);
        tRadSlider.getSlider().setMajorTickUnit(0.1);
        tRadSlider.getSlider().setBlockIncrement(0.1d);
        tRadSlider.getSlider().setSnapToTicks(true);
        
        NumberSliderControl wDivSlider = ControlFactory.buildNumberSlider(wireDivs, 4, 100);
        wDivSlider.getSlider().setMinorTickCount(25);
        wDivSlider.getSlider().setMajorTickUnit(99);
        wDivSlider.getSlider().setBlockIncrement(1);
        wDivSlider.getSlider().setSnapToTicks(true);
        
        NumberSliderControl mCropSlider = ControlFactory.buildNumberSlider(wireCrop, 0, wireDivs.multiply(0.5).intValue());
        mCropSlider.getSlider().setMinorTickCount(10);
        mCropSlider.getSlider().setMajorTickUnit(49);
        mCropSlider.getSlider().setBlockIncrement(1);
        mCropSlider.getSlider().setSnapToTicks(true);
        
        NumberSliderControl lDivSlider = ControlFactory.buildNumberSlider(lenDivs, 20, 500);
        lDivSlider.getSlider().setMinorTickCount(100);
        lDivSlider.getSlider().setMajorTickUnit(499);
        lDivSlider.getSlider().setBlockIncrement(1);
        lDivSlider.getSlider().setSnapToTicks(true);
        
        NumberSliderControl lCropSlider = ControlFactory.buildNumberSlider(lenCrop, 0, lenDivs.multiply(0.5).intValue());
        lCropSlider.getSlider().setMinorTickCount(10);
        lCropSlider.getSlider().setMajorTickUnit(49);
        lCropSlider.getSlider().setBlockIncrement(1);
        lCropSlider.getSlider().setSnapToTicks(true);
        
        NumberSliderControl pSlider = ControlFactory.buildNumberSlider(_p, 0, 10);
        pSlider.getSlider().setMinorTickCount(1);
        pSlider.getSlider().setMajorTickUnit(0.5);
        pSlider.getSlider().setBlockIncrement(0.1);
        pSlider.getSlider().setSnapToTicks(true);
        
        NumberSliderControl qSlider = ControlFactory.buildNumberSlider(_q, 0, 10);
        qSlider.getSlider().setMinorTickCount(1);
        qSlider.getSlider().setMajorTickUnit(0.5);
        qSlider.getSlider().setBlockIncrement(0.1);
        qSlider.getSlider().setSnapToTicks(true);
                
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(
                majRadSlider, minRadSlider, 
                tRadSlider, wDivSlider, mCropSlider, 
                lDivSlider, lCropSlider,
                pSlider, qSlider
        );
        
        ControlPanel panel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        useMaterial,
                        drawMode,
                        culling,
                        material.diffuseColorProperty(),
                        material.specularColorProperty()
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(textureType, sectionType, colors)
        );
        return panel;

    }

}
