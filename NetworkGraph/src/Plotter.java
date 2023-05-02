import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

//Extends JPanel class  
public class Plotter extends JPanel{
    //initialize coordinates  
    public ArrayList<Double> Xcord;
    public ArrayList<Double>  Ycord;
    public ArrayList<Double> Zcord;

    public String Title = "";
    public String xTitle = "";
    public String yTitle = "";
    public String zTitle = "";

    public void setTitle(String t){
        Title = t;
    }

    public void setAxisTitle(String x, String y, String z){
        xTitle = x;
        yTitle = y;
        zTitle = z;
    }
    public void setAxisTitle(String x, String y){
        xTitle = x;
        yTitle = y;
    }


    public boolean indicate_3d = false;

    int tm;
    int bm;
    int lm;
    int rm;

    public float[] zdirVec;
    public float[] xdirVec;
    public float[] ydirVec;
    DecimalFormat df = new DecimalFormat("0.##E0");
    int numticks = 10;


    public Plotter(){
        Xcord = new ArrayList<>();
        Ycord = new ArrayList<>();
    }

    float xEndX;
    float xEndY;
    float yEndX;
    float yEndY;
    float zEndX;
    float zEndY;
    public Plotter(int topmargin, int bottommargin, int leftmargin, int rightmargin, boolean enable3d, int nt){
        Xcord = new ArrayList<>();
        Ycord = new ArrayList<>();
        if(enable3d){
            Zcord = new ArrayList<>();
        }
        indicate_3d = enable3d;
        tm = topmargin;
        bm = bottommargin;
        lm = leftmargin;
        rm = rightmargin;
        numticks = nt;
    }


    public void populatelists(ArrayList<Double> X, ArrayList<Double> Y){
        Xcord = X;
        Ycord = Y;
    }
    public void populatelists(ArrayList<Double> X, ArrayList<Double> Y, ArrayList<Double> Z){
        Xcord = X;
        Ycord = Y;
        Zcord = Z;
    }

    public int originX;
    public int originY;
    private void setOrigin(){

        if(indicate_3d){
            originX = (getWidth() - rm - lm)/3 + lm;
            originY = getHeight() - bm;
        }
        else{
            originX = lm;
            originY = getHeight() - bm;
        }
    }

    private void setEnds(){
        if(!indicate_3d){
            xEndX = getWidth() - rm;
            xEndY = originY;
            yEndX = originX;
            yEndY = tm;
        }else{
            xEndX = getWidth() - rm;
            yEndX = originX;
            zEndX = lm;

            yEndY = originY/3f +2f*tm/3f;
            xEndY = originY - tm;
            zEndY = /*tm + 2f * originY-yEndY-xEndY*/originY - 0.5f*(originY - xEndY);

        }
    }

    private void setDirectionVectors(){
        float xlen = (float) (Math.sqrt((xEndY - originY)*(xEndY - originY) + (xEndX - originX)*(xEndX - originX)));
        float ylen = (float) (Math.sqrt((yEndY - originY)*(yEndY - originY) + (yEndX - originX)*(yEndX - originX)));
        xdirVec = new float[2];
        ydirVec = new float[2];
        xdirVec[0] =(xEndX - originX)/xlen;
        xdirVec[1] =(xEndY - originY)/xlen;

        ydirVec[0] =(yEndX - originX)/ylen;
        ydirVec[1] =(yEndY - originY)/ylen;

        if(indicate_3d){
            zdirVec = new float[2];
            float zlen = (float) (Math.sqrt((zEndY - originY)*(zEndY - originY) + (zEndX - originX)*(zEndX - originX)));
            zdirVec[0] = (zEndX - originX)/zlen;
            zdirVec[1] = (zEndY - originY)/zlen;
        }
    }

    ArrayList<Line2D> Axis;

    private void formaxis(){
        Axis = new ArrayList<>();
        Axis.add(new Line2D.Double(originX, originY, xEndX, xEndY));

        if(indicate_3d){
            Axis.add(new Line2D.Double(zEndX,zEndY, zEndX, zEndY - (originY-yEndY)));
            Axis.add(new Line2D.Double(originX, originY, zEndX, zEndY));
        }else{
            Axis.add(new Line2D.Double(originX, originY, yEndX, yEndY));
        }
    }

    ArrayList<Line2D> Tiks;
    ArrayList<String> labels;
    ArrayList<Point2D> labelLocations;

