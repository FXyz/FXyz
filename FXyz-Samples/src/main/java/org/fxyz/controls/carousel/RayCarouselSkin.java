package org.fxyz.controls.carousel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.effect.PerspectiveTransform;

public class RayCarouselSkin<T> extends AbstractCarouselSkin<T> {
  private final DoubleProperty radiusRatio = new SimpleDoubleProperty(1.0);
  public final DoubleProperty radiusRatioProperty() { return radiusRatio; }
  public final double getRadiusRatio() { return radiusRatio.get(); }

  private final DoubleProperty viewDistanceRatio = new SimpleDoubleProperty(2.0);
  public final DoubleProperty viewDistanceRatioProperty() { return viewDistanceRatio; }
  public final double getViewDistanceRatio() { return viewDistanceRatio.get(); }

  private final DoubleProperty viewAlignment = new SimpleDoubleProperty(0.5);
  public final DoubleProperty viewAlignmentProperty() { return viewAlignment; }
  public final double getViewAlignment() { return viewAlignment.get(); }

  private final DoubleProperty carouselViewFraction = new SimpleDoubleProperty(0.25);
  public final DoubleProperty carouselViewFractionProperty() { return carouselViewFraction; }
  public final double getCarouselViewFraction() { return carouselViewFraction.get(); }

  public RayCarouselSkin(TreeView<T> carousel) {
    super(carousel);

    radiusRatioProperty().addListener(requestLayout);
    viewDistanceRatioProperty().addListener(requestLayout);
    viewAlignmentProperty().addListener(requestLayout);
    carouselViewFractionProperty().addListener(requestLayout);
  }

  @Override
  public void dispose() {
    radiusRatioProperty().removeListener(requestLayout);
    viewDistanceRatioProperty().removeListener(requestLayout);
    viewAlignmentProperty().removeListener(requestLayout);
    carouselViewFractionProperty().removeListener(requestLayout);

    super.dispose();
  }

  @Override
  protected void delegateLayout(double fractionalIndex) {
    new RayLayoutPass(fractionalIndex).createLayout();
  }

  protected void customizeCell(RayLayoutPass layoutPass) {
    fadeOutEdgeCells(layoutPass, 0.5);
  }

  /**
   * Called by the CellIterator for each cell to allow customization of the
   * current cell.<p>
   *
   * The customization options are CellIterator specific.  To customize
   * a cell, examine the CellIterator's state and apply adjustments to the cell itself
   * or any of the intermediate values being calculated (if provided).<p>
   *
   * For RayLayout, this call is made before the CellIterator has calculated
   * the 3D coordinates of the cell, allowing customization of the angle on the
   * carousel.<p>
   *
   * @param item a layout item
   */
  protected void preLayoutCustomizeCell(RayLayoutItem item) {
  }

  /**
   * Called by the CellIterator for each cell to allow broad customization of the
   * current cell.<p>
   *
   * The customization options are CellIterator specific.  To customize
   * a cell, examine the CellIterator's state and apply adjustments to the cell itself
   * or any of the intermediate values being calculated (if provided).<p>
   *
   * For RayLayout, this call is made after the CellIterator has calculated
   * the 3D coordinates of the cell, allowing further transformation of these
   * coordinates.<p>
   *
     * @param item
   */
  protected void postLayoutCustomizeCell(RayLayoutItem item) {
    presentCenterCellsByAngle(item, 0.25 * Math.PI);
  }

  @SuppressWarnings("static-method")
  protected void fadeOutEdgeCells(RayLayoutPass layoutPass, double fadeOutCellCount) {
    double opacity = CellEffects.calculateEdgeCellOpacity(layoutPass.currentItem().getRelativeFractionalIndex(), layoutPass.getCellCount(), fadeOutCellCount);
    double depth = TreeView.getNodeLevel(((TreeCell<?>)layoutPass.currentItem().getCell()).getTreeItem()) - layoutPass.depth;

    opacity /= Math.pow(1.6, Math.abs(depth));

    layoutPass.currentItem().getCell().setOpacity(opacity);
  }

