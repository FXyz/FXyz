/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.pending;

import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import org.fxyz.ShapeBaseSample;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.ControlPanel;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.shapes.primitives.CapsuleMesh;

/**
 *
 * @author Dub
 */
public class Capsules extends ShapeBaseSample{
        
    
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
    
}
