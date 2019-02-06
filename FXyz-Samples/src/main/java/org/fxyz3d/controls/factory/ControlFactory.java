/**
 * ControlFactory.java
 *
 * Copyright (c) 2013-2018, F(X)yz
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

package org.fxyz3d.controls.factory;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import javafx.animation.Timeline;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.text.Font;
import org.fxyz3d.controls.*;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.scene.paint.Patterns;
import org.fxyz3d.scene.paint.Patterns.CarbonPatterns;
import org.fxyz3d.shapes.primitives.helper.TriangleMeshHelper.SectionType;
import org.fxyz3d.shapes.primitives.helper.TriangleMeshHelper.TextureType;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public final class ControlFactory {

    public static final ControlPanel buildSingleListControlPanel(){
        return new ControlPanel();
    }
    public static final ControlPanel buildControlPanel(final ControlCategory titlePane) {
        return new ControlPanel(titlePane);
    }

    public static final ControlPanel buildControlPanel(final ControlCategory... titlePane) {
        ControlPanel panel = new ControlPanel(titlePane[0]);
        for (int i = 1; i < titlePane.length; i++) {
            panel.getPanes().add(titlePane[i]);
        }
        return panel;
    }

    public static final ControlCategory buildCategory(final String title) {
        return new ControlCategory(title);
    }
    /*==========================================================================
     Standard Control Types
     ==========================================================================*/

    public static final TextFieldControl buildTextFieldControl(final String title, final StringProperty p) {
        return new TextFieldControl(title, p);
    }

    public static final CheckBoxControl buildCheckBoxControl(final Property<Boolean> p) {
        return new CheckBoxControl(p);
    }

    public static final NumberSliderControl buildNumberSlider(final Property<Number> p, final Number lb, final Number ub) {
        return new NumberSliderControl(p, lb, ub);
    }

    public static final ColorSliderControl buildColorSliderControl(final Property<Number> p, final Number lb, final Number ub) {
        return new ColorSliderControl(p, lb, ub);
    }

    public static final ColorPickControl buildColorControl(final Property<Color> p, String name) {
        return new ColorPickControl(p, name);
    }

    public static final ImagePreviewControl buildImageViewToggle(final Property<TextureImage> img, String name, final Collection<TextureImage> imgs) {
        return new ImagePreviewControl(img, name, imgs);
    }

    public static final TimelineControl buildTimelineControl(final Property<Timeline> p, String name) {
        return new TimelineControl(p, name);
    }

    /*==========================================================================
     List like Items
     ==========================================================================*/

    public static final ComboBoxControl buildCullFaceControl(final Property<CullFace> p) {
        return new ComboBoxControl("Cull Face: ", p, Arrays.asList(CullFace.values()), false);
    }

    public static final ComboBoxControl<DrawMode> buildDrawModeControl(final Property<DrawMode> dmp) {
        return new ComboBoxControl<>("Draw Mode: ", dmp, Arrays.asList(DrawMode.values()), false);
    }
    
    public static final ComboBoxControl<String> buildFontControl(final Property<String> font) {
        return new ComboBoxControl<>("Font Family: ", font, Font.getFontNames(), false);
    }

    public static final TextureTypeControl buildTextureTypeControl(
            final Property<TextureType> p,
            final Property<Number> clrs,
            final Property<TextureImage> imgP,
            final Property<Boolean> bmpMap,
            final Property<Number> bmpScale,
            final Property<Number> bmpFineScale,
            final Property<Boolean> invBmp,
            final Property<CarbonPatterns> patt, final Property<Number> pScale,
            final Property<Number> spColor,
            final Property<Number> specP,
            final Property<Function<Point3D, Number>> densFunc,
            final Property<Function<Number, Number>> funcFunc) {
        return new TextureTypeControl("Texture Type:", 
                p, 
                Arrays.asList(TextureType.values()),
                clrs,
                imgP,
                bmpMap,
                bmpScale,
                bmpFineScale,
                invBmp, 
                patt,
                pScale,
                spColor,
                specP, 
                densFunc, 
                funcFunc);
    }
    
    public static final ComboBoxControl buildPatternChooser(final Property<CarbonPatterns> p) {
        return new ComboBoxControl("Carbon Patterns: ", p, Arrays.asList(CarbonPatterns.values()), false);
    }

    public static final ComboBoxControl<SectionType> buildSectionTypeControl(final Property<SectionType> p) {
        return new ComboBoxControl<>("Section Type", p, Arrays.asList(SectionType.values()), false);
    }

    public static final ScriptFunction3DControl buildScriptFunction3DControl(final Property<Function<Point3D,Number>> p) {
        return new ScriptFunction3DControl(p, Arrays.asList("Math.sin(p.x)", "p.y", "p.z", "p.x + p.y", "p.x + p.z", "p.f", "p.magnitude()"), false);
    }

    public static final ScriptFunction2DControl buildScriptFunction2DControl(final Property<Function<Point2D,Number>> p) {
        return new ScriptFunction2DControl(p, Arrays.asList("Math.sin(p.magnitude())", "p.x", "p.y", "p.x*3+p.y*p.y", "p.magnitude()"), false);
    }

    public static final ScriptFunction1DControl buildScriptFunction1DControl(final Property<Function<Number,Number>> p) {
        return new ScriptFunction1DControl(p, Arrays.asList("Math.sin(x)", "x*x", "x+3", "Math.pow(Math.abs(x),1/2.5)"), false);
    }
    /*==========================================================================
     Standard Controls for MeshView
     ==========================================================================*/
    /* 
     builds the complete ControlCategory, shared by all MeshViews
     DrawMode, CullFace, DiffuseColor, SpecularColor
     */

    public static ControlCategory buildMeshViewCategory(final Property<DrawMode> dmp, final Property<CullFace> cfp
       ) {
        ControlCategory mvc = new ControlCategory("Standard MeshView Properties");
        mvc.addControls(
                new SectionLabel("MeshView Properties"),
                buildDrawModeControl(dmp),
                buildCullFaceControl(cfp)
        );
        return mvc;
    }

    public static ControlCategory buildAnimationCategory(final Property<Timeline> dmp) {
        ControlCategory mvc = new ControlCategory("Animation");
        mvc.addControls(
                new SectionLabel("Animation"),
                buildTimelineControl(dmp, "Timeline")
        );
        return mvc;
    }

    /*
    
     */

    public static ControlCategory buildTextureMeshCategory(
            final Property<TextureType> ttp,
            final Property<Number> cp,
            final Property<SectionType> stp,
            final Property<TextureImage> imgP,
            final Property<Boolean> bmpMap,
            final Property<Number> bmpScale,
            final Property<Number> bmpFineScale,
            final Property<Boolean> invBmp,
            final Property<Patterns.CarbonPatterns> patt,
            final Property<Number> pScale,
            final Property<Number> spColor,
            final Property<Number> specP,
            final Property<Function<Point3D, Number>> densFunc,
            final Property<Function<Number, Number>> funcFunc
    ) {

        final TextureTypeControl texType = buildTextureTypeControl(
                ttp, cp, imgP, bmpMap, bmpScale, bmpFineScale, invBmp, patt, pScale, spColor, specP, densFunc, funcFunc);

        final ControlCategory mvc = new ControlCategory("TexturedMesh Properties");
        mvc.addControls(
                new SectionLabel("Textured Mesh Properties"),
                texType
        );
        if (stp != null) {
            mvc.addControls(buildSectionTypeControl(stp));
        }

        return mvc;
    }

    public static ControlCategory buildSceneAndLightCategory(
            final Property<Boolean> show,
            final Property<Boolean> lt1On, final Property<Boolean> lt2On,
            final Property<Color> c1, final Property<Color> c2,
            final Property<Number> d1, final Property<Number> d2,
            final Property<Number> r1, final Property<Number> r2,
            final Property<javafx.geometry.Point3D> ra1, final Property<javafx.geometry.Point3D> ra2
    ) {
        final LightingControls lighting1 = new LightingControls(
                show,
                lt1On,
                c1,
                d1,
                r1,
                ra1
        );
        final LightingControls lighting2= new LightingControls(
                show,
                lt2On,
                c2,
                d2,
                r2,
                ra2
        );
        final ControlCategory mvc = new ControlCategory("Scene Lighting");
        mvc.addControls(new SectionLabel("Light 1"),
                lighting1,
                new SectionLabel("Light 2"),
                lighting2,
                new CheckBoxControl(show));

        return mvc;
    }
    /*
     Build a Category for the four Image maps available to PhongMaterial
     */
    
    static class PropertyParser{
        static void parseProperties(Property ... props){
            //Arrays.stream(props);
        }
    }

}
