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
 * This class performs a 3D triangulation for each point inserted or deleted
 * 
 */
@SuppressWarnings("serial")
public class Triangle implements Serializable {
	private Point a, b, c;
	private Triangle abTriangle, bcTriangle, caTriangle;
	private Circle circum;

	private int mc = 0;

	// true iff it is an infinite face.
	private boolean halfplane = false;

	// tag - for bfs algorithms
	private boolean mark = false;

	/**
	 * constructs a triangle form 3 point - store it in counterclockwised order.
	 */
	public Triangle(Point A, Point B, Point C) {
		a = A;
		int res = C.pointLineTest(A, B);
		if ((res <= Point.LEFT) || (res == Point.INFRONTOFA) || (res == Point.BEHINDB)) {
			b = B;
			c = C;
		} else { // RIGHT
			System.out.println("Warning, ajTriangle(A,B,C) " + "expects points in counterclockwise order.");
			System.out.println("" + A + B + C);
			b = C;
			c = B;
		}
		circumcircle();
	}

	/**
	 * creates a half plane using the segment (A,B).
	 * 
	 * @param A
	 * @param B
	 */
	public Triangle(Point A, Point B) {
		a = A;
		b = B;
		halfplane = true;
	}

	/**
	 * @return The bounding rectange between the minimum and maximum coordinates
	 *         of the triangle
	 */
	public BoundingBox getBoundingBox() {
		Point lowerLeft, upperRight;
		lowerLeft = new Point(Math.min(a.getX(), Math.min(b.getX(), c.getX())), Math.min(a.getY(), Math.min(b.getY(), c.getY())));
		upperRight = new Point(Math.max(a.getX(), Math.max(b.getX(), c.getX())), Math.max(a.getY(), Math.max(b.getY(), c.getY())));
		return new BoundingBox(lowerLeft, upperRight);
	}

	void switchneighbors(Triangle oldTriangle, Triangle newTriangle) {
		if (abTriangle == oldTriangle)
			abTriangle = newTriangle;
		else if (bcTriangle == oldTriangle)
			bcTriangle = newTriangle;
		else if (caTriangle == oldTriangle)
			caTriangle = newTriangle;
		else
			System.out.println("Error, switchneighbors can't find Old.");
	}

	Triangle neighbor(Point p) {
		if (a == p)
			return caTriangle;
		if (b == p)
			return abTriangle;
		if (c == p)
			return bcTriangle;
		System.out.println("Error, neighbors can't find p: " + p);
		return null;
	}

	/**
	 * Returns the neighbors that shares the given corner and is not the
	 * previous triangle.
	 * 
	 * @param p
	 *            The given corner
	 * @param prevTriangle
	 *            The previous triangle.
	 * @return The neighbors that shares the given corner and is not the
	 *         previous triangle.
	 * 
	 *         By: Eyal Roth & Doron Ganel.
	 */
	Triangle nextNeighbor(Point p, Triangle prevTriangle) {
		Triangle neighbor = null;

		if (a.equals(p)) {
			neighbor = caTriangle;
		}
		if (b.equals(p)) {
			neighbor = abTriangle;
		}
		if (c.equals(p)) {
			neighbor = bcTriangle;
		}

		// Udi Schneider: Added a condition check for isHalfPlane. If the
		// current
		// neighbor is a half plane, we also want to move to the next neighbor
		if (neighbor.equals(prevTriangle) || neighbor.isHalfplane()) {
			if (a.equals(p)) {
				neighbor = abTriangle;
			}
			if (b.equals(p)) {
				neighbor = bcTriangle;
			}
			if (c.equals(p)) {
				neighbor = caTriangle;
			}
		}

		return neighbor;
	}

	Circle circumcircle() {

		double u = ((a.getX() - b.getX()) * (a.getX() + b.getX()) + (a.getY() - b.getY()) * (a.getY() + b.getY())) / 2.0f;
		double v = ((b.getX() - c.getX()) * (b.getX() + c.getX()) + (b.getY() - c.getY()) * (b.getY() + c.getY())) / 2.0f;
		double den = (a.getX() - b.getX()) * (b.getY() - c.getY()) - (b.getX() - c.getX()) * (a.getY() - b.getY());
		if (den == 0) // oops, degenerate case
			circum = new Circle(a, Double.POSITIVE_INFINITY);
		else {
			Point cen = new Point((u * (b.getY() - c.getY()) - v * (a.getY() - b.getY())) / den, (v * (a.getX() - b.getX()) - u
					* (b.getX() - c.getX()))
					/ den);
			circum = new Circle(cen, cen.distance2(a));
		}
		return circum;
	}

