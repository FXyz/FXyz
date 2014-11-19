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
