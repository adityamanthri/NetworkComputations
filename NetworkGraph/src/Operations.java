import java.nio.file.FileStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.function.*;


public class Operations {

    public static double[][] mtf(double[][] m){
        if(m.length == 1){
            return new double[][]{{0d}};
        }
        //We actually need to comb the unconnected nodes out of m first
        HashSet<Integer> IGNORE = new HashSet<>();
        for(int  i = 0; i < m.length; ++i){
            boolean hasEdge = false;
            for(int j = 0; j < m.length; ++j){
                if(m[i][j] > 0){
                    hasEdge = true;
                    break;
                }
            }
            if(!hasEdge){
                IGNORE.add(i);
            }
        }
        double[][] combedM = new double[m.length - IGNORE.size()][m.length - IGNORE.size()];
        int ci = 0;
        int cj = 0;
        for(int i = 0; i < m.length; ++i){
            cj = 0;
            if(IGNORE.contains(i)){
                continue;
            }

            for(int j = 0; j < m[i].length; ++j){
                if(IGNORE.contains(j)){
                    continue;
                }
                combedM[ci][cj] = m[i][j];
                ++cj;
            }
            ++ci;
        }
        Migration M = new Migration(combedM);
        double[][] t = M.produceCoalescence();
        Coalescence T = new Coalescence(t);
        return T.produce_fst();
    }

    public static Histogram FstHistoList(double[][] fst, int bins, boolean include1){
        ArrayList<Double> vals = new ArrayList<>();
        for(int i = 0; i < fst.length; ++i){
            for(int j = i+1; j < fst[i].length; ++j){
                if(fst[i][j] >= 0 && (fst[i][j] < 0.99999 || include1)){
                    vals.add(fst[i][j]);
                }

            }
        }
        return new Histogram(vals, bins);
    }

    //Calcs h2-h1
    public static double HistogramDistance(Histogram h1, Histogram h2, DoubleBinaryOperator innerFunction, DoubleUnaryOperator outerFunction){
        double prevBound = Math.min(h1.min, h2.min);
        int cur = prevBound == h1.min ? 0 : 1;
        int index1 = 0;
        int index2 = 0;
        double innersum = 0;
        while (index1 < h1.numBins && index2 < h2.numBins){
            double nextBound = cur == 0? Math.min(h1.bins.get(index1).high_bound, h2.bins.get(index2).low_bound) : Math.min(h2.bins.get(index2).high_bound, h1.bins.get(index1).low_bound);
            if(nextBound == h1.bins.get(index1).high_bound){
                ++index1;
            }
            if(nextBound == h2.bins.get(index2).high_bound){
                ++index2;
            }
            innersum += innerFunction.applyAsDouble(h1.bins.get(index1).count, h2.bins.get(index2).count)/(nextBound - prevBound);
            prevBound = nextBound;
        }
        return outerFunction.applyAsDouble(innersum);
    }

    public static GraphTrajectory<Integer> edge_decay_traj(int nodes, int decay_steps, float decay_prob, boolean twoway) throws Exception {
        Graph<Integer> G = new Graph<>();
        for(int i = 0; i < nodes; ++i){
            G.add_node(i);
        }
        G.makedense();
        GraphTrajectory<Integer> gt = new GraphTrajectory<>(G);
        for(int i = 0; i < decay_steps; ++i){
            G.edge_decay(decay_prob, twoway);
            gt.addGraphState(G);
        }
        return gt;
    }

