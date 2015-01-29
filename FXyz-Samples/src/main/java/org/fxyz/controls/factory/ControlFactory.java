/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls.factory;

import javafx.beans.property.Property;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import org.fxyz.controls.CheckBoxControl;
import org.fxyz.controls.ColorPickControl;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.ControlPanel;
import org.fxyz.controls.CullFaceControl;
import org.fxyz.controls.ImagePreviewControl;
import org.fxyz.controls.NumberSliderControl;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public final class ControlFactory{
    
    public static ControlPanel buildControlPanel(final ControlCategory titlePane){
        return new ControlPanel(titlePane);
    }
    
    public static ControlCategory buildCategory(final String title){                
        return new ControlCategory(title);
    }
    /*==========================================================================
                              Specific Control Types
    ==========================================================================*/
    public static final CheckBoxControl buildBooleanControl(final Property<Boolean> p){
        return new CheckBoxControl(p);
    }
     
    public static final NumberSliderControl buildNumberSlider(final Property<Number> p, final Number lb, final Number ub){
        return new NumberSliderControl(p,lb,ub);
    }
    
    public static final CullFaceControl buildCullFaceControl(final Property<CullFace> p){
        return new CullFaceControl(p);
    }
    public static final ColorPickControl buildColorControl(final Property<Color> p){
        return new ColorPickControl(p);
    }
    
    public static final ImagePreviewControl buildImageToggle(final String fxml, final Property<Boolean> prop, final Image img){
        return new ImagePreviewControl(fxml, prop, img);
    }
    /*==========================================================================
                            Generic "auto" builder
    ==========================================================================/
    private static <T extends Property> ControlBase buildControlForProperty(T prop, Number lb, Number ub){
        String propName = prop.getClass().getSimpleName();
        switch(propName){
            case "ReadOnlyBooleanWrapper":break;
            case "ReadOnlyBooleanProperty":break;
            case "SimpleBooleanProperty":return buildBooleanControl((BooleanProperty) prop); 
            case "BooleanProperty": return buildBooleanControl((BooleanProperty) prop);  
                //
            case "ReadOnlyIntegerWrapper":break;
            case "ReadOnlyIntegerProperty":break;            
            case "ReadOnlyDoubleWrapper":break;
            case "ReadOnlyDoubleProperty":break;
                //
            case "SimpleIntegerProperty": return buildNumberSlider(prop, lb, ub); 
            case "IntegerProperty": return buildNumberSlider(prop, lb, ub); 
            case "SimpleDoubleProperty": return buildNumberSlider(prop, lb, ub); 
            case "DoubleProperty": return buildNumberSlider(prop, lb, ub); 
                  
                //
            case "ReadOnlyObjectWrapper":break;
            case "ReadOnlyObjectProperty":break;
            case "SimpleObjectProperty":break;
            case "ObjectProperty":break;
            
            default:break;
        }
        throw new UnsupportedOperationException("Unable to find a propper control for: " + prop.getClass().getSimpleName());
    }
    */
    
}
