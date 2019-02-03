/*
 * Copyright (c) 2019 F(X)yz
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
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import org.fxyz3d.importers.Importer;
import org.fxyz3d.importers.Model3D;
import org.fxyz3d.importers.SmoothingGroups;
import org.fxyz3d.shapes.polygon.PolygonMesh;
import org.fxyz3d.shapes.polygon.PolygonMeshView;

/**
 * object loader
 */
public class ObjImporter implements Importer {

    private static final String SUPPORTED_EXT = "obj";

    private static boolean debug = false;
    private static float scale = 1;
    private static boolean flatXZ = false;

    static void log(String string) {
        if (debug) {
            System.out.println(string);
        }
    }

    public static void setFlatXZ(boolean flatXZ) {
        ObjImporter.flatXZ = flatXZ;
    }

    public static void setDebug(boolean debug) {
        ObjImporter.debug = debug;
    }

    public static void setScale(float scale) {
        ObjImporter.scale = scale;
    }

    private static Map<Predicate<String>, BiConsumer<String, ObjModel>> parsers = new LinkedHashMap<>();

    static {
        parsers.put(l -> l.startsWith("g ") || l.equals("g"), ObjImporter::parseG);
        parsers.put(l -> l.startsWith("v "), ObjImporter::parseV);
        parsers.put(l -> l.startsWith("vt "), ObjImporter::parseVt);
        parsers.put(l -> l.startsWith("f "), ObjImporter::parseF);
        parsers.put(l -> l.startsWith("s "), ObjImporter::parseS);
        parsers.put(l -> l.startsWith("mtllib "), ObjImporter::parseMtllib);
        parsers.put(l -> l.startsWith("usemtl "), ObjImporter::parseUsemtl);
        parsers.put(l -> l.startsWith("vn "), ObjImporter::parseVn);

        // comments and empty lines are ignored
        parsers.put(l -> l.isEmpty() || l.startsWith("#"), (l, m) -> {});

        // if nothing matched, then skip line
        parsers.put(l -> true, (l, m) -> log("line skipped: " + l));
    }

    @Override
    public Model3D load(URL url) throws IOException {
        return read(url, false);
    }

    @Override
    public Model3D loadAsPoly(URL url) throws IOException {
        return read(url, true);
    }

    @Override
    public boolean isSupported(String extension) {
        return SUPPORTED_EXT.equals(extension);
    }

