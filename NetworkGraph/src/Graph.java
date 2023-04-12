import java.util.*;

public class Graph<T> {
    public HashMap<T, Node<T>> nodeList;
    public ArrayList<T> idList;

    public int size;

    public boolean weighted = false;
    public Graph(){
        nodeList = new HashMap<>();
        idList = new ArrayList<>();
    }

    public Graph(int s, ArrayList<T> ilist, HashMap<T, Node<T>> nlist, boolean w){
        nodeList = nlist;
        idList = ilist;
        weighted = w;
        size = s;
    }

    public Graph(boolean weight){
        nodeList = new HashMap<>();
        idList = new ArrayList<>();
        weighted = weight;
    }

    public double in_degreemean() throws Exception{
        int total = 0;
        int count = 0;
        for(T i : idList){
            if(nodeList.get(i).active){
                ++count;
                total += nodeList.get(i).adj_nodes.size();
            }
        }
        if(count == 0){
            throw new Exception("There are no active nodes");
        }
        return (double)total/(double)count;
    }

    public void node_decay(float prob){
        Random r = new Random();
        for(T i : idList){
            if(nodeList.get(i).active && r.nextFloat() <= prob){
                nodeList.get(i).active = false;
            }
        }
    }

    public void edge_decay(float prob, boolean two_way){
        Random r = new Random();
        for(T i : idList){
            if(nodeList.get(i).active){
                for(T j : idList){
                    if(j != i && nodeList.get(j).active){
                        if(nodeList.get(i).adj_nodes.contains(j)){
                            float det = r.nextFloat();
                            if(det < prob){
                                nodeList.get(i).adj_nodes.remove(j);
                                nodeList.get(i).weights.remove(j);
                                if(two_way){
                                    nodeList.get(j).adj_nodes.remove(i);
                                    nodeList.get(j).weights.remove(i);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void reactivate_all(){
        for(T i : idList){
            nodeList.get(i).active = true;
        }
    }
    public void ER_update(float prob) throws Exception {
        Random r = new Random();
        for(T i : idList){
            if(nodeList.get(i).active){
                for(T j : idList){
                    if(nodeList.get(j).active){
                        float det = r.nextFloat();
                        if(det <= prob && !nodeList.get(i).adj_nodes.contains(j)){
                            add_edge(i,j, true);
                        }
                    }
                }
            }
        }
    }
    public void makedense()throws Exception{
        for(T i : idList){
            if(nodeList.get(i).active){
                for(T j : idList){
                    add_edge(i, j, true);
                }
            }
        }
    }
    public void add_edge(T id1, T id2, boolean two_way) throws  Exception{
        Node<T> first;
        Node<T> second;
        try{
            first = nodeList.get(id1);
            second = nodeList.get(id2);
        }
        catch (Exception e){
            throw new Exception("One  of the nodes is not in this graph");
        }

        if(first.adj_nodes.contains(id2)){
            throw new Exception("Node " + id1 + " already contains " + id2);
        }

        first.add_connection(id2);
        if(two_way){
            second.add_connection(id1);
        }
    }

    public void add_edge(T id1, T id2, boolean two_way, float weight) throws  Exception{
        Node<T> first;
        Node<T> second;
        try{
            first = nodeList.get(id1);
            second = nodeList.get(id2);
        }
        catch (Exception e){
            throw new Exception("One  of the nodes is not in this graph");
        }

        if(first.adj_nodes.contains(id2)){
            throw new Exception("Node " + id1 + " already contains " + id2);
        }

        first.add_connection(id2, weight);
        if(two_way){
            second.add_connection(id1, weight);
        }
    }
    public void add_node(T id) throws Exception{

        if(idList.contains(id)){
            throw new Exception("Graph already contains this id: " + id);
        }
        ++size;
        idList.add(id);
        nodeList.put(id, new Node<T>(id));
    }

    public int findLargestComponent(){
        Queue<T> notvisited = new LinkedList<>(idList);
        HashSet<T> visited = new HashSet<>();
        int maxsize = 0;
        while(!notvisited.isEmpty()){
            T curId = notvisited.remove();
            if(visited.contains(curId) || !nodeList.get(curId).active){
                continue;
            }
            visited.add(curId);
            //traverse on this node
            Queue<T> innerTraversal = new LinkedList<>();
            innerTraversal.add(curId);
            int size = 0;
            while(!innerTraversal.isEmpty()){
                ++size;
                T inner_cur_id = innerTraversal.remove();
                for(T n : nodeList.get(inner_cur_id).adj_nodes){
                    if(!visited.contains(n) && nodeList.get(n).active){
                        innerTraversal.add(n);
                        visited.add(n);
                    }
                }
            }
            maxsize = Math.max(size, maxsize);
        }
        return maxsize;
    }
}

class Node<T>{
    T ID;

    boolean active;
    HashSet<T> adj_nodes;

    HashMap<T, Float> weights;
    public Node(T id){
        ID = id;
        adj_nodes = new HashSet<>();
        active = true;
        weights = new HashMap<>();
    }

    public void deactivate(){
        active = false;
    }
    public void add_connection(T id_adj){
        adj_nodes.add(id_adj);
    }

    public void add_connection(T id_adj, float w){
        adj_nodes.add(id_adj);
        weights.put(id_adj, w);
    }
}
