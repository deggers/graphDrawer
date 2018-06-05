package draw;

import java.util.HashSet;
import java.util.Iterator;
import model.Edge;
import model.GraphMLGraph;
import model.GraphNode;
import model.drawableGraph;
import model.HelperTypes.*;

public class SuqiDraw {
    
    private static boolean verbose = false;
    private static HashSet<Edge> turnedEdges; //in their turned form, hence start and target must be interchanged before drawing
    
    public static drawableGraph processGraph(GraphMLGraph theGraph, String edgeType){
        drawableGraph g;
        turnedEdges = new HashSet<>();
        try {
            g = new drawableGraph(theGraph, edgeType); //gibt Teilgraph mit allen relevanten Kanten und nicht-solitären Knoten aus, Adjazenzinformation in parent/children Format
        } catch (Exception ex) {
            return null;
        }
        //Azyklisieren
//        dfsCycleBreaker(g);
        //Layering
        
        //DummyKnoten
        
        //Kantenschnitte
        
        //Koordinaten(Brandes-Köpf)
        
        //fertig
        return g;
    }

    
}
