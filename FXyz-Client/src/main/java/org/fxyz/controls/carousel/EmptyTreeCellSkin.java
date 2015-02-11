package org.fxyz.controls.carousel;

import javafx.scene.control.TreeCell;

/**
 * TreeCellSkin with absolutely no additional paddings whatsoever.
 */
public class EmptyTreeCellSkin<T> extends com.sun.javafx.scene.control.skin.TreeCellSkin<T> {

  public EmptyTreeCellSkin(TreeCell<T> treeCell) {
    super(treeCell);
  }

  @Override
  protected void layoutChildren(double x, double y, double w, double h) {
    layoutLabelInArea(x, y, w, h);
  }
}

