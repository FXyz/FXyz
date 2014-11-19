/*
 * Copyright (C) 2013-2014 F(X)yz, 
 * Sean Phillips, Jason Pollastrini
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fxyz.utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;

/**
 *
 * @author jpereda
 */
public class Palette {
    
    private final int numColors;
    private int width;
    private int height;
    private Image imgPalette;
    
    public Palette(int numColors){
        this.numColors=numColors;
    }
    
    public Image createPalette(boolean save){
        if(numColors<1){
            return null;
        }
        // try to create a square image
        width=(int)Math.sqrt(numColors);
        height=numColors/width;
        
        imgPalette = new WritableImage(width, height);
        PixelWriter   pw  = ((WritableImage)imgPalette).getPixelWriter();
        AtomicInteger count = new AtomicInteger();
        IntStream.range(0, height).boxed()
                .forEach(y->IntStream.range(0, width).boxed()
                        .forEach(x->pw.setColor(x, y, getColor(count.getAndIncrement()))));
        if(save){
            saveImage();
        }
        return imgPalette;
    }
    
    public DoubleStream getTextureLocation(int iPoint){
        if(width==0 || height==0){
            return DoubleStream.of(0f,0f);
        }
        int y = iPoint/width; 
        int x = iPoint-width*y;
        return DoubleStream.of((((float)x)/((float)width)),(((float)y)/((float)height)));
    }
    
    private void saveImage(){
        try {
            // save
            ImageIO.write(SwingFXUtils.fromFXImage(imgPalette, null), "jpg", new File("palette_"+numColors+".jpg"));
        } catch (IOException ex) { 
            System.out.println("Error saving image");
        }
    }
    
    private Color getColor(int iColor){
        
        //TODO: add already defined nice palettes
        return Color.hsb(360*(double) iColor / (double) numColors, 1d, 1d);
        
//        return Color.rgb((iColor >> 16) & 0xFF, (iColor >> 8) & 0xFF, iColor & 0xFF);
    }

    public int getNumColors() {
        return numColors;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImgPalette() {
        return imgPalette;
    }
    
    
}