    private ObjModel read(URL url, boolean asPolygon) throws IOException {
        log("Reading from URL: " + url + " as polygon: " + asPolygon);

        ObjModel model = asPolygon ? new PolyObjModel(url) : new ObjModel(url);

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;

        while ((line = br.readLine()) != null) {
            try {
                // if thread-safety is needed, keySet() can be extracted
                for (Predicate<String> condition : parsers.keySet()) {
                    if (condition.test(line)) {
                        parsers.get(condition).accept(line, model);
                        break;
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(ObjImporter.class.getName()).log(Level.SEVERE, "Failed to parse line: " + line, e);
            }
        }

        br.close();

        model.addMesh(model.key);


        log("Totally loaded " + (model.vertices.size() / 3.) + " vertices, "
                + (model.uvs.size() / 2.) + " uvs, "
                + (model.numFaces() / 6.) + " faces, "
                + model.smoothingGroups.size() + " smoothing groups.");

        model.loadComplete();

        return model;
    }

    private static void parseG(String line, ObjModel model) {
        model.addMesh(model.key);

        model.key = line.length() > 2 ? line.substring(2) : "default";
        log("key = " + model.key);
    }

    private static void parseV(String line, ObjModel model) {
        String[] split = line.substring(2).trim().split(" +");
        float x = Float.parseFloat(split[0]) * scale;
        float y = Float.parseFloat(split[1]) * scale;
        float z = Float.parseFloat(split[2]) * scale;

        model.vertices.add(x);
        model.vertices.add(y);
        model.vertices.add(z);

        if (flatXZ) {
            model.uvs.add(x);
            model.uvs.add(z);
        }
    }

    private static void parseVt(String line, ObjModel model) {
        String[] split = line.substring(3).trim().split(" +");
        float u = split[0].trim().equalsIgnoreCase("nan") ? Float.NaN : Float.parseFloat(split[0]);
        float v = split[1].trim().equalsIgnoreCase("nan") ? Float.NaN : Float.parseFloat(split[1]);

        model.uvs.add(u);
        model.uvs.add(1 - v);
    }

    private static void parseF(String line, ObjModel model) {
        if (!model.isPolygon()) {

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
            int v1 = model.vertexIndex(data[0][0]);
            int uv1 = -1;
            int n1 = -1;
            if (uvProvided && !flatXZ) {
                uv1 = model.uvIndex(data[0][1]);
                if (uv1 < 0) {
                    uvProvided = false;
                }
            }
            if (normalProvided) {
                n1 = model.normalIndex(data[0][2]);
                if (n1 < 0) {
                    normalProvided = false;
                }
            }
            for (int i = 1; i < data.length - 1; i++) {
                int v2 = model.vertexIndex(data[i][0]);
                int v3 = model.vertexIndex(data[i + 1][0]);
                int uv2 = -1;
                int uv3 = -1;
                int n2 = -1;
                int n3 = -1;
                if (uvProvided && !flatXZ) {
                    uv2 = model.uvIndex(data[i][1]);
                    uv3 = model.uvIndex(data[i + 1][1]);
                }
                if (normalProvided) {
                    n2 = model.normalIndex(data[i][2]);
                    n3 = model.normalIndex(data[i + 1][2]);
                }

                //                    log("v1 = " + v1 + ", v2 = " + v2 + ", v3 = " + v3);
                //                    log("uv1 = " + uv1 + ", uv2 = " + uv2 + ", uv3 = " + uv3);


                model.faces.add(v1);
                model.faces.add(uv1);
                model.faces.add(v2);
                model.faces.add(uv2);
                model.faces.add(v3);
                model.faces.add(uv3);
                model.faceNormals.add(n1);
                model.faceNormals.add(n2);
                model.faceNormals.add(n3);

                model.smoothingGroups.add(model.currentSmoothGroup);
            }

        } else {

            String[] split = line.substring(2).trim().split(" +");
            int[] faceIndexes = new int[split.length*2];
            int[] faceNormalIndexes = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                String[] split2 = split[i].split("/");
                faceIndexes[i*2] = model.vertexIndex(Integer.parseInt(split2[0]));
                faceIndexes[(i*2)+1] = (split2.length > 1 && split2[1].length() > 0) ? model.uvIndex(Integer.parseInt(split2[1])) : -1;
                faceNormalIndexes[i] = (split2.length > 2 && split2[2].length() > 0) ? model.normalIndex(Integer.parseInt(split2[2])) : -1;
            }

            ((PolyObjModel) model).facesPolygon.add(faceIndexes);
            ((PolyObjModel) model).faceNormalsPolygon.add(faceNormalIndexes);
            model.smoothingGroups.add(model.currentSmoothGroup);
        }
    }

    private static void parseS(String line, ObjModel model) {
        model.currentSmoothGroup = line.substring(2).equals("off") ? 0 : Integer.parseInt(line.substring(2));
    }

    private static void parseMtllib(String line, ObjModel model) {
        // setting materials lib
        String[] split = line.substring("mtllib ".length()).trim().split(" +");
        for (String filename : split) {
            MtlReader mtlReader = new MtlReader(filename, model.url.toExternalForm());
            model.materialLibrary.add(mtlReader.getMaterials());
        }
    }

    private static void parseUsemtl(String line, ObjModel model) {
        model.addMesh(model.key);

        // setting new material for next mesh
        String materialName = line.substring("usemtl ".length());
        for (Map<String, Material> mm : model.materialLibrary) {
            Material m = mm.get(materialName);
            if (m != null) {
                model.material = m;
                break;
            }
        }
    }

    private static void parseVn(String line, ObjModel model) {
        String[] split = line.substring(2).trim().split(" +");
        float x = Float.parseFloat(split[0]);
        float y = Float.parseFloat(split[1]);
        float z = Float.parseFloat(split[2]);
        model.normals.add(x);
        model.normals.add(y);
        model.normals.add(z);
    }

    private static class ObjModel extends Model3D {

        List<Map<String, Material>> materialLibrary = new ArrayList<>();

        FloatArrayList vertices = new FloatArrayList();
        FloatArrayList uvs = new FloatArrayList();
        FloatArrayList normals = new FloatArrayList();
        IntegerArrayList smoothingGroups = new IntegerArrayList();
        Material material = new PhongMaterial(Color.WHITE);

        int facesStart = 0;
        int facesNormalStart = 0;
        int smoothingGroupsStart = 0;
        int currentSmoothGroup = 0;
        String key = "default";

        List<String> meshNames = new ArrayList<>();


        // specific to single obj model
        private Map<String, TriangleMesh> meshes = new HashMap<>();
        private IntegerArrayList faces = new IntegerArrayList();
        private IntegerArrayList faceNormals = new IntegerArrayList();

        private final URL url;

        ObjModel(URL url) {
            this.url = url;
        }

        boolean isPolygon() {
            return false;
        }

        int numFaces() {
            return faces.size();
        }

        private int vertexIndex(int vertexIndex) {
            if (vertexIndex < 0) {
                return vertexIndex + vertices.size() / 3;
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

        Node buildMeshView(String key) {
            MeshView meshView = new MeshView();
            meshView.setId(key);
            meshView.setMaterial(getMaterial(key));
            meshView.setMesh(meshes.get(key));
            meshView.setCullFace(CullFace.NONE);
            return meshView;
        }

        void loadComplete() {
            meshNames.forEach(meshName -> addMeshView(meshName, buildMeshView(meshName)));
        }

        void addMesh(String key) {
            if (facesStart >= faces.size()) {
                // we're only interested in faces
                smoothingGroupsStart = smoothingGroups.size();
                return;
            }
            Map<Integer, Integer> vertexMap = new HashMap<>(vertices.size() / 2);
            Map<Integer, Integer> uvMap = new HashMap<>(uvs.size() / 2);
            Map<Integer, Integer> normalMap = new HashMap<>(normals.size() / 2);
            FloatArrayList newVertexes = new FloatArrayList(vertices.size() / 2);
            FloatArrayList newUVs = new FloatArrayList(uvs.size() / 2);
            FloatArrayList newNormals = new FloatArrayList(normals.size() / 2);
            boolean useNormals = true;

            for (int i = facesStart; i < faces.size(); i += 2) {
                int vi = faces.get(i);
                Integer nvi = vertexMap.get(vi);
                if (nvi == null) {
                    nvi = newVertexes.size() / 3;
                    vertexMap.put(vi, nvi);
                    newVertexes.add(vertices.get(vi * 3));
                    newVertexes.add(vertices.get(vi * 3 + 1));
                    newVertexes.add(vertices.get(vi * 3 + 2));
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
            meshNames.add(key);

            addMaterial(key, material);

            log("Added mesh '" + key + "' of " + mesh.getPoints().size() / mesh.getPointElementSize() + " vertices, "
                            + mesh.getTexCoords().size() / mesh.getTexCoordElementSize() + " uvs, "
                            + mesh.getFaces().size() / mesh.getFaceElementSize() + " faces, "
                            + mesh.getFaceSmoothingGroups().size() + " smoothing groups.");
            log("material diffuse color = " + ((PhongMaterial) material).getDiffuseColor());
            log("material diffuse map = " + ((PhongMaterial) material).getDiffuseMap());

            facesStart = faces.size();
            facesNormalStart = faceNormals.size();
            smoothingGroupsStart = smoothingGroups.size();
        }
    }

    private static class PolyObjModel extends ObjModel {

        // specific to poly obj model
        private Map<String, PolygonMesh> polygonMeshes = new HashMap<>();
        private List<int[]> facesPolygon = new ArrayList<>();
        private List<int[]> faceNormalsPolygon = new ArrayList<>();

        PolyObjModel(URL url) {
            super(url);
        }

        @Override
        boolean isPolygon() {
            return true;
        }

        @Override
        int numFaces() {
            return facesPolygon.size();
        }

        @Override
        Node buildMeshView(String key) {
            PolygonMeshView polygonMeshView = new PolygonMeshView();
            polygonMeshView.setId(key);
            polygonMeshView.setMaterial(getMaterial(key));
            polygonMeshView.setMesh(polygonMeshes.get(key));
            // TODO:
            // polygonMeshView.setCullFace(CullFace.NONE);
            return polygonMeshView;
        }

        @Override
        void addMesh(String key) {
            if (facesStart >= facesPolygon.size()) {
                // we're only interested in faces
                smoothingGroupsStart = smoothingGroups.size();
                return;
            }
            Map<Integer, Integer> vertexMap = new HashMap<>(vertices.size() / 2);
            Map<Integer, Integer> uvMap = new HashMap<>(uvs.size() / 2);
            Map<Integer, Integer> normalMap = new HashMap<>(normals.size() / 2);
            FloatArrayList newVertexes = new FloatArrayList(vertices.size() / 2);
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
                        newVertexes.add(vertices.get(vi * 3));
                        newVertexes.add(vertices.get(vi * 3 + 1));
                        newVertexes.add(vertices.get(vi * 3 + 2));
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
                    System.out.println("    face:: "+Arrays.toString(face));
                }
            }

            int keyIndex = 2;
            String keyBase = key;
            while (polygonMeshes.get(key) != null) {
                key = keyBase + " (" + keyIndex++ + ")";
            }
            polygonMeshes.put(key, mesh);
            meshNames.add(key);

            addMaterial(key, material);

            log("Added mesh '" + key + "' of " + (mesh.getPoints().size()/3) + " vertices, "
                    + (mesh.getTexCoords().size()/2) + " uvs, "
                    + mesh.faces.length + " faces, "
                    + 0 + " smoothing groups.");
            log("material diffuse color = " + ((PhongMaterial) material).getDiffuseColor());
            log("material diffuse map = " + ((PhongMaterial) material).getDiffuseMap());

            facesStart = facesPolygon.size();
            facesNormalStart = faceNormalsPolygon.size();
            smoothingGroupsStart = smoothingGroups.size();
        }
    }
}
