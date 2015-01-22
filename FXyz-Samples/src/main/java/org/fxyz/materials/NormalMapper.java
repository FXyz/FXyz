/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.materials;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.fxyz.FXyzSample;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class NormalMapper extends FXyzSample{

    @Override
    public Node getSample() {
        return new StackPane();
    }

    @Override
    public String getSampleName() {
        return getClass().getSimpleName();
    }

    @Override
    public Node getPanel(Stage stage) {
        return getSample();
    }

    @Override
    public String getJavaDocURL() {
        return null;
    }
    
}
