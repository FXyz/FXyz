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

package org.fxyz3d.geometry;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Face3Test {

    @ParameterizedTest
    @CsvSource({
            "1, 2, 3",
            "-1, 0, 1",
            "2147483647, 0, -2147483648"
    })
    public void testConstructor(int p0, int p1, int p2) {
        Face3 face = new Face3(p0, p1, p2);

        assertThat(face.p0, is(p0));
        assertThat(face.p1, is(p1));
        assertThat(face.p2, is(p2));
    }

    @ParameterizedTest
    @MethodSource("faceProvider")
    public void testGetFace(Face3 face) {
        assertThat(
                face.getFace().boxed().collect(toList()),
                contains(face.p0, 0, face.p1, 0, face.p2, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("faceAndTProvider")
    void testGetFace(Face3 face, int t) {
        assertThat(
                face.getFace(t).boxed().collect(toList()),
                contains(face.p0, t, face.p1, t, face.p2, t)
        );
    }

    @ParameterizedTest
    @MethodSource("facesProvider")
    void testGetFace(Face3 face1, Face3 face2) {
        assertThat(
                face1.getFace(face2).boxed().collect(toList()),
                contains(face1.p0, face2.p0, face1.p1, face2.p1, face1.p2, face2.p2)
        );

        assertThat(
                face1.getFace(face2.p2, face2.p0, face2.p1).boxed().collect(toList()),
                contains(face1.p0, face2.p2, face1.p1, face2.p0, face1.p2, face2.p1)
        );
    }

    static Stream<Arguments> faceAndTProvider() {
        return Stream.of(
                arguments(new Face3(1, 2, 3), 0),
                arguments(new Face3(-1, 0, 1), -2147483648),
                arguments(new Face3(2147483647, 0, -2147483648), 2147483647)
        );
    }

    static Stream<Face3> faceProvider() {
        return Stream.of(
                new Face3(1, 2, 3),
                new Face3(-1, 0, 1),
                new Face3(2147483647, 0, -2147483648)
        );
    }

    static Stream<Arguments> facesProvider() {
        return Stream.of(
                arguments(new Face3(1, 2, 3), new Face3(-1, 0, 1)),
                arguments(new Face3(-1, 0, 1), new Face3(2147483647, 0, -2147483648)),
                arguments(new Face3(2147483647, 0, -2147483648), new Face3(1, 2, 3))
        );
    }
}
