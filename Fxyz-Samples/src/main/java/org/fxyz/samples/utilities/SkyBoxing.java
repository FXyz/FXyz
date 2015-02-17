/**
* SkyBoxing.java
*
* Copyright (c) 2013-2015, F(X)yz
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* * Neither the name of the organization nor the
* names of its contributors may be used to endorse or promote products
* derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.fxyz.samples.utilities;

import javafx.scene.Node;
import javafx.scene.image.Image;
import org.fxyz.samples.shapes.ShapeBaseSample;
import org.fxyz.scene.Skybox;

/**
 *
 * @author Dub
 */
public class SkyBoxing extends ShapeBaseSample {

    public static void main(String[] args){SkyBoxing.launch(args);}

    @Override
    protected void createMesh() {
        final Image top = new Image(SkyBoxing.class.getResource("/org/fxyz/samples/res/top.png").toExternalForm()),
                bottom = new Image(SkyBoxing.class.getResource("/org/fxyz/samples/res/bottom.png").toExternalForm()),
                left = new Image(SkyBoxing.class.getResource("/org/fxyz/samples/res/left.png").toExternalForm()),
                right = new Image(SkyBoxing.class.getResource("/org/fxyz/samples/res/right.png").toExternalForm()),
                front = new Image(SkyBoxing.class.getResource("/org/fxyz/samples/res/front.png").toExternalForm()),
                back = new Image(SkyBoxing.class.getResource("/org/fxyz/samples/res/back.png").toExternalForm());


        // Load Skybox AFTER camera is initialized
        double size = 100000D;
        model = new Skybox(
                top,
                bottom,
                left,
                right,
                front,
                back,
                size,
                camera
        );
       
    }

    @Override
    protected void addMeshAndListeners() {
    }
    
    @Override
    protected Node buildControlPanel() {
        return null;
    }

}
