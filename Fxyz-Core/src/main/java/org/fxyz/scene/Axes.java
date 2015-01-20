/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.scene;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 *
 * @author jpereda
 */
public class Axes extends Group {
    
    public Axes() {
        this(1);
    }
    
    public Axes(double scale) {
        Cylinder axisX = new Cylinder(3, 60);
        axisX.getTransforms().addAll(new Rotate(90, Rotate.Z_AXIS), new Translate(0, 30, 0));
        axisX.setMaterial(new PhongMaterial(Color.RED));
        Cylinder axisY = new Cylinder(3, 60);
        axisY.getTransforms().add(new Translate(0, 30, 0));
        axisY.setMaterial(new PhongMaterial(Color.GREEN));
        Cylinder axisZ = new Cylinder(3, 60);
        axisZ.setMaterial(new PhongMaterial(Color.BLUE));
        axisZ.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Translate(0, 30, 0));
        getChildren().addAll(axisX, axisY, axisZ);
        getTransforms().add(new Scale(scale, scale, scale));
    }
}