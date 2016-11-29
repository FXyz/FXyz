/**
 * MainSceneController.java
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

package org.fxyz3d.ExtrasAndTests;

import java.io.File;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class MainSceneController {
 
    @FXML
    private ResourceBundle resources;
 
    @FXML
    private BorderPane externalPane;
 
    @FXML
    private TextField fxmlFileNameTextField;
 
    @FXML
    private Button chooseFxmlFileButton;
 
    @FXML
    private Button loadFxmlFileButton;
 
    private StringProperty fxmlFileName;
 
    public void initialize() {
        fxmlFileNameTextField.textProperty().bindBidirectional(fxmlFileNameProperty());
        loadFxmlFileButton.disableProperty().bind(fxmlFileNameProperty().isEmpty());
    }
 
    public StringProperty fxmlFileNameProperty() {
        if (fxmlFileName == null) {
            fxmlFileName = new SimpleStringProperty("");
        }
        return fxmlFileName;
    }
 
    public String getFxmlFileName() {
        return fxmlFileNameProperty().getValue();
    }
 
    public void setFxmlFileName(String fxmlFileName) {
        this.fxmlFileNameProperty().setValue(fxmlFileName);
    }
 
    @FXML
    public void chooseFxmlFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose FXML file to load");
        if (getFxmlFileName().isEmpty()) {
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        } else {
            chooser.setInitialDirectory(new File(getFxmlFileName()).getParentFile());
        }
 
        File file = chooser.showOpenDialog(chooseFxmlFileButton.getScene().getWindow());
        if (file != null) {
            setFxmlFileName(file.getAbsolutePath());
        }
    }
 
    @FXML
    public void onLoadExternalFxml() {
        //try {
            //Optional<URL> url = FxmlUtils.getFxmlUrl(Paths.get(getFxmlFileName()));
            //if (true)//url.isPresent()) {
                //Pane pane = FxmlUtils.loadFxmlPane(url.get(), resources);
                //externalPane.setCenter(pane);
            //} else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText(getFxmlFileName() + " could not be found!");
                alert.show();
            //}
        //} catch (IOException ex) {
            //Dialogs.create().showException(ex);
        //}
    }
}
