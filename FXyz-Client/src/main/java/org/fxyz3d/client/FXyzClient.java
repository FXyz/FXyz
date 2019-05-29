/**
 * FXyzClient.java
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

package org.fxyz3d.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.fxyz3d.FXyzSample;
import org.fxyz3d.FXyzSampleBase;
import org.fxyz3d.model.EmptySample;
import org.fxyz3d.model.Project;
import org.fxyz3d.model.SampleTree;
import org.fxyz3d.model.WelcomePage;
import org.fxyz3d.util.SampleScanner;


public class FXyzClient extends Application {

    public static final String 
            BACKGROUNDS = FXyzClient.class.getResource("/org/fxyz3d/client/clientBackgrounds.css").toExternalForm(),
            BLACK_GLASS_BASE = FXyzClient.class.getResource("/org/fxyz3d/client/smokeBlackGlassBase.css").toExternalForm();
            //BLACK_GLASS_CONTROLS = FXyzClient.class.getResource("/org/fxyz3d/client/smokeBlackGlassControls.css").toExternalForm();

    private static final int MIN_WINDOW_WIDTH = 800;
    private static final int MIN_WINDOW_HEIGHT = 600;

    private static final int INITIAL_WINDOW_WIDTH = 1200;
    private static final int INITIAL_WINDOW_HEIGHT = 768;

    private static FXyzClient rootClientInstance;

    public FXyzClient() {
        rootClientInstance = FXyzClient.this;
    }
    
    
    
    private Map<String, Project> projectsMap;
    private Stage stage;
    private FXyzSample selectedSample;
    private TextField searchBar;
    private TreeView<FXyzSample> contentTree;
    private TreeItem<FXyzSample> root;

    private VBox leftSideContent, contentControls;
    private StackPane centerContent;
    private HBox contentPane;
    
    private HiddenSidesClient client;
    private SimpleWindowFrame frame;

    @Override
    public void start(final Stage mainStage) throws Exception {
        
        stage = mainStage;
        stage.getIcons().add(new Image(FXyzClient.class.getResource("/org/fxyz3d/images/logo2.png").toExternalForm()));
        
        projectsMap = new SampleScanner().discoverSamples();
        buildProjectTree(null);

        leftSideContent = new VBox();
        leftSideContent.setAlignment(Pos.TOP_CENTER);
        leftSideContent.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        leftSideContent.setSpacing(3);
        leftSideContent.setPadding(new Insets(3));
        leftSideContent.getStyleClass().add("fxyz3d-control");
        
        contentControls = new VBox();
        contentControls.getStyleClass().add("fxyz3d-control");
        contentControls.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
               
        centerContent = new StackPane();
        centerContent.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        
        searchBar = new TextField();
        searchBar.setFocusTraversable(false);
        searchBar.setPrefSize(USE_COMPUTED_SIZE, USE_PREF_SIZE);
        searchBar.textProperty().addListener((Observable o) -> {
            buildProjectTree(searchBar.getText());
        });
        searchBar.setOnMouseEntered(e->{
            if(client.getPinnedSide() == null){
                client.setPinnedSide(Side.LEFT);
            }
        });
        HBox.setHgrow(searchBar, Priority.ALWAYS);
        
        final Button ab = new Button();
        ab.setAlignment(Pos.CENTER);
        ab.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        ab.setOnAction(e->{
            client.setPinnedSide(null);
        });
        
        contentTree = new TreeView<>(root);
        contentTree.getStyleClass().add("fxyz3d-control");
        contentTree.setShowRoot(false);
        contentTree.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
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
        contentTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<FXyzSample>> observable, TreeItem<FXyzSample> oldValue, TreeItem<FXyzSample> newSample) -> {
            if (newSample == null) {
                return;
            } else if (newSample.getValue() instanceof EmptySample) {
                FXyzSample selectedSample1 = newSample.getValue();
                Project selectedProject = projectsMap.get(selectedSample1.getSampleName());
                System.out.println(selectedProject);
                if (selectedProject != null) {
                    changeToWelcomePage(selectedProject.getWelcomePage());
                }
                return;
            }
            selectedSample = newSample.getValue();
            changeContent();
        });
        contentTree.setFocusTraversable(false);
        leftSideContent.getChildren().addAll(new HBox(searchBar, ab),contentTree);       
        VBox.setVgrow(contentTree, Priority.ALWAYS);

        client = new HiddenSidesClient();
        client.setContent(centerContent);
        client.setLeft(leftSideContent);
        client.setTriggerDistance(20);
        
        frame = new SimpleWindowFrame(stage, MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
        frame.setText("Fxyz-SamplerApp <ver: 0.1.1>");
        frame.setRootContent(client);
                
        List<TreeItem<FXyzSample>> projects = contentTree.getRoot().getChildren();
        if (!projects.isEmpty()) {
            TreeItem<FXyzSample> firstProject = projects.get(0);
            contentTree.getSelectionModel().select(firstProject);
        } else {
            changeToWelcomePage(null);
        }

        Scene scene = new Scene(frame, INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().addAll(BLACK_GLASS_BASE);

        this.stage.setScene(scene);
        this.stage.initStyle(StageStyle.TRANSPARENT);
        this.stage.show();

        System.err.println(contentTree.getRoot().getChildren());
    }

    /*/SimpleSamplerClient client = new SimpleSamplerClient(stage);  
        
     //Look at the clientBackgrounds.css file in resources for others
     //client.getStyleClass().add("comp-fade-background");
        
     SimpleSliderClient ssc = new SimpleSliderClient(stage, true);
     ssc.getStyleClass().add("blue-fade-background");
        
     Scene scene = new Scene(ssc, 1200, 800);//client, client.getPrefWidth(), client.getPrefHeight(), true, SceneAntialiasing.BALANCED);    
     scene.setCamera(new ParallelCamera());
     scene.setFill(null);
     scene.getStylesheets().addAll(BACKGROUNDS);
        
     stage.setScene(scene);
     stage.show();    
     */
    
    /*==========================================================================
     Load all Items into TreeView
     */
    protected final void buildProjectTree(String searchText) {
        // rebuild the whole tree (it isn't memory intensive - we only scan
        // classes once at startup)
        root = new TreeItem<>(new EmptySample("FXyz-Samples"));
        root.setExpanded(true);

        for (String projectName : projectsMap.keySet()) {
            final Project project = projectsMap.get(projectName);
            if (project == null) {
                System.err.println("null: " + project);
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
        node.getChildren().stream().forEach((child) -> {
            sort(child, comparator);
        });
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

            treeItem.getChildren().stream().forEach((child) -> {
                boolean keep = pruneSampleTree(child, searchText);
                if (!keep) {
                    toRemove.add(child);
                }
            });

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
        centerContent.getChildren().clear();
        if (null == wPage) {
            wPage = getDefaultWelcomePage();
        }
        centerContent.getChildren().addAll(wPage.getContent());
    }

    private WelcomePage getDefaultWelcomePage() {
        // line 1
        Label welcomeLabel1 = new Label("Welcome to FXyz-Sampler!");
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

        contentControls.getChildren().clear();
        
        if (!centerContent.getChildren().isEmpty()) {
            centerContent.getChildren().clear();
        }

        updateContent();
    }

    private void updateContent() {
        SplitPane cPane = new SplitPane();
        cPane.getStyleClass().add("fxyz-split-pane");
        cPane.setDividerPositions(0.75);

        cPane.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        
        Node content = buildSampleContent(selectedSample);
        HBox.setHgrow(content, Priority.ALWAYS);
        
        Node controls = selectedSample.getControlPanel();
        if(controls != null){
            contentControls.getChildren().add(controls);
            VBox.setVgrow(controls, Priority.ALWAYS);
        }
        
        cPane.getItems().addAll(content, contentControls);
        centerContent.getChildren().addAll(cPane);
        centerContent.toBack();
        
    }

    private Node buildSampleContent(FXyzSample sample) {
        return FXyzSampleBase.buildSample(sample, stage);
    }

    /*==========================================================================
     *                          Source Code Methods
     ==========================================================================*/
    private String getResource(String resourceName, Class<?> baseClass) {
        Class<?> clz = baseClass == null ? getClass() : baseClass;
        return getResource(clz.getResourceAsStream(resourceName));
    }

    private String getResource(InputStream is) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getSourceCode(FXyzSample sample) {
        String sourceURL = sample.getSampleSourceURL();

        try {
            // try loading via the web or local file system
            URL url = new URL(sourceURL);
            InputStream is = url.openStream();
            return getResource(is);
        } catch (IOException e) {
            // no-op - the URL may not be valid, no biggy
        }

        return getResource(sourceURL, sample.getClass());
    }

    private String formatSourceCode(FXyzSample sample) {
        String sourceURL = sample.getSampleSourceURL();
        String src;
        if (sourceURL == null) {
            src = "No sample source available";
        } else {
            src = "Sample Source not found";
            try {
                src = getSourceCode(sample);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        // Escape '<' by "&lt;" to ensure correct rendering by SyntaxHighlighter
        src = src.replace("<", "&lt;");

        String template = getResource("/fxsampler/util/SourceCodeTemplate.html", null);
        return template.replace("<source/>", src);
    }

    private String formatCss(FXyzSample sample) {
        String cssUrl = sample.getControlStylesheetURL();
        String src;
        if (cssUrl == null) {
            src = "No CSS source available";
        } else {
            src = "Css not found";
            try {
                src = new String(
                        Files.readAllBytes(Paths.get(getClass().getResource(cssUrl).toURI()))
                );
            } catch (URISyntaxException | IOException ex) {
                ex.printStackTrace();
            }
        }

        // Escape '<' by "&lt;" to ensure correct rendering by SyntaxHighlighter
        src = src.replace("<", "&lt;");

        String template = getResource("/fxsampler/util/CssTemplate.html", null);
        return template.replace("<source/>", src);
    }

    public static FXyzClient getRootClientInstance() {
        return rootClientInstance;
    }
    
    

    public static void main(String[] args) {
        launch(args);
    }
}
