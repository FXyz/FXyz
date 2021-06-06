/*
 * Copyright (c) 2021, F(X)yz
 * Copyright (c) 2012 Yonatan Graber
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.fxyz3d.shapes.primitives.helper.delaunay.jdt;

import java.util.Iterator;

/**
 * @author Yonatan Graber https://github.com/yonatang/JDT
 *
 * Created by IntelliJ IDEA. User: Aviad Segev Date: 22/11/2009 Time: 20:10:04
 * 
 * Grid Index is a simple spatial index for fast point/triangle location. The
 * idea is to divide a predefined geographic extent into equal sized cell matrix
 * (tiles). Every cell will be associated with a triangle which lies inside.
 * Therfore, one can easily locate a triangle in close proximity of the required
 * point by searching from the point's cell triangle. If the triangulation is
 * more or less uniform and bound in space, this index is very effective,
 * roughly recuing the searched triangles by square(xCellCount * yCellCount), as
 * only the triangles inside the cell are searched.
 * 
 * The index takes xCellCount * yCellCount capacity. While more cells allow
 * faster searches, even a small grid is helpfull.
 * 
 * This implementation holds the cells in a memory matrix, but such a grid can
 * be easily mapped to a DB table or file where it is usually used for it's
 * fullest.
 * 
 * Note that the index is geographically bound - only the region given in the
 * c'tor is indexed. Added Triangles outside the indexed region will cause
 * rebuilding of the whole index. Since triangulation is mostly always used for
 * static raster data, and usually is never updated outside the initial zone
 * (only refininf existing triangles) this is never an issue in real life.
 */
public class GridIndex {
	/**
	 * The triangulation of the index
	 */
	private DelaunayTriangulation indexDelaunay;

	/**
	 * Horizontal geographic size of a cell index
	 */
	private double xSize;

	/**
	 * Vertical geographic size of a cell inedx
	 */
	private double ySize;

	/**
	 * The indexed geographic size
	 */
	private BoundingBox indexRegion;

	/**
	 * A division of indexRegion to a cell matrix, where each cell holds a
	 * triangle which lies in it
	 */
	private Triangle[][] grid;

	/**
	 * Constructs a grid index holding the triangles of a delaunay
	 * triangulation. This version uses the bounding box of the triangulation as
	 * the region to index.
	 * 
	 * @param delaunay
	 *            delaunay triangulation to index
	 * @param xCellCount
	 *            number of grid cells in a row
	 * @param yCellCount
	 *            number of grid cells in a column
	 */
	public GridIndex(DelaunayTriangulation delaunay, int xCellCount, int yCellCount) {
		this(delaunay, xCellCount, yCellCount, delaunay.getBoundingBox());
	}

	/**
	 * Constructs a grid index holding the triangles of a delaunay
	 * triangulation. The grid will be made of (xCellCount * yCellCount) cells.
	 * The smaller the cells the less triangles that fall in them, whuch means
	 * better indexing, but also more cells in the index, which mean more
	 * storage. The smaller the indexed region is, the smaller the cells can be
	 * and still maintain the same capacity, but adding geometries outside the
	 * initial region will invalidate the index !
	 * 
	 * @param delaunay
	 *            delaunay triangulation to index
	 * @param xCellCount
	 *            number of grid cells in a row
	 * @param yCellCount
	 *            number of grid cells in a column
	 * @param region
	 *            geographic region to index
	 */
	public GridIndex(DelaunayTriangulation delaunay, int xCellCount, int yCellCount, BoundingBox region) {
		init(delaunay, xCellCount, yCellCount, region);
	}

	/**
	 * Initialize the grid index
	 * 
	 * @param delaunay
	 *            delaunay triangulation to index
	 * @param xCellCount
	 *            number of grid cells in a row
	 * @param yCellCount
	 *            number of grid cells in a column
	 * @param region
	 *            geographic region to index
	 */
	private void init(DelaunayTriangulation delaunay, int xCellCount, int yCellCount, BoundingBox region) {
		indexDelaunay = delaunay;
		indexRegion = region;
		xSize = region.getWidth() / yCellCount;
		ySize = region.getHeight() / xCellCount;

		// The grid will hold a trinagle for each cell, so a point (x,y) will
		// lie
		// in the cell representing the grid partition of region to a
		// xCellCount on yCellCount grid
		grid = new Triangle[xCellCount][yCellCount];

		Triangle colStartTriangle = indexDelaunay.find(middleOfCell(0, 0));
		updateCellValues(0, 0, xCellCount - 1, yCellCount - 1, colStartTriangle);
	}

	/**
	 * Finds a triangle near the given point
	 * 
	 * @param point
	 *            a query point
	 * @return a triangle at the same cell of the point
	 */
	public Triangle findCellTriangleOf(Point point) {
		int xIndex = (int) ((point.getX() - indexRegion.minX()) / xSize);
		int yIndex = (int) ((point.getY() - indexRegion.minY()) / ySize);
		return grid[xIndex][yIndex];
	}

