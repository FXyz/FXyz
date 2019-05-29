/**
 * SimpleWindowFrame.java
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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.fxyz3d.ExtrasAndTests.CustomWindow;

/**
 * FXML Controller class
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class SimpleWindowFrame extends AnchorPane {

    @FXML
    private HBox headerBar;
    @FXML
    private Label appTitle;
    @FXML
    private ImageView iconView;
    @FXML
    private Pane headerSpacer;
    @FXML
    private HBox headerButtons;
    @FXML
    private Button minimize;
    @FXML
    private Button maximize;
    @FXML
    private Button exit;
    
    @FXML
    private StackPane rootContentPane;

    @FXML
    private Pane southEastResize;

    private final Stage stage;
    private double mX, mY, mOX, mOY, mDX, mDY, dragOffsetX, dragOffsetY;
    private final double stageMinWidth, stageMinHeight;

    public SimpleWindowFrame(Stage stage, double minWidth, double minHeight) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/org/fxyz3d/client/SimpleFrame.fxml"));
            loader.setController(SimpleWindowFrame.this);
            loader.setRoot(SimpleWindowFrame.this);

            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(CustomWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.stage = stage;
        
        this.stageMinWidth = minWidth;
        this.stageMinHeight = minHeight;

        initWindowControls();
        minimize.setOnAction(a -> {
            stage.setIconified(true);
        });
        maximize.setOnAction(a -> {
            if (!stage.isMaximized()) {
                stage.setMaximized(true);
            } else {
                stage.setMaximized(false);
            }
        });
        exit.setOnAction(a -> Platform.exit());
        
        
        //setTranslateX(stage.getX());
        //setTranslateY(stage.getY());
    }

    public void setRootContent(Node node) {
        rootContentPane.getChildren().clear();
        rootContentPane.getChildren().addAll(node);
        this.requestLayout();
    }

    public final void setOnExitAction(EventHandler<ActionEvent> value) {
        exit.setOnAction(value);
    }

    public final void setOnMinimizeAction(EventHandler<ActionEvent> value) {
        exit.setOnAction(value);
    }

    public final void setOnMaximizeAction(EventHandler<ActionEvent> value) {
        exit.setOnAction(value);
    }

    public final StringProperty textProperty() {
        return appTitle.textProperty();
    }

    public final void setText(String value) {
        appTitle.setText(value);
    }

    public final String getText() {
        return appTitle.getText();
    }

    public final void setFont(Font value) {
        appTitle.setFont(value);
    }

    public final BooleanProperty underlineProperty() {
        return appTitle.underlineProperty();
    }

    public final void setUnderline(boolean value) {
        appTitle.setUnderline(value);
    }

    public final void setTextFill(Paint value) {
        appTitle.setTextFill(value);
    }

    public final ObjectProperty<Paint> textFillProperty() {
        return appTitle.textFillProperty();
    }

    public final void setIconImage(Image value) {
        iconView.setImage(value);
    }

    public final Image getIconImage() {
        return iconView.getImage();
    }

    private void initWindowControls() {
        // drag controls
        sceneProperty().addListener(i -> {
            if (getScene() != null) {
                getScene().setOnMousePressed(e -> {
                    mOX = mX;
                    mOY = mY;
                    mX = e.getSceneX();
                    mY = e.getSceneY();
                    mDX = mX - mOX;
                    mDY = mY - mOY;

                });
            }
        });
        headerBar.setOnMouseDragged((e) -> {
            stage.setX(e.getScreenX() - mX);
            stage.setY(e.getScreenY() - mY);
        });
        // window resizing
        southEastResize.setCursor(Cursor.SE_RESIZE);

        southEastResize.setOnMouseEntered(e -> e.consume());
        southEastResize.setOnMouseExited(e -> e.consume());
        southEastResize.setOnMousePressed((e) -> {
            dragOffsetX = (getTranslateX() + getWidth() - e.getScreenX());
            dragOffsetY = (getTranslateY() + getHeight() - e.getScreenY());
        });        
        southEastResize.setOnMouseDragged((e) -> {
            double x = e.getScreenX() + dragOffsetX;
            double y = e.getScreenY() + dragOffsetY;
            double w = x - getTranslateX();
            double h = y - getTranslateY();

            setPrefWidth(Math.max(stageMinWidth, w));
            setPrefHeight(Math.max(stageMinHeight, h));

            stage.setWidth(getPrefWidth());
            stage.setHeight(getPrefHeight());
        });
    }

    public HBox getHeaderBar() {
        return headerBar;
    }

    public Label getAppTitle() {
        return appTitle;
    }

    public ImageView getIconView() {
        return iconView;
    }

    public Pane getHeaderSpacer() {
        return headerSpacer;
    }

    public Button getMinimize() {
        return minimize;
    }

    public Button getMaximize() {
        return maximize;
    }

    public Button getExit() {
        return exit;
    }

    public StackPane getRootContentPane() {
        return rootContentPane;
    }

    public Pane getSouthEastResize() {
        return southEastResize;
    }

    
    
    //==========================================================================
    public final Window getOwner() {
        return stage.getOwner();
    }

    public final void setFullScreen(boolean value) {
        stage.setFullScreen(value);
    }

    public final boolean isFullScreen() {
        return stage.isFullScreen();
    }

    public final ReadOnlyBooleanProperty fullScreenProperty() {
        return stage.fullScreenProperty();
    }

    public final ObservableList<Image> getIcons() {
        return stage.getIcons();
    }

    public final void setTitle(String value) {
        stage.setTitle(value);
    }

    public final String getTitle() {
        return stage.getTitle();
    }

    public final StringProperty titleProperty() {
        return stage.titleProperty();
    }

    public final void setIconified(boolean value) {
        stage.setIconified(value);
    }

    public final boolean isIconified() {
        return stage.isIconified();
    }

    public final ReadOnlyBooleanProperty iconifiedProperty() {
        return stage.iconifiedProperty();
    }

    public final void setMaximized(boolean value) {
        stage.setMaximized(value);
    }

    public final boolean isMaximized() {
        return stage.isMaximized();
    }

    public final ReadOnlyBooleanProperty maximizedProperty() {
        return stage.maximizedProperty();
    }

    public final void setAlwaysOnTop(boolean value) {
        stage.setAlwaysOnTop(value);
    }

    public final boolean isAlwaysOnTop() {
        return stage.isAlwaysOnTop();
    }

    public final ReadOnlyBooleanProperty alwaysOnTopProperty() {
        return stage.alwaysOnTopProperty();
    }

    public final void setResizable(boolean value) {
        stage.setResizable(value);
    }

    public final boolean isStageResizable() {
        return stage.isResizable();
    }

    public final BooleanProperty resizableProperty() {
        return stage.resizableProperty();
    }

    public final void setStageMinWidth(double value) {
        stage.setMinWidth(value);
    }

    public final double getStageMinWidth() {
        return stage.getMinWidth();
    }

    public final DoubleProperty minStageWidthProperty() {
        return stage.minWidthProperty();
    }

    public final void setStageMinHeight(double value) {
        stage.setMinHeight(value);
    }

    public final double getStageMinHeight() {
        return stage.getMinHeight();
    }

    public final DoubleProperty minStageHeightProperty() {
        return stage.minHeightProperty();
    }

    public final void setStageMaxWidth(double value) {
        stage.setMaxWidth(value);
    }

    public final double getStageMaxWidth() {
        return stage.getMaxWidth();
    }

    public final DoubleProperty maxStageWidthProperty() {
        return stage.maxWidthProperty();
    }

    public final void setStageMaxHeight(double value) {
        stage.setMaxHeight(value);
    }

    public final double getStageMaxHeight() {
        return stage.getMaxHeight();
    }

    public final DoubleProperty maxStageHeightProperty() {
        return stage.maxHeightProperty();
    }

    public void stageToFront() {
        stage.toFront();
    }

    public void stageToBack() {
        stage.toBack();
    }

    public void close() {
        stage.close();
    }

    public final void setFullScreenExitKeyCombination(KeyCombination keyCombination) {
        stage.setFullScreenExitKeyCombination(keyCombination);
    }

    public final KeyCombination getFullScreenExitKeyCombination() {
        return stage.getFullScreenExitKeyCombination();
    }

    public final ObjectProperty<KeyCombination> fullScreenExitKeyProperty() {
        return stage.fullScreenExitKeyProperty();
    }

    public final void setFullScreenExitHint(String value) {
        stage.setFullScreenExitHint(value);
    }

    public final String getFullScreenExitHint() {
        return stage.getFullScreenExitHint();
    }

    public final ObjectProperty<String> fullScreenExitHintProperty() {
        return stage.fullScreenExitHintProperty();
    }

    public void sizeToScene() {
        stage.sizeToScene();
    }

    public void centerOnScreen() {
        stage.centerOnScreen();
    }

    public final void setX(double value) {
        stage.setX(value);
    }

    public final double getX() {
        return stage.getX();
    }

    public final ReadOnlyDoubleProperty xProperty() {
        return stage.xProperty();
    }

    public final void setY(double value) {
        stage.setY(value);
    }

    public final double getY() {
        return stage.getY();
    }

    public final ReadOnlyDoubleProperty yProperty() {
        return stage.yProperty();
    }

    public final void setStageWidth(double value) {
        stage.setWidth(value);
    }

    public final double getStageWidth() {
        return stage.getWidth();
    }

    public final ReadOnlyDoubleProperty widthStageProperty() {
        return stage.widthProperty();
    }

    public final void setStageHeight(double value) {
        stage.setHeight(value);
    }

    public final double getStageHeight() {
        return stage.getHeight();
    }

    public final ReadOnlyDoubleProperty heightStageProperty() {
        return stage.heightProperty();
    }

    public final void requestStageFocus() {
        stage.requestFocus();
    }

    public final boolean isStageFocused() {
        return stage.isFocused();
    }

    public final ReadOnlyBooleanProperty focusedStageProperty() {
        return stage.focusedProperty();
    }

    public final Scene getStageScene() {
        return stage.getScene();
    }

    public final ReadOnlyObjectProperty<Scene> sceneStageProperty() {
        return stage.sceneProperty();
    }

    public final void setStageOpacity(double value) {
        stage.setOpacity(value);
    }

    public final double getStageOpacity() {
        return stage.getOpacity();
    }

    public final DoubleProperty opacityStageProperty() {
        return stage.opacityProperty();
    }

    public final void setOnCloseRequest(EventHandler<WindowEvent> value) {
        stage.setOnCloseRequest(value);
    }

    public final EventHandler<WindowEvent> getOnCloseRequest() {
        return stage.getOnCloseRequest();
    }

    public final ObjectProperty<EventHandler<WindowEvent>> onCloseRequestProperty() {
        return stage.onCloseRequestProperty();
    }

    public final void setOnShowing(EventHandler<WindowEvent> value) {
        stage.setOnShowing(value);
    }

    public final EventHandler<WindowEvent> getOnShowing() {
        return stage.getOnShowing();
    }

    public final ObjectProperty<EventHandler<WindowEvent>> onShowingProperty() {
        return stage.onShowingProperty();
    }

    public final void setOnShown(EventHandler<WindowEvent> value) {
        stage.setOnShown(value);
    }

    public final EventHandler<WindowEvent> getOnShown() {
        return stage.getOnShown();
    }

    public final ObjectProperty<EventHandler<WindowEvent>> onShownProperty() {
        return stage.onShownProperty();
    }

    public final void setOnHiding(EventHandler<WindowEvent> value) {
        stage.setOnHiding(value);
    }

    public final EventHandler<WindowEvent> getOnHiding() {
        return stage.getOnHiding();
    }

    public final ObjectProperty<EventHandler<WindowEvent>> onHidingProperty() {
        return stage.onHidingProperty();
    }

    public final void setOnHidden(EventHandler<WindowEvent> value) {
        stage.setOnHidden(value);
    }

    public final EventHandler<WindowEvent> getOnHidden() {
        return stage.getOnHidden();
    }

    public final ObjectProperty<EventHandler<WindowEvent>> onHiddenProperty() {
        return stage.onHiddenProperty();
    }

    public final boolean isShowing() {
        return stage.isShowing();
    }

    public final ReadOnlyBooleanProperty showingProperty() {
        return stage.showingProperty();
    }

    public void hide() {
        stage.hide();
    }

    public final void setStageEventDispatcher(EventDispatcher value) {
        stage.setEventDispatcher(value);
    }

    public final EventDispatcher getStageEventDispatcher() {
        return stage.getEventDispatcher();
    }

    public final ObjectProperty<EventDispatcher> stagrEventDispatcherProperty() {
        return stage.eventDispatcherProperty();
    }

    public final <T extends Event> void addStageEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        stage.addEventHandler(eventType, eventHandler);
    }

    public final <T extends Event> void removeStageEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        stage.removeEventHandler(eventType, eventHandler);
    }

    public final <T extends Event> void addStageEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
        stage.addEventFilter(eventType, eventFilter);
    }

    public final <T extends Event> void removeStageEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
        stage.removeEventFilter(eventType, eventFilter);
    }

    public final void fireStageEvent(Event event) {
        stage.fireEvent(event);
    }

    public EventDispatchChain buildStageEventDispatchChain(EventDispatchChain tail) {
        return stage.buildEventDispatchChain(tail);
    }

}
