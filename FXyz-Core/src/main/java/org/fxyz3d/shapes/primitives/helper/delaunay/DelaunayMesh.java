/**
 * DelaunayMesh.java
 *
 * Copyright (c) 2018-2019, F(X)yz
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
package org.fxyz3d.shapes.primitives.helper.delaunay;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.Triangle;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.utils.DataBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DelaunayMesh {

    private final List<Point3D> dataPoints;
    private final List<Point3D> normalizedPoints;
    private final List<Triangle3D> triangle3DList;

    private final DataBox dataBox;
    private final DelaunayPointAdapter adapter;

    public DelaunayMesh(List<Point3D> dataPoints) {
        this.dataPoints = dataPoints;
        dataBox = new DataBox(dataPoints);

        // normalize points according to coordinate system size
        normalizedPoints = new ArrayList<>(dataPoints.size());
        for (Point3D point : dataPoints) {
            double x = (point.getX() - dataBox.getMinX()) / dataBox.getSizeX();
            double y = (point.getY() - dataBox.getMinY()) / dataBox.getSizeY();
            double z = (point.getZ() - dataBox.getMinZ()) / dataBox.getSizeZ();
            normalizedPoints.add(new Point3D(x, y, z));
        }

        triangle3DList = new ArrayList<>(dataPoints.size() / 2);

        adapter = new DelaunayPointAdapter();

        // convert input for Delaunay algorithm
        List<Point> normalizedOldPoints = normalizedPoints.stream()
                .map(adapter::convertPoint3DtoDelaunay)
                .collect(Collectors.toList());

        // Do Delaunay triangulation
        List<Triangle> triangulation = new DelaunayTriangulation(normalizedOldPoints).getTriangulation();

        // convert output of Delaunay algorithm back to triangle objects
        triangle3DList.addAll(triangulation.stream()
                .filter(triangle -> ! triangle.isHalfplane())
                .map(t -> Triangle3D.of(
                        adapter.convertPointFromDelaunay(t.getA()),
                        adapter.convertPointFromDelaunay(t.getB()),
                        adapter.convertPointFromDelaunay(t.getC())))
                .collect(Collectors.toList()));
    }

    public List<Point3D> getDataPoints() {
        return dataPoints;
    }

    public List<Point3D> getNormalizedPoints() {
        return normalizedPoints;
    }

    public List<Triangle3D> getTriangle3DList() {
        return triangle3DList;
    }
}
