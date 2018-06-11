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

        //public Class<?> getAttrClass() throws ClassNotFoundException {
        //    return Class.forName(attrType);
        //} wahrscheinlich braucht keiner reflection

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
