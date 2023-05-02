import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class DisplayMain {
    public static void main(String...args) throws Exception{
        SizeVMeanInDegree t1 = new SizeVMeanInDegree();
        t1.start();
        LargeCompGrowth t2 = new LargeCompGrowth();
        t2.start();
        growthVInDegreeAndSize t3 = new growthVInDegreeAndSize();
        t3.start();
        graphGrowth t4 = new graphGrowth();
        t4.start();
        GraphFSTTrajectory t5 = new GraphFSTTrajectory();
        t5.start();
    }
}
class SizeVMeanInDegree extends Thread{
    public SizeVMeanInDegree(){

    }

    public void run() {
        //create an instance of JFrame class
        JFrame frame = new JFrame();
        // set size, layout and location for frame.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Plotter plt = new Plotter(60, 60, 60,60, false, 5);
        frame.setTitle("Large Component Size increasing with mean in-degree");
        plt.setAxisTitle("Mean In-degree", "Largest Component");
        ArrayList<Double>[] k = new ArrayList[0];
        try {
            k = Operations.largest_component_sampling(100, 100, 0, 10, 0.05f);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        plt.populatelists(k[0],k[1]);
        frame.add(plt);
        frame.setSize(550, 550);
        frame.setLocation(0, 0);
        frame.setVisible(true);

    }
}

class LargeCompGrowth extends Thread{
    public LargeCompGrowth(){

    }
    public void run(){

        JFrame frame2 = new JFrame();
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Plotter plt2 = new Plotter(60, 60, 60,60, true, 5);
        frame2.setTitle("Large Component growth with varying edge probability");
        plt2.setAxisTitle("Mean In-degree", "Largest Component", "Edge Probability");
        ArrayList<Double>[] k2 = new ArrayList[0];
        try {
            k2 = Operations.ER_chains(15,200,0.0005f,0.01f);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        plt2.populatelists(k2[0],k2[1],k2[2]);
        plt2.setColorscaling(ColorMode.Z);
        frame2.add(plt2);
        frame2.setSize(550, 550);
        frame2.setLocation(580,0);
        frame2.setVisible(true);
    }
}

class growthVInDegreeAndSize extends Thread{
    public growthVInDegreeAndSize(){

    }
    public void run(){


        JFrame frame3 = new JFrame();
        frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Plotter plt3 = new Plotter(120, 120, 120,120, true, 5);
        frame3.setTitle("Growth of LC proportion with graph size and mean in degree");
        plt3.setAxisTitle("Mean In-degree", "LC Proportion", "Graph Size");
        ArrayList<Double>[] k3 = new ArrayList[0];
        try {
            k3 = Operations.largestComponentVGraphSize(2,4,25,0, 5,400);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        plt3.populatelists(k3[0],k3[1],k3[2]);
        plt3.setColorscaling(ColorMode.X);
        frame3.add(plt3);
        frame3.setSize(550, 550);
        frame3.setLocation(1160,0);
        frame3.setVisible(true);
    }
}

class graphGrowth extends Thread{
    public graphGrowth(){

    }
    public void run(){

        GraphHistoPair GHP = null;
        try {
            GHP = Operations.ERFstTraj(25, 40, 0.01f, 5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JFrame frame4 = new JFrame();
        frame4.setTitle("ER Graph Trajectory");
        frame4.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton prev = new JButton("<<");
        prev.setBounds(25,425,50,30);
        frame4.add(prev);
        JButton next = new JButton(">>");
        next.setBounds(75,425,50,30);
        frame4.add(next);
        GraphDisplayController<Integer> GDC = new GraphDisplayController<>(50,500,50,400,GHP.gt);

        JFrame frame5 = new JFrame();
        frame5.setTitle("Fst Histograms");
        HistoTrajectoryDisplay HTD = new HistoTrajectoryDisplay(GHP.ht, 50, 400,500, 50);
        frame5.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        prev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                frame4.remove(GDC);
                frame5.remove(HTD);
                GDC.gt.to_prev();
                HTD.traj.toPrev();
                HTD.repaint();
                GDC.repaint();
                frame4.add(GDC);
                frame4.repaint();
                frame5.add(HTD);
                frame5.repaint();

            }
        });
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                frame4.remove(GDC);
                frame5.remove(HTD);
                GDC.gt.to_next();
                HTD.traj.toNext();
                HTD.repaint();
                GDC.repaint();
                frame4.add(GDC);
                frame4.repaint();
                frame5.add(HTD);
                frame5.repaint();
            }
        });
        frame4.add(GDC);
        frame5.add(HTD);



        frame4.setSize(550,500);

        frame4.setLocation(0, 580);

        frame4.setVisible(true);

        frame5.setSize(550,500);

        frame5.setLocation(0, 580);

        frame5.setVisible(true);
    }
}

class GraphFSTTrajectory extends Thread{
    public GraphFSTTrajectory(){

    }
    public void run(){

        try {
            ArrayList<double[][]> FSTs= Operations.AGGREGATED_FST(30, 40, 0.01f);
            double[][] points = Operations.MDSPoints(FSTs);

            //process points
            double minXVal = points[0][0];
            double minYVal = points[0][1];
            for(int i = 0; i < points.length ;++i){
                minXVal = Math.min(points[i][0], minXVal);
                minYVal = Math.min(points[i][1], minYVal);
            }
            for(int i = 0; i < points.length ;++i){
                points[i][0] = Math.log10(points[i][0] - 2*minXVal);
                points[i][1] = Math.log10(points[i][1] - 2*minYVal);
            }

            JFrame frame = new JFrame();
            // set size, layout and location for frame.
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Plotter plt = new Plotter(60, 60, 60,60, false, 5);
            frame.setTitle("FST MDS");
            ArrayList<Double>[] k = new ArrayList[2];
            k[0] = new ArrayList<>();
            k[1] = new ArrayList<>();
            for(int i = 0; i < points.length; ++i){
                k[0].add(points[i][0]);
                k[1].add(points[i][1]);
            }
            plt.populatelists(k[0],k[1]);
            plt.setColorscaling(ColorMode.INDEX);
            frame.add(plt);
            frame.setSize(550, 550);
            frame.setLocation(0, 0);
            frame.setVisible(true);
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }
}