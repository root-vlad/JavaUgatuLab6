import org.jdom2.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by vgorokhov on 27.12.2017.
 */
public class ProcessingSVG_JDOMTest {

    ProcessingSVG_JDOM processingSVG_jdom;

    @Before
    public void setUp(){
        processingSVG_jdom = new ProcessingSVG_JDOM("files/clouds.svg");
    }

    @Test
    public void readSVG() throws Exception {
        processingSVG_jdom.readSVG();
    }

    @Test
    public void changeColorRectAndPath(){
        processingSVG_jdom.changeColorRectAndPath();
    }

    @Test
    public void addPointInCircle(){
        processingSVG_jdom.addPointInCircle();
    }
}