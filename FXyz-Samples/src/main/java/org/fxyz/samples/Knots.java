package org.fxyz.samples;

import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.AnimationTimer;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import org.fxyz.ShapeBaseSample;
import org.fxyz.controls.ControlPanel;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.geometry.DensityFunction;
import org.fxyz.geometry.Point3D;
import org.fxyz.shapes.primitives.KnotMesh;
import org.fxyz.shapes.primitives.helper.TriangleMeshHelper.SectionType;

/**
 *
 * @author jpereda
 */
public class Knots extends ShapeBaseSample<KnotMesh> {

    private DensityFunction<Point3D> dens = p -> (double) p.x;
    private long lastEffect;

    private final Property<Boolean> useMaterial = new SimpleBooleanProperty(this, "Use PhongMaterial", false);
    private final Property<DrawMode> drawMode = new SimpleObjectProperty<>();
    private final Property<CullFace> culling = new SimpleObjectProperty<>();
    
    @Override
    protected void createMesh() {
        model = new KnotMesh(2d, 1d, 0.4d, 2d, 3d, 1000, 60, 0, 0);
        model.setDrawMode(DrawMode.LINE);
        model.setCullFace(CullFace.NONE);
        model.setSectionType(SectionType.TRIANGLE);
        model.setTextureModeNone(Color.BROWN);
    }

    @Override
    protected void addMeshAndListeners() {
        
        
        
        model.drawModeProperty().bindBidirectional(drawMode); // DOES NOT work binding the other way prop->mesh
        model.cullFaceProperty().bindBidirectional(culling);       
        
        useMaterial.addListener(i -> {
            if (useMaterial.getValue()) {
                model.setMaterial(material);
            } else if (!useMaterial.getValue()) {
                model.setMaterial(null);
                model.setTextureModeNone(Color.BROWN);
            }
        });

        model.getTransforms().addAll(new Rotate(0, Rotate.X_AXIS), rotateY);

        // IMAGE
//        knot.setTextureModeImage(getClass().getResource("res/LaminateSteel.jpg").toExternalForm());
        // PATTERN
//       knot.setTextureModePattern(3d);
        // FUNCTION
//        knot.setTextureModeVertices1D(256*256,t->t*t);
        // DENSITY
//        knot.setTextureModeVertices3D(256*256,dens);
        // FACES
//        knot.setTextureModeFaces(256*256);
        lastEffect = System.nanoTime();
        AtomicInteger count = new AtomicInteger();
        AnimationTimer timerEffect = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (now > lastEffect + 100_000_000l) {
//                    Point3D loc = knot.getPositionAt((count.get()%100)*2d*Math.PI/100d);
//                    Point3D dir = knot.getTangentAt((count.get()%100)*2d*Math.PI/100d);
//                    cameraTransform.t.setX(loc.x);
//                    cameraTransform.t.setY(loc.y);
//                    cameraTransform.t.setZ(-loc.z);
//                    javafx.geometry.Point3D axis = cameraTransform.rx.getAxis();
//                    javafx.geometry.Point3D cross = axis.crossProduct(-dir.x,-dir.y,-dir.z);
//                    double angle = axis.angle(-dir.x,-dir.y,-dir.z);
//                    cameraTransform.rx.setAngle(angle);
//                    cameraTransform.rx.setAxis(new javafx.geometry.Point3D(cross.getX(),-cross.getY(),cross.getZ()));
//                    dens = p->(float)(p.x*Math.cos(count.get()%100d*2d*Math.PI/50d)+p.y*Math.sin(count.get()%100d*2d*Math.PI/50d));
//                    knot.setDensity(dens);
//                    knot.setP(1+(count.get()%5));
//                    knot.setQ(2+(count.get()%15));

//                    if(count.get()%100<50){
//                        knot.setDrawMode(DrawMode.LINE);
//                    } else {
//                        knot.setDrawMode(DrawMode.FILL);
//                    }
//                    knot.setColors((int)Math.pow(2,count.get()%16));
//                    knot.setMajorRadius(0.5d+(count.get()%10));
//                    knot.setMinorRadius(2d+(count.get()%60));
//                    knot.setWireRadius(0.1d+(count.get()%6)/10d);
//                    knot.setPatternScale(1d+(count.get()%10)*3d);
//                    knot.setSectionType(SectionType.values()[count.get()%SectionType.values().length]);
                    count.getAndIncrement();
                    lastEffect = now;
                }
            }
        };

        group.getChildren().add(model);
    }

    @Override
    public String getSampleDescription() {
        return super.getSampleDescription(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node getControlPanel() {
        ControlPanel panel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        useMaterial,
                        drawMode,
                        culling,
                        material.diffuseColorProperty(),
                        material.specularColorProperty())
        );
        return panel; //To change body of generated methods, choose Tools | Templates.

    }

}