  /**
   * Presents <code>cellCount</code> cells to the Viewer by rotating them when they get close to
   * the center point of the Carousel.
   *
     * @param item
   * @param cellCount number of cells to present to Viewer
   */
  @SuppressWarnings("static-method")
  protected void presentCenterCells(RayLayoutItem item, double cellCount) {
    Point3D[] points = item.getCoordinates();
    double index = item.getRelativeFractionalIndex();

    if(index < cellCount) {
      double rotationAngle = index > -cellCount ? 0.5 * Math.PI * -index / cellCount + 0.5 * Math.PI : Math.PI;

      Point3D center = new Point3D((points[0].getX() + points[1].getX()) * 0.5, 0, (points[0].getZ() + points[1].getZ()) * 0.5);

      for(int i = 0; i < points.length; i++) {
        points[i] = rotateY(points[i], center, rotationAngle);
      }
    }
  }

  /**
   * Presents the cells to the Viewer by rotating them when they get closer than <code>angle</code> distance
   * to the center point of the Carousel.
   *
     * @param item
   * @param angle radians from center point of carousel within which cells are presented to Viewer
   */
  @SuppressWarnings("static-method")
  protected void presentCenterCellsByAngle(RayLayoutItem item, double angle) {
    Point3D[] points = item.getCoordinates();
    double angleOnCarousel = item.getAngleOnCarousel();

    if(angleOnCarousel < angle) {
      double rotationAngle = angleOnCarousel > -angle ? 0.5 * Math.PI * -angleOnCarousel / angle + 0.5 * Math.PI : Math.PI;

      Point3D center = new Point3D((points[0].getX() + points[1].getX()) * 0.5, 0, (points[0].getZ() + points[1].getZ()) * 0.5);

      for(int i = 0; i < points.length; i++) {
        points[i] = rotateY(points[i], center, rotationAngle);
      }
    }
  }

  protected static Point3D rotateY(Point3D p, Point3D center, double radians) {
    Point3D input = new Point3D(p.getX() - center.getX(), p.getY() - center.getY(), p.getZ() - center.getZ());

    return new Point3D(
      input.getZ() * Math.sin(radians) + input.getX() * Math.cos(radians) + center.getX(),
      input.getY() + center.getY(),
      input.getZ() * Math.cos(radians) - input.getX() * Math.sin(radians) + center.getZ()
    );
  }

  protected static Point3D translateZ(Point3D p, double distance) {
    return new Point3D(
      p.getX(),
      p.getY(),
      p.getZ() + distance
    );
  }

  private static Point2D project(Point3D p, double viewDistance, double fov, double horizonY) {
    return new Point2D(p.getX() * fov / (p.getZ() + viewDistance), p.getY() * fov / (p.getZ() + viewDistance) + horizonY);
  }

  public class RayLayoutPass extends AbstractLayoutPass<RayLayoutItem> {
    private final int baseIndex;
    private final int minimumIndex;
    private final int maximumIndex;
    private final double fractionalCellCount;
    private final int cellCount;
    private final double depth;

    private int nextCount;
    private int previousCount;

    public RayLayoutPass(double fractionalIndex) {
      super(fractionalIndex);

      depth = determineDepthCorrection(getSkinnable().getFocusModel().getFocusedIndex() - fractionalIndex);

      int centerIndex = getSkinnable().getFocusModel().getFocusedIndex() - (int)Math.round(fractionalIndex);

      this.baseIndex = centerIndex == -1 ? 0 : centerIndex;

      double fractional = getSkinnable().getWidth() / getMaxCellWidth() * getRadiusRatio() * getCarouselViewFraction() * getDensity() * Math.PI * 2;
      this.fractionalCellCount = fractional < 3 ? 3 : fractional;

      int count = (int)fractionalCellCount;

      this.cellCount = count % 2 == 0 ? count - 1 : count;  // always uneven
      this.minimumIndex = Math.max(0, centerIndex - cellCount / 2);
      this.maximumIndex = Math.min(getSkinnable().getExpandedItemCount() - 1, centerIndex + cellCount / 2);
    }

    private double determineDepthCorrection(double fractionalIndex) {
      int leftIndex = (int)fractionalIndex;
      int rightIndex = (int)(fractionalIndex + 1);
      double fraction = fractionalIndex - leftIndex;

      double depthLeft = TreeView.getNodeLevel(getSkinnable().getTreeItem(leftIndex));
      double depthRight = TreeView.getNodeLevel(getSkinnable().getTreeItem(rightIndex));

      return depthLeft * (1.0 - fraction) + depthRight * fraction;
    }

