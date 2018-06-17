package draw;

import model.GraphNode;

import java.util.ArrayList;
import java.util.List;


public class Barycenter {
    private List<BarycenterMatrix> matrices = new ArrayList<>();
    private BarycenterMatrix m0;
    // ?????? für was braucht man die beste matrix m* ?
    private BarycenterMatrix mStar, mTemp;                                   // mStar equals M*, solution matrix
    private int iterations1 = 0, iterations2 = 0, minCrossings;             // equals K and K*

    public Barycenter(model.DrawableGraph graph) {
        int graphDepth = 0;
        for (GraphNode n : graph.copyNodeSet()) {
            if (n.getLayer() > graphDepth) {
                graphDepth = n.getLayer();
            }
        }
        for (int layers = 0; layers < graphDepth; layers++) {             // < graphDepth, because matrix always level i and i+1
            m0 = new BarycenterMatrix(graph, layers);
            mStar = m0;
            mTemp = m0;
            minCrossings = m0.getCrossings();
            phase1();
        }
        for(int layers=graphDepth; layers>1; layers--){
            m0 = new BarycenterMatrix(graph, layers);
            mStar = m0;
            mTemp = m0;
            minCrossings = m0.getCrossings();
            phase1();
        }
    }

    private void phase1() {
        iterations1++;
        mTemp.orderByRow();
        matrices.add(mTemp);              // Step 2: M1= m0.orderByRow

        mTemp = matrices.get((matrices.size() - 1));   // mTemp= M1

        if (mTemp.getCrossings() < minCrossings) {    // Step 3
            mStar = mTemp;
            minCrossings = mTemp.getCrossings();
        }

        mTemp.orderByColumn();
        matrices.add(mTemp);        // Step 4: M2 = M1.orderByColumn();
        mTemp = matrices.get((matrices.size() - 1));   // mTemp= M2;

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

        mTemp.reverseRows();
        matrices.add(mTemp);                             // Step 7  M3= M2 reverse rows with equal bary  reversion
        mTemp = matrices.get((matrices.size() - 1));   // mTemp= M3;

        if (!mTemp.columnsAreIncreasing()) {           // Step 8:
            step11();                                      // go to step 2 with m0= M3
        }

        mTemp.reverseColumns();
        matrices.add(mTemp);                             // Step 9 M4= M3 reverse col with equal bary
        mTemp = matrices.get((matrices.size() - 1));

        if (!mTemp.rowsAreIncreasing()) {                // Step 10
           step11();                                       // go to step 2 with m0= M4
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
