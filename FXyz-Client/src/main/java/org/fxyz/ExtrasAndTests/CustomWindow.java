/**
* CustomWindow.java
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
