public class TesterMain {
    public static void main (String ...args)throws Exception {
//        double[][] m1 = new double[][]{{0,1,0,0,0},{0,0,1,1,1}, {0, 1,0,0,0},{0,1,0,0,0}, {0,1,0,0,0}};
//
//        double[][] f2 = Operations.mtf(m1);
//        for(int i = 0; i < f2.length; ++i){
//            for(int j = 0; j < f2[i].length; ++j){
//                System.out.print(f2[i][j] + " ");
//            }
//            System.out.println();
//        }
//    }

//        double[][] m = new double[][]{{4, -30, 60, -35}, {-30, 300, -675, 420}, {60, -675, 1620, -1050}, {-35, 420, -1050, 700}};
//        EigenvalueDecomposition ed = new EigenvalueDecomposition(m);
//        double[][] EV = ed.getV();
//        double[] v = ed.getRealEigenvalues();
//        for(int i =0; i < v.length; ++i){
//            System.out.print(v[i] + " ");
//        }
//        System.out.println("\n");
//        for(int i = 0; i < EV.length; ++i){
//            for(int j = 0; j < EV[i].length; ++j){
//                System.out.print(EV[i][j] + " ");
//            }
//            System.out.println();
//        }

        float hi = 1f;
        float lo = 0f;
        int d = 17;
        int n = 100;
        for(int k = 1; k <= n; ++k){
            float x = (hi + lo)/2f;
            float fx = (3 * x - 2)* (3*x - 2);
            System.out.println(x + " " + fx);
            if (fx > 0){
                hi = x;
            }
            else{
                lo = x;
            }
        }
        System.out.println((hi + lo)/2f);


    }

}
