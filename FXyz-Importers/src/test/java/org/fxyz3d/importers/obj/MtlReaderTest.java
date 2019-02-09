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

import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MtlReaderTest {

    private MtlReader reader;

    @BeforeEach
    void setUp() {
        reader = new MtlReader("cube.mtl", getClass().getResource("cube.mtl").toExternalForm());
    }

    @Test
    void testReadMaterials() {
        Map<String, Material> materials = reader.getMaterials();
        assertEquals(6, materials.size());
    }

    @ParameterizedTest
    @CsvSource({
            "blue, 0, 0, 0.7, 1, 1, 1, 64",
            "red, 0.7, 0, 0.022779, 1, 1, 1, 80",
            "white, 1, 1, 1, 1, 1, 1, 80",
            "black, 0, 0, 0, 1, 1, 0.7, 33",
            "green, 0, 1, 0, 1, 0.9, 1, 62",
            "purple, 0.7, 0, 0.522779, 1, 1, 1, 80"
    })
    void testEachMaterial(String mtlName,
                          double Kdr, double Kdg, double Kdb,
                          double Ksr, double Ksg, double Ksb,
                          int Ns) {

        PhongMaterial mat = (PhongMaterial) reader.getMaterials().get(mtlName);
        assertEquals(Color.color(Kdr, Kdg, Kdb), mat.getDiffuseColor());
        assertEquals(Color.color(Ksr, Ksg, Ksb), mat.getSpecularColor());
        assertEquals(Ns, mat.getSpecularPower());
    }
}
