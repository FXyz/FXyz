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

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import static javafx.scene.input.MouseButton.MIDDLE;
import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseButton.SECONDARY;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;
import org.fxyz.cameras.AdvancedCamera;
import org.fxyz.extras.Transformable;
import org.fxyz.utils.AnimationPreference;

/**
 *
 * @author Dub
 */
public abstract class CameraController implements Transformable {

    public AdvancedCamera camera;
    private Scene scene;
    private SubScene subScene;
    private double previousX, previousY, speed = 1.0;
    private AnimationTimer timer;
    private Timeline timeline;
    private Transition transition;
    private boolean enable;
    private AnimationPreference animPref;

    public CameraController(boolean enableTransforms, AnimationPreference movementType) {
        enable = enableTransforms;
        animPref = movementType;
        switch (animPref) {
            case TIMELINE:
                timeline = new Timeline();
                timeline.setCycleCount(Animation.INDEFINITE);
                break;
            case TIMER:
                timer = new AnimationTimer() {
                    @Override
                    public void handle(long l) {
                        if (enable) {
                            initialize();
                            enable = false;
                        }
                        update();
                    }
                };
                break;
            case TRANSITION:
                transition = new Transition() {
                    {setCycleDuration(Duration.seconds(1));}
                    @Override
                    protected void interpolate(double frac) {
                        updateTransition(frac);
                    }
                };
                transition.setCycleCount(Animation.INDEFINITE);
                break;
            case ANIMATION:
                
                break;
        }

    }

    //Abstract Methods
    protected abstract void update(); // called each frame handle movement/ button clicks here

    protected abstract void updateTransition(double now);

    // Following methods should update values for use in update method etc...

    protected abstract void handleKeyEvent(KeyEvent event, boolean handle);

    protected abstract void handlePrimaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier);

    protected abstract void handleMiddleMouseDrag(MouseEvent event, Point2D dragDelta, double modifier);

    protected abstract void handleSecondaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier);

    protected abstract void handlePrimaryMouseClick(MouseEvent e);

    protected abstract void handleSecondaryMouseClick(MouseEvent e);

    protected abstract void handleMiddleMouseClick(MouseEvent e);

    protected abstract void handlePrimaryMousePress(MouseEvent e);

    protected abstract void handleSecondaryMousePress(MouseEvent e);

    protected abstract void handleMiddleMousePress(MouseEvent e);

    protected abstract void handleMouseMoved(MouseEvent event, Point2D moveDelta, double modifier);

    protected abstract void handleScrollEvent(ScrollEvent event);

    protected abstract double getSpeedModifier(KeyEvent event);

    //Self contained Methods
    private void handleKeyEvent(KeyEvent t) {
        if (t.getEventType() == KeyEvent.KEY_PRESSED) {
            handleKeyEvent(t, true);
        } else if (t.getEventType() == KeyEvent.KEY_RELEASED) {
            handleKeyEvent(t, true);
        }
        speed = getSpeedModifier(t);
    }

    private void handleMouseEvent(MouseEvent t) {

        if (t.getEventType() == MouseEvent.MOUSE_PRESSED) {
            switch (t.getButton()) {
                case PRIMARY:
                    handlePrimaryMousePress(t);
                    break;
                case MIDDLE:
                    handleMiddleMousePress(t);
                    break;
                case SECONDARY:
                    handleSecondaryMousePress(t);
                    break;
                default:
                    throw new AssertionError();
            }
            handleMousePress(t);
        } else if (t.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            Point2D d = getMouseDelta(t);

            switch (t.getButton()) {
                case PRIMARY:
                    handlePrimaryMouseDrag(t, d, speed);
                    break;
                case MIDDLE:
                    handleMiddleMouseDrag(t, d, speed);
                    break;
                case SECONDARY:
                    handleSecondaryMouseDrag(t, d, speed);
                    break;
                default:
                    throw new AssertionError();
            }
        } else if (t.getEventType() == MouseEvent.MOUSE_MOVED) {
            handleMouseMoved(t, getMouseDelta(t), speed);
        } else if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
            switch (t.getButton()) {
                case PRIMARY:
                    handlePrimaryMouseClick(t);
                    break;
                case MIDDLE:
                    handleMiddleMouseClick(t);
                    break;
                case SECONDARY:
                    handleSecondaryMouseClick(t);
                    break;
                default:
                    throw new AssertionError();
            }
        }
    }

    private void setEventHandlers(Scene scene) {
        scene.addEventHandler(KeyEvent.ANY, k -> handleKeyEvent(k));
        scene.addEventHandler(MouseEvent.ANY, m -> handleMouseEvent(m));
        scene.addEventHandler(ScrollEvent.ANY, s -> handleScrollEvent(s));
    }

    private void setEventHandlers(SubScene scene) {
        scene.addEventHandler(KeyEvent.ANY, k -> handleKeyEvent(k));
        scene.addEventHandler(MouseEvent.ANY, m -> handleMouseEvent(m));
        scene.addEventHandler(ScrollEvent.ANY, s -> handleScrollEvent(s));
    }

    private void handleMousePress(MouseEvent event) {
        previousX = event.getSceneX();
        previousY = event.getSceneY();
        event.consume();
    }

    private Point2D getMouseDelta(MouseEvent event) {
        Point2D res = new Point2D(event.getSceneX() - previousX, event.getSceneY() - previousY);
        previousX = event.getSceneX();
        previousY = event.getSceneY();

        return res;
    }

    public AdvancedCamera getCamera() {
        return camera;
    }

    public void setCamera(AdvancedCamera camera) {
        this.camera = camera;
        switch (animPref) {
            case TIMELINE:
                timeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.millis(15), e -> {
                            new Timeline(new KeyFrame[]{
                                new KeyFrame(Duration.ONE, ev ->{
                                    update();
                                })
                            }).play();
                        })
                );
                timeline.play();                
                break;
            case TIMER:
                timer.start();
                break;
            case TRANSITION:
                transition.play();
                break;
            case ANIMATION:
                break;
        }
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        setEventHandlers(scene);
    }

    public void setSubScene(SubScene subScene) {
        this.subScene = subScene;
        setEventHandlers(subScene);
    }

    protected Scene getScene() {
        return scene;
    }

    protected SubScene getSubScene() {
        return subScene;
    }

    @Override
    public RotateOrder getRotateOrder() {
        return RotateOrder.USE_AFFINE;
    }

}
