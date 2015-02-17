/**
* MathUtils.java
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

package org.fxyz.geometry;
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
    
        
    public static javafx.geometry.Point3D computeNormal(javafx.geometry.Point3D v1, javafx.geometry.Point3D v2, javafx.geometry.Point3D v3) {
        javafx.geometry.Point3D a1 = v1.subtract(v2);
        javafx.geometry.Point3D a2 = v3.subtract(v2);
        return a2.crossProduct(a1).normalize();
    }
    
    public static javafx.geometry.Point3D sphericalToCartesian(javafx.geometry.Point3D sphereCoords) {
        double a, x, y, z;
        y = sphereCoords.getX() * Math.sin(sphereCoords.getZ());
        a = sphereCoords.getX() * Math.cos(sphereCoords.getZ());
        x = a * Math.cos(sphereCoords.getY());
        z = a * Math.sin(sphereCoords.getY());
        return new javafx.geometry.Point3D(x, y, z);
    }
    
    public static javafx.geometry.Point3D cartesianToSpherical(javafx.geometry.Point3D cartCoords) {
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
        return new javafx.geometry.Point3D(storex, storey, storez);
    }
    public static float clamp(float input, float min, float max) {
        return (input < min) ? min : (input > max) ? max : input;
    }
    public static double clamp(double input, double min, double max) {
        return (input < min) ? min : (input > max) ? max : input;
    }
}
