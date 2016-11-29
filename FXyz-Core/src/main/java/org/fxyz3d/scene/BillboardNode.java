/**
 * BillboardNode.java
 *
 * Copyright (c) 2013-2016, F(X)yz
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

package org.fxyz3d.scene;

import java.util.Collection;
import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 * @param <T>
 */
public abstract class BillboardNode<T extends Node> extends Group {

    public enum BillboardMode {

        SPHERICAL,
        CYLINDRICAL;
    }

    protected abstract T getBillboardNode();

    protected abstract Node getTarget();

    private final Affine affine;
    private final AnimationTimer timer;

    public BillboardNode() {
        affine = new Affine();
        getTransforms().add(affine);
        
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateMatrix();
                //System.out.println("Updated Matrix " + now / 1000000000);
            }
        };
    }

    private BillboardNode(Node... children) {
        throw new UnsupportedOperationException(" not supported yet.");
    }

    private BillboardNode(Collection<Node> children) {
        throw new UnsupportedOperationException(" not supported yet.");
    }

    public final void startBillboarding() {
        timer.start();
    }

    public final void stopBillboarding() {
        timer.stop();
    }

    private void updateMatrix() {
        Transform cam = getTarget().getLocalToSceneTransform(),
                self = getBillboardNode().getLocalToSceneTransform();
        if (cam != null && self != null) {
            Bounds b;
            double cX,
                    cY,
                    cZ;

            if (!(getBillboardNode() instanceof Shape3D)) {
                b = getBillboardNode().getBoundsInLocal();
                cX = b.getWidth() / 2;
                cY = b.getHeight() / 2;
                cZ = b.getDepth() / 2;
            } else {
                cX = self.getTx();
                cY = self.getTy();
                cZ = self.getTz();
            }

            Point3D camPos = new Point3D(cam.getTx(), cam.getTy(), cam.getTz());
            Point3D selfPos = new Point3D(cX, cY, cZ);

            Point3D up = Point3D.ZERO.add(0, -1, 0),
                    forward = new Point3D(
                            (selfPos.getX()) - camPos.getX(),
                            (selfPos.getY()) - camPos.getY(),
                            (selfPos.getZ()) - camPos.getZ()
                    ).normalize(),
                    right = up.crossProduct(forward).normalize();
            up = forward.crossProduct(right).normalize();

            switch (getBillboardMode()) {

                case SPHERICAL:
                    affine.setMxx(right.getX());
                    affine.setMxy(up.getX());
                    affine.setMzx(forward.getX());
                    affine.setMyx(right.getY());
                    affine.setMyy(up.getY());
                    affine.setMzy(forward.getY());
                    affine.setMzx(right.getZ());
                    affine.setMzy(up.getZ());
                    affine.setMzz(forward.getZ());

                    affine.setTx(cX * (1 - affine.getMxx()) - cY * affine.getMxy() - cZ * affine.getMxz());
                    affine.setTy(cY * (1 - affine.getMyy()) - cX * affine.getMyx() - cZ * affine.getMyz());
                    affine.setTz(cZ * (1 - affine.getMzz()) - cX * affine.getMzx() - cY * affine.getMzy());
                    break;

                case CYLINDRICAL:
                    affine.setMxx(right.getX());
                    affine.setMxy(0);
                    affine.setMzx(forward.getY());
                    affine.setMyx(0);
                    affine.setMyy(1);
                    affine.setMzy(0);
                    affine.setMzx(right.getZ());
                    affine.setMzy(0);
                    affine.setMzz(forward.getZ());

                    affine.setTx(cX * (1 - affine.getMxx()) - cY * affine.getMxy() - cZ * affine.getMxz());
                    affine.setTy(cY * (1 - affine.getMyy()) - cX * affine.getMyx() - cZ * affine.getMyz());
                    affine.setTz(cZ * (1 - affine.getMzz()) - cX * affine.getMzx() - cY * affine.getMzy());
                    break;
            }
        }
    }

    private final ObjectProperty<BillboardMode> mode = new SimpleObjectProperty<>(this, "mode");

    public final BillboardMode getBillboardMode() {
        return mode.getValue();
    }

    public final void setBillboardMode(BillboardMode m) {
        mode.set(m);
    }

    public final ObjectProperty<BillboardMode> billboardModeProperty() {
        return mode;
    }

    private final BooleanProperty active = new SimpleBooleanProperty(this, "Billboarding Active") {
        @Override
        protected void invalidated() {

            if (getValue()) {
                startBillboarding();
                System.out.println("timer started");
            } else if (!getValue()) {
                stopBillboarding();
                System.out.println("timer stopped");
            }

        }
    };

    public final boolean isActive() {
        return active.getValue();
    }

    public final void setActive(boolean b) {
        active.set(b);
    }

    public final BooleanProperty activeProperty() {
        return active;
    }
}
