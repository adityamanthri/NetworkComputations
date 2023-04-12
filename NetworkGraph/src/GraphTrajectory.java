import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GraphTrajectory<T> {
    int traj_l;

    String end_status;
    public ArrayList<T> idList;
    HashMap<T, Integer> idMapping;
    int mappingCount;
    public GraphState head;
    public GraphState cur;
    public GraphState tail;
    public GraphTrajectory(){
        idList = new ArrayList<>();
        idMapping = new HashMap<>();
        mappingCount = 0;
        traj_l = 0;
        end_status = "";
    }
    public GraphTrajectory(Graph<T> G){
        traj_l = 1;
        idList = new ArrayList<>();
        idMapping = new HashMap<>();
        boolean[] ns = new boolean[G.size];
        mappingCount = 0;
        double[][] es = Operations.produce_adj_matrix(G);
        for(T i : G.idList){
            idList.add(i);
            idMapping.put(i, mappingCount);
            ns[mappingCount] = G.nodeList.get(i).active;
            ++mappingCount;
        }
        GraphState gs = new GraphState(ns, es, 0);
        head = gs;
        tail = gs;
        cur = gs;
        end_status = "";
    }
    public void addGraphState(Graph<T> G){
        if(traj_l == 0){
            traj_l = 1;
            boolean[] ns = new boolean[G.size];
            mappingCount = 0;
            double[][] es = Operations.produce_adj_matrix(G);
            for(T i : G.idList){
                idMapping.put(i, mappingCount);
                ns[mappingCount] = G.nodeList.get(i).active;
            }
            GraphState gs = new GraphState(ns, es, 0);
            head = gs;
            tail = gs;
            cur = gs;
        }
        else{

            boolean[] ns = new boolean[G.size];
            double[][] es = Operations.produce_adj_matrix(G);
            for(T i : G.idList){
                ns[idMapping.get(i)] = G.nodeList.get(i).active;
            }
            GraphState gs = new GraphState(ns, es, traj_l);
            tail.next = gs;
            gs.prev = tail;
            tail = gs;
            ++traj_l;
        }
    }
    public void addGraphStateER(float prob){

        double[][] nES = new double[idList.size()][idList.size()];
        for(int i = 0; i < nES.length; ++i){
            for(int j = i+1; j < nES[i].length; ++j){
                Random R = new Random();
                if(R.nextDouble() <= prob || tail.edgeState[i][j] > 0){
                    nES[i][j] = 1;
                    nES[j][i] = 1;
                }
            }
        }
        GraphState nGS = new GraphState(tail.nodeState, nES, traj_l);
        tail.next = nGS;
        nGS.prev = tail;
        tail = nGS;
        ++traj_l;
    }
    public void to_next(){
        if(cur.next != null){
            cur = cur.next;
            end_status = "";
        }
        end_status = "This is the last graph in the sequence";
    }
    public void to_prev(){
       if(cur.prev != null){
          cur = cur.prev;
          end_status = "";
       }
       end_status = "This is the first graph in the sequence";
    }
}

class GraphState{

    int id;
    public boolean[] nodeState;
    public double[][] edgeState;

    public GraphState next;
    public GraphState prev;
    public GraphState(boolean[] ns, double[][] es, int i){
        nodeState = ns;
        edgeState = es;
        next = null;
        prev = null;
        id = i;
    }
}
