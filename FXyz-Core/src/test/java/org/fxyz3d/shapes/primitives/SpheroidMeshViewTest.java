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

package org.fxyz3d.shapes.primitives;

import javafx.scene.shape.Mesh;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SpheroidMeshViewTest {

    private SpheroidMesh meshView;

    @BeforeEach
    public void setUp() {
        meshView = new SpheroidMesh();
    }

    @Test
    public void testConstruction() {
        meshView = new SpheroidMesh(32, 40.0, 8.0);

        assertThat(meshView.getDivisions(), is(32));
        assertThat(meshView.getMajorRadius(), is(40.0));
        assertThat(meshView.getMinorRadius(), is(8.0));
    }

    @Test
    @DisplayName("Mesh is updated when major radius changes")
    public void testUpdateMeshMajorRadius() {
        Mesh oldMesh = meshView.getMesh();

        meshView.setMajorRadius(24.0);

        assertThat(meshView.getMesh(), is(not(oldMesh)));
    }

    @Test
    @DisplayName("Mesh is updated when minor radius changes")
    public void testUpdateMeshMinorRadius() {
        Mesh oldMesh = meshView.getMesh();

        meshView.setMinorRadius(6.0);

        assertThat(meshView.getMesh(), is(not(oldMesh)));
    }

    @Test
    @DisplayName("Mesh is updated when number of divisions changes")
    public void testUpdateMeshNumDivisions() {
        Mesh oldMesh = meshView.getMesh();

        meshView.setDivisions(24);

        assertThat(meshView.getMesh(), is(not(oldMesh)));
    }
}
