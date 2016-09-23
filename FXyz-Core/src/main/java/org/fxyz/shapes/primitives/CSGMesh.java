/**
 * CSGMesh.java
 *
 * Copyright (c) 2013-2016, F(X)yz
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

package org.fxyz.shapes.primitives;

import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.Vertex;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.DepthTest;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.geometry.Face3;
import org.fxyz.geometry.Point3D;

/**
 *
 * @author José Pereda Llamas
 * Created on 01-may-2015 - 12:20:06
 */
public class CSGMesh extends TexturedMesh {
    
    private final CSG primitive;
    
    public CSGMesh(CSG primitive){
        this.primitive=primitive;
        
        updateMesh();
        setCullFace(CullFace.BACK);
        setDrawMode(DrawMode.FILL);
        setDepthTest(DepthTest.ENABLE);
    }

    @Override
    protected final void updateMesh() {
        setMesh(null);
        mesh=createCSGMesh();
        setMesh(mesh);
    }
    
    private TriangleMesh createCSGMesh(){
        List<Vertex> vertices = new ArrayList<>();
        List<List<Integer>> indices = new ArrayList<>();

        listVertices.clear();
        primitive.getPolygons().forEach(p -> {
            List<Integer> polyIndices = new ArrayList<>();
            
            p.vertices.forEach(v -> {
                if (!vertices.contains(v)) {
                    vertices.add(v);
                    listVertices.add(new Point3D((float)v.pos.x, (float)v.pos.y, (float)v.pos.z));
                    polyIndices.add(vertices.size());
                } else {
                    polyIndices.add(vertices.indexOf(v) + 1);
                }
            });

            indices.add(polyIndices);
            
        });
        
        textureCoords=new float[]{0f,0f};
        listTextures.clear();
        listFaces.clear();
        indices.forEach(pVerts-> {
            int index1 = pVerts.get(0);
            for (int i = 0; i < pVerts.size() - 2; i++) {
                int index2 = pVerts.get(i + 1);
                int index3 = pVerts.get(i + 2);

                listTextures.add(new Face3(0, 0, 0));
                listFaces.add(new Face3(index1-1, index2-1, index3-1));
            }
        });
        int[] faceSmoothingGroups = new int[listFaces.size()];
        smoothingGroups=faceSmoothingGroups;
        
        return createMesh();
    }
}
