/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.samples;

import javafx.scene.Node;
import org.fxyz.ShapeBaseSample;
import org.fxyz.shapes.primitives.CapsuleMesh;

/**
 *
 * @author Dub
 */
public class Capsules extends ShapeBaseSample{
        
    
   
    @Override
    public Node getSample() {
        
        CapsuleMesh capsule = new CapsuleMesh();
        
        
        return mainPane;
    }
   
    @Override
    protected void createMesh() {
    }

    @Override
    protected void addMeshAndListeners() {
    }
    
}
