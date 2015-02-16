/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.ExtrasAndTests;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import static org.fxyz.client.FXyzClient.BACKGROUNDS;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class CustomWindow extends AnchorPane{
    private static final Logger log = Logger.getLogger(CustomWindow.class.getName());

    @FXML
    private AnchorPane justForShadow;
    @FXML
    private AnchorPane frame;
    @FXML
    private StackPane contentPane;
    /* 
     Subscene should be used for 3D content,
     otherwise replace in ContentPane(remove SubScene)
     */
    
    
    @FXML
    private HBox header;
    @FXML
    private HBox appInfo;
    @FXML
    private ImageView stageIcon;
    @FXML
    private Label versionLabel;
    @FXML
    private StackPane dragBar;
    @FXML
    private HBox windowControls;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button maximizeButton;
    @FXML
    private Button exitButton;
    @FXML
    private StackPane headerContent;
    @FXML
    private ProgressBar prograssBar;
    @FXML
    private HBox informationPane;
    @FXML
    private Label infoLabel1;
    @FXML
    private StackPane resize;
    @FXML
    private ImageView seResizeImage;

    private double mX,mY,mOX,mOY,mDX,mDY,dragOffsetX, dragOffsetY;
    private double stageMinWidth, stageMinHeight;
    private Stage stage;
    private Scene scene;
    
//==============================================================================
    // Constructors

    private CustomWindow() {

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("CustomWindow.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(CustomWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public CustomWindow(Stage stage) {        
        this();
        
        postInit();
                
        assert stage != null;
        this.stage = stage;
        this.scene = new Scene(this, 1200,800, false, SceneAntialiasing.BALANCED);
        scene.setFill(null);
        //this.getStyleClass().add("subScene-container");
        scene.setOnMousePressed(e->{
            mOX = mX;
            mOY = mY;
            mX = e.getSceneX();
            mY = e.getSceneY();
            mDX = mX - mOX; 
            mDY = mY - mOY;
            
        });
        scene.getStylesheets().addAll(BACKGROUNDS);
        SimpleSliderClient ssc = new SimpleSliderClient(stage, true);
        //ssc.getStyleClass().add("comp-fade-background");
        setContent(ssc);
        
        stage.setScene(scene);
        
        if (stage.getStyle() != StageStyle.TRANSPARENT) {
            stage.initStyle(StageStyle.TRANSPARENT);
        }
        
        stage.show();
        
        this.stageMinWidth = 1200;
        this.stageMinHeight = 800;
        
        
    }

    private void postInit(){
        initWindowControls();
    }
//==============================================================================
    @FXML
    private void minimizeWindow(ActionEvent event) {
        if (!stage.isIconified()) {
            stage.setIconified(true);
        }
    }

    @FXML
    private void maximizeWindow(ActionEvent event) {
        if (!stage.isMaximized()) {
            stage.setMaximized(true);
        } else {
            stage.setMaximized(false);
        }
    }

    @FXML
    private void exitApp(ActionEvent event) {
        Platform.exit();
    }

    private void initWindowControls() {   
        // drag controls
        dragBar.setOnMouseDragged((e) -> {
            stage.setX(e.getScreenX() - mX);
            stage.setY(e.getScreenY() - mY);
        });
        // window resizing
        resize.setOnMouseEntered(e->e.consume());
        resize.setOnMouseExited(e->e.consume());
        resize.setOnMousePressed((e) -> {
            dragOffsetX = (stage.getX() + stage.getWidth() - e.getScreenX());
            dragOffsetY = (stage.getY() + stage.getHeight() - e.getScreenY());
            e.consume();
        });
        resize.setOnMouseDragged((e) -> {
            double x = e.getScreenX() + dragOffsetX,
                    y = e.getScreenY() + dragOffsetY;
            double w = x - stage.getX();
            double h = y - stage.getY();
            stage.setWidth(Math.max(stageMinWidth, w));
            stage.setHeight(Math.max(stageMinHeight, h));
            e.consume();
        });
        
    }
    
    
    public final void setContent(Node n){
        contentPane.getChildren().add(n);
        super.requestParentLayout();
    }
    //**************************************************************************

    
    
}
