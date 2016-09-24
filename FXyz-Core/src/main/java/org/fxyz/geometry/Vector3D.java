/**
 * Vector3D.java
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

/**
 *
 * @author Dub
 */
public final class Vector3D {

    public static final Vector3D// Homogenious      public static final Vector3D// Homogenious      
            FORWARD = new Vector3D(0, 0, 1),
            BACK = new Vector3D(0, 0, -1),
            LEFT = new Vector3D(-1, 0, 0),
            RIGHT = new Vector3D(1, 0, 0),
            UP = new Vector3D(0, 1, 0),
            DOWN = new Vector3D(0, -1, 0),
            ONE = new Vector3D(1, 1, 1),
            ZERO = new Vector3D(0, 0, 0),
            NAN = new Vector3D(Double.NaN, Double.NaN, Double.NaN);

    public double x;
    public double y;
    public double z;

    public Vector3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3D(Vector3D source) {
        this.x = source.x;
        this.y = source.y;
        this.z = source.z;
    }

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(double... values) {
        if (values.length != size()) {
            throw new IllegalArgumentException();
        }
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
    }

    public double angle(Vector3D v) {
        double mag2 = (x * x) + (y * y) + (z * z);
        double vmag2 = (v.x * v.x) + (v.y * v.y) + (v.z * v.z);
        double dot = (x * v.x) + (y * v.y) + (z * v.z);
        return Math.acos(dot / Math.sqrt(mag2 * vmag2));
    }

    public Vector3D add(double constant) {
        double nx = x + constant,
                ny = y + constant,
                nz = z + constant;
        return new Vector3D(nx, ny, nz);
    }

    public Vector3D add(double dx, double dy, double dz) {
        double nx = x + dx,
                ny = y + dy,
                nz = z + dz;
        return new Vector3D(nx, ny, nz);
    }

    public Vector3D add(Vector3D v) {
        double nx = x + v.x,
                ny = y += v.y,
                nz = z += v.z;
        return new Vector3D(nx, ny, nz);
    }

    public Vector3D addMultiply(double dx, double dy, double dz, double factor) {
        double nx = x + dx * factor,
                ny = y + dy * factor,
                nz = z + dz * factor;
        return new Vector3D(nx, ny, nz);
    }

    public Vector3D addMultiply(Vector3D v, double factor) {
        double nx = x + v.x * factor,
                ny = y + v.y * factor,
                nz = z + v.z * factor;
        return new Vector3D(nx, ny, nz);
    }

    public Vector3D addProduct(Vector3D a, Vector3D b) {
        x += a.x * b.x;
        y += a.y * b.y;
        z += a.z * b.z;
        return new Vector3D(x, y, z);
    }

    public Vector3D addProduct(Vector3D a, Vector3D b, double factor) {
        x += a.x * b.x * factor;
        y += a.y * b.y * factor;
        z += a.z * b.z * factor;
        return new Vector3D(x, y, z);
    }

    public double magnitudeSquared() {
        return (x * x) + (y * y) + (z * z);
    }

    public double distanceSquared(Vector3D v) {
        double dx = x - v.x, dy = y - v.y, dz = z - v.z;
        return (dx * dx) + (dy * dy) + (dz * dz);
    }

    public double distance(Vector3D v) {
        return Math.sqrt(distanceSquared(v));
    }

    public double inverseMagnitude() {
        return 1.0 / Math.sqrt(magnitudeSquared());
    }

    public double magnitude() {
        return Math.sqrt(magnitudeSquared());
    }

    public void normalize() {
        double d = magnitude();
        if (d == 0.0) {
            x = 0;
            y = 0;
            z = 0;
        }
        x /= d;
        y /= d;
        z /= d;
    }

    public void setAs(Vector3D a) {
        this.x = a.x;
        this.y = a.y;
        this.z = a.z;
    }

    public Vector3D multiply(double d) {
        return new Vector3D(
                x * d,
                y * d,
                z * d);
    }

    public Vector3D subtractMultiple(Vector3D v, double factor) {        
        return new Vector3D(
                x - v.x * factor,
                y - v.y * factor,
                z - v.z * factor
        );
    }

    public Vector3D sub(Vector3D v) {
        double nx  = x - v.x,
               ny  = y - v.y,
               nz  = z - v.z;
        return new Vector3D(nx, ny, nz);
    }

    public Vector3D subMultiple(Vector3D v, double factor) {
        return addMultiply(v, -factor);
    }

    public double dotProduct(Vector3D a) {
        return (x * a.x) + (y * a.y) + (z * a.z);
    }

    public double dotProduct(double[] data, int offset) {
        return x * data[offset + 0] + y * data[offset + 1] + z * data[offset + 2];
    }

    public Vector3D crossProduct(Vector3D a) {
        double tx = y * a.z - z * a.y;
        double ty = z * a.x - x * a.z;
        double tz = x * a.y - y * a.x;
        
        return new Vector3D(tx, ty, tz);
    }

    public Vector3D orthogonalTo(Vector3D v1, Vector3D v2){        
        return v1.crossProduct(v2);
    }
    
    public Vector3D projectToPlane(Vector3D normal, double distance) {
        double d = dotProduct(normal);
        return addMultiply(normal, distance - d);
    }

    public int size() {
        return 3;
    }

    public double getSum() {
        return x + y + z;
    }

    public double getProduct() {
        return x * y * z;
    }

    public Vector3D scaleAdd(double factor, double constant) {
        x = (x * factor) + constant;
        y = (y * factor) + constant;
        z = (z * factor) + constant;
        return new Vector3D(x, y, z);
    }

    public Vector3D scaleAdd(double factor, Vector3D constant) {
        x = (x * factor) + constant.x;
        y = (y * factor) + constant.y;
        z = (z * factor) + constant.z;
        return new Vector3D(x, y, z);
    }

    public void setValues(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D negate() {
        return new Vector3D(-x, -y, -z);
    }

    public void getElements(double[] data, int offset) {
        data[offset] = x;
        data[offset + 1] = y;
        data[offset + 2] = z;
    }

    public double[] toDoubleArray() {
        return new double[]{x, y, z};
    }

    public Vector3D toNormal() {
        double d = this.magnitude();
        return (d == 0) ? ZERO : new Vector3D(x / d, y / d, z / d);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public boolean equals(Vector3D v) {
        return (x == v.x) && (y == v.y) && (z == v.z);
    }

    @Override
    public String toString() {
        return "Vector3: {X: " + x + ", Y: " + y + ", Z: " + z + "}";
    }

}
