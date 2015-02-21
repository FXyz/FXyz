/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.HiddenSidesPane;
import org.fxyz.ExtrasAndTests.CustomWindow;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ModelInfoTracker extends StackPane {
    @FXML
    private HBox headerArea;
    @FXML
    private Label sampleTitle;
    @FXML
    private Button hideStatus;
    @FXML
    private HBox content;
    @FXML
    private Label nodeCount;
    @FXML
    private Label timeToBuild;
    @FXML
    private Label width;
    @FXML
    private Label height;
    @FXML
    private Label depth;
    @FXML
    private Label points;
    @FXML
    private Label faces;

    private HiddenSidesPane parentPane;
    public ModelInfoTracker(HiddenSidesPane parent) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/org/fxyz/client/ModelInfo.fxml"));
            loader.setController(ModelInfoTracker.this);
            loader.setRoot(ModelInfoTracker.this);

            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(CustomWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.parentPane = parent;
        hideStatus.setFocusTraversable(false);
        hideStatus.setOnAction(e->{
            parentPane.setPinnedSide(null);
        });
        
        this.setOnMouseEntered(e->{
            if(parentPane.getPinnedSide() != Side.BOTTOM){
                parentPane.setPinnedSide(Side.BOTTOM);
            }
        });
    }
    
    public Label getSampleTitle() {
        return sampleTitle;
    }

    public Label getNodeCount() {
        return nodeCount;
    }

    public Label getTimeToBuild() {
        return timeToBuild;
    }

    public Label getBoundsWidth() {
        return width;
    }

    public Label getBoundsHeight() {
        return height;
    }

    public Label getBoundsDepth() {
        return depth;
    }

    public Label getPoints() {
        return points;
    }

    public Label getFaces() {
        return faces;
    }

       
    
}
