/**
* Spheroid.java
*
* Copyright (c) 2013-2015, F(X)yz
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* * Neither the name of the organization nor the
* names of its contributors may be used to endorse or promote products
* derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.fxyz.shapes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import org.fxyz.shapes.containers.ShapeContainer;
import org.fxyz.shapes.primitives.SpheroidMesh;

/**
 *
 * @author jdub1581
 */
public class Spheroid extends ShapeContainer<SpheroidMesh> {
    private SpheroidMesh mesh;
    public Spheroid() {
        super(new SpheroidMesh());
        mesh = getShape();        
    }

    public Spheroid(double radius) {
        this();
        mesh.setMinorRadius( radius);
        mesh.setMajorRadius( radius);
    }

    public Spheroid(double minorRadius, double majorRadius) {
        this();
        mesh.setMinorRadius(minorRadius);
        mesh.setMajorRadius(majorRadius);
    }
    
    public Spheroid(int divisions, double minorRadius, double majorRadius) {
        this();
        mesh.setDivisions(divisions);
        mesh.setMinorRadius(minorRadius);
        mesh.setMajorRadius(majorRadius);
    }
    
    public Spheroid(Color c) {
        this();
        this.setDiffuseColor(c);
    }

    public Spheroid(double radius, Color c) {
        this(radius);
        this.setDiffuseColor(c);
    }

    public Spheroid(double minorRadius, double majorRadius, Color c) {
        this(minorRadius, majorRadius);
        this.setDiffuseColor(c);
    }
    
    public Spheroid(int divisions, double minorRadius, double majorRadius, Color c) {
        this(divisions, minorRadius, majorRadius);
        this.setDiffuseColor(c);
    }

    public final void setMajorRadius(double value) {
        mesh.setMajorRadius(value);
    }

    public final void setMinorRadius(double value) {
        mesh.setMinorRadius(value);
    }

    public final void setDivisions(int value) {
        mesh.setDivisions(value);
    }

    public final void setDrawMode(DrawMode value) {
        mesh.setDrawMode(value);
    }

    public final void setCullFace(CullFace value) {
        mesh.setCullFace(value);
    }

    public boolean isSphere() {
        return mesh.isSphere();
    }

    public boolean isOblateSpheroid() {
        return mesh.isOblateSpheroid();
    }

    public boolean isProlateSpheroid() {
        return mesh.isProlateSpheroid();
    }

    public final double getMajorRadius() {
        return mesh.getMajorRadius();
    }

    public DoubleProperty majorRadiusProperty() {
        return mesh.majorRadiusProperty();
    }

    public final double getMinorRadius() {
        return mesh.getMinorRadius();
    }

    public DoubleProperty minorRadiusProperty() {
        return mesh.minorRadiusProperty();
    }

    public final int getDivisions() {
        return mesh.getDivisions();
    }

    public IntegerProperty divisionsProperty() {
        return mesh.divisionsProperty();
    }
    
    
    
}
