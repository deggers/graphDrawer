package Sugiyama;

import structure.Graph;
import structure.GraphNode;

import java.util.LinkedList;

public class PromoteLayer {
    private LinkedList<HelperNode> layering = new LinkedList<>();
    private LinkedList<HelperNode> layeringBackUp = new LinkedList<>();
//    private LinkedHashMap<Integer, LinkedList<GraphNode>> layeringOriginal, layeringTemp;

    public PromoteLayer(Graph graph) {
        //layeringOriginal = graph.getLayerMap();         //layeringTemp = graph.getLayerMap();
        for (Integer integer : graph.getLayerMap().keySet()) {
            for (GraphNode g : graph.getLayerMap().get(integer)) {
                layering.add(new HelperNode(g));
            }
        }
        layeringBackUp = copyLayering(layering);
        plAlgorithm();
    }

    public void plAlgorithm() {
        int promotions;
        do {
            promotions = 0;
            for (HelperNode helperNode : layering) {
                if (helperNode.inDegree > 0) {                // if d- von v   >0
                    if (promoteNode(helperNode) < 0) {     //if PromoteNode(v) < 0 then
                        promotions++;
                        //muss das zuerst gecleart werden oder hinfällig?
                        layeringBackUp.clear();
                        layeringBackUp = copyLayering(layering);
                    } else {
                        layering.clear();
                        layering = copyLayering(layeringBackUp);
                    }
                }
            }
        } while (promotions != 0);
    }


    // problem, da hier graphen von unten nach oben, nicht von oben nach unten?
    public int promoteNode(HelperNode v) {
        v.dummyDiff = 0;
        for (HelperNode u : v.predecessors) {
            if (u.layer == (v.layer - 1)) {                     //eigentlich +1
                v.dummyDiff += promoteNode(u);
            }
            v.layer -= 1;                                       // eigentlich +1
            v.dummyDiff = v.dummyDiff - v.predecessors.size() + v.successors.size();
        }
        return v.dummyDiff;
    }

    public LinkedList<HelperNode> copyLayering(LinkedList<HelperNode> layering) {
        LinkedList<HelperNode> copy = new LinkedList<>(layering);
        return copy;
    }


    // wie sind graphnodes eindeutig referenzierbar? über label und nodetype?
    public class HelperNode {
        GraphNode reference;
        int layer;
        boolean isDummyNode = false;
        LinkedList<HelperNode> successors;  //successors immediate successors,
        LinkedList<HelperNode> predecessors;  // predecessors immediate predecessors : u1, u2...up1 ein layer drüber---vorgänger
        int inDegree, outDegree;
        int dummyDiff = calcDummyDiff(this);

        public HelperNode(GraphNode node) {
            this.reference = node;
            this.isDummyNode = node.isDummy();
            this.layer = node.getLayer();
            calcSucPre(this);
        }


        // level beginnenn bei uns bei 1 nicht bei 0
        // aufpassen wenn suc / pre erstellen-- pre liegen drüber- level-1, suc drunter lev+1
        private void calcSucPre(HelperNode helperNode) {


        }

        public int calcDummyDiff(HelperNode helperNode) {
            int sum = successors.size() - predecessors.size();

            for (int i = 0; i < predecessors.size(); i++) {
                if (!predecessors.get(i).isDummyNode) {
                    sum += calcDummyDiff(predecessors.get(i));
                }
            }
            return sum;
        }


    }


}
