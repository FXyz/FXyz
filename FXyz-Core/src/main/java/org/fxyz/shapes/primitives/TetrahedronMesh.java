/*
 * Copyright (C) 2013-2015 F(X)yz, 
 * Sean Phillips, Jason Pollastrini and Jose Pereda
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
