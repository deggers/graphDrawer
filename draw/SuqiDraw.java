package draw;

import java.util.HashSet;
import java.util.Iterator;
import model.Edge;
import model.GraphMLGraph;
import model.GraphNode;
import model.DrawableGraph;
import model.HelperTypes.*;

public class SuqiDraw {
    
    private static boolean verbose = false;
    private static HashSet<Edge> turnedEdges; //in their turned form, hence start and target must be interchanged before drawing
    
    public static DrawableGraph processGraph(GraphMLGraph theGraph, String edgeType){
        DrawableGraph g;
        turnedEdges = new HashSet<>();
        try {
            g = new DrawableGraph(theGraph, edgeType); //gibt Teilgraph mit allen relevanten Kanten und nicht-solitären Knoten aus, Adjazenzinformation in parent/children Format
        } catch (Exception ex) {
            return null;
        }
        //Azyklisieren
//        DFS_Florian(g);
        //Layering
        
        //DummyKnoten
        
        //Kantenschnitte
        
        //Koordinaten(Brandes-Köpf)
        
        //fertig
        return g;
    }

    
}
