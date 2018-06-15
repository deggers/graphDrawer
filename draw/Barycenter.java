package draw;

import java.util.ArrayList;
import java.util.List;

/*  PHASE 1:
Step 1: M*= M0, K* = K(M0)
Step 2: M1= bR(M0)  = reorder rows as initial operation
STep 3: if K(M1) < K* then M*= M1 and K*= K(M1)
Step 4: M2= bC(M1)   = reorder columns
Step 5: If K(M2) <K* then M*= M2 and K*= K(M2)
step 6: if M0 and M2 equal OR # of iterations in Phase 1 attains an initially given number, Phase 1 STOPPED, goto 7, else goto Step 2
 */

public class Barycenter {
    private model.Graph graph;
    private List<BarycenterMatrix> matrices = new ArrayList<>();
    private BarycenterMatrix M0 = new BarycenterMatrix(graph, 0);
    private BarycenterMatrix MStar = M0, Mtemp = M0; // MStar equals M*, solution matrix
    private int crossing = 0, MinCrossings = M0.getCrossings(); // equals K and K*
    private int iterations1 = 0, iterations2 = 0;

    public Barycenter(model.Graph graph) {
        this.graph = graph;
    }

    public void phase1() {
        iterations1++;
        matrices.add(Mtemp.orderByRow());              // Step 2: M1= M0.orderByRow

        Mtemp = matrices.get((matrices.size() - 1));   // Mtemp= M1

        if (Mtemp.getCrossings() < MinCrossings) {    // Step 3
            MStar = Mtemp;
            MinCrossings = Mtemp.getCrossings();
        }

        matrices.add(Mtemp.orderByColumn());        // Step 4: M2 = M1.orderByColumn();
        Mtemp = matrices.get((matrices.size() - 1));   // Mtemp= M2;

        if (Mtemp.getCrossings() < MinCrossings) {     // Step 5
            MStar = Mtemp;
            MinCrossings = Mtemp.getCrossings();
        }

        if (M0.equals(Mtemp) || iterations1 > 10) { // anzahl iterations sinnvolle größe wählen als abbruchkriterim
            phase2();
        } else {
            phase1();
        }
    }

    public void phase2() {
        iterations2++;

        matrices.add(Mtemp.reverseRows());              // Step 7  M3= M2 reverse rows with equal bary  reversion
        Mtemp = matrices.get((matrices.size() - 1));   // Mtemp= M3;

        if (!Mtemp.columnsAreIncreasing()) {           // Step 8:
            if (iterations2 > 10) {  // terminate
            } else {
                phase1();                               // go to step 2 with M0= M3
            }
        }

        matrices.add(Mtemp.reverseColumns());           // Step 9 M4= M3 reverse col with equal bary
        Mtemp = matrices.get((matrices.size() - 1));

        if (!Mtemp.rowsAreIncreasing()) {                // Step 10
            if (iterations2 > 10) {   // terminate
            } else {
                phase1();                               // go to step 2 with M0= M4
            }
        }
    }


}
