package draw;

import model.GraphMLGraph;
import model.Node;
import model.Tree;

import java.util.Arrays;
import java.util.List;

public class RandomAlgo {

    public static GraphMLGraph processGraph(GraphMLGraph theGraph) {
        System.out.println("i am working on RandomGraph :)");
        List<Node> rootNodes = theGraph.getRoots();
        System.out.println("rootNodes = " + Arrays.toString(rootNodes.toArray()));
        return null;
    }

    public static Tree processTree(Tree theTree) {

        return null;
    }
}
