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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Moussaab AMRINE dy_amrine@esi.dz
 * @author  Yehya BELHAMRA dy_belhamra@esi.dz
 */

public class TrapezoidMesh extends MeshView {

    private final static double DEFAULT_SMALLSIZE 	= 30 ;
    private final static double DEFAULT_BIGSIZE         = 50 ;
    private final static double DEFAULT_HEIGHT		= 40 ;
    private final static double DEFAULT_DEPTH 		= 60 ;


    public TrapezoidMesh (){
        this(DEFAULT_SMALLSIZE,DEFAULT_BIGSIZE,DEFAULT_HEIGHT,DEFAULT_DEPTH);
    }

    public TrapezoidMesh (double smallSize , double bigSize , double height ,double depth){
        setSmallSize (smallSize);
        setBigSize (bigSize);
        setheight (height);
        setDepth (depth);

    }

    private TriangleMesh createTrapezoid (double smallSize , double bigSize , double high ,double depth){
        TriangleMesh m = new TriangleMesh();
        float s = ((float)smallSize) ;
        float b = ((float)bigSize);
        float h = ((float)high);
        float d = ((float)depth);

        //create Points
        m.getPoints().addAll(
                -s/2 , -h/2 ,  d/2,	// A = 0
                s/2 , -h/2 ,  d/2,	// B = 1
                -b/2 ,  h/2 ,  d/2,	// C = 2
                b/2 ,  h/2 ,  d/2,	// D = 3
                -s/2 , -h/2 , -d/2,	// E = 4
                s/2 , -h/2 , -d/2,	// F = 5
                -b/2 ,  h/2 , -d/2,	// G = 6
                b/2 ,  h/2 , -d/2	// H = 7
        );

        m.getTexCoords().addAll(0,0);

        m.getFaces().addAll(
                0 , 0 , 1 , 0 , 3 , 0 ,		// A-B-D
                0 , 0 , 3 , 0 , 2 , 0 , 	// A-D-C
                0 , 0 , 2 , 0 , 6 , 0 ,		// A-C-G
                0 , 0 , 6 , 0 , 4 , 0 , 	// A-G-E
                0 , 0 , 4 , 0 , 1 , 0 ,		// A-E-B
                1 , 0 , 4 , 0 , 5 , 0 , 	// B-E-F
                1 , 0 , 5 , 0 , 7 , 0 ,		// B-F-H
                1 , 0 , 7 , 0 , 3 , 0 ,		// B-H-D
                3 , 0 , 7 , 0 , 6 , 0 ,		// D-H-G
                3 , 0 , 6 , 0 , 2 , 0 ,		// D-G-C
                6 , 0 , 7 , 0 , 5 , 0 ,		// G-H-F
                6 , 0 , 5 , 0 , 4 , 0		// G-F-E
        );

        return m ;
    }


    private final DoubleProperty sizeSmall = new SimpleDoubleProperty(DEFAULT_SMALLSIZE){

        @Override
        protected void invalidated() {
            setMesh(createTrapezoid((float)getSmallSize(),(float)getBigSize(),(float)getHeight(),(float)getDepth()));
        }

    };

    public final double getSmallSize() {
        return sizeSmall.get();
    }

    public final void setSmallSize(double value) {
        sizeSmall.set(value);
    }

    public DoubleProperty sizeSmallProperty() {
        return sizeSmall;
    }

    private final DoubleProperty sizeBig = new SimpleDoubleProperty(DEFAULT_BIGSIZE){

        @Override
        protected void invalidated() {
            setMesh(createTrapezoid((float)getSmallSize(),(float)getBigSize(),(float)getHeight(),(float)getDepth()));
        }

    };

    public final double getBigSize() {
        return sizeBig.get();
    }

    public final void setBigSize(double value) {
        sizeBig.set(value);
    }

    public DoubleProperty sizeBigProperty() {
        return sizeBig;
    }


    private final DoubleProperty height = new SimpleDoubleProperty(DEFAULT_SMALLSIZE){

        @Override
        protected void invalidated() {
            setMesh(createTrapezoid((float)getSmallSize(),(float)getBigSize(),(float)getHeight(),(float)getDepth()));
        }

    };

    public final double getHeight() {
        return height.get();
    }

    public final void setheight(double value) {
        height.set(value);
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    private final DoubleProperty depth = new SimpleDoubleProperty(DEFAULT_DEPTH){

        @Override
        protected void invalidated() {
            setMesh(createTrapezoid((float)getSmallSize(),(float)getBigSize(),(float)getHeight(),(float)getDepth()));
        }

    };

    public final double getDepth() {
        return depth.get();
    }

    public final void setDepth(double value) {
        depth.set(value);
    }

    public DoubleProperty depthProperty() {
        return depth;
    }


}