/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    
    
    private final int width;
    private final int height;
    
    private Image imgPattern;
    
    public Patterns(int width, int height){
        this.width=width;
        this.height=height;
    }
    
    public Image createPattern(boolean save){
        ImagePattern pattern = createCarbonPattern();
        Rectangle rectangle=new Rectangle(width,height);
        rectangle.setFill(pattern);
        rectangle.setStrokeWidth(0);
        imgPattern=rectangle.snapshot(new SnapshotParameters(), null);
        if(save){
            saveImage();
        }
        return imgPattern;
    }
    
    private void saveImage(){
        try {
            // save
            ImageIO.write(SwingFXUtils.fromFXImage(imgPattern, null), "png", new File("pattern_"+width+"x"+height+".png"));
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
        final double WIDTH        = 12;
        final double HEIGHT       = 12;
        final Canvas CANVAS       = new Canvas(WIDTH, HEIGHT);
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
