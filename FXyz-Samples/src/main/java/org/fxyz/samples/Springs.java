package org.fxyz.samples;

import javafx.scene.Node;
import javafx.scene.shape.CullFace;
import javafx.scene.transform.Rotate;
import org.fxyz.TexturedMeshSample;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.shapes.primitives.SpringMesh;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.SectionType;

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
//        spring.setDrawMode(DrawMode.LINE);
        model.setCullFace(CullFace.NONE);
        model.setSectionType(SectionType.TRIANGLE);

//    // NONE
//        spring.setTextureModeNone(Color.ROYALBLUE);
        // IMAGE
//        spring.setTextureModeImage(getClass().getResource("res/LaminateSteel.jpg").toExternalForm());
        // PATTERN
//       spring.setTextureModePattern(5d);
        // FUNCTION
//        spring.setTextureModeVertices1D(256*256,t->t);
        // DENSITY
        model.setTextureModeVertices3D(256 * 256, p -> (double) p.magnitude());
            // FACES
//        spring.setTextureModeFaces(256*256);

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
                        this.useDiffMap,
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
