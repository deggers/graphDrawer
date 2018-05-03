package model;

import controller.ParseController;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.*;



public class GraphMLParser {
    
    public static boolean parseFileToTree(File file) throws Exception {
        try {
            // Ressourcen anlegen
            GraphMLGraph graph = null;
            Node node = null;
            Node source = null;
            Node target = null;
            //Edge edge = null; brauchen wahrscheinlich edge
            String edgeType = null;
            Double edgeWeight = null;
            
            // Speichern von Knoten, Kanten und Map<Name, Knoten> zum einfachen auffinden
            ArrayList<Node> nodes = new ArrayList<>();
            //ArrayList<Edge> edges = new ArrayList<>(); //brauche Edge-Klasse
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
                            case "key":
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
                            case "graph":
                                //eigentlich nichts
                                break;
                            case "node":
                                attributes = startElement.getAttributes();
                                String label = null;
                                String ntype = null;
                                while (attributes.hasNext()){
                                    String name = attributes.next().getName().getLocalPart();
                                    switch (name){
                                        case "id":
                                            label = name;
                                            break;
                                        case "type":
                                            ntype = name;
                                            break;
                                        default:
                                            System.out.println("Unknown attribute type for Node: " + name);
                                            break;
                                    }
                                }
                                node = new Node(label);
                                node.GraphMLType = ntype;
                                nodes.add(node);
                                nodesMap.put(label, node);
                                break;
                            case "edge":
                                break;
                            case "data":
                                break;
                            default:
                                break;
                        }
                        break;
                        
                    case XMLStreamConstants.CHARACTERS:
                        Characters characters = event.asCharacters();
                        
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
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while parsing GraphML file");
            return false;
        }
    }
    
}