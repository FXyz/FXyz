/*
 * Copyright (c) 2014, 2016, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.fxyz3d.importers.obj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import org.fxyz3d.importers.Importer;
import org.fxyz3d.importers.SmoothingGroups;
import org.fxyz3d.shapes.polygon.PolygonMesh;
import org.fxyz3d.shapes.polygon.PolygonMeshView;

/**
 * object loader
 */
public class ObjOrPolyObjImporter implements Importer {

    private static boolean debug = false;
    private static float scale = 1;
    private static boolean flatXZ = false;

    private Map<String, TriangleMesh> meshes = new HashMap<>();
    private Map<String, Material> materials = new HashMap<>();
    private List<Map<String, Material>> materialLibrary = new ArrayList<>();

    private FloatArrayList vertexes = new FloatArrayList();
    private FloatArrayList uvs = new FloatArrayList();
    private IntegerArrayList faces = new IntegerArrayList();
    private IntegerArrayList smoothingGroups = new IntegerArrayList();
    private FloatArrayList normals = new FloatArrayList();
    private IntegerArrayList faceNormals = new IntegerArrayList();
    private Material material = new PhongMaterial(Color.WHITE);
    private int facesStart = 0;
    private int facesNormalStart = 0;
    private int smoothingGroupsStart = 0;

    private Map<String, PolygonMesh> polygonMeshes = new HashMap<>();
    private List<int[]> facesPolygon = new ArrayList<>();
    private List<int[]> faceNormalsPolygon = new ArrayList<>();


    private final Group root = new Group();

    private boolean isPolygon = false;

    @Override
    public void load(URL url) throws IOException {
        read(url, false);
    }

    @Override
    public void loadAsPolygonMesh(URL url) throws IOException {
        isPolygon = true;
        read(url, true);
    }

    @Override
    public Group getRoot() {
        return root;
    }

    @Override
    public boolean isSupported(String extension) {
        return extension != null && extension.equals("obj");
    }

    @Override
    public Timeline getTimeline() {
        return null;
    }

    public Set<String> getMeshes() {
        return isPolygon ? polygonMeshes.keySet() : meshes.keySet();
    }

    public TriangleMesh getMesh() {
        return meshes.values().iterator().next();
    }

    public Material getMaterial() {
        return materials.values().iterator().next();
    }

    public TriangleMesh getMesh(String key) {
        return meshes.get(key);
    }

    public Material getMaterial(String key) {
        return materials.get(key);
    }

    public PolygonMesh getPolygonMesh() {
        return polygonMeshes.values().iterator().next();
    }

    public PolygonMesh getPolygonMesh(String key) {
        return polygonMeshes.get(key);
    }

    public MeshView buildMeshView(String key) {
        MeshView meshView = new MeshView();
        meshView.setId(key);
        meshView.setMaterial(materials.get(key));
        meshView.setMesh(meshes.get(key));
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }

    public PolygonMeshView buildPolygonMeshView(String key) {
        PolygonMeshView polygonMeshView = new PolygonMeshView();
        polygonMeshView.setId(key);
        polygonMeshView.setMaterial(materials.get(key));
        polygonMeshView.setMesh(polygonMeshes.get(key));
//        polygonMeshView.setCullFace(CullFace.NONE); TODO
        return polygonMeshView;
    }

