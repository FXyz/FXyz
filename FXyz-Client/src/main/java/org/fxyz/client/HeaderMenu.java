/**
 * HeaderMenu.java
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
