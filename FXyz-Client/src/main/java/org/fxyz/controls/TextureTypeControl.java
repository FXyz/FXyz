/**
* TextureTypeControl.java
*
* Copyright (c) 2013-2015, F(X)yz
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* * Neither the name of the organization nor the
* names of its contributors may be used to endorse or promote products
* derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
import org.fxyz.scene.paint.Patterns.CarbonPatterns;
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
    protected ComboBoxControl patternChooser;
    
    protected ScriptFunction3DControl densFunct;
    protected ScriptFunction1DControl funcFunct;
    
    protected ColorSliderControl specColor;
    protected NumberSliderControl specSlider;
    
    protected CheckBoxControl bumpMap;
    protected CheckBoxControl invertBumpMap;
    protected NumberSliderControl bumpScale;
    protected NumberSliderControl bumpFine;
    
    
    private final BooleanBinding  useColorSlider,
            useImage, usePatternScaler ,
            useDensScriptor, useFuncScriptor,
            usePatternChooser,
            useSpecColor, useSpecPower,
            useBumpMapping;
    
    public TextureTypeControl(String lbl, Property<TextureType> type, Collection<TextureType> items,
            final Property<Number> colors,
            final Property<Boolean> udm,
            final Property<Image> dmp,
            final Property<Boolean> bmpMap,
            final Property<Number> bmpScale,
            final Property<Number> bmpFineScale,
            final Property<Boolean> invBmp,
            final Property<CarbonPatterns> patt,
            final Property<Number> pScale,
            final Property<Number> spColor,
            final Property<Number> specP,
            final Property<Function<Point3D,Number>> densFunc,
            final Property<Function<Number,Number>> funcFunc
    ) {
        super(lbl, type, items, true);
        buildSubControls(colors, udm, dmp,bmpMap,bmpScale,bmpFineScale,invBmp, pScale, patt, spColor, specP, densFunc, funcFunc);
        this.useColorSlider = selection.valueProperty().isEqualTo(TextureType.NONE);
        this.useImage = selection.valueProperty().isEqualTo(TextureType.IMAGE);
        this.usePatternScaler = selection.valueProperty().isEqualTo(TextureType.PATTERN);   
        this.useDensScriptor = selection.valueProperty().isEqualTo(TextureType.COLORED_VERTICES_3D);
        this.useFuncScriptor = selection.valueProperty().isEqualTo(TextureType.COLORED_VERTICES_1D);
        this.usePatternChooser = selection.valueProperty().isEqualTo(TextureType.PATTERN);
        this.useSpecColor = selection.valueProperty().isNotNull();
        this.useSpecPower = selection.valueProperty().isNotNull();
        this.useBumpMapping = selection.valueProperty().isNotNull().and(usePatternChooser);
        
        
                
        EasyBind.includeWhen(subControls.getChildren(), patternChooser, usePatternChooser);
        EasyBind.includeWhen(subControls.getChildren(), colorSlider, useColorSlider);
        EasyBind.includeWhen(subControls.getChildren(), diffMapControl, useImage);
        EasyBind.includeWhen(subControls.getChildren(), imgLoader, useImage);
        EasyBind.includeWhen(subControls.getChildren(), patternScaler, usePatternScaler);
        
        EasyBind.includeWhen(subControls.getChildren(), densFunct, useDensScriptor);
        EasyBind.includeWhen(subControls.getChildren(), funcFunct, useFuncScriptor);
        
        EasyBind.includeWhen(subControls.getChildren(), specColor, useSpecColor);
        EasyBind.includeWhen(subControls.getChildren(), specSlider, useSpecPower);
        
        EasyBind.includeWhen(subControls.getChildren(), bumpMap, useBumpMapping);
        EasyBind.includeWhen(subControls.getChildren(), invertBumpMap, useBumpMapping);
        EasyBind.includeWhen(subControls.getChildren(), bumpScale, useBumpMapping);
        EasyBind.includeWhen(subControls.getChildren(), bumpFine, useBumpMapping);
    }

    @Override
    protected boolean useSubControls() {
        return true;
    }

    private void buildSubControls(
            final Property<Number> colors, 
            final Property<Boolean> udm, 
            final Property<Image> img, 
            final Property<Boolean> bmpMap,
            final Property<Number> bmpScale,
            final Property<Number> bmpFineScale,
            final Property<Boolean> invBmp, 
            final Property<Number> pScale, 
            final Property<CarbonPatterns> patt,
            final Property<Number> spColor,
            final Property<Number> specP,
            final Property<Function<Point3D,Number>> densFunc,
            final Property<Function<Number,Number>> funcFunc
    ) {
        specColor = new ColorSliderControl(spColor, 0, 1530);
        specColor.setPrefSize(USE_COMPUTED_SIZE, USE_PREF_SIZE);
        specSlider = ControlFactory.buildNumberSlider(specP, 32, 10000);
        
        bumpMap = ControlFactory.buildCheckBoxControl(bmpMap);
        invertBumpMap = ControlFactory.buildCheckBoxControl(invBmp);
        bumpScale = ControlFactory.buildNumberSlider(bmpScale, 1, 100);
        bumpFine = ControlFactory.buildNumberSlider(bmpFineScale, 1, 10);
        
        colorSlider = ControlFactory.buildColorSliderControl(colors, 0l, 1530l);
        diffMapControl = ControlFactory.buildImageViewToggle(udm, img, "Diffuse Map Image:");
        imgLoader = new FileLoadControl();
        patternScaler = ControlFactory.buildNumberSlider(pScale, 1, 100);
        patternChooser = ControlFactory.buildPatternChooser(patt);
        densFunct = ControlFactory.buildScriptFunction3DControl(densFunc);
        funcFunct = ControlFactory.buildScriptFunction1DControl(funcFunc);
        
    }
    
}
