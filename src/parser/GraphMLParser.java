package parser;

import structure.*;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.*;
import java.io.File;
import java.io.FileReader;
import java.time.Duration;
import java.util.*;

public class GraphMLParser {
    private static final boolean VERBOSE = false;
    private static final boolean DEBUG = false;
    private static final boolean TESTINGGRAPHS = true;

    @SuppressWarnings("ConstantConditions")
    public static Graph parseFileToGraph(File file) {
        // Ressourcen anlegen
        long startTime = System.nanoTime();
        long stopTime;
        Graph graph = null;
        Edge edge = new Edge();
        try { // XML-Reader
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(new FileReader(file));

            while (reader.hasNext()) { //Über Inhalt der XML iterieren
                XMLEvent event = reader.nextEvent();
                switch (event.getEventType()) {

                    case XMLStreamConstants.START_ELEMENT:
                        StartElement startElement = event.asStartElement();
                        String sName = startElement.getName().getLocalPart();

//                        System.out.println("sName = " + sName);
                        // key
                        Iterator<Attribute> attributes;
                        switch (sName) {
                            case "graphml":
                                graph = new Graph();
                                if (DEBUG) System.out.println("found graphml start");
                                break;
                            case "key"://-----------------------------Key--------------------------------------
                                if (DEBUG) System.out.println("found key start");
                                attributes = startElement.getAttributes();
                                LinkedList<String> edgeType = null;
                                while (attributes.hasNext()) {
                                    Attribute attrib = attributes.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    switch (attributeName) {
                                        case "id":
                                            graph.addEdgeType(attributeValue);
//                                            id = attributeValue;
                                            if (DEBUG) System.out.println("set id: " + attributeValue);
                                            break;
                                        case "for":
                                            break;
                                        case "attr.name":
                                            break;
                                        case "attr.type":
                                            graph.addEdgeType(attributeValue);
//                                            ktype = attributeValue;
                                            if (DEBUG && !attributeValue.equalsIgnoreCase("double")) {
                                                System.out.println("Warning: non-standard EdgeType added: " + attributeValue);
                                            }
                                            break;
                                        default:
                                            System.out.println("Unknown attribute type for Key: " + attributeName);
                                            break;
                                    }
                                }
                                break;
                            case "graph"://-----------------------------Graph--------------------------------------
                                break;

                            case "node"://-----------------------------Node--------------------------------------
                                if (DEBUG) System.out.println("found node start");
                                attributes = startElement.getAttributes();
                                String label, type;
                                label = type = "";
                                while (attributes.hasNext()) {
                                    Attribute attrib = attributes.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    switch (attributeName) {
                                        case "id":
                                            if (DEBUG) System.out.println("node id: " + attributeValue);
                                            label = attributeValue;
                                            break;
                                        case "type":
                                            if (DEBUG) System.out.println("node type: " + attributeValue);
                                            type = attributeValue;
                                            break;
                                        default:
                                            System.out.println("Unknown attribute type for Node: " + attributeName);
                                            break;
                                    }
                                }
                                if (type != null && label != null) {
                                    graph.addNode(new GraphNode(label, type));
                                } else
                                    throw new RuntimeException("Exception due to improperly formatted GraphML Node entry: " + startElement.toString());

                                break;
                            case "edge": //-----------------------------Edge--------------------------------------
                                if (DEBUG) System.out.println("found edge start");
                                attributes = startElement.getAttributes();
                                GraphNode source = null;
                                GraphNode target = null;
                                while (attributes.hasNext()) {
                                    Attribute attrib = attributes.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    switch (attributeName) {
                                        case "source":
                                            source = graph.getNodes().get(new GraphNode(attributeValue));
                                            if (DEBUG) System.out.println("edge source: " + source);
                                            break;
                                        case "target":
                                            target = graph.getNodes().get(new GraphNode(attributeValue));
                                            if (DEBUG) System.out.println("edge target: " + target);
                                            break;
                                        default:
                                            if (DEBUG)
                                                System.out.println("Unknown attribute type for Edge: " + attributeName);
                                            break;}
                                }
                                edge = new Edge(source, target);
                                break;
                            case "data"://-----------------------------Data--------------------------------------
                                if (DEBUG) System.out.println("found data start");
                                attributes = startElement.getAttributes();
                                while (attributes.hasNext()) {
                                    Attribute attrib = attributes.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    switch (attributeName) {
                                        case "key":
                                            edge.setEdgeType(attributeValue);
                                            break;
                                        default:
                                            System.out.println("Unknown attribute type for Edge>Data: " + attributeName);
                                            break;}
                                }
                                break;
                            default:
                                System.out.println("Unknown Element ID: " + sName);
                                break;
                        }
                        break; // END-CASE START.ELEMENT

                    case XMLStreamConstants.CHARACTERS:
                        Characters characters = event.asCharacters();
                        // Read tag content
                        String tagContent = characters.getData().trim();
                        if (!tagContent.equals("")) {
                            try {
                                edge.setWeight(Double.parseDouble(tagContent));
                                if (DEBUG) System.out.println("Edge weight: " + edge.getWeight());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();}
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        EndElement endElement = event.asEndElement();
                        String eName = endElement.getName().getLocalPart();
                        switch (eName) { //Switch Ende der Elemente, baue Edge mit den gesammelten Informationen, gib Graphen zurück
                            case "graphml":
                                break;
                            case "key":
                                if (VERBOSE) System.out.println("found key end");
                                break;
                            case "graph":
                                break;
                            case "node":
                                if (VERBOSE) System.out.println("found node end");
                                break;
                            case "edge":
                                if (!edge.tail.equals(edge.head))  //keine Selbstkanten
                                    graph.addEdge(edge);
                                else System.out.println("found self edge!");
                                break;
                            case "data":
//                                System.out.println("found data end");
                                break;
                            default:
                                System.out.println("Unknown end tag: " + eName);
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
            //Postprocessing der erhaltenen
            if (!TESTINGGRAPHS) {
                if (graph != null) {
                    StringBuilder sb;
                    LinkedHashMap<GraphNode, GraphNode> nodes = graph.getNodes();
                    for (GraphNode mn : nodes.values()) { // add missing nodes for package hierarchy
                        try {
                            sb = new StringBuilder(mn.getLabel());
                            sb.delete(sb.lastIndexOf("."), sb.length());
                            GraphNode s = new GraphNode(sb.toString());
                            if (!nodes.containsKey(s)) {
                                for (String par : makeListOfPackageParents(s.getLabel())) {
                                    GraphNode parNode = new GraphNode(par);
                                    parNode.setNodeType("package");
                                    if (!nodes.containsKey(parNode)) nodes.put(parNode, parNode);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    for (GraphNode parentalNode : nodes.values()) { //package parent von n.label suchen
                        try {
                            sb = new StringBuilder(parentalNode.getLabel());
                            sb.delete(sb.lastIndexOf("."), sb.length());
                            GraphNode childishNode = new GraphNode(sb.toString());
                            if (DEBUG)
                                System.out.printf("parent for %s is %s\n", parentalNode.getLabel(), childishNode);
                            if (nodes.containsKey(new GraphNode(childishNode))) {
                                Edge childParEdge = new Edge(nodes.get(childishNode), parentalNode, "package");
                                graph.addEdge(childParEdge);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
            System.out.println("Parsing finished!");
            stopTime = System.nanoTime();
            Duration dur = Duration.ofNanos(startTime - stopTime);
            System.out.println("Estimated elapsed time: " + (dur));
            return graph;
        } catch (NoSuchElementException e) {
//            e.printStackTrace();
            System.out.println("no such element");
            return null;
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("some mistake in GraphML: " + e);
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
