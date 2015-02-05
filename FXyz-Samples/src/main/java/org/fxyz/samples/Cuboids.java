package org.fxyz.samples;

import static javafx.application.Application.launch;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.fxyz.TexturedMeshSample;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.shapes.primitives.CuboidMesh;

/**
 *
 * @author jpereda
 */
public class Cuboids extends TexturedMeshSample{
    public static void main(String[] args){launch(args);}
    
    private final DoubleProperty width = new SimpleDoubleProperty(model, "Width", 10d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CuboidMesh)model).setWidth(width.get());
            }
        }
    };
    private final DoubleProperty height = new SimpleDoubleProperty(model, "Height", 12d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CuboidMesh)model).setHeight(height.get());
            }
        }
    };
    private final DoubleProperty depth = new SimpleDoubleProperty(model, "Depth", 4d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CuboidMesh)model).setDepth(depth.get());
            }
        }
    };
    private final IntegerProperty level = new SimpleIntegerProperty(model, "Level", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((CuboidMesh)model).setLevel(level.get());
            }
        }
    };
    @Override
    protected void createMesh() {
        model = new CuboidMesh(this.width.get(), this.height.get(), this.depth.get(), this.level.get());
//        model.setTextureModeNone(Color.ROYALBLUE);
    }


    @Override
    protected void addMeshAndListeners() {
    }

    @Override
    protected Node buildControlPanel() {
        NumberSliderControl widthSlider = ControlFactory.buildNumberSlider(this.width, .01D, 200D);
        widthSlider.getSlider().setMinorTickCount(10);
        widthSlider.getSlider().setMajorTickUnit(0.5);
        widthSlider.getSlider().setBlockIncrement(0.01d);

        NumberSliderControl heightSlider = ControlFactory.buildNumberSlider(this.height, .01D, 200D);
        heightSlider.getSlider().setMinorTickCount(10);
        heightSlider.getSlider().setMajorTickUnit(0.5);
        heightSlider.getSlider().setBlockIncrement(0.01d);
        
        NumberSliderControl depthSlider = ControlFactory.buildNumberSlider(this.depth, .01D, 200D);
        depthSlider.getSlider().setMinorTickCount(10);
        depthSlider.getSlider().setMajorTickUnit(0.5);
        depthSlider.getSlider().setBlockIncrement(0.01d);
        
        NumberSliderControl levelSlider = ControlFactory.buildNumberSlider(this.level, 0, 8);
        levelSlider.getSlider().setMinorTickCount(0);
        levelSlider.getSlider().setMajorTickUnit(1);
        levelSlider.getSlider().setBlockIncrement(1);
        levelSlider.getSlider().setSnapToTicks(true);
        
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(widthSlider,heightSlider,depthSlider,levelSlider);

        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.useDiffMap,
                        this.drawMode,
                        this.culling,
                        this.material.diffuseColorProperty(),
                        this.material.specularColorProperty()
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(this.textureType, this.colors, 
                        null, this.useDiffMap, this.material.diffuseMapProperty(), 
                        this.pattScale, this.dens, this.func)
        );
        
        return this.controlPanel;
    }

}
