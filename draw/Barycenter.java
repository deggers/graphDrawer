package draw;

import model.Graph;
import model.GraphNode;

import java.util.*;


public class Barycenter {
    private BarycenterMatrix m0;
    private BarycenterMatrix mStar, mTemp;                                   // mStar equals M*, solution matrix
    private int iterations = 0;
    private int minCrossings = 10000;

    public static void barycenterAlgo(Graph graph) {
        Barycenter b = new Barycenter(graph);
    }

    public Barycenter(Graph graph) {
        int graphDepth = 1;
        for (GraphNode n : graph.getNodes()) {
            if (n.getLayer() > graphDepth) {
                graphDepth = n.getLayer();
            }
        }
        for (int layers = 1; layers < graphDepth; layers++) {             // layers start at 1, < graphDepth, because matrix always level i and i+1
            System.out.println("starting down for layer: " + layers);
            minCrossings = Integer.MAX_VALUE;
            m0 = new BarycenterMatrix(graph, layers, "down");
            mStar = m0.copy();
            mTemp = m0.copy();
            minCrossings = m0.getCrossings();
            System.out.println("layers = " + layers + ", min cross = " + minCrossings);
            if (minCrossings != 0) {
                iterations = 0;
                phase1();
            }
            // hier auch noch die layer map ändern
          graph.getLayerMap().put(layers, mStar.getRows());

            for (GraphNode gn : graph.getNodes()) {
                for (GraphNode g : mStar.getRows()) {
                    if (gn.equals(g) && gn.y == mStar.getColumns().get(0).y) {
                        gn.x = g.x;
                    }
                }
            }
            System.out.println("new min cross = " + minCrossings);
        }

        for (int layers = graphDepth; layers > 1; layers--) {
            System.out.println("starting up for layer: " + layers);
            minCrossings = Integer.MAX_VALUE;
            m0 = new BarycenterMatrix(graph, layers, "up");
            mStar = m0.copy();
            mTemp = m0.copy();
            minCrossings = m0.getCrossings();
            System.out.println("layers = " + layers + ", min cross = " + minCrossings);
            if (minCrossings != 0) {
                iterations = 0;
                phase1();
            }
            for (GraphNode gn : graph.getNodes()) {
                for (GraphNode g : mStar.getRows()) {
                    if (gn.equals(g) && gn.y == mStar.getColumns().get(0).y) {
                        gn.x = g.x;
                    }
                }
            }
            graph.getLayerMap().put(layers, mStar.getRows());

            System.out.println("new min cross = " + minCrossings);

        }            //

/*        for(Map.Entry<Integer, LinkedList<GraphNode>> entry : graph.getLayerMap().entrySet()) {
            System.out.println(" on layer: " + entry.getKey());
            for (GraphNode graphNode : entry.getValue()) {
                System.out.println(graphNode.getLabel() + ": " + graphNode.x);
            }
        }*/
        // stattdessen die layer map ändern, also die reihenfolge der knoten
        // graph.getLayerMap().put
    }

    private void phase1() {
        if (iterations < 1000) {
            iterations++;         //System.out.println("iterations1 = " + iterations1);
            mTemp.orderByRow();
            if (mTemp.getCrossings() < minCrossings) {    // Step 3
                mStar = mTemp.copy();
                minCrossings = mTemp.getCrossings();
                System.out.println("changed minCrossings to = " + minCrossings);
                System.out.println("mTemp col= " + mTemp.getColumns()+ " "+ mTemp.getColumns());
                System.out.println("mTemp row = " + mTemp.getRows());

            }

            mTemp.orderByColumn();
            if (mTemp.getCrossings() < minCrossings) {     // Step 5
                mStar = mTemp.copy();
                minCrossings = mTemp.getCrossings();
                System.out.println("changed minCrossings to = " + minCrossings);
            }

            if (m0.equals(mTemp)) { // anzahl iterations sinnvolle größe wählen als abbruchkriterim
                // auf periodisches auftreten prüfen-- klappt noch nicht???? !!!
                phase2();
            } else {
                phase1();
            }
        }
    }

    private void phase2() {
        mTemp.reverseRows();

        if (!mTemp.columnsAreIncreasing()) {           // Step 8:
            phase1();
        }

        mTemp.reverseColumns();
        if (!mTemp.rowsAreIncreasing()) {                // Step 10
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