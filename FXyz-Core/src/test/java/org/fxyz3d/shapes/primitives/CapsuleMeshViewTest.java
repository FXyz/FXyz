/*
 * F(X)yz
 *
 * Copyright (c) 2013-2021, F(X)yz
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

package org.fxyz3d.shapes.primitives;

import javafx.scene.shape.Mesh;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CapsuleMeshViewTest {

    private CapsuleMesh meshView;

    @BeforeEach
    public void setUp() {
        meshView = new CapsuleMesh();
    }

    @Test
    public void testConstruction() {
        meshView = new CapsuleMesh(80, 15.0, 25.0);

        assertThat(meshView.getDivisions(), is(80));
        assertThat(meshView.getRadius(), is(15.0));
        assertThat(meshView.getHeight(), is(25.0));
    }

    @Test
    @DisplayName("Mesh is updated when num divisions changes")
    public void testUpdateMeshDivisions() {
        Mesh oldMesh = meshView.getMesh();

        meshView.setDivisions(80);

        assertThat(meshView.getMesh(), is(not(oldMesh)));
    }

    @Test
    @DisplayName("Mesh is updated when radius changes")
    public void testUpdateMeshRadius() {
        Mesh oldMesh = meshView.getMesh();

        meshView.setRadius(80);

        assertThat(meshView.getMesh(), is(not(oldMesh)));
    }

    @Test
    @DisplayName("Mesh is updated when height changes")
    public void testUpdateHeight() {
        Mesh oldMesh = meshView.getMesh();

        meshView.setHeight(80);

        assertThat(meshView.getMesh(), is(not(oldMesh)));
    }
}
