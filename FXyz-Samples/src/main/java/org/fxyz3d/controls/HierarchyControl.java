/**
 * HierarchyControl.java
 *
 * Copyright (c) 2013-2019, F(X)yz
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

package org.fxyz3d.controls;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import org.fxyz3d.controls.ControlBase;

/**
 * FXML Controller class
 *
 * @author Jose Pereda
 */
public class HierarchyControl extends ControlBase<Property<Node>>{

    @FXML private TreeTableView<Node> hierarchyTreeTable;
    @FXML private TreeTableColumn<Node, String> nodeColumn;
    @FXML private TreeTableColumn<Node, String> idColumn;
    @FXML private TreeTableColumn<Node, Boolean> visibilityColumn;


    public HierarchyControl(Property<Node> prop) {
        super("/org/fxyz3d/controls/HierarchyControl.fxml", prop);
        nodeColumn.setCellValueFactory(p -> p.getValue().valueProperty().asString());
        idColumn.setCellValueFactory(p -> p.getValue().getValue().idProperty());
        visibilityColumn.setCellValueFactory(p -> p.getValue().getValue().visibleProperty());
        visibilityColumn.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(visibilityColumn));

        loadBindings();
    }

    public final void loadBindings() {
        if (controlledProperty == null) {
            return;
        }
        hierarchyTreeTable.rootProperty().bind(Bindings.createObjectBinding(() -> {
                Node content3D = controlledProperty.getValue();
                if (content3D != null) {
                    return new TreeItemImpl(content3D);
                } else {
                    return null;
                }
            }, controlledProperty));
    }

    private class TreeItemImpl extends TreeItem<Node> {

        public TreeItemImpl(Node node) {
            super(node);
            if (node instanceof Parent) {
                for (Node n : ((Parent) node).getChildrenUnmodifiable()) {
                    getChildren().add(new TreeItemImpl(n));
                }
            }
            node.setOnMouseClicked(t -> {
                TreeItem<Node> parent = getParent();
                while (parent != null) {
                    parent.setExpanded(true);
                    parent = parent.getParent();
                }
                hierarchyTreeTable.getSelectionModel().select(TreeItemImpl.this);
                hierarchyTreeTable.scrollTo(hierarchyTreeTable.getSelectionModel().getSelectedIndex());
                t.consume();
            });
        }
    }
}
