/**
 * Capsules.java
 *
 * Copyright (c) 2013-2016, F(X)yz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of F(X)yz, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL F(X)yz BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package org.fxyz.pending;

import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Camera;
import javafx.scene.Node;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.ControlPanel;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.samples.shapes.ShapeBaseSample;
import org.fxyz.shapes.primitives.CapsuleMesh;
import org.fxyz.utils.CameraAdapter;

/**
 *
 * @author Dub
 */
public class Capsules extends ShapeBaseSample implements CameraAdapter{
        
    
    private final List<CapsuleMesh> objectAsList = Collections.singletonList(new CapsuleMesh());
    private final DoubleProperty rad = new SimpleDoubleProperty(this, "Radius: ");
    private final DoubleProperty height = new SimpleDoubleProperty(this, "Height: ");
    
   
    @Override
    protected void createMesh() {
        //CapsuleMesh cm = objectAsList.get(0);  
        
    }

    @Override
    protected void addMeshAndListeners() {
        //rad.addListener(i->{objectAsList.get(0).setRadius(rad.doubleValue());});
        //height.addListener(i->{objectAsList.get(0).setHeight(rad.doubleValue() * 10);});
        //Capsule is broken need to fix, height property does not work right.
        //group.getChildren().addAll(objectAsList);
    }

    @Override
    public Node getControlPanel() {
        
        NumberSliderControl radSlider = ControlFactory.buildNumberSlider(rad, 1.0d, 24.0d);
        radSlider.getSlider().setMinorTickCount(22);
        radSlider.getSlider().setMajorTickUnit(23);
        radSlider.getSlider().setBlockIncrement(1);
        radSlider.getSlider().setSnapToTicks(true);       
        
        NumberSliderControl heightSlider = ControlFactory.buildNumberSlider(height, 10.0d, 34.0d);
        heightSlider.getSlider().setMajorTickUnit(24);
        heightSlider.getSlider().setMinorTickCount(22);        
        heightSlider.getSlider().setBlockIncrement(1);
        heightSlider.getSlider().setSnapToTicks(true);

        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(radSlider, heightSlider);
        geomControls.setExpanded(true);

        ControlPanel cPanel = ControlFactory.buildControlPanel(geomControls);
        cPanel.setExpandedPane(geomControls);

        return cPanel;
    }

    @Override
    protected Node buildControlPanel() {
        return null;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }
    
}
