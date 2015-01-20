/*
 * Copyright (C) 2014 F(Y)zx :
 * Authored by : Jason Pollastrini aka jdub1581, 
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
