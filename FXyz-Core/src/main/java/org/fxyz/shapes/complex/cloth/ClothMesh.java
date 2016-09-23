/**
 * ClothMesh.java
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

package org.fxyz.shapes.complex.cloth;

import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.util.Duration;
import org.fxyz.collections.FloatCollector;
import org.fxyz.geometry.Point3D;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ClothMesh extends MeshView {

    /**
     * Static Default Variables
     */
    private static final Logger log = Logger.getLogger(ClothMesh.class.getName());

    private static final int DEFAULT_DIVISIONS_X = 75;
    private static final int DEFAULT_DIVISIONS_Y = 35;

    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 200;

    private static final double DEFAULT_BEND_STRENGTH = 0.85;
    private static final double DEFAULT_SHEAR_STRENGTH = 0.75;
    private static final double DEFAULT_STRETCH_STRENGTH = 0.55;

    private static final int DEFAULT_CONSTRAINT_ACCURACY = 8;
    private static final int DEFAULT_ITERATIONS = 5;
    
    private static final double DEFAULT_POINT_MASS = 1.0;

    //==========================================================================
    private final ClothTimer timer = new ClothTimer();
    private TriangleMesh mesh = new TriangleMesh();
    private final PhongMaterial material = new PhongMaterial();
    private final List<WeightedPoint> points = new ArrayList<>();
    private final Affine affine = new Affine();

    private BiFunction<Integer, TriangleMesh, int[]> faceValues = (index, m) -> {
        if (index > ((m.getFaces().size() - 1) - m.getFaceElementSize())) {
            return null;
        }
        if (index > 0) {
            index = (index * 6);
            return m.getFaces().toArray(index, null, 6);
        }
        return m.getFaces().toArray(index, null, index + 6);
    };

    private EventHandler<MouseEvent> onPressed;

    /**
     * Builds a ClothMesh with default settings
     */
    public ClothMesh() {
        this(
                DEFAULT_DIVISIONS_X,
                DEFAULT_DIVISIONS_Y,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                DEFAULT_BEND_STRENGTH,
                DEFAULT_SHEAR_STRENGTH,
                DEFAULT_STRETCH_STRENGTH
        );
    }

    /**
     * Builds a ClothMesh with width and height; defaults others
     *
     * @param width
     * @param height
     */
    public ClothMesh(double width, double height) {
        this(
                DEFAULT_DIVISIONS_X,
                DEFAULT_DIVISIONS_Y,
                width,
                height,
                DEFAULT_BEND_STRENGTH,
                DEFAULT_SHEAR_STRENGTH,
                DEFAULT_STRETCH_STRENGTH
        );
    }

    /**
     * Builds a ClothMesh with divsX, divsY; defaults others
     *
     * @param dx
     * @param dy
     */
    public ClothMesh(int dx, int dy) {
        this(
                dx,
                dy,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                DEFAULT_BEND_STRENGTH,
                DEFAULT_SHEAR_STRENGTH,
                DEFAULT_STRETCH_STRENGTH
        );

    }

    /**
     * Builds a ClothMesh with divsX, divsY, width and height; defaults others
     *
     * @param dx
     * @param dy
     * @param width
     * @param height
     */
    public ClothMesh(int dx, int dy, double width, double height) {
        this(
                dx,
                dy,
                width,
                height,
                DEFAULT_BEND_STRENGTH,
                DEFAULT_SHEAR_STRENGTH,
                DEFAULT_STRETCH_STRENGTH
        );
    }

    /**
     * Builds a ClothMesh with divsX, divsY, width and height, stretchStrength;
     * defaults others
     *
     * @param dx
     * @param dy
     * @param width
     * @param height
     * @param stretch
     */
    public ClothMesh(int dx, int dy, double width, double height, double stretch) {
        this(
                dx,
                dy,
                width,
                height,
                DEFAULT_BEND_STRENGTH,
                DEFAULT_SHEAR_STRENGTH,
                stretch
        );
    }

    /**
     * Builds a ClothMesh using settings
     *
     * @param divsX divisions along X axis
     * @param divsY divisions along Y axis
     * @param width requested width
     * @param height requested height
     * @param bendStr strength of bend links
     * @param shearStr strength of shear links
     * @param stretchStr strength of stretch links
     */
    public ClothMesh(int divsX, int divsY, double width, double height, double bendStr, double shearStr, double stretchStr) {

        assert divsX >= 4;
        this.setDivisionsX(divsX);
        assert divsY >= 4;
        this.setDivisionsY(divsY);

        this.setWidth(width);
        this.setHeight(height);

        this.setStretchStrength(stretchStr);
        this.setBendStrength(bendStr);
        this.setShearStrength(shearStr);

        this.getTransforms().add(affine);
        
        this.buildMesh(getDivisionsX(), getDivisionsY(), getWidth(), getHeight(), isUsingShearLinks(), isUsingBendingLinks());        
        
        this.setOnMousePressed((MouseEvent me) -> {
            if (me.isPrimaryButtonDown()) {
                PickResult pr = me.getPickResult();
                if (pr.getIntersectedFace() != -1) {
                    int[] vals = faceValues.apply(pr.getIntersectedFace(), mesh);
                    if (me.isControlDown()) {
                        points.get(vals[0]).setOldPosition(points.get(vals[0]).getOldPosition().add(0, 0, 25));
                        points.get(vals[2]).setOldPosition(points.get(vals[2]).getOldPosition().add(0, 0, 25));
                        points.get(vals[4]).setOldPosition(points.get(vals[4]).getOldPosition().add(0, 0, 25));
                    } else {
                        points.get(vals[0]).setOldPosition(points.get(vals[0]).getOldPosition().add(0, 0, -25));
                        points.get(vals[2]).setOldPosition(points.get(vals[2]).getOldPosition().add(0, 0, -25));
                        points.get(vals[4]).setOldPosition(points.get(vals[4]).getOldPosition().add(0, 0, -25));
                    }
                }
            }
        });
    }
    /*==========================================================================
     Updating Methods
     */

    /**
     *
     */
    private void updatePoints() {
        float[] pts = this.points.stream()
                .flatMapToDouble(wp -> {
                    return wp.getPosition().getCoordinates();
                })
                .collect(() -> new FloatCollector(this.points.size() * 3), FloatCollector::add, FloatCollector::join)
                .toArray();

        mesh.getPoints().setAll(pts, 0, pts.length);
    }

    /**
     *
     */
    public void updateUI() {
        updatePoints();
    }
    /*==========================================================================
     Mesh Creation
     *///=======================================================================

    /**
     * @param divsX number of points along X axis
     * @param divsY number of points along Y axis
     * @param width desired Width of Mesh
     * @param height desired Height of Mesh
     * @param stretch constraint elasticity / stiffness
     */
    private void buildMesh(int divsX, int divsY, double width, double height, boolean shear, boolean bend) {
        float minX = (float) (-width / 2f),
                maxX = (float) (width / 2f),
                minY = (float) (-height / 2f),
                maxY = (float) (height / 2f);

        int sDivX = (divsX - 1),
                sDivY = (divsY - 1);
        double xDist = (width / divsX),
                yDist = (height / divsY);
        //build Points and TexCoords        
        for (int Y = 0; Y <= sDivY; Y++) {

            float currY = (float) Y / sDivY;
            float fy = (1 - currY) * minY + currY * maxY;

            for (int X = 0; X <= sDivX; X++) {

                float currX = (float) X / sDivX;
                float fx = (1 - currX) * minX + currX * maxX;

                //create point: parent, mass, x, y, z
                WeightedPoint p = new WeightedPoint(this, getPerPointMass(), fx, fy, Math.random());

                //Pin Points in place
                if (Y == 0 && X == 0 || (X == 0 && Y == sDivY)) {
                    p.setAnchored(true);
                    p.setForceAffected(false);
                } else {
                    p.setForceAffected(true);
                }
                if (((Y < 5) && (X == 0)) || ((Y > sDivY - 5) && X == 0)) {
                    p.setMass(100);
                }
                // stabilLinks 
                if (X != 0) {
                    p.attatchTo((points.get(points.size() - 1)), xDist, getStretchStrength());
                    //log.log(Level.INFO, "\nLINK-INFO\nOther Index: {0}, This Index: {1}\nLink Distance: {2}\nStiffness: {3}\n", new Object[]{(points.size() - 2), points.indexOf(p),(width / divsX), stiffness});
                }
                if (Y != 0) {
                    p.attatchTo((points.get((Y - 1) * (divsX) + X)), yDist, getStretchStrength());
                    //log.log(Level.INFO, "\nLINK-INFO\nOther Index: {0}, This Index: {1}\nLink Distance: {2}\nStiffness: {3}\n", new Object[]{((Y - 1) * (divsX) + X), points.indexOf(p),(height / divsY), stiffness});
                }
                //add to points
                points.add(p);
                // add Point data into Mesh
                mesh.getPoints().addAll(p.position.x, p.position.y, p.position.z);
                // add texCoords
                mesh.getTexCoords().addAll(currX, currY);
            }
        }
        //shearLinks
        if (shear) {
            for (int Y = 0; Y <= sDivY; Y++) {
                for (int X = 0; X <= sDivX; X++) {
                    WeightedPoint p = points.get(Y * divsX + X);
                    // top left(xy) to right(xy + 1)
                    if (X < (divsX - 1) && Y < (divsY - 1)) {
                        p.attatchTo((points.get(((Y + 1) * (divsX) + (X + 1)))), sqrt((xDist * xDist) + (yDist * yDist)), getShearStrength());
                    }
                    // index(xy) to left(x - 1(y + 1))
                    if (Y != 0 && X != (divsX - 1)) {
                        p.attatchTo((points.get(((Y - 1) * divsX + (X + 1)))), sqrt((xDist * xDist) + (yDist * yDist)), getShearStrength());
                    }
                }
            }
        }
        //bendLinks
        if (bend) {
            for (int Y = 0; Y <= sDivY; Y++) {
                for (int X = 0; X <= sDivX; X++) {
                    WeightedPoint p = points.get(Y * divsX + X);
                    //skip every other
                    if (X < (divsX - 2)) {
                        p.attatchTo((points.get((Y * divsX + (X + 2)))), xDist * 2, getBendStrength());
                    }
                    if (Y < (divsY - 2)) {
                        p.attatchTo((points.get((Y + 2) * divsX + X)), xDist * 2, getBendStrength());
                    }
                    p.setOldPosition(p.getPosition());
                }
            }
        }
        // build faces
        for (int Y = 0; Y < sDivY; Y++) {
            for (int X = 0; X < sDivX; X++) {
                int p00 = Y * (sDivX + 1) + X;
                int p01 = p00 + 1;
                int p10 = p00 + (sDivX + 1);
                int p11 = p10 + 1;
                int tc00 = Y * (sDivX + 1) + X;
                int tc01 = tc00 + 1;
                int tc10 = tc00 + (sDivX + 1);
                int tc11 = tc10 + 1;

                mesh.getFaces().addAll(p00, tc00, p10, tc10, p11, tc11);
                mesh.getFaces().addAll(p11, tc11, p01, tc01, p00, tc00);
            }
        }
        //set triMesh
        setMesh(mesh);
        setMaterial(material);
    }
    /*==========================================================================
     Properties
     *///=======================================================================
    private final DoubleProperty width = new SimpleDoubleProperty(this, "width", DEFAULT_WIDTH) {
        @Override
        protected void invalidated() {

        }
    };

    public final double getWidth() {
        return width.get();
    }

    public final void setWidth(double value) {
        width.set(value);
    }

    public DoubleProperty widthProperty() {
        return width;
    }
    //==========================================================================
    private final DoubleProperty height = new SimpleDoubleProperty(this, "height", DEFAULT_HEIGHT) {
        @Override
        protected void invalidated() {

        }
    };

    public final double getHeight() {
        return height.get();
    }

    public final void setHeight(double value) {
        height.set(value);
    }

    public DoubleProperty heightProperty() {
        return height;
    }
    //==========================================================================    
    private final IntegerProperty divisionsX = new SimpleIntegerProperty(this, "divisionsX", DEFAULT_DIVISIONS_X) {
        @Override
        protected void invalidated() {

        }
    };

    public final int getDivisionsX() {
        return divisionsX.get();
    }

    public final void setDivisionsX(int value) {
        divisionsX.set(value);
    }

    public final IntegerProperty divisionsXProperty() {
        return divisionsX;
    }
    //==========================================================================
    private final IntegerProperty divisionsY = new SimpleIntegerProperty(this, "divisionsY", DEFAULT_DIVISIONS_Y) {
        @Override
        protected void invalidated() {

        }
    };

    public final int getDivisionsY() {
        return divisionsY.get();
    }

    public final void setDivisionsY(int value) {
        divisionsY.set(value);
    }

    public final IntegerProperty divisionsYProperty() {
        return divisionsY;
    }
    /*==========================================================================
     Constraint Strengths
     */
    private final DoubleProperty stretchStrength = new SimpleDoubleProperty(this, "stretchStrength", DEFAULT_STRETCH_STRENGTH) {
        @Override
        protected void invalidated() {

        }
    };

    public final double getStretchStrength() {
        return stretchStrength.get();
    }

    public final void setStretchStrength(double value) {
        stretchStrength.set(value);
    }

    public final DoubleProperty stretchStrengthProperty() {
        return stretchStrength;
    }
    //==========================================================================
    private final DoubleProperty shearStrength = new SimpleDoubleProperty(this, "shearStrength", DEFAULT_SHEAR_STRENGTH) {
        @Override
        protected void invalidated() {

        }
    };

    public final double getShearStrength() {
        return shearStrength.get();
    }

    public final void setShearStrength(double value) {
        shearStrength.set(value);
    }

    public final DoubleProperty shearStrengthProperty() {
        return shearStrength;
    }
    //==========================================================================
    private final DoubleProperty bendStrength = new SimpleDoubleProperty(this, "bendStrength", DEFAULT_BEND_STRENGTH) {
        @Override
        protected void invalidated() {

        }
    };

    public final double getBendStrength() {
        return bendStrength.get();
    }

    public final void setBendStrength(double value) {
        bendStrength.set(value);
    }

    public final DoubleProperty bendStrengthProperty() {
        return bendStrength;
    }
    /*==========================================================================
     Use Constraints?
     */
    private final BooleanProperty useBendingLinks = new SimpleBooleanProperty(this, "useBendingLinks", true) {
        @Override
        protected void invalidated() {

        }
    };

    public final boolean isUsingBendingLinks() {
        return useBendingLinks.get();
    }

    public final void setUseBendingLinks(boolean value) {
        useBendingLinks.set(value);
    }

    public final BooleanProperty usingBendingLinksProperty() {
        return useBendingLinks;
    }
    //==========================================================================
    private final BooleanProperty useShearLinks = new SimpleBooleanProperty(this, "useShearLinks", true) {
        @Override
        protected void invalidated() {

        }
    };

    public final boolean isUsingShearLinks() {
        return useShearLinks.get();
    }

    public final void setUseShearLinks(boolean value) {
        useShearLinks.set(value);
    }

    public final BooleanProperty usingShearLinksProperty() {
        return useShearLinks;
    }

    /*==========================================================================
     Timer related Properties
     */
    private final IntegerProperty constraintAccuracy = new SimpleIntegerProperty(this, "constraintAccuracy", DEFAULT_CONSTRAINT_ACCURACY);

    public final int getConstraintAccuracy() {
        return constraintAccuracy.get();
    }

    public final void setConstraintAccuracy(int value) {
        constraintAccuracy.set(value);
    }

    public final IntegerProperty constraintAccuracyProperty() {
        return constraintAccuracy;
    }
    //==========================================================================
    private final IntegerProperty iterations = new SimpleIntegerProperty(this, "iterations", DEFAULT_ITERATIONS);

    public final int getIterations() {
        return iterations.get();
    }

    public final void setIterations(int value) {
        iterations.set(value);
    }

    public final IntegerProperty iterationsProperty() {
        return iterations;
    }
    
    /**=========================================================================
     * Starts the Cloth Simulation
     */
    public final void startSimulation() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    /**
     * Pauses the Cloth Simulation
     */
    public final void pauseSimulation() {
        timer.pause();
    }

    /**
     * Stops the Cloth Simulation
     */
    public final void stopSimulation() {
        timer.cancel();
    }

    /*==========================================================================
     *   Material Delagates                                                     *
     *///========================================================================
    public final void setDiffuseColor(Color value) {
        material.setDiffuseColor(value);
    }

    public final Color getDiffuseColor() {
        return material.getDiffuseColor();
    }

    public final ObjectProperty<Color> diffuseColorProperty() {
        return material.diffuseColorProperty();
    }

    public final void setSpecularColor(Color value) {
        material.setSpecularColor(value);
    }

    public final Color getSpecularColor() {
        return material.getSpecularColor();
    }

    public final ObjectProperty<Color> specularColorProperty() {
        return material.specularColorProperty();
    }

    public final void setSpecularPower(double value) {
        material.setSpecularPower(value);
    }

    public final double getSpecularPower() {
        return material.getSpecularPower();
    }

    public final DoubleProperty specularPowerProperty() {
        return material.specularPowerProperty();
    }

    public final void setDiffuseMap(Image value) {
        material.setDiffuseMap(value);
    }

    public final Image getDiffuseMap() {
        return material.getDiffuseMap();
    }

    public final ObjectProperty<Image> diffuseMapProperty() {
        return material.diffuseMapProperty();
    }

    public final void setSpecularMap(Image value) {
        material.setSpecularMap(value);
    }

    public final Image getSpecularMap() {
        return material.getSpecularMap();
    }

    public final ObjectProperty<Image> specularMapProperty() {
        return material.specularMapProperty();
    }

    public final void setBumpMap(Image value) {
        material.setBumpMap(value);
    }

    public final Image getBumpMap() {
        return material.getBumpMap();
    }

    public final ObjectProperty<Image> bumpMapProperty() {
        return material.bumpMapProperty();
    }

    public final void setSelfIlluminationMap(Image value) {
        material.setSelfIlluminationMap(value);
    }

    public final Image getSelfIlluminationMap() {
        return material.getSelfIlluminationMap();
    }

    public final ObjectProperty<Image> selfIlluminationMapProperty() {
        return material.selfIlluminationMapProperty();
    }

    /*==========================================================================
     *   TriangleMesh Data
     */
    public final ObservableFloatArray getPoints() {
        return mesh.getPoints();
    }

    public final ObservableFloatArray getTexCoords() {
        return mesh.getTexCoords();
    }

    public final ObservableFaceArray getFaces() {
        return mesh.getFaces();
    }

    public final ObservableIntegerArray getFaceSmoothingGroups() {
        return mesh.getFaceSmoothingGroups();
    }   
    /*==========================================================================
    *   Point Properties
    */
    private final DoubleProperty perPointMass = new SimpleDoubleProperty(this, "perPointMass", DEFAULT_POINT_MASS);

    public double getPerPointMass() {
        return perPointMass.get();
    }

    public void setPerPointMass(double value) {
        perPointMass.set(value);
    }
    
    public void setPointsMass(int index, double m){
        points.get(index).setMass(m);        
    }
    
    public DoubleProperty perPointMassProperty() {
        return perPointMass;
    }
    /*==========================================================================
        Force for Points
    */
    private final ObjectProperty<Point3D> accumulatedForces = new SimpleObjectProperty<>(this, "accumulatedForces");

    public Point3D getAccumulatedForces() {
        return accumulatedForces.get();
    }
    
    public void addForce(Point3D f){
        setAccumulatedForces(getAccumulatedForces().add(f));
    }
    
    public void setAccumulatedForces(Point3D value) {
        accumulatedForces.set(value);
    }

    public ObjectProperty<Point3D> accumulatedForcesProperty() {
        return accumulatedForces;
    }
    
    
    /*==========================================================================
     Points List
     */
    protected final List<WeightedPoint> getPointList() {
        return points;
    }
    
    //End ClothMesh=============================================================
    /**
     * *************************************************************************
     * ClothTimer is a simple timer class for updating points * * @author Jason
     * Pollastrini aka jdub1581 *
     *************************************************************************
     */
    /**
     * Timer to handle Cloth updates
     */
    private class ClothTimer extends ScheduledService<Void> {

        private final long ONE_NANO = 1000000000L;
        private final double ONE_NANO_INV = 1f / 1000000000L;

        private long startTime, previousTime;
        private double deltaTime;
        private final double fixedDeltaTime = 0.16;
        private int leftOverDeltaTime, timeStepAmt;

        private final NanoThreadFactory tf;
        private boolean paused;

        public ClothTimer() {
            super();

            this.setPeriod(Duration.millis(16));

            this.tf = new NanoThreadFactory();
            this.setExecutor(Executors.newSingleThreadExecutor(tf));
        }

        /**
         * @return elapsed time as a double
         */
        public double getTimeAsSeconds() {
            return getTime() * ONE_NANO_INV;
        }

        /**
         *
         * @return elapsed time as a long
         */
        public long getTime() {
            return System.nanoTime() - startTime;
        }

        /**
         *
         * @return one nano second
         */
        private long getOneSecondAsNano() {
            return ONE_NANO;
        }

        /**
         *
         * @return deltaTime
         */
        public double getDeltaTime() {
            return deltaTime;
        }

        /**
         *
         * @return updates Timers clock values
         */
        private void updateTimer() {
            deltaTime = (getTime() - previousTime) * (10.0f / ONE_NANO);
            previousTime = getTime();
            timeStepAmt = (int) ((deltaTime + leftOverDeltaTime) / fixedDeltaTime);
            timeStepAmt = Math.min(timeStepAmt, 5);
            leftOverDeltaTime = (int) (deltaTime - (timeStepAmt * fixedDeltaTime));
        }

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    updateTimer();
                    
                    IntStream.range(0, getIterations()).forEach(i->{});
                    points.parallelStream().filter(p->{return points.indexOf(p) % (getDivisionsX() - 1) == 0;}).forEach(p -> {
                        p.applyForce(new Point3D(5,-1,1));
                    });
                    for (int i = 0; i < getConstraintAccuracy(); i++) {
                        points.parallelStream().forEach(WeightedPoint::solveConstraints);
                    }
                    points.parallelStream().forEach(p -> {
                        p.applyForce(new Point3D(4.8f,1,-1));
                        p.updatePhysics(deltaTime, 1);                        
                    });

                    return null;
                }
            };
        }

        @Override
        protected void failed() {
            getException().printStackTrace(System.err);

        }

        @Override
        protected void succeeded() {
            super.succeeded();
            updateUI();
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            reset();
        }

        @Override
        public void start() {
            if (isRunning()) {
                return;
            }
            super.start();
            if (startTime <= 0) {
                startTime = System.nanoTime();
            }

        }

        protected void pause() {
            paused = true;
            if (isRunning()) {
                if (cancel()) {
                    cancelled();
                }
            }
        }

        @Override
        public void reset() {
            super.reset();
            if (!paused) {
                startTime = System.nanoTime();
                previousTime = getTime();
            }
        }

        @Override
        public String toString() {
            return "ClothTimer{" + "startTime=" + startTime + ", previousTime=" + previousTime + ", deltaTime=" + deltaTime + ", fixedDeltaTime=" + fixedDeltaTime + ", leftOverDeltaTime=" + leftOverDeltaTime + ", timeStepAmt=" + timeStepAmt + '}';
        }

        /*==========================================================================
    
         */
        private class NanoThreadFactory implements ThreadFactory {

            public NanoThreadFactory() {
            }

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "ClothTimerThread");
                t.setDaemon(true);
                return t;
            }

        }
    }//End ClothTimer===========================================================

}
