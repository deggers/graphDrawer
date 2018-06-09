package model;

import model.HelperTypes.ProtoNode;

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
        ProtoNode node = null;
        Edge edge = null;;

        // Speichern von Knoten, Kanten und Map<Name, Knoten> zum einfachen auffinden
        HashSet<ProtoNode> nodes = new HashSet<>();
        HashSet<Edge> edges = new HashSet<>(); //brauche Edge-Klasse
        HashMap<String, ProtoNode> nodesMap = new HashMap<>();

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
                        Iterator<Attribute> attributes;
                        switch (sName) {
                            case "graphml":
                                graph = new GraphMLGraph();
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
                                    switch (attributeName) {
                                        case "id":
                                            id = attributeValue;
                                            break;
                                        case "for":
                                            break;
                                        case "attr.name":
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
                                break;
                            case "node"://-----------------------------Node--------------------------------------
                                break;
                            case "edge": //-----------------------------Edge--------------------------------------
                                edge = new Edge();
                                attributes = startElement.getAttributes();
                                while (attributes.hasNext()) {
                                    Attribute attrib = attributes.next();
                                    String attributeName = attrib.getName().getLocalPart();
                                    String attributeValue = attrib.getValue();
                                    switch (attributeName) {
                                        case "source":
                                            ProtoNode start = new ProtoNode(attributeValue);
                                            nodes.add(start);
                                            edge.start = start;
                                            break;
                                        case "target":
                                            ProtoNode target = new ProtoNode(attributeValue);
                                            nodes.add(target);
                                            edge.target = target;
                                            break;
                                        default:
//                                            System.out.println("Unknown attribute type for Edge: " + attributeName);
                                            break;
                                    }
                                }
                                break;
                            case "data"://-----------------------------Data--------------------------------------
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
                                //fehler, graph sollte zuerst beendet sein
                                break;
                            case "key":
                                break;
                            case "graph":
                                //Graph ausgeben, wenn es mehrere sind wird nur der erste ausgegeben
                                break;
                            case "node":
//                                System.out.println("found node end");
                                break;
                            case "edge":
                                ProtoNode startNode = (ProtoNode) edge.start;
                                ProtoNode targetNode = (ProtoNode) edge.target;
                                if (!startNode.equals(targetNode)) {
                                    edges.add(edge); //keine Selbstkanten
                                } else {
                                    System.out.println("self-Edge found!");
                                    System.out.println("startNode = " + startNode);
                                    System.out.println("targetNode = " + targetNode);
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
//                HashSet<ProtoNode> missingNodes = new HashSet<>();
//                HashMap<String, ProtoNode> mapMissingNodes = new HashMap<>();
//                StringBuilder sb;
//                String s;
//                for (ProtoNode mn : nodes) { // add missing nodes for package hierarchy
//                    sb = new StringBuilder(mn.getLabel());
//                    sb.delete(sb.lastIndexOf("."), sb.length());
//                    s = sb.toString();
//                    if (!nodesMap.containsKey(s) && !mapMissingNodes.containsKey(s)) {
//                        for (String par : makeListOfPackageParents(s)) {
//                            if (!nodesMap.containsKey(par) && !mapMissingNodes.containsKey(par)) {
//                                ProtoNode tmpnd = new ProtoNode(par, "package");
//                                missingNodes.add(tmpnd);
//                                mapMissingNodes.put(tmpnd.getLabel(), tmpnd);
//                            }
//                        }
//                    }
//                }
//                nodes.addAll(missingNodes);
//                nodesMap.putAll(mapMissingNodes);
//                for (ProtoNode n : nodes) { //package parent von n.label suchen
//                    try {
//                        sb = new StringBuilder(n.getLabel());
//                        sb.delete(sb.lastIndexOf("."), sb.length());
//                        s = sb.toString();
//                        //System.out.printf("parent for %s is %s\n", n.label, s);
//                        if (nodesMap.containsKey(s)) {
//                            edges.add(new Edge(nodesMap.get(s), n, "package", 1.0));
//                        }
//                    } catch (Exception e) {
//                    }
//                }
                graph.addAllEdges(edges);
                graph.addAllNodes(nodes);
            }
            System.out.println("Parsing finished!");
            stopTime = System.nanoTime();
            Duration dur = Duration.ofNanos(startTime - stopTime);
            System.out.println("Estimated elapsed time: " + (dur));
            return graph;
        } catch (
                NoSuchElementException e)
        {
//            e.printStackTrace();
            System.out.println("no such element");
            return null;
        } catch (
                Exception e)
        {
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