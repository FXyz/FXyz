/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class NumberSliderControl extends ControlBase<Property<Number>> {

    public enum PrecisionString {
        DEFAULT,
        D_2,
        D_4,
        INT;

        private PrecisionString() {
        }
    }
    
    public NumberSliderControl(final Property<Number> prop, final Number lowerBound, final Number upperBound) {
        super("NumberSliderControl.fxml", prop);
        valSlider.setMin(lowerBound.doubleValue());
        valSlider.setMax(upperBound.doubleValue());
        controlledProperty.bind(valSlider.valueProperty());
        propName.setText(!controlledProperty.getName().isEmpty() ? controlledProperty.getName() : "Empty Property Name:");
      
    }

    public Slider getSlider() {
        return valSlider;
    }

    @FXML
    private Label propName;
    @FXML
    private StackPane spacer;
    @FXML
    private Label valueLabel;
    @FXML
    private Slider valSlider;

}
