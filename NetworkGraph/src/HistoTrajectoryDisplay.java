import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class HistoTrajectoryDisplay extends JPanel {
    HistogramTraj traj;
    int tm;
    int bm;
    int lm;
    int rm;
    public HistoTrajectoryDisplay(HistogramTraj t, int top, int bottom, int right, int left){
        traj = t;
        tm = top;
        bm = bottom;
        lm = left;
        rm = right;
    }
    double leftmarginValue;
    double rightmarginValue;
    int topmarginValue;
    public void setMarginVals(){
        leftmarginValue = traj.globalmin;
        rightmarginValue = traj.globalmax;
        topmarginValue = traj.maxbin;
    }
    //the lm will be the location where the leftmost bin starts
    //the rm will be the location where the rightmost bin ends
    //We need to draw rectangles accordingly
    protected void paintComponent(Graphics grf){

        //create instance of the Graphics to use its methods
        super.paintComponent(grf);
        Graphics2D graph = (Graphics2D)grf;

        //Sets the value of a single preference for the rendering algorithms.
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Histogram cur = traj.cur.h;
        //First we draw the axis;
        Line2D xax = new Line2D.Double(lm, bm, rm, bm);
        Line2D yax = new Line2D.Double(lm, bm, lm, tm);
        graph.draw(xax);
        graph.draw(yax);

        //Now we need to draw each bin
        for(Bin b : cur.bins){
            double l = b.count*(bm - tm)/(double)topmarginValue;
            double x1 = b.low_bound*(rm - lm)/(rightmarginValue - leftmarginValue);
            double x2 = b.high_bound*(rm - lm)/(rightmarginValue - leftmarginValue);
            graph.draw(new Line2D.Double(x1, bm, x1, bm - l));
            graph.draw(new Line2D.Double(x2, bm, x2, bm - l));
            graph.draw(new Line2D.Double(x1, bm - l, x2, bm - l));
        }


    }
}
