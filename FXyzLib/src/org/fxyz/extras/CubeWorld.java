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

package org.fxyz.extras;

import java.util.ArrayList;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

/**
 *
 * @author SPhillips
 */
public class CubeWorld extends Group {
    public Group cubeWorldChildren = new Group();
    
    // X-Axis Material
    public final PhongMaterial redMaterial = new PhongMaterial();
    // Y-Axis Material
    public final PhongMaterial greenMaterial = new PhongMaterial();
    // Z-Axis Material
    public final PhongMaterial blueMaterial = new PhongMaterial();
    public double gridLinesOpacity = 1.0;
    public double gridPanelsOpacity = 0.25;
    //Color.DARKSLATEGRAY
    public Color panelWallColor = new Color(0.18431373f, 0.30980393f, 0.30980393f,gridPanelsOpacity);
    //DARKGRAY
    public Color panelFloorColor = new Color(0.6627451f, 0.6627451f, 0.6627451f,gridPanelsOpacity);
    //Color.DARKSLATEGRAY
    public Color panelCeilingColor = new Color(0.18431373f, 0.30980393f, 0.30980393f,gridPanelsOpacity);
    //Color.DARKSLATEGRAY
    public Color gridLinesWallColor = new Color(0.18431373f, 0.30980393f, 0.30980393f,gridPanelsOpacity);
    //Color.LIGHTGRAY;
    public Color gridLinesFloorColor = new Color(0.827451f, 0.827451f, 0.827451f, gridLinesOpacity);
    //Color.DARKSLATEGRAY        
    public Color gridLinesCeilingColor = new Color(0.18431373f, 0.30980393f, 0.30980393f,gridPanelsOpacity);
    
    
          
    private double axesSize = 1000;
    private double gridLineSpacing = 100;
    final double axesThickness = 5;
    final double gridSize = 2;
    private boolean selfLightEnabled = true;

    double cameraRX = 0;
    double cameraRY = 0;
    double cameraRZ = 0;

    final Group axisGroup = new Group();

    public Rectangle x1AxisRectangle;
    public Rectangle x2AxisRectangle;
    public Rectangle y1AxisRectangle;
    public Rectangle y2AxisRectangle;
    public Rectangle z1AxisRectangle;
    public Rectangle z2AxisRectangle;

    public boolean showx1AxisRectangle = true;
    public boolean showx2AxisRectangle = true;
    public boolean showy1AxisRectangle = true;
    public boolean showy2AxisRectangle = true;
    public boolean showz1AxisRectangle = true;
    public boolean showz2AxisRectangle = true;    
    
    public Group xAxesGroup = new Group();
    public Group yAxesGroup = new Group();
    public Group zAxesGroup = new Group();
    
    public boolean showxAxesGroup = true;
    public boolean showyAxesGroup = true;
    public boolean showzAxesGroup = true;
    
    public Group xy1GridLinesGroup = new Group();
    public Group xx1GridLinesGroup = new Group();
    public Group yy1GridLinesGroup = new Group();
    public Group yx1GridLinesGroup = new Group();
    public Group zy1GridLinesGroup = new Group();
    public Group zx1GridLinesGroup = new Group();
    public Group xy2GridLinesGroup = new Group();
    public Group xx2GridLinesGroup = new Group();
    public Group yy2GridLinesGroup = new Group();
    public Group yx2GridLinesGroup = new Group();
    public Group zy2GridLinesGroup = new Group();
    public Group zx2GridLinesGroup = new Group();

    public boolean showxy1GridLinesGroup = true;
    public boolean showxx1GridLinesGroup = true;
    public boolean showyy1GridLinesGroup = true;
    public boolean showyx1GridLinesGroup = true;
    public boolean showzy1GridLinesGroup = true;
    public boolean showzx1GridLinesGroup = true;
    public boolean showxy2GridLinesGroup = true;
    public boolean showxx2GridLinesGroup = true;
    public boolean showyy2GridLinesGroup = true;
    public boolean showyx2GridLinesGroup = true;
    public boolean showzy2GridLinesGroup = true;
    public boolean showzx2GridLinesGroup = true;
    
    public CubeWorld(boolean ambientLight) {
        selfLightEnabled = ambientLight;
        init();
        
    }
    public CubeWorld(double size, double spacing, boolean selfLight) {
        axesSize = size;
        gridLineSpacing = spacing;
        selfLightEnabled = selfLight;        
        init();
    }
    
