import java.util.HashMap;

public class MathSolver {
    HashMap<Integer, Long> fact_memo;
    public MathSolver(){
        fact_memo = new HashMap<>();
    }

    public double frobNorm(double[][] m){
        double sum = 0.0;
        for(int i = 0; i < m.length; ++i){
            for(int j = 0; j < m.length; ++j){
                sum+=m[i][j];
            }
        }
        return Math.sqrt(sum);
    }

    public long factorial(int x){
        if(fact_memo.containsKey(x)){
            return fact_memo.get(x);
        }
        if(x <= 1){
            return 1;
        }
        long result = x * factorial(x-1);
        fact_memo.put(x, result);
        return result;
    }

    public long comb(int n, int k){
        return factorial(n)/(factorial(k)*factorial(n-k));
    }
    public int altcombo(int n, int k){
        double base = 1d;
        for(int i = 0; i < Math.min(k, n-k); ++i){
            base *= (n-i)/(double)(k-i);
        }
        return (int)base;
    }
    public void makeProbMatrix(double[][] input){
        for(int i = 0; i < input.length; ++i){
            double sum = 0.0;
            for(int j= 0; j < input[i].length; ++j){
                sum += input[i][j];
            }
            for(int j = 0; j < input[i].length; ++j){
                input[i][j] /= sum;
            }
        }
    }

    public double[] gaussJordan(double[][] A, double[] b) {
        int n = A.length;
        double[][] Ab = new double[n][n + 1];

        // Create augmented matrix Ab
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Ab[i][j] = A[i][j];
            }
            Ab[i][n] = b[i];
        }

        // Perform forward elimination
        for (int k = 0; k < n; k++) {
            // Find pivot row
            int pivot = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(Ab[i][k]) > Math.abs(Ab[pivot][k])) {
                    pivot = i;
                }
            }
            // Swap rows if necessary
            if (pivot != k) {
                double[] temp = Ab[pivot];
                Ab[pivot] = Ab[k];
                Ab[k] = temp;
            }
            // Eliminate entries below pivot
            for (int i = k + 1; i < n; i++) {
                double factor = Ab[i][k] / Ab[k][k];
                for (int j = k; j < n + 1; j++) {
                    Ab[i][j] -= factor * Ab[k][j];
                }
            }
        }

        // Perform back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < n; j++) {
                sum += Ab[i][j] * x[j];
            }
            x[i] = (Ab[i][n] - sum) / Ab[i][i];
        }

        return x;
    }
}

