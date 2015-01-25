/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import javafx.beans.property.BooleanProperty;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ControlFactory{
    
    public static final BoolPropertyControl buildBooleanControl(BooleanProperty p){
        return new BoolPropertyControl(p);
    }
    
    //Others 
}
