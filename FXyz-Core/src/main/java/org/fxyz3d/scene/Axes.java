/**
 * Axes.java
 *
 * Copyright (c) 2013-2019, F(X)yz
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

    private final Cylinder axisX;
    private final Cylinder axisY;
    private final Cylinder axisZ;

    public Axes() {
        this(1);
    }

    public Axes(double scale) {
        axisX = new Cylinder(3, 60);
        axisY = new Cylinder(3, 60);
        axisZ = new Cylinder(3, 60);
        axisX.getTransforms().addAll(new Rotate(-90, Rotate.Z_AXIS), new Translate(0, 30, 0));
        axisX.setMaterial(new PhongMaterial(Color.RED));
        axisY.getTransforms().add(new Translate(0, 30, 0));
        axisY.setMaterial(new PhongMaterial(Color.GREEN));
        axisZ.setMaterial(new PhongMaterial(Color.BLUE));
        axisZ.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Translate(0, 30, 0));
        getChildren().addAll(axisX, axisY, axisZ);
        getTransforms().add(new Scale(scale, scale, scale));
    }

    public void setHeight(double equalHeights) {
        double oldHeight = axisX.getHeight();
        axisX.setHeight(equalHeights);
        axisX.getTransforms().add(new Translate(0, (equalHeights/2.0)-(oldHeight/2.0), 0));
        axisY.setHeight(equalHeights);
        axisY.getTransforms().add(new Translate(0, (equalHeights/2.0)-(oldHeight/2.0), 0));
        axisZ.setHeight(equalHeights);
        axisZ.getTransforms().add(new Translate(0,(equalHeights/2.0)-(oldHeight/2.0), 0));
    }

    public void setRadius(double equalRadius) {
        axisX.setRadius(equalRadius);
        axisY.setRadius(equalRadius);
        axisZ.setRadius(equalRadius);
    }
}