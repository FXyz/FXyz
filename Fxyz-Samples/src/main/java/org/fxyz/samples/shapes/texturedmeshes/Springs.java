package org.fxyz.samples.shapes.texturedmeshes;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.samples.shapes.TexturedMeshSample;
import org.fxyz.shapes.primitives.SpringMesh;

/**
 *
 * @author jpereda
 */
public class Springs extends TexturedMeshSample {

    public static void main(String[] args) {
        Springs.launch(args);
    }

    @Override
    protected void createMesh() {

        model = new SpringMesh(50d, 10d, 20d, 2 * 20d * 2d * Math.PI,
                1000, 60, 0, 0);
        model.setTextureModeNone(Color.ROYALBLUE);
        model.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);
    }

    @Override
    protected void addMeshAndListeners() {
    }

    @Override
    protected Node buildControlPanel() {
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        //geomControls.addControls()

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

}
