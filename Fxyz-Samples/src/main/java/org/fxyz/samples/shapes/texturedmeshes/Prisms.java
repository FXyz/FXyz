package org.fxyz.samples.shapes.texturedmeshes;

import static javafx.application.Application.launch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;
import org.fxyz.controls.CheckBoxControl;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.geometry.Point3D;
import org.fxyz.samples.shapes.TexturedMeshSample;
import org.fxyz.shapes.primitives.PrismMesh;

/**
 *
 * @author jpereda
 */
public class Prisms extends TexturedMeshSample{
    
    public static void main(String[] args){
        launch(args);
    }
    
    private final BooleanProperty showKnots = new SimpleBooleanProperty(this, "Show Knots");
    
    private final DoubleProperty radius = new SimpleDoubleProperty(model, "Radius", 1d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((PrismMesh)model).setRadius(radius.get());
            }
        }
    };
    private final DoubleProperty height = new SimpleDoubleProperty(model, "Height", 3d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((PrismMesh)model).setHeight(height.get());
            }
        }
    };
    private final IntegerProperty level = new SimpleIntegerProperty(model, "Level", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((PrismMesh)model).setLevel(level.get());
            }
        }
    };
    @Override
    protected void createMesh() {
        model = new PrismMesh(this.radius.get(), this.height.get(), this.level.get());
        model.setTextureModeNone(Color.ROYALBLUE);
    }


    @Override
    protected void addMeshAndListeners() {
        showKnots.addListener((obs, b, b1) ->{
            if (showKnots.get()) {
                Point3D k0 = ((PrismMesh)model).getAxisOrigin();
                Point3D k3 = ((PrismMesh)model).getAxisEnd();
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
        });
    }

    @Override
    protected Node buildControlPanel() {
        CheckBoxControl chkKnots = ControlFactory.buildCheckBoxControl(showKnots);
      
        NumberSliderControl radiusSlider = ControlFactory.buildNumberSlider(this.radius, .01D, 200D);
        radiusSlider.getSlider().setMinorTickCount(10);
        radiusSlider.getSlider().setMajorTickUnit(0.5);
        radiusSlider.getSlider().setBlockIncrement(0.01d);

//        NumberSliderControl heightSlider = ControlFactory.buildNumberSlider(this.height, .01D, 200D);
//        heightSlider.getSlider().setMinorTickCount(10);
//        heightSlider.getSlider().setMajorTickUnit(0.5);
//        heightSlider.getSlider().setBlockIncrement(0.01d);
        
        NumberSliderControl levelSlider = ControlFactory.buildNumberSlider(this.level, 0, 8);
        levelSlider.getSlider().setMinorTickCount(0);
        levelSlider.getSlider().setMajorTickUnit(1);
        levelSlider.getSlider().setBlockIncrement(1);
        levelSlider.getSlider().setSnapToTicks(true);
        
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(chkKnots,radiusSlider,levelSlider);

        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.drawMode,
                        this.culling,
                        this.material.diffuseColorProperty(),
                        this.material.specularColorProperty()
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(this.textureType, this.colors, 
                        this.sectionType, this.useDiffMap, this.material.diffuseMapProperty(), 
                        this.pattScale, this.dens, this.func)
        );
        
        return this.controlPanel;
    }

}
