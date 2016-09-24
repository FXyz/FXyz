/**
 * FXyzSample.java
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

package org.fxyz.samples;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.fxyz.FXyzSampleBase;
import org.fxyz.samples.utilities.SkyBoxing;
import org.fxyz.scene.Skybox;

public abstract class FXyzSample extends FXyzSampleBase {

    
    protected final ThreadFactory threadFactory;
    public static ExecutorService serviceExecutor;
    
    protected final Image 
            top = new Image(SkyBoxing.class.getResource("/org/fxyz/images/skyboxes/top.png").toExternalForm()),
            bottom = new Image(SkyBoxing.class.getResource("/org/fxyz/images/skyboxes/bottom.png").toExternalForm()),
            left = new Image(SkyBoxing.class.getResource("/org/fxyz/images/skyboxes/left.png").toExternalForm()),
            right = new Image(SkyBoxing.class.getResource("/org/fxyz/images/skyboxes/right.png").toExternalForm()),
            front = new Image(SkyBoxing.class.getResource("/org/fxyz/images/skyboxes/front.png").toExternalForm()),
            back = new Image(SkyBoxing.class.getResource("/org/fxyz/images/skyboxes/back.png").toExternalForm());

    protected Skybox skyBox;
    
    protected double mousePosX;
    protected double mousePosY;
    protected double mouseOldX;
    protected double mouseOldY;
    protected double mouseDeltaX;
    protected double mouseDeltaY;
    
    protected Node controlPanel;
    
    public FXyzSample(){
        threadFactory = new SampleThreadFactory(getSampleName());
        if(serviceExecutor == null){
            serviceExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
        }
    }
    
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
    public final String getSampleName() {
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

    public static ExecutorService getServiceExecutor() {
        return serviceExecutor;
    }
            
    static class SampleThreadFactory implements ThreadFactory{
        final String name;
        public SampleThreadFactory(String name) {
            this.name = name;
        }
        
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName(name);
            t.setPriority(Thread.NORM_PRIORITY + 1);
            return t;
        }    
    }
    
    
    
}
