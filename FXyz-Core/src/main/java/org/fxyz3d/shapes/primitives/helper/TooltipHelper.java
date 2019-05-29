/**
 * TooltipHelper.java
 *
 * Copyright (c) 2018-2019, F(X)yz
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
package org.fxyz3d.shapes.primitives.helper;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.logging.Logger;

public class TooltipHelper extends Pane {

    private static final Logger LOG = Logger.getLogger(TooltipHelper.class.getName());

    private static final double SIZE = 20;

    private final Node node;
    private final Node parent;
    private final Tooltip tooltip;

    public TooltipHelper(Node parent, Node node) {
        this.parent = parent;
        this.node = node;
        setPrefSize(SIZE, SIZE);
        setOpacity(0);

        tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(this, tooltip);
    }

    public void updateTooltip(String id, Point3D point3D) {
        updateTooltip(point3D, String.format("%s :: %.3f %.3f %.3f", id == null ? "--" : id,
                point3D.getX(), point3D.getZ(), point3D.getY()));
    }

    public void updateTooltip(Point3D point3D, String tooltipText) {
        if (node == null) {
            LOG.warning("Node was null");
            return;
        }
        if (parent == null) {
            LOG.warning("Parent was null");
            return;
        }
        if (getScene() == null) {
            LOG.warning("TooltipHelper was not added to the scene graph");
            return;
        }
        Point3D coordinates = node.localToScene(point3D, true);
        Point3D p2 = parent.sceneToLocal(coordinates);
        getTransforms().setAll(new Translate(p2.getX() - SIZE / 2, p2.getY() - SIZE / 2));
        tooltip.setText(tooltipText);
    }
}
