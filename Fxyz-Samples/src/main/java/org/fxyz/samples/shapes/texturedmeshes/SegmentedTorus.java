package org.fxyz.samples.shapes.texturedmeshes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.samples.shapes.TexturedMeshSample;
import org.fxyz.shapes.primitives.SegmentedTorusMesh;

/**
 *
 * @author jpereda
 */
public class SegmentedTorus extends TexturedMeshSample {
    public static void main(String[] args){SegmentedTorus.launch(args);}
    
    private final DoubleProperty majRad = new SimpleDoubleProperty(model, "Major Radius", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setMajorRadius(majRad.get());
            }
        }
    };
    private final DoubleProperty minRad = new SimpleDoubleProperty(model, "Minor Radius", 1) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setMinorRadius(minRad.get());
            }
        }
    };
    private final IntegerProperty majorDivs = new SimpleIntegerProperty(model, "Major Radius Divisions", 100) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setMajorRadiusDivisions(majorDivs.get());
            }
        }
    };
    private final IntegerProperty minorDivs = new SimpleIntegerProperty(model, "Minor Radius Divisions", 100) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setMinorRadiusDivisions(minorDivs.get());
            }
        }
    };
    private final DoubleProperty _x = new SimpleDoubleProperty(model, "X Offset") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setxOffset(_x.doubleValue());
            }
        }
    };
    private final DoubleProperty _y = new SimpleDoubleProperty(model, "Y Offset") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setyOffset(_y.doubleValue());
            }
        }
    };
    private final DoubleProperty _z = new SimpleDoubleProperty(model, "Z Offset") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setzOffset(_z.doubleValue());
            }
        }
    };

    private final IntegerProperty majRadCrop = new SimpleIntegerProperty(model, "Major Radius Crop") {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SegmentedTorusMesh)model).setMajorRadiusCrop(majRadCrop.getValue());
            }
        }
    };
    
    
    @Override
    public void createMesh() {
        model = new SegmentedTorusMesh(50, 42, 0, 100d, 25d);        
        model.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);
    }
    
    @Override
    protected void addMeshAndListeners() {
    }
    
    @Override
    protected Node buildControlPanel() {
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(
                ControlFactory.buildNumberSlider(minorDivs, 8, 360),
                ControlFactory.buildNumberSlider(majorDivs, 8, 360),
                ControlFactory.buildNumberSlider(minRad, 1, 100),
                ControlFactory.buildNumberSlider(majRad, 1, 100),
                ControlFactory.buildNumberSlider(majRadCrop, 0, 50),
                ControlFactory.buildNumberSlider(_x, -1, 1),
                ControlFactory.buildNumberSlider(_y, -1, 1),
                ControlFactory.buildNumberSlider(_z, 0.01, 100)
                //ControlFactory.buildNumberSlider(_angle, 0.01, 359.89)                
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
                        null, this.useDiffMap,
                        this.material.diffuseMapProperty(),
                        this.pattScale, this.dens, this.func
                )
        );
        
        return this.controlPanel;
    }

    
    
}
