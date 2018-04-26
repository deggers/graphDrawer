import java.util.List;

public class NaiveAlgorithm implements Drawing {
    Node node;
    MutableTree returnTree;
    int level = 0;
    private List getRoots;

    @Override
    public MutableTree calculateCoordinates(MutableTree tree) {
        System.out.println("I am really calculating Naives-Coordinates");
        getRoots = tree.getRoots();
        System.out.println("getRoots = " + getRoots);

        return returnTree;
    }
}
