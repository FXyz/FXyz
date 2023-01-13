/**
 * NormalMap.java
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

package org.fxyz3d.tools;

import java.util.Random;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 *	Class represent a Normal Map Image.
 *  When it comes to Scaling think of intensity as the Macro, 
 *  and intensity scale as the micro	
    
    Also if you recieve a lot of "Bright" pixeling viewing object from the side, 
    Apply a SMALL amount of blur to the Image PRIOR to creating this Image.
    It should help smooth things out.

 * @author Jason Pollastrini aka jdub1581
 */
public class NormalMap extends WritableImage {

    private final double DEFAULT_INTENSITY = 5.0, DEFAULT_INTENSITY_SCALE = 5.0;
    private final boolean DEFAULT_INVERTED = new Random().nextBoolean();
    
    protected PixelReader pReader;
    private final PixelWriter pWriter;
    private final Image srcImage;
    
    
    public NormalMap(final Image src){
        super(src.getPixelReader(),0,0, (int)src.getWidth(), (int)src.getHeight());
        this.srcImage = src;
        this.pWriter = getPixelWriter();
        this.buildNormalMap(DEFAULT_INTENSITY, DEFAULT_INTENSITY_SCALE, DEFAULT_INVERTED);
    }

    public NormalMap(final double i, final double is, final boolean inv, final Image src){
        this(src);
        this.buildNormalMap(i, is, inv);
    }
    
    private void buildNormalMap(double scale, double scaleFactor, boolean invert) {

        pReader = srcImage.getPixelReader();
        final int w = (int) srcImage.getWidth();
        final int h = (int) srcImage.getHeight();

        final WritableImage gray = new WritableImage(w, h);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                gray.getPixelWriter().setColor(x, y, pReader.getColor(x, y).grayscale());
            }
        }

        final byte[] heightPixels = new byte[w * h * 4];
        final byte[] normalPixels = new byte[w * h * 4];
        // get pixels
        pReader = gray.getPixelReader();
        pReader.getPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(), heightPixels, 0, w * 4);

        if (invert) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    final int pixelIndex = (y * w * 4) + (x * 4);
                    heightPixels[pixelIndex + 0] = (byte) (255 - Byte.toUnsignedInt(heightPixels[pixelIndex]));
                    heightPixels[pixelIndex + 1] = (byte) (255 - Byte.toUnsignedInt(heightPixels[pixelIndex + 1]));
                    heightPixels[pixelIndex + 2] = (byte) (255 - Byte.toUnsignedInt(heightPixels[pixelIndex + 2]));
                    heightPixels[pixelIndex + 3] = (byte) (heightPixels[pixelIndex + 3]);
                }
            }
        }
        // generate normal map
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                final int yAbove = Math.max(0, y - 1);
                final int yBelow = Math.min(h - 1, y + 1);
                final int xLeft = Math.max(0, x - 1);
                final int xRight = Math.min(w - 1, x + 1);

                final int pixelIndex = (y * w * 4) + (x * 4);

                final int pixelAboveIndex = (yAbove * w * 4) + (x * 4);
                final int pixelBelowIndex = (yBelow * w * 4) + (x * 4);
                final int pixelLeftIndex = (y * w * 4) + (xLeft * 4);
                final int pixelRightIndex = (y * w * 4) + (xRight * 4);

                final int pixelAboveHeight = Byte.toUnsignedInt(heightPixels[pixelAboveIndex]);
                final int pixelBelowHeight = Byte.toUnsignedInt(heightPixels[pixelBelowIndex]);
                final int pixelLeftHeight = Byte.toUnsignedInt(heightPixels[pixelLeftIndex]);
                final int pixelRightHeight = Byte.toUnsignedInt(heightPixels[pixelRightIndex]);

                Point3D pixelAbove = new Point3D(x, yAbove, pixelAboveHeight);
                Point3D pixelBelow = new Point3D(x, yBelow, pixelBelowHeight);
                Point3D pixelLeft = new Point3D(xLeft, y, pixelLeftHeight);
                Point3D pixelRight = new Point3D(xRight, y, pixelRightHeight);

                Point3D H = pixelLeft.subtract(pixelRight);
                Point3D V = pixelAbove.subtract(pixelBelow);

                Point3D normal = H.crossProduct(V);
                normal = new Point3D(
                        (normal.getX() / w),
                        (normal.getY() / h),
                        (1 / normal.getZ()) / (Math.max(1.0, scale) / (scaleFactor))
                ).normalize();

                normalPixels[pixelIndex + 0] = (byte) (255 - (normal.getZ()));        //Blue              
                normalPixels[pixelIndex + 1] = (byte) (128 + (normal.getY() * 128.0));//Green                 
                normalPixels[pixelIndex + 2] = (byte) (128 + (normal.getX() * 128.0));//Red                 
                normalPixels[pixelIndex + 3] = (byte) (255);                          //alpha

            }
        }
        // create output image
        pWriter.setPixels(0, 0, w, h, PixelFormat.getByteBgraPreInstance(), normalPixels, 0, w * 4);
    }
    
    
    /*==========================================================================
        Properties
    */
    
    /**
     * 
     */
    private final DoubleProperty intensity = new SimpleDoubleProperty(this, "intensity" , DEFAULT_INTENSITY){

        
    };
    /**
     * 
     * @return 
     */
    public double getIntensity() {
        return intensity.get();
    }
    /**
     * 
     * @param value
     */
    public void setIntensity(double value) {
        intensity.set(value);
    }
    /**
     * 
     * @return intensity
     */
    public DoubleProperty intensityProperty() {
        return intensity;
    }

    private final DoubleProperty intensityScale = new SimpleDoubleProperty(this, "intensityScale" , DEFAULT_INTENSITY_SCALE){

        
    };
    /**
     * 
     * @return 
     */
    public double getIntensityScale() {
        return intensityScale.get();
    }
    /**
     * 
     * @param value
     */
    public void setIntensityScale(double value) {
        intensityScale.set(value);
    }
    /**
     * 
     * @return intensityScale
     */
    public DoubleProperty intensityScaleProperty() {
        return intensityScale;
    }

    private final BooleanProperty invertNormals = new SimpleBooleanProperty(this, "inverted" , DEFAULT_INVERTED){

        
    };
    /**
     * 
     * @return 
     */
    public boolean isInvertNormals() {
        return invertNormals.get();
    }
    /**
     * 
     * @param value
     */
    public void setInvertNormals(boolean value) {
        invertNormals.set(value);
    }
    /**
     * 
     * @return invertNormals
     */
    public BooleanProperty invertNormalsProperty() {
        return invertNormals;
    }
}
