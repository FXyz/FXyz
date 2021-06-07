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
 * This class represents a 3D point, with some simple geometric methods
 * (pointLineTest).
 */
@SuppressWarnings("serial")
public class Point implements Comparable<Point>, Serializable {
	private double x, y, z;

	/**
	 * Default Constructor. <br />
	 * constructs a 3D point at (0,0,0).
	 */
	public Point() {
		this(0, 0);
	}

	/**
	 * constructs a 3D point
	 */
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/** constructs a 3D point with a z value of 0. */
	public Point(double x, double y) {
		this(x, y, 0);
	}

	/** simple copy constructor */
	public Point(Point p) {
		x = p.x;
		y = p.y;
		z = p.z;
	}

	/** returns the x-coordinate of this point. */
	public double getX() {
		return x;
	};

	/**
	 * Sets the x coordinate.
	 * 
	 * @param x
	 *            The new x coordinate.
	 */
	public void setX(double x) {
		this.x = x;
	}

	/** returns the y-coordinate of this point. */
	public double getY() {
		return y;
	};

	/**
	 * Sets the y coordinate.
	 * 
	 * @param y
	 *            The new y coordinate.
	 */
	public void setY(double y) {
		this.y = y;
	}

	/** returns the z-coordinate of this point. */
	public double getZ() {
		return z;
	};

	/**
	 * Sets the z coordinate.
	 * 
	 * @param z
	 *            The new z coordinate.
	 */
	public void setZ(double z) {
		this.z = z;
	}

	double distance2(Point p) {
		return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
	}

	double distance2(double px, double py) {
		return (px - x) * (px - x) + (py - y) * (py - y);
	}

	boolean isLess(Point p) {
		return compareTo(p) < 0;
	}

	boolean isGreater(Point p) {
		return compareTo(p) > 0;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	/** @return the L2 distanse NOTE: 2D only!!! */
	public double distance(Point p) {
		double temp = Math.pow(p.getX() - x, 2) + Math.pow(p.getY() - y, 2);
		return Math.sqrt(temp);
	}

	/** @return the L2 distanse NOTE: 3D only!!! */
	public double distance3D(Point p) {
		double temp = Math.pow(p.getX() - x, 2) + Math.pow(p.getY() - y, 2) + Math.pow(p.getZ() - z, 2);
		return Math.sqrt(temp);
	}

	// pointLineTest
	// ===============
	// simple geometry to make things easy!
	public final static int ONSEGMENT = 0;
	public final static int LEFT = 1;
	public final static int RIGHT = 2;
	public final static int INFRONTOFA = 3;
	public final static int BEHINDB = 4;
	public final static int ERROR = 5;

	/**
	 * tests the relation between this point (as a 2D [x,y] point) and a 2D
	 * segment a,b (the Z values are ignored), returns one of the following:
	 * LEFT, RIGHT, INFRONTOFA, BEHINDB, ONSEGMENT
	 * 
	 * @param a
	 *            the first point of the segment.
	 * @param b
	 *            the second point of the segment.
	 * @return the value (flag) of the relation between this point and the a,b
	 *         line-segment.
	 */
	public int pointLineTest(Point a, Point b) {

		double dx = b.x - a.x;
		double dy = b.y - a.y;
		double res = dy * (x - a.x) - dx * (y - a.y);

		if (res < 0)
			return LEFT;
		if (res > 0)
			return RIGHT;

		if (dx > 0) {
			if (x < a.x)
				return INFRONTOFA;
			if (b.x < x)
				return BEHINDB;
			return ONSEGMENT;
		}
		if (dx < 0) {
			if (x > a.x)
				return INFRONTOFA;
			if (b.x > x)
				return BEHINDB;
			return ONSEGMENT;
		}
		if (dy > 0) {
			if (y < a.y)
				return INFRONTOFA;
			if (b.y < y)
				return BEHINDB;
			return ONSEGMENT;
		}
		if (dy < 0) {
			if (y > a.y)
				return INFRONTOFA;
			if (b.y > y)
				return BEHINDB;
			return ONSEGMENT;
		}
		System.out.println("Error, pointLineTest with a=b");
		return ERROR;
	}

	boolean areCollinear(Point a, Point b) {
		double dx = b.x - a.x;
		double dy = b.y - a.y;
		double res = dy * (x - a.x) - dx * (y - a.y);
		return res == 0;
	}

	/*
	 * public ajSegment Bisector( ajPoint b) { double sx = (x+b.x)/2; double sy
	 * = (y+b.y)/2; double dx = b.x-x; double dy = b.y-y; ajPoint p1 = new
	 * ajPoint(sx-dy,sy+dx); ajPoint p2 = new ajPoint(sx+dy,sy-dx); return new
	 * ajSegment( p1,p2 ); }
	 */

	Point circumcenter(Point a, Point b) {
		double u = ((a.x - b.x) * (a.x + b.x) + (a.y - b.y) * (a.y + b.y)) / 2.0f;
		double v = ((b.x - x) * (b.x + x) + (b.y - y) * (b.y + y)) / 2.0f;
		double den = (a.x - b.x) * (b.y - y) - (b.x - x) * (a.y - b.y);
		if (den == 0) // oops
			System.out.println("circumcenter, degenerate case");
		return new Point((u * (b.y - y) - v * (a.y - b.y)) / den, (v * (a.x - b.x) - u * (b.x - x)) / den);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * return true iff this point [x,y] coordinates are the same as p [x,y]
	 * coordinates. (the z value is ignored).
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public int compareTo(Point o) {
		if (o == null)
			return 1;

		Point d1 = this;
		Point d2 = o;
		if (d1.getX() > d2.getX())
			return 1;
		if (d1.getX() < d2.getX())
			return -1;
		// x1 == x2
		if (d1.getY() > d2.getY())
			return 1;
		if (d1.getY() < d2.getY())
			return -1;
		// y1==y2
		return 0;
	}

}