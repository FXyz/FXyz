/**
* CameraView.java
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

package org.fxyz.scene;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.fxyz.utils.CameraTransformer;

/**
 *  This class is based on "AnotherView.java" provided by: 
 *
 * Date: 2013/10/31  
 * 
 * Author:
 * August Lammersdorf, InteractiveMesh e.K.
 * Hauptstraï¿½e 28d, 85737 Ismaning
 * Germany / Munich Area
 * www.InteractiveMesh.com/org
 *
 * Please create your own implementation.
 * This source code is provided "AS IS", without warranty of any kind.
 * You are allowed to copy and use all lines you like of this source code
 * without any copyright notice,
 * but you may not modify, compile, or distribute this 'AnotherView.java'.
 * 
 * 
 * Following changes were made:
 *  replaced Affine with standard Rotate transforms for Camera rx, ry, rz with first person controls.
 *  extended ImageView directly (rather than nested node).
 *  changed constructors to accept a SubScene, or Group, and/or specified PerspectiveCamera ***ToDo
 *  
 * 
 * @author Dub
 */
public final class CameraView extends ImageView {
    
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    private final SnapshotParameters params = new SnapshotParameters();
    private WritableImage image = null;    

    private double startX = 0;
    private double startY = 0;

    private CameraTransformer cameraTransform = new CameraTransformer();
    private PerspectiveCamera camera;
    private Rotate rx = new Rotate(0,0,0,0,Rotate.X_AXIS),
            ry = new Rotate(0,0,0,0,Rotate.X_AXIS),
            rz = new Rotate(0,0,0,0,Rotate.X_AXIS);
    private Translate t = new Translate(0,0,0);
    
    private Group worldToView;

    private AnimationTimer viewTimer = null;
    

    public CameraView(SubScene scene) {
        // Make sure "world" is a group
        assert scene.getRoot().getClass().equals(Group.class);
        
        worldToView = (Group)scene.getRoot();
               
        camera = new PerspectiveCamera(true);
//        cameraTransform.setTranslate(0, 0, -500);
        cameraTransform.getChildren().add(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(15000.0);
        camera.setTranslateZ(-1500);
//        cameraTransform.ry.setAngle(-45.0);
//        cameraTransform.rx.setAngle(-10.0);

        params.setCamera(camera);
        
        params.setDepthBuffer(true);
        params.setFill(Color.rgb(0, 0, 0, 0.5));

        viewTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                redraw();
            }
        };
    }

    public void startViewing() {
        viewTimer.start();
    }

    public void pause() {
        viewTimer.stop();
    }

    public void setFirstPersonNavigationEabled(boolean b) {
        if (b) {
            // Navigation
            setMouseTransparent(false);
            
            //First person shooter keyboard movement
        setOnKeyPressed(event -> {
            double change = 10.0;
            //Add shift modifier to simulate "Running Speed"
            if(event.isShiftDown()) { change = 50.0; }
            //What key did the user press?
            KeyCode keycode = event.getCode();
            //Step 2c: Add Zoom controls
            if(keycode == KeyCode.W) { camera.setTranslateZ(camera.getTranslateZ() + change); }
            if(keycode == KeyCode.S) { camera.setTranslateZ(camera.getTranslateZ() - change); }
            //Step 2d: Add Strafe controls
            if(keycode == KeyCode.A) { camera.setTranslateX(camera.getTranslateX() - change); }
            if(keycode == KeyCode.D) { camera.setTranslateX(camera.getTranslateX() + change); }            
        });
        
        setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
            
        });
        setOnMouseDragged((MouseEvent me) -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);
            
            double modifier = 10.0;
            double modifierFactor = 0.1;
            
            if (me.isControlDown()) {
                modifier = 0.1;
            }
            if (me.isShiftDown()) {
                modifier = 50.0;
            }
            if (me.isPrimaryButtonDown()) {
                cameraTransform.ry.setAngle(((cameraTransform.ry.getAngle() + mouseDeltaX * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // +
                cameraTransform.rx.setAngle(((cameraTransform.rx.getAngle() - mouseDeltaY * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // -                
            } else if (me.isSecondaryButtonDown()) {
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX * modifierFactor * modifier;
                camera.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown() ) {
                cameraTransform.t.setX(cameraTransform.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3); // -
                cameraTransform.t.setY(cameraTransform.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3); // -
                
            }
        });
            
        }else{
            setOnMouseDragged(null);
            setOnScroll(null);
            setOnMousePressed(null);
            setOnKeyPressed(null);
            setMouseTransparent(true);
        }
    }

    private void redraw() {

        params.setViewport(new Rectangle2D(0, 0, getFitWidth(), getFitHeight()));
        if (image == null
                || image.getWidth() != getFitWidth() || image.getHeight() != getFitHeight()) {
            image = worldToView.snapshot(params, null);
        } else {
            worldToView.snapshot(params, image);
        }
        setImage(image);
    }

    
    public PerspectiveCamera getCamera() {
        return camera;
    }

    public Rotate getRx() {
        return rx;
    }

    public Rotate getRy() {
        return ry;
    }

    public Rotate getRz() {
        return rz;
    }

    public Translate getT() {
        return t;
    }

}
