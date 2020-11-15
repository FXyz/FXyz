/**
 * F(X)yz
 *
 * Copyright (c) 2013-2018, F(X)yz
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

module org.fxyz3d.core {
    requires transitive javafx.controls;
    requires static javafx.swing;
    requires java.desktop;
    requires java.logging;
    requires static jcsg;
    requires static vvecmath;
    requires static poly2tri.core;
    requires static jzy3d.jdt.core;

    exports org.fxyz3d.geometry;
    exports org.fxyz3d.io;
    exports org.fxyz3d.scene;
    exports org.fxyz3d.scene.paint;
    exports org.fxyz3d.shapes;
    exports org.fxyz3d.shapes.complex.cloth;
    exports org.fxyz3d.shapes.composites;
    exports org.fxyz3d.shapes.containers;
    exports org.fxyz3d.shapes.polygon;
    exports org.fxyz3d.shapes.polygon.symbolic;
    exports org.fxyz3d.shapes.primitives;
    exports org.fxyz3d.shapes.primitives.helper;
    exports org.fxyz3d.tools;
    exports org.fxyz3d.utils;
    exports org.fxyz3d.utils.geom;
}
