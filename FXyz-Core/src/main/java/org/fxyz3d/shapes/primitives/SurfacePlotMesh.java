/**
 * SurfacePlotMesh.java
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
package org.fxyz3d.shapes.primitives;

import java.util.Arrays;
import java.util.function.Function;

import org.fxyz3d.geometry.Face3;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.shapes.polygon.PolygonMesh;

/**
 * SurfacePlotMesh to plot 2D functions z = f(x,y)
 */
public class SurfacePlotMesh extends TexturedMesh {

    private static final Function<Point2D,Number> DEFAULT_FUNCTION = p->Math.sin(p.magnitude())/p.magnitude();

    private static final double DEFAULT_X_RANGE = 10; // -5 +5
    private static final double DEFAULT_Y_RANGE = 10; // -5 +5
    private static final int DEFAULT_X_DIVISIONS = 64;
    private static final int DEFAULT_Y_DIVISIONS = 64;
    private static final double DEFAULT_FUNCTION_SCALE = 1.0D;

    private PolygonMesh polygonMesh;

    public SurfacePlotMesh() {
        this(DEFAULT_FUNCTION,DEFAULT_X_RANGE,DEFAULT_Y_RANGE,DEFAULT_X_DIVISIONS, DEFAULT_Y_DIVISIONS,DEFAULT_FUNCTION_SCALE);
    }

    public SurfacePlotMesh(Function<Point2D,Number> function) {
        this(function,DEFAULT_X_RANGE,DEFAULT_Y_RANGE,DEFAULT_X_DIVISIONS, DEFAULT_Y_DIVISIONS,DEFAULT_FUNCTION_SCALE);
    }

    public SurfacePlotMesh(Function<Point2D,Number> function, double rangeX, double rangeY) {
        this(function,rangeX,rangeY,DEFAULT_X_DIVISIONS, DEFAULT_Y_DIVISIONS,DEFAULT_FUNCTION_SCALE);
    }

    public SurfacePlotMesh(Function<Point2D,Number> function, double rangeX, double rangeY, double functionScale) {
        this(function,rangeX,rangeY,DEFAULT_X_DIVISIONS, DEFAULT_Y_DIVISIONS,functionScale);
    }

    public SurfacePlotMesh(Function<Point2D,Number> function, double rangeX, double rangeY, int divisionsX, int divisionsY, double functionScale) {
        setFunction2D(function);
        setRangeX(rangeX);
        setRangeY(rangeY);
        setDivisionsX(divisionsX);
        setDivisionsY(divisionsY);
        setFunctionScale(functionScale);

        updateMesh();
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
    }

    @Override
    protected final void updateMesh(){
        setMesh(null);
        mesh=createPlotMesh(
                getFunction2D(),
                getRangeX(),getRangeY(),
                getDivisionsX(),getDivisionsY(),
                getFunctionScale());
        polygonMesh=createPolygonMesh(
                getFunction2D(),
                getRangeX(),getRangeY(),
                getDivisionsX(),getDivisionsY(),
                getFunctionScale());
        setMesh(mesh);
    }

