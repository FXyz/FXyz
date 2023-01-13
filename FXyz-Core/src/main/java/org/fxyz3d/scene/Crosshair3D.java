/**
 * Crosshair3D.java
 *
 * Copyright (c) 2013-2017, F(X)yz
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
    /**
     * Recreates and sets the location of the 3D crosshair with the origin 
     * at the newCenter. If the crosshair must be moved frequently it may be
     * more efficient to update the translate of the group itself.
     * @param newCenter origin for new Crosshair 
     */
    public void setCenter(Point3D newCenter) {
        this.centerPoint = newCenter;
        //remove the current polylines so they detach and get GC'd
        getChildren().clear();
        //create set of polylines that create a 3D crosshair
        float half = Double.valueOf(size/2.0f).floatValue();
        float startPointX = centerPoint.x;
        float startPointY = centerPoint.y;
        float startPointZ = centerPoint.z;

        //x Axis - Positive direction from centerPoint
        List<Point3D> xPositiveData = new ArrayList<>();
        xPositiveData.add(new Point3D(startPointX, startPointY, startPointZ));
        xPositiveData.add(new Point3D(half, startPointY, startPointZ));
        xPositivePoly = new PolyLine3D(xPositiveData, Float.valueOf(lineWidth), xPositiveColor, PolyLine3D.LineType.TRIANGLE);
        //x Axis - Negative direction from centerPoint
        List<Point3D> xNegativeData = new ArrayList<>();
        xNegativeData.add(new Point3D(startPointX, startPointY, startPointZ));
        xNegativeData.add(new Point3D(-half, startPointY, startPointZ));
        xNegativePoly = new PolyLine3D(xNegativeData, Float.valueOf(lineWidth), xNegativeColor, PolyLine3D.LineType.TRIANGLE);

        //y Axis - Positive direction from centerPoint
        List<Point3D> yPositiveData = new ArrayList<>();
        yPositiveData.add(new Point3D(startPointX, startPointY, startPointZ));
        yPositiveData.add(new Point3D(startPointX, half, startPointZ));
        yPositivePoly = new PolyLine3D(yPositiveData, Float.valueOf(lineWidth), yPositiveColor, PolyLine3D.LineType.TRIANGLE);
        //y Axis - Negative direction from centerPoint
        List<Point3D> yNegativeData = new ArrayList<>();
        yNegativeData.add(new Point3D(startPointX, startPointY, startPointZ));
        yNegativeData.add(new Point3D(startPointX, -half, startPointZ));
        yNegativePoly = new PolyLine3D(yNegativeData, Float.valueOf(lineWidth), yNegativeColor, PolyLine3D.LineType.TRIANGLE);

        //z Axis - Positive direction from centerPoint
        List<Point3D> zPositiveData = new ArrayList<>();
        zPositiveData.add(new Point3D(startPointX, startPointY, startPointZ));
        zPositiveData.add(new Point3D(startPointX, startPointY, half));
        zPositivePoly = new PolyLine3D(zPositiveData, Float.valueOf(lineWidth), zPositiveColor, PolyLine3D.LineType.TRIANGLE);
        //z Axis - Negative direction from centerPoint
        List<Point3D> zNegativeData = new ArrayList<>();
        zNegativeData.add(new Point3D(startPointX, startPointY, startPointZ));
        zNegativeData.add(new Point3D(startPointX, startPointY, -half));
        zNegativePoly = new PolyLine3D(zNegativeData, Float.valueOf(lineWidth), zNegativeColor, PolyLine3D.LineType.TRIANGLE);        
        getChildren().addAll(
            xPositivePoly, xNegativePoly, 
            yPositivePoly, yNegativePoly, 
            zPositivePoly, zNegativePoly);
    }
}
