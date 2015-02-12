/**
 * Copyright (c) 2013, 2014, ControlsFX All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of ControlsFX, any associated
 * website, nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.fxyz.ExtrasAndTests;

import org.fxyz.*;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.fxyz.model.EmptySample;
import org.fxyz.model.Project;
import org.fxyz.model.SampleTree.TreeNode;
import org.fxyz.model.WelcomePage;
import org.fxyz.util.SampleScanner;
import org.fxyz.util.SampleScanner;

public class FXyzSampler_backup extends Application {

    public static final String 
            GLASS_BLACK_SMOKE = FXyzSampler_backup.class.getResource("cyanBlackGlass.css").toExternalForm();
            ;
    private Map<String, Project> projectsMap;

    private Stage stage;
    
    private HBox rootContainer;
    private VBox leftContainer; 
    private StackPane centerContainer;
    private VBox rightContainer;
    

    private FXyzSample selectedSample;

    private TreeView<FXyzSample> samplesTreeView;
    private TreeItem<FXyzSample> root;

    public static void main(String[] args) {
        launch(args);        
    }

    @Override
    public void start(final Stage stage) throws Exception {
        Application.setUserAgentStylesheet(GLASS_BLACK_SMOKE);
        this.stage = stage;
//        primaryStage.getIcons().add(new Image("/org/controlsfx/samples/controlsfx-logo.png"));

        projectsMap = new SampleScanner().discoverSamples();
        buildSampleTree(null);
        
        rootContainer = new HBox();
        

        // --- left hand side
        leftContainer = new VBox();
        leftContainer.setSpacing(3.0);
        // search box
        final TextField searchBox = new TextField();
        searchBox.setPromptText("Search");
        searchBox.getStyleClass().addAll("search-box", "fxyz-control");
        searchBox.textProperty().addListener((Observable o) -> {
            buildSampleTree(searchBox.getText());
        });
        searchBox.setFocusTraversable(false);
        // treeview
        samplesTreeView = new TreeView<>(root);
        samplesTreeView.setShowRoot(false);
        samplesTreeView.getStyleClass().add("samples-tree");
        samplesTreeView.setMinWidth(USE_PREF_SIZE);
        samplesTreeView.setMaxWidth(Double.MAX_VALUE);
        samplesTreeView.setCellFactory(new Callback<TreeView<FXyzSample>, TreeCell<FXyzSample>>() {
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
        samplesTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<FXyzSample>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<FXyzSample>> observable, TreeItem<FXyzSample> oldValue, TreeItem<FXyzSample> newSample) {
               
                if (newSample == null) {
                    return;
                } else if (newSample.getValue() instanceof EmptySample) {
                    FXyzSample selectedSample = newSample.getValue();
                    Project selectedProject = projectsMap.get(selectedSample.getSampleName());
                    System.out.println(selectedProject);
                    if (selectedProject != null) {
                        changeToWelcomeTab(selectedProject.getWelcomePage());
                    }
                    return;
                }
                selectedSample = newSample.getValue();
                changeSample();
            }
        });
        samplesTreeView.setFocusTraversable(false);
        samplesTreeView.getStyleClass().add("fxyz-control");
        
        VBox.setVgrow(searchBox, Priority.NEVER);
        VBox.setVgrow(samplesTreeView, Priority.ALWAYS);
        
        leftContainer.getChildren().addAll(searchBox, samplesTreeView);
        leftContainer.setPrefSize(USE_PREF_SIZE, USE_COMPUTED_SIZE);
        HBox.setHgrow(leftContainer, Priority.SOMETIMES);

        // center stack
        centerContainer = new StackPane();        
        centerContainer.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        HBox.setHgrow(centerContainer, Priority.ALWAYS);
        // by default we'll show the welcome message of first project in the tree
        // if no projects are available, we'll show the default page
        List<TreeItem<FXyzSample>> projects = samplesTreeView.getRoot().getChildren();
        if (!projects.isEmpty()) {
            TreeItem<FXyzSample> firstProject = projects.get(0);
            samplesTreeView.getSelectionModel().select(firstProject);
        } else {
            changeToWelcomeTab(null);
        }
        
        rightContainer = new VBox();     
        rightContainer.setPrefSize(USE_PREF_SIZE, Double.MAX_VALUE);
        HBox.setHgrow(rightContainer, Priority.SOMETIMES);
        
        // scene root
        rootContainer = new HBox(leftContainer, centerContainer, rightContainer);
        rootContainer.setAlignment(Pos.CENTER);
        rootContainer.setSpacing(5);
        rootContainer.setPadding(new Insets(3));
        rootContainer.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        rootContainer.setFillHeight(true);
        
        
        // put it all together
        Scene scene = new Scene(rootContainer, 1024, 800, true, SceneAntialiasing.BALANCED);
        
        scene.setFill(Color.gray(0.6));
                
        stage.setScene(scene);

        // set width / height values to be 75% of users screen resolution
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setWidth(screenBounds.getWidth() * 0.75);
        stage.setHeight(screenBounds.getHeight() * .75);
        //stage.setMinWidth(grid.getPrefWidth());
        stage.setTitle("FXyz-Sampler!");
        stage.show();
        
        rootContainer.getStyleClass().addAll("client-root");
        
    }
    
    private String getUserInterfaceFXML(){
        String fxmlPath = "", userDir;
        userDir = System.getProperty("user.dir");
        
        
        return fxmlPath;
    }

    /*==========================================================================
    *                               TreeView
    ==========================================================================*/
    protected void buildSampleTree(String searchText) {
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
            TreeNode n = project.getSampleTree().getRoot();
            root.getChildren().add(n.createTreeItem());
        }

        // with this newly built and full tree, we filter based on the search text
        if (searchText != null) {
            pruneSampleTree(root, searchText);

            // FIXME weird bug in TreeView I think
            samplesTreeView.setRoot(null);
            samplesTreeView.setRoot(root);
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

    /*==========================================================================
     *                          Sample Content Area
     =========================================================================*/
    
    private void changeToWelcomeTab(WelcomePage wPage) {
        //change to index above 0 -> 0 will be content header overlay
        centerContainer.getChildren().removeIf(index-> centerContainer.getChildren().indexOf(index) == 0 && index instanceof StackPane);
        
        if (null == wPage) {
            wPage = getDefaultWelcomePage();            
        }
        centerContainer.getChildren().addAll(wPage.getContent());
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
    
    protected void changeSample() {
        if (selectedSample == null) {
            return;
        }

        if (!centerContainer.getChildren().isEmpty()) {
            centerContainer.getChildren().clear();                        
            rightContainer.getChildren().clear();            
        }
        
        updateTab();
    }

    private void updateTab() {
        centerContainer.getChildren().addAll(buildSampleTabContent(selectedSample));
        rightContainer.getChildren().add(selectedSample.getControlPanel());
    }
    
    /*==========================================================================
    *                           Source Code Methods
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
    
    

    private Node buildSampleTabContent(FXyzSample sample) {
        return FXyzSampleBase.buildSample(sample, stage);
    }    

    
}