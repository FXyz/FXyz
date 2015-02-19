/**
* BezierMeshes.java
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

package org.fxyz.samples.shapes.texturedmeshes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static javafx.application.Application.launch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.geometry.Point3D;
import org.fxyz.samples.shapes.GroupOfTexturedMeshSample;
import org.fxyz.shapes.primitives.BezierMesh;
import org.fxyz.shapes.primitives.PrismMesh;
import org.fxyz.shapes.primitives.helper.BezierHelper;
import org.fxyz.shapes.primitives.helper.InterpolateBezier;

/**
 *
 * @author jpereda
 */
public class BezierMeshes extends GroupOfTexturedMeshSample {

    public static void main(String[] args) {
        launch(args);
    }

    private final BooleanProperty showKnots = new SimpleBooleanProperty(this, "Show Knots");
    private final BooleanProperty showControlPoints = new SimpleBooleanProperty(this, "Show Control Points");

    private final DoubleProperty wireRad = new SimpleDoubleProperty(this, "Wire Radius",0.1);
    private final IntegerProperty rDivs = new SimpleIntegerProperty(this, "Radius Divisions", 10);
    private final IntegerProperty tDivs = new SimpleIntegerProperty(this, "Length Divisions", 200);
    private final IntegerProperty lengthCrop = new SimpleIntegerProperty(this, "Length Crop", 0);
    private final IntegerProperty wireCrop = new SimpleIntegerProperty(this, "Wire Crop", 0);

    private List<BezierMesh> beziers;
    private List<BezierHelper> splines;

    @Override
    protected void createMesh() {
        System.err.println(showKnots.getClass().getSimpleName());
        model = new Group();
        List<Point3D> knots = Arrays.asList(new Point3D(3f, 0f, 0f), new Point3D(0.77171f, 1.68981f, 0.989821f),
                new Point3D(-0.681387f, 0.786363f, -0.281733f), new Point3D(-2.31757f, -0.680501f, -0.909632f),
                new Point3D(-0.404353f, -2.81233f, 0.540641f), new Point3D(1.1316f, -0.727237f, 0.75575f),
                new Point3D(1.1316f, 0.727237f, -0.75575f), new Point3D(-0.404353f, 2.81233f, -0.540641f),
                new Point3D(-2.31757f, 0.680501f, 0.909632f), new Point3D(-0.681387f, -0.786363f, 0.281733f),
                new Point3D(0.77171f, -1.68981f, -0.989821f), new Point3D(3f, 0f, 0f));
        InterpolateBezier interpolate = new InterpolateBezier(knots);
        splines = interpolate.getSplines();
        beziers = splines.parallelStream().map(spline -> {
            BezierMesh bezier = new BezierMesh(spline, wireRad.get(), tDivs.get(), rDivs.get(), lengthCrop.get(), wireCrop.get());
            bezier.setTextureModeNone(Color.ROYALBLUE);
            return bezier;
        }).collect(Collectors.toList());
    }

    @Override
    protected void addMeshAndListeners() {
        model.getChildren().addAll(beziers);
        beziers.forEach(bezier -> {
            bezier.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);
        });
        wireRad.addListener(i -> beziers.forEach(bm -> bm.setWireRadius(wireRad.doubleValue())));
        tDivs.addListener(i -> beziers.forEach(bm -> bm.setLengthDivisions(tDivs.intValue())));
        rDivs.addListener(i -> beziers.forEach(bm -> bm.setWireDivisions(rDivs.intValue())));
        lengthCrop.addListener(i -> beziers.forEach(bm -> bm.setLengthCrop((int)Math.min(lengthCrop.intValue(),tDivs.intValue()/2))));
        wireCrop.addListener(i -> beziers.forEach(bm -> bm.setWireCrop((int)Math.min(wireCrop.intValue(),rDivs.intValue()/2))));
        