    private void read(URL url, boolean asPolygon) throws IOException {
        log("Reading from URL: " + url + " as polygon: " + asPolygon);

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        int currentSmoothGroup = 0;
        String key = "default";

        while ((line = br.readLine()) != null) {
            try {
                if (line.startsWith("g ") || line.equals("g")) {


                    if (asPolygon) {
                        addPolygonMesh(key);
                    } else {
                        addMesh(key);
                    }


                    key = line.length() > 2 ? line.substring(2) : "default";
                    log("key = " + key);
                } else if (line.startsWith("v ")) {
                    String[] split = line.substring(2).trim().split(" +");
                    float x = Float.parseFloat(split[0]) * scale;
                    float y = Float.parseFloat(split[1]) * scale;
                    float z = Float.parseFloat(split[2]) * scale;

                    //                log("x = " + x + ", y = " + y + ", z = " + z);

                    vertexes.add(x);
                    vertexes.add(y);
                    vertexes.add(z);

                    if (flatXZ) {
                        uvs.add(x);
                        uvs.add(z);
                    }
                } else if (line.startsWith("vt ")) {
                    String[] split = line.substring(3).trim().split(" +");
                    float u = split[0].trim().equalsIgnoreCase("nan") ? Float.NaN : Float.parseFloat(split[0]);
                    float v = split[1].trim().equalsIgnoreCase("nan") ? Float.NaN : Float.parseFloat(split[1]);

                    //                log("u = " + u + ", v = " + v);

                    uvs.add(u);
                    uvs.add(1 - v);
                } else if (line.startsWith("f ")) {

                    if (!asPolygon) {

                        String[] split = line.substring(2).trim().split(" +");
                        int[][] data = new int[split.length][];
                        boolean uvProvided = true;
                        boolean normalProvided = true;
                        for (int i = 0; i < split.length; i++) {
                            String[] split2 = split[i].split("/");
                            if (split2.length < 2) {
                                uvProvided = false;
                            }
                            if (split2.length < 3) {
                                normalProvided = false;
                            }
                            data[i] = new int[split2.length];
                            for (int j = 0; j < split2.length; j++) {
                                if (split2[j].length() == 0) {
                                    data[i][j] = 0;
                                    if (j == 1) {
                                        uvProvided = false;
                                    }
                                    if (j == 2) {
                                        normalProvided = false;
                                    }
                                } else {
                                    data[i][j] = Integer.parseInt(split2[j]);
                                }
                            }
                        }
                        int v1 = vertexIndex(data[0][0]);
                        int uv1 = -1;
                        int n1 = -1;
                        if (uvProvided && !flatXZ) {
                            uv1 = uvIndex(data[0][1]);
                            if (uv1 < 0) {
                                uvProvided = false;
                            }
                        }
                        if (normalProvided) {
                            n1 = normalIndex(data[0][2]);
                            if (n1 < 0) {
                                normalProvided = false;
                            }
                        }
                        for (int i = 1; i < data.length - 1; i++) {
                            int v2 = vertexIndex(data[i][0]);
                            int v3 = vertexIndex(data[i + 1][0]);
                            int uv2 = -1;
                            int uv3 = -1;
                            int n2 = -1;
                            int n3 = -1;
                            if (uvProvided && !flatXZ) {
                                uv2 = uvIndex(data[i][1]);
                                uv3 = uvIndex(data[i + 1][1]);
                            }
                            if (normalProvided) {
                                n2 = normalIndex(data[i][2]);
                                n3 = normalIndex(data[i + 1][2]);
                            }

                            //                    log("v1 = " + v1 + ", v2 = " + v2 + ", v3 = " + v3);
                            //                    log("uv1 = " + uv1 + ", uv2 = " + uv2 + ", uv3 = " + uv3);

                            faces.add(v1);
                            faces.add(uv1);
                            faces.add(v2);
                            faces.add(uv2);
                            faces.add(v3);
                            faces.add(uv3);
                            faceNormals.add(n1);
                            faceNormals.add(n2);
                            faceNormals.add(n3);

                            smoothingGroups.add(currentSmoothGroup);
                        }

                    } else {

                        String[] split = line.substring(2).trim().split(" +");
                        int[] faceIndexes = new int[split.length*2];
                        int[] faceNormalIndexes = new int[split.length];
                        for (int i = 0; i < split.length; i++) {
                            String[] split2 = split[i].split("/");
                            faceIndexes[i*2] = vertexIndex(Integer.parseInt(split2[0]));
                            faceIndexes[(i*2)+1] = (split2.length > 1 && split2[1].length()>0) ? uvIndex(Integer.parseInt(split2[1])) : -1;
                            faceNormalIndexes[i] = (split2.length > 2 && split2[2].length()>0) ? normalIndex(Integer.parseInt(split2[2])) : -1;
                        }
                        facesPolygon.add(faceIndexes);
                        faceNormalsPolygon.add(faceNormalIndexes);
                        smoothingGroups.add(currentSmoothGroup);
                    }



                } else if (line.startsWith("s ")) {
                    if (line.substring(2).equals("off")) {
                        currentSmoothGroup = 0;
                    } else {
                        currentSmoothGroup = Integer.parseInt(line.substring(2));
                    }
                } else if (line.startsWith("mtllib ")) {
                    // setting materials lib
                    String[] split = line.substring("mtllib ".length()).trim().split(" +");
                    for (String filename : split) {
                        MtlReader mtlReader = new MtlReader(filename, url.toExternalForm());
                        materialLibrary.add(mtlReader.getMaterials());
                    }
                } else if (line.startsWith("usemtl ")) {


                    if (asPolygon) {
                        addPolygonMesh(key);
                    } else {
                        addMesh(key);
                    }

                    // setting new material for next mesh
                    String materialName = line.substring("usemtl ".length());
                    for (Map<String, Material> mm : materialLibrary) {
                        Material m = mm.get(materialName);
                        if (m != null) {
                            material = m;
                            break;
                        }
                    }
                } else if (line.isEmpty() || line.startsWith("#")) {
                    // comments and empty lines are ignored
                } else if (line.startsWith("vn ")) {
                    String[] split = line.substring(2).trim().split(" +");
                    float x = Float.parseFloat(split[0]);
                    float y = Float.parseFloat(split[1]);
                    float z = Float.parseFloat(split[2]);
                    normals.add(x);
                    normals.add(y);
                    normals.add(z);
                } else {
                    log("line skipped: " + line);
                }
            } catch (Exception ex) {
                Logger.getLogger(MtlReader.class.getName()).log(Level.SEVERE, "Failed to parse line:" + line, ex);
            }
        }

        if (asPolygon) {
            addPolygonMesh(key);
        } else {
            addMesh(key);
        }

        log("Totally loaded " + (vertexes.size() / 3.) + " vertexes, "
                        + (uvs.size() / 2.) + " uvs, "
                        + ((isPolygon ? facesPolygon.size() : faces.size()) / 6.) + " faces, "
                        + smoothingGroups.size() + " smoothing groups.");

        if (asPolygon) {
            for (String mesh : getMeshes()) {
                root.getChildren().add(buildPolygonMeshView(mesh));
            }
        } else {
            for (String mesh : getMeshes()) {
                root.getChildren().add(buildMeshView(mesh));
            }
        }
    }

