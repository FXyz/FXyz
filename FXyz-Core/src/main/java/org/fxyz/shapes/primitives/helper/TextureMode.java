/**
 * TextureMode.java
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

package org.fxyz.shapes.primitives.helper;

import java.util.List;
import java.util.function.Function;
import javafx.scene.paint.Color;
import org.fxyz.geometry.Point3D;
import org.fxyz.scene.paint.Palette.ColorPalette;
import org.fxyz.scene.paint.Patterns.CarbonPatterns;

/**
 *
 * @author usuario
 */
public interface TextureMode {
    
    public void setTextureModeNone();
    public void setTextureModeNone(Color color);
    public void setTextureModeNone(Color color, String image);
    public void setTextureModeImage(String image);
    public void setTextureModePattern(CarbonPatterns pattern, double scale);
    public void setTextureModeVertices3D(int colors, Function<Point3D, Number> dens);
    public void setTextureModeVertices3D(ColorPalette palette, int colors, Function<Point3D, Number> dens);
    public void setTextureModeVertices3D(int colors, Function<Point3D, Number> dens, double min, double max);
    public void setTextureModeVertices1D(int colors, Function<Number, Number> function);
    public void setTextureModeVertices1D(ColorPalette palette, int colors, Function<Number, Number> function);
    public void setTextureModeVertices1D(int colors, Function<Number, Number> function, double min, double max);
    public void setTextureModeFaces(int colors);
    public void setTextureModeFaces(ColorPalette palette, int colors);
    
    public void updateF(List<Number> values);
}
