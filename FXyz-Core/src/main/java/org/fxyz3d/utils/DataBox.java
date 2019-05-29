/**
 * DataBox.java
 *
 * Copyright (c) 2013-2019, F(X)yz
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
package org.fxyz3d.utils;

import java.util.Arrays;
import java.util.List;
import org.fxyz3d.geometry.Point3D;

/**
 *
 * @author JosePereda
 */
public class DataBox {

    private double minX, minY, minZ;
    private double maxX, maxY, maxZ;

    public DataBox(Point3D... ini) {
        this(Arrays.asList(ini));
    }

    public DataBox(List<Point3D> dataPoints) {
        reset();
        updateExtremes(dataPoints);
    }

    public static DataBox getDefaultDataBox() {
        return new DataBox(new Point3D(-50, -50, -50), new Point3D(50, 50, 50));
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public void setMinZ(double minZ) {
        this.minZ = minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
    }

    public double getSizeX() {
        return (maxX - minX);
    }

    public double getSizeY() {
        return (maxY - minY);
    }

    public double getSizeZ() {
        return (maxZ - minZ);
    }

    public double getCenterX() {
        return (maxX + minX) / 2d;
    }

    public double getCenterY() {
        return (maxY + minY) / 2d;
    }

    public double getCenterZ() {
        return (maxZ + minZ) / 2d;
    }

    public double getMaxSize() {
        return Math.max(Math.max(getSizeX(), getSizeY()), getSizeZ());
    }

    public final void reset() {
        minX = 0; minY = 0; minZ = 0;
        maxX = 1; maxY = 1; maxZ = 1;
    }

    public final void updateExtremes(List<Point3D> points) {
        double max = points.parallelStream()
                .mapToDouble(p -> p.x)
                .max()
                .orElse(1.0);
        setMaxX(Math.max(max, getMaxX()));
        max = points.parallelStream()
                .mapToDouble(p -> p.y)
                .max()
                .orElse(1.0);
        setMaxY(Math.max(max, getMaxY()));
        max = points.parallelStream()
                .mapToDouble(p -> p.x)
                .max()
                .orElse(1.0);
        setMaxZ(Math.max(max, getMaxZ()));
        double min = points.parallelStream()
                .mapToDouble(p -> p.x)
                .min()
                .orElse(0.0);
        setMinX(Math.min(min, getMinX()));
        min = points.parallelStream()
                .mapToDouble(p -> p.y)
                .min()
                .orElse(0.0);
        setMinY(Math.min(min, getMinY()));
        min = points.parallelStream()
                .mapToDouble(p -> p.z)
                .min()
                .orElse(0.0);
        setMinZ(Math.min(min, getMinZ()));
    }

    @Override
    public String toString() {
        return "DataBox{" + "minX=" + minX + ", minY=" + minY + ", minZ=" + minZ + ", maxX=" + maxX + ", maxY=" + maxY + ", maxZ=" + maxZ + '}';
    }

}
