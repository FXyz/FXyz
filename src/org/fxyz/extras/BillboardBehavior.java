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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import org.fxyz.geometry.Vector3D;

/**
 * Basic interface for Billboard Nodes. 
 * ie: Keeps this Node oriented (looks at) towards specified Node.
 * 
 * @author jdub1581
 * @param <T> Type of node to be used for (this) "Billboard".
 */
public interface BillboardBehavior<T extends Node>{
    /**
     * Spherical means object will look at other on all axes
     * Cylindrical means object will rotate on Y axis only
     */
    public enum BillboardMode{
        SPHERICAL, 
        CYLINDRICAL;
    }
    
    
    static BillboardTimer timer = new BillboardTimer();
    /**
     * 
     * @return The node to be used for this behavior.
     */
    public T getBillboardNode();    
    /**
     * 
     * @return The node to look at.
     */
    public Node getOther();
      
    
    public Affine affine = new Affine();  
    /**
     *  Adds the Affine transform to Node and starts timer.
     */
    default void startBillboardBehavior(){
        if(timer.getUpdateList().isEmpty()){
            timer.addUpdate(() -> {
                updateMatrix();
                return null;
            });
        }
        getBillboardNode().getTransforms().addAll(affine);
        timer.start();
    }
    /**
     *  Stops timer and removes transform 
     */
    default void stopBillboardBehavior(){
        timer.stop();
        getBillboardNode().getTransforms().clear();
    }
    /**
     * Updates the transformation matrix.
     * can change the Translate for fixed distance  
     */
    default void updateMatrix(){
        Transform cam  = getOther().getLocalToSceneTransform(),
                  self = getBillboardNode().getLocalToSceneTransform();         
                
        Bounds b;
        double cX,
               cY,
               cZ;
        
        if(!(getBillboardNode() instanceof Shape3D)){
            b = getBillboardNode().getBoundsInLocal();
                cX = b.getWidth() / 2;
                cY = b.getHeight() / 2;
                cZ = b.getDepth() / 2;            
        }else{
            cX = self.getTx();
            cY = self.getTy();
            cZ = self.getTz();
        }       
        
        Point3D camPos = new Point3D(cam.getTx(), cam.getTy(), cam.getTz());
                Point3D selfPos = new Point3D(cX, cY, cZ);
                
        Vector3D up = Vector3D.UP,
        forward = new Vector3D(
                (selfPos.getX()) - camPos.getX(),
                (selfPos.getY()) - camPos.getY(),
                (selfPos.getZ()) - camPos.getZ()
        ).toNormal(),
        right = up.crossProduct(forward).toNormal();
        up = forward.crossProduct(right).toNormal();
                
        switch(getBillboardMode()){
            
            case SPHERICAL:                  
                affine.setMxx(right.x); affine.setMxy(up.x); affine.setMzx(forward.x); 
                affine.setMyx(right.y); affine.setMyy(up.y); affine.setMzy(forward.y); 
                affine.setMzx(right.z); affine.setMzy(up.z); affine.setMzz(forward.z);
        
                affine.setTx(cX * (1 - affine.getMxx()) - cY * affine.getMxy() - cZ * affine.getMxz());
                affine.setTy(cY * (1 - affine.getMyy()) - cX * affine.getMyx() - cZ * affine.getMyz());
                affine.setTz(cZ * (1 - affine.getMzz()) - cX * affine.getMzx() - cY * affine.getMzy());                
                break;
                
            case CYLINDRICAL:                
                affine.setMxx(right.x); affine.setMxy(0); affine.setMzx(forward.x); 
                affine.setMyx(0);       affine.setMyy(1); affine.setMzy(0); 
                affine.setMzx(right.z); affine.setMzy(0); affine.setMzz(forward.z);
                                
                affine.setTx(cX * (1 - affine.getMxx()) - cY * affine.getMxy() - cZ * affine.getMxz());
                affine.setTy(cY * (1 - affine.getMyy()) - cX * affine.getMyx() - cZ * affine.getMyz());
                affine.setTz(cZ * (1 - affine.getMzz()) - cX * affine.getMzx() - cY * affine.getMzy());
                break;
        }
        
    }
    
    ObjectProperty<BillboardMode> mode = new SimpleObjectProperty<>(BillboardMode.SPHERICAL);
    default BillboardMode getBillboardMode(){
        return mode.get();
    }
    default void setBillboardMode(BillboardMode m){
        mode.set(m);
    }
    default ObjectProperty<BillboardMode> getBillboardModeProperty(){
        return mode;
    }
}
