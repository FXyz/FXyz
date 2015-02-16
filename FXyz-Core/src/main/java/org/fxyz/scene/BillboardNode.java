/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.scene;

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
