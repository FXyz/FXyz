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
