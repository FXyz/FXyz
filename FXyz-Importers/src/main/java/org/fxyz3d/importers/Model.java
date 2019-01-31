package org.fxyz3d.importers;

import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Material;

import java.util.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Model {

    private final Group root = new Group();
    private Map<String, Material> materials = new HashMap<>();
    private Map<String, Node> meshViews = new HashMap<>();

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

    public final Node getMeshView(String key) {
        return meshViews.get(key);
    }

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
