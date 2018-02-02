import generated.*;
import generated.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vgorokhov on 03.01.2018.
 */
public class StAX_and_JAXB {
    String fileName = "C:\\Users\\vgorokhov\\IdeaProjects\\JavaUgatuLab6\\files\\UfaCenter.xml";
//    String fileName = "C:\\Users\\vgorokhov\\IdeaProjects\\JavaUgatuLab6\\files\\UfaCenterSmall.xml";
    String schemaName = "C:\\Users\\vgorokhov\\IdeaProjects\\JavaUgatuLab6\\files\\osm.xsd";

    Map<String, StreetData> streets = new TreeMap<String, StreetData>();
    Map<String, StreetData> streetsJAXB = new TreeMap<String, StreetData>();
    StreetData streetData;
    String currentClassStreet;
    boolean flagNode = false, flagBusStop = false, flagWay = false, flagStreet = false;
    private String nameStreet = "";


    public boolean checkXMLSchema(){
        try {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            File schemaLocation = new File(schemaName);
            Schema schema = factory.newSchema(schemaLocation);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource( new File(fileName)));
        } catch (SAXException |IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void readFileStAX(){
        try {
            if (checkXMLSchema()) {
                XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().
                        createXMLStreamReader(fileName, new FileInputStream(fileName));
                while (xmlStreamReader.hasNext()) {
                    xmlStreamReader.next();
                    if (xmlStreamReader.isStartElement() || xmlStreamReader.isEndElement()) {
                        searchBusStop(xmlStreamReader);
                        searchWay(xmlStreamReader);
                    }
//                    myWait();
                }
            } else {
                System.out.println("ВАЛИДАЦИЯ ПРОВАЛЕНА");
            }


        } catch (XMLStreamException | FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void myWait(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void searchBusStop(XMLStreamReader xsr) throws XMLStreamException {
        if (xsr.getLocalName().equals("Node") && xsr.isStartElement()){
            flagNode = true;
        }
        if (xsr.getLocalName().equals("Node") && xsr.isEndElement()){
            flagNode = false;
            flagBusStop = false;
        }
        if (xsr.getLocalName().equals("tag") && xsr.isStartElement()) {
            if (xsr.getAttributeValue("", "k").equals("highway") && xsr.getAttributeValue("", "v").equals("bus_stop")) {
                flagBusStop = true;
            }
            if (flagBusStop && xsr.getAttributeValue("", "k").equals("name")) {
//                System.out.println("");
//                System.out.println("Найдена остановка: " + xsr.getAttributeValue("", "v"));
                System.out.println("");
            }
        }
    }



    public void searchWay(XMLStreamReader xsr) throws XMLStreamException {
        if (xsr.getLocalName().equals("way") && xsr.isStartElement()) {
            flagWay = true;

            currentClassStreet=null;
            nameStreet=null;
        }
        if (xsr.getLocalName().equals("way") && xsr.isEndElement()) {
            flagWay = false;

        }

        if (xsr.getLocalName().equals("way") && xsr.isEndElement() &&
                currentClassStreet!=null && nameStreet!=null){
            if (streets.containsKey(nameStreet)){
                streets.get(nameStreet).addSegment(currentClassStreet);
            }else {
                streetData = new StreetData(nameStreet);
                streetData.addSegment(currentClassStreet);
                streets.put(nameStreet, streetData);
            }
        }

            if (xsr.getLocalName().equals("tag") && xsr.isStartElement() && flagWay) {
                if (xsr.getAttributeValue("", "k").equals("highway")) {
//                    streetData = new StreetData();
                    currentClassStreet = xsr.getAttributeValue("", "v");
//                    streetData.addSegment(xsr.getAttributeValue("", "v"));

                }
                if (xsr.getAttributeValue("", "k").equals("name")) {
                    nameStreet = xsr.getAttributeValue("", "v");
                }
            }



    }



    void unmarshal() throws JAXBException,SAXException {
        JAXBContext jxc = JAXBContext.newInstance("generated");
        Unmarshaller u = jxc.createUnmarshaller();

        Source source = new StreamSource(fileName);
 SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
// читаем схему из файла
 Schema schema = sf.newSchema(new StreamSource(schemaName));
 u.setSchema(schema);

        JAXBElement<Osm> je = (JAXBElement) u.unmarshal(source, Osm.class);

        Osm osm = je.getValue();
        System.out.println(je.getDeclaredType());
        System.out.println(je.getValue().toString());

        List<Node> nodes;
        List<Way> ways;

        nodes = (osm.getBoundOrUserOrPreferences().stream().filter((t) -> t.getClass()== Node.class).map((x) -> (Node) x).collect(Collectors.toList()));
        ways = (osm.getBoundOrUserOrPreferences().stream().filter((t) -> t.getClass()== Way.class).map((x) -> (Way) x).collect(Collectors.toList()));

        searchBusStopJAXB(nodes);
        searchWayJAXB(ways);
    }




    public void searchWayJAXB(List<Way> ways)  {
        for (Way way: ways) {
            boolean highwayFlag = false;
            boolean nameFlag = false;
//            currentClassStreet="";
//            nameStreet = "";
            for (Tag tag : way.getRest().stream().filter((t) -> t.getClass()== Tag.class).map((x) -> (Tag) x).collect(Collectors.toList())) {
                if (tag.getK().equals("highway")){
                    currentClassStreet = tag.getV();
                    highwayFlag = true;
                }
                if (tag.getK().equals("name")){
                    nameStreet = tag.getV();
                    nameFlag = true;
                }
            }

            if (highwayFlag && nameFlag){
                if (streetsJAXB.containsKey(nameStreet)){
                    streetsJAXB.get(nameStreet).addSegment(currentClassStreet);
                }else {
                    streetData = new StreetData(nameStreet);
                    streetData.addSegment(currentClassStreet);
                    streetsJAXB.put(nameStreet, streetData);
                }
            }
        }

    }

    public void searchBusStopJAXB(List<Node> nodes){
        String nameBusStop = "";
        for (Node node: nodes ) {
            flagBusStop = false;
            for (Tag tag: node.getTag()) {
                if (tag.getK().equals("highway") && tag.getV().equals("bus_stop")) {
                    flagBusStop = true;
                }
                if (tag.getK().equals("name")){
                    nameBusStop = tag.getV();
                }
            }
            if (flagBusStop){
//                System.out.println("НАйдена остановка: " + nameBusStop);
            }




        }
    }



    }
