package org.fxyz.controls.carousel;

public class CellEffects {
  public static double calculateEdgeCellOpacity(double relativeFractionalIndex, int visibleCells, double fadeOutCellCount) {
    double absoluteIndex = Math.abs(relativeFractionalIndex);
    double fadeOutDistance = visibleCells / 2 - fadeOutCellCount + 0.5;

    return absoluteIndex > fadeOutDistance ? 1.0 - (absoluteIndex - fadeOutDistance) / fadeOutCellCount : 1.0;
  }
}
