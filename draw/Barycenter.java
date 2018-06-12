package draw;

import model.DrawableGraph;

public class Barycenter {
    private DrawableGraph drawableGraph;
    private int crossing = 0, crossingsNew=0;

    public Barycenter(DrawableGraph drawableGraph) {
        this.drawableGraph = drawableGraph;
    }

    // for all levels: compare with adjacent level
    BarycenterMatrix M0 = new BarycenterMatrix(drawableGraph, 0);

    BarycenterMatrix MNew = M0;


    // method calcCrossings(Ba
}
