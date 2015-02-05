/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls.factory;

import java.util.Arrays;
import java.util.function.Function;
import javafx.beans.property.Property;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import org.fxyz.controls.CheckBoxControl;
import org.fxyz.controls.ColorPickControl;
import org.fxyz.controls.ColorSliderControl;
import org.fxyz.controls.ComboBoxControl;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.ControlPanel;
import org.fxyz.controls.ImagePreviewControl;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.ScriptDensityControl;
import org.fxyz.controls.ScriptFunctionControl;
import org.fxyz.controls.TextureTypeControl;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.SectionType;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.TextureType;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public final class ControlFactory {

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

    public static final ImagePreviewControl buildImageToggle(final Property<Boolean> prop, final Property<? extends Image> img, String name) {
        return new ImagePreviewControl(prop, img, name);
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

    public static final TextureTypeControl buildTextureTypeControl(final Property<TextureType> p, 
            final Property<Number> clrs, final Property<Boolean> uDiffMap, final Property<Image> imgP,
            final Property<Number> pScale, final Property<Function<Point3D,Number>> densFunc, 
            final Property<Function<Number,Number>> funcFunc) {
        return new TextureTypeControl("Texture Type:", p, Arrays.asList(TextureType.values()), 
                clrs, uDiffMap, imgP, pScale, densFunc, funcFunc);
    }

    public static final ComboBoxControl<SectionType> buildSectionTypeControl(final Property<SectionType> p) {
        return new ComboBoxControl<>("Section Type", p, Arrays.asList(SectionType.values()), false);
    }

    public static final ScriptDensityControl buildScriptDensityControl(final Property<Function<Point3D,Number>> p) {
        return new ScriptDensityControl(p, Arrays.asList("Math.sin(p.x)", "p.y", "p.z", "p.x + p.y", "p.x + p.z", "p.f", "p.magnitude()"), false);
    }

    public static final ScriptFunctionControl buildScriptFunctionControl(final Property<Function<Number,Number>> p) {
        return new ScriptFunctionControl(p, Arrays.asList("Math.sin(x)", "x*x", "x+3", "Math.pow(Math.abs(x),1/2.5)"), false);
    }
    /*==========================================================================
     Standard Controls for MeshView
     ==========================================================================*/
    /* 
     builds the complete ControlCategory, shared by all MeshViews
     DrawMode, CullFace, DiffuseColor, SpecularColor
     */
    public static ControlCategory buildMeshViewCategory(final Property<Boolean> useMat, final Property<DrawMode> dmp, final Property<CullFace> cfp,
            final Property<Color> dcp, final Property<Color> scp) {
        ControlCategory mvc = new ControlCategory("Standard MeshView Properties");
        mvc.addControls(
                buildCheckBoxControl(useMat),
                buildDrawModeControl(dmp),
                buildCullFaceControl(cfp),
                buildColorControl(dcp, "Diffuse Color: "),
                buildColorControl(scp, "Specular Color: ")
        );
        return mvc;
    }
    /*
     Build a Category for the four Image maps available to PhongMaterial
     */

    public static ControlCategory buildMaterialMapCategory(
            final Property<Image> dm, final Property<Boolean> udm,
            final Property<? extends Image> bm, final Property<Boolean> ubm,
            final Property<Image> sm, final Property<Boolean> usm,
            final Property<Image> im, final Property<Boolean> uim
    ) {
        ControlCategory mvc = new ControlCategory("Material Image Maps");
        mvc.addControls(
                buildImageToggle(udm, dm, "Use Diffuse Map"),
                buildImageToggle(ubm, bm, "Use Bump Map"),
                buildImageToggle(usm, sm, "Use Specular Map"),
                buildImageToggle(uim, im, "Use Illumination Map")
        );
        return mvc;
    }
    /*
    
     */

    public static ControlCategory buildTextureMeshCategory(
            final Property<TextureType> ttp,
            final Property<Number> cp,
            final Property<SectionType> stp,
            final Property<Boolean> uDiffMap, 
            final Property<Image> imgP,
            final Property<Number> pScale, 
            final Property<Function<Point3D,Number>> densFunc,
            final Property<Function<Number,Number>> funcFunc
    ) {
        
        final TextureTypeControl texType = buildTextureTypeControl(
                ttp, cp, uDiffMap, imgP, pScale, densFunc, funcFunc);

        ControlCategory mvc = new ControlCategory("TexturedMesh Properties");
        mvc.addControls(
                texType,
                buildSectionTypeControl(stp)
        );

        return mvc;
    }
    /*
     Build a Category for the four Image maps available to PhongMaterial
     */

}
