/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.shapes.primitives.helper;

import java.util.ArrayList;
import java.util.List;
import org.fxyz.geometry.Point3D;
import org.fxyz.geometry.GaussianQuadrature;

/** Spring base on the curve of helix around a torus
 * 
 * Ecuation: r[t]={Cos[t] (R+r Cos[h t]),(R+r Cos[h t]) Sin[t],r Sin[h t]};
 *
 * Tube around helix: S[t,u]=r[t]+a·cos(u)·n[t]+a·sin(u)·b[t] according
 * Frenet-Serret trihedron
 * http://www.usciences.edu/~lvas/math430/Curves.pdf
 * 
 * @author jpereda
 */
public class CurvedSpringHelper {
    
    public static final int tR = 0;
    public static final int tN = 1;
    public static final int tB = 2;
    
    private final double R, r, h;
    
    private double arc;
    private List<Point3D[]> trihedrons;
    private int subDivLength;
    
    public CurvedSpringHelper(double R, double r, double h){
        this.R=R;
        this.r=r;
        this.h=h;
    }

    public void calculateTrihedron(int subDivLength, double arc){
        // Create points
        trihedrons=new ArrayList<>();
        this.subDivLength=subDivLength;
        this.arc=arc;
        for (int t = 0; t <= subDivLength; t++) {  // 0 - length
            trihedrons.add(getTrihedron((float) t / subDivLength * arc));
        }
    }
    /*
    t [0,<=2Pi]
    */
    private Point3D[] getTrihedron(double t){
        // r[t]
        Point3D vR = new Point3D((float)(Math.cos(t)*(R + r*Math.cos(h*t))),
                                 (float)((R + r*Math.cos(h*t))*Math.sin(t)),
                                 (float)(r*Math.sin(h*t)));
        
        // r'[t]
        Point3D dR= new Point3D((float)(-((R + r*Math.cos(h*t))*Math.sin(t))-h*r*Math.cos(t)*Math.sin(h*t)),
                                (float)(Math.cos(t)*(R + r*Math.cos(h*t)) - h*r*Math.sin(t)*Math.sin(h*t)),
                                (float)(h*r*Math.cos(h*t))); 
        
        float nT=dR.magnitude(); // || r'[t] ||
        
        // r''[t]
        Point3D ddR= new Point3D((float)(-(Math.cos(t)*(R + (1 + h*h)*r*Math.cos(h*t))) + 2*h*r*Math.sin(t)*Math.sin(h*t)),
                                 (float)(-((R + (1 + h*h)*r*Math.cos(h*t))*Math.sin(t)) - 2*h*r*Math.cos(t)*Math.sin(h*t)),
                                 (float)(-(h*h*r*Math.sin(h*t)))); 
        // (|| r'[t] ||^2)'[t]
        float dn=(float)(-2*h*r*(R + r*Math.cos(h*t))*Math.sin(h*t));
        // T'[t]=r''[t]/||r't||-r'[t]*(|| r'[t] ||^2)'[t]/2/|| r'[t] ||^3
        Point3D dT=ddR.multiply(1f/nT).substract(dR.multiply(dn/((float)(Math.pow(nT,3d)*2d))));
        
        // T[t]=r'[t]/||r'[t]||
        Point3D T=dR.normalize();
        // N[t]=T'[t]/||T'[t]||
        Point3D N=dT.normalize();
        // B[t]=T[t]xN[t]/||T[t]xN[t]||
        Point3D B=T.crossProduct(N).normalize();
        
        // R,N,B
        return new Point3D[]{vR,N,B};
    }
    
    
    public Point3D getS(int t, float cu, float su){
        Point3D[] trihedron = trihedrons.get(t);
        // S[t,u]
        Point3D p = trihedron[CurvedSpringHelper.tR]
                        .add(trihedron[CurvedSpringHelper.tN].multiply(cu)
                        .add(trihedron[CurvedSpringHelper.tB].multiply(su)));
        p.f=((float)(t*arc)/(float)subDivLength); // [0-<=2Pi]
        return p;
        
    }
    
    public double getLength(double arc){ // [0-<=2Pi]
        GaussianQuadrature gauss = new GaussianQuadrature(5,0,arc);
        // || r'[t] ||
        return gauss.NIntegrate(t->Math.sqrt(r*r+2d*h*h*r*r+2d*R*R+4d*r*R*Math.cos(h*t)+r*r*Math.cos(2d*h*t))/Math.sqrt(2d));
    }
    
    public double getKappa(double t){
        // r'[t]
        Point3D dR= new Point3D((float)(-((R + r*Math.cos(h*t))*Math.sin(t))-h*r*Math.cos(t)*Math.sin(h*t)),
                                (float)(Math.cos(t)*(R + r*Math.cos(h*t)) - h*r*Math.sin(t)*Math.sin(h*t)),
                                (float)(h*r*Math.cos(h*t))); 
        float nT=dR.magnitude(); // || r'[t] ||
        
        // r''[t]
        Point3D ddR= new Point3D((float)(-(Math.cos(t)*(R + (1 + h*h)*r*Math.cos(h*t))) + 2*h*r*Math.sin(t)*Math.sin(h*t)),
                                 (float)(-((R + (1 + h*h)*r*Math.cos(h*t))*Math.sin(t)) - 2*h*r*Math.cos(t)*Math.sin(h*t)),
                                 (float)(-(h*h*r*Math.sin(h*t)))); 
        // || r''[t]xr'[t] ||
        float nddRxdR=ddR.crossProduct(dR).magnitude();
        // kappa[t] = || r''[t]xr'[t] || / || r'[t] ||^3
        return nddRxdR/(float)Math.pow(nT,3d);
        
    }
    
    public double getTau(double t){
        Point3D dR= new Point3D((float)(-((R + r*Math.cos(h*t))*Math.sin(t))-h*r*Math.cos(t)*Math.sin(h*t)),
                                (float)(Math.cos(t)*(R + r*Math.cos(h*t)) - h*r*Math.sin(t)*Math.sin(h*t)),
                                (float)(h*r*Math.cos(h*t))); 
        // r''[t]
        Point3D ddR= new Point3D((float)(-(Math.cos(t)*(R + (1 + h*h)*r*Math.cos(h*t))) + 2*h*r*Math.sin(t)*Math.sin(h*t)),
                                 (float)(-((R + (1 + h*h)*r*Math.cos(h*t))*Math.sin(t)) - 2*h*r*Math.cos(t)*Math.sin(h*t)),
                                 (float)(-(h*h*r*Math.sin(h*t)))); 
        // r'''[t]
        Point3D dddR = new Point3D((float)((R + (1 + 3*h*h)*r*Math.cos(h*t))*Math.sin(t) + h*(3 + h*h)*r*Math.cos(t)*Math.sin(h*t)),
                                   (float)(-(Math.cos(t)*(R + (1 + 3*h*h)*r*Math.cos(h*t))) + h*(3 + h*h)*r*Math.sin(t)*Math.sin(h*t)),
                                   (float)(-(h*h*h*r*Math.cos(h*t))));
        //  r'[t]xr''[t] . r'''[t]
        float dRxddRxdddR=dR.crossProduct(ddR).dotProduct(dddR);
        // || r''[t]xr'[t] ||
        float ndRxddR=dR.crossProduct(ddR).magnitude();
        
        // tau[t] = r'[t]xr''[t].r'''[t] / || r''[t]xr'[t] ||^2
        return Math.abs(dRxddRxdddR/(float)Math.pow(ndRxddR,2d));
    }
    
}
