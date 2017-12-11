package com.bt.nextgen.messages;

import com.bt.nextgen.service.avaloq.NotificationImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * 
 * @author L070354
 * 
 * Lightweight Test Class for compiling the xpath defined in the Service Implementation
 *
 */

public class NotificationMessagesIntegrationTestCheckXpath
{
	private static Logger logger = LoggerFactory.getLogger(NotificationMessagesIntegrationTestCheckXpath.class);
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder;
	Document doc = null;
	Document docReport = null;
	Document docReport2 = null;
	// Create XPathFactory object
	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPathExpression xPathExpression = null;

	// Create XPath object
	XPath xpath = xpathFactory.newXPath();
	String filePath = "src//main//resources//webservices//response//BTFG$UI_NTFCN_LIST.NTFCN.xml";
	String fileContent;
	Node nodeReport;
	BufferedReader reader;

	String sharedNotificationAdditionalXmlData = "<ShareNotification>\\n            " +
			"<personalizedMessage>Some Personalized message goes here..." +
			"</personalizedMessage>\\n            <type>ASX</type>\\n            <url>http://www.btfg.com.au/</url>\\n            " +
			"<urlText>SOME URL TEXT</urlText>\\n            </ShareNotification>";

	@Before
	public void setUp() throws Exception
	{
		fileContent = readFile(filePath);
		factory.setNamespaceAware(false);
		builder = factory.newDocumentBuilder();
		doc = builder.parse(new InputSource(new StringReader(fileContent)));

		xPathExpression = xpath.compile("//data");
		nodeReport = (Node)xPathExpression.evaluate(doc, XPathConstants.NODE);
		converttoString(nodeReport);
	}

	@After
	public void tearDown() throws Exception
	{}

	@Test
	public void testXpathLoadNotifications() throws XPathExpressionException
	{
		logger.trace("Inside testMethod: testXpathLoadNotifications()");

		List <String> xpathList = new ArrayList <String>();
		xpathList.add(NotificationImpl.PATH_NTFCN_BP_ID);
		xpathList.add(NotificationImpl.PATH_NTFCN_CAT);
		xpathList.add(NotificationImpl.PATH_NTFCN_EVENTTYPE);
		xpathList.add(NotificationImpl.PATH_NTFCN_MACC_NR);
		xpathList.add(NotificationImpl.PATH_NTFCN_MSG);
		xpathList.add(NotificationImpl.PATH_NTFCN_MSG_ID);
		//xpathList.add(NotificationImpl.PATH_NTFCN_MSGTYPE);
		xpathList.add(NotificationImpl.PATH_NTFCN_ORDERID);
		xpathList.add(NotificationImpl.PATH_NTFCN_OWNER_TYPE_ID);
		xpathList.add(NotificationImpl.PATH_NTFCN_PERSON_ID);
		xpathList.add(NotificationImpl.PATH_NTFCN_PRIORITY);
		xpathList.add(NotificationImpl.PATH_NTFCN_RECPID);
		xpathList.add(NotificationImpl.PATH_NTFCN_RESP_USERID);
		xpathList.add(NotificationImpl.PATH_NTFCN_STATUS);
		xpathList.add(NotificationImpl.PATH_NTFCN_TIMESTAMP);
		xpathList.add(NotificationImpl.PATH_NTFCN_TRIGOBJ);
		xpathList.add(NotificationImpl.PATH_NTFCN_VALIDTO);
		xpathList.add(NotificationImpl.PATH_NTFCN_SUB_CAT);
		xpathList.add(NotificationImpl.PATH_NTFCN_IS_ADVR_DSHBD);

		for (String s : xpathList)
		{
			logger.info("xpath to be compiled is: {}", s);
			xPathExpression = xpath.compile(s);
			String value = (String)xPathExpression.evaluate(nodeReport, XPathConstants.STRING);
			assertThat(value, is(notNullValue()));
		}

	}

	private String readFile(String file) throws IOException
	{
		reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = reader.readLine()) != null)
		{
			stringBuilder.append(line);

		}

		return stringBuilder.toString();
	}

	private String converttoString(Node node) throws TransformerException
	{
		DOMSource domSource = new DOMSource(node);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		return writer.toString();
	}

	@Test
	public void testXPathForSharedNotification() throws XPathExpressionException {

		xPathExpression = xpath.compile("//data/ntfcn_list/ntfcn/ntfcn_head_list/ntfcn_head");
		Node nodeReport = (Node)xPathExpression.evaluate(doc, XPathConstants.NODE);

		xPathExpression = xpath.compile("msg_text/val");
		String value = (String)xPathExpression.evaluate(nodeReport, XPathConstants.STRING);

		assertThat(value.trim(),is(sharedNotificationAdditionalXmlData));
	}

}
