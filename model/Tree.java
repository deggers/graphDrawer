package model;

import java.io.Serializable;
import java.util.List;

// necessary ?

public interface Tree<T> extends Serializable {
    public List<T> getRoots();
    public T getParent(T node);
    public List<T> getChildren(T node);
}
