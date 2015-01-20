/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.shapes.containers;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;

/**
 *
 * @author Dub
 * @param <T>
 */
public class ShapeContainer<T extends MeshView> extends Group implements ShapeContainerBase<T> {
    
    private final T shape;
    private final PhongMaterial material;
    private final PointLight emissive;
    private final AmbientLight selfIllumination;

    public ShapeContainer(T shape) {
        this.shape = shape;
        this.material = new PhongMaterial();
        this.emissive = new PointLight();
        this.selfIllumination = new AmbientLight();
        
        this.selfIllumination.getScope().add(ShapeContainer.this);
        initialize();
    }

    @Override
    public T getShape() {
        return shape;
    }

    @Override
    public Group getContainer() {
        return this;
    }

    @Override
    public PhongMaterial getMaterial() {
        return material;
    }

    @Override
    public PointLight getEmissiveLight() {
        return emissive;
    }

    @Override
    public AmbientLight getSelfIlluminationLight() {
        return selfIllumination;
    }
    
}