    ArrayList<Line2D> gridLines;
    private void formTiks(boolean labeled){
        gridLines = new ArrayList<>();
        Tiks = new ArrayList<>();
        if(labeled){
            labels = new ArrayList<>();
            labelLocations = new ArrayList<>();
        }

        for(int i = 0; i < Axis.size(); ++i){
            Line2D axis = Axis.get(i);
            double axlen = Math.sqrt((axis.getX1() - axis.getX2()) * (axis.getX1() - axis.getX2()) + (axis.getY1() - axis.getY2())*(axis.getY1() - axis.getY2()));
            double between = axlen/(numticks - 1);
            double startx = axis.getX1();
            double starty = axis.getY1();
            for(int j = 0; j < numticks; ++j){
                float[] dirvec = new float[2];
                if(i == 0){
                    if(indicate_3d){
                        dirvec[0] = zdirVec[0]*-1;
                        dirvec[1] = zdirVec[1]*-1;
                    }
                    else{
                        dirvec[0] = ydirVec[0]*-1;
                        dirvec[1] = ydirVec[1]*-1;
                    }
                }
                else if(i == 1){
                    if(indicate_3d){
                        dirvec[0] = zdirVec[0];
                        dirvec[1] = zdirVec[1];
                    }
                    else{
                        dirvec[0] = xdirVec[0]*-1;
                        dirvec[1] = xdirVec[1]*-1;
                    }
                }
                else if(i == 2){
                    dirvec[0] = xdirVec[0]*-1;
                    dirvec[1] = xdirVec[1]*-1;
                }
                if(labeled){
                    double labelRange = 0.0;
                    double labelmin = 0.0;
                    double betweenlabel = 0.0;
                    double offsetx = 0.0;
                    double offsety = 0.0;
                    //we need to calculate the labels as well
                    if(i == 0){
                       //we are on x axis
                       labelRange = xmax - xmin;
                       betweenlabel = labelRange/(numticks-1);
                       labelmin = xmin;
                       offsetx = indicate_3d ? -30 : -10;
                       offsety = indicate_3d ? 5 : -15;
                    }else if(i == 1){
                        //we are on the y axis
                        labelRange = ymax - ymin;
                        betweenlabel = labelRange/(numticks-1);
                        labelmin = ymin;
                        offsety = indicate_3d ? 12:5;

                    }
                    else if(i == 2){
                        //we are on the z axis
                        labelRange = zmax - zmin;
                        betweenlabel = labelRange/(numticks-1);
                        labelmin = zmin;
                        offsetx = -7;
                        offsety = 10;
                    }
                    String labelval = df.format(labelmin + j * betweenlabel);
                    labels.add(labelval);
                    labelLocations.add(new Point2D.Double( startx +50*dirvec[0] + offsetx, starty + 50*dirvec[1] + offsety));
                }
                Tiks.add(new Line2D.Double(startx, starty,startx + 10*dirvec[0], starty + 10*dirvec[1]));

                if(i == 0){
                    //we are on the x axis, add appropriate grid lines
                    if(indicate_3d){
                        Line2D zAx = Axis.get(2);
                        Line2D yAx = Axis.get(1);
                        Line2D gridZ = new Line2D.Double(startx, starty, zAx.getX2() + (startx - zAx.getX1()), zAx.getY2() + (starty - zAx.getY1()));
                        Line2D gridY = new Line2D.Double(gridZ.getX2(),  gridZ.getY2(), yAx.getX2() + (gridZ.getX2() - yAx.getX1()), yAx.getY2() + (gridZ.getY2() - yAx.getY1()));
                        gridLines.add(gridY);
                        gridLines.add(gridZ);
                    }else{
                        Line2D yAx = Axis.get(1);
                        Line2D gridY = new Line2D.Double(startx, starty, startx, yAx.getY2());
                        gridLines.add(gridY);
                    }
                    startx += xdirVec[0]  * between;
                    starty += xdirVec[1]  * between;
                }
                else if(i == 1){
                    //we are on the y axis, add appropriate grid lines
                    if(indicate_3d){
                        Line2D xAx = Axis.get(0);
                        Line2D zAx = Axis.get(2);
                        Line2D gridX = new Line2D.Double(startx, starty, xAx.getX2() + (startx - xAx.getX1()), xAx.getY2() + (starty - xAx.getY1()));
                        Line2D gridZ = new Line2D.Double(zAx.getX1() + (gridX.getX2() - zAx.getX2()),  zAx.getY1() + (gridX.getY2() - zAx.getY2()), gridX.getX2(), gridX.getY2());
                        gridLines.add(gridZ);
                        gridLines.add(gridX);
                    }else{

                        Line2D xAx = Axis.get(0);
                        Line2D gridX = new Line2D.Double(startx, starty, xAx.getX2(), starty);
                        gridLines.add(gridX);
                    }
                    startx += ydirVec[0]  * between;
                    starty += ydirVec[1]  * between;
                }
                else if(i == 2){
                    //we are on the z axis, so we must be in 3d mode, add appropriate grid lines
                    Line2D xAx = Axis.get(0);
                    Line2D yAx = Axis.get(1);
                    Line2D gridX = new Line2D.Double(startx, starty, xAx.getX2() + (startx - xAx.getX1()), xAx.getY2() + (starty - xAx.getY1()));
                    Line2D gridY = new Line2D.Double(gridX.getX2(),  gridX.getY2(), yAx.getX2() + (gridX.getX2() - yAx.getX1()), yAx.getY2() + (gridX.getY2() - yAx.getY1()));
                    gridLines.add(gridY);
                    gridLines.add(gridX);
                    startx += zdirVec[0] * between;
                    starty += zdirVec[1] * between;
                }

            }
        }
    }

