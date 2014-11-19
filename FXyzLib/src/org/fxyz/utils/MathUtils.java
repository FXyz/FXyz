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

package org.fxyz.utils;

import org.fxyz.geometry.Vector3D;

/**
 *
 * @author Dub
 */
public class MathUtils {
    public static final double DBL_EPSILON = 2.220446049250313E-16d; 
    public static final double ZERO_TOLERANCE = 0.0001d;
    public static final double ONE_THIRD = 1d / 3d;
    public static final double TAU =  (Math.PI * 2.0);
    public static final double HALF_TAU =  Math.PI;
    public static final double QUARTER_TAU =   (Math.PI / 2.0);
    public static final double INVERSE_TAU =   (1.0 / Math.PI);
    public static final double PI =   Math.PI;
    public static final double TWO_PI = 2.0d * PI;
    public static final double HALF_PI = 0.5d * PI;
    public static final double QUARTER_PI = 0.25d * PI;
    public static final double INV_PI = 1.0d / PI;
    public static final double INV_TWO_PI = 1.0d / TWO_PI;
    public static final double DEG_TO_RAD = PI / 180.0d;
    public static final double RAD_TO_DEG = 180.0d / PI;
    
    public static boolean isWithinEpsilon(double a, double b, double epsilon) {
        return Math.abs(a - b) <= epsilon;
    }

    public static boolean isWithinEpsilon(double a, double b) {
        return isWithinEpsilon(a, b, ZERO_TOLERANCE);
    }

    public static boolean isPowerOfTwo(int number) {
        return (number > 0) && (number & (number - 1)) == 0;
    }

    public static int nearestPowerOfTwo(int number) {
        return (int) Math.pow(2, Math.ceil(Math.log(number) / Math.log(2)));
    }
    
        
    public static Vector3D computeNormal(Vector3D v1, Vector3D v2, Vector3D v3) {
        Vector3D a1 = v1.sub(v2);
        Vector3D a2 = v3.sub(v2);
        return a2.crossProduct(a1).toNormal();
    }
    
    public static Vector3D sphericalToCartesian(Vector3D sphereCoords) {
        double a, x, y, z;
        y = sphereCoords.getX() * Math.sin(sphereCoords.getZ());
        a = sphereCoords.getX() * Math.cos(sphereCoords.getZ());
        x = a * Math.cos(sphereCoords.getY());
        z = a * Math.sin(sphereCoords.getY());
        return new Vector3D(x, y, z);
    }
    
    public static Vector3D cartesianToSpherical(Vector3D cartCoords) {
        double x = cartCoords.getX();
        double storex, storey, storez;
        if (x == 0) {
            x = DBL_EPSILON;
        }
        storex = Math.sqrt((x * x)
                + (cartCoords.getY() * cartCoords.getY())
                + (cartCoords.getZ() * cartCoords.getZ()));
        storey = Math.atan(cartCoords.getZ() / x);
        if (x < 0) {
            storey += PI;
        }
        storez = Math.asin(cartCoords.getY() / storex);
        return new Vector3D(storex, storey, storez);
    }
    public static float clamp(float input, float min, float max) {
        return (input < min) ? min : (input > max) ? max : input;
    }
    public static double clamp(double input, double min, double max) {
        return (input < min) ? min : (input > max) ? max : input;
    }
}
