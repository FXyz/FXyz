package org.fxyz.controls.carousel;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TreeView;
import javafx.scene.effect.PerspectiveTransform;

// TODO cell density is meaningless for non-spaced version; cell spacing is only meaningful for non-spaced version
public class RibbonCarouselSkin<T> extends AbstractCarouselSkin<T> {

  public RibbonCarouselSkin(TreeView<T> carousel) {
    super(carousel);
  }

  @Override
  protected void delegateLayout(double fractionalIndex) {
    new RibbonLayoutPass(fractionalIndex).createLayout();
  }

  /**
   * Provides subclasses with a method to customize each cell calculated during
   * a layout pass.
   *
   * @param layoutPass a layout pass
   */
  protected void customizeCell(RibbonLayoutPass layoutPass) {
  }

  public class RibbonLayoutPass extends AbstractLayoutPass<RibbonLayoutItem> {
    private final int baseIndex;
    private final double halfWidth;

    private int nextCount;
    private int previousCount;
    private double minX = Double.MAX_VALUE;
    private double maxX = Double.MIN_VALUE;

    public RibbonLayoutPass(double fractionalIndex) {
      super(fractionalIndex);

      int centerIndex = getSkinnable().getFocusModel().getFocusedIndex() - (int)Math.round(fractionalIndex);

      this.baseIndex = centerIndex == -1 ? 0 : centerIndex;
      this.halfWidth = getSkinnable().getWidth() / 2;
    }

    private boolean hasMoreLeftCells() {
      return minX > -halfWidth && baseIndex - previousCount - 1 >= 0;
    }

    private boolean hasMoreRightCells() {
      return maxX < halfWidth && baseIndex + nextCount <= (getSkinnable().getExpandedItemCount() - 1);
    }

    @Override
    protected int nextIndex() {
      boolean hasMoreLeftCells = hasMoreLeftCells();
      boolean hasMoreRightCells = hasMoreRightCells();

      if(!hasMoreLeftCells && !hasMoreRightCells) {
        return -1;
      }
      if((hasMoreLeftCells() && previousCount < nextCount) || !hasMoreRightCells()) {
        return baseIndex - previousCount++ - 1;
      }

      return baseIndex + nextCount++;
    }

    @Override
    protected void customizeLayoutItem() {
      Rectangle2D cellRectangle = currentItem().getCellRectangle(0.5);
      double index = currentItem().getRelativeFractionalIndex();
      double spacing = 10;  // TODO hard-coded currently...
      double offset;

      if(minX == Double.MAX_VALUE && maxX == Double.MIN_VALUE) {
        offset = cellRectangle.getWidth() * index;  // this isn't perfect, scroll speed will depend on width of the center cell
      }
      else {
        offset = index < 0 ? -(maxX + cellRectangle.getWidth() / 2 + spacing)
                           : -(minX - cellRectangle.getWidth() / 2 - spacing);
      }

      currentItem().setTranslation(offset, 0);

      customizeCell(this);

      PerspectiveTransform perspectiveTransform = currentItem().getPerspectiveTransform();

      minX = Math.min(minX, perspectiveTransform.getUlx());
      maxX = Math.max(maxX, perspectiveTransform.getUrx());
    }

    @Override
    public RibbonLayoutItem addLayoutItem(int index) {
      return addVisibleCell(index, c -> new RibbonLayoutItem(c));
    }
  }

  public class RibbonLayoutItem extends AbstractCarouselSkin<T>.AbstractLayoutItem { // TODO same as FlatLayoutItem... merge somehow.

    public RibbonLayoutItem(IndexedCell<?> cell) {
      super(cell);
    }

    @Override
    protected PerspectiveTransform createPerspectiveTransform() {
      Rectangle2D cellRectangle = getCellRectangle(0.5);
      double translateX = getTranslateX();
      double translateY = getTranslateY();

      PerspectiveTransform perspectiveTransform = new PerspectiveTransform(
        cellRectangle.getMinX() - translateX, cellRectangle.getMinY() - translateY,
        cellRectangle.getMaxX() - translateX, cellRectangle.getMinY() - translateY,
        cellRectangle.getMaxX() - translateX, cellRectangle.getMaxY() - translateY,
        cellRectangle.getMinX() - translateX, cellRectangle.getMaxY() - translateY
      );

      return perspectiveTransform;
    }
  }
}
