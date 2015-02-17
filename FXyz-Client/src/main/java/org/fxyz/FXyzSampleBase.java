package org.fxyz;

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
