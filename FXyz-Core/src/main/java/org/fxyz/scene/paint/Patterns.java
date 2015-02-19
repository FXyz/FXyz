/**
 * Patterns.java
 * 
* Copyright (c) 2013-2015, F(X)yz All rights reserved.
 * 
* Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the organization nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.fxyz.scene.paint;

import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javax.imageio.ImageIO;

/**
 *
 * @author jpereda
 */
public class Patterns {

    public enum CarbonPatterns {
        DARK_CARBON,
        LIGHT_CARBON,
        CARBON_KEVLAR;
    }

    private final int width;
    private final int height;

    private Image imgPattern;

    public Patterns(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Image createPattern(boolean save) {
        return createPattern(CarbonPatterns.DARK_CARBON, save);
    }

    public Image createPattern(CarbonPatterns cp, boolean save) {
        ImagePattern pattern = null;
        switch (cp) {
            case DARK_CARBON:
                pattern = createCarbonPattern();
                break;
            case LIGHT_CARBON:
                pattern = createLightCarbonPattern();
                break;
            case CARBON_KEVLAR:
                pattern = createCarbonKevlarPattern();
                break;
            default:
                pattern = createCarbonPattern();
                break;
        }

        Rectangle rectangle = new Rectangle(width, height);
        if (pattern != null) {
            rectangle.setFill(pattern);            
        }
        rectangle.setStrokeWidth(0);
        imgPattern = rectangle.snapshot(new SnapshotParameters(), null);
        if (save) {
            saveImage();
        }
        return imgPattern;
    }

    private void saveImage() {
        try {
            // save
            ImageIO.write(SwingFXUtils.fromFXImage(imgPattern, null), "png", new File("pattern_" + width + "x" + height + ".png"));
        } catch (IOException ex) {
            System.out.println("Error saving image");
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getPatternImage() {
        return imgPattern;
    }

    public static final ImagePattern createCarbonPattern() {
        final double WIDTH = 12;
        final double HEIGHT = 12;
        final Canvas CANVAS = new Canvas(WIDTH, HEIGHT);
        final GraphicsContext CTX = CANVAS.getGraphicsContext2D();

        double offsetY = 0;

        CTX.beginPath();
        CTX.rect(0, 0, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();

        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(35, 35, 35)),
                new Stop(1, Color.rgb(23, 23, 23))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.083333, 0, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(38, 38, 38)),
                new Stop(1, Color.rgb(30, 30, 30))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.5, HEIGHT * 0.5, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(35, 35, 35)),
                new Stop(1, Color.rgb(23, 23, 23))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.583333, HEIGHT * 0.5, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(38, 38, 38)),
                new Stop(1, Color.rgb(30, 30, 30))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.5, 0, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(48, 48, 48)),
                new Stop(1, Color.rgb(40, 40, 40))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.583333, HEIGHT * 0.083333, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.083333;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(53, 53, 53)),
                new Stop(1, Color.rgb(45, 45, 45))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(0, HEIGHT * 0.5, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(48, 48, 48)),
                new Stop(1, Color.rgb(40, 40, 40))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.083333, HEIGHT * 0.583333, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.583333;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(53, 53, 53)),
                new Stop(1, Color.rgb(45, 45, 45))));
        CTX.fill();

        final Image PATTERN_IMAGE = CANVAS.snapshot(new SnapshotParameters(), null);
        final ImagePattern PATTERN = new ImagePattern(PATTERN_IMAGE, 0, 0, WIDTH, HEIGHT, false);

        return PATTERN;
    }

    public static final ImagePattern createLightCarbonPattern() {
        final double WIDTH = 12;
        final double HEIGHT = 12;
        final Canvas CANVAS = new Canvas(WIDTH, HEIGHT);
        final GraphicsContext CTX = CANVAS.getGraphicsContext2D();

        double offsetY = 0;

        CTX.beginPath();
        CTX.rect(0, 0, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();

        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(138, 138, 138)),
                new Stop(1, Color.rgb(130, 130, 130))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.083333, 0, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(142, 142, 142)),
                new Stop(1, Color.rgb(130, 130, 130))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.5, HEIGHT * 0.5, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(138, 138, 138)),
                new Stop(1, Color.rgb(130, 130, 130))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.583333, HEIGHT * 0.5, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(142, 142, 142)),
                new Stop(1, Color.rgb(130, 130, 130))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.5, 0, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(152, 152, 152)),
                new Stop(1, Color.rgb(146, 146, 146))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.583333, HEIGHT * 0.083333, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.083333;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(160, 160, 160)),
                new Stop(1, Color.rgb(152, 152, 152))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(0, HEIGHT * 0.5, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(152, 152, 152)),
                new Stop(1, Color.rgb(146, 146, 146))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.083333, HEIGHT * 0.583333, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.583333;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(160, 160, 160)),
                new Stop(1, Color.rgb(152, 152, 152))));
        CTX.fill();

        final Image PATTERN_IMAGE = CANVAS.snapshot(new SnapshotParameters(), null);
        final ImagePattern PATTERN = new ImagePattern(PATTERN_IMAGE, 0, 0, WIDTH, HEIGHT, false);

        return PATTERN;
    }

    public static final ImagePattern createCarbonKevlarPattern() {
        final double WIDTH = 12;
        final double HEIGHT = 12;
        final Canvas CANVAS = new Canvas(WIDTH, HEIGHT);
        final GraphicsContext CTX = CANVAS.getGraphicsContext2D();

        double offsetY = 0;

        CTX.beginPath();
        CTX.rect(0, 0, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();

        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(35, 35, 35)),
                new Stop(1, Color.rgb(23, 23, 23))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.083333, 0, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(38, 38, 38)),
                new Stop(1, Color.rgb(30, 30, 30))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.5, HEIGHT * 0.5, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(35, 35, 35)),
                new Stop(1, Color.rgb(23, 23, 23))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.583333, HEIGHT * 0.5, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(38, 38, 38)),
                new Stop(1, Color.rgb(30, 30, 30))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.5, 0, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(48, 48, 48)),
                new Stop(1, Color.rgb(40, 40, 40))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.583333, HEIGHT * 0.083333, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.083333;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(53, 53, 53)),
                new Stop(1, Color.rgb(45, 45, 45))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(0, HEIGHT * 0.5, WIDTH * 0.5, HEIGHT * 0.5);
        CTX.closePath();
        offsetY = 0.5;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.5 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(48, 48, 48)),
                new Stop(1, Color.rgb(40, 40, 40))));
        CTX.fill();

        CTX.beginPath();
        CTX.rect(WIDTH * 0.083333, HEIGHT * 0.583333, WIDTH * 0.333333, HEIGHT * 0.416666);
        CTX.closePath();
        offsetY = 0.583333;
        CTX.setFill(new LinearGradient(0, offsetY * HEIGHT,
                0, 0.416666 * HEIGHT + offsetY * HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(53, 53, 53)),
                new Stop(1, Color.rgb(45, 45, 45))));
        CTX.fill();

        final Image PATTERN_IMAGE = CANVAS.snapshot(new SnapshotParameters(), null);
        final ImagePattern PATTERN = new ImagePattern(PATTERN_IMAGE, 0, 0, WIDTH, HEIGHT, false);

        return PATTERN;
    }

}
