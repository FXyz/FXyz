/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.shapes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import org.fxyz.shapes.containers.ShapeContainer;
import org.fxyz.shapes.primitives.ConeMesh;

/**
 *
 * @author Birdasaur
 * @adapted Dub's Capsule class served as an example framework to adapt 
 * my existing Cone code.
 */
public class Cone extends ShapeContainer<ConeMesh>{
     private ConeMesh mesh;
    
    public Cone() {
        super(new ConeMesh());
        this.mesh = getShape();
    }
    public Cone(int divisions, double radius, double height){
        this();
        mesh.setDivisions(divisions);
        mesh.setRadius(radius);
        mesh.setHeight(height);        
    }
    public Cone(Color c){
        this();
        this.setDiffuseColor(c);
    }
    public Cone(int divisions, double radius, double height, Color c){
        //this(divisions, radius, height);
        super(new ConeMesh(divisions, radius, height));
        this.mesh = getShape();
        this.setDiffuseColor(c);
    }
    public final void setRadius(double value) {
        mesh.setRadius(value);
    }
    public final void setHeight(double value) {
        mesh.setHeight(value);
    }
    public final void setDivisions(int value) {
        mesh.setDivisions(value);
    }
    public final void setMaterial(Material value) {
        mesh.setMaterial(value);
    }
    public final void setDrawMode(DrawMode value) {
        mesh.setDrawMode(value);
    }
    public final void setCullFace(CullFace value) {
        mesh.setCullFace(value);
    }
    public final double getRadius() {
        return mesh.getRadius();
    }
    public DoubleProperty radiusProperty() {
        return mesh.radiusProperty();
    }
    public final double getHeight() {
        return mesh.getHeight();
    }
    public DoubleProperty heightProperty() {
        return mesh.heightProperty();
    }
    public final int getDivisions() {
        return mesh.getDivisions();
    }
    public IntegerProperty divisionsProperty() {
        return mesh.divisionsProperty();
    }       
}