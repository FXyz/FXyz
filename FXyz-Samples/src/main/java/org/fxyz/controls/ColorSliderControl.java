/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import java.text.NumberFormat;
import java.text.ParsePosition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ColorSliderControl extends ControlBase<Property<Number>> {

    public enum PrecisionString {
        DEFAULT,
        D_2,
        D_4,
        INT;

        private PrecisionString() {
        }
    }
    
    private final NumberFormat format = NumberFormat.getInstance();
    
    private final StringProperty colorBinding = new SimpleStringProperty();
    private final IntegerProperty colors = new SimpleIntegerProperty(this, "Colors", 1530){

        @Override
        protected void invalidated() {
            super.invalidated(); 
            Color color = Color.hsb(360*(1d-colors.get()/1530d), 1, 1);
            colorBinding.set(String.format("#%02X%02X%02X",
                (int)(color.getRed()*255),(int)(color.getGreen()*255),(int)(color.getBlue()*255)));
        }
        
    };
    
    public ColorSliderControl(final Property<Number> prop, final Number lowerBound, final Number upperBound) {
        super("ColorSliderControl.fxml", prop);
        if(prop instanceof IntegerProperty){
            format.setMaximumFractionDigits(0);
            valSlider.setMin(lowerBound.intValue());
            valSlider.setMax(upperBound.intValue());
        }
        else if(prop instanceof DoubleProperty){
            format.setMaximumFractionDigits(2);
            valSlider.setMin(lowerBound.doubleValue());
            valSlider.setMax(upperBound.doubleValue());
        }
        valueLabel.textProperty().bindBidirectional(colorBinding);
        valSlider.setValue(controlledProperty.getValue().doubleValue());
        
        colors.bind(valSlider.valueProperty());
        controlledProperty.bind(valSlider.valueProperty());
        propName.setText(!controlledProperty.getName().isEmpty() ? controlledProperty.getName() : "Empty Property Name:");
        
        valSlider.setShowTickLabels(false);
        valSlider.setShowTickMarks(false);
      
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
