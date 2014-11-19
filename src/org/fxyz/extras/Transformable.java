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
package org.fxyz.extras;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Callback;
import org.fxyz.geometry.Vector3D;

/**
 * An Interface implementation of Xform found in the Molecule Sample
 * 
 * @author Dub
 * @param <T> Node type to be used
 */
public interface Transformable<T extends Node> {

    public enum RotateOrder {
        XYZ,
        XZY,
        YXZ,
        YZX,
        ZXY,
        ZYX,
        USE_AFFINE;

        private RotateOrder() {
        }
    }

    // Simple Transforms
    
    //Rotates
    public Rotate 
            rotateX = new Rotate(0.0, Rotate.X_AXIS),
            rotateY = new Rotate(0.0, Rotate.Y_AXIS),
            rotateZ = new Rotate(0.0, Rotate.Z_AXIS);
    
    public default void setRotate(double x, double y, double z) {
        rotateX.setAngle(x);
        rotateY.setAngle(y);
        rotateZ.setAngle(z);
    }    
    public default void setRotateX(double x) { rotateX.setAngle(x); }
    public default void setRotateY(double y) { rotateY.setAngle(y); }
    public default void setRotateZ(double z) { rotateZ.setAngle(z); }
    
    // Translates
    public Translate 
            t = new Translate(),
            p = new Translate(),
            ip = new Translate();
    
    public default void setTx(double x) { t.setX(x); }
    public default void setTy(double y) { t.setY(y); }
    public default void setTz(double z) { t.setZ(z); }
    public default double getTx() { return t.getX(); }
    public default double getTy() { return t.getY(); }
    public default double getTz() { return t.getZ(); }
    
    // Scale
    public Scale s = new Scale();
    public default void setScale(double scaleFactor) {
        s.setX(scaleFactor);
        s.setY(scaleFactor);
        s.setZ(scaleFactor);
    }
    public default void setScale(double x, double y, double z) {
        s.setX(x);
        s.setY(y);
        s.setZ(z);
    }
    // Transform methods
    public default void setPivot(double x, double y, double z) {
        p.setX(x);
        p.setY(y);
        p.setZ(z);
        ip.setX(-x);
        ip.setY(-y);
        ip.setZ(-z);
    }
    
    //advanced transform
    public Affine affine = new Affine();
    
    //Vectors: fwd, right, up   Point3D: pos
    
    //Forward / look direction    
    Callback<Transform, Vector3D> forwardDirCallback = (a) -> {
        return new Vector3D(a.getMzx(), a.getMzy(), a.getMzz());
    };
    Callback<Transform, Vector3D> forwardMatrixRowCallback = (a) -> {
        return new Vector3D(a.getMxz(), a.getMyz(), a.getMzz());
    };
    // up direction
    Callback<Transform, Vector3D> upDirCallback = (a) -> {
        return new Vector3D(a.getMyx(), a.getMyy(), a.getMyz());
    };
    Callback<Transform, Vector3D> upMatrixRowCallback = (a) -> {
        return new Vector3D(a.getMxy(), a.getMyy(), a.getMzy());
    };
    // right direction
    Callback<Transform, Vector3D> rightDirCallback = (a) -> {
        return new Vector3D(a.getMxx(), a.getMxy(), a.getMxz());
    };
    Callback<Transform, Vector3D> rightMatrixRowCallback = (a) -> {         
        return new Vector3D(a.getMxx(), a.getMyx(), a.getMzx());
    };
    //position
    Callback<Transform, Point3D> positionCallback = (a) ->{
        return new Point3D(a.getTx(), a.getTy(), a.getTz());
    };
    
