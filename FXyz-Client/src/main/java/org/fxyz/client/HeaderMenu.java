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
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import org.fxyz.ExtrasAndTests.CustomWindow;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class HeaderMenu extends HBox{
    @FXML
    private MenuBar mainMenuBar;
    @FXML
    private Menu fileMenu;
    @FXML
    private Menu exportMenu;
    @FXML
    private MenuItem fxmlExport;
    @FXML
    private MenuItem javaExport;
    @FXML
    private MenuItem objExport;
    @FXML
    private Menu editMenu;
    @FXML
    private MenuItem undoItem;
    @FXML
    private MenuItem redoItem;
    @FXML
    private MenuItem resetSceneItem;
    @FXML
    private MenuBar aboutMenuBar;
    @FXML
    private Menu aboutMenu;

    public HeaderMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("HeaderMenu.fxml"));
            loader.setController(HeaderMenu.this);
            loader.setRoot(HeaderMenu.this);

            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(CustomWindow.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    public HeaderMenu(double spacing) {
        throw new UnsupportedOperationException("not supported");
    }

    public HeaderMenu(Node... children) {
        throw new UnsupportedOperationException("not supported");
    }

    public HeaderMenu(double spacing, Node... children) {
        throw new UnsupportedOperationException("not supported");
    }

    
    
}
