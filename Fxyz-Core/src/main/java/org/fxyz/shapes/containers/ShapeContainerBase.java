/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