    default Vector3D getForwardDirection(){
        return forwardDirCallback.call(getTransformableNode().getLocalToSceneTransform());
    }
    default Vector3D getForwardMatrixRow(){
        return forwardMatrixRowCallback.call(getTransformableNode().getLocalToSceneTransform());
    }
    default Vector3D getRightDirection(){
        return rightDirCallback.call(getTransformableNode().getLocalToSceneTransform());
    }
    default Vector3D getRightMatrixRow(){
        return rightMatrixRowCallback.call(getTransformableNode().getLocalToSceneTransform());
    }
    default Vector3D getUpDirection(){
        return upDirCallback.call(getTransformableNode().getLocalToSceneTransform());
    }
    default Vector3D getUpMatrixRow(){
        return upMatrixRowCallback.call(getTransformableNode().getLocalToSceneTransform());
    }
    default Point3D getPosition(){
        return positionCallback.call(getTransformableNode().getLocalToSceneTransform());
    }
    
    public default void reset() {
        t.setX(0.0);
        t.setY(0.0);
        t.setZ(0.0);
        rotateX.setAngle(0.0);
        rotateY.setAngle(0.0);
        rotateZ.setAngle(0.0);
        s.setX(1.0);
        s.setY(1.0);
        s.setZ(1.0);
        p.setX(0.0);
        p.setY(0.0);
        p.setZ(0.0);
        ip.setX(0.0);
        ip.setY(0.0);
        ip.setZ(0.0);
        
        affine.setMxx(1);
        affine.setMxy(0);
        affine.setMxz(0);
        
        affine.setMyx(0);
        affine.setMyy(1);
        affine.setMyz(0);
        
        affine.setMzx(0);
        affine.setMzy(0);
        affine.setMzz(1);
    }

    public default void resetTSP() {
        t.setX(0.0);
        t.setY(0.0);
        t.setZ(0.0);
        s.setX(1.0);
        s.setY(1.0);
        s.setZ(1.0);
        p.setX(0.0);
        p.setY(0.0);
        p.setZ(0.0);
        ip.setX(0.0);
        ip.setY(0.0);
        ip.setZ(0.0);
    }

    public default void debug() {
        System.out.println("t = (" +
                           t.getX() + ", " +
                           t.getY() + ", " +
                           t.getZ() + ")  " +
                           "r = (" +
                           rotateX.getAngle() + ", " +
                           rotateY.getAngle() + ", " +
                           rotateZ.getAngle() + ")  " +
                           "s = (" +
                           s.getX() + ", " + 
                           s.getY() + ", " + 
                           s.getZ() + ")  " +
                           "p = (" +
                           p.getX() + ", " + 
                           p.getY() + ", " + 
                           p.getZ() + ")  " +
                           "ip = (" +
                           ip.getX() + ", " + 
                           ip.getY() + ", " + 
                           ip.getZ() + ")" +
                           "affine = " + affine);        
    }
    
    
    /**
     * Toggle Transforms on / off
     * @param b 
     */
    default void enableTransforms(boolean b) {
        // if true, check if node is a camera
        if (b) {
            if (getRotateOrder() != null) {
                    switch (getRotateOrder()) {
                        case XYZ:
                            getTransformableNode().getTransforms().addAll(t, p, rotateZ, rotateY, rotateX, s, ip);
                            break;
                        case XZY:
                            getTransformableNode().getTransforms().addAll(t, p, rotateY, rotateZ, rotateX, s, ip);
                            break;
                        case YXZ:
                            getTransformableNode().getTransforms().addAll(t, p, rotateZ, rotateX, rotateY, s, ip);
                            break;
                        case YZX:
                            getTransformableNode().getTransforms().addAll(t, p, rotateX, rotateZ, rotateY, s, ip);
                            break;
                        case ZXY:
                            getTransformableNode().getTransforms().addAll(t, p, rotateY, rotateX, rotateZ, s, ip);
                            break;
                        case ZYX:
                            getTransformableNode().getTransforms().addAll(t, p, rotateX, rotateY, rotateZ, s, ip);
                            break;
                        case USE_AFFINE:
                            getTransformableNode().getTransforms().addAll(affine);
                            break;
                    }
                
            }
        // if false clear transforms from Node.    
        } else if(!b){
            getTransformableNode().getTransforms().clear();
            reset();
        }
    }
    
    public default void initialize(){
        if(getTransformableNode() != null){
            enableTransforms(true);
        }
    }

    public T getTransformableNode();
    public RotateOrder getRotateOrder();
}
