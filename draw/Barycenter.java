package draw;

import model.Graph;
import model.GraphNode;

import java.util.ArrayList;
import java.util.List;


public class Barycenter {
    private Graph graph;
    private BarycenterMatrix m0;
    private BarycenterMatrix mStar, mTemp;                                   // mStar equals M*, solution matrix
    private int iterations1 = 0, iterations2 = 0, minCrossings=10000, graphDepth;             // equals K and K*


    public static Graph barycenterAlgo(Graph graph){
        Barycenter b= new Barycenter(graph);
        return graph;
    }

    public Barycenter(Graph graph) {
        this.graph= graph;
        graphDepth = 1;
        for (GraphNode n : graph.getNodes()) {
            if (n.getLayer() > graphDepth) {
                graphDepth = n.getLayer();
            }
        }
        for (int layers = 1; layers < graphDepth; layers++) {             // layers start at 1, < graphDepth, because matrix always level i and i+1
            //System.out.println("starting down for layer: "+ layers);
            m0 = new BarycenterMatrix(graph, layers, "down");
            mStar = m0;
            mTemp = m0;
            minCrossings = m0.getCrossings();
            //System.out.println("minCrossings in beginning = " + minCrossings);
            iterations1=0;
            phase1();
        }            //System.out.println("down finished");

        for(int layers=graphDepth; layers>1; layers--){
            //System.out.println("starting up for layer: "+ layers);
            m0 = new BarycenterMatrix(graph, layers, "up");
            mStar = m0;
            mTemp = m0;
            minCrossings = m0.getCrossings();
            //System.out.println("minCrossings in beginning = " + minCrossings);
            iterations1=0;
            phase1();
        }            //System.out.println("up finished");

    }

    private void phase1() {
        iterations1++;
        //System.out.println("iterations1 = " + iterations1);
        mTemp.orderByRow();
        //System.out.println("mTemp crossings = " + mTemp.getCrossings() + " min cross = "+ minCrossings);
        if (mTemp.getCrossings() < minCrossings) {    // Step 3
            mStar = mTemp;
            minCrossings = mTemp.getCrossings();
            //System.out.println("new minCrossings = " + minCrossings);
        }

        mTemp.orderByColumn();

        if (mTemp.getCrossings() < minCrossings) {     // Step 5
            mStar = mTemp;
            minCrossings = mTemp.getCrossings();
        }

        if (m0.equals(mTemp) || iterations1 > 10) { // anzahl iterations sinnvolle größe wählen als abbruchkriterim
            phase2();
        } else {
            phase1();
        }
    }

    private void phase2() {
        iterations2++;
        //System.out.println("iterations2 = " + iterations2);
        mTemp.reverseRows();

        if (!mTemp.columnsAreIncreasing()) {           // Step 8:
            step11();                                      // go to step 2 with m0= M3
        } else{
            mTemp.reverseColumns();

            if (!mTemp.rowsAreIncreasing()) {                // Step 10
                step11();                                       // go to step 2 with m0= M4
            }
        }
    }

    private void step11(){
        if(iterations2<10){
            phase1();
        }
    }

}

/*  PHASE 1:
Step 1: M*= m0, K* = K(m0)
Step 2: M1= bR(m0)  = reorder rows as initial operation
STep 3: if K(M1) < K* then M*= M1 and K*= K(M1)
Step 4: M2= bC(M1)   = reorder columns
Step 5: If K(M2) <K* then M*= M2 and K*= K(M2)
step 6: if m0 and M2 equal OR # of iterations in Phase 1 attains an initially given number, Phase 1 STOPPED, goto 7, else goto Step 2
 */