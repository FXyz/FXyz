/*
 * Copyright (c) 2013-2019, F(X)yz
 * Copyright (c) 2010, 2014, Oracle and/or its affiliates.
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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

/** Reader for OBJ file MTL material files. */
public class MtlReader {

    private String baseUrl;

    public MtlReader(String filename, String parentUrl) {
        baseUrl = parentUrl.substring(0, parentUrl.lastIndexOf('/') + 1);
        String fileUrl = baseUrl + filename;
        try (Stream<String> line = Files.lines(Paths.get(new URI(fileUrl)))) {
            line.map(String::trim).filter(l -> !l.isEmpty() && !l.startsWith("#")).forEach(this::parse);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Material> materials = new HashMap<>();
    private PhongMaterial currentMaterial;

    // mtl format spec: http://paulbourke.net/dataformats/mtl/
    private enum ParseKey {

        // Material Name
        NEW_MATERIAL("newmtl") {
            @Override
            protected void parse(String value, MtlReader reader) {
                reader.parseNewMaterial(value);
            }
        },
        
        // Material color and illumination
        AMBIENT_REFLECTIVITY ("Ka"),
        DIFFUSE_REFLECTIVITY ("Kd") {
            @Override
            protected void parse(String value, MtlReader reader) {
                reader.parseDiffuseReflectivity(value);
            }
        },
        SPECULAR_REFLECTIVITY("Ks") {
            @Override
            protected void parse(String value, MtlReader reader) {
                reader.parseSpecularReflectivity(value);
            }
        },
        SPECULAR_EXPONENT    ("Ns") {
            @Override
            protected void parse(String value, MtlReader reader) {
                reader.parseSpecularExponent(value);
            }
        },
        TRANSMISION_FILTER   ("Tf"),
        ILLUMINATION_MODEL   ("illum"),
        DISSOLVE             ("d"),
        TRANSPARENCY         ("Tr"),
        SHARPNESS            ("sharpness"),
        OPTICAL_DENSITY      ("Ni"),

        // Material texture map
        AMBIENT_REFLECTIVITY_MAP ("map_Ka"),
        DIFFUSE_REFLECTIVITY_MAP ("map_Kd") {
            @Override
            protected void parse(String value, MtlReader reader) {
                reader.parseDiffuseReflectivityMap(value);
            }
        },
        SPECULAR_REFLECTIVITY_MAP("map_Ks") {
            @Override
            protected void parse(String value, MtlReader reader) {
                reader.parseSpecularReflectivityMap(value);
            }
        },
        SPECULAR_EXPONENT_MAP    ("map_Ns"),
        DISSOLVE_MAP             ("map_d"),
        DISPLACEMENT_MAP         ("disp"),
        DECAL_STENCIL_MAP        ("decal"),
        BUMP_MAP                 ("bump") {
            @Override
            protected void parse(String value, MtlReader reader) {
                reader.parseBumpMap(value);
            }
        },
        REFLECTION_MAP           ("refl"),
        ANTI_ALIASING            ("map_aat");

        private String key;

        private ParseKey(String key) {
            this.key = key;
        }

        protected void parse(String value, MtlReader reader) {
            reader.parseIgnore(toString() + " (" + key +")");
        }

        boolean testAndParse(String line, MtlReader reader) {
            if (line.startsWith(key + " ")) {
                parse(line.substring(key.length() + 1), reader);
                return true;
            }
            return false;
        }
    }

    private void parseIgnore(String nameAndKey) {
        ObjImporter.log(nameAndKey + " is not supported. Ignoring.");
    }

    private void parseNewMaterial(String line) {
        currentMaterial = new PhongMaterial();
        materials.put(line, currentMaterial);
//      TODO: ignore duplicates? ObjImporter.log("This material is already added. Ignoring " + line);
    }
    
    private void parseDiffuseReflectivity(String value) {
        currentMaterial.setDiffuseColor(readColor(value));
    }

    private void parseSpecularReflectivity(String value) {
        currentMaterial.setSpecularColor(readColor(value));
    }

    private void parseSpecularExponent(String value) {
        currentMaterial.setSpecularPower(Double.parseDouble(value));
    }

    private void parseDiffuseReflectivityMap(String value) {
        currentMaterial.setDiffuseMap(loadImage(value));
    }

    private void parseSpecularReflectivityMap(String value) {
        currentMaterial.setSpecularMap(loadImage(value));
    }

    private void parseBumpMap(String value) {
        currentMaterial.setBumpMap(loadImage(value));
    }

    private void parse(String line) {
        for (ParseKey parseKey : ParseKey.values()) {
            if (parseKey.testAndParse(line, this)) {
                return;
            }
        }
        ObjImporter.log("No parser found for: " + line);
    }

    private Color readColor(String line) {
        String[] split = line.trim().split(" +");
        float red = Float.parseFloat(split[0]);
        float green = Float.parseFloat(split[1]);
        float blue = Float.parseFloat(split[2]);
        return Color.color(red, green, blue);
    }

    private Image loadImage(String filename) {
        filename = baseUrl + filename;
        ObjImporter.log("Loading image from " + filename);
        return new Image(filename);
    }

    public Map<String, Material> getMaterials() {
        return Collections.unmodifiableMap(materials);
    }
}