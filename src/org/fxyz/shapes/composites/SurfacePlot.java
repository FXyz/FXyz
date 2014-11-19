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

package org.fxyz.shapes.composites;

import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Sean
 */
public class SurfacePlot extends Group {

    public AmbientLight selfLight = new AmbientLight(Color.WHITE);
    public double nodeRadius = 1;
    private double axesSize = 1000;
    private boolean normalized = false;
    public boolean selfLightEnabled = true;
    public Color color = Color.WHITE;
    private TriangleMesh mesh;
    public MeshView meshView;
    public PhongMaterial material;

    public SurfacePlot(boolean selfLit) {
        selfLightEnabled = selfLit;
        init();
    }

    public SurfacePlot(float[][] arrayY, int spacing, Color color, boolean fill, boolean selfLit) {
        selfLightEnabled = selfLit;
        init();
        setHeightData(arrayY,spacing, color,selfLit,fill);
    }

    private void init() {
        if (selfLightEnabled) {
            getChildren().add(selfLight);
        }
        setDepthTest(DepthTest.ENABLE);
    }

    public void setHeightData(float[][] arrayY, int spacing, Color color, boolean ambient, boolean fill) {
        material = new PhongMaterial();
        material.setSpecularColor(color);
        material.setDiffuseColor(color);

        mesh = new TriangleMesh();

        // Fill Points
        for (int x = 0; x < arrayY.length; x++) {
            for (int z = 0; z < arrayY[0].length; z++) {
                mesh.getPoints().addAll(x * spacing, arrayY[x][z], z * spacing);
            }
        }

        //for now we'll just make an empty texCoordinate group
        mesh.getTexCoords().addAll(0, 0);
        int total = arrayY.length * arrayY.length;
        int nextRow = arrayY.length;
        //Add the faces "winding" the points generally counter clock wise
        for (int i = 0; i < total - nextRow -1; i++) {
            //Top upper left triangle
            mesh.getFaces().addAll(i,0,i+nextRow,0,i+1,0);
            //Top lower right triangle
            mesh.getFaces().addAll(i+nextRow,0,i+nextRow + 1,0,i+1,0);
            
            //Bottom            
        }
        //Create a viewable MeshView to be added to the scene
        //To add a TriangleMesh to a 3D scene you need a MeshView container object
        meshView = new MeshView(mesh);
        //The MeshView allows you to control how the TriangleMesh is rendered
        if(fill) { 
            meshView.setDrawMode(DrawMode.FILL);
        } else {
            meshView.setDrawMode(DrawMode.LINE); //show lines only by default
        }
        meshView.setCullFace(CullFace.BACK); //Removing culling to show back lines

        getChildren().add(meshView);
        meshView.setMaterial(material);
        if (ambient) {
            selfLight.getScope().add(meshView);
            if(!getChildren().contains(selfLight))
                getChildren().add(selfLight);
        }
        else if(getChildren().contains(selfLight))
            getChildren().remove(selfLight);
        setDepthTest(DepthTest.ENABLE);
    }
}