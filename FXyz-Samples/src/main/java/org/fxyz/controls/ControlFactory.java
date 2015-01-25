/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.beans.property.Property;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ControlFactory{
    
    public static ControlPanel buildControlPanel(ControlCategory titlePane){
        return new ControlPanel(titlePane);
    }
    
    public static ControlCategory buildCategory(String title){                
        return new ControlCategory(title);
    }
    /*==========================================================================
                              Specific Control Types
    ==========================================================================*/
    public static final BoolPropertyControl buildBooleanControl(Property<Boolean> p){
        return new BoolPropertyControl(p);
    }
     
    public static final NumberSliderControl buildNumberSlider(Property<Number> p, Number lb, Number ub){
        return new NumberSliderControl(p,lb,ub);
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
