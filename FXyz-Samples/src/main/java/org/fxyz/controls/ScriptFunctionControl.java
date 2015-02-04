/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.fxyz.geometry.DensityFunction;
import org.fxyz.geometry.Point3D;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ScriptFunctionControl extends ControlBase<Property<DensityFunction<Point3D>>>{

    public ScriptFunctionControl(Property<DensityFunction<Point3D>> prop, final Collection<String> items, boolean subControl) {
        super("ScriptFunctionControl.fxml", prop);
        
        selection.getItems().addAll(items);
        //controlledProperty.bindBidirectional(selection.valueProperty());
        if (subControl) {
            subControlCache = FXCollections.observableHashMap();
            this.usesSubControls.set(subControl);
        }
        selection.getEditor().setEditable(false);
        selection.getStyleClass().add("noEditable-textField");
        selection.getSelectionModel().selectedIndexProperty().addListener((obs,n,n1)->{
            if(n1!=null){
                selection.getStyleClass().remove("noEditable-textField");
                selection.getEditor().setEditable(n1.intValue()==5);
                if(n1.intValue()==5){
                    selection.getEditor().selectAll();
                    Platform.runLater(()->{
                        selection.getEditor().setText("");
                        selection.getEditor().promptTextProperty().unbind();
                        selection.getEditor().setPromptText("Enter a valid expression");
                    });
                } else {
                    selection.getStyleClass().add("noEditable-textField");
                }
            }
        });
    }
    
    @FXML
    private Label res1;
    @FXML
    private Label res2;
    @FXML
    private ComboBox<String> selection;
    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private final ObservableList<Point3D> list =FXCollections.observableArrayList(new Point3D(3f,4f,5f), new Point3D(2f,-3f,0.4f));
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        
        @SuppressWarnings("unchecked")
        Function<Object,Object> f;
        try {
            f = (Function<Object,Object>)engine.eval(
                    String.format("new java.util.function.Function(%s)", "function(p) "+selection.getValue()));
            Point3D p=list.get(0);
            //res1.setText("{"+p.x+","+p.y+","+p.z+"} -> "+f.apply(p));
            p=list.get(1);
            //res2.setText("{"+p.x+","+p.y+","+p.z+"} -> "+f.apply(p));
        } catch (ScriptException ex) {
            System.out.println("error "+ex);
        }
       
    }
    
    @FXML
    protected VBox subControls;

    protected ObservableMap<String, List<ControlBase<Property<String>>>> subControlCache;

    public void addSubControl(final ControlBase ... controls) {
        if (useSubControls()) {
                subControlCache.putIfAbsent(selection.getValue(), Arrays.asList(controls));       
            //subControls.getChildren().add(subControlCache.get(control.controlledProperty));
        }
    }

    protected ObservableMap<String, List<ControlBase<Property<String>>>> getSubControlCache() {
        if (useSubControls()) {
            return subControlCache;
        } else {
            return null;
        }
    }

    private final BooleanProperty usesSubControls = new SimpleBooleanProperty(this, "usesSubControls", false) {

        @Override
        protected void invalidated() {
            super.invalidated();

        }

    };

    protected boolean useSubControls() {
        return usesSubControls.get();
    }

    public BooleanProperty usingSubControlsProperty() {
        return usesSubControls;
    }
}
