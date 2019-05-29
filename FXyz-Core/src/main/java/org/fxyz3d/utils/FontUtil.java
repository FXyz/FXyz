/**
 * FontUtil.java
 *
 * Copyright (c) 2018-2019, F(X)yz
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
package org.fxyz3d.utils;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class FontUtil {

    private static final Text helper = new Text();
    private static final double DEFAULT_WRAPPING_WIDTH = helper.getWrappingWidth();
    private static final double DEFAULT_LINE_SPACING = helper.getLineSpacing();
    private static final String DEFAULT_TEXT = helper.getText();
    private static final TextBoundsType DEFAULT_BOUNDS_TYPE = helper.getBoundsType();

    private FontUtil() {}

    /**
     * Returns the width of the specified text if rendered with the specified font.
     *
     * @param font          font used for rendering
     * @param text          text to measure
     * @param wrappingWidth wrapping width ( string width is defined as {@code min(text.prefWidth, text.wrappingWidth)} )
     * @return the width of the specified text
     */
    public static double computeStringWidth(Font font, String text, double wrappingWidth) {
        // see com.sun.javafx.scene.control.skin.Utils
        helper.setText(text);
        helper.setFont(font);
        // Note that the wrapping width needs to be set to zero before
        // getting the text's real preferred width.
        helper.setWrappingWidth(0);
        helper.setLineSpacing(0);
        double w = Math.min(helper.prefWidth(-1), wrappingWidth);
        helper.setWrappingWidth((int) Math.ceil(w));
        w = Math.ceil(helper.getLayoutBounds().getWidth());
        // RESTORE STATE
        helper.setWrappingWidth(DEFAULT_WRAPPING_WIDTH);
        helper.setLineSpacing(DEFAULT_LINE_SPACING);
        helper.setText(DEFAULT_TEXT);
        return w;
    }

    /**
     * Returns the width of the specified text (rendered with the specified font).
     *
     * @param font font used for rendering
     * @param text text to measure
     * @return the width of the specified text
     */
    public static double computeStringWidth(Font font, String text) {
        return computeStringWidth(font, text, Double.MAX_VALUE);
    }

    /**
     * Returns the height of the specified text (rendered with the specified font).
     *
     * @param font          font used for rendering
     * @param text          text to measure
     * @param wrappingWidth wrapping width ( string width is defined as {@code min(text.prefWidth, text.wrappingWidth)} )
     * @param boundsType    bounds type
     * @return the height of the specified text
     */
    public static double computeStringHeight(Font font, String text, double wrappingWidth, TextBoundsType boundsType) {
        return computeStringHeight(font, text, wrappingWidth, 0, boundsType);
    }

    /**
     * Returns the height of the specified text (rendered with the specified font).
     *
     * @param font          font used for rendering
     * @param text          text to measure
     * @param wrappingWidth wrapping width ( string width is defined as {@code min(text.prefWidth, text.wrappingWidth)} )
     * @param lineSpacing   line spacing
     * @param boundsType    bounds type
     * @return the height of the specified text
     */
    static double computeStringHeight(Font font, String text, double wrappingWidth, double lineSpacing,
                                      TextBoundsType boundsType) {
        // see com.sun.javafx.scene.control.skin.Utils
        helper.setText(text);
        helper.setFont(font);
        helper.setWrappingWidth((int) wrappingWidth);
        helper.setLineSpacing((int) lineSpacing);
        helper.setBoundsType(boundsType);
        final double height = helper.getLayoutBounds().getHeight();
        // RESTORE STATE
        helper.setWrappingWidth(DEFAULT_WRAPPING_WIDTH);
        helper.setLineSpacing(DEFAULT_LINE_SPACING);
        helper.setText(DEFAULT_TEXT);
        helper.setBoundsType(DEFAULT_BOUNDS_TYPE);
        return height;
    }
}
