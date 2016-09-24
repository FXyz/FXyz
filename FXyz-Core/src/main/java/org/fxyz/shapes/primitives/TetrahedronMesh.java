/**
 * TetrahedronMesh.java
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

import javafx.scene.shape.MeshView;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Moussaab AMRINE dy_amrine@esi.dz
 * @author  Yehya BELHAMRA dy_belhamra@esi.dz
 */

public class TetrahedronMesh extends MeshView{
	
	private static final double DEFAULT_HEIGHT = 100.0D;
	
	public TetrahedronMesh(){
		this(DEFAULT_HEIGHT);
	}
	
	public TetrahedronMesh(double height  ) { 
		setHeight(height);
    }
	
	
	private TriangleMesh createTetrahedron(double height){
		
		TriangleMesh mesh = new TriangleMesh();
		
		float he = (float)height;
		
		mesh.getPoints().addAll(
				0  ,  			 0 	    	   , (float)(-he/4) ,	///point O
			    0  , (float)(he/(Math.sqrt(3))) , (float)(he/4),	///point A
		(float)(-he/2) , (float)(-he/(2*Math.sqrt(3))) , (float)(he/4) , ///point B
		(float)(he/2)  , (float)(-he/(2*Math.sqrt(3))) , (float)(he/4)   ///point C
				);
		
		
		mesh.getTexCoords().addAll(0,0);
		
		mesh.getFaces().addAll(
				1 , 0 , 0 , 0 , 2 , 0 ,		// A-O-B
				2 , 0 , 0 , 0 , 3 , 0 ,		// B-O-C
				3 , 0 , 0 , 0 , 1 , 0 ,		// C-O-A
				1 , 0 , 2 , 0 , 3 , 0  		// A-B-C
				);
		
		
		return mesh;
		
	}
	
	
	 /*
    	Properties
	  */
	
	private final DoubleProperty height = new SimpleDoubleProperty(){
        @Override
        protected void invalidated() {
			setMesh(createTetrahedron((float)getHeight()));
		}        
    };

    public final double getHeight() {
        return height.get();
    }

    public final void setHeight(double value) {
        height.set(value);
    }

    public DoubleProperty heightProperty() {
        return height;
    }
    
}
