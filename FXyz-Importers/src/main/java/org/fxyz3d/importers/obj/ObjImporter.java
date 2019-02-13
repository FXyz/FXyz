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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
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
        return SUPPORTED_EXT.equalsIgnoreCase(extension);
    }

    private ObjModel read(URL url, boolean asPolygon) {
        log("Reading from URL: " + url + " as polygon: " + asPolygon);

        ObjModel model = asPolygon ? new PolyObjModel(url) : new ObjModel(url);

        try (Stream<String> lines = Files.lines(Paths.get(url.toURI()))) {
            lines.map(String::trim)
                 .filter(l -> !l.isEmpty() && !l.startsWith("#"))
                 .forEach(model::parseLine);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        model.addMesh(model.key);

        log("Totally loaded " + (model.vertices.size() / 3.) + " vertices, "
                + (model.uvs.size() / 2.) + " uvs, "
                + (model.numFaces() / 6.) + " faces, "
                + model.smoothingGroups.size() + " smoothing groups.");

        model.loadComplete();

        return model;
    }

    private static class ObjModel extends Model3D {

        // obj format spec: http://paulbourke.net/dataformats/obj/
        private static final Map<String, BiConsumer<String, ObjModel>> PARSERS = Map.of(
                "g",       (l, m) -> m.parseGroupName(l),
                "v ",      (l, m) -> m.parseVertex(l),
                "vt ",     (l, m) -> m.parseVertexTexture(l),
                "f ",      (l, m) -> m.parseFace(l),
                "s ",      (l, m) -> m.parseSmoothGroup(l),
                "mtllib ", (l, m) -> m.parseMaterialLib(l),
                "usemtl ", (l, m) -> m.parseUseMaterial(l),
                "vn ",     (l, m) -> m.parseVertexNormal(l));

        private void parseLine(String line) {
            for (Entry<String, BiConsumer<String, ObjModel>> parser : PARSERS.entrySet()) {
                String identifier = parser.getKey();
                if (line.startsWith(identifier)) {
                    parser.getValue().accept(line.substring(identifier.length()), this);
                    return;
                }
            }
            log("line skipped: " + line);
        }

        List<Map<String, Material>> materialLibrary = new ArrayList<>();

        ObservableFloatArray vertices = FXCollections.observableFloatArray();
        ObservableFloatArray uvs = FXCollections.observableFloatArray();
        ObservableFloatArray normals = FXCollections.observableFloatArray();
        ObservableIntegerArray smoothingGroups = FXCollections.observableIntegerArray();
        Material material = new PhongMaterial(Color.WHITE);

        int facesStart = 0;
        int facesNormalStart = 0;
        int smoothingGroupsStart = 0;
        int currentSmoothGroup = 0;
        String key = "default";

        List<String> meshNames = new ArrayList<>();

        // specific to single obj model
        private Map<String, TriangleMesh> meshes = new HashMap<>();
        private ObservableIntegerArray faces = FXCollections.observableIntegerArray();
        private ObservableIntegerArray faceNormals = FXCollections.observableIntegerArray();

        private final URL url;

        ObjModel(URL url) {
            this.url = url;
        }

        int numFaces() {
            return faces.size();
        }

        protected int vertexIndex(int vertexIndex) {
            return vertexIndex + (vertexIndex < 0 ? vertices.size() / 3 : -1);
        }

        protected int uvIndex(int uvIndex) {
            return uvIndex + (uvIndex < 0 ? uvs.size() / 2 : -1);
        }

        protected int normalIndex(int normalIndex) {
            return normalIndex + (normalIndex < 0 ? normals.size() / 3 : -1);
        }

        Node buildMeshView(String key) {
            MeshView meshView = new MeshView();
            meshView.setId(key);
            meshView.setMaterial(getMaterial(key));
            meshView.setMesh(meshes.get(key));
            meshView.setCullFace(CullFace.NONE);
            return meshView;
        }

        private void loadComplete() {
            meshNames.forEach(meshName -> addMeshView(meshName, buildMeshView(meshName)));
        }

        void addMesh(String key) {
            if (facesStart >= faces.size()) {
                // we're only interested in faces
                smoothingGroupsStart = smoothingGroups.size();
                return;
            }

            TriangleMesh mesh = new TriangleMesh();
            mesh.getPoints().ensureCapacity(vertices.size() / 2);
            mesh.getTexCoords().ensureCapacity(uvs.size() / 2);
            ObservableFloatArray newNormals = FXCollections.observableFloatArray();
            newNormals.ensureCapacity(normals.size() / 2);
            boolean useNormals = true;

            Map<Integer, Integer> vertexMap = new HashMap<>(vertices.size() / 2);
            Map<Integer, Integer> uvMap = new HashMap<>(uvs.size() / 2);
            Map<Integer, Integer> normalMap = new HashMap<>(normals.size() / 2);

            for (int i = facesStart; i < faces.size(); i += 2) {
                int vi = faces.get(i);
                Integer nvi = vertexMap.putIfAbsent(vi, mesh.getPoints().size() / 3);
                if (nvi == null) {
                    nvi = mesh.getPoints().size() / 3;
                    mesh.getPoints().addAll(vertices, vi * 3, 3);
                }
                faces.set(i, nvi);

                int uvi = faces.get(i + 1);
                Integer nuvi = uvMap.putIfAbsent(uvi, mesh.getTexCoords().size() / 2);
                if (nuvi == null) {
                    nuvi = mesh.getTexCoords().size() / 2;
                    if (uvi >= 0) {
                        mesh.getTexCoords().addAll(uvs, uvi * 2 , 2);
                    } else {
                        mesh.getTexCoords().addAll(0, 0);
                    }
                }
                faces.set(i + 1, nuvi);

                if (useNormals) {
                    int ni = faceNormals.get(i/2);
                    Integer nni = normalMap.putIfAbsent(ni, newNormals.size() / 3);
                    if (nni == null) {
                        nni = newNormals.size() / 3;
                        if (ni >= 0 && normals.size() >= (ni + 1) * 3) {
                            newNormals.addAll(normals, ni * 3, 3);
                        } else {
                            useNormals = false;
                            newNormals.addAll(0, 0, 0);
                        }
                    }
                    faceNormals.set(i/2, nni);
                }
            }
            mesh.getFaces().setAll(faces, facesStart, faces.size() - facesStart);

            // Use normals if they are provided
            if (useNormals) {
                int[] facesArray = mesh.getFaces().toArray(new int[mesh.getFaces().size()]);

                int fnLength = faceNormals.size() - facesNormalStart;
                int[] faceNormalsArray = faceNormals.toArray(facesNormalStart, new int[fnLength], fnLength);

                float[] normalsArray = newNormals.toArray(new float[newNormals.size()]);

                int[] smGroups = SmoothingGroups.calcSmoothGroups(mesh, facesArray, faceNormalsArray, normalsArray);
                mesh.getFaceSmoothingGroups().setAll(smGroups);
            } else {
                int sgLength = smoothingGroups.size() - smoothingGroupsStart;
                mesh.getFaceSmoothingGroups().setAll(smoothingGroups, smoothingGroupsStart, sgLength);
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

        private void parseGroupName(String line) {
            addMesh(key);
            key = line.isEmpty() ? "default" : line.substring(1);
            log("key = " + key);
        }

        private void parseVertex(String line) {
            String[] split = line.split(" +");
            float x = Float.parseFloat(split[0]) * scale;
            float y = Float.parseFloat(split[1]) * scale;
            float z = Float.parseFloat(split[2]) * scale;
            vertices.addAll(x, y, z);

            if (flatXZ) {
                uvs.addAll(x, z);
            }
        }

        private void parseVertexTexture(String line) {
            String[] split = line.split(" +");
            float u = split[0].trim().equalsIgnoreCase("nan") ? Float.NaN : Float.parseFloat(split[0]);
            float v = split[1].trim().equalsIgnoreCase("nan") ? Float.NaN : Float.parseFloat(split[1]);
            uvs.addAll(u, 1 - v);
        }

        protected void parseFace(String line) {
            String[] split = line.split(" +");
            int[][] data = new int[split.length][];
            boolean uvProvided = true;
            boolean normalProvided = true;
            for (int i = 0; i < split.length; i++) {
                String[] split2 = split[i].split("/");
                if (split2.length < 2) {
                    uvProvided = false;
                    normalProvided = false;
                } else if (split2.length < 3) {
                    normalProvided = false;
                }
                data[i] = new int[split2.length];
                for (int j = 0; j < split2.length; j++) {
                    if (split2[j].length() == 0) {
                        data[i][j] = 0;
                        if (j == 1) {
                            uvProvided = false;
                        } else if (j == 2) {
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

                // log("v1 = " + v1 + ", v2 = " + v2 + ", v3 = " + v3);
                // log("uv1 = " + uv1 + ", uv2 = " + uv2 + ", uv3 = " + uv3);

                faces.addAll(v1, uv1, v2, uv2, v3, uv3);
                faceNormals.addAll(n1, n2, n3);

                smoothingGroups.addAll(currentSmoothGroup);
            }
        }

        private void parseSmoothGroup(String line) {
            currentSmoothGroup = line.equals("off") ? 0 : Integer.parseInt(line.substring(2));
        }

        private void parseMaterialLib(String line) {
            // setting materials lib
            String[] split = line.split(" +");
            for (String filename : split) {
                MtlReader mtlReader = new MtlReader(filename, url.toExternalForm());
                materialLibrary.add(mtlReader.getMaterials());
            }
        }

        private void parseUseMaterial(String line) {
            addMesh(key);

            // setting new material for next mesh
            String materialName = line;
            for (Map<String, Material> mm : materialLibrary) {
                Material m = mm.get(materialName);
                if (m != null) {
                    material = m;
                    break;
                }
            }
        }

        private void parseVertexNormal(String line) {
            String[] split = line.split(" +");
            float x = Float.parseFloat(split[0]);
            float y = Float.parseFloat(split[1]);
            float z = Float.parseFloat(split[2]);
            normals.addAll(x ,y, z);
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

            PolygonMesh mesh = new PolygonMesh();
            mesh.getPoints().ensureCapacity(vertices.size() / 2);
            mesh.getTexCoords().ensureCapacity(uvs.size() / 2);
            ObservableFloatArray newNormals = FXCollections.observableFloatArray();
            newNormals.ensureCapacity(normals.size() / 2);
            boolean useNormals = true;

            Map<Integer, Integer> vertexMap = new HashMap<>(vertices.size() / 2);
            Map<Integer, Integer> uvMap = new HashMap<>(uvs.size() / 2);
            Map<Integer, Integer> normalMap = new HashMap<>(normals.size() / 2);

            mesh.setFaces(new int[facesPolygon.size() - facesStart][]);
            int[][] faceNormalArrays = new int[faceNormalsPolygon.size() - facesNormalStart][];

            for (int i = facesStart; i < facesPolygon.size(); i++) {
                int[] faceIndexes = facesPolygon.get(i);
                int[] faceNormalIndexes = faceNormalsPolygon.get(i);
                for (int j = 0; j < faceIndexes.length; j += 2){
                    int vi = faceIndexes[j];
                    Integer nvi = vertexMap.putIfAbsent(vi, mesh.getPoints().size() / 3);
                    if (nvi == null) {
                        nvi = mesh.getPoints().size() / 3;
                        mesh.getPoints().addAll(vertices, vi * 3, 3);
                    }
                    faceIndexes[j] = nvi;
//                  faces.set(i, nvi);

                    int uvi = faceIndexes[j+1];
                    Integer nuvi = uvMap.putIfAbsent(uvi, mesh.getTexCoords().size() / 2);
                    if (nuvi == null) {
                        nuvi = mesh.getTexCoords().size() / 2;
                        if (uvi >= 0) {
                            mesh.getTexCoords().addAll(uvs, uvi * 2 , 2);
                        } else {
                            mesh.getTexCoords().addAll(0, 0);
                        }
                    }
                    faceIndexes[j+1] = nuvi;
//                  faces.set(i + 1, nuvi);

                    int ni = faceNormalIndexes[j/2];
                    Integer nni = normalMap.putIfAbsent(ni, newNormals.size() / 3);
                    if (nni == null) {
                        nni = newNormals.size() / 3;
                        if (ni >= 0 && normals.size() >= (ni + 1) * 3) {
                            newNormals.addAll(normals, ni * 3, 3);
                        } else {
                            useNormals = false;
                            newNormals.addAll(0, 0, 0);
                        }
                    }
                    faceNormalIndexes[j/2] = nni;
                }
                mesh.getFaces()[i-facesStart] = faceIndexes;
                faceNormalArrays[i-facesNormalStart] = faceNormalIndexes;
            }

            // Use normals if they are provided
            if (useNormals) {
                float[] normalsArray = newNormals.toArray(new float[newNormals.size()]);
                int[] smGroups = SmoothingGroups.calcSmoothGroups(mesh.getFaces(), faceNormalArrays, normalsArray);
                mesh.getFaceSmoothingGroups().setAll(smGroups);
            } else {
                int length = smoothingGroups.size() - smoothingGroupsStart;
                mesh.getFaceSmoothingGroups().setAll(smoothingGroups, smoothingGroupsStart, length);
            }

            if (debug) {
                System.out.println("mesh.points = " + mesh.getPoints());
                System.out.println("mesh.texCoords = " + mesh.getTexCoords());
                System.out.println("mesh.faces: ");
                for (int[] face: mesh.getFaces()) {
                    System.out.println("    face:: " + Arrays.toString(face));
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
                    + mesh.getFaces().length + " faces, "
                    + 0 + " smoothing groups.");
            log("material diffuse color = " + ((PhongMaterial) material).getDiffuseColor());
            log("material diffuse map = " + ((PhongMaterial) material).getDiffuseMap());

            facesStart = facesPolygon.size();
            facesNormalStart = faceNormalsPolygon.size();
            smoothingGroupsStart = smoothingGroups.size();
        }

        @Override
        protected void parseFace(String line) {
            String[] split = line.split(" +");
            int[] faceIndexes = new int[split.length * 2];
            int[] faceNormalIndexes = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                String[] split2 = split[i].split("/");
                faceIndexes[i * 2] = vertexIndex(Integer.parseInt(split2[0]));
                faceIndexes[(i * 2) + 1] = (split2.length > 1 && split2[1].length() > 0) ? uvIndex(Integer.parseInt(split2[1])) : -1;
                faceNormalIndexes[i] = (split2.length > 2 && split2[2].length() > 0) ? normalIndex(Integer.parseInt(split2[2])) : -1;
            }

            facesPolygon.add(faceIndexes);
            faceNormalsPolygon.add(faceNormalIndexes);
            smoothingGroups.addAll(currentSmoothGroup);
        }
    }
}
