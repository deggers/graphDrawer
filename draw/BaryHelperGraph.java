package draw;

import draw.Bary.BaryMatrix;
import model.GraphNode;

import java.util.*;

class BaryHelperGraph {
    private int crossingsTotal = 0;
    private LinkedHashMap<Integer, LinkedList<GraphNode>> layerMap; // key layer, entry nodes in corr order
    private Map<Integer, BaryMatrix> baryMat=new HashMap<>();  // position in array= level of rows

    void calcCrossingsTotal() {
        for (int i = 1; i <= baryMat.size(); i++) {
            if(i==1){
                crossingsTotal=0;
            }
            crossingsTotal += baryMat.get(i).getMatCrossings();
        }
    }

    void setBaryMat(Integer i, BaryMatrix baryMat) {
        this.baryMat.put(i, baryMat);
    }
    BaryMatrix getBaryMatOnLevel(Integer level){
        return this.baryMat.get(level);
    }

    void setLayerMap(LinkedHashMap<Integer, LinkedList<GraphNode>> layerMap){
        this.layerMap= layerMap;
    }

    void setLayerInMap(Integer i, LinkedList<GraphNode> list){
        this.layerMap.put(i,list );
    }

    LinkedHashMap<Integer, LinkedList<GraphNode>> getLayerMap() {
        return layerMap;
    }

    int getCrossingsTotal(){
        return this.crossingsTotal;
    }

    BaryHelperGraph copy(){
        BaryHelperGraph other = new BaryHelperGraph();
        other.layerMap = new LinkedHashMap<>(this.layerMap);
        other.baryMat= new HashMap<>(this.baryMat);
        other.crossingsTotal = this.crossingsTotal;
        return other;
    }
}
