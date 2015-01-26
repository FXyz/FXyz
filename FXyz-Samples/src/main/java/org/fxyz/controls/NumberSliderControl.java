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
public class NumberSliderControl extends ControlBase<Property<Number>> {

    public enum PrecisionString {
        DEFAULT,
        D_2,
        D_4,
        INT;

        private PrecisionString() {
        }
    }

    private final NumberFormat numFormat = NumberFormat.getInstance();

    public NumberSliderControl(Property<Number> prop, Number lowerBound, Number upperBound, PrecisionString format) {
        super("NumberSliderControl.fxml", prop);
        valSlider.setMin(lowerBound.doubleValue());
        valSlider.setMax(upperBound.doubleValue());
        controlledProp.bind(valSlider.valueProperty());
        switch(format) {
            case DEFAULT: numFormat.setMaximumFractionDigits(1);break;
            case D_2: numFormat.setMaximumFractionDigits(2);break;
            case D_4: numFormat.setMaximumFractionDigits(4);break;
            case INT: numFormat.setMaximumFractionDigits(0);break;
            default: numFormat.setMaximumFractionDigits(1);break;
        }
        valSlider.valueProperty().addListener(i -> {
            valueLabel.setText(String.valueOf(numFormat.format(valSlider.getValue())));
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
