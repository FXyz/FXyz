/*
 * Copyright (C) 2013-2014 F(X)yz, 
 * Sean Phillips, Jason Pollastrini
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

package org.fxyz.geometry;

import java.util.stream.DoubleStream;

/**
 *
 * @author Sean
 * @Description Just a useful data structure for X,Y,Z triplets.

 */
public class Point3D {
    
    public float x = 0;
    public float y = 0;
    public float z = 0;

    public float r = 0;
    public float phi = 0;
    public float theta = 0;
    /* 
    * @param X,Y,Z are all floats to align with TriangleMesh needs 
    */
    public Point3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    
        r=(float)Math.sqrt(x*x+y*y+z*z);
        phi=(float)Math.atan2(y,x);
        theta=(float)Math.acos(z/r);
    }
    
    public DoubleStream getCoordinates() { return DoubleStream.of(x,y,z); }
    public DoubleStream getCoordinates(float factor) { return DoubleStream.of(factor*x,factor*y,factor*z); }
    
    public Point3D add(Point3D point) {
        return add(point.x, point.y, point.z);
    }
    
    public Point3D add(float x, float y, float z) {
        return new Point3D(this.x + x, this.y + y, this.z+ z);
    }
    
    public Point3D multiply(float factor) {
        return new Point3D(this.x * factor, this.y * factor, this.z * factor);
    }
    
    public Point3D normalize() {
        final float mag = magnitude();

        if (mag == 0.0) {
            return new Point3D(0f, 0f, 0f);
        }

        return new Point3D(x / mag, y / mag, z / mag);
    }
    
    public float magnitude() {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    @Override
    public String toString() {
        return "Point3D{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
    
}
