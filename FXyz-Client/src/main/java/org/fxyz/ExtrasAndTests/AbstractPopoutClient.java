/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.ExtrasAndTests;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import static javafx.scene.layout.AnchorPane.setBottomAnchor;
import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public abstract class AbstractPopoutClient extends AnchorPane {

    @FXML
    protected StackPane topPane;
    @FXML
    protected StackPane menuPane;
    @FXML
    protected HBox centerPane;
    @FXML
    protected StackPane center;
    @FXML
    protected StackPane right;
    @FXML
    protected StackPane bottomPane;

    @FXML
    protected Pane menuPaneTrigger;
    @FXML
    protected Pane bottomPaneTrigger;
    @FXML
    protected Pane topPaneTrigger;

    protected Stage stage;

    private AbstractPopoutClient() {
        try {
            FXMLLoader ldr = getUILoader();
            ldr.setController(this);
            ldr.setRoot(this);
            ldr.load();
        } catch (IOException ex) {
            Logger.getLogger(SimpleSamplerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public AbstractPopoutClient(Stage stage, boolean popsEnabled) {
        this();
        if (popsEnabled) {
            initialize();
        }
        this.stage = stage;
    }

    private void initialize() {
        menuPaneLocation.addListener(it -> updateMenuPaneAnchors());
        bottomPaneLocation.addListener(it -> updateBottomPaneAnchors());

        showMenuPaneProperty().addListener(it -> animateMenuPane());
        showBottomPaneProperty().addListener(it -> animateBottomPane());

        menuPane.setOnMouseExited(evt -> {
            if (menuPane.getOpacity() == 1) {
                setShowMenuPane(false);
                menuPaneTrigger.toFront();
                menuPaneTrigger.setVisible(true);
            }
        });
        menuPaneTrigger.setOnMouseEntered(e -> {
            if (menuPane.getOpacity() != 1) {
                setShowMenuPane(true);
                menuPaneTrigger.toBack();
                menuPaneTrigger.setVisible(false);
            }
        });
        bottomPaneTrigger.setOnMouseEntered(e -> {
            if (bottomPane.getOpacity() != 1) {
                setShowBottomPane(true);
                bottomPaneTrigger.toBack();
                bottomPaneTrigger.setVisible(false);
            }
        });
        bottomPane.setOnMouseClicked(evt -> {
            if (bottomPane.getOpacity() == 1) {
                setShowBottomPane(false);
                bottomPaneTrigger.toFront();
                bottomPaneTrigger.setVisible(true);
            }            
        });
    }

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

    /*
     * The updateMenu/BottomPaneAnchors methods get called whenever the value of
     * menuPaneLocation or bottomPaneLocation changes. Setting anchor pane
     * constraints will automatically trigger a relayout of the anchor pane
     * children.
     */
    private void updateMenuPaneAnchors() {
        setLeftAnchor(menuPane, getMenuPaneLocation());
        setLeftAnchor(centerPane, getMenuPaneLocation() + menuPane.getWidth());
    }

    private void updateBottomPaneAnchors() {
        setBottomAnchor(bottomPane, getBottomPaneLocation());
        setBottomAnchor(centerPane,
                getBottomPaneLocation() + bottomPane.getHeight());
        setBottomAnchor(menuPane,
                getBottomPaneLocation() + bottomPane.getHeight());
    }

    /*
     * Starts the animation for the menu pane.
     */
    private void animateMenuPane() {
        if (isShowMenuPane()) {
            slideMenuPane(0);
        } else {
            slideMenuPane(-menuPane.prefWidth(-1));
        }
    }

    /*
     * Starts the animation for the bottom pane.
     */
    private void animateBottomPane() {
        if (isShowBottomPane()) {
            slideBottomPane(0);
        } else {
            slideBottomPane(-bottomPane.prefHeight(-1));
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
        KeyValue keyValue = new KeyValue(menuPaneLocation, toX, Interpolator.EASE_BOTH);
        KeyValue visible = new KeyValue(menuPane.opacityProperty(), menuPane.getOpacity() == 1 ? 0 : 1);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(600), keyValue, visible);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }

    private void slideBottomPane(double toY) {
        KeyValue keyValue = new KeyValue(bottomPaneLocation, toY, Interpolator.EASE_BOTH);
        KeyValue visible = new KeyValue(bottomPane.opacityProperty(), bottomPane.getOpacity() == 1 ? 0 : 1);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(600), keyValue, visible);
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

    protected String getFXMLPath() {
        return "Client_Slide_Outs.fxml";
    }

    private FXMLLoader getUILoader() {
        return new FXMLLoader(getClass().getResource(getFXMLPath()));
    }

    public StackPane getTopPane() {
        return topPane;
    }

    public StackPane getMenuPane() {
        return menuPane;
    }

    public StackPane getCenter() {
        return center;
    }

    public StackPane getRight() {
        return right;
    }

    public StackPane getBottomPane() {
        return bottomPane;
    }

    public Pane getBottomPaneTrigger() {
        return bottomPaneTrigger;
    }

}
