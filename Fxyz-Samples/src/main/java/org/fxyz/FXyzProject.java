/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.fxyz.model.WelcomePage;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class FXyzProject implements FXyzSamplerProject{
    
        
    //@Override
    @Override
    public final String getProjectName() {
        return "FXyz-Samples";
    }

    //@Override
    @Override
    public final String getSampleBasePackage() {
        return "org.fxyz.samples";
    }

    //@Override
    @Override
    public WelcomePage getWelcomePage() {
        VBox vBox = new VBox();
        //ImageView imgView = new ImageView();
        //imgView.setStyle("-fx-image: url('org/controlsfx/samples/ControlsFX.png');");
        StackPane pane = new StackPane();
        pane.setPrefHeight(207);
        //pane.getChildren().add(imgView);
        Label label = new Label();
        label.setWrapText(true);
        StringBuilder desc = new StringBuilder();
        desc.append("F(X)yz is an open source project for JavaFX that aims ");
        desc.append("to provide really high quality UI controls and other tools to ");
        desc.append("complement the core JavaFX 3D distribution.");
        desc.append("\n\n");
        desc.append("Explore the available tools and shapes by clicking on the options to the left.");
        label.setText(desc.toString());
        label.setStyle("-fx-font-size: 1.5em; -fx-padding: 20 0 0 5;");
        vBox.getChildren().addAll(pane, label);
        WelcomePage wPage = new WelcomePage("Welcome to FXyzLib!", vBox);
               
        return wPage;
    }
    
}