    private final ObjectProperty<Function<Point2D, Number>> function2D = new SimpleObjectProperty<Function<Point2D, Number>>(DEFAULT_FUNCTION){
        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public Function<Point2D, Number> getFunction2D() {
        return function2D.get();
    }

    public final void setFunction2D(Function<Point2D, Number> value) {
        function2D.set(value);
    }

    public ObjectProperty function2DProperty() {
        return function2D;
    }

    private final DoubleProperty rangeX = new SimpleDoubleProperty(DEFAULT_X_RANGE){
        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public double getRangeX() {
        return rangeX.get();
    }

    public final void setRangeX(double value) {
        rangeX.set(value);
    }

    public DoubleProperty rangeXProperty() {
        return rangeX;
    }

    private final DoubleProperty rangeY = new SimpleDoubleProperty(DEFAULT_Y_RANGE){
        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public double getRangeY() {
        return rangeY.get();
    }

    public final void setRangeY(double value) {
        rangeY.set(value);
    }

    public DoubleProperty rangeYProperty() {
        return rangeY;
    }

    private final IntegerProperty divisionsX = new SimpleIntegerProperty(DEFAULT_X_DIVISIONS){
        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public int getDivisionsX() {
        return divisionsX.get();
    }

    public final void setDivisionsX(int value) {
        divisionsX.set(value);
    }

    public IntegerProperty divisionsXProperty() {
        return divisionsX;
    }

    private final IntegerProperty divisionsY = new SimpleIntegerProperty(DEFAULT_Y_DIVISIONS){
        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public int getDivisionsY() {
        return divisionsY.get();
    }

    public final void setDivisionsY(int value) {
        divisionsY.set(value);
    }

    public IntegerProperty divisionsYProperty() {
        return divisionsY;
    }
    private final DoubleProperty functionScale = new SimpleDoubleProperty(DEFAULT_FUNCTION_SCALE) {
        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public double getFunctionScale() {
        return functionScale.get();
    }

    public final void setFunctionScale(double value) {
        functionScale.set(value);
    }

    public DoubleProperty functionScaleProperty() {
        return functionScale;
    }

    public PolygonMesh getPolygonMesh() {
        return polygonMesh;
    }

    private TriangleMesh createPlotMesh(Function<Point2D,Number> function2D, double rangeX, double rangeY, int divisionsX, int divisionsY, double scale) {

        listVertices.clear();
        listTextures.clear();
        listFaces.clear();

        int numDivX = divisionsX + 1;
        float pointY;

        areaMesh.setWidth(rangeX);
        areaMesh.setHeight(rangeY);

        // Create points
        for (int y = 0; y <= divisionsY; y++) {
            float dy = (float)(-rangeY/2d + ((float)y /(float)divisionsY)*rangeY);
            for (int x = 0; x <= divisionsX; x++) {
                float dx = (float)(-rangeX/2d + ((float)x /(float)divisionsX)*rangeX);
                pointY = (float)scale*function2D.apply(new Point2D(dx,dy)).floatValue();
                listVertices.add(new Point3D(dx, pointY, dy));
            }
        }
        // Create texture coordinates
        createTexCoords(divisionsX,divisionsY);

        // Create textures indices
        for (int y = 0; y < divisionsY; y++) {
            for (int x = 0; x < divisionsX; x++) {
                int p00 = y * numDivX + x;
                int p01 = p00 + 1;
                int p10 = p00 + numDivX;
                int p11 = p10 + 1;
                listTextures.add(new Face3(p00,p10,p11));
                listTextures.add(new Face3(p11,p01,p00));
            }
        }
        // Create faces indices
        for (int y = 0; y < divisionsY; y++) {
            for (int x = 0; x < divisionsX; x++) {
                int p00 = y * numDivX + x;
                int p01 = p00 + 1;
                int p10 = p00 + numDivX;
                int p11 = p10 + 1;
                listFaces.add(new Face3(p00,p10,p11));
                listFaces.add(new Face3(p11,p01,p00));
            }
        }
        return createMesh();
    }

    private PolygonMesh createPolygonMesh(Function<Point2D,Number> function2D, double rangeX, double rangeY, int divisionsX, int divisionsY, double scale) {

        int numDivX = divisionsX + 1;
        float pointY;

        // Create points
        float[] points = new float[(divisionsY + 1) * numDivX * 3];
        int counter = 0;
        for (int y = 0; y <= divisionsY; y++) {
            float dy = (float)(-rangeY/2d + ((float)y /(float)divisionsY)*rangeY);
            for (int x = 0; x <= divisionsX; x++) {
                float dx = (float)(-rangeX/2d + ((float)x /(float)divisionsX)*rangeX);
                pointY = 1.0f*(float)scale*function2D.apply(new Point2D(dx,dy)).floatValue();
                points[counter++] = dx;
                points[counter++] = pointY;
                points[counter++] = dy;
            }
        }

        // Create faces indices
        int[][] faces = new int[divisionsY * divisionsX][8];
        counter = 0;
        for (int y = 0; y < divisionsY; y++) {
            for (int x = 0; x < divisionsX; x++) {
                int p00 = y * numDivX + x;
                int p01 = p00 + 1;
                int p10 = p00 + numDivX;
                int p11 = p10 + 1;
                faces[counter][0] = p00;
                faces[counter][2] = p10;
                faces[counter][4] = p11;
                faces[counter][6]= p01;
                counter++;
            }
        }

        PolygonMesh mesh = new PolygonMesh(points, new float[]{0, 0}, faces);
        int[] faceSmoothingGroups = new int[faces.length]; // 0 == hard edges
        Arrays.fill(faceSmoothingGroups, 1);
        mesh.getFaceSmoothingGroups().addAll(faceSmoothingGroups);
        return mesh;
    }


}
