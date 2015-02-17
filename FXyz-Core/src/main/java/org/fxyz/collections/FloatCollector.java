/**
* FloatCollector.java
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
 * @author JosÃ© Pereda Llamas
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