	/**
	 * Updates the grid index to reflect changes to the triangulation. Note that
	 * added triangles outside the indexed region will force to recompute the
	 * whole index with the enlarged region.
	 */
	/**
	 * Updates the grid index to reflect changes to the triangulation. Note that
	 * added triangles outside the indexed region will force to recompute the
	 * whole index with the enlarged region.
	 * 
	 * @param updatedTriangles
	 *            changed triangles of the triangulation. This may be added
	 *            triangles, removed triangles or both. All that matter is that
	 *            they cover the changed area.
	 */
	public void updateIndex(Iterator<Triangle> updatedTriangles) {

		// Gather the bounding box of the updated area
		BoundingBox updatedRegion = new BoundingBox();

		while (updatedTriangles.hasNext()) {
			updatedRegion = updatedRegion.unionWith(updatedTriangles.next().getBoundingBox());
		}

		if (updatedRegion.isNull()) // No update...
			return;

		// Bad news - the updated region lies outside the indexed region.
		// The whole index must be recalculated
		if (!indexRegion.contains(updatedRegion)) {
			init(indexDelaunay, (int) (indexRegion.getWidth() / xSize), (int) (indexRegion.getHeight() / ySize),
					indexRegion.unionWith(updatedRegion));
		} else {
			// Find the cell region to be updated
			PointInt minInvalidCell = getCellOf(updatedRegion.getMinPoint());
			PointInt maxInvalidCell = getCellOf(updatedRegion.getMaxPoint());

			// And update it with fresh triangles
			Triangle adjacentValidTriangle = findValidTriangle(minInvalidCell);
			updateCellValues(minInvalidCell.x, minInvalidCell.y, maxInvalidCell.x, maxInvalidCell.y,
					adjacentValidTriangle);
		}
	}

	private class PointInt {
		private int x;
		private int y;

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public PointInt(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	private void updateCellValues(int startXCell, int startYCell, int lastXCell, int lastYCell, Triangle startTriangle) {
		// Go over each grid cell and locate a triangle in it to be the cell's
		// starting search triangle. Since we only pass between adjacent cells
		// we can search from the last triangle found and not from the start.

		// Add triangles for each column cells
		for (int i = startXCell; i <= lastXCell; i++) {
			// Find a triangle at the begining of the current column
			startTriangle = indexDelaunay.find(middleOfCell(i, startYCell), startTriangle);
			grid[i][startYCell] = startTriangle;
			Triangle prevRowTriangle = startTriangle;

			// Add triangles for the next row cells
			for (int j = startYCell + 1; j <= lastYCell; j++) {
				grid[i][j] = indexDelaunay.find(middleOfCell(i, j), prevRowTriangle);
				prevRowTriangle = grid[i][j];
			}
		}
	}

	/**
	 * Finds a valid (existing) trinagle adjacent to a given invalid cell
	 * 
	 * @param minInvalidCell
	 *            minimum bounding box invalid cell
	 * @return a valid triangle adjacent to the invalid cell
	 */
	private Triangle findValidTriangle(PointInt minInvalidCell) {
		// If the invalid cell is the minimal one in the grid we are forced to
		// search the
		// triangulation for a trinagle at that location
		if (minInvalidCell.x == 0 && minInvalidCell.y == 0)
			return indexDelaunay.find(middleOfCell(minInvalidCell.getX(), minInvalidCell.getY()), null);
		else
			// Otherwise we can take an adjacent cell triangle that is still
			// valid
			return grid[Math.min(0, minInvalidCell.getX())][Math.min(0, minInvalidCell.getY())];
	}

	/**
	 * Locates the grid cell point covering the given coordinate
	 * 
	 * @param coordinate
	 *            world coordinate to locate
	 * @return cell covering the coordinate
	 */
	private PointInt getCellOf(Point coordinate) {
		int xCell = (int) ((coordinate.getX() - indexRegion.minX()) / xSize);
		int yCell = (int) ((coordinate.getY() - indexRegion.minY()) / ySize);
		return new PointInt(xCell, yCell);
	}

	/**
	 * Create a point at the center of a cell
	 * 
	 * @param xIndex
	 *            horizontal cell index
	 * @param yIndex
	 *            vertical cell index
	 * @return Point at the center of the cell at (xIndex, yIndex)
	 */
	private Point middleOfCell(int xIndex, int yIndex) {
		double middleXCell = indexRegion.minX() + xIndex * xSize + xSize / 2;
		double middleYCell = indexRegion.minY() + yIndex * ySize + ySize / 2;
		return new Point(middleXCell, middleYCell);
	}
}
