package org.fxyz.samples;

import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import org.fxyz.TexturedMeshSample;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.shapes.primitives.IcosahedronMesh;

/**
 *
 * @author jpereda
 */
public class Icosahedron extends TexturedMeshSample {
    public static void main(String[] args){Icosahedron.launch(args);}
    @Override
    public void createMesh() {
//         (float)(3d*Math.pow(Math.sin(p.phi),2)*Math.pow(Math.abs(Math.cos(p.theta)),0.1)+
//         Math.pow(Math.cos(p.phi),2)*Math.pow(Math.abs(Math.sin(p.theta)),0.1));
//         private Density dens = p->p.x*p.y*p.z;

        model = new IcosahedronMesh(5, 1f);
//                ico.setDrawMode(DrawMode.LINE);
        // NONE
//        ico.setTextureModeNone(Color.ROYALBLUE);
        // IMAGE
//        ico.setTextureModeImage(getClass().getResource("res/0ZKMx.png").toExternalForm());
        // PATTERN
//        ico.setTextureModePattern(2d);
        // DENSITY
        dens.setValue(p-> (double)p.x);
        model.setTextureModeVertices3D(256 * 256, dens.getValue());
    // FACES
//        ico.setTextureModeFaces(256);

        model.getTransforms().addAll(new Rotate(30, Rotate.X_AXIS), rotateY);
       
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
                        this.colors, this.sectionType, 
                        this.useDiffMap, this.material.diffuseMapProperty(),
                        this.pattScale, this.densMax, this.dens, this.func)
        );
        
        return this.controlPanel;
    }

}
