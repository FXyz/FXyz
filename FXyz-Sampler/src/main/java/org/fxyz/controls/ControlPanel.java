/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ControlPanel extends StackPane{

    private ControlPanel() {
        this.accordion = new ControlBasePane();
    }
    
    private final ControlBasePane accordion;

    public ControlPanel(ControlCategory cat) {
        this();
        this.accordion.getPanes().add(cat);
        this.getChildren().add(accordion);
    }

    public final TitledPane getExpandedPane() {
        return accordion.getExpandedPane();
    }

    public final void setExpandedPane(TitledPane value) {
        accordion.setExpandedPane(value);
    }

    public final ObjectProperty<TitledPane> expandedPaneProperty() {
        return accordion.expandedPaneProperty();
    }

    public final ObservableList<TitledPane> getPanes() {
        return accordion.getPanes();
    }
   
    
}
