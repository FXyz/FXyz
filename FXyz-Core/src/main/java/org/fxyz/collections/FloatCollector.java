/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.collections;

import java.util.Arrays;

/**
 * Collector to generate a float[] array from a DoubleStream of floats
 * 
 * General use:
 * .collect(FloatCollector::new, FloatCollector::add, FloatCollector::join)
 * Known initial size:
 * .collect(()- new FloatCollector(size), FloatCollector::add, FloatCollector::join)
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
