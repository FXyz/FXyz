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

import java.util.ArrayList;
import javafx.scene.AmbientLight;
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
    public AmbientLight selfLight = new AmbientLight(Color.WHITE);
    public double nodeRadius = 1;    
    private double axesSize = 1000;
    private boolean normalized = false;
    public boolean selfLightEnabled = true;
    
        double plotSize = 1000;
        double nodeSize = 1;
    public enum NodeType {SPHERE, CUBE, PYRAMID, STAR};
    private NodeType defaultNodeType = NodeType.SPHERE;
    
    public ScatterPlotMesh(boolean selfLit) {
        selfLightEnabled = selfLit;
        init();
    }
    public ScatterPlotMesh(double axesSize, double nodeRadius, boolean selfLit) {
        selfLightEnabled = selfLit;
        this.axesSize = axesSize;
        this.nodeRadius = nodeRadius;
        init();
    }    
    private void init(){
        if(selfLightEnabled) {
            getChildren().add(selfLight);
        }
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

        for(int i=0;i<xAxisData.size();i++) {
            //some safety checks for array sizes
            double translateY = 0.0;
            double translateZ = 0.0;            
            if(!yAxisData.isEmpty() && yAxisData.size() > i)
                translateY = yAxisData.get(i);
            if(!zAxisData.isEmpty() && zAxisData.size() > i)
                translateZ = zAxisData.get(i);
            setTranslateX(xAxisData.get(i));
            //Convert to Floats and build list of adjusted points
            point3DList.add(new Point3D(new Float(xAxisData.get(i)), new Float(translateY), new Float(translateZ)));

            float width = 1;
            final TriangleMesh mesh = new TriangleMesh();
            //add each point. For each point add another point shifted on Z axis by width
            //This extra point allows us to build triangles later
            for(Point3D point: point3DList) {
                //Rear points
                //top right rear point
                mesh.getPoints().addAll(point.x+width,point.y+width,point.z+width);
                //top left rear point
                mesh.getPoints().addAll(point.x-width,point.y+width,point.z+width);
                //bottom right rear point
                mesh.getPoints().addAll(point.x+width,point.y-width,point.z+width);
                //bottom left rear point
                mesh.getPoints().addAll(point.x-width,point.y-width,point.z+width);
                //Front points
                //top right front point
                mesh.getPoints().addAll(point.x+width,point.y+width,point.z-width);
                //top left front point
                mesh.getPoints().addAll(point.x-width,point.y+width,point.z-width);
                //bottom right front point
                mesh.getPoints().addAll(point.x+width,point.y-width,point.z-width);
                //bottom left front point
                mesh.getPoints().addAll(point.x-width,point.y-width,point.z-width);
            }
            //add dummy Texture Coordinate
            mesh.getTexCoords().addAll(0,0);
            //Now generate nodes for each point
            for(int p=8;p<point3DList.size()*7;p+=8) {  //add each segment
                //Wind the next 8 vertices as a cube.  The cube itself will represent the data
                //Vertices wound counter-clockwise which is the default front face of any Triangle
                //Rear triangle faces should be wound clockwise to face away from center
                mesh.getFaces().addAll(p,0,p+3,0,p+2,0); //TRR,BLR,BRR
                mesh.getFaces().addAll(p+3,0,p,0,p+1,0); //BLR,TRR,TLR
                //left side faces
                mesh.getFaces().addAll(p+1,0,p+5,0,p+3,0); //TLR,TLF,BLR
                mesh.getFaces().addAll(p+5,0,p+7,0,p+3,0); //TLF,BLR,BLF
                //front side faces
                mesh.getFaces().addAll(p+5,0,p+7,0,p+4,0); //TLF,BLF,TLR
                mesh.getFaces().addAll(p+4,0,p+7,0,p+6,0); //TRF,BLF,BRF
                //front side faces
                mesh.getFaces().addAll(p+4,0,p+6,0,p+2,0); //TRF,BRF,BRR
                mesh.getFaces().addAll(p+4,0,p+2,0,p,0); //TRF,BRR,TRR
               
                //Top faces
                mesh.getFaces().addAll(p,0,p+1,0,p+3,0); //TRR,TLR,TRF
                mesh.getFaces().addAll(p+1,0,p+5,0,p+3,0); //TLR,TLF,TRF
               
                //bottom faces
                mesh.getFaces().addAll(p+3,0,p+7,0,p+6,0); //BLR,BLF,BRF
                mesh.getFaces().addAll(p+3,0,p+6,0,p+2,0); //BLR,BRF,BRR
            }
           
            //Need to add the mesh to a MeshView before adding to our 3D scene
            MeshView meshView = new MeshView(mesh);
            meshView.setDrawMode(DrawMode.FILL);  //Fill so that the line shows width
                              
            Color hsb = Color.hsb((new Double(i)  / 12) * 360, 1.0, 1.0, 0.5);
            PhongMaterial material = new PhongMaterial(hsb);
            material.setDiffuseColor(hsb);
            material.setSpecularColor(hsb);
            meshView.setMaterial(material);
            //Make sure you Cull the Back so that no black shows through
            meshView.setCullFace(CullFace.BACK);    
//            //Add some ambient light so folks can see it
//            Group line = new Group();
//            AmbientLight light = new AmbientLight(Color.WHITE);
//            light.getScope().add(meshView);
//            line.getChildren().add(light);
//            line.getChildren().add(meshView);           
            getChildren().addAll(meshView);           
        }
    }
    
}
