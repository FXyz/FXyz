/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fxyz.shapes.primitives.helper;

import java.util.ArrayList;
import java.util.List;
import org.fxyz.geometry.Point3D;

/** Given N 3D points (knots), N-1 Bezier cubic curves will be generated, 
 *  passing through these knots, by adding 2(N-1) control points
 *  Conditions if spline is open: 
 *  - C2 in all the inner knots (2 ecuations x N-2 knots)
 *  - On external knots r''=0 (2 ecuations)
 *  Conditions if spline is closed (knot[0]==knot[N]:
 * - C2 in all the inner knots (2 ecuations x N-2 knots)

*  Tridiagonal linear system -  Thomas algorithm
 *  See: http://www.particleincell.com/blog/2012/bezier-splines/
 * 
 * @author jpereda
 */
public class InterpolateBezier {

    private final List<Point3D> knots;
    private final int numSplines;
    private final boolean isClosed;
    
    private final List<BezierHelper> splines;
    
    public InterpolateBezier(List<Point3D> knots){
        this.knots=knots;
        numSplines = knots.size()-1;
        
        isClosed=knots.get(0).equals(knots.get(numSplines));
        
        splines = new ArrayList<>(numSplines);
        calculateControlPoints();
    }
    
    public List<BezierHelper> getSplines() { return splines; }
    
    private void calculateControlPoints(){
        
        if(isClosed){
            List<Point3D> p1=new ArrayList<>(numSplines);
            for(int j=0; j<numSplines; j++){
                p1.add(knots.get(j+1).substract(j==0?knots.get(numSplines-1):knots.get(j-1)).multiply(1/3f).add(knots.get(j)));
            }
            
            for(int j=0; j<numSplines; j++){
                Point3D p2=j==numSplines-1?knots.get(0).multiply(2f).substract(p1.get(0)):
                         knots.get(j+1).multiply(2f).substract(p1.get(j+1));
                splines.add(new BezierHelper(knots.get(j),p1.get(j),p2,knots.get(j+1)));
            }
        } else {
            for(int j=0; j<numSplines; j++){
                splines.add(new BezierHelper(knots.get(j),new Point3D(0f, 0f, 0f),new Point3D(0f, 0f, 0f),knots.get(j+1)));
            }

            for(int i=0; i<3; i++){
                double[] p1=new double[numSplines];
                double[] p2=new double[numSplines];
                double[] ca=new double[numSplines];
                double[] cb=new double[numSplines];
                double[] cc=new double[numSplines];
                double[] cr=new double[numSplines];
                cb[0] = 2d; 
                cc[0] = 1d; 
                cr[0] = i==0?knots.get(0).x + 2d*knots.get(1).x:i==1?knots.get(0).y + 2d*knots.get(1).y:knots.get(0).z + 2d*knots.get(1).z;
                for(int j=1; j<numSplines-1; j++){
                    ca[j]=1d;
                    cb[j]=4d;
                    cc[j]=1d;
                    cr[j]=i==0?4d*knots.get(j).x + 2d*knots.get(j+1).x:i==1?4d*knots.get(j).y + 2d*knots.get(j+1).y:4d*knots.get(j).z + 2d*knots.get(j+1).z;
                }
                ca[numSplines-1]=2d;
                cb[numSplines-1]=7d;
                cr[numSplines-1]=i==0?8d*knots.get(numSplines-1).x + knots.get(numSplines).x:i==1?8d*knots.get(numSplines-1).y + knots.get(numSplines).y:8d*knots.get(numSplines-1).z + knots.get(numSplines).z;

                for(int j = 1; j<numSplines; j++){
                    double m=ca[j]/cb[j-1];
                    cb[j]-=m*cc[j-1];
                    cr[j]-=m*cr[j-1];
                }
                p1[numSplines-1]=cr[numSplines-1]/cb[numSplines-1];
                for(int j=numSplines-2; j>=0; j--){
                    p1[j] = (cr[j]-cc[j]*p1[j+1])/cb[j];
                }
                for(int j=0; j<numSplines-1; j++){
                    p2[j]=(i==0?2d*knots.get(j+1).x:i==1?2d*knots.get(j+1).y:2d*knots.get(j+1).z)-p1[j+1];
                }
                p2[numSplines-1]=((i==0?knots.get(numSplines).x:i==1?knots.get(numSplines).y:knots.get(numSplines).z)+p1[numSplines-1])/2d;

                for(int j=0; j<numSplines; j++){
                    BezierHelper bez = splines.get(j);
                    if(i==0){
                        bez.getPoints().get(1).x=(float)p1[j];
                        bez.getPoints().get(2).x=(float)p2[j];
                    } else if(i==1){
                        bez.getPoints().get(1).y=(float)p1[j];
                        bez.getPoints().get(2).y=(float)p2[j];
                    } else if(i==2){
                        bez.getPoints().get(1).z=(float)p1[j];
                        bez.getPoints().get(2).z=(float)p2[j];
                    } 
                    splines.set(j,bez);
                }
            }
        }
//        splines.forEach(System.out::println);
        
    }
    
}
