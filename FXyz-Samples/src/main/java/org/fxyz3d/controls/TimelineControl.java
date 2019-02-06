/**
 * TimelineControl.java
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

import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author Jose Pereda
 */
public class TimelineControl extends ControlBase<Property<Timeline>> {

    @FXML private Label title;

    @FXML private StackPane timelineDisplay;
    @FXML private Pane background;
    @FXML private HBox progressBar;
    @FXML private Pane bar;
    @FXML private Text startTxt;
    @FXML private Text currentTxt;
    @FXML private Text endTxt;

    @FXML private HBox controls;
    @FXML private Button startBtn;
    @FXML private Button rwBtn;
    @FXML private ToggleButton playBtn;
    @FXML private Button ffBtn;
    @FXML private Button endBtn;
    @FXML private ToggleButton loopBtn;

    private final ChangeListener<Number> rateListener = (obs, ov, nv) -> {
        if (nv.intValue() == 0 && playBtn.isSelected()) {
            playBtn.setSelected(false);
        }
    };

    private final DoubleProperty currentTimeAsPercentage = new SimpleDoubleProperty(0);

    public final Timeline getTimeline() { return timeline.get(); }

    private final ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>() {
        private Timeline old;
        @Override protected void invalidated() {
            Timeline t = get();
            if (old != null) {
                currentTimeAsPercentage.unbind();
                currentTxt.textProperty().unbind();
                endTxt.textProperty().unbind();
                bar.prefWidthProperty().unbind();
                old.currentRateProperty().removeListener(rateListener);
            }
            if (t == null) {
                timelineDisplay.setVisible(false);
                controls.setDisable(true);
            } else {
                timelineDisplay.setVisible(true);
                currentTimeAsPercentage.bind(Bindings.createDoubleBinding(
                        () ->  t.getCurrentTime().toMillis() / t.getCycleDuration().toMillis(),
                        t.currentTimeProperty(), t.cycleDurationProperty()));
                endTxt.textProperty().bind(Bindings.createStringBinding(
                        () -> String.format("%.2fs", t.getCycleDuration().toSeconds()),
                        t.cycleDurationProperty()));
                currentTxt.textProperty().bind(Bindings.createStringBinding(
                        () -> String.format("%.2fs", t.getCurrentTime().toSeconds()),
                        t.currentTimeProperty()));
                bar.prefWidthProperty().bind(Bindings.createDoubleBinding(
                        () -> progressBar.getWidth() * currentTimeAsPercentage.get(),
                        currentTimeAsPercentage));

                controls.setDisable(false);
                playBtn.setSelected(t.getCurrentRate() != 0);
                loopBtn.setSelected(t.getCycleDuration().equals(Timeline.INDEFINITE));
                t.currentRateProperty().addListener(rateListener);
            }
            old = t;
        }
    };

    public TimelineControl(Property<Timeline> prop, String name) {
        super("/org/fxyz3d/controls/TimelineControl.fxml", prop);
        timeline.bindBidirectional(prop);
        title.setText(name);

        background.setCache(true);

        startBtn.setOnAction(e -> {
            Timeline timeline = getTimeline();
            timeline.jumpTo(Duration.ZERO);
            timeline.pause();
        });
        endBtn.setOnAction(e -> {
            Timeline timeline = getTimeline();
            timeline.jumpTo(timeline.getTotalDuration());
            timeline.pause();
        });
        playBtn.setOnAction(e -> {
            Timeline timeline = getTimeline();
            if (playBtn.isSelected()) {
                timeline.play();
            } else {
                timeline.pause();
            }
        });
        ffBtn.setOnMousePressed(e -> setTimelineRate(2));
        ffBtn.setOnMouseReleased(e -> setTimelineRate(1));
        rwBtn.setOnMousePressed(e -> setTimelineRate(-2));
        rwBtn.setOnMouseReleased(e -> setTimelineRate(1));
        loopBtn.setOnAction(e -> {
            Timeline timeline = getTimeline();
            timeline.stop();
            if (loopBtn.isSelected()) {
                timeline.setCycleCount(Timeline.INDEFINITE);
            } else {
                timeline.setCycleCount(1);
            }
            timeline.play();
        });

    }

    private void setTimelineRate(int rate) {
        getTimeline().setRate(rate);
    }

}
