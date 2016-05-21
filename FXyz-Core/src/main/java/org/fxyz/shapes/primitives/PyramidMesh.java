/**
* PyramidMesh.java
*
* Copyright (c) 2013-2015, F(X)yz
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* * Neither the name of the organization nor the
* names of its contributors may be used to endorse or promote products
* derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
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

public class PyramidMesh extends MeshView{
	
	private static final double DEFAULT_HEIGHT = 100.0D;
    private static final double DEFAULT_HYPOTENUSE = 100.0D;
	
	public PyramidMesh(){
		this(DEFAULT_HEIGHT,DEFAULT_HYPOTENUSE);
	}
	
	public PyramidMesh(double height, double hypotenuse) {
		setHypotenuse(hypotenuse);
		setHeight(height);
    }
	
	
	private TriangleMesh createPyramid(double hypotenuse , double height){
		
		TriangleMesh mesh = new TriangleMesh();
		
		float hy = (float)hypotenuse;
		float he = (float)height;
		
		mesh.getPoints().addAll(
				  0 ,   0 ,   0,    //point O
				  0 ,  he , -hy/2,  //point A
				-hy/2, he ,   0,    //point B
				 hy/2, he ,   0,	//point C
				  0 ,  he ,  hy/2	//point D
				);
		
		
		mesh.getTexCoords().addAll(0,0);
		
		mesh.getFaces().addAll(
				0 , 0 , 2 , 0 , 1 , 0 ,		// O-B-A
				0 , 0 , 1 , 0 , 3 , 0 ,		// O-A-C
				0 , 0 , 3 , 0 , 4 , 0 ,		// O-C-D
				0 , 0 , 4 , 0 , 2 , 0 ,		// O-D-B
				4 , 0 , 1 , 0 , 2 , 0 ,		// D-A-B
				4 , 0 , 3 , 0 , 1 , 0 		// D-C-A
				);
		
		
		return mesh;
		
	}
	
	
	 /*
    	Properties
	  */
	private final DoubleProperty hypotenuse = new SimpleDoubleProperty(){
		@Override
		protected void invalidated() {
			setMesh(createPyramid(getHypotenuse(), (float)getHeight()));
		}
	};

	public final double getHypotenuse() {
		return hypotenuse.get();
	}
	
	public final void setHypotenuse(double value) {
		hypotenuse.set(value);
	}

	public DoubleProperty hypotenuseProperty() {
		return hypotenuse;
	}
	
	
	private final DoubleProperty height = new SimpleDoubleProperty(){
        @Override
        protected void invalidated() {
			setMesh(createPyramid(getHypotenuse(), (float)getHeight()));
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
