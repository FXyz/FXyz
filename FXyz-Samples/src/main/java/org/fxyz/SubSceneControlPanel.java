/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import jfxtras.scene.control.ListSpinner;
import org.fxyz.controls.ControlBase;
import org.fxyz.geometry.Point3D;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class SubSceneControlPanel extends ControlBase<Property<Boolean>> {

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
    private VBox lighting2;
    @FXML
    private Label light2Label;
    @FXML
    private Slider colorL2;
    @FXML
    private VBox rotControlL2;
    @FXML
    private ListSpinner<Integer> light2Rotate;
    @FXML
    private HBox rotAxisL2;
    @FXML
    private ToggleButton xAxisL2;
    @FXML
    private ToggleButton yAxisL2;
    @FXML
    private ToggleButton zAxisL2;

    private final ObservableList<Integer> angL1 = FXCollections.observableArrayList();
    private final ObservableList<Integer> angL2 = FXCollections.observableArrayList();
    
    private final Property<Color> color1 = new SimpleObjectProperty<>(), 
            color2 = new SimpleObjectProperty<>();

    public SubSceneControlPanel(final Property<Boolean> show,
            final Property<Color> c1, final Property<Color> c2,
            final Property<Number> r1, final Property<Number> r2,
            final Property<Point3D> ra1, final Property<Point3D> ra2
    ) {
        super("SubSceneControlPanel.fxml", show);

        loadAngles();

        sceneProperty().addListener(i -> {
            if (getScene() != null) {
                light1Rotate.setItems(angL1);
                light2Rotate.setItems(angL2);
                
                light1Rotate.setValue(0);
                light2Rotate.setValue(0);                
            }
        });
        
        colorL1.valueProperty().addListener(e->{
            color1.setValue(Color.hsb(360*(1d - colorL1.getValue() / 1530d), 1, 1));
            c1.setValue(color1.getValue());
        });
        
        
        colorL2.valueProperty().addListener(e->{
            color2.setValue(Color.hsb(360*(1d - colorL2.getValue() / 1530d), 1, 1));
            c2.setValue(color2.getValue());
        });
        
    }

    private void loadAngles() {
        for (int i = 0; i < 360; i++) {
            angL1.add(i);
            angL2.add(i);
        }
    }

}
