/**
 * WeightedPoint.java
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

import java.util.HashMap;
import org.fxyz.geometry.Point3D;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class WeightedPoint {

        private final ClothMesh parent;

        public double mass = 0;
        public Point3D position,
                oldPosition,
                anchorPosition,
                force;

        private final HashMap<WeightedPoint, Constraint> constraints = new HashMap<>();

        private boolean anchored = false,
                forceAffected = true;

        /*==========================================================================
         Constructors
         */
        private WeightedPoint() {
            this.parent = null;
        }

        public WeightedPoint(ClothMesh parent) {
            this(parent, 0.1, 0, 0, 0, false);
        }

        public WeightedPoint(ClothMesh parent, double mass) {
            this(parent, mass, 0, 0, 0, false);
        }

        public WeightedPoint(ClothMesh parent, double mass, double x, double y, double z) {
            this(parent, mass, x, y, z, false);
        }

        public WeightedPoint(ClothMesh parent, double mass, double x, double y, double z, boolean anchored) {

            this.position = new Point3D((float) x, (float) y, (float) z);
            this.oldPosition = new Point3D((float) x, (float) y, (float) z);
            this.anchorPosition = new Point3D(0, 0, 0);
            this.force = new Point3D(0, 0, 0);

            this.parent = parent;
            this.mass = mass;
            this.anchored = anchored;

        }
        /*==========================================================================
         Constraints
         */

        public final void attatchTo(WeightedPoint other, double linkDistance, double stiffness) {
            attatchTo(this, other, linkDistance, stiffness);
        }

        public final void attatchTo(WeightedPoint self, WeightedPoint other, double linkDistance, double stiffness) {
            Link pl = new Link(self, other, linkDistance, stiffness);
            addConstraint(other, (Constraint) pl);
        }

        //==========================================================================
        public HashMap<WeightedPoint, Constraint> getConstraints() {
            return constraints;
        }

        public void addConstraint(WeightedPoint other, Constraint constraint) {
            this.constraints.put(other, constraint);
        }

        public void addConstraint(Constraint c){
            this.constraints.put(this, c);
        }
        
        public void removeConstraint(Constraint pl) {
            
        }

        public void clearConstraints() {
            constraints.clear();
        }
        /*==========================================================================
         Updating
         */

        public void solveConstraints() {
            constraints.values().parallelStream().forEach(Constraint::solve);
        }

        public void updatePhysics(double dt, double t) {
            synchronized (this) {
                if (isAnchored()) {
                    setPosition(getAnchorPosition());
                    return;
                }
                Point3D vel = new Point3D(
                        (position.x - oldPosition.x),
                        (position.y - oldPosition.y),
                        (position.z - oldPosition.z)
                );
                float dtSq = (float) (dt * dt);

                // calculate the next position using Verlet Integration
                Point3D next = new Point3D(
                        position.x + (vel.x + (((force.x / (float) (mass)) * 0.5f) * dtSq)),
                        position.y + (vel.y + (((force.y / (float) (mass)) * 0.5f) * dtSq)),
                        position.z + (vel.z + (((force.z / (float) (mass)) * 0.5f) * dtSq))
                );

                // reset variables
                setOldPosition(position);
                setPosition(next);
                clearForces();
                //log.log(Level.INFO, "\n Velocity: {0}", vel);
            }
        }
        /*==========================================================================
         Variable's Getters / setters
         */

        public double getMass() {
            return mass;
        }

        public void setMass(double mass) {
            this.mass = mass;
        }

        /*==========================================================================
         Positions
         */
        public final Point3D getPosition() {
            return new Point3D(position.x, position.y, position.z);
        }

        public void setPosition(Point3D pos) {
            position.x = pos.x;
            position.y = pos.y;
            position.z = pos.z;
        }

        public void setPosition(double x, double y, double z) {
            position.x = (float) x;
            position.y = (float) y;
            position.z = (float) z;
        }

        //==========================================================================
        public Point3D getOldPosition() {
            return oldPosition;
        }

        public void setOldPosition(Point3D oldPosition) {
            this.oldPosition.x = oldPosition.x;
            this.oldPosition.y = oldPosition.y;
            this.oldPosition.z = oldPosition.z;
        }

        public void setOldPosition(float x, float y, float z) {
            this.oldPosition.x = x;
            this.oldPosition.y = y;
            this.oldPosition.z = z;
        }

        public void setOldPosition(double x, double y, double z) {
            this.oldPosition.x = (float) x;
            this.oldPosition.y = (float) y;
            this.oldPosition.z = (float) z;
        }

        //==========================================================================
        public boolean isAnchored() {
            return anchored;
        }

        public void setAnchored(boolean anchored) {
            this.anchored = anchored;
            if (anchored) {
                setAnchorPosition(new Point3D(position.x, position.y, position.z));
            } else {
                setAnchorPosition(null);
            }
        }

        public Point3D getAnchorPosition() {
            return anchorPosition;
        }

        public void setAnchorPosition(Point3D anchorPosition) {
            this.anchorPosition = anchorPosition;
        }
        /*==========================================================================
         Forces
         */

        public Point3D getForce() {
            return force;
        }

        private void setForce(Point3D p) {
            this.force = p;
        }

        public void applyForce(Point3D force) {
            if (isForceAffected()) {
                this.force.x += force.x;
                this.force.y += force.y;
                this.force.z += force.z;
            }
        }

        public void clearForces() {
            setForce(new Point3D(0, 0, 0));
        }

        public boolean isForceAffected() {
            return forceAffected;
        }

        public void setForceAffected(boolean forceAffected) {
            this.forceAffected = forceAffected;
        }

        /*==========================================================================
    
         */
        @Override
        public String toString() {
            return "WeightedPoint: ".concat(position.toString());
        }

    }//End WeightedPoint========================================================
