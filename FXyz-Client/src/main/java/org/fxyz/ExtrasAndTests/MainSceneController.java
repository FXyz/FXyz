/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.ExtrasAndTests;

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
