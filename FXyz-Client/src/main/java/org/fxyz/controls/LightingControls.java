/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import jfxtras.scene.control.ListSpinner;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class LightingControls extends ControlBase<Property<Boolean>> {

    @FXML
    private VBox lighting1;
    @FXML
    private Label light1Label;
    @FXML
    private Slider colorL1;
    @FXML
    private VBox rotControlL1;
    @FXML
    private ListSpinner<Integer> light1Rotate;
    @FXML
    private HBox rotAxisL1;
    @FXML
    private ToggleButton xAxisL1;
    @FXML
    private ToggleButton yAxisL1;
    @FXML
    private ToggleButton zAxisL1;
    @FXML
    private Slider distL1;
    @FXML
    private CheckBox l1On;

    private final ObservableList<Integer> angL1 = FXCollections.observableArrayList();

    public LightingControls(final Property<Boolean> show,
            final Property<Boolean> lt1On,
            final Property<Color> c1, 
            final Property<Number> d1, 
            final Property<Number> r1,
            final Property<Point3D> ra1 
    ) {
        super("SubSceneControlPanel.fxml", show);
        this.setOnMouseEntered(me->{me.consume();});
        colorL1.getStyleClass().addAll("colorSlider", "lighting-slider");
        
        
        loadAngles();
        
        light1Rotate.setValue(0);
        light1Rotate.valueProperty().addListener(e->{
            r1.setValue(light1Rotate.getValue());
        });
        

        colorL1.valueProperty().addListener(e -> {
            if(colorL1.getValue() < 10){
                c1.setValue(Color.WHITE);
            }else{
                c1.setValue(Color.hsb(360 * (1d - colorL1.getValue() / 1530d), 1, 1));
            }
        });

        xAxisL1.selectedProperty().addListener(e->{
            ra1.setValue(Point3D.ZERO.add(
                xAxisL1.isSelected() ? 1 : 0,
                yAxisL1.isSelected() ? 1 : 0,
                zAxisL1.isSelected() ? 1 : 0
            ));
        });
        yAxisL1.selectedProperty().addListener(e->{
            ra1.setValue(Point3D.ZERO.add(
                xAxisL1.isSelected() ? 1 : 0,
                yAxisL1.isSelected() ? 1 : 0,
                zAxisL1.isSelected() ? 1 : 0
            ));
        });
        zAxisL1.selectedProperty().addListener(e->{
            ra1.setValue(Point3D.ZERO.add(
                xAxisL1.isSelected() ? 1 : 0,
                yAxisL1.isSelected() ? 1 : 0,
                zAxisL1.isSelected() ? 1 : 0
            ));
        });
        
        d1.bind(distL1.valueProperty());
        
        lt1On.bind(l1On.selectedProperty());
    }

    private void loadAngles() {
        for (int i = 0; i < 360; i+=3) {
            angL1.add(i);
        }
        light1Rotate.setItems(angL1);
    }
    
    

}