	boolean circumcircleContains(Point p) {

		return circum.radius() > circum.center().distance2(p);
	}

	public String toString() {
		String res = ""; // +_id+") ";
		res += a.toString() + b.toString();
		if (!halfplane)
			res += c.toString();
		// res +=c.toString() +"   | "+abnext._id+" "+bcnext._id+" "+canext._id;
		return res;
	}

	/**
	 * determinates if this triangle contains the point p.
	 * 
	 * @param p
	 *            the query point
	 * @return true iff p is not null and is inside this triangle (Note: on
	 *         boundary is considered inside!!).
	 */
	public boolean contains(Point p) {
		boolean ans = false;
		if (this.halfplane || p == null)
			return false;

		if (isCorner(p)) {
			return true;
		}

		int a12 = p.pointLineTest(a, b);
		int a23 = p.pointLineTest(b, c);
		int a31 = p.pointLineTest(c, a);

		if ((a12 == Point.LEFT && a23 == Point.LEFT && a31 == Point.LEFT)
				|| (a12 == Point.RIGHT && a23 == Point.RIGHT && a31 == Point.RIGHT)
				|| (a12 == Point.ONSEGMENT || a23 == Point.ONSEGMENT || a31 == Point.ONSEGMENT))
			ans = true;

		return ans;
	}

	/**
	 * determinates if this triangle contains the point p.
	 * 
	 * @param p
	 *            the query point
	 * @return true iff p is not null and is inside this triangle (Note: on
	 *         boundary is considered outside!!).
	 */
	public boolean containsBoundaryIsOutside(Point p) {
		boolean ans = false;
		if (this.halfplane || p == null)
			return false;

		if (isCorner(p)) {
			return true;
		}

		int a12 = p.pointLineTest(a, b);
		int a23 = p.pointLineTest(b, c);
		int a31 = p.pointLineTest(c, a);

		if ((a12 == Point.LEFT && a23 == Point.LEFT && a31 == Point.LEFT)
				|| (a12 == Point.RIGHT && a23 == Point.RIGHT && a31 == Point.RIGHT))
			ans = true;

		return ans;
	}

	/**
	 * Checks if the given point is a corner of this triangle.
	 * 
	 * @param p
	 *            The given point.
	 * @return True iff the given point is a corner of this triangle.
	 * 
	 *         By Eyal Roth &amp; Doron Ganel.
	 */
	public boolean isCorner(Point p) {
		return (p.getX() == a.getX() && p.getY() == a.getY()) || (p.getX() == b.getX() && p.getY() == b.getY())
				|| (p.getX() == c.getX() && p.getY() == c.getY());
	}

	// Doron
	public boolean fallInsideCircumcircle(Point[] arrayPoints) {
		boolean isInside = false;
		Point p1 = this.getA();
		Point p2 = this.getB();
		Point p3 = this.getC();
		int i = 0;
		while (!isInside && i < arrayPoints.length) {
			Point p = arrayPoints[i];
			if (!p.equals(p1) && !p.equals(p2) && !p.equals(p3)) {
				isInside = this.circumcircleContains(p);
			}
			i++;
		}

		return isInside;
	}

