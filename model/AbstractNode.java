package model;

import java.io.Serializable;

public abstract class AbstractNode implements Serializable {
    
    public final String label;
    String GraphMLType;
    //walker
    public double x = 0;
    public double y = 0;
    public int level= 0;  // not used? confusing with layer.. renamed to layer? but then initial value to -1 ? --dustyn

    public AbstractNode(String label) {
        this.label = label;
    }
    
    public abstract boolean isLeaf();

    @Override
    public abstract String toString();

}
