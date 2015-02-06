package org.fxyz.samples.shapes.texturedmeshes;

import java.util.function.Function;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.samples.shapes.TexturedMeshSample;
import org.fxyz.shapes.primitives.SurfacePlotMesh;

/**
 *
 * @author jpereda
 */
public class SurfacePlot extends TexturedMeshSample {
    public static void main(String[] args){SurfacePlot.launch(args);}
    
    private final ObjectProperty<Function<Point2D, Number>> function2D = 
            new SimpleObjectProperty<Function<Point2D, Number>>(model,"Function F(P(x,y))",p->p.magnitude()){
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SurfacePlotMesh)model).setFunction2D(function2D.get());
            }
        }
    };
    private final DoubleProperty rangeX = new SimpleDoubleProperty(model, "Range X", 10) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SurfacePlotMesh)model).setRangeX(rangeX.get());
            }
        }
    };

    private final DoubleProperty rangeY = new SimpleDoubleProperty(model, "Range Y", 10) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SurfacePlotMesh)model).setRangeY(rangeY.get());
            }
        }
    };

    private final IntegerProperty divisionsX = new SimpleIntegerProperty(model, "Divisions X", 10) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SurfacePlotMesh)model).setDivisionsX(divisionsX.get());
            }
        }
    };
    
    private final IntegerProperty divisionsY = new SimpleIntegerProperty(model, "Divisions Y", 10) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SurfacePlotMesh)model).setDivisionsY(divisionsY.get());
            }
        }
    };
    
    private final DoubleProperty scale = new SimpleDoubleProperty(model, "Scale", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((SurfacePlotMesh)model).setScale(scale.get());
            }
        }
    };

    
    @Override
    public void createMesh() {
        model = new SurfacePlotMesh();        
        model.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);
    }
    
    @Override
    protected void addMeshAndListeners() {
    }
    
    @Override
    protected Node buildControlPanel() {
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(
                ControlFactory.buildScriptFunction2DControl(function2D),
                ControlFactory.buildNumberSlider(rangeX, 0, 100),
                ControlFactory.buildNumberSlider(rangeY, 0, 100),
                ControlFactory.buildNumberSlider(divisionsX, 1, 1000),
                ControlFactory.buildNumberSlider(divisionsY, 1, 1000),
                ControlFactory.buildNumberSlider(scale, 0.01, 100)
        );

        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.useDiffMap,
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
