import org.junit.Before;
import org.junit.Test;

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
}