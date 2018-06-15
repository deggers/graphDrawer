package model;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.*;
import java.io.File;
import java.io.FileReader;
import java.time.Duration;
import java.util.*;

public class GraphMLParser {
    private static final boolean verbose = true;

    @SuppressWarnings("ConstantConditions")
    public static Graph parseFileToGraph(File file) {

        long startTime = System.nanoTime();
        long stopTime;

        Edge edge = new Edge();
        LinkedList<HelperTypes.EdgeType> edgeTypes = new LinkedList<>();

        LinkedHashSet<GraphNode> nodes = new LinkedHashSet<>();
        LinkedHashSet<Edge> edges = new LinkedHashSet<>();
        HashMap<String, GraphNode> nodeHashmap = new HashMap<>();

        try {
            XMLEventReader reader;
            reader = XMLInputFactory.newInstance().createXMLEventReader(new FileReader(file));

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                switch (event.getEventType()) {

                    case XMLStreamConstants.START_ELEMENT:
                        StartElement startElement = event.asStartElement();
                        String sName = startElement.getName().getLocalPart();
                        Iterator attributeIterator;
                        switch (sName) {
                            case "key":
                                String id = null;
                                String ktype = "none";
                                attributeIterator = startElement.getAttributes();
                                while (attributeIterator.hasNext()) {
                                    Attribute attrib = (Attribute) attributeIterator.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    switch (attributeName) {
                                        case "id":
                                            id = attributeValue;
                                            break;
                                        case "attr.type":
                                            ktype = attributeValue;
                                            if (!attributeValue.equalsIgnoreCase("double")) {
                                                System.out.println("Warning: non-standard EdgeType added: " + attributeValue);
                                            }
                                            break;
                                        default:
//                                            System.out.println("Unknown attribute type for Key: " + attributeName);
                                            break;
                                    }
                                }
                                edgeTypes.add(new HelperTypes.EdgeType(id, ktype));
                                break;

                            case "node":
                                String label = null;
                                String type = null;
                                attributeIterator = startElement.getAttributes();
                                while (attributeIterator.hasNext()) {
                                    Attribute attrib = (Attribute) attributeIterator.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    switch (attributeName) {
                                        case "id":
                                            label = attributeValue;
                                            break;
                                        case "type":
                                            type = attributeValue;
                                            break;
                                        default:
                                            System.out.println("Unknown attribute type for Node: " + attributeName);
                                            break;
                                    }
                                }
                                if (type != null && label != null) {
                                    GraphNode node = new GraphNode(label, type);
                                    nodes.add(node);
                                    nodeHashmap.put(node.getLabel(), node);
                                }
                                break;

                            case "edge":
                                edge = new Edge();
                                attributeIterator = startElement.getAttributes();
                                while (attributeIterator.hasNext()) {
                                    Attribute attrib = (Attribute) attributeIterator.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    switch (attributeName) {
                                        case "source":
                                            edge.start = nodeHashmap.get(attributeValue);
                                            break;
                                        case "target":
                                            edge.target = nodeHashmap.get(attributeValue);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                break;
                            case "data":
                                attributeIterator = startElement.getAttributes();
                                while (attributeIterator.hasNext()) {
                                    Attribute attrib = (Attribute) attributeIterator.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    switch (attributeName) {
                                        case "key":
                                            edge.edgeType = attributeValue;
                                            break;
                                        default:
//                                            System.out.println("Unknown attribute type for Edge>Data: " + attributeName);
                                            break;
                                    }
                                }
                                break;
                            default:
//                                System.out.println("Unknown Element ID: " + sName);
                                break;
                        }
                        break; // END-CASE START.ELEMENT

                    case XMLStreamConstants.CHARACTERS:
                        Characters characters = event.asCharacters();
                        String tagContent = characters.getData().trim();
                        if (!tagContent.equals("")) {
                            try {
                                edge.weight = Double.parseDouble(tagContent);
//                                System.out.println("Edge weight: " + edge.weight);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        EndElement endElement = event.asEndElement();
                        String eName = endElement.getName().getLocalPart();
                        switch (eName) { //Switch Ende der Elemente, baue Edge mit den gesammelten Informationen, gib Graphen zurück
                            case "edge":
                                if (!edge.isSelfEdge())
                                    edges.add(edge);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
            //Postprocessing der erhaltenen Daten
            if (edgeTypes.contains(new HelperTypes.EdgeType("package"))) {
                System.out.println("Doing in Parser that package-stuff ");
                HashSet<GraphNode> missingNodes = new HashSet<>();
                HashMap<String, GraphNode> mapMissingNodes = new HashMap<>();
                StringBuilder sb;
                String s;
                for (GraphNode mn : nodes) { // add missing nodes for package hierarchy
                    try {
                        sb = new StringBuilder(mn.getLabel());
                        System.out.println("sb = " + sb);
                        sb.delete(sb.lastIndexOf("."), sb.length());
                        s = sb.toString();
                        if (!nodeHashmap.containsKey(s) && !mapMissingNodes.containsKey(s)) {
                            for (String par : makeListOfPackageParents(s)) {
                                if (!nodeHashmap.containsKey(par) && !mapMissingNodes.containsKey(par)) {
                                    GraphNode tmpnd = new GraphNode(par, "package");
                                    missingNodes.add(tmpnd);
                                    mapMissingNodes.put(tmpnd.getLabel(), tmpnd);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                nodes.addAll(missingNodes);
                nodeHashmap.putAll(mapMissingNodes);
                for (GraphNode n : nodes) { //package parent von n.label suchen
                    try {
                        sb = new StringBuilder(n.getLabel());
                        sb.delete(sb.lastIndexOf("."), sb.length());
                        s = sb.toString();
                        //System.out.printf("parent for %s is %s\n", n.label, s);
                        if (nodeHashmap.containsKey(s)) {
                            edges.add(new Edge(nodeHashmap.get(s), n, "package", 1.0));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            stopTime = System.nanoTime();
            Duration dur = Duration.ofNanos(startTime - stopTime);
            System.out.println("Estimated elapsed time: " + (dur));

            Graph graph = new Graph(edges, null);
            graph.setEdgeTypes(edgeTypes);
            return graph;

        } catch (
                NoSuchElementException e)
        {
//            e.printStackTrace();
            System.out.println("no such element");
            return null;
        } catch ( Exception e) {
            System.out.println("some mistake in GraphML: " + e);
            e.printStackTrace();
            return null;
        }

    }

    private static ArrayList<String> makeListOfPackageParents(String s) {
        ArrayList<String> returnList = new ArrayList<>();
        StringBuilder sb = new StringBuilder(s);
        returnList.add(s);
        while (sb.lastIndexOf(".") > 0) {
            returnList.add(sb.delete(sb.lastIndexOf("."), sb.length()).toString());
        }

        return returnList;
    }


}