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

    public ControlPanel() {
        this.accordion = new ControlBasePane();
        this.rootCategory = new ControlCategory("");
        this.accordion.getPanes().add(rootCategory);
        this.rootCategory.setExpanded(true);
        this.getChildren().add(accordion);
    }
    
    private final ControlCategory rootCategory;
    private final ControlBasePane accordion;

    public ControlPanel(ControlCategory cat) {
        this();
        this.accordion.getPanes().clear();
        this.accordion.getPanes().add(cat);
        
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
    
    public final void addToRoot(StackPane control){
        this.rootCategory.addControl(control);
    }
    
    public final void addToRoot(StackPane ... control){
        this.rootCategory.addControls(control);
    }
}
