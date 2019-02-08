/*
 * F(X)yz
 *
 * Copyright (c) 2013-2019, F(X)yz
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
package org.fxyz3d.importers.obj;

import org.fxyz3d.importers.Model3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ObjImporterTest {

    private ObjImporter importer;

    @BeforeEach
    void setUp() {
        importer = new ObjImporter();
    }

    @Test
    void testExtensions() {
        assertTrue(importer.isSupported("obj"));
        assertTrue(importer.isSupported("OBJ"));
        assertFalse(importer.isSupported(null));
    }

    @Test
    void testLoad() throws Exception {
        Model3D model = importer.load(getClass().getResource("cube.obj"));

        assertEquals(1, model.getRoot().getChildren().size());
        assertEquals(1, model.getMeshNames().size());
        assertTrue(model.getMeshNames().contains("cube"));
        assertSame(model.getMeshView("cube"), model.getRoot().getChildren().get(0));
    }

    @Test
    void testLoadAsPoly() throws Exception {
        Model3D model = importer.load(getClass().getResource("duke_king_poly.obj"));

        assertEquals(9, model.getRoot().getChildren().size());
        assertEquals(9, model.getMeshNames().size());
        assertEquals(9, model.getMeshViews().size());
    }

    @Test
    void testLoadMaterial() throws Exception {
        Model3D model = importer.load(getClass().getResource("cube_with_mtl.obj"));

        assertEquals(6, model.getMaterials().size());
    }

    @Test
    void testMeshes() throws Exception {
        Model3D model = importer.load(getClass().getResource("cube_with_mtl.obj"));

        assertEquals(6, model.getMeshNames().size());
        assertNotNull(model.getMeshView("front"));
        assertNotNull(model.getMeshView("back"));
        assertNotNull(model.getMeshView("top"));
        assertNotNull(model.getMeshView("bottom"));
        assertNotNull(model.getMeshView("left"));
        assertNotNull(model.getMeshView("right"));
    }
}
