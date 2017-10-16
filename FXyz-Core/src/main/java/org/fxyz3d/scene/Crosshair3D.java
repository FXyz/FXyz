/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz3d.scene;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.shapes.composites.PolyLine3D;

/**
 *
 * @author sphillips
 */
public class Crosshair3D extends Group {
    
    public double size;
    public Point3D centerPoint;
    public int lineWidth;
    public PolyLine3D xPositivePoly;
    public PolyLine3D xNegativePoly;
    public PolyLine3D yPositivePoly;
    public PolyLine3D yNegativePoly;
    public PolyLine3D zPositivePoly;
    public PolyLine3D zNegativePoly;
    public Color xPositiveColor = Color.ALICEBLUE;
    public Color xNegativeColor = Color.ALICEBLUE;
    public Color yPositiveColor = Color.ALICEBLUE;
    public Color yNegativeColor = Color.ALICEBLUE;
    public Color zPositiveColor = Color.ALICEBLUE;
    public Color zNegativeColor = Color.ALICEBLUE;
    
    public Crosshair3D(Point3D centerPoint, double size, int lineWidth) {
        this.centerPoint = centerPoint;
        this.size = size;
        this.lineWidth = lineWidth;

        setCenter(centerPoint);
    }
    
    public void setCenter(Point3D newCenter) {
        this.centerPoint = newCenter;
        //remove the current polylines so they detach and get GC'd
        getChildren().clear();
        //create set of polylines that create a 3D crosshair
        float half = new Float(size/2.0);
        float startPointX = centerPoint.x;
        float startPointY = centerPoint.y;
        float startPointZ = centerPoint.z;

        //x Axis - Positive direction from centerPoint
        List<Point3D> xPositiveData = new ArrayList<>();
        xPositiveData.add(new Point3D(startPointX, startPointY, startPointZ));
        xPositiveData.add(new Point3D(half, startPointY, startPointZ));
        xPositivePoly = new PolyLine3D(xPositiveData, lineWidth, xPositiveColor, PolyLine3D.LineType.TRIANGLE);
        //x Axis - Negative direction from centerPoint
        List<Point3D> xNegativeData = new ArrayList<>();
        xNegativeData.add(new Point3D(startPointX, startPointY, startPointZ));
        xNegativeData.add(new Point3D(-half, startPointY, startPointZ));
        xNegativePoly = new PolyLine3D(xNegativeData, lineWidth, xNegativeColor, PolyLine3D.LineType.TRIANGLE);

        //y Axis - Positive direction from centerPoint
        List<Point3D> yPositiveData = new ArrayList<>();
        yPositiveData.add(new Point3D(startPointX, startPointY, startPointZ));
        yPositiveData.add(new Point3D(startPointX, half, startPointZ));
        yPositivePoly = new PolyLine3D(yPositiveData, lineWidth, yPositiveColor, PolyLine3D.LineType.TRIANGLE);
        //y Axis - Negative direction from centerPoint
        List<Point3D> yNegativeData = new ArrayList<>();
        yNegativeData.add(new Point3D(startPointX, startPointY, startPointZ));
        yNegativeData.add(new Point3D(startPointX, -half, startPointZ));
        yNegativePoly = new PolyLine3D(yNegativeData, lineWidth, yNegativeColor, PolyLine3D.LineType.TRIANGLE);

        //z Axis - Positive direction from centerPoint
        List<Point3D> zPositiveData = new ArrayList<>();
        zPositiveData.add(new Point3D(startPointX, startPointY, startPointZ));
        zPositiveData.add(new Point3D(startPointX, startPointY, half));
        zPositivePoly = new PolyLine3D(zPositiveData, lineWidth, zPositiveColor, PolyLine3D.LineType.TRIANGLE);
        //z Axis - Negative direction from centerPoint
        List<Point3D> zNegativeData = new ArrayList<>();
        zNegativeData.add(new Point3D(startPointX, startPointY, startPointZ));
        zNegativeData.add(new Point3D(startPointX, startPointY, -half));
        zNegativePoly = new PolyLine3D(zNegativeData, lineWidth, zNegativeColor, PolyLine3D.LineType.TRIANGLE);        
        getChildren().addAll(
            xPositivePoly, xNegativePoly, 
            yPositivePoly, yNegativePoly, 
            zPositivePoly, zNegativePoly);
    }
}
