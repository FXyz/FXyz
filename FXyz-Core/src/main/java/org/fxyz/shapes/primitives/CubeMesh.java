/**
 * CubeMesh.java
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

package org.fxyz.shapes.primitives;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Dub
 */
public class CubeMesh extends MeshView {
    
    private final static double DEFAULT_SIZE = 10;

    public CubeMesh() {
        this(DEFAULT_SIZE);
    }
    
    public CubeMesh(double size) {
        setSize(size);
        setMesh(createCube((float)getSize()));
    }    
    
    
    private TriangleMesh createCube(float size) {
        TriangleMesh m = new TriangleMesh();

        float hw = size / 2,
                hh = hw,
                hd = hh;

        //create points
        m.getPoints().addAll(
            hw, hh, hd,
            hw, hh, -hd,
            hw, -hh, hd,
            hw, -hh, -hd,
            -hw, hh, hd,
            -hw, hh, -hd,
            -hw, -hh, hd,
            -hw, -hh, -hd
        );
        float x0 = 0.0f, x1 = 1.0f / 4.0f, x2 = 2.0f / 4.0f, x3 =  3.0f / 4.0f, x4 = 1.0f;
        float y0 = 0.0f, y1 = 1.0f /3.0f, y2 = 2.0f / 3.0f, y3 = 1.0f;
        
        
        
        m.getTexCoords().addAll(
            (x1 + getImagePadding()), (y0 + getImagePadding()), //0,1                
            (x2 - getImagePadding()), (y0 + getImagePadding()), //2,3             
            (x0)                    , (y1 + getImagePadding()), //4,5
            (x1 + getImagePadding()), (y1 + getImagePadding()), //6,7           
            (x2 - getImagePadding()), (y1 + getImagePadding()), //8,9           
            (x3),                     (y1 + getImagePadding()), //10,11           
            (x4),                     (y1 + getImagePadding()),  //12,13           
            (x0),                     (y2 - getImagePadding()), //14,15           
            (x1 + getImagePadding()), (y2 - getImagePadding()), //16,17           
            (x2 - getImagePadding()), (y2 - getImagePadding()), //18,19           
            (x3),                     (y2 - getImagePadding()), //20,21           
            (x4),                     (y2 - getImagePadding()), //22,23           
            (x1 + getImagePadding()), (y3 - getImagePadding()), //24,25           
            (x2),                     (y3 - getImagePadding())  //26,27
            
        );
        
        
        m.getFaces().addAll(
            0, 10, 2, 5, 1, 9,
            2, 5, 3, 4, 1, 9,
            
            4, 7, 5, 8, 6, 2,
            6, 2, 5, 8, 7, 3,
            
            0, 13, 1, 9, 4, 12,
            4, 12, 1, 9, 5, 8,
            
            2, 1, 6, 0, 3, 4,
            3, 4, 6, 0, 7, 3,
            
            0, 10, 4, 11, 2, 5,
            2, 5, 4, 11, 6, 6,
            
            1, 9, 3, 4, 5, 8,
            5, 8, 3, 4, 7, 3
        );
        
        return m;
    }
    private final DoubleProperty size = new SimpleDoubleProperty(DEFAULT_SIZE){

        @Override
        protected void invalidated() {
            setMesh(createCube((float)getSize()));
        }
        
    };

    public final double getSize() {
        return size.get();
    }

    public final void setSize(double value) {
        size.set(value);
    }

    public DoubleProperty sizeProperty() {
        return size;
    }
    
    private final FloatProperty imagePadding = new SimpleFloatProperty(0.0015f);

    public float getImagePadding() {
        return imagePadding.get();
    }

    public void setImagePadding(float value) {
        imagePadding.set(value);
    }

    public FloatProperty imagePaddingProperty() {
        return imagePadding;
    }
    
    
}
