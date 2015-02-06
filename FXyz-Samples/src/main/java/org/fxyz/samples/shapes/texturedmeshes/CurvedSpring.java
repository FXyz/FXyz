package org.fxyz.samples.shapes.texturedmeshes;

import org.fxyz.samples.shapes.TexturedMeshSample;
import javafx.scene.Node;
import javafx.scene.shape.CullFace;
import javafx.scene.transform.Rotate;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.shapes.primitives.CurvedSpringMesh;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.SectionType;

/**
 *
 * @author jpereda
 */
public class CurvedSpring extends TexturedMeshSample {
    public static void main(String[] args){CurvedSpring.launch(args);}
    @Override
    public void createMesh() {        
        model = new CurvedSpringMesh(6d, 2d, 0.4d, 25d, 12.5d * 2d * Math.PI,
                1000, 60, 0, 0);
        model.setSectionType(SectionType.TRIANGLE);
        model.setCullFace(CullFace.NONE);
//        spring.setDrawMode(DrawMode.LINE);

        // NONE
        //model.setTextureModeNone(Color.ROYALBLUE);
    // IMAGE
//        spring.setTextureModeImage(getClass().getResource("res/LaminateSteel.jpg").toExternalForm());
        // PATTERN
//       spring.setTextureModePattern(10d);
        // FUNCTION
        model.setTextureModeVertices1D(256*256,t->t);
        // DENSITY
//        spring.setTextureModeVertices3D(256*256,dens);
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
                ControlFactory.buildTextureMeshCategory(this.textureType, 
                        this.colors, this.sectionType, this.useDiffMap, 
                        this.material.diffuseMapProperty(), this.pattScale, 
                        this.dens, this.func)
        );
        
        return this.controlPanel;
    }
}
