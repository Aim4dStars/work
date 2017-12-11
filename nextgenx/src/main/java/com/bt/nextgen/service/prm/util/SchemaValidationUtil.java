package com.bt.nextgen.service.prm.util;

import com.aciworldwide.risk.gateway.formatter.xml.XFRqst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by L081012-Rishi Gupta on 5/02/2016.
 */

/**
 * This class is used to send XML messages to PRM queue.
 *
 * @author Rishi Gupta
 *
 */

public final class SchemaValidationUtil {

	private static final Logger logger = LoggerFactory.getLogger(SchemaValidationUtil.class);


	/**
	 * This method is used for validation of XSD.
	 
	 */
// TODO: will be used for schema validation
	public void validateNonFinTranRequest(com.aciworldwide.risk.gateway.formatter.xml.XFRqst document)
			 {
		logger.debug("Method: MqMessageSender.validateNonFinTranRequest()");
		com.aciworldwide.risk.gateway.formatter.xml.ObjectFactory factory = new com.aciworldwide.risk.gateway.formatter.xml.ObjectFactory();
		ClassLoader cl = com.aciworldwide.risk.gateway.formatter.xml.ObjectFactory.class.getClassLoader();
				 Source schemaExportLcSource;
				 JAXBContext context;

				 try {
					 context = JAXBContext.newInstance("com.aciworldwide.risk.gateway.formatter.xml", cl);
					 JAXBElement<XFRqst> element = factory.createXFRqst(document);
					 JAXBSource source = new JAXBSource(context, element);
					 SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
					 InputStream isExLcSchema = this.getClass().getResourceAsStream("XF_1_0_0.xsd");
					 schemaExportLcSource = new StreamSource(isExLcSchema);
					 Schema schema = sf.newSchema(schemaExportLcSource);
					 javax.xml.validation.Validator validator = schema.newValidator();
					 validator.validate(source);
				 }
				 catch (JAXBException | SAXException | IOException e) {
					 logger.error("Error Message :" + e);
			 }
			 }
}