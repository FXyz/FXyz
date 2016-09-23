/**
 * ModelInfoTracker.java
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
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.HiddenSidesPane;
import org.fxyz.ExtrasAndTests.CustomWindow;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ModelInfoTracker extends StackPane {
    @FXML
    private HBox headerArea;
    @FXML
    private Label sampleTitle;
    @FXML
    private Button hideStatus;
    @FXML
    private HBox content;
    @FXML
    private Label nodeCount;
    @FXML
    private Label timeToBuild;
    @FXML
    private Label width;
    @FXML
    private Label height;
    @FXML
    private Label depth;
    @FXML
    private Label points;
    @FXML
    private Label faces;

    private HiddenSidesPane parentPane;
    public ModelInfoTracker(HiddenSidesPane parent) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/org/fxyz/client/ModelInfo.fxml"));
            loader.setController(ModelInfoTracker.this);
            loader.setRoot(ModelInfoTracker.this);

            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(CustomWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.parentPane = parent;
        hideStatus.setFocusTraversable(false);
        hideStatus.setOnAction(e->{
            parentPane.setPinnedSide(null);
        });
        
        this.setOnMouseEntered(e->{
            if(parentPane.getPinnedSide() != Side.BOTTOM){
                parentPane.setPinnedSide(Side.BOTTOM);
            }
        });
    }
    
    public Label getSampleTitle() {
        return sampleTitle;
    }

    public Label getNodeCount() {
        return nodeCount;
    }

    public Label getTimeToBuild() {
        return timeToBuild;
    }

    public Label getBoundsWidth() {
        return width;
    }

    public Label getBoundsHeight() {
        return height;
    }

    public Label getBoundsDepth() {
        return depth;
    }

    public Label getPoints() {
        return points;
    }

    public Label getFaces() {
        return faces;
    }

       
    
}
