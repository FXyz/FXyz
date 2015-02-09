/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.samples.utilities;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.util.Duration;
import org.fxyz.samples.shapes.ShapeBaseSample;
import org.fxyz.geometry.Ray;

/**
 * A simple app Showing the newly added Ray class.
 * <br><p>
 * Clicking on the Scene will spawn a Sphere at that position (origin of
 * Ray)<br>
 * Mouse buttons target different nodes Targets are breifly highlighted if an
 * intersection occurs.
 * <br><br>
 * RayTest in org.fxyz.tests package has reference on TriangleMesh intersections
 *
 * </p><br>
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class SimpleRayTest extends ShapeBaseSample {

    private final PhongMaterial red = new PhongMaterial(Color.ORCHID),
            blue = new PhongMaterial(Color.BLUEVIOLET),
            highlight = new PhongMaterial(Color.LIME.brighter());
    //==========================================================================
    
    private final AmbientLight rayLight = new AmbientLight();
    private boolean fireRay = true;

    @Override
    protected void addMeshAndListeners() {
        /* add camera so it doesn't affect all nodes
        rayLight.getScope().add(camera);

        PointLight light = new PointLight(Color.GAINSBORO);
        light.setTranslateX(-300);
        light.setTranslateY(300);
        light.setTranslateZ(-2000);

        PointLight light2 = new PointLight(Color.ALICEBLUE);
        light2.setTranslateX(300);
        light2.setTranslateY(-300);
        light2.setTranslateZ(2000);

        PointLight light3 = new PointLight(Color.SPRINGGREEN);
        light3.setTranslateY(-2000);
        //create a target
        Sphere target1 = new Sphere(80);
        target1.setId("t1");
        target1.setDrawMode(DrawMode.LINE);
        target1.setCullFace(CullFace.NONE);
        target1.setTranslateX(300);
        target1.setTranslateY(300);
        target1.setTranslateZ(2500);
        target1.setMaterial(red);
        target1.setOnMouseEntered(e -> {
            e.consume();
        });
        // create another target
        SpheroidMesh target2 = new SpheroidMesh(120, 50);
        target2.setId("t2");
        target2.setDrawMode(DrawMode.LINE);
        target2.setCullFace(CullFace.NONE);
        target2.setTranslateX(200);
        target2.setTranslateY(-200);
        target2.setTranslateZ(-500);
        target2.setMaterial(blue);

        group.getChildren().addAll(target1, target2, light, light2, light3, rayLight);

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
                Point3D o = CameraHelper.pickProjectPlane(camera, e.getSceneX(), e.getSceneY());
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
                        
                    }

                } // repeat for other target as well
                else if (e.isSecondaryButtonDown()) {
                    Point3D tgt = Point3D.ZERO.add(target1.getTranslateX(), target1.getTranslateY(), target1.getTranslateZ()),
                            dir = tgt.subtract(o);

                    Ray r = new Ray(o, dir);
                    double dist = tgt.distance(o);
                    if (target1.getBoundsInParent().contains(r.project(dist))) {
                        animateRayTo(r, target1, Duration.seconds(2));
                        
                    }

                }
            }
        });
        */
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
                            try {
                                this.finalize();
                            } catch (Throwable ex) {
                                Logger.getLogger(SimpleRayTest.class.getName()).log(Level.SEVERE, null, ex);
                            }

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
  
        ((Shape3D)group.getChildren().get(1)).setMaterial(red);
        ((Shape3D)group.getChildren().get(2)).setMaterial(blue);
        
    }

    @Override
    protected void createMesh() {
    }

    

    @Override
    public Node getControlPanel() {
        return super.getControlPanel(); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected Node buildControlPanel() {
        return null;
    }
    

}
