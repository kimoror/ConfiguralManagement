package practice1;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class XMLHandler extends DefaultHandler{
    String urlToPackage;
   @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("a")) {
            if(attributes.getValue("href").contains(".whl#"))
                urlToPackage = attributes.getValue("href");
        }
}
}