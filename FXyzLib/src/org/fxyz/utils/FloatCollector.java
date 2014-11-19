/*
 * Copyright (C) 2013-2014 F(X)yz, 
 * Sean Phillips, Jason Pollastrini
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

package org.fxyz.utils;

import java.util.Arrays;

/**
 * Collector to generate a float[] array from a DoubleStream of floats
 * 
 * General use:
 * .collect(FloatCollector::new, FloatCollector::add, FloatCollector::join)
 * Known initial size:
 * .collect(()->new FloatCollector(size), FloatCollector::add, FloatCollector::join)
 * 
 * Generates a FloatCollector, use FloatCollector.toArray() to get the float[]
 * 
 * @author José Pereda Llamas
 * Created on 19-nov-2014 - 18:51:11
 */
public class FloatCollector {

    private float[] curr=new float[64];
    private int size;

    public FloatCollector(){}
    
    public FloatCollector(int initialSize){
        if(curr.length<initialSize){
            curr=Arrays.copyOf(curr, initialSize);
        }
    }
    public void add(double d) {
        if(curr.length==size){
            curr=Arrays.copyOf(curr, size*2);
        }
        curr[size++]=(float)d;
    }

    public void join(FloatCollector other) {
        if(size+other.size > curr.length) {
            curr=Arrays.copyOf(curr, size+other.size);
        }
        System.arraycopy(other.curr, 0, curr, size, other.size);
        size+=other.size;
    }

    public float[] toArray() {
        if(size!=curr.length){
            curr=Arrays.copyOf(curr, size);
        }
        return curr;
    }
}
