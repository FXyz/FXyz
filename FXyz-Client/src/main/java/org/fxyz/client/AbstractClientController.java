/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.client;

import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public abstract class AbstractClientController extends AnchorPane implements Client, Initializable{
    
    protected Stage stage;
    
    protected abstract void loadClientProperties();
    protected abstract void saveClientProperties();
    
    protected abstract void initHeader();
    protected abstract void initLeftPanel();
    protected abstract void initCenterContentPane();
    protected abstract void initCenterContentHeaderOverlay();
    protected abstract void initRightPanel();
    protected abstract void initFooter();   
    
    protected abstract void changeContent();
    protected abstract String getFXMLPath();
            
    protected abstract void buildProjectTree(String searchText);

    public final Stage getStage() {
        return stage;
    }

    public final void setStage(Stage stage) {
        if(null == this.stage){
            this.stage = stage;
        }
    }
    
    
}
