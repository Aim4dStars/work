package com.bt.nextgen.messages;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.*;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.bt.nextgen.service.avaloq.NotificationUnreadCountResponseImpl;

/**
 * @author L070589
 *
 * Test class to validate the Xpath to retrieve the Count values from response xml.
 */
public class NotificationXpathValidatorTest
{
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder;
	Document doc = null;
	// Create XPathFactory object
	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPathExpression xPathExpression = null;

	// Create XPath object
	XPath xpath = xpathFactory.newXPath();
	String filePath = "/webservices/response/BTFG$UI_NTFCN_LIST.PRIO_CAT_CTR.xml";
	String fileContent;
	String xPathClientPriorityXPathNotification = NotificationUnreadCountResponseImpl.XPATH_CLIENT_PRIORITY_UNREAD_NOTIFICATION;
	String xPathMyPriorityXPathNotification = NotificationUnreadCountResponseImpl.XPATH_MY_PRIORITY_UNREAD_NOTIFICATION;
	String xPathClientNonPriorityXPathNotification = NotificationUnreadCountResponseImpl.XPATH_CLIENT_NONPRIORITY_UNREAD_NOTIFICATION;
	String xPathMyNonPriorityXPathNotification = NotificationUnreadCountResponseImpl.XPATH_MY_NONPRIORITY_UNREAD_NOTIFICATION;
	Node nodeReport;

	@Before
	public void setUp() throws Exception
	{

		fileContent = readFile(filePath);
		factory.setNamespaceAware(true);
		builder = factory.newDocumentBuilder();
		doc = builder.parse(new InputSource(new StringReader(fileContent)));
		xPathExpression = xpath.compile("//data/report");
		nodeReport = (Node)xPathExpression.evaluate(doc, XPathConstants.NODE);
	}

	@After
	public void tearDown() throws Exception
	{}

	@Test
	public void testXpathClientPriorityXPathNotification() throws XPathExpressionException
	{
		xPathExpression = xpath.compile(xPathClientPriorityXPathNotification);
		String XpathClientPriorityXPathNotification = (String)xPathExpression.evaluate(nodeReport, XPathConstants.STRING);
		assertThat(XpathClientPriorityXPathNotification, is(notNullValue()));
		assertThat(XpathClientPriorityXPathNotification, is("5"));
	}

	@Test
	public void testXPathMyNonPriorityXPathNotification() throws XPathExpressionException
	{
		xPathExpression = xpath.compile(xPathMyNonPriorityXPathNotification);
		String XPathMyNonPriorityXPathNotification = (String)xPathExpression.evaluate(nodeReport, XPathConstants.STRING);
		assertThat(XPathMyNonPriorityXPathNotification, is(notNullValue()));
		assertThat(XPathMyNonPriorityXPathNotification, is("7"));
	}

	@Test
	public void testXPathClientNonPriorityXPathNotification() throws XPathExpressionException
	{
		xPathExpression = xpath.compile(xPathClientNonPriorityXPathNotification);
		String XPathClientNonPriorityXPathNotification = (String)xPathExpression.evaluate(nodeReport, XPathConstants.STRING);
		assertThat(XPathClientNonPriorityXPathNotification, is(notNullValue()));
		assertThat(XPathClientNonPriorityXPathNotification, is("6"));
	}

	@Test
	public void testXPathMyPriorityXPathNotification() throws XPathExpressionException
	{
		xPathExpression = xpath.compile(xPathMyPriorityXPathNotification);
		String XPathMyPriorityXPathNotification = (String)xPathExpression.evaluate(nodeReport, XPathConstants.STRING);
		assertThat(XPathMyPriorityXPathNotification, is(notNullValue()));
		assertThat(XPathMyPriorityXPathNotification, is("0"));
	}

	private String readFile(String file) throws IOException
	{
		URL url = getClass().getResource(file);
		BufferedReader reader = new BufferedReader(new FileReader(new File(url.getPath())));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = reader.readLine()) != null)
		{
			stringBuilder.append(line);

		}

		return stringBuilder.toString();
	}

}
