package com.bt.nextgen.util;

import java.io.IOException;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.xml.XmlUtil;

public class SamlUtil
{
	private static final String SAML_TOKEN_XPATH = "//*[local-name()='Assertion']";
	private static final String SAML_REGEX = "(?s)\\<saml\\:Assertion.*\\<\\/saml\\:Assertion\\>";

	private static final Logger logger = LoggerFactory.getLogger(SamlUtil.class);

	public static String loadSaml()
	{
		logger.debug("Loading Saml token without substitution");
		return loadSaml("/saml-sample.xml");
	}

	public static String loadWplSaml()
	{
		logger.debug("Loading WPL SAML token");
		return loadSaml("/direct-saml.xml");
	}

	public static String loadWplSamlNewPanoramaCustomer()
	{
		logger.debug("Loading WPL SAML token");
		return loadSaml("/direct-saml_new_panorama_customer.xml");
	}
	public static  String loadSamlwithoutPPId()
	{
		logger.debug("Loading SAML with PPId");
		return loadSaml("/PPIdSaml.xml");
	}

    public static  String loadSamlwithoutGCMId()
    {
        logger.debug("Loading Direct SAML without GCM");
        return loadSaml("/direct-saml-invalid.xml");
    }


    public static String loadSaml(Roles role)
	{
		switch (role)
		{
			case ROLE_INVESTOR:
				return loadSaml("/investor-saml-sample.xml");

			case ROLE_ADVISER:
				return loadSaml("/saml-sample.xml");

			default:
				return loadSaml("/saml-sample.xml");

		}

	}
	public static String loadSaml(String username, String[] authorities, String bankingCustomerId)
	{
		String role = "adviser";
		if(authorities.length>0)
			role = authorities[0];
		if(username==null || "".equals(username.trim()))
			return loadSaml();
		if(role==null || "".equals(role.trim()))
			return loadSaml();
		if(bankingCustomerId== null || "".equals(role.trim()))
			return loadSaml();

		String[] context = new String[3];
		context[0] = username;
		context[1] = role;
		context[2] = bankingCustomerId;

		return loadSaml("dynamic-saml.xml", context);
	}


	public static String loadSaml(String filename)
	{
		StringWriter writer = new StringWriter();
		try
		{
			IOUtils.copy(new ClassPathResource(filename).getInputStream(), writer);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		return writer.toString();

	}
	
	/**
	 *  Retrieves a SAML token from and XML document using Xpath
	 *
	 * @param xmlDocument
	 * @return  the resulting SamlToken
	 * @throws Exception
	 */
	public static SamlToken getSamlTokenFromDocument(Document xmlDocument)
	{
		XPath xPath =  new XPathFactoryImpl().newXPath();
		NodeList samlResult = null;
		try
		{
			XPathExpression xPathExpression = xPath.compile(SAML_TOKEN_XPATH);

			samlResult= (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);
		}
		catch(XPathExpressionException error)
		{
			throw new RuntimeException(error);
		}

		if(samlResult.getLength()!=1)
			throw new RuntimeException("No SAML token or too many SAML tokens present in the result");

		String saml = XmlUtil.printIdentical(samlResult.item(0));//stringFromXpathResult(samlResult.item(0));//

		return new SamlToken(saml);

	}

	/**
	 * Method to locate a saml Assertion in an xml file using regex
	 * @param xml The xml in which there is a SAML Token
	 * @return The extracted Saml
	 * @throws Exception if no Saml token can be found
	 */
	@Deprecated
	public static String extractSamlToken(String xml) throws Exception
	{
		Pattern p =  Pattern.compile(SAML_REGEX);
		Matcher matcher = p.matcher(xml);
		if(matcher.find())
			return xml.substring(matcher.start(), matcher.end());
		else
			throw new RuntimeException("Matcher Failed to find a SAML token");

	}

	public static String loadSaml(String filename,String[] usernameParts)
	{
		return MessageFormat.format(loadSaml(filename), usernameParts);
	}

}