    public static <T> double[][] produce_adj_matrix(Graph<T> G){
        int index = 0;
        HashMap<T, Integer> indexmapping = new HashMap<>();
        for(T i : G.idList){
            indexmapping.put(i, index);
            ++index;
        }
        double[][] matrix = new double[index][index];

        for(T i : G.idList){
            for(T j : G.nodeList.get(i).adj_nodes){
                matrix[indexmapping.get(i)][indexmapping.get(j)] = G.weighted ? G.nodeList.get(i).weights.get(j) : 1.0;
            }
        }
        return matrix;
    }
    public static ArrayList[] ER_chains(int graphsize, int numgraphs, float probstart, float probend) throws Exception {
        float probstep = (probend-probstart)/numgraphs;
        ArrayList<Double> xvalues = new ArrayList<>();
        ArrayList<Double> yvalues = new ArrayList<>();
        ArrayList<Double> zvalues = new ArrayList<>();

        for(int i = 0; i < numgraphs; ++i){
            //create the initial graph
            float curprob = probstart + i * probstep;
            Graph<Integer> g = ER_graph(graphsize, curprob);
            int comp = g.findLargestComponent();
            xvalues.add(g.in_degreemean());
            yvalues.add((double) comp);
            zvalues.add((double) curprob);
            int convCounter = 0;
            while(comp <= graphsize && convCounter < 3){
                comp = g.findLargestComponent();

                xvalues.add(g.in_degreemean());
                yvalues.add((double) comp);
                zvalues.add((double) curprob);
                g.ER_update(curprob);
                if(comp >= graphsize){
                    ++convCounter;
                }
            }
        }

        ArrayList[] vals = new ArrayList[3];
        vals[0] =xvalues;
        vals[1] = yvalues;
        vals[2] = zvalues;
        return vals;
    }

    public static ArrayList<Double>[] largest_component_sampling(int graph_size, int numsamples, int indegreemean_start, int indeegreemean_end, float indegreestep) throws Exception{
        ArrayList<Double> xvalues = new ArrayList<>();
        ArrayList<Double> yvalues = new ArrayList<>();
//        PrintWriter pw = new PrintWriter(new FileWriter("out.txt"));
        for(float i = indegreemean_start; i < indeegreemean_end + indegreestep; i+=indegreestep){
            float p = i/((float)graph_size - 1f);

            float sum = 0f;
            for(int j = 0; j < numsamples; ++j){
                Graph<Integer> G = ER_graph(graph_size, p);
                sum += G.findLargestComponent();
            }
            xvalues.add((double) i);
            yvalues.add((double) (sum/(float) numsamples));
//            DecimalFormat df = new DecimalFormat("0.###");
//            pw.print(df.format(i));
//            pw.print(" ");
//            pw.println(sum/(float) numsamples);
//            System.out.println(df.format(i) + " " + sum/(float) numsamples);
        }
        ArrayList<Double>[] vals = new ArrayList[2];
        vals[0] = xvalues;
        vals[1] = yvalues;
        return vals;
//        pw.close();
    }

    public static ArrayList<Double>[] largestComponentVGraphSize(int graphsize0, int graphsizestep, int steps, int indegreemean_start, int indeegreemean_end, int indegreestep) throws Exception {
        float in_degree_steps = (indeegreemean_end - indegreemean_start)/(float)indegreestep;
        ArrayList<Double>[] vals = new ArrayList[3];
        vals[0] = new ArrayList<>();
        vals[1] = new ArrayList<>();
        vals[2] = new ArrayList<>();
        for(int  i = 0; i < steps; ++i){
            int graphsize = graphsize0 + i*(graphsizestep);
            ArrayList<Double>[] sampled = largest_component_sampling(graphsize, 25,indegreemean_start,indeegreemean_end,in_degree_steps);
            for(int j = 0; j < sampled[0].size(); ++j){
                vals[0].add(sampled[0].get(j));
                vals[1].add(sampled[1].get(j)/graphsize);
                vals[2].add((double) graphsize);
            }
        }
        return vals;
    }

    public static ArrayList<Double>[] ECE3100SeqGeneration(float[] p){
        ArrayList<Double>[] ans = new ArrayList[3];
        ans[0] = new ArrayList<>();
        ans[1] = new ArrayList<>();
        ans[2] = new ArrayList<>();
        float[] avgLengths = new float[4];
        Random r = new Random();
        for(float x : p) {
            for (int i = 0; i < 10000; ++i) {
                int seqIndex = 0;
                int seQlen = 0;
                boolean last = false;
                int last_index = 0;
                while (seqIndex < 4 && seQlen < 5000) {
                    if (seQlen == 0) {
                        last = r.nextFloat() <= x;
                    } else {
                        float det = r.nextFloat();
                        if (det <= x != last) {
                            avgLengths[seqIndex] += seQlen - last_index;
                            last_index = seQlen;
                            last = det <= x;
                            ++seqIndex;
                        }
                    }
                    ++seQlen;
                }
            }


            for (int i = 0; i < 4; ++i) {
                avgLengths[i] /= 10000;
                ans[0].add((double) i);
                ans[1].add((double) avgLengths[i]);
                ans[2].add((double)x);
            }
        }
        return ans;
    }
    public static GraphHistoPair ERFstTraj(int nodecount, int steps, float prob, int bins) throws Exception{
        Graph<Integer> g0 = new Graph<>();
        for(int i = 0; i < nodecount; ++i){
            g0.add_node(i);
        }
        GraphHistoPair GHP = new GraphHistoPair();
        GHP.gt = new GraphTrajectory<>(g0);
        GHP.ht = new HistogramTraj(FstHistoList(mtf(GHP.gt.cur.edgeState), bins, false));
        for(int i = 0; i < steps; ++i){
            GHP.gt.addGraphStateER(prob);
            GHP.ht.addHisto(FstHistoList(mtf(GHP.gt.tail.edgeState), bins, false));
        }
        return GHP;

    }

