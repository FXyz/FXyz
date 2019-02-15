/**
 * JointChain.java
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
package org.fxyz3d.utils.geom;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.fxyz3d.shapes.primitives.FrustumMesh;

/**
 * A node that can be used as a visible link between two joints
 *
 * @author Jose Pereda
 */
public class JointChain extends Group {

    private final Box origin;
    private final FrustumMesh bone;
    private final Sphere end;
    private final double scale;

    public JointChain(Joint joint, double scale) {
        if (scale == 0) {
            scale = 1;
        }
        this.scale = scale;

        origin = new Box(16, 16, 16);
        origin.setMaterial(new PhongMaterial(getColor()));

        bone = new FrustumMesh(6, 3, 10, 0);
        bone.setTextureModeNone(getColor());
        
        end = new Sphere(5);
        end.setMaterial(new PhongMaterial(getColor()));
        
        getChildren().addAll(origin, bone, end);
        getTransforms().add(new Scale(scale, scale, scale));

        joint.localToParentTransformProperty().addListener((obs, ov, nv) -> updateChain(joint));
        updateChain(joint);
    }

    private void updateChain(Joint joint) {
        Point3D scaled = getJointLocation(joint).multiply(1d / scale);
        origin.getTransforms().setAll(new Translate(scaled.getX(), scaled.getY(), scaled.getZ()));
        bone.setAxisOrigin(org.fxyz3d.geometry.Point3D.convertFromJavaFXPoint3D(scaled));
    }

    private Point3D getJointLocation(Joint joint) {
        try {
            Affine a = new Affine();
            joint.getTransforms().forEach(a::append);
            return a.inverseTransform(Point3D.ZERO);
        } catch (NonInvertibleTransformException e) { }
        return Point3D.ZERO;
    }

    // drawMode
    private final ObjectProperty<DrawMode> drawMode = new SimpleObjectProperty<>(this, "drawMode", DrawMode.FILL) {
        @Override
        protected void invalidated() {
            getChildren().stream()
                .filter(Shape3D.class::isInstance)
                .map(Shape3D.class::cast)
                .forEach(n -> n.setDrawMode(get()));
        }
    };
    public final ObjectProperty<DrawMode> drawModeProperty() {
       return drawMode;
    }
    public final DrawMode getDrawMode() {
       return drawMode.get();
    }
    public final void setDrawMode(DrawMode value) {
        drawMode.set(value);
    }

    // color
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(this, "color", Color.DARKBLUE) {
        @Override
        protected void invalidated() {
            getChildren().stream()
                    .filter(Shape3D.class::isInstance)
                    .map(Shape3D.class::cast)
                    .map(s -> (PhongMaterial) s.getMaterial())
                    .forEach(m -> m.setDiffuseColor(get()));
        }
    };
    public final ObjectProperty<Color> colorProperty() {
       return color;
    }
    public final Color getColor() {
       return color.get();
    }
    public final void setColor(Color value) {
        color.set(value);
    }

}
