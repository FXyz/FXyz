/*
 * F(X)yz
 *
 * Copyright (c) 2013-2019, F(X)yz
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
package org.fxyz3d.importers;

import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Material;

import java.util.*;

/**
 * Represents a loader-independent 3D model data.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Model3D {

    private final Group root = new Group();
    private final Map<String, Material> materials = new HashMap<>();
    private final Map<String, Node> meshViews = new HashMap<>();

    /**
     * The root that may contain Node / MeshView / PolygonMeshView.
     * You can add this root to your scene graph.
     *
     * @return root node
     */
    public Group getRoot() {
        return root;
    }

    public final Set<String> getMeshNames() {
        return meshViews.keySet();
    }

    public final void addMeshView(String key, Node view) {
        meshViews.put(key, view);
        root.getChildren().add(view);
    }

    /**
     * Mesh names can be obtained by calling getMeshNames().
     *
     * @param key mesh name
     * @return a specific view (part) of this model
     */
    public final Node getMeshView(String key) {
        return meshViews.get(key);
    }

    /**
     * @return all views that this model contains
     */
    public final List<Node> getMeshViews() {
        return new ArrayList<>(meshViews.values());
    }

    public final void addMaterial(String key, Material material) {
        materials.put(key, material);
    }

    public final Material getMaterial(String key) {
        return materials.get(key);
    }

    public final List<Material> getMaterials() {
        return new ArrayList<>(materials.values());
    }

    public Optional<Timeline> getTimeline() {
        return Optional.empty();
    }
}
