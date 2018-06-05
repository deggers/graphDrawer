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
        dfsCycleBreaker(g);
        //Layering
        
        //DummyKnoten
        
        //Kantenschnitte
        
        //Koordinaten(Brandes-Köpf)
        
        //fertig
        return g;
    }

    private static void dfsCycleBreaker(drawableGraph g) {
        //Tiefensuche um Zyklen zu entfernen
        Iterator<GraphNode> iter = g.getNodes().iterator();
        while (iter.hasNext()){
            GraphNode startNode = iter.next();
            if (startNode.getDfsStatus()=='u') {
                dfsRec(startNode);
            }
        }
    }
    private static void dfsRec(GraphNode node){
        if (node.isLeaf()) {
            node.setDfsStatus('f');
        } else {
            node.setDfsStatus('v');
            for (GraphNode graphNode : node.getChildren()) {
                if (graphNode.getDfsStatus()=='v') {
                    if (verbose==true) {
                        System.out.printf("Cycle found, turning edge from %s to %s \n", node.label, graphNode.label);
                    }
                    graphNode.removeParent(node);
                    graphNode.addChild(node);
                    node.removeChild(graphNode);
                    node.addParent(graphNode);
                    turnedEdges.add(new Edge(new protoNode(graphNode.label), new protoNode(node.label)));
                } else {
                    if (graphNode.getDfsStatus()=='u') {
                        dfsRec(graphNode);
                    } else {
                        //Node has been finalized, nothing to do
                    }
                }

            }
            node.setDfsStatus('f');
        }
        
    }
    
}
