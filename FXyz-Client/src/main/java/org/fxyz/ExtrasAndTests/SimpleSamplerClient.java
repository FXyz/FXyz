/**
* SimpleSamplerClient.java
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

package org.fxyz.ExtrasAndTests;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
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
public class SimpleSamplerClient extends AbstractClientController {

    @FXML
    private BorderPane rootBorderPane;
    @FXML
    private HBox header;
    @FXML
    private StackPane menuPane;
    @FXML
    private VBox leftSide;
    @FXML
    private TextField searchBar;
    @FXML
    private TreeView<FXyzSample> contentTree;
    @FXML
    private HBox statusBar;
    @FXML
    private HBox footer;
    @FXML
    private HBox leftStatusContainer;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private HBox rightStatusContainer;
    @FXML
    private Label rightStatusLabel;
    @FXML
    private StackPane contentPane;
    @FXML
    private VBox centerOverlay;
    @FXML
    private HBox sceneTrackerOverlay;
    @FXML
    private VBox rightSide;
    @FXML
    private VBox descriptionPane;
    @FXML
    private Pane leftSlideTrigger;

    private TreeItem<FXyzSample> root;
    private final Map<String, Project> projectsMap;
    private FXyzSample selectedSample;

    public SimpleSamplerClient(final Stage stage) {
        try {
            FXMLLoader ldr = getUILoader();
            ldr.setController(SimpleSamplerClient.this);
            ldr.setRoot(SimpleSamplerClient.this);
            ldr.load();
        } catch (IOException ex) {
            Logger.getLogger(SimpleSamplerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.stage = stage;
        this.projectsMap = new SampleScanner().discoverSamples();
        buildProjectTree(null);
        initController();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void initController() {
        initHeader();
        initLeftPanel();
        initCenterContentPane();
        initCenterContentHeaderOverlay();
        initRightPanel();
        initFooter();
        initialize();
    }

    /**
     * *************************************************************************
     * Path to FXML (extend later for custom layouts (sample:
     * MainSceneController)
     * ************************************************************************
     */
    @Override
    public final FXMLLoader getUILoader() {
        return new FXMLLoader(getClass().getResource(getFXMLPath()));

    }

    @Override
    protected String getFXMLPath() {
        return "Client.fxml";
    }

    /**
     * *************************************************************************
     * Header Setup
     * ************************************************************************
     */
    @Override
    protected void initHeader() {

    }

    /**
     * *************************************************************************
     * LeftPanel Setup
     * ************************************************************************
     */
    @Override
    protected void initLeftPanel() {
        contentTree.setRoot(root);

        searchBar.textProperty().addListener((Observable o) -> {
            buildProjectTree(searchBar.getText());
        });
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

    }

    /**
     * *************************************************************************
     * ContentSetup Setup
     * ************************************************************************
     */
    @Override
    protected void initCenterContentPane() {
        List<TreeItem<FXyzSample>> projects = contentTree.getRoot().getChildren();
        if (!projects.isEmpty()) {
            TreeItem<FXyzSample> firstProject = projects.get(0);
            contentTree.getSelectionModel().select(firstProject);
        } else {
            changeToWelcomePage(null);
        }
    }

    /**
     * *************************************************************************
     * Content Header Overlay Setup
     * ************************************************************************
     */
    @Override
    protected void initCenterContentHeaderOverlay() {
    }

    /**
     * *************************************************************************
     * Controls Setup
     * ************************************************************************
     */
    @Override
    protected void initRightPanel() {
        
    }

    /**
     * *************************************************************************
     * Footer Setup
     * ************************************************************************
     */
    @Override
    protected void initFooter() {
    }

    /**
     * *************************************************************************
     * Persistent Properties Setup
     * ************************************************************************
     */
    @Override
    protected void loadClientProperties() {
    }

    @Override
    protected void saveClientProperties() {
    }

    /**
     * *************************************************************************
     * ControlsFX FXSampler setup for loading samples
     *
     * /////////////////////////////////////////////////////////////////////////
     */
    /*==========================================================================
     Load all Items into TreeView
     */
    @Override
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
        contentPane.getChildren().removeIf(index -> contentPane.getChildren().indexOf(index) == 0 && index instanceof StackPane);

        if (null == wPage) {
            wPage = getDefaultWelcomePage();
        }
        contentPane.getChildren().addAll(wPage.getContent());
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

    @Override
    protected void changeContent() {
        if (selectedSample == null) {
            return;
        }

        if (!contentPane.getChildren().isEmpty()) {
            contentPane.getChildren().clear();
            rightSide.getChildren().clear();
        }

        updateContent();
    }

    private void updateContent() {
        contentPane.getChildren().addAll(buildSampleTabContent(selectedSample));
        // below add labels / textflow if needed preferably befor controls  
        Node controls = selectedSample.getControlPanel();
        VBox.setVgrow(controls, Priority.ALWAYS);
        rightSide.getChildren().addAll(controls);
        setShowMenuPane(false);
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

    /*==========================================================================
     Getters and Setters for FXML and SamplerApp Samples
     */
    public BorderPane getRootBorderPane() {
        return rootBorderPane;
    }

    public HBox getHeader() {
        return header;
    }

    public VBox getLeftSide() {
        return leftSide;
    }

    public TextField getSearchBar() {
        return searchBar;
    }

    public TreeView<FXyzSample> getContentTree() {
        return contentTree;
    }

    public HBox getStatusBar() {
        return statusBar;
    }

    public HBox getFooter() {
        return footer;
    }

    public HBox getLeftStatusContainer() {
        return leftStatusContainer;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public HBox getRightStatusContainer() {
        return rightStatusContainer;
    }

    public Label getRightStatusLabel() {
        return rightStatusLabel;
    }

    public StackPane getContentPane() {
        return contentPane;
    }

    public VBox getCenterOverlay() {
        return centerOverlay;
    }

    public HBox getSceneTrackerOverlay() {
        return sceneTrackerOverlay;
    }

    public VBox getRightSide() {
        return rightSide;
    }

    //==========================================================================
    //      OPTIONAL Pop-out trays based on Derick Limmerman(?) example
    private final BooleanProperty showMenuPane = new SimpleBooleanProperty(this, "showMenuPane", true);

    public final boolean isShowMenuPane() {
        return showMenuPane.get();
    }

    public final void setShowMenuPane(boolean showMenu) {
        showMenuPane.set(showMenu);
    }

    /**
     * Returns the property used to control the visibility of the menu panel.
     * When the value of this property changes to false then the menu panel will
     * slide out to the left).
     *     
* @return the property used to control the menu panel
     */
    public final BooleanProperty showMenuPaneProperty() {
        return showMenuPane;
    }

    private final BooleanProperty showBottomPane = new SimpleBooleanProperty(this, "showBottomPane", true);

    public final boolean isShowBottomPane() {
        return showBottomPane.get();
    }

    public final void setShowBottomPane(boolean showBottom) {
        showBottomPane.set(showBottom);
    }

    /**
     * Returns the property used to control the visibility of the bottom panel.
     * When the value of this property changes to false then the bottom panel
     * will slide out to the left).
     *     
* @return the property used to control the bottom panel
     */
    public final BooleanProperty showBottomPaneProperty() {
        return showBottomPane;
    }

    public final void initialize() {
        menuPaneLocation.addListener(it -> updateMenuPaneAnchors());
        bottomPaneLocation.addListener(it -> updateBottomPaneAnchors());

        showMenuPaneProperty().addListener(it -> animateMenuPane());
        showBottomPaneProperty().addListener(it -> animateBottomPane());

        menuPane.setOnMouseExited(evt -> setShowMenuPane(false));
        leftSlideTrigger.setOnMouseEntered(evt -> setShowMenuPane(true));
        //menuPane.setOnMouseClicked(evt -> setShowMenuPane(false));

        contentPane.setOnMouseClicked(evt -> {
            //setShowMenuPane(true);
            //setShowBottomPane(true);
        });

        footer.setOnMouseClicked(evt -> setShowBottomPane(false));
    }

    /*
     * The updateMenu/BottomPaneAnchors methods get called whenever the value of
     * menuPaneLocation or bottomPaneLocation changes. Setting anchor pane
     * constraints will automatically trigger a relayout of the anchor pane
     * children.
     */
    private void updateMenuPaneAnchors() {
        setLeftAnchor(menuPane, getMenuPaneLocation());
        setLeftAnchor(contentPane, getMenuPaneLocation() + menuPane.getWidth());
    }

    private void updateBottomPaneAnchors() {
        setBottomAnchor(footer, getBottomPaneLocation());
        setBottomAnchor(contentPane,
                getBottomPaneLocation() + footer.getHeight());
        setBottomAnchor(menuPane,
                getBottomPaneLocation() + footer.getHeight());
    }

    /*
     * Starts the animation for the menu pane.
     */
    private void animateMenuPane() {
        if (isShowMenuPane()) {
            slideMenuPane(0);
        } else {
            slideMenuPane(-leftSide.prefWidth(-1));
        }
    }

    /*
     * Starts the animation for the bottom pane.
     */
    private void animateBottomPane() {
        if (isShowBottomPane()) {
            slideBottomPane(0);
        } else {
            slideBottomPane(-footer.prefHeight(-1));
        }
    }

    /*
     * The animations are using the JavaFX timeline concept. The timeline updates
     * properties. In this case we have to introduce our own properties further
     * below (menuPaneLocation, bottomPaneLocation) because ultimately we need to
     * update layout constraints, which are not properties. So this is a little
     * work-around.
     */
    private void slideMenuPane(double toX) {
        KeyValue keyValue = new KeyValue(menuPaneLocation, toX);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }

    private void slideBottomPane(double toY) {
        KeyValue keyValue = new KeyValue(bottomPaneLocation, toY);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }

    private DoubleProperty menuPaneLocation = new SimpleDoubleProperty(this, "menuPaneLocation");

    private double getMenuPaneLocation() {
        return menuPaneLocation.get();
    }

    private DoubleProperty bottomPaneLocation = new SimpleDoubleProperty(this, "bottomPaneLocation");

    private double getBottomPaneLocation() {
        return bottomPaneLocation.get();
    }

}
