package org.fxyz.controls.carousel;

import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.shape.Shape;

/**
 * Layout Items represent Nodes that are displayed by the CarouselSkin.  They are
 * updated during the layout pass by a LayoutPass with calculated values that can
 * be modified by extensions to change the final appearance of each item.<p>
 *
 * Note that these items are stateful and their values are only available and
 * modifiable at certain steps during a layout pass.
 */
public interface LayoutItem {
  Node getCell();
  Effect getEffect();
  Shape createReflectionShape();
}