    private void init(){
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);  
        
        buildAxes(axesSize, axesThickness);
        buildPanels(axesSize);
        buildGrids(axesSize, gridLineSpacing);
        buildEventHandlers();        
        getChildren().add(cubeWorldChildren); //Holds ScatterPlot data
        if(selfLightEnabled) {
            AmbientLight light = new AmbientLight(Color.WHITE);
            getChildren().add(light);
        }
    }
    
    private void buildPanels(double size) {
        //@SMP TODO might be easier to just replace the Rectangle side panels with really flat Box 3D objects
        x1AxisRectangle = new Rectangle(size, size, panelWallColor);
        x2AxisRectangle = new Rectangle(size, size, panelWallColor);
        y1AxisRectangle = new Rectangle(size, size, panelWallColor);
        y2AxisRectangle = new Rectangle(size, size, panelWallColor);
        z1AxisRectangle = new Rectangle(size, size, panelFloorColor);
        z2AxisRectangle = new Rectangle(size, size, panelCeilingColor);

        x1AxisRectangle.setTranslateX(-size / 2);
        x1AxisRectangle.setTranslateY(-size / 2);
        x1AxisRectangle.setTranslateZ(-size / 2);
        x2AxisRectangle.setTranslateX(-size / 2);
        x2AxisRectangle.setTranslateY(-size / 2);
        x2AxisRectangle.setTranslateZ(size / 2);
        getChildren().addAll(x1AxisRectangle, x2AxisRectangle);

        y2AxisRectangle.setTranslateY(-size / 2);
        y2AxisRectangle.setRotationAxis(Rotate.Y_AXIS);
        y2AxisRectangle.setRotate(89.9);
        y1AxisRectangle.setTranslateX(-size);
        y1AxisRectangle.setTranslateY(-size / 2);
        y1AxisRectangle.setRotationAxis(Rotate.Y_AXIS);
        y1AxisRectangle.setRotate(89.9);
        getChildren().addAll(y1AxisRectangle, y2AxisRectangle);

        z1AxisRectangle.setTranslateX(-size / 2);
        z1AxisRectangle.setRotationAxis(Rotate.X_AXIS);
        z1AxisRectangle.setRotate(89.9);
        z2AxisRectangle.setTranslateX(-size / 2);
        z2AxisRectangle.setTranslateY(-size);
        z2AxisRectangle.setRotationAxis(Rotate.X_AXIS);
        z2AxisRectangle.setRotate(89.9);
        getChildren().addAll(z1AxisRectangle, z2AxisRectangle);
    }
    private void buildGrids(double size, double spacing) {
        //fill in grid lines for X Axis
            PhongMaterial phong = new PhongMaterial();
            phong.setSpecularColor(gridLinesWallColor);
            phong.setDiffuseColor(gridLinesWallColor);        
        ArrayList xy1Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);
            newCyl.setTranslateX((-size / 2) + i);
            newCyl.setTranslateZ(-size / 2);
            xy1Cyls.add(newCyl);
        }
        xy1GridLinesGroup = new Group(xy1Cyls);
        //Now build the grids for the mirror image
        ArrayList xy2Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);          
            newCyl.setTranslateX((-size / 2) + i);
            newCyl.setTranslateZ(size / 2);
            xy2Cyls.add(newCyl);
        }
        xy2GridLinesGroup = new Group(xy2Cyls);

        ArrayList xx1Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);          
            newCyl.setTranslateZ(-size / 2);
            newCyl.setTranslateY((size / 2) - i);
            newCyl.setRotationAxis(Rotate.Z_AXIS);
            newCyl.setRotate(90);
            xx1Cyls.add(newCyl);
        }
        xx1GridLinesGroup = new Group(xx1Cyls);
        //Now build the grids for the mirror image
        ArrayList xx2Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);           
            newCyl.setTranslateZ(size / 2);
            newCyl.setTranslateY((size / 2) - i);
            newCyl.setRotationAxis(Rotate.Z_AXIS);
            newCyl.setRotate(90);
            xx2Cyls.add(newCyl);
        }
        xx2GridLinesGroup = new Group(xx2Cyls);
        //Add the sub groups to the parent group 
        getChildren().addAll(xy1GridLinesGroup);
        getChildren().addAll(xx1GridLinesGroup);
        getChildren().addAll(xy2GridLinesGroup);
        getChildren().addAll(xx2GridLinesGroup);

        // File in grid Lines for Y Axis //////////////////////////
        ArrayList yy1Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);           
            newCyl.setTranslateX(-size / 2);
            newCyl.setTranslateZ((-size / 2) + i);
            yy1Cyls.add(newCyl);
        }
        yy1GridLinesGroup = new Group(yy1Cyls);
        //Now build the grids for the mirror image
        ArrayList yy2Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);           
            newCyl.setTranslateX(size / 2);
            newCyl.setTranslateZ((-size / 2) + i);
            yy2Cyls.add(newCyl);
        }
        yy2GridLinesGroup = new Group(yy2Cyls);

        ArrayList yx1Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);           
            newCyl.setTranslateX(-size / 2);
            newCyl.setTranslateY((size / 2) - i);
            newCyl.setRotationAxis(Rotate.X_AXIS);
            newCyl.setRotate(90);
            yx1Cyls.add(newCyl);
        }
        yx1GridLinesGroup = new Group(yx1Cyls);
        //Now build the grids for the mirror image
        ArrayList yx2Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);               
            newCyl.setTranslateX(size / 2);
            newCyl.setTranslateY((size / 2) - i);
            newCyl.setRotationAxis(Rotate.X_AXIS);
            newCyl.setRotate(90);
            yx2Cyls.add(newCyl);
        }
        yx2GridLinesGroup = new Group(yx2Cyls);
        //Add the sub groups to the parent group
        getChildren().addAll(yy1GridLinesGroup);
        getChildren().addAll(yx1GridLinesGroup);
        getChildren().addAll(yy2GridLinesGroup);
        getChildren().addAll(yx2GridLinesGroup);

        // File in grid Lines for Z Axis //////////////////////////
        phong = new PhongMaterial();
        phong.setSpecularColor(gridLinesFloorColor);
        phong.setDiffuseColor(gridLinesFloorColor);
           
        ArrayList zy1Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);              
            newCyl.setTranslateY(size / 2);
            newCyl.setTranslateX((-size / 2) + i);
            newCyl.setRotationAxis(Rotate.X_AXIS);
            newCyl.setRotate(90);
            zy1Cyls.add(newCyl);
        }
        zy1GridLinesGroup = new Group(zy1Cyls);
        //Now build the grids for the mirror image
        ArrayList zy2Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);             
            newCyl.setTranslateY(-size / 2);
            newCyl.setTranslateX((-size / 2) + i);
            newCyl.setRotationAxis(Rotate.X_AXIS);
            newCyl.setRotate(90);
            zy2Cyls.add(newCyl);
        }
        zy2GridLinesGroup = new Group(zy2Cyls);

        ArrayList zx1Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);             
            newCyl.setTranslateY(size / 2);
            newCyl.setTranslateZ((-size / 2) + i);
            newCyl.setRotationAxis(Rotate.Z_AXIS);
            newCyl.setRotate(90);
            zx1Cyls.add(newCyl);
        }
        zx1GridLinesGroup = new Group(zx1Cyls);
        //Now build the grids for the mirror image
        ArrayList zx2Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);             
            newCyl.setTranslateY(-size / 2);
            newCyl.setTranslateZ((-size / 2) + i);
            newCyl.setRotationAxis(Rotate.Z_AXIS);
            newCyl.setRotate(90);
            zx2Cyls.add(newCyl);
        }
        zx2GridLinesGroup = new Group(zx2Cyls);

        //Add the sub groups to the parent group
        getChildren().addAll(zy1GridLinesGroup);
        getChildren().addAll(zx1GridLinesGroup);
        getChildren().addAll(zy2GridLinesGroup);
        getChildren().addAll(zx2GridLinesGroup);
    }
    private void buildGridsOld(double size, double spacing) {
        //fill in grid lines for X Axis
            PhongMaterial phong = new PhongMaterial();
            phong.setSpecularColor(gridLinesWallColor);
            phong.setDiffuseColor(gridLinesWallColor);        
        ArrayList xy1Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);
            newCyl.setTranslateX((-size / 2) + i);
            newCyl.setTranslateZ(-size / 2);
            xy1Cyls.add(newCyl);
        }
        xy1GridLinesGroup = new Group(xy1Cyls);
        //Now build the grids for the mirror image
        ArrayList xy2Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);          
            newCyl.setTranslateX((-size / 2) + i);
            newCyl.setTranslateZ(size / 2);
            xy2Cyls.add(newCyl);
        }
        xy2GridLinesGroup = new Group(xy2Cyls);

        ArrayList xx1Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);          
            newCyl.setTranslateZ(-size / 2);
            newCyl.setTranslateY((size / 2) - i);
            newCyl.setRotationAxis(Rotate.Z_AXIS);
            newCyl.setRotate(90);
            xx1Cyls.add(newCyl);
        }
        xx1GridLinesGroup = new Group(xx1Cyls);
        //Now build the grids for the mirror image
        ArrayList xx2Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);           
            newCyl.setTranslateZ(size / 2);
            newCyl.setTranslateY((size / 2) - i);
            newCyl.setRotationAxis(Rotate.Z_AXIS);
            newCyl.setRotate(90);
            xx2Cyls.add(newCyl);
        }
        xx2GridLinesGroup = new Group(xx2Cyls);
        //Add the sub groups to the parent group 
        getChildren().addAll(xy1GridLinesGroup);
        getChildren().addAll(xx1GridLinesGroup);
        getChildren().addAll(xy2GridLinesGroup);
        getChildren().addAll(xx2GridLinesGroup);

        // File in grid Lines for Y Axis //////////////////////////
        ArrayList yy1Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);           
            newCyl.setTranslateX(-size / 2);
            newCyl.setTranslateZ((-size / 2) + i);
            yy1Cyls.add(newCyl);
        }
        yy1GridLinesGroup = new Group(yy1Cyls);
        //Now build the grids for the mirror image
        ArrayList yy2Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);           
            newCyl.setTranslateX(size / 2);
            newCyl.setTranslateZ((-size / 2) + i);
            yy2Cyls.add(newCyl);
        }
        yy2GridLinesGroup = new Group(yy2Cyls);

        ArrayList yx1Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);           
            newCyl.setTranslateX(-size / 2);
            newCyl.setTranslateY((size / 2) - i);
            newCyl.setRotationAxis(Rotate.X_AXIS);
            newCyl.setRotate(90);
            yx1Cyls.add(newCyl);
        }
        yx1GridLinesGroup = new Group(yx1Cyls);
        //Now build the grids for the mirror image
        ArrayList yx2Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);               
            newCyl.setTranslateX(size / 2);
            newCyl.setTranslateY((size / 2) - i);
            newCyl.setRotationAxis(Rotate.X_AXIS);
            newCyl.setRotate(90);
            yx2Cyls.add(newCyl);
        }
        yx2GridLinesGroup = new Group(yx2Cyls);
        //Add the sub groups to the parent group
        getChildren().addAll(yy1GridLinesGroup);
        getChildren().addAll(yx1GridLinesGroup);
        getChildren().addAll(yy2GridLinesGroup);
        getChildren().addAll(yx2GridLinesGroup);

        // File in grid Lines for Z Axis //////////////////////////
        phong = new PhongMaterial();
        phong.setSpecularColor(gridLinesFloorColor);
        phong.setDiffuseColor(gridLinesFloorColor);
           
        ArrayList zy1Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);              
            newCyl.setTranslateY(size / 2);
            newCyl.setTranslateX((-size / 2) + i);
            newCyl.setRotationAxis(Rotate.X_AXIS);
            newCyl.setRotate(90);
            zy1Cyls.add(newCyl);
        }
        zy1GridLinesGroup = new Group(zy1Cyls);
        //Now build the grids for the mirror image
        ArrayList zy2Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);             
            newCyl.setTranslateY(-size / 2);
            newCyl.setTranslateX((-size / 2) + i);
            newCyl.setRotationAxis(Rotate.X_AXIS);
            newCyl.setRotate(90);
            zy2Cyls.add(newCyl);
        }
        zy2GridLinesGroup = new Group(zy2Cyls);

        ArrayList zx1Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);             
            newCyl.setTranslateY(size / 2);
            newCyl.setTranslateZ((-size / 2) + i);
            newCyl.setRotationAxis(Rotate.Z_AXIS);
            newCyl.setRotate(90);
            zx1Cyls.add(newCyl);
        }
        zx1GridLinesGroup = new Group(zx1Cyls);
        //Now build the grids for the mirror image
        ArrayList zx2Cyls = new ArrayList<>();
        for (int i = 0; i < size; i += spacing) {
            Cylinder newCyl = new Cylinder(gridSize, size);
            newCyl.setMaterial(phong);             
            newCyl.setTranslateY(-size / 2);
            newCyl.setTranslateZ((-size / 2) + i);
            newCyl.setRotationAxis(Rotate.Z_AXIS);
            newCyl.setRotate(90);
            zx2Cyls.add(newCyl);
        }
        zx2GridLinesGroup = new Group(zx2Cyls);

        //Add the sub groups to the parent group
        getChildren().addAll(zy1GridLinesGroup);
        getChildren().addAll(zx1GridLinesGroup);
        getChildren().addAll(zy2GridLinesGroup);
        getChildren().addAll(zx2GridLinesGroup);
    }

    private void buildAxes(double size, double axisThickness) {


        //XYZ 1
        final Cylinder x1AxisCyl = new Cylinder(axisThickness, size);
        final Cylinder y1AxisCyl = new Cylinder(axisThickness, size);
        final Cylinder z1AxisCyl = new Cylinder(axisThickness, size);
        x1AxisCyl.setTranslateY(size / 2);
        x1AxisCyl.setTranslateZ(-size / 2);

        y1AxisCyl.setTranslateX(-size / 2);
        y1AxisCyl.setTranslateZ(-size / 2);

        z1AxisCyl.setTranslateX((-size) / 2);
        z1AxisCyl.setTranslateY(size / 2);

        x1AxisCyl.setRotationAxis(Rotate.Z_AXIS);
        x1AxisCyl.setRotate(90);
        z1AxisCyl.setRotationAxis(Rotate.X_AXIS);
        z1AxisCyl.setRotate(-90);

        x1AxisCyl.setMaterial(redMaterial);
        y1AxisCyl.setMaterial(greenMaterial);
        z1AxisCyl.setMaterial(blueMaterial);

        //getChildren().addAll(x1AxisCyl, y1AxisCyl, z1AxisCyl);
        //XYZ 2
        final Cylinder x2AxisCyl = new Cylinder(axisThickness, size);
        final Cylinder y2AxisCyl = new Cylinder(axisThickness, size);
        final Cylinder z2AxisCyl = new Cylinder(axisThickness, size);

        x2AxisCyl.setTranslateY(size / 2);
        x2AxisCyl.setTranslateZ(size / 2);

        y2AxisCyl.setTranslateX(size / 2);
        y2AxisCyl.setTranslateZ(size / 2);

        z2AxisCyl.setTranslateX(size / 2);
        z2AxisCyl.setTranslateY(size / 2);

        x2AxisCyl.setRotationAxis(Rotate.Z_AXIS);
        x2AxisCyl.setRotate(90);
        z2AxisCyl.setRotationAxis(Rotate.X_AXIS);
        z2AxisCyl.setRotate(-90);

        x2AxisCyl.setMaterial(redMaterial);
        y2AxisCyl.setMaterial(greenMaterial);
        z2AxisCyl.setMaterial(blueMaterial);

        //getChildren().addAll(x2AxisCyl, y2AxisCyl, z2AxisCyl);
        //XYZ 3
        final Cylinder x3AxisCyl = new Cylinder(axisThickness, size);
        final Cylinder y3AxisCyl = new Cylinder(axisThickness, size);
        final Cylinder z3AxisCyl = new Cylinder(axisThickness, size);

        x3AxisCyl.setTranslateY(-size / 2);
        x3AxisCyl.setTranslateZ(-size / 2);

        y3AxisCyl.setTranslateX(size / 2);
        y3AxisCyl.setTranslateZ(-size / 2);

        z3AxisCyl.setTranslateX(size / 2);
        z3AxisCyl.setTranslateY(-size / 2);

        x3AxisCyl.setRotationAxis(Rotate.Z_AXIS);
        x3AxisCyl.setRotate(90);
        z3AxisCyl.setRotationAxis(Rotate.X_AXIS);
        z3AxisCyl.setRotate(-90);

        x3AxisCyl.setMaterial(redMaterial);
        y3AxisCyl.setMaterial(greenMaterial);
        z3AxisCyl.setMaterial(blueMaterial);

        //getChildren().addAll(x3AxisCyl, y3AxisCyl, z3AxisCyl);        
        //XYZ 4
        final Cylinder x4AxisCyl = new Cylinder(axisThickness, size);
        final Cylinder y4AxisCyl = new Cylinder(axisThickness, size);
        final Cylinder z4AxisCyl = new Cylinder(axisThickness, size);

        x4AxisCyl.setTranslateY(-size / 2);
        x4AxisCyl.setTranslateZ(size / 2);

        y4AxisCyl.setTranslateX(-size / 2);
        y4AxisCyl.setTranslateZ(size / 2);

        z4AxisCyl.setTranslateX(-size / 2);
        z4AxisCyl.setTranslateY(-size / 2);

        x4AxisCyl.setRotationAxis(Rotate.Z_AXIS);
        x4AxisCyl.setRotate(90);
        z4AxisCyl.setRotationAxis(Rotate.X_AXIS);
        z4AxisCyl.setRotate(-90);

        x4AxisCyl.setMaterial(redMaterial);
        y4AxisCyl.setMaterial(greenMaterial);
        z4AxisCyl.setMaterial(blueMaterial);

        //getChildren().addAll(x4AxisCyl, y4AxisCyl, z4AxisCyl);        
        xAxesGroup.getChildren().addAll(x1AxisCyl, x2AxisCyl, x3AxisCyl, x4AxisCyl);
        yAxesGroup.getChildren().addAll(y1AxisCyl, y2AxisCyl, y3AxisCyl, y4AxisCyl);
        zAxesGroup.getChildren().addAll(z1AxisCyl, z2AxisCyl, z3AxisCyl, z4AxisCyl);

        getChildren().addAll(xAxesGroup, yAxesGroup, zAxesGroup);

        //fuck it throw a little sphere at the corners
        final Sphere top1Sphere = new Sphere(2 * axisThickness);
        final Sphere top2Sphere = new Sphere(2 * axisThickness);
        final Sphere top3Sphere = new Sphere(2 * axisThickness);
        final Sphere top4Sphere = new Sphere(2 * axisThickness);
        final Sphere bottom1Sphere = new Sphere(2 * axisThickness);
        final Sphere bottom2Sphere = new Sphere(2 * axisThickness);
        final Sphere bottom3Sphere = new Sphere(2 * axisThickness);
        final Sphere bottom4Sphere = new Sphere(2 * axisThickness);
        top1Sphere.setTranslateX(-size / 2);
        top1Sphere.setTranslateY(-size / 2);
        top1Sphere.setTranslateZ(-size / 2);
        top2Sphere.setTranslateX(-size / 2);
        top2Sphere.setTranslateY(-size / 2);
        top2Sphere.setTranslateZ(size / 2);
        top3Sphere.setTranslateX(size / 2);
        top3Sphere.setTranslateY(-size / 2);
        top3Sphere.setTranslateZ(size / 2);
        top4Sphere.setTranslateX(size / 2);
        top4Sphere.setTranslateY(-size / 2);
        top4Sphere.setTranslateZ(-size / 2);
        bottom1Sphere.setTranslateX(-size / 2);
        bottom1Sphere.setTranslateY(size / 2);
        bottom1Sphere.setTranslateZ(-size / 2);
        bottom2Sphere.setTranslateX(-size / 2);
        bottom2Sphere.setTranslateY(size / 2);
        bottom2Sphere.setTranslateZ(size / 2);
        bottom3Sphere.setTranslateX(size / 2);
        bottom3Sphere.setTranslateY(size / 2);
        bottom3Sphere.setTranslateZ(size / 2);
        bottom4Sphere.setTranslateX(size / 2);
        bottom4Sphere.setTranslateY(size / 2);
        bottom4Sphere.setTranslateZ(-size / 2);
        getChildren().addAll(top1Sphere, top2Sphere, top3Sphere, top4Sphere);
        getChildren().addAll(bottom1Sphere, bottom2Sphere, bottom3Sphere, bottom4Sphere);
    }

    public void adjustPanelsByPos(double rx, double ry, double rz) {
        cameraRX = rx;
        cameraRY = ry;
        cameraRZ = rz;

        if (-85 < ry && ry < 85) {
            x1AxisRectangle.setVisible(false);
            xy1GridLinesGroup.setVisible(false);
            xx1GridLinesGroup.setVisible(false);
        } else {
            if(showx1AxisRectangle)
                x1AxisRectangle.setVisible(true);
            if(showxy1GridLinesGroup)
                xy1GridLinesGroup.setVisible(true);
            if(showxx1GridLinesGroup)
                xx1GridLinesGroup.setVisible(true);
        }
        if ((95 < ry && ry < 180) || (-180 < ry && ry < -95)) {
            x2AxisRectangle.setVisible(false);
            xy2GridLinesGroup.setVisible(false);
            xx2GridLinesGroup.setVisible(false);

        } else {
            if(showx1AxisRectangle)
                x2AxisRectangle.setVisible(true);
            if(showxy2GridLinesGroup)
                xy2GridLinesGroup.setVisible(true);
            if(showxx2GridLinesGroup)
                xx2GridLinesGroup.setVisible(true);
        }

        if (5 < ry && ry < 175) {
            y1AxisRectangle.setVisible(false);
            yy1GridLinesGroup.setVisible(false);
            yx1GridLinesGroup.setVisible(false);
        } else {
            if(showy1AxisRectangle)            
                y1AxisRectangle.setVisible(true);
            if(showyy1GridLinesGroup)
                yy1GridLinesGroup.setVisible(true);
            if(showyx1GridLinesGroup)
                yx1GridLinesGroup.setVisible(true);
        }
        if (-175 < ry && ry < -5) {
            y2AxisRectangle.setVisible(false);
            yy2GridLinesGroup.setVisible(false);
            yx2GridLinesGroup.setVisible(false);
        } else {
            if(showy2AxisRectangle)
                y2AxisRectangle.setVisible(true);
            if(showyy2GridLinesGroup)
                yy2GridLinesGroup.setVisible(true);
            if(showyx2GridLinesGroup)
                yx2GridLinesGroup.setVisible(true);
        }

        if (rx > 0) {
            z1AxisRectangle.setVisible(false);
            zy1GridLinesGroup.setVisible(false);
            zx1GridLinesGroup.setVisible(false);
        } else {
            if(showz1AxisRectangle)
                z1AxisRectangle.setVisible(true);
            if(showzy1GridLinesGroup)
                zy1GridLinesGroup.setVisible(true);
            if(showzx1GridLinesGroup)
                zx1GridLinesGroup.setVisible(true);
        }
        if (rx < 0) {
            z2AxisRectangle.setVisible(false);
            zy2GridLinesGroup.setVisible(false);
            zx2GridLinesGroup.setVisible(false);
        } else {
            if(showz2AxisRectangle)
                z2AxisRectangle.setVisible(true);
            if(showzy2GridLinesGroup)
                zy2GridLinesGroup.setVisible(true);
            if(showzx2GridLinesGroup)
                zx2GridLinesGroup.setVisible(true);
        }
    }

    private void buildEventHandlers() {
        xAxesGroup.setOnMouseEntered((MouseEvent t) -> {
            x1AxisRectangle.setVisible(true);
            x2AxisRectangle.setVisible(true);
            t.consume();
        });
        xAxesGroup.setOnMouseExited((MouseEvent t) -> {
            adjustPanelsByPos(cameraRX, cameraRY, cameraRZ);
            t.consume();
        });
        yAxesGroup.setOnMouseEntered((MouseEvent t) -> {
            y1AxisRectangle.setVisible(true);
            y2AxisRectangle.setVisible(true);
            t.consume();
        });
        yAxesGroup.setOnMouseExited((MouseEvent t) -> {
            adjustPanelsByPos(cameraRX, cameraRY, cameraRZ);
            t.consume();
        });
        zAxesGroup.setOnMouseEntered((MouseEvent t) -> {
            z1AxisRectangle.setVisible(true);
            z2AxisRectangle.setVisible(true);
            t.consume();
        });
        zAxesGroup.setOnMouseExited((MouseEvent t) -> {
            adjustPanelsByPos(cameraRX, cameraRY, cameraRZ);
            t.consume();
        });
    }

    public void showAll(boolean visible) {
        showXAxesGroup(visible);
        showYAxesGroup(visible);
        showZAxesGroup(visible);
        showX1Panel(visible);
        showX2Panel(visible);
        showY1Panel(visible);
        showY2Panel(visible);
        showZ1Panel(visible);
        showZ2Panel(visible);
        showXY1GridLinesGroup(visible);
        showXX1GridLinesGroup(visible);
        showYY1GridLinesGroup(visible);
        showYX1GridLinesGroup(visible);
        showZY1GridLinesGroup(visible);
        showZX1GridLinesGroup(visible);      
        showXY2GridLinesGroup(visible);
        showXX2GridLinesGroup(visible);
        showYY2GridLinesGroup(visible);
        showYX2GridLinesGroup(visible);
        showZY2GridLinesGroup(visible);
        showZX2GridLinesGroup(visible);      
    }
    
    public void showX1Panel(boolean visible) {
        showx1AxisRectangle = visible;
        x1AxisRectangle.setVisible(visible);
    }
    public void showX2Panel(boolean visible) {
        showx2AxisRectangle = visible;
        x2AxisRectangle.setVisible(visible);
    }
    public void showY1Panel(boolean visible) {
        showy1AxisRectangle = visible;
        y1AxisRectangle.setVisible(visible);
    }
    public void showY2Panel(boolean visible) {
        showy2AxisRectangle = visible;
        y2AxisRectangle.setVisible(visible);
    }
    public void showZ1Panel(boolean visible) {
        showz1AxisRectangle = visible;
        z1AxisRectangle.setVisible(visible);
    }
    public void showZ2Panel(boolean visible) {
        showz2AxisRectangle = visible;
        z2AxisRectangle.setVisible(visible);
    }
    
    public void showXAxesGroup(boolean visible) {
        showxAxesGroup = visible;
        xAxesGroup.setVisible(visible);
    }
    public void showYAxesGroup(boolean visible) {
        showyAxesGroup = visible;
        yAxesGroup.setVisible(visible);
    }
    public void showZAxesGroup(boolean visible) {
        showzAxesGroup = visible;
        zAxesGroup.setVisible(visible);
    }
    public void showXY1GridLinesGroup(boolean visible) {
        showxy1GridLinesGroup = visible;        
        xy1GridLinesGroup.setVisible(visible);
    }
    public void showXX1GridLinesGroup(boolean visible) {
        showxx1GridLinesGroup = visible;        
        xx1GridLinesGroup.setVisible(visible);
    }    
    public void showYY1GridLinesGroup(boolean visible) {
        showyy1GridLinesGroup = visible;        
        yy1GridLinesGroup.setVisible(visible);
    }
    public void showYX1GridLinesGroup(boolean visible) {
        showyx1GridLinesGroup = visible;        
        yx1GridLinesGroup.setVisible(visible);
    }    
    public void showZY1GridLinesGroup(boolean visible) {
        showzy1GridLinesGroup = visible;        
        zy1GridLinesGroup.setVisible(visible);
    }
    public void showZX1GridLinesGroup(boolean visible) {
        showzx1GridLinesGroup = visible;        
        zx1GridLinesGroup.setVisible(visible);
    }    
    public void showXY2GridLinesGroup(boolean visible) {
        showxy2GridLinesGroup = visible;        
        xy2GridLinesGroup.setVisible(visible);
    }
    public void showXX2GridLinesGroup(boolean visible) {
        showxx2GridLinesGroup = visible;        
        xx2GridLinesGroup.setVisible(visible);
    }    
    public void showYY2GridLinesGroup(boolean visible) {
        showyy2GridLinesGroup = visible;        
        yy2GridLinesGroup.setVisible(visible);
    }
    public void showYX2GridLinesGroup(boolean visible) {
        showyx2GridLinesGroup = visible;        
        yx2GridLinesGroup.setVisible(visible);
    }    
    public void showZY2GridLinesGroup(boolean visible) {
        showzy2GridLinesGroup = visible;        
        zy2GridLinesGroup.setVisible(visible);
    }
    public void showZX2GridLinesGroup(boolean visible) {
        showzx2GridLinesGroup = visible;        
        zx2GridLinesGroup.setVisible(visible);
    }    

    /**
     * @return the selfLightEnabled
     */
    public boolean isSelfLightEnabled() {
        return selfLightEnabled;
    }

    /**
     * @param selfLightEnabled the selfLightEnabled to set
     */
    public void setSelfLightEnabled(boolean selfLightEnabled) {
        this.selfLightEnabled = selfLightEnabled;
    }
}