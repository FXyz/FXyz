/**
 * ScatterPlotMesh.java
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

package org.fxyz.shapes.composites;

import java.util.ArrayList;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Point3D;

/**
 *
 * @author Sean
 */
public class ScatterPlotMesh extends Group {

    private ArrayList<Double> xAxisData = new ArrayList<>();
    private ArrayList<Double> yAxisData = new ArrayList<>();
    private ArrayList<Double> zAxisData = new ArrayList<>();
    public Group scatterDataGroup = new Group();
    public double nodeRadius = 1;
    private double axesSize = 1000;
    private boolean normalized = false;
    public boolean selfLightEnabled = true;

    double plotSize = 1000;
    double nodeSize = 1;

    public enum NodeType {
        SPHERE, CUBE, PYRAMID, STAR
    };
    protected NodeType defaultNodeType = NodeType.SPHERE;

    public ScatterPlotMesh(boolean selfLit) {
        selfLightEnabled = selfLit;
        init();
    }

    public ScatterPlotMesh(double axesSize, double nodeRadius, boolean selfLit) {
        this.nodeRadius = nodeRadius;
        init();
    }

    private void init() {
        setDepthTest(DepthTest.ENABLE);
    }

    public void setXYZData(ArrayList<Double> xData, ArrayList<Double> yData, ArrayList<Double> zData) {
        xAxisData = xData;
        yAxisData = yData;
        zAxisData = zData;
        getChildren().clear();
        //for now we will always default to x axis
        //later we could maybe dynamically determine the smallest axis and then
        //uses 0's for the other axes that are larger.
        ArrayList<Point3D> point3DList = new ArrayList<>();

        for (int i = 0; i < xAxisData.size(); i++) {
            //some safety checks for array sizes
            double translateY = 0.0;
            double translateZ = 0.0;
            if (!yAxisData.isEmpty() && yAxisData.size() > i) {
                translateY = yAxisData.get(i);
            }
            if (!zAxisData.isEmpty() && zAxisData.size() > i) {
                translateZ = zAxisData.get(i);
            }
            setTranslateX(xAxisData.get(i));
            //Convert to Floats and build list of adjusted points
            point3DList.add(new Point3D(xAxisData.get(i).floatValue(), (float) translateY, (float) translateZ));

            float width = 1;
            final TriangleMesh mesh = new TriangleMesh();
            //add each point. For each point add another point shifted on Z axis by width
            //This extra point allows us to build triangles later
            point3DList.stream().map((point) -> {
                //Rear points
                //top right rear point
                mesh.getPoints().addAll(point.x + width, point.y + width, point.z + width);
                return point;
            }).map((point) -> {
                //top left rear point
                mesh.getPoints().addAll(point.x - width, point.y + width, point.z + width);
                return point;
            }).map((point) -> {
                //bottom right rear point
                mesh.getPoints().addAll(point.x + width, point.y - width, point.z + width);
                return point;
            }).map((point) -> {
                //bottom left rear point
                mesh.getPoints().addAll(point.x - width, point.y - width, point.z + width);
                return point;
            }).map((point) -> {
                //Front points
                //top right front point
                mesh.getPoints().addAll(point.x + width, point.y + width, point.z - width);
                return point;
            }).map((point) -> {
                //top left front point
                mesh.getPoints().addAll(point.x - width, point.y + width, point.z - width);
                return point;
            }).map((point) -> {
                //bottom right front point
                mesh.getPoints().addAll(point.x + width, point.y - width, point.z - width);
                return point;
            }).forEach((point) -> {
                //bottom left front point
                mesh.getPoints().addAll(point.x - width, point.y - width, point.z - width);
            });
            //add dummy Texture Coordinate
            mesh.getTexCoords().addAll(0, 0);
            //Now generate nodes for each point
            for (int p = 8; p < point3DList.size() * 7; p += 8) {  //add each segment
                //Wind the next 8 vertices as a cube.  The cube itself will represent the data
                //Vertices wound counter-clockwise which is the default front face of any Triangle
                //Rear triangle faces should be wound clockwise to face away from center
                mesh.getFaces().addAll(p, 0, p + 3, 0, p + 2, 0); //TRR,BLR,BRR
                mesh.getFaces().addAll(p + 3, 0, p, 0, p + 1, 0); //BLR,TRR,TLR
                //left side faces
                mesh.getFaces().addAll(p + 1, 0, p + 5, 0, p + 3, 0); //TLR,TLF,BLR
                mesh.getFaces().addAll(p + 5, 0, p + 7, 0, p + 3, 0); //TLF,BLR,BLF
                //front side faces
                mesh.getFaces().addAll(p + 5, 0, p + 7, 0, p + 4, 0); //TLF,BLF,TLR
                mesh.getFaces().addAll(p + 4, 0, p + 7, 0, p + 6, 0); //TRF,BLF,BRF
                //front side faces
                mesh.getFaces().addAll(p + 4, 0, p + 6, 0, p + 2, 0); //TRF,BRF,BRR
                mesh.getFaces().addAll(p + 4, 0, p + 2, 0, p, 0); //TRF,BRR,TRR

                //Top faces
                mesh.getFaces().addAll(p, 0, p + 1, 0, p + 3, 0); //TRR,TLR,TRF
                mesh.getFaces().addAll(p + 1, 0, p + 5, 0, p + 3, 0); //TLR,TLF,TRF

                //bottom faces
                mesh.getFaces().addAll(p + 3, 0, p + 7, 0, p + 6, 0); //BLR,BLF,BRF
                mesh.getFaces().addAll(p + 3, 0, p + 6, 0, p + 2, 0); //BLR,BRF,BRR
            }

            //Need to add the mesh to a MeshView before adding to our 3D scene
            MeshView meshView = new MeshView(mesh);
            meshView.setDrawMode(DrawMode.LINE);  //Fill so that the line shows width

            //Color hsb = Color.hsb(((double) i / 12) * 360, 1.0, 1.0, 0.5);
            PhongMaterial material = new PhongMaterial(Color.ROYALBLUE);
            //material.setDiffuseColor(hsb);
            //material.setSpecularColor(hsb);
            meshView.setMaterial(material);
            //Make sure you Cull the Back so that no black shows through
            meshView.setCullFace(CullFace.NONE);          
            getChildren().addAll(meshView);
        }
    }

}
