interface MutableTree <T> extends Tree<T>  {
    public boolean add (T parent, T node);
    public boolean remove (T node, boolean cascade);
}