        showKnots.addListener((obs, b, b1) -> splines.forEach(spline -> {
            if (showKnots.get()) {
                Point3D k0 = spline.getPoints().get(0);
                Point3D k3 = spline.getPoints().get(3);
                Sphere s = new Sphere(0.2d);
                s.setId("knot");
                s.getTransforms().add(new Translate(k0.x, k0.y, k0.z));
                s.setMaterial(new PhongMaterial(Color.GREENYELLOW));
                group.getChildren().add(s);
                s = new Sphere(0.2d);
                s.setId("knot");
                s.getTransforms().add(new Translate(k3.x, k3.y, k3.z));
                s.setMaterial(new PhongMaterial(Color.GREENYELLOW));
                group.getChildren().add(s);
            } else {
                group.getChildren().removeIf(s -> s.getId() != null && s.getId().equals("knot"));
            }
        }));
        showControlPoints.addListener((obs, b, b1) -> splines.forEach(spline -> {
            if (showControlPoints.get()) {
                Point3D k0 = spline.getPoints().get(0);
                Point3D k1 = spline.getPoints().get(1);
                Point3D k2 = spline.getPoints().get(2);
                Point3D k3 = spline.getPoints().get(3);
                PrismMesh c = new PrismMesh(0.03d, 1d, 1, k0, k1);
                c.setTextureModeNone(Color.GREEN);
                c.setId("Control");
                group.getChildren().add(c);

                c = new PrismMesh(0.03d, 1d, 1, k1, k2);
                c.setTextureModeNone(Color.GREEN);
                c.setId("Control");
                group.getChildren().add(c);

                c = new PrismMesh(0.03d, 1d, 1, k2, k3);
                c.setTextureModeNone(Color.GREEN);
                c.setId("Control");
                group.getChildren().add(c);

                Sphere s = new Sphere(0.1d);
                s.getTransforms().add(new Translate(k1.x, k1.y, k1.z));
                s.setMaterial(new PhongMaterial(Color.RED));
                s.setId("Control");
                group.getChildren().add(s);
                s = new Sphere(0.1d);
                s.getTransforms().add(new Translate(k2.x, k2.y, k2.z));
                s.setMaterial(new PhongMaterial(Color.RED));
                s.setId("Control");
                group.getChildren().add(s);
            } else {
                group.getChildren().removeIf(s -> s.getId() != null && s.getId().equals("Control"));
            }
        }));
    }

    @Override
    public String getSampleDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nBezierMesh:\nAllows for a Tubular mesh to be built using a BezierCurve method ")
                .append("allowing the use of control points in 3D space.");
        return sb.toString();
    }

    @Override
    protected Node buildControlPanel() {
        NumberSliderControl radSlider = ControlFactory.buildNumberSlider(wireRad, 0.1D, 0.5D);
        radSlider.getSlider().setMinorTickCount(4);
        radSlider.getSlider().setMajorTickUnit(0.5);
        radSlider.getSlider().setBlockIncrement(0.1d);
        radSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl rDivSlider = ControlFactory.buildNumberSlider(this.rDivs, 2, 100);
        rDivSlider.getSlider().setMinorTickCount(25);
        rDivSlider.getSlider().setMajorTickUnit(99);
        rDivSlider.getSlider().setBlockIncrement(1);
        rDivSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl rCropSlider = ControlFactory.buildNumberSlider(this.wireCrop, 0, 98);
        rCropSlider.getSlider().setMinorTickCount(48);
        rCropSlider.getSlider().setMajorTickUnit(49);
        rCropSlider.getSlider().setBlockIncrement(1);
        rCropSlider.getSlider().setSnapToTicks(true);

        NumberSliderControl tDivSlider = ControlFactory.buildNumberSlider(this.tDivs, 4l, 250);
        tDivSlider.getSlider().setMinorTickCount(50);
        tDivSlider.getSlider().setMajorTickUnit(250);
        tDivSlider.getSlider().setBlockIncrement(1);

        NumberSliderControl lCropSlider = ControlFactory.buildNumberSlider(this.lengthCrop, 0l, 200);
        lCropSlider.getSlider().setMinorTickCount(0);
        lCropSlider.getSlider().setMajorTickUnit(0.5);
        lCropSlider.getSlider().setBlockIncrement(1);

        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(ControlFactory.buildCheckBoxControl(showKnots),
                ControlFactory.buildCheckBoxControl(showControlPoints),
                radSlider,rDivSlider,rCropSlider,tDivSlider,lCropSlider);
        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.drawMode,
                        this.culling,
                        this.material.diffuseColorProperty(),
                        this.material.specularColorProperty()
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(this.textureType, this.colors, 
                        this.sectionType, this.useDiffMap, this.material.diffuseMapProperty(), 
                        this.patterns,this.pattScale, this.dens, this.func)
        );
        
        return this.controlPanel;
        
        
    }

}
