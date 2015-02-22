/**
* Prisms.java
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

package org.fxyz.samples.shapes.texturedmeshes;

import java.util.List;
import java.util.stream.Collectors;
import static javafx.application.Application.launch;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;
import org.fxyz.controls.CheckBoxControl;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.NumberSliderControl;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.geometry.Point3D;
import org.fxyz.samples.shapes.TexturedMeshSample;
import org.fxyz.shapes.primitives.PrismMesh;

/**
 *
 * @author jpereda
 */
public class Prisms extends TexturedMeshSample{
    
    public static void main(String[] args){
        launch(args);
    }
    
    private PrismMesh fake;
    
    private final BooleanProperty showKnots = new SimpleBooleanProperty(this, "Show Knots");
    private final BooleanProperty enablePicking = new SimpleBooleanProperty(this, "Allow Knots Dragging");
    private final BooleanProperty pickingOnDragging = new SimpleBooleanProperty(this, "Update Prism on Dragging");
    
    private final DoubleProperty radius = new SimpleDoubleProperty(model, "Radius", 1d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((PrismMesh)model).setRadius(radius.get());
            }
        }
    };
    private final DoubleProperty height = new SimpleDoubleProperty(model, "Height", 3d) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((PrismMesh)model).setHeight(height.get());
            }
        }
    };
    private final IntegerProperty level = new SimpleIntegerProperty(model, "Level", 2) {
        @Override
        protected void invalidated() {
            super.invalidated();
            if (model != null) {
                ((PrismMesh)model).setLevel(level.get());
            }
        }
    };
    @Override
    protected void createMesh() {
        model = new PrismMesh(this.radius.get(), this.height.get(), this.level.get());
        model.setTextureModeNone(Color.ROYALBLUE);
    }

    private List<Point3D> updateKnotsList(){
        return group.getChildren().stream().filter(Sphere.class::isInstance)
                .filter(n->n.getId().equals("knot"))
                .map(s->s.localToParent(new javafx.geometry.Point3D(0, 0, 0)))
                .map(p->new Point3D((float)p.getX(),(float)p.getY(),(float)p.getZ()))
                .collect(Collectors.toList());
    }
    
    @Override
    protected void addMeshAndListeners() {
        
        enablePicking.addListener((obs,b,b1)->{
            group.getChildren().stream()
                    .filter(Sphere.class::isInstance)
                    .forEach(s->s.setId(b1?"knot":""));
        });
        
        showKnots.addListener((obs, b, b1) ->{
            if (b1) {
                Point3D k0 = ((PrismMesh)model).getAxisOrigin();
                Point3D k3 = ((PrismMesh)model).getAxisEnd();
                final Sphere s = new Sphere(0.2d);
                s.setId("");
                s.getTransforms().add(new Translate(k0.x, k0.y, k0.z));
                s.setMaterial(new PhongMaterial(Color.ROSYBROWN));
                group.getChildren().add(s);
                final Sphere s2 = new Sphere(0.2d);
                s2.setId("");
                s2.getTransforms().add(new Translate(k3.x, k3.y, k3.z));
                s2.setMaterial(new PhongMaterial(Color.GREENYELLOW));
                group.getChildren().add(s2);
                s.getTransforms().addListener((Observable observable) -> {
                    javafx.geometry.Point3D p=s.localToParent(new javafx.geometry.Point3D(0, 0, 0));
                    if(pickingOnDragging.get()){
                        ((PrismMesh)model).setAxisOrigin(new Point3D((float)p.getX(),(float)p.getY(),(float)p.getZ()));
                    } else if(fake!=null){
                        fake.setAxisOrigin(new Point3D((float)p.getX(),(float)p.getY(),(float)p.getZ()));
                    }
                });
                s2.getTransforms().addListener((Observable observable) -> {
                    javafx.geometry.Point3D p=s2.localToParent(new javafx.geometry.Point3D(0, 0, 0));
                    if(pickingOnDragging.get()){
                        ((PrismMesh)model).setAxisEnd(new Point3D((float)p.getX(),(float)p.getY(),(float)p.getZ()));
                    } else if(fake!=null){
                        fake.setAxisEnd(new Point3D((float)p.getX(),(float)p.getY(),(float)p.getZ()));
                    }
                });
            } else {
                group.getChildren().removeIf(Sphere.class::isInstance);
                chkEnablePicking.setSelected(false);
                chkPickingOnDragging.setSelected(false);
            }
        });
        
        pickingProperty().addListener((obs,b,b1)->{
            if(b1 && !pickingOnDragging.get()){ // start picking
                fake=new PrismMesh(((PrismMesh)model).getRadius(),1,0,((PrismMesh)model).getAxisOrigin(),((PrismMesh)model).getAxisEnd());
                fake.setSectionType(((PrismMesh)model).getSectionType());
                fake.setDrawMode(DrawMode.LINE);
                fake.setId("fake");
                group.getChildren().add(fake);
            }
            if(b && !b1){ // after picking
                if(!pickingOnDragging.get()){
                    group.getChildren().removeIf(n->n.getId()!=null && n.getId().equals("fake"));
                    fake=null;
                }
                List<Point3D> list = updateKnotsList();
                if(list!=null && list.size()==2){
                    if(!list.get(0).equals(((PrismMesh)model).getAxisOrigin())){
                        ((PrismMesh)model).setAxisOrigin(list.get(0));
                    } else if(!list.get(1).equals(((PrismMesh)model).getAxisEnd())){
                        ((PrismMesh)model).setAxisEnd(list.get(1));
                    }
                }
                
            }
        });
    }

    private final CheckBoxControl chkKnots = ControlFactory.buildCheckBoxControl(showKnots);
    private final CheckBoxControl chkEnablePicking = ControlFactory.buildCheckBoxControl(enablePicking);
    private final CheckBoxControl chkPickingOnDragging = ControlFactory.buildCheckBoxControl(pickingOnDragging);
        
    @Override
    protected Node buildControlPanel() {
        chkEnablePicking.disableProperty().bind(showKnots.not());
        chkPickingOnDragging.disableProperty().bind(showKnots.not().or(enablePicking.not()));
      
        NumberSliderControl radiusSlider = ControlFactory.buildNumberSlider(this.radius, .01D, 200D);
        radiusSlider.getSlider().setMinorTickCount(10);
        radiusSlider.getSlider().setMajorTickUnit(0.5);
        radiusSlider.getSlider().setBlockIncrement(0.01d);

//        NumberSliderControl heightSlider = ControlFactory.buildNumberSlider(this.height, .01D, 200D);
//        heightSlider.getSlider().setMinorTickCount(10);
//        heightSlider.getSlider().setMajorTickUnit(0.5);
//        heightSlider.getSlider().setBlockIncrement(0.01d);
        
        NumberSliderControl levelSlider = ControlFactory.buildNumberSlider(this.level, 0, 8);
        levelSlider.getSlider().setMinorTickCount(0);
        levelSlider.getSlider().setMajorTickUnit(1);
        levelSlider.getSlider().setBlockIncrement(1);
        levelSlider.getSlider().setSnapToTicks(true);
        
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        geomControls.addControls(chkKnots,chkEnablePicking,chkPickingOnDragging,
                radiusSlider,levelSlider);

        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.drawMode,
                        this.culling
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(this.textureType, this.colors, 
                        this.sectionType, this.textureImage, 
                        this.useBumpMap, this.bumpScale,
                        this.bumpFineScale, this.invert,
                        this.patterns, this.pattScale,
                        this.specColor, this.specularPower, 
                        this.dens, this.func
                )
        );
        
        return this.controlPanel;
    }

}
