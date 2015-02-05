package org.fxyz.samples;

import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import org.fxyz.TexturedMeshSample;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.shapes.primitives.SegmentedTorusMesh;

/**
 *
 * @author jpereda
 */
public class SegmentedTorus extends TexturedMeshSample {
    public static void main(String[] args){SegmentedTorus.launch(args);}
    

    @Override
    public void createMesh() {

        model = new SegmentedTorusMesh(50, 42, 0, 100d, 25d);
//        torus.setDrawMode(DrawMode.LINE);
        // NONE
//        torus.setTextureModeNone(Color.ROYALBLUE);
        // IMAGE
//        torus.setTextureModeImage(getClass().getResource("res/grid.png").toExternalForm());
        // PATTERN
        
    // DENSITY
        model.setTextureModeVertices3D(256*256,dens.getValue());
        // FACES
//        torus.setTextureModeFaces(256*256);

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
                        this.pattScale, this.densMax,
                        this.dens,this.func
                )
        );
        
        return this.controlPanel;
    }

    
    
}
