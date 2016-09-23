/**
 * ShapeContainerBase.java
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

package org.fxyz.shapes.containers;

import javafx.collections.ObservableList;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;

/**
 * Basic Group for holding FX(yz) primitive MeshViews
 * Allows for selfIllumination and emissiveLighting.
 * Lazily implemented Transforms: Rotate(x,y,z), Translate, Scale.
 *
 * @author Dub
 * @param <T> the primitiveMeshClass this container implements
 */
public interface ShapeContainerBase<T extends MeshView>{
    
    
    default void initialize(){
        getSelfIlluminationLight().setLightOn(false);
        getEmissiveLight().setLightOn(false);
        
        getShape().setMaterial(getMaterial());
        getContainer().getChildren().addAll(getSelfIlluminationLight(), getEmissiveLight(), getShape());
    }
    
    
    /*
     * 
     * Material and Lighting methods 
     * 
     */
    public default void setEmissiveLightingColor(Color value) {
        getEmissiveLight().setColor(value);
    }

    public default Color getEmissiveLightingColor() {
        return getEmissiveLight().getColor();
    }

    public default void setEmissiveLightingOn(boolean value) {
        getEmissiveLight().setLightOn(value);
    }

    public default boolean isEmissiveLightingOn() {
        return getEmissiveLight().isLightOn();
    }

    public default ObservableList<Node> getEmissiveLightingScope() {
        return getEmissiveLight().getScope();
    }

    
    public default void setDiffuseColor(Color value) {
        getMaterial().setDiffuseColor(value);
    }

    public default Color getDiffuseColor() {
        return getMaterial().getDiffuseColor();
    }

    public default void setSpecularColor(Color value) {
        getMaterial().setSpecularColor(value);
    }

    public default Color getSpecularColor() {
        return getMaterial().getSpecularColor();
    }

    public default void setSpecularPower(double value) {
        getMaterial().setSpecularPower(value);
    }

    public default double getSpecularPower() {
        return getMaterial().getSpecularPower();
    }

    public default void setDiffuseMap(Image value) {
        getMaterial().setDiffuseMap(value);
    }

    public default Image getDiffuseMap() {
        return getMaterial().getDiffuseMap();
    }

    public default void setSpecularMap(Image value) {
        getMaterial().setSpecularMap(value);
    }

    public default Image getSpecularMap() {
        return getMaterial().getSpecularMap();
    }

    public default void setBumpMap(Image value) {
        getMaterial().setBumpMap(value);
    }

    public default Image getBumpMap() {
        return getMaterial().getBumpMap();
    }

    public default void setSelfIlluminationMap(Image value) {
        getMaterial().setSelfIlluminationMap(value);
    }

    public default Image getSelfIlluminationMap() {
        return getMaterial().getSelfIlluminationMap();
    }

    
    
    public default void setSelfIlluminationColor(Color value) {
        getSelfIlluminationLight().setColor(value);
    }

    public default Color getSelfIlluminationColor() {
        return getSelfIlluminationLight().getColor();
    }

    public default void setSelfIlluminationLightOn(boolean value) {
        getSelfIlluminationLight().setLightOn(value);
    }

   public default boolean isSelfIlluminationLightOn() {
        return getSelfIlluminationLight().isLightOn();
    }
    
    public T getShape();
    public Group getContainer();
    public PhongMaterial getMaterial();
    public PointLight getEmissiveLight();
    public AmbientLight getSelfIlluminationLight();
    
}