    public int getCellCount() {
      return cellCount;
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

    protected double calculateAngleOnCarousel() {
      return 2 * Math.PI * getCarouselViewFraction() / fractionalCellCount * currentItem().getRelativeFractionalIndex();
    }

    protected double getCarouselRadius() {
      return getSkinnable().getWidth() * getRadiusRatio();
    }

    @Override
    public RayLayoutItem addLayoutItem(int index) {
      return addVisibleCell(index, c -> new RayLayoutItem(c));
    }

    @Override
    protected void customizeLayoutItem() {
      currentItem().setAngleOnCarousel(calculateAngleOnCarousel());
      currentItem().setCarouselDepth(depth);
      customizeCell(this);
    }
  }

  public class RayLayoutItem extends AbstractCarouselSkin<T>.AbstractLayoutItem {

    /*
     * Input fields:
     */

    private double angleOnCarousel;
    private double carouselDepth;

    /*
     * Derived fields:
     */

    private Point3D[] coordinates;

    public RayLayoutItem(IndexedCell<?> cell) {
      super(cell);
    }

    public Point3D[] getCoordinates() {
      return coordinates;
    }

    public void setCoordinates(Point3D[] coordinates) {
      this.coordinates = coordinates;
    }

    /**
     * The angle in radians where this item is located on the Carousel.  The center item is
     * at 0 radians, items to the left and right are respectively in the ranges 0 to PI and
     * 0 to -PI.
     *
     * @return the angle in radians where this item is located on the Carousel
     */
    public double getAngleOnCarousel() {
      return angleOnCarousel;
    }

    public void setAngleOnCarousel(double angleOnCarousel) {
      this.angleOnCarousel = angleOnCarousel;
    }
    
    public double getCarouselDepth() {
      return carouselDepth;
    }

    public void setCarouselDepth(double carouselDepth) {
      this.carouselDepth = carouselDepth;
    }

    @Override
    protected PerspectiveTransform createPerspectiveTransform() {

      /*
       * Calculate the angle on the carousel the cell is located at based on its
       * index.
       */

      preLayoutCustomizeCell(this);

      /*
       * Calculate where the cell bounds are in 3D space based on its angle on the
       * carousel.
       */

      setCoordinates(calculateCarouselCoordinates());

      /*
       * Apply additional transformations to the cell's 3D coordinates based on its index.
       */

      postLayoutCustomizeCell(this);

      /*
       * Project the final position to 2D space.
       */

      Point2D[] projectedPoints = project(getCoordinates());

      /*
       * Create the PerspectiveTransform and set it on the cell.
       */

      IndexedCell<?> cell = getCell();

      return new PerspectiveTransform(
        projectedPoints[0].getX() / cell.getScaleX(), projectedPoints[0].getY() / cell.getScaleY(),
        projectedPoints[1].getX() / cell.getScaleX(), projectedPoints[1].getY() / cell.getScaleY(),
        projectedPoints[2].getX() / cell.getScaleX(), projectedPoints[2].getY() / cell.getScaleY(),
        projectedPoints[3].getX() / cell.getScaleX(), projectedPoints[3].getY() / cell.getScaleY()
      );
    }

    protected Point3D[] calculateCarouselCoordinates() {
      Rectangle2D cellRectangle = getCellRectangle(getViewAlignment());
      double sin = -Math.sin(angleOnCarousel);
      double cos = -Math.cos(angleOnCarousel);

      double depth = TreeView.getNodeLevel(((TreeCell<?>)getCell()).getTreeItem());
      double radius = getCarouselRadius() / Math.pow(1.3, depth - this.carouselDepth);

      double l = radius - cellRectangle.getMinX();
      double r = l - cellRectangle.getWidth();

      double lx = l * sin;
      double rx = r * sin;
      double ty = cellRectangle.getMinY();
      double by = ty + cellRectangle.getHeight();
      double lz = l * cos;
      double rz = r * cos;

      return new Point3D[] {new Point3D(lx, ty, lz), new Point3D(rx, ty, rz), new Point3D(rx, by, rz), new Point3D(lx, by, lz)};
    }

    protected Point2D[] project(Point3D[] points) {
      double carouselRadius = getCarouselRadius();
      double viewDistance = getViewDistanceRatio() * carouselRadius;
      double fov = viewDistance - carouselRadius;
      double horizonY = getMaxCellHeight() * (getViewAlignment() - 0.5);

      Point2D[] projectedPoints = new Point2D[points.length];

      for(int i = 0; i < points.length; i++) {
        projectedPoints[i] = RayCarouselSkin.project(points[i], viewDistance, fov, horizonY);
      }

      return projectedPoints;
    }

    private double getCarouselRadius() {
      return getSkinnable().getWidth() * getRadiusRatio();
    }
  }
}
