/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.controls.factory;

import java.util.Arrays;
import java.util.function.Function;
import javafx.beans.property.Property;
import javafx.geometry.Point2D;
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
import org.fxyz.controls.LightingControls;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.ScriptFunction1DControl;
import org.fxyz.controls.ScriptFunction2DControl;
import org.fxyz.controls.ScriptFunction3DControl;
import org.fxyz.controls.SectionLabel;
import org.fxyz.controls.TextureTypeControl;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.SectionType;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.TextureType;

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

    public static final ImagePreviewControl buildImageViewToggle(final Property<Boolean> prop, final Property<? extends Image> img, String name) {
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
            final Property<Number> pScale, final Property<Function<Point3D, Number>> densFunc,
            final Property<Function<Number, Number>> funcFunc) {
        return new TextureTypeControl("Texture Type:", p, Arrays.asList(TextureType.values()),
                clrs, uDiffMap, imgP, pScale, densFunc, funcFunc);
    }

    public static final ComboBoxControl<SectionType> buildSectionTypeControl(final Property<SectionType> p) {
        return new ComboBoxControl<>("Section Type", p, Arrays.asList(SectionType.values()), false);
    }

    public static final ScriptFunction3DControl buildScriptFunction3DControl(final Property<Function<Point3D,Number>> p) {
        return new ScriptFunction3DControl(p, Arrays.asList("Math.sin(p.x)", "p.y", "p.z", "p.x + p.y", "p.x + p.z", "p.f", "p.magnitude()"), false);
    }

    public static final ScriptFunction2DControl buildScriptFunction2DControl(final Property<Function<Point2D,Number>> p) {
        return new ScriptFunction2DControl(p, Arrays.asList("Math.sin(p.magnitude())/p.magnitude()", "p.x", "p.y", "p.x*3+p.y*p.y", "p.magnitude()"), false);
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

    public static ControlCategory buildMeshViewCategory(final Property<DrawMode> dmp, final Property<CullFace> cfp,
            final Property<Color> dcp, final Property<Color> scp) {
        ControlCategory mvc = new ControlCategory("Standard MeshView Properties");
        mvc.addControls(
                new SectionLabel("MeshView Properties"),
                buildDrawModeControl(dmp),
                buildCullFaceControl(cfp)
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
                new SectionLabel("Material Image Maps"),
                buildImageViewToggle(udm, dm, "Use Diffuse Map"),
                buildImageViewToggle(ubm, bm, "Use Bump Map"),
                buildImageViewToggle(usm, sm, "Use Specular Map"),
                buildImageViewToggle(uim, im, "Use Illumination Map")
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
            final Property<Function<Point3D, Number>> densFunc,
            final Property<Function<Number, Number>> funcFunc
    ) {

        final TextureTypeControl texType = buildTextureTypeControl(
                ttp, cp, uDiffMap, imgP, pScale, densFunc, funcFunc);

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
