package org.fxyz.samples;

import static javafx.application.Application.launch;
import javafx.scene.Node;
import org.fxyz.TexturedMeshSample;
import org.fxyz.controls.ControlCategory;
import org.fxyz.controls.factory.ControlFactory;
import org.fxyz.shapes.primitives.CuboidMesh;

/**
 *
 * @author jpereda
 */
public class Cuboids extends TexturedMeshSample{
    public static void main(String[] args){launch(args);}
    @Override
    protected void createMesh() {
      model = new CuboidMesh(10f, 12f, 4f, 2);
        //cuboid.setDrawMode(DrawMode.LINE);
        //cuboid.setCullFace(CullFace.NONE);
        // NONE
        //cuboid.setTextureModeNone(Color.ROYALBLUE);
        // IMAGE
        //cuboid.setTextureModeImage(getClass().getResource("res/netCuboid.png").toExternalForm());
        // DENSITY
      
        // FACES
        //ico.setTextureModeFaces(256*256);
       
    }


    @Override
    protected void addMeshAndListeners() {
       /*  boolean testRayIntersection = false;

        if (testRayIntersection) {
            /*
             RAY INTERSECTION
             /
            javafx.geometry.Point3D gloOrigin = new javafx.geometry.Point3D(4, -7, -4);
            javafx.geometry.Point3D gloTarget = new javafx.geometry.Point3D(2, 3, 4);

            javafx.geometry.Point3D gloDirection = gloTarget.subtract(gloOrigin).normalize();
            javafx.geometry.Point3D gloOriginInLoc = model.sceneToLocal(gloOrigin);

            Bounds locBounds = model.getBoundsInLocal();
            Bounds gloBounds = model.localToScene(locBounds);

            Sphere s = new Sphere(0.05d);
            s.getTransforms().add(new Translate(gloOrigin.getX(), gloOrigin.getY(), gloOrigin.getZ()));
            s.setMaterial(new PhongMaterial(Color.GREENYELLOW));
            group.getChildren().add(s);
            s = new Sphere(0.05d);
            s.getTransforms().add(new Translate(gloTarget.getX(), gloTarget.getY(), gloTarget.getZ()));
            s.setMaterial(new PhongMaterial(Color.GREENYELLOW));
            group.getChildren().add(s);

            javafx.geometry.Point3D dir = gloTarget.subtract(gloOrigin).crossProduct(new javafx.geometry.Point3D(0, -1, 0));
            double angle = Math.acos(gloTarget.subtract(gloOrigin).normalize().dotProduct(new javafx.geometry.Point3D(0, -1, 0)));
            double h1 = gloTarget.subtract(gloOrigin).magnitude();
            Cylinder c = new Cylinder(0.03d, h1);
            c.getTransforms().addAll(new Translate(gloOrigin.getX(), gloOrigin.getY() - h1 / 2d, gloOrigin.getZ()),
                    new Rotate(-Math.toDegrees(angle), 0d, h1 / 2d, 0d,
                            new javafx.geometry.Point3D(dir.getX(), -dir.getY(), dir.getZ())));
            c.setMaterial(new PhongMaterial(Color.GREEN));
            group.getChildren().add(c);

            group.getChildren().add(new Axes(0.02));
            Box box = new Box(gloBounds.getWidth(), gloBounds.getHeight(), gloBounds.getDepth());
            box.setDrawMode(DrawMode.LINE);
            box.setMaterial(new PhongMaterial(Color.BLUEVIOLET));
            box.getTransforms().add(new Translate(gloBounds.getMinX() + gloBounds.getWidth() / 2d,
                    gloBounds.getMinY() + gloBounds.getHeight() / 2d, gloBounds.getMinZ() + gloBounds.getDepth() / 2d));
    //        group.getChildren().add(box);

            /*
             FIRST STEP; Check the ray crosses the bounding box of the shape at any of
             its 6 faces
             /
            List<javafx.geometry.Point3D> normals = Arrays.asList(new javafx.geometry.Point3D(-1, 0, 0), new javafx.geometry.Point3D(1, 0, 0), new javafx.geometry.Point3D(0, -1, 0),
                    new javafx.geometry.Point3D(0, 1, 0), new javafx.geometry.Point3D(0, 0, -1), new javafx.geometry.Point3D(0, 0, 1));
            List<javafx.geometry.Point3D> positions = Arrays.asList(new javafx.geometry.Point3D(locBounds.getMinX(), 0, 0), new javafx.geometry.Point3D(locBounds.getMaxX(), 0, 0),
                    new javafx.geometry.Point3D(0, locBounds.getMinY(), 0), new javafx.geometry.Point3D(0, locBounds.getMaxY(), 0),
                    new javafx.geometry.Point3D(0, 0, locBounds.getMinZ()), new javafx.geometry.Point3D(0, 0, locBounds.getMaxZ()));
            AtomicInteger counter = new AtomicInteger();
            IntStream.range(0, 6).forEach(i -> {
                // ray[t]= ori+t.dir; t/ray[t]=P in plane
                // plane P·N+d=0->(ori+t*dir)·N+d=0->t=-(ori.N+d)/(dir.N)
                // P=P(x,y,z), N={a,b,c}, d=-a·x0-b·y0-c·z0
                double d = -normals.get(i).dotProduct(positions.get(i));
                double t = -(gloOriginInLoc.dotProduct(normals.get(i)) + d) / (gloDirection.dotProduct(normals.get(i)));
                javafx.geometry.Point3D locInter = gloOriginInLoc.add(gloDirection.multiply(t));
                if (locBounds.contains(locInter)) {
                    counter.getAndIncrement();

                    javafx.geometry.Point3D gloInter = model.localToScene(locInter);
                    Sphere s2 = new Sphere(0.1d);
                    s2.getTransforms().add(new Translate(gloInter.getX(), gloInter.getY(), gloInter.getZ()));
                    s2.setMaterial(new PhongMaterial(Color.GOLD));
                    //                group.getChildren().add(s2);
                }
            });
            if (counter.get() > 0) {
                /*
                 SECOND STEP: Check if the ray crosses any of the triangles of the mesh
                 /
                // triangle mesh
                org.fxyz.geometry.Point3D gloOriginInLoc1 = new org.fxyz.geometry.Point3D((float) gloOriginInLoc.getX(), (float) gloOriginInLoc.getY(), (float) gloOriginInLoc.getZ());
                org.fxyz.geometry.Point3D gloDirection1 = new org.fxyz.geometry.Point3D((float) gloDirection.getX(), (float) gloDirection.getY(), (float) gloDirection.getZ());

                System.out.println("number of intersections: " + model.getIntersections(gloOriginInLoc1, gloDirection1));
            }
        }
               */
    }

    @Override
    protected Node buildControlPanel() {
        ControlCategory geomControls = ControlFactory.buildCategory("Geometry");
        //geomControls.addControls()

        this.controlPanel = ControlFactory.buildControlPanel(
                ControlFactory.buildMeshViewCategory(
                        this.useDiffMap,
                        this.drawMode,
                        this.culling,
                        this.material.diffuseColorProperty(),
                        this.material.specularColorProperty()
                ),
                geomControls,
                ControlFactory.buildTextureMeshCategory(this.textureType, this.colors, this.sectionType, this.useDiffMap, this.material.diffuseMapProperty(), this.pattScale, this.densMax)
        );
        
        return this.controlPanel;
    }

}
