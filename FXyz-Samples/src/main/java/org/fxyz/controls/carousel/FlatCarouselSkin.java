package org.fxyz.controls.carousel;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TreeView;
import javafx.scene.effect.PerspectiveTransform;

public class FlatCarouselSkin<T> extends AbstractCarouselSkin<T> {

  public FlatCarouselSkin(TreeView<T> carousel) {
    super(carousel);
  }

  @Override
  protected void delegateLayout(double fractionalIndex) {
    new FlatLayoutPass(fractionalIndex).createLayout();
  }

  /**
   * Provides subclasses with a method to customize each cell calculated during
   * a layout pass.
   *
   * @param layoutPass a layout pass
   */
  protected void customizeCell(FlatLayoutPass layoutPass) {
    fadeOutEdgeCells(layoutPass, 0.5);
  }

  @SuppressWarnings("static-method")
  protected void fadeOutEdgeCells(FlatLayoutPass layoutPass, double fadeOutCellCount) {
    layoutPass.currentItem().getCell().setOpacity(CellEffects.calculateEdgeCellOpacity(layoutPass.currentItem().getRelativeFractionalIndex(), layoutPass.cellCount, fadeOutCellCount));
  }

  public class FlatLayoutPass extends AbstractLayoutPass<FlatLayoutItem> {
    private final int baseIndex;
    private final int cellCount;
    private final int minimumIndex;
    private final int maximumIndex;
    private final double fractionalCellCount;

    private int nextCount;
    private int previousCount;

    public FlatLayoutPass(double fractionalIndex) {
      super(fractionalIndex);

      int centerIndex = getSkinnable().getFocusModel().getFocusedIndex() - (int)Math.round(fractionalIndex);
      this.baseIndex = centerIndex == -1 ? 0 : centerIndex;
      this.fractionalCellCount = calculateCellCount();

      int cellCount = (int)fractionalCellCount;

      if(cellCount % 2 == 0) {
        cellCount--;
      }

      this.cellCount = cellCount;
      this.minimumIndex = Math.max(0, centerIndex - (cellCount - 1) / 2);
      this.maximumIndex = Math.min(getSkinnable().getExpandedItemCount() - 1, centerIndex + cellCount / 2);
    }

    protected double calculateCellCount() {
      double count = getSkinnable().getWidth() / getMaxCellWidth() * getDensity();

      return count < 3 ? 3 : count;
    }

    private boolean hasMoreLeftCells() {
      return baseIndex - previousCount - 1 >= minimumIndex;
    }

    private boolean hasMoreRightCells() {
      return baseIndex + nextCount <= maximumIndex;
    }

    @Override
    protected int nextIndex() {
      boolean hasMoreLeftCells = hasMoreLeftCells();
      boolean hasMoreRightCells = hasMoreRightCells();

      if(!hasMoreLeftCells && !hasMoreRightCells) {
        return -1;
      }
      if((hasMoreLeftCells && previousCount < nextCount) || !hasMoreRightCells) {
        return baseIndex - previousCount++ - 1;
      }

      return baseIndex + nextCount++;
    }

    @Override
    protected void customizeLayoutItem() {
      double index = currentItem().getRelativeFractionalIndex();
      double offset = getSkinnable().getWidth() / fractionalCellCount * index;

      currentItem().setTranslation(offset, 0);
      customizeCell(this);
    }

    @Override
    public FlatLayoutItem addLayoutItem(int index) {
      return addVisibleCell(index, c -> new FlatLayoutItem(c));
    }
  }

  public class FlatLayoutItem extends AbstractCarouselSkin<T>.AbstractLayoutItem {

    public FlatLayoutItem(IndexedCell<?> cell) {
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
