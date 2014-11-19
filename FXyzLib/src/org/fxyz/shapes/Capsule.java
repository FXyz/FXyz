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
