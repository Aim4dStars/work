package com.bt.nextgen.test;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class JaxbUnmarshallTestUtil {

	/**
	 * A simple test method to load an object which is not the top level element in the XSD definition
	 * 
	 * 
	 * @param xmlInput A String containing simple XML with the object to be unmarshalled as the first node
	 * @param returnClass The jaxb generated class which is expected
	 * @return An object of type return class which is the unmarshalled XML
	 * @throws Exception If the unmarshalling fails (due to bad XML structure or format)
	 */
	public static <T> T unmarshallSimpleObject(String xmlInput, Class<T> returnClass) throws Exception
	{
		 JAXBContext jc = JAXBContext.newInstance(returnClass);
		 Unmarshaller u = jc.createUnmarshaller();
		  
		 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		 dbf.setNamespaceAware(true);
		 DocumentBuilder db = dbf.newDocumentBuilder();
		 Document doc = db.parse(new ByteArrayInputStream(xmlInput.getBytes()));
		 Node subtree = doc.getFirstChild();
		  
		 JAXBElement<T> element = u.unmarshal( subtree, returnClass);
		 assertNotNull(element);
		 T object = element.getValue();
		 return object;
		 
	}


    /**
     * A simple test method to load an object which is not the top level element in the XSD definition
     *
     *
     * @param xmlInput A String containing simple XML with the object to be unmarshalled as the first node
     * @return A raw Node object to mimic one whose unmarshalling context is unknown to the server.
     * @throws Exception If the unmarshalling fails (due to bad XML structure or format)
     */
    public static Node unmarshallUnknownObject(String xmlInput) throws Exception
    {
        JAXBContext jc = JAXBContext.newInstance();
        Unmarshaller u = jc.createUnmarshaller();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xmlInput.getBytes()));
        Node subtree = doc.getFirstChild();

       return subtree;

    }

}
