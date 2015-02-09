package org.fxyz.shapes.primitives;

import java.util.function.Function;
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
import org.fxyz.geometry.Face3;
import org.fxyz.geometry.Point3D;

/**
 * SurfacePlotMesh to plot 2D functions z = f(x,y)
 */
public class SurfacePlotMesh extends TexturedMesh {

    private static final Function<Point2D,Number> DEFAULT_FUNCTION = p->Math.sin(p.magnitude())/p.magnitude();
    
    private static final double DEFAULT_X_RANGE = 10; // -5 +5
    private static final double DEFAULT_Y_RANGE = 10; // -5 +5
    private static final int DEFAULT_X_DIVISIONS = 64;
    private static final int DEFAULT_Y_DIVISIONS = 64;
    private static final double DEFAULT_SCALE = 1.0D;
    
    public SurfacePlotMesh() {
        this(DEFAULT_FUNCTION,DEFAULT_X_RANGE,DEFAULT_Y_RANGE,DEFAULT_X_DIVISIONS, DEFAULT_Y_DIVISIONS,DEFAULT_SCALE);
    }

    public SurfacePlotMesh(Function<Point2D,Number> function) {
        this(function,DEFAULT_X_RANGE,DEFAULT_Y_RANGE,DEFAULT_X_DIVISIONS, DEFAULT_Y_DIVISIONS,DEFAULT_SCALE);
    }

    public SurfacePlotMesh(Function<Point2D,Number> function, double rangeX, double rangeY) {
        this(function,rangeX,rangeY,DEFAULT_X_DIVISIONS, DEFAULT_Y_DIVISIONS,DEFAULT_SCALE);
    }

    public SurfacePlotMesh(Function<Point2D,Number> function, double rangeX, double rangeY, double scale) {
        this(function,rangeX,rangeY,DEFAULT_X_DIVISIONS, DEFAULT_Y_DIVISIONS,scale);
    }

    public SurfacePlotMesh(Function<Point2D,Number> function, double rangeX, double rangeY, int divisionsX, int divisionsY, double scale) {
        setFunction2D(function);
        setRangeX(rangeX);
        setRangeY(rangeY);
        setDivisionsX(divisionsX);
        setDivisionsY(divisionsY);
        setScale(scale);
        
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
            getScale());
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

    public Function getFunction2D() {
        return function2D.get();
    }

    public final void setFunction2D(Function value) {
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
    private final DoubleProperty scale = new SimpleDoubleProperty(DEFAULT_SCALE){
        @Override
        protected void invalidated() {
            if(mesh!=null){
                updateMesh();
            }
        }
    };

    public double getScale() {
        return scale.get();
    }

    public final void setScale(double value) {
        scale.set(value);
    }

    public DoubleProperty scaleProperty() {
        return scale;
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


}