	/**
	 * compute the Z value for the X,Y values of q. <br />
	 * assume this triangle represent a plane --&gt; q does NOT need to be
	 * contained in this triangle.
	 * 
	 * @param q
	 *            query point (its Z value is ignored).
	 * @return the Z value of this plane implies by this triangle 3 points.
	 */
	public double zValue(Point q) {
		if (q == null || this.halfplane)
			throw new RuntimeException("*** ERR wrong parameters, can't approximate the z value ..***: " + q);
		/* incase the query point is on one of the points */
		if (q.getX() == a.getX() && q.getY() == a.getY())
			return a.getZ();
		if (q.getX() == b.getX() && q.getY() == b.getY())
			return b.getZ();
		if (q.getX() == c.getX() && q.getY() == c.getY())
			return c.getZ();

		/*
		 * plane: aX + bY + c = Z: 2D line: y= mX + k
		 */
		double X = 0, x0 = q.getX(), x1 = a.getX(), x2 = b.getX(), x3 = c.getX();
		double Y = 0, y0 = q.getY(), y1 = a.getY(), y2 = b.getY(), y3 = c.getY();
		double Z = 0, m01 = 0, k01 = 0, m23 = 0, k23 = 0;

		// 0 - regular, 1-horisintal , 2-vertical.
		int flag01 = 0;
		if (x0 != x1) {
			m01 = (y0 - y1) / (x0 - x1);
			k01 = y0 - m01 * x0;
			if (m01 == 0)
				flag01 = 1;
		} else { // 2-vertical.
			flag01 = 2;// x01 = x0
		}
		int flag23 = 0;
		if (x2 != x3) {
			m23 = (y2 - y3) / (x2 - x3);
			k23 = y2 - m23 * x2;
			if (m23 == 0)
				flag23 = 1;
		} else { // 2-vertical.
			flag23 = 2;// x01 = x0
		}

		if (flag01 == 2) {
			X = x0;
			Y = m23 * X + k23;
		} else {
			if (flag23 == 2) {
				X = x2;
				Y = m01 * X + k01;
			} else { // regular case
				X = (k23 - k01) / (m01 - m23);
				Y = m01 * X + k01;

			}
		}
		double r = 0;
		if (flag23 == 2) {
			r = (y2 - Y) / (y2 - y3);
		} else {
			r = (x2 - X) / (x2 - x3);
		}
		Z = b.getZ() + (c.getZ() - b.getZ()) * r;
		if (flag01 == 2) {
			r = (y1 - y0) / (y1 - Y);
		} else {
			r = (x1 - x0) / (x1 - X);
		}
		double qZ = a.getZ() + (Z - a.getZ()) * r;
		return qZ;
	}

	/**
	 * compute the Z value for the X,Y values of q. assume this triangle
	 * represent a plane --&gt; q does NOT need to be contained in this triangle.
	 * 
	 * @param x
	 *            x-coordinate of the query point.
	 * @param y
	 *            y-coordinate of the query point.
	 * @return z (height) value approximation given by the triangle it falls in.
	 * 
	 */
	public double getZ(double x, double y) {
		return zValue(new Point(x, y));
	}

	/**
	 * compute the Z value for the X,Y values of q. assume this triangle
	 * represent a plane --&gt; q does NOT need to be contained in this triangle.
	 * 
	 * @param q
	 *            query point (its Z value is ignored).
	 * @return q with updated Z value.
	 * 
	 */
	public Point getZ(Point q) {
		double z = zValue(q);
		return new Point(q.getX(), q.getY(), z);
	}
	
	public boolean isMark() {
		return mark;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}

	/**
	 * Modification counter for triangulation fast update
	 * 
	 * @return
	 */
	public int getMc() {
		return mc;
	}

	/**
	 * returns true iff this triangle is actually a half plane.
	 */
	public boolean isHalfplane() {
		return this.halfplane;
	}

	public void setHalfplane(boolean halfplane) {
		this.halfplane = halfplane;
	}

	public void setMc(int mc) {
		this.mc = mc;
	}

	/**
	 * returns the consecutive triangle which shares this triangle a,b edge.
	 */
	public Triangle getAbTriangle() {
		return abTriangle;
	}

	public void setAbTriangle(Triangle abTriangle) {
		this.abTriangle = abTriangle;
	}

	/**
	 * returns the consecutive triangle which shares this triangle b,c edge.
	 */
	public Triangle getBcTriangle() {
		return bcTriangle;
	}

	public void setBcTriangle(Triangle bcTriangle) {
		this.bcTriangle = bcTriangle;
	}

	/**
	 * returns the consecutive triangle which shares this triangle c,a edge.
	 */
	public Triangle getCaTriangle() {
		return caTriangle;
	}

	public void setCanext(Triangle canext) {
		this.caTriangle = canext;
	}

	/**
	 * returns the first vertex of this triangle.
	 */
	public Point getA() {
		return a;
	}

	public void setA(Point a) {
		this.a = a;
	}

	/**
	 * returns the second vertex of this triangle.
	 */
	public Point getB() {
		return b;
	}

	public void setB(Point b) {
		this.b = b;
	}

	/**
	 * returns the 3th vertex of this triangle.
	 */
	public Point getC() {
		return c;
	}

	public void setC(Point c) {
		this.c = c;
	}
}
