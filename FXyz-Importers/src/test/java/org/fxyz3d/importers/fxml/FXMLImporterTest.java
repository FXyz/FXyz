/*
 * F(X)yz
 *
 * Copyright (c) 2013-2020, F(X)yz
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
package org.fxyz3d.importers.fxml;

import javafx.scene.Node;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import org.fxyz3d.importers.Model3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author JosePereda
 */
class FXMLImporterTest {

    private FXMLImporter importer;

    @BeforeEach
    void setUp() {
        importer = new FXMLImporter();
    }

    @Test
    void testExtensions() {
        assertTrue(importer.isSupported("fxml"));
        assertTrue(importer.isSupported("FXML"));
        assertFalse(importer.isSupported(null));
    }

    @Test
    void testLoad() throws Exception {
        Model3D model = importer.load(getClass().getResource("mesh.fxml"));

        assertEquals(1, model.getRoot().getChildren().size());
        assertEquals(1, model.getMeshViews().size());
        Node n = model.getMeshViews().get(0);
        assertTrue(n instanceof MeshView);
        MeshView mv = (MeshView) n;
        assertTrue(mv.getMesh() instanceof TriangleMesh);
        TriangleMesh t = (TriangleMesh) mv.getMesh();
        assertEquals(4, t.getPoints().size() / t.getPointElementSize());
        assertEquals(6, t.getTexCoords().size() / t.getTexCoordElementSize());
        assertEquals(4, t.getFaces().size() / t.getFaceElementSize());
        assertEquals(4, t.getFaceSmoothingGroups().size());
        assertTrue(mv.getMaterial() instanceof PhongMaterial);
    }
}
