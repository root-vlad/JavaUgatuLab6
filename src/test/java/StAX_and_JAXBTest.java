import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;

import static org.junit.Assert.*;

/**
 * Created by vgorokhov on 03.01.2018.
 */
public class StAX_and_JAXBTest {

    StAX_and_JAXB stAX_and_jaxb;

    @Before
    public void setUp(){
        stAX_and_jaxb = new StAX_and_JAXB();
    }

    @Test
    public void readFileStAX() throws Exception {
        stAX_and_jaxb.readFileStAX();
    }

    @Test
    public void readStreets(){
        stAX_and_jaxb.readFileStAX();

        for (StreetData sd : stAX_and_jaxb.streets.values()){
            System.out.println("Найдена улица: " +  sd.getName());
            System.out.println("Данная улица имеет " + sd.getCount() + " классов дорог:");
            sd.printClasses();
            System.out.println("===============================================");
            System.out.println();
        }

    }

    @Test
    public void jaxbUnmarshaler(){
        try {
            stAX_and_jaxb.readFileStAX();
            stAX_and_jaxb.unmarshal();

            Assert.assertEquals(stAX_and_jaxb.streets.size(), stAX_and_jaxb.streetsJAXB.size());
            Assert.assertEquals(stAX_and_jaxb.streets, stAX_and_jaxb.streetsJAXB);
//
//            Assert.assertEquals(stAX_and_jaxb.streets.size(), stAX_and_jaxb.streetsJAXB.size());
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }


}