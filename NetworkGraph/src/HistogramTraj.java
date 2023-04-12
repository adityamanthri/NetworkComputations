public class HistogramTraj {
    HistoNode cur;
    HistoNode head;
    HistoNode tail;
    int size;

    double globalmin;
    double globalmax;

    int maxbin;
    public HistogramTraj(Histogram h){
        head = new HistoNode(h);
        cur = head;
        tail = head;
        size = 1;
        globalmax = h.max;
        globalmin = h.min;
        int bmax = 0;
        for(Bin b : h.bins){
            bmax = Math.max(b.count, bmax);
        }
    }
    public void addHisto(Histogram h){
        if(size == 1){
            tail = new HistoNode(h);
            head.next = tail;
            tail.prev = head;
            cur.next = tail;
        }
        else{
            HistoNode next = new HistoNode(h);
            tail.next = next;
            next.prev = tail;
            tail = next;
        }
        globalmin = Math.min(globalmin, h.min);
        globalmax = Math.max(globalmax, h.max);
        for(Bin b : h.bins){
            maxbin = Math.max(maxbin, b.count);
        }
        ++size;
    }
    public void toNext(){
        if(cur.next != null){
            cur = cur.next;
        }
    }
    public void toPrev(){
        if(cur.prev != null){
            cur = cur.prev;
        }
    }

}
class HistoNode{
    Histogram h;
    HistoNode next;
    HistoNode prev;
    public HistoNode(Histogram h){
        this.h = h;
        next = null;
        prev = null;
    }
}