    public static GraphTrajectory<Integer> ERTrajectory(int nodecount,int steps, float prob) throws Exception {
        Graph<Integer> g0 = new Graph<>();
        for(int i = 0; i < nodecount; ++i){
            g0.add_node(i);
        }
        GraphTrajectory<Integer> gt = new GraphTrajectory<>(g0);
        for(int i = 0; i < steps; ++i){

            gt.addGraphStateER(prob);
        }
        return gt;
    }
    public static Graph<Integer> ER_graph(int numNodes, float prob) throws Exception {
        Random R = new Random();
        Graph<Integer> erG = new Graph<>();
        for(int i = 0; i < numNodes; ++i){
            erG.add_node(i);
        }
        for(int i = 0; i < numNodes; ++i){
            for(int j = i + 1; j < numNodes; ++j){
                float det = R.nextFloat();
                if(det <= prob){
                    erG.add_edge(i, j, true);
                }
            }
        }
        return erG;
    }
    public static Graph<Integer> migrationGraph(int numNodes, float edge_prob, float[]migrationprobs) throws Exception {
        Random R = new Random();
        Graph<Integer> erG = new Graph<>(true);
        for(int i = 0; i < numNodes; ++i){
            erG.add_node(i);
        }
        for(int i = 0; i < numNodes; ++i){
            for(int j = i + 1; j < numNodes; ++j){
                float det = R.nextFloat();
                if(det <= edge_prob){
                    int weight_index = R.nextInt(migrationprobs.length);
                    erG.add_edge(i, j, true, migrationprobs[weight_index]);
                }
            }
        }
        return erG;
    }

    public static double[][] MDSPoints(ArrayList<double[][]> FST_CHAIN) throws Exception {
        double[][] distArr = new double[FST_CHAIN.size()][FST_CHAIN.size()];
        for(int i = 0; i < distArr.length; ++i){
            for(int j = i + 1; j < distArr.length; ++j){
                double dist = Math.abs(MathSolver.frobNorm(FST_CHAIN.get(i)) - MathSolver.frobNorm(FST_CHAIN.get(j)));
                distArr[i][j] = dist;
                distArr[j][i] = dist;
            }
        }
        return MathSolver.metricMDS(distArr);
    }

    public static ArrayList<double[][]> AGGREGATED_FST(int graph_size, int steps, float edgeProb){
        ArrayList<double[][]> traj = new ArrayList<>();
        SGraph graph = new SGraph(graph_size);
        for(int i = 0; i < steps; ++i){
            graph.ERStep(edgeProb);
            double[][] FST = new double[graph_size][graph_size];
            for(int j = 0; j < graph_size; ++j){
                for(int k = 0; k < graph_size; ++k){
                    FST[j][k] = 1d;
                }
            }
            ArrayList<SGraph> allSubComps = graph.formComponentList();
            for(SGraph subComp: allSubComps){
                double[][] subfst = mtf(subComp.edge_weights);
                for(int j = 0; j < subfst.length; ++j){
                    for(int k = 0; k < subfst.length; ++k){
                        FST[subComp.nodeList.get(j)][subComp.nodeList.get(k)] = subfst[j][k];
                        FST[subComp.nodeList.get(k)][subComp.nodeList.get(j)] = subfst[k][j];
                    }
                }
            }
            traj.add(FST);

        }

        return traj;

    }
}
class GraphHistoPair{
    GraphTrajectory<Integer> gt;
    HistogramTraj ht;
}
