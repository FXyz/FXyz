/**
 * ScatterPlot.java
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

package org.fxyz3d.shapes.composites;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import org.fxyz3d.collections.ListOfOne;

/**
 *
 * @author Sean
 * Provides a simple but configurable Group object whose children
 * are each nodes rendered in 3D space.  The data structure allows for each
 * of the three axes to be independently changed there by affording the user to
 * create variations on the view dynamically.
 */
public class ScatterPlot extends Group {
    
    private List<Double> xAxisData = new ArrayList<>();
    private List<Double> yAxisData = new ArrayList<>();
    private List<Double> zAxisData = new ArrayList<>();  
    private List<Color> colorMap = new ArrayList<>();
    //A sub group for holding the nodes that represent the data in 3D
    public Group scatterDataGroup = new Group();    
//    public PointLight selfLight = new PointLight(Color.WHITE);
    public AmbientLight selfLight = new AmbientLight(Color.WHITE);
    
    public double nodeRadius = 1;    
    private double axesSize = 1000;
    private boolean normalized = false;
    public boolean selfLightEnabled = true;
    
    public enum NodeType {SPHERE, CUBE, PYRAMID, STAR};
    
    private NodeType defaultNodeType = NodeType.SPHERE;
    
    public ScatterPlot(boolean selfLit) {
        selfLightEnabled = selfLit;
        init();
    }
    public ScatterPlot(double axesSize, double nodeRadius, boolean selfLit) {
        selfLightEnabled = selfLit;
        this.axesSize = axesSize;
        this.nodeRadius = nodeRadius;
        init();
    }    
    private void init(){
        getChildren().add(scatterDataGroup); //Holds ScatterPlot data        
        if(selfLightEnabled) {
            getChildren().add(selfLight);
        }
        setDepthTest(DepthTest.ENABLE);        
    }   
    public void setXYZData(List<Double> xData, List<Double> yData, List<Double> zData) {
        setXYZData(xData, yData, zData, new ListOfOne<>(Color.WHITE, xData.size()));
    }    
    
    public void setXYZData(List<Double> xData, List<Double> yData, List<Double> zData, List<Color> colors) {
        xAxisData = xData;
        yAxisData = yData;
        zAxisData = zData;
        scatterDataGroup.getChildren().clear();
        //for now we will always default to x axis
        //later we could maybe dynamically determine the smallest axis and then
        //uses 0's for the other axes that are larger.
        for(int i=0;i<xAxisData.size();i++) {
            final Shape3D dataSphere = createDefaultNode(nodeRadius);
            double translateY = 0.0;
            double translateZ = 0.0;            
            if(!yAxisData.isEmpty() && yAxisData.size() > i)
                translateY = yAxisData.get(i);
            if(!zAxisData.isEmpty() && zAxisData.size() > i)
                translateZ = zAxisData.get(i);
            dataSphere.setTranslateX(xAxisData.get(i));
            dataSphere.setTranslateY(translateY);
            dataSphere.setTranslateZ(translateZ);
            dataSphere.setMaterial(new PhongMaterial(colors.get(i)));
            scatterDataGroup.getChildren().add(dataSphere);
        }        
    }   
    
     /**
     * @return the xAxisData
     */
    public List<Double> getxAxisData() {
        return xAxisData;
    }

    /**
     * @param data the xAxisData to set
     */
    public void setxAxisData(List<Double> data) {
        xAxisData = data;
        scatterDataGroup.getChildren().clear();
        for(int i=0;i<xAxisData.size();i++) {
//            final Sphere dataSphere = new Sphere(scatterRadius);
//            final Box dataSphere = new Box(scatterRadius, scatterRadius, scatterRadius);
            final Node dataSphere = createDefaultNode(nodeRadius);
            double translateY = 0.0;
            double translateZ = 0.0;
            if(!yAxisData.isEmpty() && yAxisData.size() > i)
                translateY = yAxisData.get(i);
            if(!zAxisData.isEmpty() && zAxisData.size() > i)
                translateZ = zAxisData.get(i);
            dataSphere.setTranslateX(xAxisData.get(i));
            dataSphere.setTranslateY(translateY);
            dataSphere.setTranslateZ(translateZ);
            scatterDataGroup.getChildren().add(dataSphere);
        }        
    }

    /**
     * @return the yAxisData
     */
    public List<Double> getyAxisData() {
        return yAxisData;
    }

    /**
     * @param data the yAxisData to set
     */
    public void setyAxisData(List<Double> data) {
        yAxisData = data;
        scatterDataGroup.getChildren().clear();
        for(int i=0;i<yAxisData.size();i++) {
//            final Sphere dataSphere = new Sphere(scatterRadius);
//            final Box dataSphere = new Box(scatterRadius, scatterRadius, scatterRadius);
            final Node dataSphere = createDefaultNode(nodeRadius);
            double translateX = 0.0;
            double translateZ = 0.0;
            if(!xAxisData.isEmpty() && xAxisData.size() > i)
                translateX = xAxisData.get(i);
            if(!zAxisData.isEmpty() && zAxisData.size() > i)
                translateZ = zAxisData.get(i);
            dataSphere.setTranslateX(translateX);
            dataSphere.setTranslateY(yAxisData.get(i));
            dataSphere.setTranslateZ(translateZ);
            scatterDataGroup.getChildren().add(dataSphere);
        }        
    }

    /**
     * @return the zAxisData
     */
    public List<Double> getzAxisData() {
        return zAxisData;
    }

    /**
     * @param data the zAxisData to set
     */
    public void setzAxisData(List<Double> data) {
        zAxisData = data;
        scatterDataGroup.getChildren().clear();
        for(int i=0;i<zAxisData.size();i++) {
//            final Sphere dataSphere = new Sphere(scatterRadius);
//            final Box dataSphere = new Box(scatterRadius, scatterRadius, scatterRadius);
            final Node dataSphere = createDefaultNode(nodeRadius);
            double translateX = 0.0;
            double translateY = 0.0;
            if(!xAxisData.isEmpty() && xAxisData.size() > i)
                translateX = xAxisData.get(i);
            if(!yAxisData.isEmpty() && yAxisData.size() > i)
                translateY = yAxisData.get(i);
            dataSphere.setTranslateX(translateX);
            dataSphere.setTranslateY(translateY);
            dataSphere.setTranslateZ(zAxisData.get(i));
            scatterDataGroup.getChildren().add(dataSphere);
        }       
    }
    private Shape3D createDefaultNode(double radius) {
        switch(defaultNodeType) {
            case SPHERE: return new Sphere(radius);
            case CUBE: return new Box(radius, radius, radius);
            default: return new Box(radius, radius, radius);    
        }
    }
    
    /**
     * @return the defaultNodeType
     */
    public NodeType getDefaultNodeType() {
        return defaultNodeType;
    }

    /**
     * @param defaultNodeType the defaultNodeType to set
     */
    public void setDefaultNodeType(NodeType defaultNodeType) {
        this.defaultNodeType = defaultNodeType;
    }    
}
