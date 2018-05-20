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

    @SuppressWarnings("ConstantConditions")
    public static GraphMLGraph parseFileToGraph(File file) {
        // Ressourcen anlegen
        long startTime = System.nanoTime();
        long stopTime;
        GraphMLGraph graph = null;
        Node node = null;
        Edge edge = null; //brauchen wahrscheinlich edge

        // Speichern von Knoten, Kanten und Map<Name, Knoten> zum einfachen auffinden
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<Edge> edges = new ArrayList<>(); //brauche Edge-Klasse
        HashMap<String, Node> nodesMap = new HashMap<>();

        try {

            // XML-Reader
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
                                graph = new GraphMLGraph(); //MappedTreeStructure forken, damit mehrere Rootelemente möglich sind
//                                System.out.println("found graphml start");
                                break;
                            case "key"://-----------------------------Key--------------------------------------
//                                System.out.println("found key start");
                                attributes = startElement.getAttributes();
                                String id = null;
                                String ktype = "none";
                                while (attributes.hasNext()) {
                                    Attribute attrib = attributes.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    // attr.name
//                                    System.out.println("befor switch: " + attributeName);
                                    switch (attributeName) {
                                        case "id":
                                            id = attributeValue;
//                                            System.out.println("set id: " + attributeValue);
                                            break;
                                        case "for":
//                                            System.out.println("found for");
//                                            if (attributeValue.equalsIgnoreCase("edge")) {
////                                                System.out.println("inside if clause");
//                                            } else {
//                                                throw new Exception("Unknown attribute for GraphML key: for=\"" + attributeValue + "\"");
//                                            }
                                            break;
                                        case "attr.name":
                                            if (!attributeValue.equalsIgnoreCase(id))
                                                //throw new Exception("GraphML Key id doesn't match attr.name or was not set");
                                                break;
                                        case "attr.type":
                                            ktype = attributeValue;
                                            if (!attributeValue.equalsIgnoreCase("double")) {
                                                System.out.println("Warning: non-standard EdgeType added: " + attributeValue);
                                            }
                                            break;
                                        default:
                                            System.out.println("Unknown attribute type for Key: " + attributeName);
                                            break;
                                    }
                                }
                                graph.addEdgeType(id, ktype);
                                break;
                            case "graph"://-----------------------------Graph--------------------------------------
//                                System.out.println("found graph start");
                                //eigentlich nichts
                                break;

                            case "node"://-----------------------------Node--------------------------------------
//                                System.out.println("found node start");
                                attributes = startElement.getAttributes();
                                node = new Node("ich sollte nicht hier sein");
                                while (attributes.hasNext()) {
                                    Attribute attrib = attributes.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    switch (attributeName) {
                                        case "id":
//                                            System.out.println("node id: " + attributeValue);
                                            node.label = attributeValue;
                                            break;
                                        case "type":
//                                            System.out.println("node type: " + attributeValue);
                                            node.GraphMLType = attributeValue;
                                            break;
                                        default:
                                            System.out.println("Unknown attribute type for Node: " + attributeName);
                                            break;
                                    }
                                }
                                nodes.add(node);
                                nodesMap.put(node.label, node);
                                break;
                            case "edge": //-----------------------------Edge--------------------------------------
//                                System.out.println("found edge start");
                                edge = new Edge();
                                attributes = startElement.getAttributes();
                                while (attributes.hasNext()) {
                                    Attribute attrib = attributes.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    switch (attributeName) {
                                        case "source":
                                            edge.start = nodesMap.get(attributeValue);
//                                            System.out.println("edge source: " + edge.start);
                                            break;
                                        case "target":
                                            edge.target = nodesMap.get(attributeValue);
//                                            System.out.println("edge target: " + edge.target);
                                            break;
                                        default:
//                                            System.out.println("Unknown attribute type for Edge: " + attributeName);
                                            break;
                                    }
                                }
                                break;
                            case "data"://-----------------------------Data--------------------------------------
//                                System.out.println("found data start");
                                attributes = startElement.getAttributes();
                                while (attributes.hasNext()) {
                                    Attribute attrib = attributes.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    switch (attributeName) {
                                        case "key":
                                            edge.edgeType = attributeValue;
                                            break;
                                        default:
                                            System.out.println("Unknown attribute type for Edge>Data: " + attributeName);
                                            break;
                                    }
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
                            case "graphml":
//                                System.out.println("found graphml end");
                                //fehler, graph sollte zuerst beendet sein
                                break;
                            case "key":
//                                System.out.println("found key end");
                                break;
                            case "graph":
//                                System.out.println("found graph end");
                                //Graph ausgeben, wenn es mehrere sind wird nur der erste ausgegeben
                                break;
                            case "node":
//                                System.out.println("found node end");
                                break;
                            case "edge":
//                                System.out.println("found edge end");
                                //Edge fertig bauen
                                /* boolean edgeIsNew = true; //test for existing equal edge
                                for (Edge e : edges) {
                                    if (e.start.label.equals(nodeStart.label) && e.end.label.equals(nodeEnd.label) && e.edgeType.equals(edgeType)) {
                                        edgeIsNew = false;
                                    }
                                }*/
                                if (/*edgeIsNew*/true) { // checkt aktuell nicht, ob die Kante schon existiert
//                                    System.out.println("Adding Edge: " + edge);
                                    if (!edge.start.label.equals(edge.target.label))
                                        edges.add(edge); //keine Selbstkanten
                                }
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
            //Postprocessing der erhaltenen Daten
            if (graph != null) {
                HashSet<Node> missingNodes = new HashSet<>();
                HashMap<String, Node> mapMissingNodes = new HashMap<>();
                StringBuilder sb;
                String s;
                for (Node mn : nodes) { // add missing nodes for package hierarchy
                    sb = new StringBuilder(mn.label);
                    sb.delete(sb.lastIndexOf("."), sb.length());
                    s = sb.toString();
                    if (!nodesMap.containsKey(s) && !mapMissingNodes.containsKey(s)) {
                        for (String par : makeListOfPackageParents(s)) {
                            if (!nodesMap.containsKey(par) && !mapMissingNodes.containsKey(par)) {
                                Node tmpnd = new Node(par);
                                tmpnd.GraphMLType = "package";
                                missingNodes.add(tmpnd);
                                mapMissingNodes.put(tmpnd.label, tmpnd);
                            }
                        }
                    }
                }
                nodes.addAll(missingNodes);
                nodesMap.putAll(mapMissingNodes);
                for (Node n : nodes) { //package parent von n.label suchen
                    try {
                        sb = new StringBuilder(n.label);
                        sb.delete(sb.lastIndexOf("."), sb.length());
                        s = sb.toString();
                        //System.out.printf("parent for %s is %s\n", n.label, s);
                        if (nodesMap.containsKey(s)) {
                            edges.add(new Edge(nodesMap.get(s), n, "package", 1.0));
                        }
                    } catch (Exception e) {
                    }
                }
                graph.addAllEdges(edges);
                graph.addAllNodes(nodes);

                //graph.finalizeGraphFromParser(); nocht nicht gebraucht: erst, wenn root und edgeType eines teilbaumes bekannt
//                ParseController.getInstance().setTree(graph);
                // der Parse-Controller setzt den jetzt
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
//        do {
//            int punkt = sb.lastIndexOf(".");
//            if (punkt < 0) {
//                break;
//            }
//            sb.delete(punkt, sb.length());
//            returnList.add(sb.toString());
//        } while (true);

//        more compact ?
        while (sb.lastIndexOf(".") > 0) {
            returnList.add(sb.delete(sb.lastIndexOf("."), sb.length()).toString());
        }

        return returnList;
    }


}