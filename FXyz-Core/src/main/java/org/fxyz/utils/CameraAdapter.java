/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.utils;

import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.transform.Transform;
import javafx.util.Callback;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public interface CameraAdapter {
    public Camera getCamera();
    
    /*==========================================================================
     Callbacks    
       | R | Up| F |  | P|
     U |mxx|mxy|mxz|  |tx|
     V |myx|myy|myz|  |ty|
     N |mzx|mzy|mzz|  |tz|
    
     */
    //Forward / look direction    
    Callback<Transform, Point3D> F = (a) -> {
        return new Point3D(a.getMzx(), a.getMzy(), a.getMzz());
    };
    Callback<Transform, Point3D> N = (a) -> {
        return new Point3D(a.getMxz(), a.getMyz(), a.getMzz());
    };
    // up direction
    Callback<Transform, Point3D> UP = (a) -> {
        return new Point3D(a.getMyx(), a.getMyy(), a.getMyz());
    };
    Callback<Transform, Point3D> V = (a) -> {
        return new Point3D(a.getMxy(), a.getMyy(), a.getMzy());
    };
    // right direction
    Callback<Transform, Point3D> R = (a) -> {
        return new Point3D(a.getMxx(), a.getMxy(), a.getMxz());
    };
    Callback<Transform, Point3D> U = (a) -> {
        return new Point3D(a.getMxx(), a.getMyx(), a.getMzx());
    };
    //position
    Callback<Transform, Point3D> P = (a) -> {
        return new Point3D(a.getTx(), a.getTy(), a.getTz());
    };

    default Point3D getCameraForwardVectorColumn() {
        return F.call(getCamera().getLocalToSceneTransform());
    }

    default Point3D getCameraForwardVectorRow() {
        return N.call(getCamera().getLocalToSceneTransform());
    }

    default Point3D getCameraRightVectorColumn() {
        return R.call(getCamera().getLocalToSceneTransform());
    }

    default Point3D getCameraRightVectorRow() {
        return U.call(getCamera().getLocalToSceneTransform());
    }

    default Point3D getCameraUpVectorColumn() {
        return UP.call(getCamera().getLocalToSceneTransform());
    }

    default Point3D getCameraUpVectorRow() {
        return V.call(getCamera().getLocalToSceneTransform());
    }

    default Point3D getCameraScenePosition() {
        return P.call(getCamera().getLocalToSceneTransform());
    }
}
