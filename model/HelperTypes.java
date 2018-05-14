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
class HelperTypes {
    
    protected static class protoNode { //Hält eine kleine Repräsentation eines Knotens in einem GraphML Graphen, die keinerlei Zeichenfunktionalität oder Adjazenzinformationen bereitstellt
        
        private final String label;
        private final String GraphMLType;

        public protoNode(String label) {
            this.label = label;
            GraphMLType = null;
        }

        public protoNode(String label, String GraphMLType) {
            this.label = label;
            this.GraphMLType = GraphMLType;
        }
        
        public Node toNode(){
            Node node = new Node(label);
            node.GraphMLType = GraphMLType;
            return node;
        }

        public String getLabel() {
            return label;
        }

        public String getGraphMLType() {
            return GraphMLType;
        }
    }
    
    protected static class EdgeType{ //Hält einen Kantentyp nach GraphML Spezifikation mit ID und Art des folgenden Attributfeldes (normalerweise double)
    final String id;
    final String attrType;
    
    public EdgeType(String id, String attrType) {
        this.id = id;
        this.attrType = attrType; 
    }
    
    public String getId() {
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
        if (!Objects.equals(this.attrType, other.attrType)) {
            return false;
        }
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
