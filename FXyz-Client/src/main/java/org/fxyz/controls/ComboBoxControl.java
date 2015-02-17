/**
* ComboBoxControl.java
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

package org.fxyz.controls;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class ComboBoxControl<T> extends ControlBase<Property<T>> {

    public ComboBoxControl(final String lbl, final Property<T> p, final Collection<T> items, boolean subControl) {
        super("ComboBoxControl.fxml", p);
        title.setText(lbl);
        selection.getItems().addAll(items);
        selection.setValue(p.getValue());
        controlledProperty.bind(selection.valueProperty());
        if (subControl) {
            subControlCache = FXCollections.observableHashMap();
            this.usesSubControls.set(subControl);
        }
    }

    @FXML
    protected ComboBox<T> selection;
    @FXML
    private Label title;
    @FXML
    private HBox hbox;
    @FXML
    private Pane spacer;
    @FXML
    protected VBox subControls;

    protected ObservableMap<T, List<ControlBase<Property<T>>>> subControlCache;

    public void addSubControl(final ControlBase ... controls) {
        if (useSubControls()) {
                subControlCache.putIfAbsent(selection.getValue(), Arrays.asList(controls));       
            //subControls.getChildren().add(subControlCache.get(control.controlledProperty));
        }
    }

    protected ObservableMap<T, List<ControlBase<Property<T>>>> getSubControlCache() {
        if (useSubControls()) {
            return subControlCache;
        } else {
            return null;
        }
    }

    private final BooleanProperty usesSubControls = new SimpleBooleanProperty(this, "usesSubControls", false) {

        @Override
        protected void invalidated() {
            super.invalidated();

        }

    };

    protected boolean useSubControls() {
        return usesSubControls.get();
    }

    public BooleanProperty usingSubControlsProperty() {
        return usesSubControls;
    }

}
