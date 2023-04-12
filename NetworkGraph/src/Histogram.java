import java.util.ArrayList;

public class Histogram {
    ArrayList<Bin> bins;
    int numBins;
    double min;
    double max;
    public Histogram(ArrayList<Double> data, int binCount){
        bins = new ArrayList<>();
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for(double i : data){
            min = Math.min(min, i);
            max = Math.max(max, i);
        }
        //Now we form the bins
        double step = (max - min)/binCount;
        //Note, we will add a bin at the beginning and at the end for floating point safety. This should not
        //affect distance computation by too much.
        Bin firstBin = new Bin(min - step, min);
        this.min = min-step;

        for(int i = 0 ; i < binCount; ++i){
            Bin nextBin = new Bin(min + i * step, min + (i+1)*step);
            bins.add(nextBin);
        }

        Bin lastBin = new Bin(max, max +step);
        this.max = max + step;

        //Now, add the data to the bins
        for(double i : data){
            int index = 0;
            while(i < bins.get(index).low_bound){
                ++index;
            }
            ++bins.get(index).count;
        }
        this.numBins = binCount;
    }
    public void incByData(double val) throws Exception{
        if(val < min || val > max){
            throw new Exception("This value is not in range of this histogram");
        }
        int index = 0;
        while(val < bins.get(index).low_bound){
            ++index;
        }
        ++bins.get(index).count;
    }
    public void reformBinnedData(ArrayList<Double> data, int numBins){
        bins = new ArrayList<>();
        double  min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for(double i : data){
            min = Math.min(min, i);
            max = Math.max(max, i);
        }
        //Now we form the bins
        double step = (max - min)/numBins;
        //Note, we will add a bin at the beginning and at the end for floating point safety. This should not
        //affect distance computation by too much.
        Bin firstBin = new Bin(min - step, min);
        this.min = min-step;

        for(int i = 0 ; i < numBins; ++i){
            Bin nextBin = new Bin(min + i * step, min + (i+1)*step);
            bins.add(nextBin);
        }

        Bin lastBin = new Bin(max, max +step);
        this.max = max + step;

        //Now, add the data to the bins
        for(double i : data){
            int index = 0;
            while(i < bins.get(index).low_bound){
                ++index;
            }
            ++bins.get(index).count;
        }
        this.numBins = numBins;
    }


}
class Bin implements Comparable<Bin>{
    double low_bound;
    double high_bound;
    int count;

    public Bin(double l, double h){
        count = 0;
        low_bound = l;
        high_bound = h;
    }

    @Override
    public int compareTo(Bin o) {
        return Double.compare(low_bound,o.low_bound);
    }
}