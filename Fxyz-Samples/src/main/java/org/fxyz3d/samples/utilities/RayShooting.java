/**
 * RayShooting.java
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

package org.fxyz3d.samples.utilities;

import com.sun.javafx.scene.CameraHelper;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.util.Duration;
import org.fxyz3d.geometry.Ray;
import org.fxyz3d.samples.shapes.ShapeBaseSample;

/**
 * A simple app Showing the newly added Ray class.
 * <br><p>
 * Clicking on the Scene will spawn a Sphere at that position (origin of
 * Ray)<br>
 * Mouse buttons target different nodes Targets are breifly highlighted if an
 * intersection occurs.
 * <br><br>
 * RayTest in org.fxyz3d.tests package has reference on TriangleMesh intersections
 *
 * </p><br>
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class RayShooting extends ShapeBaseSample {
    
    public static void main(String[] args){launch(args);}

    private final PhongMaterial red = new PhongMaterial(Color.ORCHID),
            blue = new PhongMaterial(Color.BLUEVIOLET),
            highlight = new PhongMaterial(Color.LIME.brighter());
    //==========================================================================
    
    private final AmbientLight rayLight = new AmbientLight();
    protected PointLight light, light2, light3;
    private boolean fireRay = true;
    protected Sphere target1, target2;

    @Override
    protected void addMeshAndListeners() {
              
        
        camera.setTranslateZ(-5000);
        //First person shooter keyboard movement
        subScene.setOnKeyPressed(ke -> {
            // add a flag so we can still move the camera
            if (ke.getCode().equals(KeyCode.CONTROL)) {
                fireRay = false;
            }
            
        });
        
        subScene.setOnKeyReleased(ke -> {
            // release flag
            if (ke.getCode().equals(KeyCode.CONTROL)) {
                fireRay = true;
            }
        });

        subScene.setOnMousePressed(e -> {
            if (fireRay) {
                Point3D o = CameraHelper.pickProjectPlane(camera, e.getX(), e.getY());
                if (e.isPrimaryButtonDown()) {
                    // set Target and Direction
                    Point3D t = Point3D.ZERO.add(target2.getTranslateX(), target2.getTranslateY(), target2.getTranslateZ()),
                            d = t.subtract(o);
                    //Build the Ray
                    Ray r = new Ray(o, d);
                    double dist = t.distance(o);
                    // If ray intersects node, spawn and animate
                    if (target2.getBoundsInParent().contains(r.project(dist))) {
                        animateRayTo(r, target2, Duration.seconds(2));
                        e.consume();
                    }
                    e.consume();
                } // repeat for other target as well
                else if (e.isSecondaryButtonDown()) {
                    Point3D tgt = Point3D.ZERO.add(target1.getTranslateX(), target1.getTranslateY(), target1.getTranslateZ()),
                            dir = tgt.subtract(o);

                    Ray r = new Ray(o, dir);
                    double dist = tgt.distance(o);
                    if (target1.getBoundsInParent().contains(r.project(dist))) {
                        animateRayTo(r, target1, Duration.seconds(2));
                        e.consume();
                    }
                    e.consume();
                }
            }e.consume();
        });
        //*/
    }

    /**
     *
     * @param r The Ray that holds the info
     * @param tx to x
     * @param ty to y
     * @param tz to z
     * @param dps distance per step to move ray
     * @param time length of animation
     */
    private void animateRayTo(final Ray r, final Shape3D target, final Duration time) {

        final Transition t = new Transition() {
            protected Ray ray;
            protected Sphere s;
            protected double dist;

            {
                this.ray = r;

                this.s = new Sphere(5);
                s.setTranslateX((ray.getOrigin()).getX());
                s.setTranslateY((ray.getOrigin()).getY());
                s.setTranslateZ((ray.getOrigin()).getZ());
                s.setMaterial(highlight);
                rayLight.getScope().add(s);
                this.dist = ray.getOrigin().distance(
                        Point3D.ZERO.add(target.getTranslateX(), target.getTranslateY(), target.getTranslateZ())
                );

                setCycleDuration(time);
                this.setInterpolator(Interpolator.LINEAR);
                this.setOnFinished(e -> {
                    if (target.getBoundsInParent().contains(ray.getPosition())) {
                        if (target.getBoundsInLocal().contains(target.parentToLocal(ray.getPosition()))) {
                            target.setMaterial(highlight);
                        }

                        PauseTransition t = new PauseTransition(Duration.millis(150));
                        t.setOnFinished(pe -> {
                            reset();
                            group.getChildren().removeAll(s);
                            s = null;
                        });
                        t.playFromStart();
                    }
                });
                group.getChildren().add(s);
            }

            @Override
            protected void interpolate(double frac) {
                // frac-> 0.0 - 1.0 
                // project ray 
                ray.project(dist * frac);
                // set the sphere to ray position
                s.setTranslateX(ray.getPosition().getX());
                s.setTranslateY(ray.getPosition().getY());
                s.setTranslateZ(ray.getPosition().getZ());

            }

        };
        t.playFromStart();
    }

    // resets materisl on targets
    private void reset() {
  
        ((Shape3D)((Group)model).getChildren().get(0)).setMaterial(red);
        ((Shape3D)((Group)model).getChildren().get(1)).setMaterial(blue);
        
    }

    @Override
    protected void createMesh() {
        // add camera so it doesn't affect all nodes
        rayLight.getScope().add(camera);

        light = new PointLight(Color.GAINSBORO);
        light.setTranslateX(-300);
        light.setTranslateY(300);
        light.setTranslateZ(-2000);

        light2 = new PointLight(Color.ALICEBLUE);
        light2.setTranslateX(300);
        light2.setTranslateY(-300);
        light2.setTranslateZ(2000);

        light3 = new PointLight(Color.SPRINGGREEN);
        light3.setTranslateY(-2000);
        //create a target
        target1 = new Sphere(180);
        target1.setId("t1");
        target1.setDrawMode(DrawMode.LINE);
        target1.setCullFace(CullFace.NONE);
        target1.setTranslateX(300);
        target1.setTranslateY(300);
        target1.setTranslateZ(2500);
        target1.setMaterial(red);
        // create another target
        target2 = new Sphere(150);
        target2.setId("t2");
        target2.setDrawMode(DrawMode.LINE);
        target2.setCullFace(CullFace.NONE);
        target2.setTranslateX(200);
        target2.setTranslateY(-200);
        target2.setTranslateZ(-500);
        target2.setMaterial(blue);

        model = new Group(target1, target2, light, light2, light3, rayLight);
    }
    
    
    @Override
    protected Node buildControlPanel() {
        return null;
    }
    

}
