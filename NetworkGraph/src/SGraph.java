import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;

/**
 * Simplified graph class - used for building subgraphs
 */
public class SGraph {
    public ArrayList<Integer> nodeList;
    public HashMap<Integer, ArrayList<Integer>> adj;
    public int size;

    /**
     * Edgeweight from nodeList(i) to nodeList(j)
     */
    public double[][] edge_weights;
    public SGraph(){
        nodeList = new ArrayList<>();
        adj = new HashMap<>();
        size = 0;
        edge_weights = new double[0][0];
    }
    public SGraph(int n){
        size = n;
        edge_weights = new double[size][size];
        nodeList = new ArrayList<>();
        adj = new HashMap<>();
        for(int i = 0; i < n; ++i){
            nodeList.add(i);
            adj.put(i, new ArrayList<>());
        }
    }

    public void ERStep(float prob){
        int numAdded = 0;
        for(int i = 0; i < size; ++i){
            for(int j = i + 1; j < size; ++j){
                if(!adj.get(i).contains(j) && !adj.get(j).contains(i)){
                    Random r = new Random();
                    if(r.nextFloat() < prob){
                        ++numAdded;
                        edge_weights[i][j] = 1.0;
                        edge_weights[j][i] = 1.0;
                        adj.get(i).add(j);
                        adj.get(j).add(i);
                    }
                }
            }
        }
        //If we didn't add any edges, lets add another ER step
        if(numAdded == 0){
            ERStep(prob);
        }
    }

    /**
     * Gets a connected component. Do not use recusivly.
     * @param id
     * @return
     */
    public SGraph subGraph(int id){
        SGraph sg = new SGraph();
        if(!nodeList.contains(id)){
            //If this node is not a part of this graph return an empty graph
            return sg;
        }
            //otherwise we need to BFS
        ArrayList<Integer> subGraphNodes = new ArrayList<>();
        HashMap<Integer, ArrayList<Integer>> adjSG = new HashMap<>();
        Queue<Integer> searchQueue = new LinkedList<>();
        searchQueue.add(id);
        subGraphNodes.add(id);
        adjSG.put(id, new ArrayList<>());
        while (!searchQueue.isEmpty()){
            int curNode = searchQueue.remove();

            for(int adjacent : adj.get(curNode)){
                if(!subGraphNodes.contains(adjacent)){
                    subGraphNodes.add(adjacent);
                    adjSG.put(adjacent, new ArrayList<>());
                    adjSG.get(curNode).add(adjacent);
                    adjSG.get(adjacent).add(curNode);
                    searchQueue.add(adjacent);
                }
            }
        }

        int sgSize = subGraphNodes.size();
        double[][] sgEdgeState = new double[sgSize][sgSize];
        for (int i = 0; i < sgSize; ++i){
            for(int j = 0; j < sgSize; ++j){
                sgEdgeState[i][j] = edge_weights[subGraphNodes.get(i)][subGraphNodes.get(j)];
            }
        }
        sg.edge_weights = sgEdgeState;
        sg.nodeList = subGraphNodes;
        sg.adj = adjSG;
        sg.size = sg.nodeList.size();
        return sg;
    }

    /**
     *     Find list of components on subgraph. Do not call recursively
     */
    public ArrayList<SGraph> formComponentList(){
        ArrayList<Integer> aggregate = new ArrayList<>();
        ArrayList<SGraph> components = new ArrayList<>();
        for(int i = 0; i < size; ++i){
            if(aggregate.contains(i)){
                continue;
            }
            SGraph comp = subGraph(i);
            components.add(comp);
            aggregate.addAll(comp.nodeList);
        }
        return components;
    }
}
