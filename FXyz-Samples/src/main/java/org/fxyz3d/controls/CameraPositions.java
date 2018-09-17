/**
 * CameraPositions.java
 *
 * Copyright (c) 2013-2016, F(X)yz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of F(X)yz, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL F(X)yz BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package org.fxyz3d.controls;

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
