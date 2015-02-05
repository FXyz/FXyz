package org.fxyz.samples;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.fxyz.TexturedMeshSample;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.shapes.primitives.IcosahedronMesh;

/**
 *
 * @author jpereda
 */
public class Icosahedron extends TexturedMeshSample {
    public static void main(String[] args){Icosahedron.launch(args);}
    
    private final DoubleProperty diameter = new SimpleDoubleProperty(model, "Diameter", 10d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((IcosahedronMesh)model).setDiameter(diameter.floatValue());
            }
        }
    };
    private final IntegerProperty level = new SimpleIntegerProperty(model, "Level", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((IcosahedronMesh)model).setLevel(level.get());
            }
        }
    };
    
    @Override
    public void createMesh() {
        model = new IcosahedronMesh(diameter.floatValue(),level.get());
//        model.setTextureModeNone(Color.ROYALBLUE);
//        model.setTextureModeVertices3D(1530,p->p.x);
    }
    
    @Override
    protected void addMeshAndListeners() {
    }    
    
    @Override
    protected Node buildControlPanel() {
        NumberSliderControl diameterSlider = ControlFactory.buildNumberSlider(this.diameter, .01D, 200D);
        diameterSlider.getSlider().setMinorTickCount(10);
        diameterSlider.getSlider().setMajorTickUnit(0.5);
        diameterSlider.getSlider().setBlockIncrement(0.01d);
        
        NumberSliderControl levelSlider = ControlFactory.buildNumberSlider(this.level, 0, 8);
        levelSlider.getSlider().setMinorTickCount(0);
        levelSlider.getSlider().setMajorTickUnit(1);
        levelSlider.getSlider().setBlockIncrement(1);
        levelSlider.getSlider().setSnapToTicks(true);
        
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(diameterSlider,levelSlider);

        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.useDiffMap,
                        this.drawMode,
                        this.culling,
                        this.material.diffuseColorProperty(),
                        this.material.specularColorProperty()
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(this.textureType,
                        this.colors, null, 
                        this.useDiffMap, this.material.diffuseMapProperty(),
                        this.pattScale, this.dens, this.func)
        );
        
        return this.controlPanel;
    }

}