    double xmin;
    double xmax;
    double ymin;
    double ymax;
    double zmin;
    double zmax;

    public void setColorscaling(ColorMode c){
        colorscaling = c;
    }
    public void setminmax(){
        xmin = getMinX();
        xmax = getMaxX();
        ymin = getMinY();
        ymax = getMaxY();
        zmin = 0;
        zmax = 0;
        if(indicate_3d){
            zmin = getMinZ();
            zmax = getMaxZ();
        }

    }

    ArrayList<Double> graphXCords;
    ArrayList<Double> graphYCords;

    ArrayList<Line2D> XYlines;
    double linxmin = Double.POSITIVE_INFINITY;
    double linxmax = Double.NEGATIVE_INFINITY;
    double linymin = Double.POSITIVE_INFINITY;
    double linymax = Double.NEGATIVE_INFINITY;
    private void convertCords() {
        XYlines = new ArrayList<>();
        Line2D XAxis = Axis.get(0);
        double lenx = Math.sqrt((XAxis.getX1() - XAxis.getX2()) * (XAxis.getX1() - XAxis.getX2()) + ((XAxis.getY2() - XAxis.getY1()) * (XAxis.getY2() - XAxis.getY1())));
        Line2D YAxis = Axis.get(1);
        double leny = Math.sqrt((YAxis.getX1() - YAxis.getX2()) * (YAxis.getX1() - YAxis.getX2()) + ((YAxis.getY2() - YAxis.getY1()) * (YAxis.getY2() - YAxis.getY1())));
        double lenz = 0.0;
        if (indicate_3d) {
            Line2D ZAxis = Axis.get(2);
            lenz = Math.sqrt((ZAxis.getX1() - ZAxis.getX2()) * (ZAxis.getX1() - ZAxis.getX2()) + ((ZAxis.getY2() - ZAxis.getY1()) * (ZAxis.getY2() - ZAxis.getY1())));
        }
        graphXCords = new ArrayList<>();
        graphYCords = new ArrayList<>();
        double prevx = 0.0;
        double prevy = 0.0;
        for (int i = 0; i < Xcord.size(); ++i) {
            double curX = (Xcord.get(i) - xmin) / (xmax - xmin) * lenx;
            double curY = (Ycord.get(i) - ymin) / (ymax - ymin) * leny;
            double Xcoord = originX + curX * xdirVec[0] + curY * ydirVec[0];
            double Ycoord = originY + curX * xdirVec[1] + curY * ydirVec[1];
            if (indicate_3d) {
                double curZ = (Zcord.get(i) - zmin) / (zmax - zmin) * lenz;
                Xcoord += curZ * zdirVec[0];
                Ycoord += curZ * zdirVec[1];
            }
            graphXCords.add(Xcoord);
            graphYCords.add(Ycoord);
            linymax = Math.max(linymax, Ycoord);
            linymin = Math.min(linymin, Ycoord);
            linxmax = Math.max(linxmax, Xcoord);
            linxmin = Math.min(linxmin, Xcoord);
            if (i > 0) {
                if (!indicate_3d || Objects.equals(Zcord.get(i), Zcord.get(i - 1))) {

                    XYlines.add(new Line2D.Double(prevx, prevy, Xcoord, Ycoord));
                }

            }
            prevx = Xcoord;
            prevy = Ycoord;

        }
    }
    public void setup() throws Exception {
        setminmax();
        setOrigin();
        setEnds();
        setDirectionVectors();
        formaxis();
        formTiks(true);
        convertCords();
    }
    private Color produceColor(double val, double min, double max){
        return new Color((float)((val - min)/(max - min))*0.95f + 0.05f, 0.01f,1.0f - ((float)((val - min)/(max - min))*0.95f));
    }
    public ColorMode colorscaling;
    protected void paintComponent(Graphics grf){

        //create instance of the Graphics to use its methods  
        super.paintComponent(grf);
        Graphics2D graph = (Graphics2D)grf;

        //Sets the value of a single preference for the rendering algorithms.  
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try {
            setup();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //draw grid lines
        graph.setColor(Color.GRAY);
        for(Line2D gridline : gridLines){
            graph.draw(gridline);
        }
        graph.setColor(Color.BLACK);
        // draw axis
        for(Line2D axis : Axis){
            graph.draw(axis);
        }

        // draw axis labels

        graph.drawString(yTitle, rm/6f, (float)Axis.get(1).getY2() - tm/3f);
        if(indicate_3d){
            graph.drawString(xTitle, (xEndX - originX)/2 + originX, originY + bm/3f);
            graph.drawString(zTitle, lm/2f, originY + bm/3f);
        }else{
            graph.drawString(xTitle, (xEndX - originX)/2 + originX, getHeight() - bm/6f);
        }


        //draw tick marks
        for (Line2D curtik : Tiks) {
            graph.draw(curtik);
        }
        for(int i = 0; i < labels.size(); ++i){
            graph.drawString(labels.get(i), (int) labelLocations.get(i).getX(), (int) labelLocations.get(i).getY());
        }

        ArrayList<Double> scalingList = null;
        double scalemin = 0;
        double scalemax = 0;
        boolean scaleexist = true;
        if(colorscaling == ColorMode.X){
            scalingList = Xcord;
            scalemin = xmin;
            scalemax = xmax;
        }
        else if(colorscaling == ColorMode.Y){
            scalingList = Ycord;
            scalemin = -ymax;
            scalemax = -ymin;
        }
        else if(colorscaling == ColorMode.Z){
            scalingList = Zcord;
            scalemin = zmin;
            scalemax = zmax;
        }else if(colorscaling == ColorMode.INDEX){
            scalemin = 0;
            scalemax = Xcord.size();

        }else{
            scaleexist = false;
        }

        for(int i=0; i<graphXCords.size(); i++){
            double x1 = graphXCords.get(i);
            double y1 = graphYCords.get(i);
            if(scaleexist){
                double val = i;
                //If the scalingList is not null then we are using an axis scale
                if(scalingList != null){
                    val = scalingList.get(i);
                    if(colorscaling == ColorMode.Y){
                        val *= -1;
                    }
                }
                graph.setColor(produceColor(val, scalemin, scalemax));

            }
            else{
                graph.setColor(Color.BLACK);
            }
            if(i == 0){
                graph.setColor(Color.RED);
            }
            graph.fill(new Ellipse2D.Double(x1-2, y1-2, 4, 4));

            if(i < XYlines.size()){
                if(scaleexist){
                    if(colorscaling == ColorMode.X){
                        double xmid = (XYlines.get(i).getX1() + XYlines.get(i).getX2())/2;
                        graph.setColor(produceColor(xmid, linxmin, linxmax));
                    }
                    else if(colorscaling == ColorMode.Y){
                        double ymid = (XYlines.get(i).getY1() + XYlines.get(i).getY2())/2;
                        graph.setColor(produceColor(ymid, linxmin, linxmax));
                    }
                }
                graph.draw(XYlines.get(i));
            }



        }
    }


    private double getMaxX(){
        double max = -Double.MAX_VALUE;
        for (double j : Xcord) {
            if (j > max)
                max = j;

        }
        return max;
    }
    private double getMaxY(){
        double max = -Double.MAX_VALUE;
        for (double j : Ycord) {
            if (j > max)
                max = j;

        }
        return max;
    }

    private double getMaxZ(){
        double max = -Double.MAX_VALUE;
        for (double j : Zcord) {
            if (j > max)
                max = j;

        }
        return max;
    }
    private double getMinX(){
        double min = Double.MAX_VALUE;
        for (double j : Xcord) {
            if (j < min)
                min = j;

        }
        return min;
    }
    private double getMinY(){
        double min = Double.MAX_VALUE;
        for (double j : Ycord) {
            if (j < min)
                min = j;

        }
        return min;
    }

    private double getMinZ(){
        double min = Double.MAX_VALUE;
        for (double j : Zcord) {
            if (j < min)
                min = j;

        }
        return min;
    }
}

enum ColorMode{
    X, Y, Z, INDEX;
}