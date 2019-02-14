/*
 * Copyright (c) 2013-2019, F(X)yz
 * Copyright (c) 2010, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.fxyz3d.importers.maya;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.util.Duration;
import org.fxyz3d.importers.Importer;
import org.fxyz3d.importers.Model3D;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Maya Importer with support for Maya "ma" format.
 */
public class MayaImporter implements Importer {

    private static final String SUPPORTED_EXT = "ma";

    @Override
    public Model3D load(URL url) throws IOException {
        return load(url, false);
    }

    @Override
    public Model3D loadAsPoly(URL url) throws IOException {
        return load(url, true);
    }

    private Model3D load(URL url, boolean asPolygonMesh) {
        Loader loader = new Loader();
        loader.load(url, asPolygonMesh);

        // This root is not automatically added to the scene.
        // It needs to be added by the user of MayaImporter.
        //            root = new Xform();

        MayaGroup mayaRoot = new MayaGroup();
        // Add top level nodes to the root
        int nodeCount = 0;
        for (Node n : loader.loaded.values()) {
            if (n != null) {
                // Only add a node if it has no parents, ie. top level node
                if (n.getParent() == null) {
                    log("Adding top level node " + n.getId() + " to root!");

                    n.setDepthTest(DepthTest.ENABLE);
                    if (!(n instanceof MeshView) || ((TriangleMesh)((MeshView)n).getMesh()).getPoints().size() > 0) {
                        mayaRoot.getChildren().add(n);
                    }
                }
                nodeCount++;
            }
        }
        // rootCharacter.setRootJoint(loader.rootJoint);
        log("There are " + nodeCount + " nodes.");

        // if meshes were not loaded in the code above
        // (which they now are) one would need to
        // set meshParents from the loader
        // meshParents.addAll(loader.meshParents.keySet());
        // this is not necessary at the moment

        Timeline timeline = new Timeline();

        // TODO: possibly add parallel option
        loader.keyFrameMap.entrySet().stream()
                .map(entry -> new KeyFrame(Duration.seconds(entry.getKey()), null, null, entry.getValue()))
                .forEach(timeline.getKeyFrames()::add);

        log("Loaded " + timeline.getKeyFrames().size() + " key frames.");

        Model3D model = new Model3D() {
            @Override
            public Optional<Timeline> getTimeline() {
                return Optional.of(timeline);
            }
        };

        model.addMeshView("default", mayaRoot);

        return model;
    }

    @Override
    public boolean isSupported(String extension) {
        return SUPPORTED_EXT.equalsIgnoreCase(extension);
    }

    private static void log(String message) {
        Logger.getLogger(MayaImporter.class.getName()).log(Level.FINEST, message);
    }
}
