/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.shapes;

import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import org.fxyz.shapes.containers.ShapeContainer;
import org.fxyz.shapes.primitives.CapsuleMesh;

/**
 *
 * @author Dub
 */
public class Capsule extends ShapeContainer<CapsuleMesh>{
    private CapsuleMesh mesh;
    
    public Capsule() {
        super(new CapsuleMesh());
        this.mesh = getShape();
    }
    
    public Capsule(double radius, double height){
        this();
        mesh.setRadius(radius);
        mesh.setHeight(height);        
    }
    
    public Capsule(Color c){
        this();
        this.setDiffuseColor(c);
    }
    
    public Capsule(double radius, double height, Color c){
        this(radius, height);
        this.setDiffuseColor(c);
    }

    public final void setRadius(double value) {
        mesh.setRadius(value);
    }

    public final void setHeight(double value) {
        mesh.setHeight(value);
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
    
}
