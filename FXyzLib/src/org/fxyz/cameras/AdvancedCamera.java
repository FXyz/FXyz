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

package org.fxyz.cameras;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import org.fxyz.cameras.controllers.CameraController;

/**
 *
 * @author Dub
 */
public class AdvancedCamera extends PerspectiveCamera {

    // Wrapper for "World" movement and lighting
    private final Group wrapper = new Group();
    private final PointLight headLight = new PointLight();
    private final AmbientLight ambientLight = new AmbientLight();
    
    private CameraController controller;

    public AdvancedCamera() {
        super(true);
        setNearClip(0.1);
        setFarClip(10000);
        setFieldOfView(42);
        //setVerticalFieldOfView(true);
        
        ambientLight.setLightOn(false);
        wrapper.getChildren().addAll(AdvancedCamera.this, headLight, ambientLight);
        
    }

    public CameraController getController() {
        return controller;
    }

    public void setController(CameraController controller) {
        controller.setCamera(this);
        this.controller = controller;        
    }

    public Group getWrapper() {
        return wrapper;
    }

    public PointLight getHeadLight() {
        return headLight;
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    
    
}
