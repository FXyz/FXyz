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

module org.fxyz3d.FXyz.Samples {
    requires org.fxyz3d.core;
    requires org.fxyz3d.importers;
    requires org.fxyz3d.client;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires java.logging;
    requires org.controlsfx.controls;
    requires easybind;
    requires reactfx;
    requires jfxtras.common;
    requires jfxtras.controls;
    requires jfxtras.fxml;
    requires java.scripting;

    opens org.fxyz3d.controls to javafx.fxml;
    provides org.fxyz3d.FXyzSamplerProject with org.fxyz3d.samples.FXyzProject;
    
    exports org.fxyz3d.samples.importers to org.fxyz3d.client, org.fxyz3d.importers;
    exports org.fxyz3d.samples.shapes.compound to org.fxyz3d.client;
    exports org.fxyz3d.samples.shapes.texturedmeshes to org.fxyz3d.client;
    exports org.fxyz3d.samples.utilities to org.fxyz3d.client;
    exports org.fxyz3d.samples;
    
}
