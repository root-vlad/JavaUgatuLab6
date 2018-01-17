import org.jdom2.*;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.internal.SystemProperty;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.jdom2.xpath.jaxen.JaxenXPathFactory;
import org.xml.sax.SAXException;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vgorokhov on 27.12.2017.
 */
public class ProcessingSVG_JDOM {

    Document document;

    ProcessingSVG_JDOM(String filePath) {
        try {
            this.document = useDOMParser(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }

    public void readSVG(){
        document.getRootElement().setNamespace(Namespace.NO_NAMESPACE);
        //Печатаем все найденные елементы
        List<Element> listElements = getListElement("//svg:circle|//svg:rect");
        printElements(listElements);
    }

    private List<Element> getListElement(String xpathExpression) {
        XPathFactory xpath = JaxenXPathFactory.instance();
        XPathExpression<Element> expr = xpath.compile(xpathExpression, Filters.element(), null, Namespace.getNamespace("svg", "http://www.w3.org/2000/svg"));
        return expr.evaluate(document);
    }

    private Document useDOMParser(String fileName) throws  IOException, SAXException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(fileName);
        return  document;
    }

    public void printElements(List<Element> listElements){

        for (Element element: listElements) {
            if (element.getName().equals("circle")) {
                System.out.println(">>" + element.getName() + "  " + element.getAttributeValue("r"));
            }
            if (element.getName().equals("rect")) {
                System.out.println(">>" + element.getName() + "    " + element.getAttributeValue("height") + "x" + element.getAttributeValue("width"));
            }
            if (element.getName().equals("path")){
                System.out.println(">>" + element.getName() + "    " + element.getAttributeValue("d"));

            }
        }
    }


    public void changeColorRectAndPath(){
        List<Element> listElements = getListElement("//svg:path|//svg:rect");
//        printElements(listElements);
        Iterator<Element> iterator = listElements.iterator();
        while (iterator.hasNext()){
            Element element = iterator.next();
            if (element.getName().equals("rect")) {
                element.setAttribute("style", "fill: yellow");
            }
            if (element.getName().equals("path")){
                element.setAttribute("style", "fill: red");
            }
        }
        saveNewDocument("rectANDpath.svg");
    }

    public void addPointInCircle(){
        List<Element> listElements = getListElement("//svg:circle");
        Iterator<Element> iterator = listElements.iterator();
        while (iterator.hasNext()){
            Element element = iterator.next();
            element.getParentElement().addContent(element.clone().setAttribute("r", "3").setAttribute("style", "fill: red"));
        }
        saveNewDocument("circlePiont.svg");
    }

    public void saveNewDocument(String newFileName){
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        try {
            xmlOutputter.output(document, new FileOutputStream(System.getProperty("user.dir") + File.separator + "files" + File.separator + newFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


