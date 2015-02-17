/**
* Transformable.java
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

package org.fxyz.utils;

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
