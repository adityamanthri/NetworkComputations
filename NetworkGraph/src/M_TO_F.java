import java.util.ArrayList;
import java.util.Arrays;

public class M_TO_F {
}

class Coalescence{
    double[][] matrix;
    int length;
    public Coalescence(double[][] m){
        length = m.length;
        matrix = m;
    }

    public double[][] produce_fst(){
        double[][] F_mat = new double[length][length];
        for(int i = 0; i <length; ++i){
            for(int j = i + 1; j < length; ++j){
                double t_S = (matrix[i][i] + matrix[j][j])/2.0;
                double t_T = (matrix[i][j] + t_S)/2.0;
                double F_i_j=Math.min((t_T - t_S)/t_T,1.0);
                F_mat[i][j] = F_i_j;
                F_mat[j][i] = F_mat[i][j];
            }
        }
        return F_mat;
    }
}

class Migration{
    double[][] matrix;
    MathSolver ms;
    int length;
    public Migration(double[][] m){
        length = m.length;
        matrix = m;
        ms = new MathSolver();
    }

    public double[][] produceCoalescence(){
        double[][] A = produceCoeffMatrix();
        double[] b = produce_sln_vector();
        double[] x = ms.gaussJordan(A, b);

        double[][] T_mat = new double[length][length];
        int cur_ind = 0;
        for(int i = 0; i < length; ++i){
            for(int j  = i; j < length; ++j){
                T_mat[i][j] = x[cur_ind];
                T_mat[j][i] = x[cur_ind];
                ++cur_ind;
            }
        }
        return T_mat;
    }
    public double calculateFirstCoefficient(int j, int i, int same_pop, int lower_bound, int upper_bound, int[] p_list, int[] count){
        int n = matrix[0].length;
        if(j == same_pop){
            double sum = 0;
            for(int k = 0; k < matrix[i].length; ++k){
                sum += matrix[i][k];
            }
            return sum + 1;
        }
        if (lower_bound <= j && j <= upper_bound){
            ++count[0];
            return -1 * matrix[i][i + count[0] - 1];
        }
        for(int p : p_list){
            int sum = 0;
            for(int k = 0; k < p; ++k){
                sum += n - k;
            }
            if(j == i - p + sum){
                return -1 * matrix[i][p];
            }
        }
        return 0;
    }

    public double calculateLastCoefficient(int j, int curpop, int otherpop){
        int n = matrix[0].length;
        int sum = 0;
        for(int k = 0; k < otherpop; ++k){
            sum += n - k;
        }
        if(j == sum + curpop - otherpop){
            double sum2 = 0;
            for (int k = 0; k < matrix[0].length; k++) {
                sum2 += matrix[curpop][k] + matrix[otherpop][k];
            }
            return sum2;
        }
        for(int p = 0; p < n; ++p){
            int[] arr = new int[]{otherpop, curpop};
            for(int t : arr){
                int not_t;
                if(t == otherpop){
                    not_t = curpop;
                }
                else{
                    not_t = otherpop;
                }
                if(p != not_t){
                    int mintp = Math.min(t, p);
                    int maxtp = Math.max(t, p);
                    int sum3 = 0;
                    for(int k = 0; k < mintp; ++k){
                        sum3 += n - k;
                    }
                    if(j == sum3 + maxtp - mintp){
                        return -1 * matrix[not_t][p];
                    }
                }
            }
        }
        return 0;
    }

    public double[][] produceCoeffMatrix(){
        int n = length;

        int nlasteq = n*(n-1)/2;

        int nfirsteq = n;
        int matsize = nfirsteq + nlasteq;
        double[][] coeffmatrix = new double[matsize][matsize];
        for(int i = 0; i < nfirsteq; ++i){
            int sum = 0;
            for(int j = 0; j < i; ++j){
                sum += n-j;
            }
            int samepop = sum;
            int lower = samepop + 1;
            int upper = samepop + (n - (i + 1));
            int[] smallindlist = new int[i];
            for(int p = 0; p < i; ++p){
                smallindlist[p] = p;
            }
            int[] counter = new int[]{1};
            for(int j = 0; j < matsize; ++j){
                coeffmatrix[i][j] = calculateFirstCoefficient(j,i, samepop, lower, upper, smallindlist, counter);
            }
        }

        int curpop = 1;
        int otherpop = 0;
        for(int i = 0; i < nlasteq; ++i){
            if (otherpop == curpop) {
                otherpop = 0;
                ++curpop;
            }
            for(int j =0;j<matsize; ++j){
                coeffmatrix[n+i][j] = calculateLastCoefficient(j, curpop, otherpop);
            }
            ++otherpop;
        }
        return coeffmatrix;
    }

    public double[] produce_sln_vector(){
        int n = length;
        double[] n_first = new double[n];
        Arrays.fill(n_first, 1);
        int n_last_size = (n * (n - 1)) / 2;
        double[] n_last = new double[n_last_size];
        Arrays.fill(n_last, 2);
        double[] b = new double[n + n_last_size];
        for(int i = 0; i < b.length; ++i){
            b[i] = i < n ? n_first[i] : n_last[i - n];
        }
        return b;
    }


}