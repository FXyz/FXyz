/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import java.util.Collection;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.scene.image.Image;
import org.fxmisc.easybind.EasyBind;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.geometry.DensityFunction;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.TextureType;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class TextureTypeControl extends ComboBoxControl<TextureType>{

    protected ColorSliderControl colorSlider;
    protected ImagePreviewControl diffMapControl;
    protected FileLoadControl imgLoader;
    protected NumberSliderControl patternScaler;
    protected NumberSliderControl densMinMax;
    protected ScriptFunctionControl densFunct;
    
    private final BooleanBinding  useColorSlider, useImage, usePatternScaler , useDensMinMax, useScriptor;
    
    
    public TextureTypeControl(String lbl, Property<TextureType> type, Collection<TextureType> items,
            final Property<Number> colors,
            final Property<Boolean> udm,
            final Property<Image> dmp,
            final Property<Number> pScale,
            final Property<Number> dens,
            final Property<DensityFunction<Point3D>> densFunc
    ) {
        super(lbl, type, items, true);
        buildSubControls(colors, udm, dmp, pScale, dens, densFunc);
        // NONE -> color slider -> model diffColor property
        
        //IMAGE -> image preview / loader -> model diffMap
        
        //PATTERN -> number slider (scale) 
        
        //COLORED_FACES -> color slider -> model colors(int) prop ->def: 1530
        
        // VERT3D -> color slider, density(point3D) control, (2) num sliders min/max -> model colors(int) prop ->def: 1530
        
        //VERT1D  -> color slider, density(point3D) control, (2) num sliders min/max -> model colors(int) prop ->def: 1530
        this.useColorSlider = 
                    selection.valueProperty().isEqualTo(TextureType.NONE)
                    .or(selection.valueProperty().isEqualTo(TextureType.COLORED_FACES))
                    .or(selection.valueProperty().isEqualTo(TextureType.COLORED_VERTICES_1D))
                    .or(selection.valueProperty().isEqualTo(TextureType.COLORED_VERTICES_3D));
        this.useImage = selection.valueProperty().isEqualTo(TextureType.IMAGE);
        this.usePatternScaler = selection.valueProperty().isEqualTo(TextureType.PATTERN);   
        this.useDensMinMax = selection.valueProperty().isEqualTo(TextureType.COLORED_VERTICES_3D)
                .or(selection.valueProperty().isEqualTo(TextureType.COLORED_VERTICES_1D));
        this.useScriptor = useDensMinMax;
                
        EasyBind.includeWhen(subControls.getChildren(), colorSlider, useColorSlider);
        EasyBind.includeWhen(subControls.getChildren(), diffMapControl, useImage);
        EasyBind.includeWhen(subControls.getChildren(), imgLoader, useImage);
        EasyBind.includeWhen(subControls.getChildren(), patternScaler, usePatternScaler);
        EasyBind.includeWhen(subControls.getChildren(), densMinMax, useDensMinMax);
        EasyBind.includeWhen(subControls.getChildren(), densFunct, useScriptor);
        
    }

    @Override
    protected boolean useSubControls() {
        return true;
    }

    private void buildSubControls(final Property<Number> colors, 
            final Property<Boolean> udm, 
            final Property<Image> img, 
            final Property<Number> pScale, 
            final Property<Number> densVal,
            final Property<DensityFunction<Point3D>> densFunc
    ) {
        colorSlider = ControlFactory.buildColorSliderControl(colors, 0l, 1530l);
        diffMapControl = ControlFactory.buildImageToggle(udm, img, "Diffuse Map Image:");
        imgLoader = new FileLoadControl();
        patternScaler = ControlFactory.buildNumberSlider(pScale, 1, 100);
        densMinMax = ControlFactory.buildNumberSlider(densVal, 0.01, 1);
        densFunct = ControlFactory.buildScriptFunctionControl(densFunc);
    }
    
}
