package org.fxyz.controls.carousel;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import javafx.scene.control.IndexedCell;
import javafx.util.Callback;

public class SimpleCellPool<V, C extends IndexedCell<?>> implements CellPool<C> {
  private final NavigableMap<Integer, C> availableCells = new TreeMap<>();
  private final Map<Integer, C> activeCells = new HashMap<>();

  private final Callback<V, C> cellFactory;
  private final V view;

  public SimpleCellPool(V view, Callback<V, C> cellFactory) {
    this.view = view;
    this.cellFactory = cellFactory;
  }

  @Override
  public C getCell(int index) {
    C cell = activeCells.get(index);

    if(cell != null) {
      return cell;
    }

    cell = availableCells.remove(index);

    if(cell == null) {

      /*
       * Either create a new cell if there are no available cells, or reuse the cell
       * with the index that is furthest away from the requested index.
       */

      if(availableCells.isEmpty()) {
        cell = cellFactory.call(view);
      }
      else {
        int lastKey = availableCells.lastKey();
        int firstKey = availableCells.firstKey();

        cell = availableCells.remove(Math.abs(lastKey - index) > Math.abs(firstKey - index) ? lastKey : firstKey);
      }
    }

    // TODO This is required apparently when a TreeNode is opened and new Items appear at indices previously occupied by other items
//    if(cell.getIndex() != index) {
      cell.updateIndex(index);  // This used to be called always even when index is unchanged with the reason that another item might be at the index now... does it still apply? --> Did not cause any problems over a few weeks testing.
  //  }

    activeCells.put(index, cell);

    return cell;
  }

  @Override
  public void reset() {
    for(Map.Entry<Integer, C> entry : activeCells.entrySet()) {
      C duplicateCell = availableCells.put(entry.getKey(), entry.getValue());

      if(duplicateCell != null) {
        System.out.println(">>> Discarding duplicate cell, index = " + entry.getKey());
        duplicateCell.updateIndex(-1);  // discard cell
      }
    }

    activeCells.clear();
  }

  @Override
  public void trim() {
    for(C cell : availableCells.values()) {
      cell.updateIndex(-1);  // discard cell
    }

    availableCells.clear();
  }
}