    private void addMesh(String key) {
        if (facesStart >= faces.size()) {
            // we're only interested in faces
            smoothingGroupsStart = smoothingGroups.size();
            return;
        }
        Map<Integer, Integer> vertexMap = new HashMap<>(vertexes.size() / 2);
        Map<Integer, Integer> uvMap = new HashMap<>(uvs.size() / 2);
        Map<Integer, Integer> normalMap = new HashMap<>(normals.size() / 2);
        FloatArrayList newVertexes = new FloatArrayList(vertexes.size() / 2);
        FloatArrayList newUVs = new FloatArrayList(uvs.size() / 2);
        FloatArrayList newNormals = new FloatArrayList(normals.size() / 2);
        boolean useNormals = true;

        for (int i = facesStart; i < faces.size(); i += 2) {
            int vi = faces.get(i);
            Integer nvi = vertexMap.get(vi);
            if (nvi == null) {
                nvi = newVertexes.size() / 3;
                vertexMap.put(vi, nvi);
                newVertexes.add(vertexes.get(vi * 3));
                newVertexes.add(vertexes.get(vi * 3 + 1));
                newVertexes.add(vertexes.get(vi * 3 + 2));
            }
            faces.set(i, nvi);

            int uvi = faces.get(i + 1);
            Integer nuvi = uvMap.get(uvi);
            if (nuvi == null) {
                nuvi = newUVs.size() / 2;
                uvMap.put(uvi, nuvi);
                if (uvi >= 0) {
                    newUVs.add(uvs.get(uvi * 2));
                    newUVs.add(uvs.get(uvi * 2 + 1));
                } else {
                    newUVs.add(0f);
                    newUVs.add(0f);
                }
            }
            faces.set(i + 1, nuvi);

            if (useNormals) {
                int ni = faceNormals.get(i/2);
                Integer nni = normalMap.get(ni);
                if (nni == null) {
                    nni = newNormals.size() / 3;
                    normalMap.put(ni, nni);
                    if (ni >= 0 && normals.size() >= (ni+1)*3) {
                        newNormals.add(normals.get(ni * 3));
                        newNormals.add(normals.get(ni * 3 + 1));
                        newNormals.add(normals.get(ni * 3 + 2));
                    } else {
                        useNormals = false;
                        newNormals.add(0f);
                        newNormals.add(0f);
                        newNormals.add(0f);
                    }
                }
                faceNormals.set(i/2, nni);
            }
        }

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(newVertexes.toFloatArray());
        mesh.getTexCoords().setAll(newUVs.toFloatArray());
        mesh.getFaces().setAll(((IntegerArrayList) faces.subList(facesStart, faces.size())).toIntArray());

        // Use normals if they are provided
        if (useNormals) {
            int[] newFaces = ((IntegerArrayList) faces.subList(facesStart, faces.size())).toIntArray();
            int[] newFaceNormals = ((IntegerArrayList) faceNormals.subList(facesNormalStart, faceNormals.size())).toIntArray();
            int[] smGroups = SmoothingGroups.calcSmoothGroups(mesh, newFaces, newFaceNormals, newNormals.toFloatArray());
            mesh.getFaceSmoothingGroups().setAll(smGroups);
        } else {
            mesh.getFaceSmoothingGroups().setAll(((IntegerArrayList) smoothingGroups.subList(smoothingGroupsStart, smoothingGroups.size())).toIntArray());
        }

        int keyIndex = 2;
        String keyBase = key;
        while (meshes.get(key) != null) {
            key = keyBase + " (" + keyIndex++ + ")";
        }
        meshes.put(key, mesh);
        materials.put(key, material);

        log(
                "Added mesh '" + key + "' of " + mesh.getPoints().size() / mesh.getPointElementSize() + " vertexes, "
                        + mesh.getTexCoords().size() / mesh.getTexCoordElementSize() + " uvs, "
                        + mesh.getFaces().size() / mesh.getFaceElementSize() + " faces, "
                        + mesh.getFaceSmoothingGroups().size() + " smoothing groups.");
        log("material diffuse color = " + ((PhongMaterial) material).getDiffuseColor());
        log("material diffuse map = " + ((PhongMaterial) material).getDiffuseMap());

        facesStart = faces.size();
        facesNormalStart = faceNormals.size();
        smoothingGroupsStart = smoothingGroups.size();
    }

