package util;

import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * JaxbValidator is used to validate if object is conforms to an XSD schema.
 */
public class JaxbValidator<T> {

    private Schema schema;
    private Marshaller marshaller;

    public JaxbValidator(String pathInClassPath, Class<T> clazz) throws Exception {
        String systemId = clazz.getClassLoader().getResource(pathInClassPath).toString();
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(systemId);
        schema = factory.newSchema(schemaFile);

        JAXBContext context = JAXBContext.newInstance(clazz);
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    }

    public void validate(T jaxbObject) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();

        marshaller.marshal(jaxbObject, document);
        schema.newValidator().validate(new DOMSource(document));
    }
}
