/**
 * SampleTree.java
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

package org.fxyz.model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TreeItem;
import org.fxyz.FXyzSample;

public class SampleTree {

    private TreeNode root;

    private int count = 0;

    public SampleTree(FXyzSample rootSample) {
        root = new TreeNode(null, null, rootSample);
    }

    public TreeNode getRoot() {
        return root;
    }

    public Object size() {
        return count;
    }

    public void addSample(String[] packages, FXyzSample sample) {
        if (packages.length == 0) {
            root.addSample(sample);
            return;
        }

        TreeNode n = root;
        for (String packageName : packages) {
            if (n.containsChild(packageName)) {
                n = n.getChild(packageName);
            } else {
                TreeNode newNode = new TreeNode(packageName);
                n.addNode(newNode);
                n = newNode;
            }
        }

        if (n.packageName.equals(packages[packages.length - 1])) {
            n.addSample(sample);
            count++;
        }
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public static class TreeNode {

        private final FXyzSample sample;
        private final String packageName;

        private final TreeNode parent;
        private List<TreeNode> children;

        public TreeNode() {
            this(null, null, null);
        }

        public TreeNode(String packageName) {
            this(null, packageName, null);
        }

        public TreeNode(TreeNode parent, String packageName, FXyzSample sample) {
            this.children = new ArrayList<>();
            this.sample = sample;
            this.parent = parent;
            this.packageName = packageName;
        }

        public boolean containsChild(String packageName) {
            if (packageName == null) {
                return false;
            }

            return children.stream().anyMatch((n) -> (packageName.equals(n.packageName)));
        }

        public TreeNode getChild(String packageName) {
            if (packageName == null) {
                return null;
            }

            for (TreeNode n : children) {
                if (packageName.equals(n.packageName)) {
                    return n;
                }
            }
            return null;
        }

        public void addSample(FXyzSample sample) {
            children.add(new TreeNode(this, null, sample));
        }

        public void addNode(TreeNode n) {
            children.add(n);
        }

        public FXyzSample getSample() {
            return sample;
        }

        public String getPackageName() {
            return packageName;
        }

        public TreeItem<FXyzSample> createTreeItem() {
            TreeItem<FXyzSample> treeItem = null;

            if (sample != null) {
                treeItem = new TreeItem<>(sample);
            } else if (packageName != null) {
                treeItem = new TreeItem<>(new EmptySample(packageName));
            }
            if (treeItem != null) {
                treeItem.setExpanded(true);

                // recursively add in children
                for (TreeNode n : children) {
                    treeItem.getChildren().add(n.createTreeItem());
                }
            }
            return treeItem;
        }

        @Override
        public String toString() {
            if (sample != null) {
                return " Sample [ sampleName: " + sample.getSampleName() + ", children: " + children + " ]";
            } else {
                return " Sample [ packageName: " + packageName + ", children: " + children + " ]";
            }
        }
    }
}
