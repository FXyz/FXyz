/**
 * Ray.java
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

package org.fxyz.geometry;

import javafx.geometry.Point3D;

/**
 *  <br>Simple Ray class.<br>
 * Allowing for a variety of performance and productivity enhancements.<br> 
 * Following up on Jose Pereda's RayTest intersection example in the Tests package
 * 
 * @author Jason Pollastrini aka jdub1581
 */
public class Ray {
    
    private Point3D direction, origin, position;
    
    private Ray() {
    }

    /**
     * Constructs a Ray with origin and direction
     * @param orig the origin point of the Ray
     * @param dir direction of the Ray
     */
    public Ray(Point3D orig, Point3D dir) {
        this.origin = orig;
        this.direction = dir;
    }

    /**
     * 
     * @return origin of the Ray
     */
    public Point3D getOrigin() {
        return origin;
    }
    private void setOrigin(Point3D origin) {
        this.origin = origin;
    }
    /**
     * 
     * @return Position of the Ray <br> If projection has not been performed returns origin;
     */    
    public Point3D getPosition() {
        return position != null ? position : origin;
    }

    private void setPosition(Point3D position) {
        this.position = position;
    }
    
    /**
     * 
     * @return direction of the Ray
     */
    public Point3D getDirection() {
        return direction;
    }
    private void setDirection(Point3D direction) {
        this.direction = direction;
    }
    
    /*
        Methods
    */
    
    /**
     * Projects the Ray from <code>origin</code> along <code>direction</code> by <code>distance</code>
     * @param distance
     * @return projected position
     */
    public Point3D project(double distance){
        setPosition(getOrigin().add((getDirection().normalize().multiply(distance))));
        
        return getPosition();
    }
    
    /**
     * Projects the Ray from new <code>origin</code> along <code>direction</code> by <code>distance</code>
     * using  the  original  direction. <br> 
     * Useful for objects moving in a constant direction.
     * @param orig the new origin point
     * @param dist distance to project ray
     * @return sets origin and returns projected position
     */
    public Point3D reProject(Point3D orig, double dist){
        setOrigin(orig);
        setPosition(getOrigin().add((getDirection().normalize().multiply(dist))));
        
        return getPosition();
    }
    
    /**
     * Projects the Ray from new <code>origin</code> along <code>direction</code> by <code>distance</code>
     * @param orig the new origin point
     * @param dir the new direction of travel
     * @param dist distance to project ray
     * @return sets origin and returns projected position
     */
    public Point3D setProject(Point3D orig, Point3D dir, double dist){
        setOrigin(orig);
        setDirection(dir);
        setPosition(getOrigin().add((getDirection().normalize().multiply(dist))));
        
        return getPosition();
    }
    
    /*==========================================================================
    
        TO DO: Consider adding Intersection methods for:
    
            ~ node.boundsInParent
                *with check for meshView
    
            ~ TriangleMesh triangles. 
                *performance can potentially be increased by implementing a 
                 BVT (bounding volume tree), or an OctTree hierarchy 
                 possibly reducing the number of triangle intersection checks.
    
    *///========================================================================

    @Override
    public String toString() {
        return "Ray ::\n" + "    origin    = " + origin + ",\n    direction = " + direction + ",\n    position  = " + position + "\n";
    }  
    
    
}
