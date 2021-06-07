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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author Yonatan Graber https://github.com/yonatang/JDT
 */
public class IOParsers {

	/**
	 * creates a Delaunay Triangulation from all the points in the suggested
	 * tsin file or from a smf file (off like). if the file name is .smf - read
	 * it as an smf file as try to read it as .tsin <br>
	 * Note: duplicated points are ignored! <br>
	 * SMF file has an OFF like format (a face (f) is presented by the indexes
	 * of its points - starting from 1 - not from 0): <br>
	 * begin <br>
	 * v x1 y1 z1 <br>
	 * ... <br>
	 * v xn yn zn <br>
	 * f i11 i12 i13 <br>
	 * ... <br>
	 * f im1 im2 im3 <br>
	 * end <br>
	 * <br>
	 * The tsin text file has the following (very simple) format <br>
	 * vertices# (n) <br>
	 * x1 y1 z1 <br>
	 * ... <br>
	 * xn yn zn <br>
	 * 
	 * 
	 */
	public static List<Point> readPoints(File file) throws IOException {
		return readPoints(new FileInputStream(file));
	}

	public static List<Point> readPoints(String file) throws IOException {
		return readPoints(new FileInputStream(file));
	}

	public static List<Point> readPoints(InputStream is) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			String s;
			while ((s = br.readLine()).startsWith("/")) {
			}
			if (s.equals("begin")) {
				return readSmf(br);
			} else if (Utils.isNumeric(s)) {
				return readTsin(br, s);
			} else {
				throw new UnsupportedFormatException("File format not recognized");
			}
		} finally {
			Utils.closeQuietly(br);
		}
	}

	private static List<Point> readSmf(BufferedReader is) throws IOException {
		String s;
		while (!(s = is.readLine()).startsWith("v")) {
		}

		double dx = 1, dy = 1, dz = 1, minX = 0, minY = 0, minZ = 0;

		List<Point> points = new ArrayList<Point>();
		while (s != null && s.charAt(0) == 'v') {
			StringTokenizer st = new StringTokenizer(s);
			st.nextToken();
			double d1 = new Double(st.nextToken()).doubleValue() * dx + minX;
			double d2 = new Double(st.nextToken()).doubleValue() * dy + minY;
			double d3 = new Double(st.nextToken()).doubleValue() * dz + minZ;
			points.add(new Point(d1, d2, d3));
			s = is.readLine();
		}
		return points;
	}

	private static List<Point> readTsin(BufferedReader is, String firstLine) throws IOException {

		List<Point> points = new ArrayList<Point>();
		String s;
		while ((s = is.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(s);
			double d1 = new Double(st.nextToken()).doubleValue();
			double d2 = new Double(st.nextToken()).doubleValue();
			double d3 = new Double(st.nextToken()).doubleValue();
			points.add(new Point(d1, d2, d3));
		}
		return points;
	}

	public static void exportSmf(List<Triangle> triangulation, OutputStream os) {
		exportSmf(triangulation, new OutputStreamWriter(os));
	}

	public static void exportSmf(List<Triangle> triangulation, Writer writer) {
		Set<Point> pointSet = new HashSet<Point>();
		for (Triangle t : triangulation) {
			if (!t.isHalfplane()) {
				pointSet.add(t.getA());
				pointSet.add(t.getB());
				pointSet.add(t.getC());
			}
		}
		ArrayList<Point> pointList = new ArrayList<Point>(pointSet);
		Collections.sort(pointList);
		Map<Point, Integer> pointMap = new HashMap<Point, Integer>();
		for (int i = 0; i < pointList.size(); i++) {
			pointMap.put(pointList.get(i), i);
		}

		PrintWriter os = new PrintWriter(writer);
		try {
			os.println("begin");

			for (Point p : pointList) {
				os.println(String.format("v %s %s %s", p.getX(), p.getY(), p.getZ()));
			}

			for (Triangle t : triangulation) {
				if (!t.isHalfplane()) {
					Integer i1 = pointMap.get(t.getA());
					Integer i2 = pointMap.get(t.getB());
					Integer i3 = pointMap.get(t.getC());
					if (i1 == null || i2 == null || i3 == null)
						throw new RuntimeException("wrong triangulation inner bug - cant write as an SMF file!");
					os.println(String.format("f %d %d %d", (i1 + 1), (i2 + 1), (i3 + 1)));
				}
			}
			os.println("end");
		} finally {
			Utils.closeQuietly(os);
		}
	}

	public static void exportSmf(List<Triangle> triangulation, File smfFile) throws IOException {
		exportSmf(triangulation, new FileWriter(smfFile));
	}

	public static void exportSmf(List<Triangle> triangulation, String smfFile) throws IOException {
		exportSmf(triangulation, new FileWriter(smfFile));
	}

	public static void exportTsin(DelaunayTriangulation dto, File tsinFile) throws IOException {
		exportTsin(dto, new FileWriter(tsinFile));
	}

	public static void exportTsin(DelaunayTriangulation dto, String tsinFile) throws IOException {
		exportTsin(dto, new FileWriter(tsinFile));
	}

	public static void exportTsin(DelaunayTriangulation dto, OutputStream os) {
		exportTsin(dto, new OutputStreamWriter(os));
	}

	public static void exportTsin(DelaunayTriangulation dto, Writer writer) {
		PrintWriter os = new PrintWriter(writer);
		try {
			// prints the tsin file header:
			int len = dto.size();
			os.println(len);
			Iterator<Point> it = dto.verticesIterator();
			while (it.hasNext()) {
				Point p = it.next();
				os.println(String.format("%s %s %s", p.getX(), p.getY(), p.getZ()));
			}
		} finally {
			Utils.closeQuietly(os);
		}
	}

	public static void exportCHTsin(DelaunayTriangulation dto, String tsinFile) throws IOException {
		FileWriter fw = new FileWriter(tsinFile);
		PrintWriter os = new PrintWriter(fw);
		// prints the tsin file header:
		os.println(dto.getConvexHullSize());
		Iterator<Point> it = dto.getConvexHullVerticesIterator();
		while (it.hasNext()) {
			Point p = it.next();
			os.println(String.format("%s %s", p.getX(), p.getY()));
		}
		os.close();
		fw.close();

	}

}
