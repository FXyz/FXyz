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

import java.io.Serializable;

/**
 * @author Yonatan Graber https://github.com/yonatang/JDT
 *
 * Created by IntelliJ IDEA. User: Aviad Segev Date: 22/11/2009 Time: 20:29:56
 * BoundingBox represents a horizontal bounding rectangle defined by its lower
 * left and upper right point. This is usually used as a rough approximation
 * of@SuppressWarnings("serial")
 * 
 * the bounded geometry
 */
@SuppressWarnings("serial")
public class BoundingBox implements Serializable {
	/**
	 * the minimum x-coordinate
	 */
	private double minX;

	/**
	 * the maximum x-coordinate
	 */
	private double maxX;

	/**
	 * the minimum y-coordinate
	 */
	private double minY;

	/**
	 * the maximum y-coordinate
	 */
	private double maxY;

	/**
	 * the minimum z-coordinate
	 */
	private double minZ;

	/**
	 * the maximum z-coordinate
	 */
	private double maxZ;

	/**
	 * Creates an empty bounding box
	 */
	public BoundingBox() {
		setToNull();
	}

	/**
	 * Copy constructor
	 * 
	 * @param other
	 *            the copied bounding box
	 */
	public BoundingBox(BoundingBox other) {
		if (other.isNull())
			setToNull();
		else
			init(other.minX, other.maxX, other.minY, other.maxY, other.minZ, other.maxZ);
	}

	/**
	 * Creates a bounding box given the extent
	 * 
	 * @param minx
	 *            minimum x coordinate
	 * @param maxx
	 *            maximum x coordinate
	 * @param miny
	 *            minimum y coordinate
	 * @param maxy
	 *            maximum y coordinate
	 */
	public BoundingBox(double minx, double maxx, double miny, double maxy, double minz, double maxz) {
		init(minx, maxx, miny, maxy, minz, maxz);
	}

	/**
	 * Create a bounding box between lowerLeft and upperRight
	 * 
	 * @param lowerLeft
	 *            lower left point of the box
	 * @param upperRight
	 *            upper left point of the box
	 */
	public BoundingBox(Point lowerLeft, Point upperRight) {
		init(lowerLeft.getX(), upperRight.getX(), lowerLeft.getY(), upperRight.getY(), lowerLeft.getZ(),
				upperRight.getZ());
	}

	/**
	 * Initialize a BoundingBox for a region defined by maximum and minimum
	 * values.
	 * 
	 * @param x1
	 *            the first x-value
	 * @param x2
	 *            the second x-value
	 * @param y1
	 *            the first y-value
	 * @param y2
	 *            the second y-value
	 */
	private void init(double x1, double x2, double y1, double y2, double z1, double z2) {
		if (x1 < x2) {
			minX = x1;
			maxX = x2;
		} else {
			minX = x2;
			maxX = x1;
		}
		if (y1 < y2) {
			minY = y1;
			maxY = y2;
		} else {
			minY = y2;
			maxY = y1;
		}
		if (z1 < z2) {
			minZ = z1;
			maxZ = z2;
		} else {
			minZ = z2;
			maxZ = z1;
		}
	}

	/**
	 * Makes this BoundingBox a "null" envelope, that is, the envelope of the
	 * empty geometry.
	 */
	private void setToNull() {
		minX = 0;
		maxX = -1;
		minY = 0;
		maxY = -1;
	}

	/**
	 * Returns true if this BoundingBox is a "null" envelope.
	 * 
	 * @return true if this BoundingBox is uninitialized or is the envelope of
	 *         the empty geometry.
	 */
	public boolean isNull() {
		return maxX < minX;
	}

	/**
	 * Tests if the other BoundingBox lies wholely inside this BoundingBox
	 * 
	 * @param other
	 *            the BoundingBox to check
	 * @return true if this BoundingBox contains the other BoundingBox
	 */
	public boolean contains(BoundingBox other) {
		return !(isNull() || other.isNull()) && other.minX >= minX && other.maxY <= maxX && other.minY >= minY
				&& other.maxY <= maxY;
	}

	/**
	 * Unify the BoundingBoxes of this and the other BoundingBox
	 * 
	 * @param other
	 *            another BoundingBox
	 * @return The union of the two BoundingBoxes
	 */
	public BoundingBox unionWith(BoundingBox other) {
		if (other.isNull()) {
			return new BoundingBox(this);
		}
		if (isNull()) {
			return new BoundingBox(other);
		} else {
			return new BoundingBox(Math.min(minX, other.minX), Math.max(maxX, other.maxX), Math.min(minY, other.minY),
					Math.max(maxY, other.maxY), Math.min(minZ, other.minZ), Math.max(maxZ, other.maxZ));
		}
	}

	/**
	 * @return Minimum x value
	 */
	public double minX() {
		return minX;
	}

	/**
	 * @return Minimum y value
	 */
	public double minY() {
		return minY;
	}

	/**
	 * @return Maximum x value
	 */
	public double maxX() {
		return maxX;
	}

	/**
	 * @return Maximum y value
	 */
	public double maxY() {
		return maxY;
	}

	/**
	 * @return Width of the bounding box
	 */
	public double getWidth() {
		return maxX - minX;
	}

	/**
	 * @return Height of the bounding box
	 */
	public double getHeight() {
		return maxY - minY;
	}

	@Override
	public String toString() {
		return "BoundingBox [minX=" + minX + ", maxX=" + maxX + ", minY=" + minY + ", maxY=" + maxY + ", minZ=" + minZ
				+ ", maxZ=" + maxZ + "]";
	}

	/**
	 * @return Maximum coordinate of bounding box
	 */
	public Point getMinPoint() {
		return new Point(minX, minY, minZ);
	}

	/**
	 * @return Minimum coordinate of bounding box
	 */
	public Point getMaxPoint() {
		return new Point(maxX, maxY, maxZ);
	}
}