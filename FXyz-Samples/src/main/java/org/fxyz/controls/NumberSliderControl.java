/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import java.text.NumberFormat;
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
public class NumberSliderControl  extends ControlBase<Property<Number>>{
    
    public NumberSliderControl(Property<Number> prop, Number lowerBound, Number upperBound) {
        super("NumberSliderControl.fxml", prop);
        valSlider.setMin(lowerBound.doubleValue());
        valSlider.setMax(upperBound.doubleValue());
        controlledProp.bind(valSlider.valueProperty());
        valSlider.valueProperty().addListener(i->{
            double v = valSlider.getValue();
            NumberFormat f = NumberFormat.getInstance();
            f.setMaximumFractionDigits(4);
            
            valueLabel.setText(String.valueOf(f.format(v)));            
        });
        propName.setText(!controlledProp.getName().isEmpty() ? controlledProp.getName() : "Empty Property Name:");
        valueLabel.setText(String.valueOf(prop.getValue()));  
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
