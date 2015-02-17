/**
* FXyzSample.java
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

package org.fxyz.samples;

import javafx.scene.Node;
import javafx.stage.Stage;
import org.fxyz.FXyzSampleBase;

public abstract class FXyzSample extends FXyzSampleBase {

    protected double mousePosX;
    protected double mousePosY;
    protected double mouseOldX;
    protected double mouseOldY;
    protected double mouseDeltaX;
    protected double mouseDeltaY;
    
    protected Node controlPanel;
    
    @Override
    public String getProjectName() {
        return "FXyz-Samples";
    }

    @Override
    public String getProjectVersion() {
        return "1.0";
    }
    
    public abstract Node getSample();
    
    @Override
    public Node getPanel(Stage stage) {
        return getSample();
    }
    
    @Override
    public String getSampleName() {
        String name = getClass().toGenericString();
        return name.substring(name.lastIndexOf(".") + 1, name.length());
    }
    
    // create a util class to retreive, or provide..
    @Override
    public String getSampleSourceURL() {
        return null;
    }

    @Override
    public String getJavaDocURL() {
        return null;
    }       
    
    @Override
    public String getControlStylesheetURL() {
        return null;
    }    
   
    protected abstract Node buildControlPanel();
    
    @Override
    public Node getControlPanel() {
        return controlPanel;       
    }
    
}
