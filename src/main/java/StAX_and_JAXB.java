import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
    String fileName = "C:\\Users\\vgorokhov\\IdeaProjects\\JavaUgatuLab6\\files\\UfaCenter.xml";
//    String fileName = "C:\\Users\\vgorokhov\\IdeaProjects\\JavaUgatuLab6\\files\\UfaCenterSmall.xml";
    String schemaName = "C:\\Users\\vgorokhov\\IdeaProjects\\JavaUgatuLab6\\files\\osm.xsd";

    Map<String, StreetData> streets = new TreeMap<String, StreetData>();
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
            flagStreet = false;
        }

        if (xsr.getLocalName().equals("way") && xsr.isEndElement()){
            if (streets.containsKey(nameStreet)){
                streets.get(nameStreet).addSegment(currentClassStreet);
            }else {
                streetData = new StreetData(nameStreet);
                streetData.addSegment(currentClassStreet);
                streets.put(nameStreet, streetData);
            }
            flagWay = false;
        }

            if (xsr.getLocalName().equals("tag") && xsr.isStartElement() && flagWay) {
                if (xsr.getAttributeValue("", "k").equals("highway")) {
//                    streetData = new StreetData();
                    currentClassStreet = xsr.getAttributeValue("", "v");
//                    streetData.addSegment(xsr.getAttributeValue("", "v"));
                    flagStreet = true;
                }
                if (xsr.getAttributeValue("", "k").equals("name")) {
                    nameStreet = xsr.getAttributeValue("", "v");
                }
            }


    }



    public Object getObject() throws JAXBException {//, Class<?> c) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance("generated");//c);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object object = unmarshaller.unmarshal(new File(fileName));

        return object;
    }


    void unmarshal() throws JAXBException,SAXException {
        JAXBContext jxc =
                JAXBContext.newInstance();
        //  JAXBContext.newInstance(ObjectFactory.class);

        Unmarshaller u = jxc.createUnmarshaller();


        //    StreamSource ss = new StreamSource("test.xsd");
        //    SchemaFactory sf = SchemaFactory.newInstance(
        //           XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // читаем схему из файла
        //    Schema schema = sf.newSchema(ss);
        //    u.setSchema(schema);

        JAXBElement je = (JAXBElement)
                u.unmarshal(new File(fileName));

//        price=(PriceType) je.getValue();
        System.out.println(je.getValue());
    }



}
