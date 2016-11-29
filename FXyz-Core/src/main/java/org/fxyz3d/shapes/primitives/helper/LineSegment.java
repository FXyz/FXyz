/**
 * LineSegment.java
 *
 * Copyright (c) 2013-2016, F(X)yz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of F(X)yz, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL F(X)yz BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package org.fxyz3d.shapes.primitives.helper;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Path;
import org.fxyz3d.geometry.Point3D;

/**
 *
 * @author José Pereda 
 */
public class LineSegment {

    /*
    Given one single character in terms of Path, LineSegment stores a list of points that define 
    the exterior of one of its polygons (!isHole). It can contain reference to one or several 
    holes inside this polygon.
    Or it can define the perimeter of a hole (isHole), with no more holes inside.
    */
    
    private boolean hole;
    private List<Point3D> points;
    private Path path;
    private Point3D origen;   
    private List<LineSegment> holes=new ArrayList<>();
    private String letter;

    public LineSegment(String text) {
        letter=text;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public boolean isHole() {
        return hole;
    }

    public void setHole(boolean isHole) {
        this.hole = isHole;
    }

    public List<Point3D> getPoints() {
        return points;
    }

    public void setPoints(List<Point3D> points) {
        this.points = points;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Point3D getOrigen() {
        return origen;
    }

    public void setOrigen(Point3D origen) {
        this.origen = origen;
    }

    public List<LineSegment> getHoles() {
        return holes;
    }

    public void setHoles(List<LineSegment> holes) {
        this.holes = holes;
    }

    public void addHole(LineSegment hole) {
        holes.add(hole);
    }

    @Override
    public String toString() {
        return "Poly{" + "points=" + points + ", path=" + path + ", origen=" + origen + ", holes=" + holes + '}';
    }
}
