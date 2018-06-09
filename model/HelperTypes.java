/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Objects;

/**
 *
 * @author gross
 */
public class HelperTypes {
    
    public static class ProtoNode { //Hält eine kleine Repräsentation eines Knotens in einem GraphML Graphen, die keinerlei Zeichenfunktionalität oder Adjazenzinformationen bereitstellt
        
        private final String label;
        private final String GraphMLType;
        private static int counter = 0;
        private int uniqueNodeId;

        ProtoNode(String label){
            counter++;
            this.label = label;
            this.GraphMLType = "noneByDustyn";
            this.uniqueNodeId = counter;
        }

        public TreeNode toTreeNode(){
            TreeNode node = new TreeNode(label);
            node.GraphMLType = GraphMLType;
            return node;
        }
        
        public GraphNode toGraphNode(){
            return new GraphNode(label, GraphMLType, false);
        }

        public String getLabel() {
            return label;
        }

        public String getGraphMLType() {
            return GraphMLType;
        }

        @Override
        public String toString() {
            return "id:" + uniqueNodeId + " " + this.label;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ProtoNode)) return false;

            ProtoNode protoNode = (ProtoNode) o;

            if (label != null ? !label.equals(protoNode.label) : protoNode.label != null) return false;
            return GraphMLType != null ? GraphMLType.equals(protoNode.GraphMLType) : protoNode.GraphMLType == null;
        }

        @Override
        public int hashCode() {
            int result = label != null ? label.hashCode() : 0;
            result = 31 * result + (GraphMLType != null ? GraphMLType.hashCode() : 0);
            return result;
        }
    }
    
    public static class EdgeType{
        final String id;
        final String attrType;

        public EdgeType(String id, String attrType) {
            this.id = id;
            this.attrType = attrType; 
        }
        
        public EdgeType(String id){
            this.id = id;
            attrType = null;
        }

        public String getId() {
            return id; 
        }

        @Override
        public String toString() {
            return id;
        }

        public String getAttrType() {
            return attrType; 
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final EdgeType other = (EdgeType) obj;
            if (!Objects.equals(this.id, other.id)) {
                return false;
            }
            //if (!Objects.equals(this.attrType, other.attrType)) {return false;}
            return true;
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 19 * hash + Objects.hashCode(this.id);
            hash = 19 * hash + Objects.hashCode(this.attrType);
            return hash;
        }
    
    }
        
}
