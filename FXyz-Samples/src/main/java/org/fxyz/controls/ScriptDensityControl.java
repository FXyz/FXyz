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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.fxyz.geometry.Point3D;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ScriptDensityControl extends ControlBase<Property<Function<Point3D,Number>>>{

    private ObjectProperty<Function<Point3D,Number>> function = new SimpleObjectProperty<>();
    
    private BooleanProperty change=new SimpleBooleanProperty();
    private BooleanProperty error=new SimpleBooleanProperty();
    
    public ScriptDensityControl(Property<Function<Point3D,Number>> prop, final Collection<String> items, boolean subControl) {
        super("ScriptDensityControl.fxml", prop);
        
        Point3D p=new Point3D(1f,2f,3f);
        res1.setText("p: {"+p.x+","+p.y+","+p.z+"}");
                    
        selection.getItems().setAll(items);
        selection.getItems().add("Enter a valid expression");
        if (subControl) {
            subControlCache = FXCollections.observableHashMap();
            this.usesSubControls.set(subControl);
        }
        selection.getEditor().setEditable(false);
        selection.getStyleClass().add("noEditable-textField");
        selection.getSelectionModel().selectedIndexProperty().addListener((obs,n,n1)->{
            if(n1!=null){
                selection.getStyleClass().remove("noEditable-textField");
                selection.getEditor().setEditable(n1.intValue()==items.size());
                if(selection.getEditor().isEditable()){
                    selection.getEditor().selectAll();
                    Platform.runLater(()->{
                        selection.getEditor().setText("");
                        selection.getEditor().promptTextProperty().unbind();
                        selection.getEditor().setPromptText("Enter a valid expression");
                    });
                } else {
                    selection.getStyleClass().add("noEditable-textField");
                    change.set(true);
                }
                controlledProperty.unbind();
                controlledProperty.bindBidirectional(function);
            }
        });
        
        selection.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
        if (event.getCode() == KeyCode.ENTER && !selection.getEditor().getText().isEmpty()){
                change.set(true);
            }
        });
        selection.getEditor().focusedProperty().addListener((obs,b,b1)->{
            if(!b1 && !selection.getEditor().getText().isEmpty()){
                change.set(true);
            }
        });
        change.addListener((obs,b,b1)->{
            if(b1){
                change.set(false);
                String text=selection.getValue();
                if(!selection.getEditor().getText().isEmpty()){
                    text=selection.getEditor().getText();
                }
                @SuppressWarnings("unchecked")
                Function<Point3D,Number> f;
                try {
                    f = (Function<Point3D,Number>)engine.eval(
                            String.format("new java.util.function.Function(%s)", "function(p) "+text));
                    // check if f is a valid function
                    try{
                        res2.setText("val: "+String.format("%.3f", f.apply(p)));
                        error.set(false);
                    } catch(Exception e){
                        res2.setText("val: error");
                        error.set(true);
                    }
                    function.set(f);
                } catch (RuntimeException | ScriptException ex) {
                    System.err.println("Script Error "+ex);
                }
            }
        });
        
        selection.getSelectionModel().select(0);
        
        
    }
    
    public Property<Function<Point3D,Number>> functionProperty() { return function; }
            
    @FXML
    private Label res1;
    @FXML
    private Label res2;
    @FXML
    private ComboBox<String> selection;
    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    
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
