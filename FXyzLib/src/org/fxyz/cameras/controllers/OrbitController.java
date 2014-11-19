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
package org.fxyz.cameras.controllers;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.fxyz.utils.AnimationPreference;

/**
 *
 * @author Dub
 */
public class OrbitController extends CameraController{

    public OrbitController() {
        super(true, AnimationPreference.TRANSITION);
    }

    @Override
    protected void update() {
    }

    @Override
    protected void handleKeyEvent(KeyEvent event, boolean handle) {
    }

    @Override
    protected void handlePrimaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
    }

    @Override
    protected void handleMiddleMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
    }

    @Override
    protected void handleSecondaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
    }

    @Override
    protected void handlePrimaryMouseClick(MouseEvent e) {
    }

    @Override
    protected void handleSecondaryMouseClick(MouseEvent e) {
    }

    @Override
    protected void handleMiddleMouseClick(MouseEvent e) {
    }

    @Override
    protected void handlePrimaryMousePress(MouseEvent e) {
    }

    @Override
    protected void handleSecondaryMousePress(MouseEvent e) {
    }

    @Override
    protected void handleMiddleMousePress(MouseEvent e) {
    }

    @Override
    protected void handleMouseMoved(MouseEvent event, Point2D moveDelta, double modifier) {
    }

    @Override
    protected void handleScrollEvent(ScrollEvent event) {
    }

    @Override
    protected double getSpeedModifier(KeyEvent event) {
        return 0;
    }

    @Override
    public Node getTransformableNode() {
        return null;
    }

    @Override
    protected void updateTransition(double now) {
    }
    
}
