package org.fxyz.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jpereda
 */
public class GaussianQuadrature {
    
    private final double a;
    private final double b;
    private final List<Pair> gauss=new ArrayList<>();
    
    public GaussianQuadrature(){
        this(5,-1d,1d);
    }
    public GaussianQuadrature(int points){
        this(points,-1d,1d);
    }
    public GaussianQuadrature(int points, double a, double b){
        this.a=a;
        this.b=b;
        switch(points){
            case 2: gauss.add(new Pair(-0.5773502691896258, 1d));
                    gauss.add(new Pair(0.5773502691896258, 1d));
                break;
            case 3: gauss.add(new Pair(-0.7745966692414834, 0.5555555555555554));
                    gauss.add(new Pair(0d, 0.8888888888888888));
                    gauss.add(new Pair(0.7745966692414834, 0.5555555555555554));
                break;
            case 4: gauss.add(new Pair(-0.8611363115940526, 0.34785484513745396));
                    gauss.add(new Pair(-0.33998104358485626, 0.6521451548625462));
                    gauss.add(new Pair(0.33998104358485626, 0.6521451548625462));
                    gauss.add(new Pair(0.8611363115940526, 0.34785484513745396));
                break;
            case 5: gauss.add(new Pair(-0.906179845938664, 0.23692688505618922));
                    gauss.add(new Pair(-0.538469310105683, 0.4786286704993666));
                    gauss.add(new Pair(0, 0.5688888888888889));
                    gauss.add(new Pair(0.538469310105683, 0.4786286704993666));
                    gauss.add(new Pair(0.906179845938664, 0.23692688505618922));
                break;
            case 6: gauss.add(new Pair(-0.9324695142031519, 0.17132449237917097));
                    gauss.add(new Pair(-0.6612093864662645, 0.3607615730481388));
                    gauss.add(new Pair(-0.23861918608319727, 0.4679139345726911));
                    gauss.add(new Pair(0.23861918608319727, 0.4679139345726911));
                    gauss.add(new Pair(0.6612093864662645, 0.3607615730481388));
                    gauss.add(new Pair(0.9324695142031519, 0.17132449237917097));
                break;
            default: gauss.add(new Pair(-0.9491079123427586, 0.12948496616886915));
                    gauss.add(new Pair(-0.7415311855993945, 0.27970539148927703));
                    gauss.add(new Pair(-0.40584515137739713, 0.38183005050511903));
                    gauss.add(new Pair(0d, 0.4179591836734694));
                    gauss.add(new Pair(0.40584515137739713, 0.38183005050511903));
                    gauss.add(new Pair(0.7415311855993945, 0.27970539148927703));
                    gauss.add(new Pair(0.9491079123427586, 0.12948496616886915));
                break;
        }
    }
    
    public double NIntegrate(DensityFunction<Double> f){ // f[t]
        return gauss.parallelStream()
            .mapToDouble(p->p.getWeight()*f.eval((b-a)/2d*p.getAbscissa()+(b+a)/2d))
            .reduce(Double::sum).getAsDouble()*(b-a)/2d;
    }
    
//    // Test
//    public static void main(String[] args) {
//        GaussianQuadrature gauss=new GaussianQuadrature(7,0,Math.PI);
//        System.out.println(""+gauss.NIntegrate(x->Math.sin(x)));
//    }

    private class Pair {
        
        private final Double abscissa;
        private final Double weight;
        
        public Pair(double abscissa, double weight){
            this.abscissa=abscissa;
            this.weight=weight;
        }
        
        public double getAbscissa() { return abscissa; }
        public double getWeight() { return weight; }
    }
}
