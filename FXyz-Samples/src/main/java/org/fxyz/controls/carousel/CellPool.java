package org.fxyz.controls.carousel;

import javafx.scene.control.IndexedCell;

/**
 * A cell pool keeps track of a set of cell instances.  The pool
 * automatically expands to meet demand.<p>
 *
 * Getting cells from a pool marks those cells as active (in use), and
 * they will remain active until the pool is reset.  When getting a
 * new cell, the pool will attempt to reuse inactive cells where
 * possible or expand the pool with new cells when needed.<p>
 *
 * Resetting the pool makes all cells available for reuse.  Trimming
 * the pool removes all unused cells.<p>
 *
 * Typical usage for a cell based control is to reset the pool before
 * rendering starts, then get the required cells needed for rendering
 * (which marks them as active) and then trim the pool at the end of
 * the rendering phase, which removes all cells that weren't needed.<p>
 *
 * @param <C> the cell type
 */
public interface CellPool<C extends IndexedCell<?>> {

  /**
   * Gets a cell with the given index.  If unavailable, the cell is either
   * taken from the cells available for reuse or newly instantiated if no
   * cells were available.<p>
   *
   * The returned cell is part of the pool and must eventually be returned
   * by resetting the pool to prevent the pool from expanding too much.
   * Subsequent calls for a cell with the same index will return the same
   * cell instance.<p>
   *
   * @param index the index of the cell
   * @return a cell with the given index
   */
  C getCell(int index);

  /**
   * Resets the pool and makes all cells available for reuse.
   */
  void reset();

  /**
   * Discards any cells currently available for reuse.
   */
  void trim();
}
