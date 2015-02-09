
package org.fxyz.geometry;

import java.util.stream.IntStream;

/**
 *
 * @author jpereda
 */
public class Face3 {
    
    public int p0=0;
    public int p1=0;
    public int p2=0;

    public Face3(int p0, int p1, int p2) {
        this.p0=p0;
        this.p1=p1;
        this.p2=p2;
    }

    public IntStream getFace() { return getFace(0); }
    public IntStream getFace(int t) { return IntStream.of(p0,t,p1,t,p2,t); }
    public IntStream getFace(int t0, int t1, int t2) { return IntStream.of(p0,t0,p1,t1,p2,t2); }
    public IntStream getFace(Face3 t) { return IntStream.of(p0,t.p0,p1,t.p1,p2,t.p2); }
    
    @Override
    public String toString() {
        return "Face3{" + "p0=" + p0 + ", p1=" + p1 + ", p2=" + p2 + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.p0;
        hash = 17 * hash + this.p1;
        hash = 17 * hash + this.p2;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Face3 other = (Face3) obj;
        if (this.p0 != other.p0) {
            return false;
        }
        if (this.p1 != other.p1) {
            return false;
        }
        if (this.p2 != other.p2) {
            return false;
        }
        return true;
    }
    
    
    

}
