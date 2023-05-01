public class TesterMain {
    public static void main (String ...args)throws Exception{
        double[][] m1 = new double[][]{{0,1,0,0,0},{0,0,1,1,1}, {0, 1,0,0,0},{0,1,0,0,0}, {0,1,0,0,0}};

        double[][] f2 = Operations.mtf(m1);
        for(int i = 0; i < f2.length; ++i){
            for(int j = 0; j < f2[i].length; ++j){
                System.out.print(f2[i][j] + " ");
            }
            System.out.println();
        }
    }


}
