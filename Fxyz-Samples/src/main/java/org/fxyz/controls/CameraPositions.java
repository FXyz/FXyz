/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

/**
 *  Utility class providing vantage points.
 *  For primary use with (upcoming) CameraControl
 * @author Jason Pollastrini aka jdub1581
 */
public enum CameraPositions {
    //6 centers
    FRONT_CENTER,     
    BACK_CENTER,    
    LEFT_CENTER,
    RIGHT_CENTER,
    TOP_CENTER,
    BOTTOM_CENTER,
    
    //12 edges
    LEFT_LEFT,
    LEFT_RIGHT, 
    LEFT_TOP,
    LEFT_BOTTOM,
    
    RIGHT_LEFT, 
    RIGHT_RIGHT,
    RIGHT_TOP,
    RIGHT_BOTTOM,
    
    TOP_LEFT, 
    TOP_RIGHT, 
    TOP_FRONT,
    TOP_BACK,
    
    BOTTOM_LEFT, 
    BOTTOM_RIGHT, 
    BOTTOM_FRONT,
    BOTTOM_BACK,
    
    // 8 corners
    TOP_LEFT_FRONT_CORNER,
    TOP_LEFT_BACK_CORNER,
    TOP_RIGHT_FRONT_CORNER,
    TOP_RIGHT_BACK_CORNER,
    
    BOTTOM_LEFT_FRONT_CORNER,
    BOTTOM_LEFT_BACK_CORNER,
    BOTTOM_RIGHT_FRONT_CORNER,
    BOTTOM_RIGHT_BACK_CORNER;
    
    private CameraPositions(){}        
}
