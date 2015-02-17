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
    protected ScriptFunction3DControl densFunct;
    protected ScriptFunction1DControl funcFunct;
    
    private final BooleanBinding  useColorSlider, useImage, usePatternScaler , useDensScriptor, useFuncScriptor;
    
    public TextureTypeControl(String lbl, Property<TextureType> type, Collection<TextureType> items,
            final Property<Number> colors,
            final Property<Boolean> udm,
            final Property<Image> dmp,
            final Property<Number> pScale,
            final Property<Function<Point3D,Number>> densFunc,
            final Property<Function<Number,Number>> funcFunc
    ) {
        super(lbl, type, items, true);
        buildSubControls(colors, udm, dmp, pScale, densFunc, funcFunc);
        this.useColorSlider = selection.valueProperty().isEqualTo(TextureType.NONE);
        this.useImage = selection.valueProperty().isEqualTo(TextureType.IMAGE);
        this.usePatternScaler = selection.valueProperty().isEqualTo(TextureType.PATTERN);   
        this.useDensScriptor = selection.valueProperty().isEqualTo(TextureType.COLORED_VERTICES_3D);
        this.useFuncScriptor = selection.valueProperty().isEqualTo(TextureType.COLORED_VERTICES_1D);
                
        EasyBind.includeWhen(subControls.getChildren(), colorSlider, useColorSlider);
        EasyBind.includeWhen(subControls.getChildren(), diffMapControl, useImage);
        EasyBind.includeWhen(subControls.getChildren(), imgLoader, useImage);
        EasyBind.includeWhen(subControls.getChildren(), patternScaler, usePatternScaler);
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
            final Property<Function<Point3D,Number>> densFunc,
            final Property<Function<Number,Number>> funcFunc
    ) {
        colorSlider = ControlFactory.buildColorSliderControl(colors, 0l, 1530l);
        diffMapControl = ControlFactory.buildImageViewToggle(udm, img, "Diffuse Map Image:");
        imgLoader = new FileLoadControl();
        patternScaler = ControlFactory.buildNumberSlider(pScale, 1, 100);
        densFunct = ControlFactory.buildScriptFunction3DControl(densFunc);
        funcFunct = ControlFactory.buildScriptFunction1DControl(funcFunc);
    }
    
}
