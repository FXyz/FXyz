/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.fxyz.FXyzSample;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ClientController extends AnchorPane implements Initializable{

    @FXML
    private BorderPane rootBorderPane;
    @FXML
    private HBox header;
    @FXML
    private VBox leftSide;
    @FXML
    private TextField searchBar;
    @FXML
    private TreeView<FXyzSample> contentTree;
    @FXML
    private HBox statusBar;
    @FXML
    private HBox footer;
    @FXML
    private HBox leftStatusContainer;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private HBox rightStatusContainer;
    @FXML
    private Label rightStatusLabel;
    @FXML
    private StackPane contentPane;
    @FXML
    private VBox centerOverlay;
    @FXML
    private HBox sceneTrackerOverlay;
    @FXML
    private VBox rightSide;
    @FXML
    private Accordion sampleControls;
    @FXML
    private TitledPane removeMe;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    /***************************************************************************
     *      Header Setup
     **************************************************************************/
    
    
    /***************************************************************************
     *      LeftPanel Setup
     **************************************************************************/
    
    
    
    /***************************************************************************
     *      ContentSetup Setup
     **************************************************************************/
    
    
    
    /***************************************************************************
     *      Content Header Overlay Setup
     **************************************************************************/
        
    
    
    /***************************************************************************
     *      Controls Setup
     **************************************************************************/
    public void clearControls(){
        sampleControls.getPanes().clear();
    }
    
    public void addControl(){}
    public void addControls(){}
    
    
    
    
    /***************************************************************************
     *      Footer Setup
     **************************************************************************/
    
    
    
    
    //==========================================================================
}
