/**
 * ScatterPlotMeshes.java
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

package org.fxyz3d.samples.shapes.compound;

import java.util.ArrayList;
import static javafx.application.Application.launch;
import javafx.scene.Node;
import org.fxyz3d.samples.shapes.ShapeBaseSample;
import org.fxyz3d.shapes.composites.ScatterPlotMesh;

/**
 *
 * @author Sean
 */
public class ScatterPlotMeshes extends ShapeBaseSample<ScatterPlotMesh> {

    

    public static void main(String[] args){launch(args);}
    
    @Override
    protected void createMesh() {
        model = new ScatterPlotMesh(250, 50, true);
        //model.getTransforms().add(rotateY);
        ArrayList<Double> dataX = new ArrayList<>();
        ArrayList<Double> dataY = new ArrayList<>();
        ArrayList<Double> dataZ = new ArrayList<>();
        for (int i = -250; i < 250; i++) {
            dataX.add((double)i);
            dataY.add(Math.sin(i * 2 * Math.PI) * (i != 0 ? i:1) / 50 + i);
            dataZ.add(Math.cos(i) * 50 + i);
        }

        model.setXYZData(dataX, dataY, dataZ);
        model.getTransforms().add(rotateY);
    }
    
    @Override
    protected void addMeshAndListeners() {
    }
    
    @Override
    protected Node buildControlPanel() {
        return null;
    }
}
