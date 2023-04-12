import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GraphDisplayController<T> extends JPanel {
    GraphTrajectory<T> gt;
    Point2D center;
    HashMap<T, Point2D> id_to_point;
    HashMap<T, Point2D> id_to_vel;
    HashMap<T, Point2D> id_to_acc;
    ArrayList<Line2D> bounds;
    final double node_radius = 7.5f;
    final double dt = 0.01;
    final int numsteps = 2000;
    public GraphDisplayController(double lb, double rb, double tb, double bb, GraphTrajectory<T> gt){
        this.gt = gt;
        bounds = new ArrayList<>();
        bounds.add(new Line2D.Double(lb, tb, lb, bb));
        bounds.add(new Line2D.Double(rb, tb, rb, bb));
        bounds.add(new Line2D.Double(lb, tb, rb, tb));
        bounds.add(new Line2D.Double(lb, bb, rb, bb));
        center =new Point2D.Double((lb + rb)/2, (bb + tb)/2);
        Random R = new Random();
        id_to_point = new HashMap<>();
        id_to_acc = new HashMap<>();
        id_to_vel = new HashMap<>();
        for(T id : gt.idList){
            id_to_acc.put(id, new Point2D.Double(0,0));
            id_to_vel.put(id, new Point2D.Double(0,0));
            id_to_point.put(id, new Point2D.Double(lb + node_radius + R.nextDouble()*(rb - 2*node_radius - lb), tb + node_radius + R.nextDouble()*(bb - 2*node_radius - tb)));
        }
        for(int i = 0; i < numsteps; ++i){
            updatePos();
            update_vel();
            update_accel();
        }
    }


    public Point2D unit_direction(Point2D from, Point2D to){
        double distance = from.distance(to);
        return new Point2D.Double((to.getX() - from.getX())/distance, (to.getY() - from.getY())/distance);
    }

    public void update_accel(){
        HashMap<T, Point2D> nextAccel = new HashMap<>();
        for(T id : id_to_point.keySet()){
            double cx = 0.0;
            double cy = 0.0;
            for(Line2D bound : bounds){
                    //if we are too close to a boundary, accelerate towards the center
                    Point2D unit = unit_direction(id_to_point.get(id),center);
                    cx += 7*unit.getX()/Math.sqrt(bound.ptLineDist(id_to_point.get(id)));
                    cy += 7*unit.getY()/Math.sqrt(bound.ptLineDist(id_to_point.get(id)));

            }
            for(T other : id_to_point.keySet()){
                if(id != other){
                    Point2D p1 = id_to_point.get(id);
                    Point2D p2 = id_to_point.get(other);
                    double dist = p1.distance(p2);
                    Point2D unit_dir = unit_direction(p1, p2);
                    //Completely repel if distance is below collision threshold
                    if(dist < 2*node_radius){
                        double mag = -500/(dist * dist);
                        cx += mag * unit_dir.getX();
                        cy += mag * unit_dir.getY();
                    }
                    //If there is an edge, there should be some attraction
                    else if(gt.cur.edgeState[gt.idMapping.get(id)][gt.idMapping.get(id)] > 0){
                        double mag = 6.5/(dist*dist);
                        cx += mag * unit_dir.getX();
                        cy += mag * unit_dir.getY();
                    }
                    //otherwise weakly repel
                    else{
                        double mag = -100/(dist*dist);
                        cx += mag * unit_dir.getX();
                        cy += mag * unit_dir.getY();
                    }
                }
            }
            nextAccel.put(id, new Point2D.Double(cx, cy));
        }
        id_to_acc = nextAccel;
    }
    public void update_vel(){
        HashMap<T, Point2D> nextVel = new HashMap<>();
        for(T id : id_to_vel.keySet()){
            Point2D prevvel = id_to_vel.get(id);
            Point2D accel = id_to_acc.get(id);
            Point2D newVel = new Point2D.Double(prevvel.getX() + accel.getX()*dt, prevvel.getY() + accel.getY()*dt);
            nextVel.put(id, newVel);
        }
        id_to_vel = nextVel;
    }
    public void updatePos(){
        HashMap<T, Point2D> nextPos = new HashMap<>();
        for(T id : id_to_point.keySet()){
            Point2D ppos = id_to_point.get(id);
            Point2D vel = id_to_vel.get(id);
            Point2D newX = new Point2D.Double(ppos.getX() + vel.getX()*dt, ppos.getY() + vel.getY()*dt);
            nextPos.put(id, newX);
        }
        id_to_point = nextPos;
    }

    protected void paintComponent(Graphics grf){

        //create instance of the Graphics to use its methods
        super.paintComponent(grf);
        Graphics2D graph = (Graphics2D)grf;

        //Sets the value of a single preference for the rendering algorithms.
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for(T id : gt.idList){
            //if this node is active, draw it and all its edges
            if(gt.cur.nodeState[gt.idMapping.get(id)]){
                for(T other : gt.idList){
                    if(other != id && gt.cur.edgeState[gt.idMapping.get(id)][gt.idMapping.get(other)] > 0){
                        graph.draw(new Line2D.Double(id_to_point.get(id).getX(), id_to_point.get(id).getY(), id_to_point.get(other).getX(), id_to_point.get(other).getY() ));
                    }
                }
            }
        }
        //draw the nodes after, so they appear on top of edges
        for(T id : gt.idList){
            if(gt.cur.nodeState[gt.idMapping.get(id)]){
                graph.setColor(Color.BLUE);
                graph.fill(new Ellipse2D.Double(id_to_point.get(id).getX() - node_radius, id_to_point.get(id).getY() - node_radius, 2*node_radius, 2*node_radius));
            }
        }
    }
}
