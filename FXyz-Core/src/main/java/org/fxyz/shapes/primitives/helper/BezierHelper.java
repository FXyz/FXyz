/**
 * BezierHelper.java
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

package org.fxyz.shapes.primitives.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.fxyz.geometry.GaussianQuadrature;
import org.fxyz.geometry.Point3D;

/** Bezier cubic curve passing through external points (a,d), with control
 * points (b,c)
 * 
 * Ecuation: r[t]=(1-t)^3Â·a+3(1-t)^2Â·tÂ·b+3(1-3)Â·t^2Â·c+t^3Â·d
 *
 * Tube around spline: S[t,u]=r[t]+aÂ·cos(u)Â·n[t]+aÂ·sin(u)Â·b[t] according
 * Frenet-Serret trihedron
 * http://www.usciences.edu/~lvas/math430/Curves.pdf
 * 
 * @author jpereda
 */
public class BezierHelper {
    
    public static final int R = 0;
    public static final int N = 1;
    public static final int B = 2;
    
    private final List<Point3D> points;
    
    private Point3D ab, bc, cd, abc, bcd, abcd;
    private double length;
    private List<Point3D[]> trihedrons;
    private int subDivLength;
    
    public BezierHelper(Point3D a, Point3D b, Point3D c, Point3D d){
        points=Arrays.asList(a,b,c,d);
    }
    
    public List<Point3D> getPoints() { return points; }

    public void preProcess(){
        ab=points.get(1).substract(points.get(0));
        bc=points.get(2).substract(points.get(1));
        cd=points.get(3).substract(points.get(2));
        
        abc=bc.substract(ab);
        bcd=cd.substract(bc);
        
        abcd=bcd.substract(abc);
        
        length=getLength();
    }
    
    public void calculateTrihedron(int subDivLength){
        // Create points
        trihedrons=new ArrayList<>();
        this.subDivLength=subDivLength;
        for (int t = 0; t <= subDivLength; t++) {  // 0 - length
            trihedrons.add(getTrihedron((float) t / subDivLength));
        }
    }
    /*
    t [0,1]
    */
    private Point3D[] getTrihedron(double t){
        if(ab==null || bc==null || cd==null){
            preProcess();
        }
        
        // r[t]
        Point3D R=points.get(0).multiply((float)Math.pow(1d-t, 3))
                        .add(points.get(1).multiply((float)(3d*Math.pow(1d-t, 2)*t))
                          .add(points.get(2).multiply((float)(3d*(1d-t)*Math.pow(t, 2)))
                              .add(points.get(3).multiply((float)(Math.pow(t, 3)))))); 
        // r'[t]
        Point3D dR=ab.multiply((float)(3d*Math.pow(1d-t, 2)))
                    .add(bc.multiply((float)(6d*(1d-t)*t))
                        .add(cd.multiply((float)(3d*Math.pow(t, 2))))); 
        float nT=dR.magnitude(); // || r'[t] ||
        
        // r''[t]
        Point3D ddR=abc.multiply((float)(6d*(1d-t))).add(bcd.multiply((float)(6d*t))); 
        // (|| r'[t] ||^2)'[t]
        float dn=(float)(2*(6*bc.x*(1-2*t)-6*ab.x*(1-t)+6*cd.x*t)*(3*ab.x*Math.pow(1-t,2)+6*bc.x*(1-t)*t+3*cd.x*Math.pow(t,2))+ 
                         2*(6*bc.y*(1-2*t)-6*ab.y*(1-t)+6*cd.y*t)*(3*ab.y*Math.pow(1-t,2)+6*bc.y*(1-t)*t+3*cd.y*Math.pow(t,2))+ 
                         2*(6*bc.z*(1-2*t)-6*ab.z*(1-t)+6*cd.z*t)*(3*ab.z*Math.pow(1-t,2)+6*bc.z*(1-t)*t+3*cd.z*Math.pow(t,2)));
        // T'[t]=r''[t]/||r't||-r'[t]*(|| r'[t] ||^2)'[t]/2/|| r'[t] ||^3
        Point3D dT=ddR.multiply(1f/nT).substract(dR.multiply(dn/((float)(Math.pow(nT,3d)*2d))));
        
        // T[t]=r'[t]/||r'[t]||
        Point3D T=dR.normalize();
        // N[t]=T'[t]/||T'[t]||
        Point3D N=dT.normalize();
        // B[t]=T[t]xN[t]/||T[t]xN[t]||
        Point3D B=T.crossProduct(N).normalize();
        
        // R,N,B
        return new Point3D[]{R,N,B};
    }
    
    
    public Point3D getS(int t, float cu, float su){
        if(ab==null || bc==null || cd==null){
            preProcess();
        }
        
        Point3D[] trihedron = trihedrons.get(t);
        // S[t,u]
        Point3D p = trihedron[BezierHelper.R]
                        .add(trihedron[BezierHelper.N].multiply(cu)
                        .add(trihedron[BezierHelper.B].multiply(su)));
        p.f=((float)t/(float)subDivLength); // [0-1]
        return p;
        
    }
    
    public double getLength(){
        if(ab==null || bc==null || cd==null){
            preProcess();
        }
        GaussianQuadrature gauss = new GaussianQuadrature(5,0,1);
        // || r'[t] ||
        return gauss.NIntegrate(t->(double)(ab.multiply((float)(3d*Math.pow(1d-t, 2)))
                                    .add(bc.multiply((float)(6d*(1d-t)*t))
                                    .add(cd.multiply((float)(3d*Math.pow(t, 2)))))
                                    .magnitude()));
    }
    
    public double getKappa(double t){
        if(ab==null || bc==null || cd==null){
            preProcess();
        }
        // r'[t]
        Point3D dR=ab.multiply((float)(3d*Math.pow(1d-t, 2)))
                    .add(bc.multiply((float)(6d*(1d-t)*t))
                        .add(cd.multiply((float)(3d*Math.pow(t, 2))))); 
        float nT=dR.magnitude(); // || r'[t] ||
        
        // r''[t]
        Point3D ddR=abc.multiply((float)(6d*(1d-t))).add(bcd.multiply((float)(6d*t))); 
        // || r''[t]xr'[t] ||
        float nddRxdR=ddR.crossProduct(dR).magnitude();
        // kappa[t] = || r''[t]xr'[t] || / || r'[t] ||^3
        return nddRxdR/(float)Math.pow(nT,3d);
        
    }
    
    public double getTau(double t){
        if(ab==null || bc==null || cd==null){
            preProcess();
        }
        // r'[t]
        Point3D dR=ab.multiply((float)(3d*Math.pow(1d-t, 2)))
                    .add(bc.multiply((float)(6d*(1d-t)*t))
                        .add(cd.multiply((float)(3d*Math.pow(t, 2))))); 
        // r''[t]
        Point3D ddR=abc.multiply((float)(6d*(1d-t))).add(bcd.multiply((float)(6d*t))); 
        //  r'[t]xr''[t] . r'''[t]
        float dRxddRxdddR=dR.crossProduct(ddR).dotProduct(abcd.multiply(6f));
        // || r''[t]xr'[t] ||
        float ndRxddR=dR.crossProduct(ddR).magnitude();
        
        // tau[t] = r'[t]xr''[t].r'''[t] / || r''[t]xr'[t] ||^2
        return Math.abs(dRxddRxdddR/(float)Math.pow(ndRxddR,2d));
    }
    
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder("{");
        points.forEach(p->{
            sb.append("{").append(p.x).append(",").append(p.y).append(",").append(p.z).append("}");
        });
        return sb.append("}").toString();
    }
    
}
