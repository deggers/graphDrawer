package draw;

import model.DrawableGraph;

/*  PHASE 1:
Step 1: M*= M0, K* = K(M0)
Step 2: M1= bR(M0)  = reorder rows as initial operation
STep 3: if K(M1) < K* then M*= M1 and K*= K(M1)
Step 4: M2= bC(M1)   = reorder columns
Step 5: If K(M2) <K* then M*= M2 and K*= K(M2)
step 6: if M0 and M2 equal OR # of iterations in Phase 1 attains an initially given number, Phase 1 STOPPED, goto 7, else goto Step 2

 */

// problem: wiederholungen, zugriff auf die jeweiligen matrizen und benennung der matrizen???

public class Barycenter {
    private DrawableGraph drawableGraph;
    private BarycenterMatrix M0 = new BarycenterMatrix(drawableGraph, 0);
    private BarycenterMatrix MStar = M0; // equals M*, solution matrix
    private int crossing = 0, crossingsStar = M0.getCrossings(); // equals K and K*
    private int iterations1=0, iterations2=0;

    public Barycenter(DrawableGraph drawableGraph) {
        this.drawableGraph = drawableGraph;
    }

    public void phase1() {  // problem: namenskonflikte!!!!!
        iterations1 ++;
        BarycenterMatrix M1 = M0.orderByRow();      // Step 2

        if (M1.getCrossings() < crossingsStar) {    // Step 3
            MStar = M1;
            crossingsStar = M1.getCrossings();
        }

        BarycenterMatrix M2 = M1.orderByColumn();   // Step 4

        if( M2.getCrossings() < crossingsStar){     // Step 5
            MStar= M2;
            crossingsStar= M2.getCrossings();
        }

        if( M0.equals(M2) || iterations1 >10){ // anzahl iterations sinnvolle größe wählen als abbruchkriterim
            phase2();
        } else {
            phase1();
        }
    }

    public void phase2(){
        iterations2++;
      //  BarycenterMatrix M3= M2.orderByRow();   // Step 7
        // Step 8:
        // if barycenters of cols of M3 are nor arranged in an increasing order, go to Step 11 with M0= M3
        // else goto Step 9

        //BarycenterMatrix M4= M3.orderByColumn();    // Step 9

        //Step 10:
        // if the bary of M4 are nor arranged in an increasing order, goto step 11 with M0= M4
        // else terminate calc

        if (iterations2 >10) { }        //Step 11:if the number of iterations in phase 2 attains limit terminate
        else{
            phase1();
        }

    }



}