    private void addPolygonMesh(String key) {
        if (facesStart >= facesPolygon.size()) {
            // we're only interested in faces
            smoothingGroupsStart = smoothingGroups.size();
            return;
        }
        Map<Integer, Integer> vertexMap = new HashMap<>(vertexes.size() / 2);
        Map<Integer, Integer> uvMap = new HashMap<>(uvs.size() / 2);
        Map<Integer, Integer> normalMap = new HashMap<>(normals.size() / 2);
        FloatArrayList newVertexes = new FloatArrayList(vertexes.size() / 2);
        FloatArrayList newUVs = new FloatArrayList(uvs.size() / 2);
        FloatArrayList newNormals = new FloatArrayList(normals.size() / 2);
        boolean useNormals = true;

        int[][] faceArrays = new int[facesPolygon.size()-facesStart][];
        int[][] faceNormalArrays = new int[faceNormalsPolygon.size()-facesNormalStart][];

        for (int i = facesStart; i < facesPolygon.size();i++) {
            int[] faceIndexes = facesPolygon.get(i);
            int[] faceNormalIndexes = faceNormalsPolygon.get(i);
            for (int j=0;j<faceIndexes.length;j+=2){
                int vi = faceIndexes[j];
                Integer nvi = vertexMap.get(vi);
                if (nvi == null) {
                    nvi = newVertexes.size() / 3;
                    vertexMap.put(vi, nvi);
                    newVertexes.add(vertexes.get(vi * 3));
                    newVertexes.add(vertexes.get(vi * 3 + 1));
                    newVertexes.add(vertexes.get(vi * 3 + 2));
                }
                faceIndexes[j] = nvi;
//                faces.set(i, nvi);
                int uvi = faceIndexes[j+1];
                Integer nuvi = uvMap.get(uvi);
                if (nuvi == null) {
                    nuvi = newUVs.size() / 2;
                    uvMap.put(uvi, nuvi);
                    if (uvi >= 0) {
                        newUVs.add(uvs.get(uvi * 2));
                        newUVs.add(uvs.get(uvi * 2 + 1));
                    } else {
                        newUVs.add(0f);
                        newUVs.add(0f);
                    }
                }
                faceIndexes[j+1] = nuvi;
//                faces.set(i + 1, nuvi);

                int ni = faceNormalIndexes[j/2];
                Integer nni = normalMap.get(ni);
                if (nni == null) {
                    nni = newNormals.size() / 3;
                    normalMap.put(ni, nni);
                    if (ni >= 0 && normals.size() >= (ni+1)*3) {
                        newNormals.add(normals.get(ni * 3));
                        newNormals.add(normals.get(ni * 3 + 1));
                        newNormals.add(normals.get(ni * 3 + 2));
                    } else {
                        useNormals = false;
                        newNormals.add(0f);
                        newNormals.add(0f);
                        newNormals.add(0f);
                    }
                }
                faceNormalIndexes[j/2] = nni;
            }
            faceArrays[i-facesStart] = faceIndexes;
            faceNormalArrays[i-facesNormalStart] = faceNormalIndexes;
        }

        PolygonMesh mesh = new PolygonMesh(
                newVertexes.toFloatArray(),
                newUVs.toFloatArray(),
                faceArrays
        );

        // Use normals if they are provided
        if (useNormals) {
            int[] smGroups = SmoothingGroups.calcSmoothGroups(faceArrays, faceNormalArrays, newNormals.toFloatArray());
            mesh.getFaceSmoothingGroups().setAll(smGroups);
        } else {
            mesh.getFaceSmoothingGroups().setAll(((IntegerArrayList) smoothingGroups.subList(smoothingGroupsStart, smoothingGroups.size())).toIntArray());
        }

        if (debug) {
            System.out.println("mesh.points = " + mesh.getPoints());
            System.out.println("mesh.texCoords = " + mesh.getTexCoords());
            System.out.println("mesh.faces: ");
            for (int[] face: mesh.faces) {
                //System.out.println("    face:: "+Arrays.toString(face));
            }
        }

        int keyIndex = 2;
        String keyBase = key;
        while (polygonMeshes.get(key) != null) {
            key = keyBase + " (" + keyIndex++ + ")";
        }
        polygonMeshes.put(key, mesh);
        materials.put(key, material);

        log("Added mesh '" + key + "' of " + (mesh.getPoints().size()/3) + " vertexes, "
                + (mesh.getTexCoords().size()/2) + " uvs, "
                + mesh.faces.length + " faces, "
                + 0 + " smoothing groups.");
        log("material diffuse color = " + ((PhongMaterial) material).getDiffuseColor());
        log("material diffuse map = " + ((PhongMaterial) material).getDiffuseMap());

        facesStart = facesPolygon.size();
        facesNormalStart = faceNormalsPolygon.size();
        smoothingGroupsStart = smoothingGroups.size();
    }

    static void log(String string) {
        if (debug) {
            System.out.println(string);
        }
    }

    public static void setFlatXZ(boolean flatXZ) {
        ObjOrPolyObjImporter.flatXZ = flatXZ;
    }

    public static void setDebug(boolean debug) {
        ObjOrPolyObjImporter.debug = debug;
    }

    public static void setScale(float scale) {
        ObjOrPolyObjImporter.scale = scale;
    }

    private int vertexIndex(int vertexIndex) {
        if (vertexIndex < 0) {
            return vertexIndex + vertexes.size() / 3;
        } else {
            return vertexIndex - 1;
        }
    }

    private int uvIndex(int uvIndex) {
        if (uvIndex < 0) {
            return uvIndex + uvs.size() / 2;
        } else {
            return uvIndex - 1;
        }
    }

    private int normalIndex(int normalIndex) {
        if (normalIndex < 0) {
            return normalIndex + normals.size() / 3;
        } else {
            return normalIndex - 1;
        }
    }
}
