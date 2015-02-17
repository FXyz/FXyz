/**
* SphereSegment.java
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

package org.fxyz.shapes;

import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Sean
 */
public class SphereSegment extends Group {

    public boolean selfLightEnabled = true;
    public AmbientLight selfLight = new AmbientLight(Color.WHITE);
    public double radius;
    public Color color;
    public double phimin;
    public double phimax;
    public double thetamin;
    public double thetamax;
    public int granularity;
    public boolean ambient;
    public boolean fill;

    public TriangleMesh mesh;
    public MeshView meshView;

    /**
     * @param radius radius of the sphere segment
     * @param color The sphere segment color.
     * @param phimin The starting azimutal angle [rad], 0-2*pi.
     * @param phimax The ending azimutal angle [rad], 0-2*pi, phimax &gt;
     * phimin.
     * @param thetamin The starting polar angle [rad], -pi/2-pi/2.
     * @param thetamax The ending polar angle [rad], -pi/2-pi/2, thetamax &gt;
     * thetamin.
     * @param granularity The number of segments of curves approximations,
     * granulariy &gt; 2.
     * @param ambient Whether to have an ambient light or not
     * @param fill whether to show filled with the color param or as wire mesh
     */
    public SphereSegment(double radius, Color color,
            double phimin, double phimax, double thetamin, double thetamax,
            int granularity, boolean ambient, boolean fill) {

        this.radius = radius;
        this.color = color;
        this.phimin = phimin;
        this.phimax = phimax;
        this.thetamin = thetamin;
        this.thetamax = thetamax;
        this.granularity = granularity;
        this.ambient = ambient;
        this.fill = fill;
        setDepthTest(DepthTest.ENABLE);

        mesh = new TriangleMesh();
        // Fill Points
        double phi = phimin;
        double theta;

        PhongMaterial maxPhong = new PhongMaterial();
        maxPhong.setSpecularColor(color);
        maxPhong.setDiffuseColor(color);

        for (int i = 0; i < granularity + 1; i++) {
            theta = thetamin;
            for (int j = 0; j < granularity + 1; j++) {
                Point3D p3D = new Point3D((float) (radius * Math.cos(theta) * Math.sin(phi)),
                        (float) (radius * Math.cos(theta) * Math.cos(phi)),
                        (float) (radius * Math.sin(theta)));
                mesh.getPoints().addAll(new Float(p3D.getX()), new Float(p3D.getY()), new Float(p3D.getZ()));
                theta += (thetamax - thetamin) / granularity;
            }
            phi += (phimax - phimin) / granularity;
        }

        //for now we'll just make an empty texCoordinate group
        mesh.getTexCoords().addAll(0, 0);
        //Add the faces "winding" the points generally counter clock wise
        for (int i = 0; i < granularity; i++) {
            int multiplier = (i * granularity) + i;
            //Up the Outside
            for (int j = multiplier; j < granularity + multiplier; j++) {
                mesh.getFaces().addAll(j, 0, j + 1, 0, j + granularity + 1, 0); //lower triangle
                mesh.getFaces().addAll(j + granularity + 1, 0, j + 1, 0, j + granularity + 2, 0); //upper triangle
            }
            //Down the Inside            
            for (int j = granularity + multiplier; j > multiplier; j--) {
                mesh.getFaces().addAll(j, 0, j - 1, 0, j + granularity + 1, 0); //lower triangle
                mesh.getFaces().addAll(j - 1, 0, j + granularity, 0, j + granularity + 1, 0); //upper triangle
            }
        }

        //Create a viewable MeshView to be added to the scene
        //To add a TriangleMesh to a 3D scene you need a MeshView container object
        meshView = new MeshView(mesh);
        //The MeshView allows you to control how the TriangleMesh is rendered
        if (fill) {
            meshView.setDrawMode(DrawMode.FILL);
        } else {
            meshView.setDrawMode(DrawMode.LINE); //show lines only by default
        }
        meshView.setCullFace(CullFace.BACK); //Removing culling to show back lines

        getChildren().add(meshView);
        meshView.setMaterial(maxPhong);
        if (ambient) {
            AmbientLight light = new AmbientLight(Color.WHITE);
            light.getScope().add(meshView);
            getChildren().add(light);
        }
    }

}
