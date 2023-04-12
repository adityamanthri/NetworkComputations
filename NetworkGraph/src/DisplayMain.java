import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class DisplayMain {
    public static void main(String...args) throws Exception{

        JFrame frame0 = new JFrame();
        frame0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Plotter plt0 = new Plotter(60, 60, 60,60, true, 5);
        ArrayList<Double>[] k1 = Operations.ECE3100SeqGeneration(new float[]{0.1f, 0.3f,0.5f, 0.7f, 0.9f});
        plt0.populatelists(k1[0],k1[1], k1[2]);
        frame0.add(plt0);
        frame0.setSize(550,500);
        frame0.setLocation(0,0);
        frame0.setVisible(true);
        //create an instance of JFrame class
        JFrame frame = new JFrame();
        // set size, layout and location for frame.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Plotter plt = new Plotter(60, 60, 60,60, false, 5);
        frame.setTitle("Large Component Size increasing with mean in-degree");
        plt.setAxisTitle("Mean In-degree", "Largest Component");
        ArrayList<Double>[] k = Operations.largest_component_sampling(100, 100, 0, 10, 0.05f);
        plt.populatelists(k[0],k[1]);
        frame.add(plt);
        frame.setSize(550, 550);
        frame.setLocation(0, 0);
        frame.setVisible(true);

        JFrame frame2 = new JFrame();
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Plotter plt2 = new Plotter(60, 60, 60,60, true, 5);
        frame2.setTitle("Large Component growth with varying edge probability");
        plt2.setAxisTitle("Mean In-degree", "Largest Component", "Edge Probability");
        ArrayList<Double>[] k2 = Operations.ER_chains(15,200,0.0005f,0.01f);
        plt2.populatelists(k2[0],k2[1],k2[2]);
        plt2.setColorscaling(ColorMode.Z);
        frame2.add(plt2);
        frame2.setSize(550, 550);
        frame2.setLocation(580,0);
        frame2.setVisible(true);

        JFrame frame3 = new JFrame();
        frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Plotter plt3 = new Plotter(120, 120, 120,120, true, 5);
        frame3.setTitle("Growth of LC proportion with graph size and mean in degree");
        plt3.setAxisTitle("Mean In-degree", "LC Proportion", "Graph Size");
        ArrayList<Double>[] k3 = Operations.largestComponentVGraphSize(2,4,25,0, 5,400);
        plt3.populatelists(k3[0],k3[1],k3[2]);
        plt3.setColorscaling(ColorMode.X);
        frame3.add(plt3);
        frame3.setSize(550, 550);
        frame3.setLocation(1160,0);
        frame3.setVisible(true);


        GraphHistoPair GHP = Operations.ERFstTraj(25, 40, 0.01f, 5);
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
