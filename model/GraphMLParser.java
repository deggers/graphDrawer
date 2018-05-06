package model;

import controller.ParseController;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.*;



public class GraphMLParser {
    
    public static boolean parseFileToTree(File file) {
        try {
            // Ressourcen anlegen
            GraphMLGraph graph = null;
            Node node = null;
            Node source = null;
            Node target = null;
            Edge edge = null; //brauchen wahrscheinlich edge
            String edgeType = null;
            Double edgeWeight = null;
            
            // Speichern von Knoten, Kanten und Map<Name, Knoten> zum einfachen auffinden
            ArrayList<Node> nodes = new ArrayList<>();
            ArrayList<Edge> edges = new ArrayList<>(); //brauche Edge-Klasse
            LinkedHashMap<String, Node> nodesMap = new LinkedHashMap<>();
            
            // XML-Reader
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(new FileReader(file));
            
            while(reader.hasNext()){ //Über Inhalt der XML iterieren
                XMLEvent event = reader.nextEvent();
                switch (event.getEventType()){
                    
                    case XMLStreamConstants.START_ELEMENT:
                        StartElement startElement = event.asStartElement();
                        String sName = startElement.getName().getLocalPart();
                        Iterator<Attribute> attributes;
                        switch (sName) {
                            case "graphml":
                                graph = new GraphMLGraph(); //MappedTreeStructure forken, damit mehrere Rootelemente möglich sind
                                break;
                            case "key"://-----------------------------Key--------------------------------------
                                attributes = startElement.getAttributes();
                                String id = null;
                                String ktype = "none";
                                while (attributes.hasNext()){
                                    String name = attributes.next().getName().getLocalPart();
                                    switch (name){
                                            case "id":
                                                id = name;
                                                break;
                                            case "for":
                                                if (!name.equalsIgnoreCase("edge")) throw new Exception("Unknown attribute for GraphML key: for=\""+name+"\"");
                                                break;
                                            case "attr.name":
                                                if (!name.equalsIgnoreCase(id)) throw new Exception("GraphML Key id doesn't match attr.name or was not set");
                                                break;
                                            case "sttr.type":
                                                ktype = name;
                                                System.out.println("Warning: non-standard EdgeType added: " + name);
                                                break;
                                            default:
                                                System.out.println("Unknown attribute type for Key: " + name);
                                            break;
                                    }
                                }
                                graph.addEdgeType(id, ktype);
                                break;
                            case "graph"://-----------------------------Graph--------------------------------------
                                //eigentlich nichts
                                break;
                            case "node"://-----------------------------Node--------------------------------------
                                attributes = startElement.getAttributes();
                                String label = null;
                                node = new Node("ich sollte nicht hier sein");
                                while (attributes.hasNext()){
                                    String name = attributes.next().getName().getLocalPart();
                                    switch (name){
                                        case "id":
                                            node.label = name;
                                            break;
                                        case "type":
                                            node.GraphMLType = name;
                                            break;
                                        default:
                                            System.out.println("Unknown attribute type for Node: " + name);
                                            break;
                                    }
                                }
                                nodes.add(node);
                                nodesMap.put(label, node);
                                break;
                            case "edge": //-----------------------------Edge--------------------------------------
                                attributes = startElement.getAttributes();
                                while (attributes.hasNext()){
                                    String name = attributes.next().getName().getLocalPart();
                                    switch (name){
                                        case "source":
                                            source = nodesMap.get(name);
                                            break;
                                        case "target":
                                            target = nodesMap.get(name);
                                            break;
                                        default:
                                            System.out.println("Unknown attribute type for Edge: " + name);
                                            break;
                                    }
                                }
                                break;
                            case "data"://-----------------------------Data--------------------------------------
                                attributes = startElement.getAttributes();
                                while (attributes.hasNext()){
                                    String name = attributes.next().getName().getLocalPart();
                                    switch (name){
                                        case "key":
                                            edgeType = name;
                                            break;
                                        default:
                                            System.out.println("Unknown attribute type for Edge>Data: " + name);
                                            break;
                                    }
                                }
                                break;
                            default:
                                System.out.println("Unknown Element ID: " +sName);
                                break;
                        }
                        break;
                        
                    case XMLStreamConstants.CHARACTERS:
                        Characters characters = event.asCharacters();
                        // Read tag content
                        String tagContent = characters.getData().trim();
                        if (!tagContent.equals("")) {
                            try {
                                edgeWeight = Double.parseDouble(tagContent);
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
                                break;
                            case "edge":
                                //Edge fertig bauen
                                /* boolean edgeIsNew = true; //test for existing equal edge
                                for (Edge e : edges) {
                                    if (e.start.label.equals(nodeStart.label) && e.end.label.equals(nodeEnd.label) && e.edgeType.equals(edgeType)) {
                                        edgeIsNew = false;
                                    }
                                }*/
                                if (/*edgeIsNew*/true) {
                                edge = new Edge();
                                edge.start = source;
                                edge.target = target;
                                edge.edgeType = edgeType;
                                edge.weight = edgeWeight;
                                edges.add(edge);
                                }
                                break;
                            case "data":
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
            if (graph != null){
                graph.addAllNodes(nodes);
                graph.addAllEdges(edges);
                graph.finalizeGraphFromParser();
                ParseController.getInstance().setTree(graph);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while parsing GraphML file");
            return false;
        }
    }
    
}