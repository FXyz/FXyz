/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls;

import java.util.Collection;
import java.util.function.Function;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.scene.image.Image;
import org.fxmisc.easybind.EasyBind;
import org.fxyz.controls.factory.ControlFactory;
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
    protected ScriptDensityControl densFunct;
    protected ScriptFunctionControl funcFunct;
    
    private final BooleanBinding  useColorSlider, useImage, usePatternScaler , useDensMinMax, 
            useDensScriptor, useFuncScriptor;
    
    public TextureTypeControl(String lbl, Property<TextureType> type, Collection<TextureType> items,
            final Property<Number> colors,
            final Property<Boolean> udm,
            final Property<Image> dmp,
            final Property<Number> pScale,
            final Property<Number> dens,
            final Property<Function<Point3D,Number>> densFunc,
            final Property<Function<Number,Number>> funcFunc
    ) {
        super(lbl, type, items, true);
        buildSubControls(colors, udm, dmp, pScale, dens, densFunc, funcFunc);
        this.useColorSlider = selection.valueProperty().isEqualTo(TextureType.NONE);
        this.useImage = selection.valueProperty().isEqualTo(TextureType.IMAGE);
        this.usePatternScaler = selection.valueProperty().isEqualTo(TextureType.PATTERN);   
        this.useDensMinMax = selection.valueProperty().isEqualTo(TextureType.COLORED_VERTICES_3D)
                .or(selection.valueProperty().isEqualTo(TextureType.COLORED_VERTICES_1D));
        this.useDensScriptor = selection.valueProperty().isEqualTo(TextureType.COLORED_VERTICES_3D);
        this.useFuncScriptor = selection.valueProperty().isEqualTo(TextureType.COLORED_VERTICES_1D);
                
        EasyBind.includeWhen(subControls.getChildren(), colorSlider, useColorSlider);
        EasyBind.includeWhen(subControls.getChildren(), diffMapControl, useImage);
        EasyBind.includeWhen(subControls.getChildren(), imgLoader, useImage);
        EasyBind.includeWhen(subControls.getChildren(), patternScaler, usePatternScaler);
        EasyBind.includeWhen(subControls.getChildren(), densMinMax, useDensMinMax);
        EasyBind.includeWhen(subControls.getChildren(), densFunct, useDensScriptor);
        EasyBind.includeWhen(subControls.getChildren(), funcFunct, useFuncScriptor);
        
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
            final Property<Function<Point3D,Number>> densFunc,
            final Property<Function<Number,Number>> funcFunc
    ) {
        colorSlider = ControlFactory.buildColorSliderControl(colors, 0l, 1530l);
        diffMapControl = ControlFactory.buildImageToggle(udm, img, "Diffuse Map Image:");
        imgLoader = new FileLoadControl();
        patternScaler = ControlFactory.buildNumberSlider(pScale, 1, 100);
        densMinMax = ControlFactory.buildNumberSlider(densVal, 0.01, 1);
        densFunct = ControlFactory.buildScriptDensityControl(densFunc);
        funcFunct = ControlFactory.buildScriptFunctionControl(funcFunc);
    }
    
}
