package Sugiyama;

import structure.Edge;
import structure.Graph;
import structure.GraphNode;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class PromoteLayer {
    private LinkedList<HelperNode> layering = new LinkedList<>();
    private LinkedList<HelperNode> layeringBackUp = new LinkedList<>();

    public static void promoteLayerAlgo(Graph graph) {
        PromoteLayer p = new PromoteLayer(graph);
    }

    public PromoteLayer(Graph graph) {

        fillLayering(graph);
        layeringBackUp = copyLayering(layering);

        plAlgorithm();

        LinkedHashMap<Integer, LinkedList<GraphNode>> layersAndNodesNew = new LinkedHashMap<>();

        for (HelperNode helperNode : layering) {
            LinkedList<GraphNode> tempList = layersAndNodesNew.get(helperNode.layer);
            tempList.add(helperNode.reference);
            layersAndNodesNew.put(helperNode.layer, tempList);
        }

        graph.setLayerMap(layersAndNodesNew);
    }

    public void fillLayering(Graph graph) {
        for (Integer integer : graph.getLayerMap().keySet()) {
            for (GraphNode g : graph.getLayerMap().get(integer)) {
                HelperNode temp = new HelperNode(g);
                layering.add(temp);

                // pretendeing that all edges go from top to bottom, immer checken ob schon contains damit keine duplikate?
                for (Edge edge : graph.getEdges()) {
                    if (edge.getStart().equals(g)) {
                        // aufpassen mit den levels von oben nach unten oder oben nach unten? richtung unwichtig
                        if (edge.getStart().getLayer() == edge.getTarget().getLayer() - 1) {
                            HelperNode suc = new HelperNode(edge.getTarget());
                            if (!(temp.successors.contains(suc))) {
                                temp.successors.add(suc);
                                temp.outDegree += 1;
                            }
                        } else if (edge.getStart().getLayer() == edge.getTarget().getLayer() + 1) {
                            HelperNode pred = new HelperNode(edge.getTarget());
                            if (!(temp.predecessors.contains(pred))) {
                                temp.predecessors.add(new HelperNode(edge.getTarget()));
                                temp.inDegree += 1;
                            }
                        }
                    }
                    if (edge.getTarget().equals(g)) {
                        if (edge.getTarget().getLayer() == edge.getStart().getLayer() + 1) {
                            HelperNode pred = new HelperNode(edge.getStart());
                            if (!(temp.predecessors.contains(pred))) {
                                temp.predecessors.add(pred);
                                temp.inDegree += 1;
                            }
                        } else if (edge.getTarget().getLayer() == edge.getStart().getLayer() - 1) {
                            HelperNode suc = new HelperNode(edge.getTarget());
                            if (!(temp.successors.contains(suc))) {
                                temp.successors.add(suc);
                                temp.outDegree += 1;
                            }
                        }
                    }
                }
            }
        }
        for (HelperNode h : layering) {
            h.calcDummyDiff(h);
        }
    }

    public void plAlgorithm() {
        int promotions;
        do {
            promotions = 0;
            for (HelperNode helperNode : layering) {
                if (helperNode.inDegree > 0) {                // if d- von v   >0
                    if (promoteNode(helperNode) < 0) {         //if PromoteNode(v) < 0 then
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


    // evtl kompikationen, da hier graphen von unten nach oben, nicht von oben nach unten?
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

    // level beginnenn bei uns bei 1 nicht bei 0
    // aufpassen wenn suc / pre erstellen-- pre liegen drüber- level-1, suc drunter lev+1
    // wie sind graphnodes eindeutig referenzierbar? über label und nodetype?
    public class HelperNode {
        GraphNode reference;
        int layer;
        boolean isDummyNode = false;
        LinkedList<HelperNode> successors = new LinkedList<>();  //successors immediate successors,
        LinkedList<HelperNode> predecessors = new LinkedList<>();  // predecessors immediate predecessors : u1, u2...up1 ein layer drüber---vorgänger
        int inDegree = 0, outDegree = 0;
        int dummyDiff = 0;

        public HelperNode(GraphNode node) {
            this.reference = node;
            this.isDummyNode = node.isDummy();
            this.layer = node.getLayer();
        }

        public int calcDummyDiff(HelperNode helperNode) {
            int sum = (helperNode.successors.size() - helperNode.predecessors.size());

            for (int i = 0; i < helperNode.predecessors.size(); i++) {
                if (!(helperNode.predecessors.get(i).isDummyNode)) {
                    sum += calcDummyDiff(helperNode.predecessors.get(i));
                }
            }
            return sum;
        }

    }
}

