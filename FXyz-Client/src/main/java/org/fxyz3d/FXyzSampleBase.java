/**
 * FXyzSampleBase.java
 *
 * Copyright (c) 2013-2016, F(X)yz
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

package org.fxyz3d;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A base class for samples - it is recommended that they extend this class
 * rather than Application, as then the samples can be run either standalone
 * or within FXSampler. 
 */
public abstract class FXyzSampleBase extends Application implements FXyzSample {
    
    /** {@inheritDoc}
     * @throws java.lang.Exception */
    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(getSampleName());
        
        Scene scene = new Scene((Parent)buildSample(this, primaryStage), 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /** {@inheritDoc}
     * @return  */
    @Override public boolean isVisible() {
        return true;
    }
    
    /** {@inheritDoc}
     * @return  */
    @Override public Node getControlPanel() {
        return null;
    }
    
    /** {@inheritDoc}
     * @return  */
    //@Override
    public double getControlPanelDividerPosition() {
    	return 0.6;
    }
    
    /** {@inheritDoc}
     * @return  */
    @Override public String getSampleDescription() {
        return "";
    }
    
    /** {@inheritDoc}
     * @return  */
    @Override public String getProjectName() {
        return "FXyz-Samples";
    }
    
    /**
     * Utility method to create the default look for samples.
     * 
     * This is also where the service should be ran from or the changeSample method in FXyzSampler
     * 
     * @param sample
     * @param stage
     * @return 
     */
    public static Node buildSample(FXyzSample sample, Stage stage) {                
        return sample.getPanel(stage);
    }
}
