import org.xml.sax.SAXException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by vgorokhov on 03.01.2018.
 */
public class StAX_and_JAXB {
//    String fileName = "C:\\Users\\vgorokhov\\IdeaProjects\\JavaUgatuLab6\\files\\UfaCenter.xml";
    String fileName = "C:\\Users\\vgorokhov\\IdeaProjects\\JavaUgatuLab6\\files\\UfaCenterSmall.xml";
    String schemaName = "C:\\Users\\vgorokhov\\IdeaProjects\\JavaUgatuLab6\\files\\osm.xsd";

    Map<String, StreetData> streets = new TreeMap<String, StreetData>();
    boolean flagNode = false, flagBusStop = false, flagWay = false, flagStreet = false;



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
                    if (xmlStreamReader.hasText()) //System.out.println("Text " + i + " = " + xmlStreamReader.getText());
                    myWait();
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
        }
        if (xsr.getLocalName().equals("tag") && xsr.isStartElement()) {
            if (xsr.getAttributeValue("", "k").equals("highway") && xsr.getAttributeValue("", "v").equals("bus_stop")) {
                flagBusStop = true;
            }
            if (flagBusStop && xsr.getAttributeValue("", "k").equals("name")) {
                System.out.println("");
                System.out.println("Найдена остановка: " + xsr.getAttributeValue("", "v"));
                System.out.println("");
                flagBusStop = false;
            }
        }
    }



    public void searchWay(XMLStreamReader xsr) throws XMLStreamException {
        if (xsr.getLocalName().equals("way") && xsr.isStartElement()) {
            System.out.println("6666666666");

            flagWay = true;
        }
        if (xsr.getLocalName().equals("way") && xsr.isEndElement()){
            System.out.println("9999999999");

            flagWay = false;
            System.out.println();
        }

            if (xsr.getLocalName().equals("tag") && xsr.isStartElement() && flagWay) {
                if (xsr.getAttributeValue("", "k").equals("highway")) {

                    flagStreet = true;
                }
                if (flagStreet && xsr.getAttributeValue("", "k").equals("name")) {
//                    System.out.println("");
                    System.out.println("Найдена улица: " + xsr.getAttributeValue("", "v"));
//                    System.out.println("");
                    flagStreet = false;
                }
            }


    }



}