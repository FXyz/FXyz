/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.fxyz.FXyzSample;
import org.fxyz.FXyzSampleBase;
import org.fxyz.model.EmptySample;
import org.fxyz.model.Project;
import org.fxyz.model.SampleTree;
import org.fxyz.model.WelcomePage;
import org.fxyz.util.SampleScanner;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class SimpleSliderClient extends AbstractPopoutClient {

    private TreeItem<FXyzSample> root;
    private final Map<String, Project> projectsMap;
    private FXyzSample selectedSample;
    private TreeView<FXyzSample> contentTree;
    private TextField searchBar;

    public SimpleSliderClient(Stage stage, boolean popsEnabled) {
        super(stage, popsEnabled);
        this.projectsMap = new SampleScanner().discoverSamples();
        buildProjectTree(null);

        VBox treeMenu = new VBox();
        treeMenu.setFillWidth(true);
        treeMenu.setSpacing(3);
        treeMenu.setPadding(new Insets(3));

        contentTree = new TreeView<>(root);
        contentTree.setFocusTraversable(false);

        searchBar = new TextField();
        searchBar.setFocusTraversable(false);
        searchBar.setMaxWidth(USE_COMPUTED_SIZE);
        searchBar.getStyleClass().add("fxyz-search-box");
        searchBar.textProperty().addListener((Observable o) -> {
            buildProjectTree(searchBar.getText());
        });

        treeMenu.getChildren().addAll(searchBar, contentTree);
        VBox.setVgrow(contentTree, Priority.ALWAYS);

        menuPane.getChildren().add(treeMenu);
        menuPane.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        contentTree.setShowRoot(false);
        contentTree.getStyleClass().add("samples-tree");
        contentTree.setMinWidth(USE_PREF_SIZE);
        contentTree.setMaxWidth(Double.MAX_VALUE);
        contentTree.setCellFactory(new Callback<TreeView<FXyzSample>, TreeCell<FXyzSample>>() {
            @Override
            public TreeCell<FXyzSample> call(TreeView<FXyzSample> param) {
                return new TreeCell<FXyzSample>() {
                    @Override
                    protected void updateItem(FXyzSample item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setText("");
                        } else {
                            setText(item.getSampleName());
                        }
                    }
                };
            }
        });
        contentTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<FXyzSample>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<FXyzSample>> observable, TreeItem<FXyzSample> oldValue, TreeItem<FXyzSample> newSample) {

                if (newSample == null) {
                    return;
                } else if (newSample.getValue() instanceof EmptySample) {
                    FXyzSample selectedSample = newSample.getValue();
                    Project selectedProject = projectsMap.get(selectedSample.getSampleName());
                    System.out.println(selectedProject);
                    if (selectedProject != null) {
                        changeToWelcomePage(selectedProject.getWelcomePage());
                    }
                    return;
                }
                selectedSample = newSample.getValue();
                changeContent();
            }
        });

        List<TreeItem<FXyzSample>> projects = contentTree.getRoot().getChildren();
        if (!projects.isEmpty()) {
            TreeItem<FXyzSample> firstProject = projects.get(0);
            contentTree.getSelectionModel().select(firstProject);
        } else {
            changeToWelcomePage(null);
        }

        parentProperty().addListener(l -> {
            if (getParent() != null) {
                getParent().requestLayout();
            }
        });

        setShowMenuPane(false);
        setShowBottomPane(false);
    }

    protected final void buildProjectTree(String searchText) {
        // rebuild the whole tree (it isn't memory intensive - we only scan
        // classes once at startup)
        root = new TreeItem<>(new EmptySample("FXyz-Sampler"));
        root.setExpanded(true);

        for (String projectName : projectsMap.keySet()) {
            final Project project = projectsMap.get(projectName);
            if (project == null) {
                continue;
            }

            // now work through the project sample tree building the rest
            SampleTree.TreeNode n = project.getSampleTree().getRoot();
            root.getChildren().add(n.createTreeItem());
        }

        // with this newly built and full tree, we filter based on the search text
        if (searchText != null) {
            pruneSampleTree(root, searchText);

            // FIXME weird bug in TreeView I think
            contentTree.setRoot(null);
            contentTree.setRoot(root);
        }

        // and finally we sort the display a little
        sort(root, (o1, o2) -> o1.getValue().getSampleName().compareTo(o2.getValue().getSampleName()));
    }

    private void sort(TreeItem<FXyzSample> node, Comparator<TreeItem<FXyzSample>> comparator) {
        node.getChildren().sort(comparator);
        for (TreeItem<FXyzSample> child : node.getChildren()) {
            sort(child, comparator);
        }
    }

    // true == keep, false == delete
    private boolean pruneSampleTree(TreeItem<FXyzSample> treeItem, String searchText) {
        // we go all the way down to the leaf nodes, and check if they match
        // the search text. If they do, they stay. If they don't, we remove them.
        // As we pop back up we check if the branch nodes still have children,
        // and if not we remove them too
        if (searchText == null) {
            return true;
        }

        if (treeItem.isLeaf()) {
            // check for match. Return true if we match (to keep), and false
            // to delete
            return treeItem.getValue().getSampleName().toUpperCase().contains(searchText.toUpperCase());
        } else {
            // go down the tree...
            List<TreeItem<FXyzSample>> toRemove = new ArrayList<>();

            for (TreeItem<FXyzSample> child : treeItem.getChildren()) {
                boolean keep = pruneSampleTree(child, searchText);
                if (!keep) {
                    toRemove.add(child);
                }
            }

            // remove the unrelated tree items
            treeItem.getChildren().removeAll(toRemove);

            // return true if there are children to this branch, false otherwise
            // (by returning false we say that we should delete this now-empty branch)
            return !treeItem.getChildren().isEmpty();
        }
    }

    public String getSearchString() {
        return searchBar.getText();
    }

    private void changeToWelcomePage(WelcomePage wPage) {
        //change to index above 0 -> 0 will be content header overlay
        center.getChildren().clear();

        if (null == wPage) {
            wPage = getDefaultWelcomePage();
        }
        center.getChildren().addAll(wPage.getContent());
    }

    private WelcomePage getDefaultWelcomePage() {
        // line 1
        Label welcomeLabel1 = new Label("Welcome to FXSampler!");
        welcomeLabel1.setStyle("-fx-font-size: 2em; -fx-padding: 0 0 0 5;");

        // line 2
        Label welcomeLabel2 = new Label(
                "Explore the available UI controls and other interesting projects "
                + "by clicking on the options to the left.");
        welcomeLabel2.setStyle("-fx-font-size: 1.25em; -fx-padding: 0 0 0 5;");

        WelcomePage wPage = new WelcomePage("Welcome!", new VBox(5, welcomeLabel1, welcomeLabel2));
        return wPage;
    }

    protected void changeContent() {
        if (selectedSample == null) {
            return;
        }
        right.getChildren().clear();
        if (!center.getChildren().isEmpty()) {
            center.getChildren().clear();

        }

        updateContent();
    }

    private void updateContent() {
        center.getChildren().addAll(buildSampleTabContent(selectedSample));
        // below add labels / textflow if needed preferably befor controls  

        Node controls = selectedSample.getControlPanel();
        if (controls != null) {
            VBox.setVgrow(controls, Priority.ALWAYS);
            right.getChildren().addAll(controls);
        }

    }

    private Node buildSampleTabContent(FXyzSample sample) {
        return FXyzSampleBase.buildSample(sample, stage);
    }

    public Map<String, Project> getProjectsMap() {
        return projectsMap;
    }

    public FXyzSample getSelectedSample() {
        return selectedSample;
    }

}
