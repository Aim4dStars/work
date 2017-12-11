package com.bt.nextgen.login.util;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;

import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.xml.XmlUtil;

public class SamlUtilTest
{

 @Test
 public void testLoadSaml()
  {
	String response = SamlUtil.loadSaml();
	assertThat(response, is(notNullValue()));
 }	
	
 @Test
 public void testLoadSamlWithFileName()
 {
	String filename = "/saml-sample.xml";
	String response = SamlUtil.loadSaml(filename);
	assertThat(response, is(notNullValue()));
 }
 
//no point to test a deprecated method which is not even being used in application
 @Ignore
 @Test
 public void testGetSamlTokenFromDocument() throws Exception
 {
	StringWriter writer = new StringWriter();
	IOUtils.copy(new ClassPathResource("server-saml-response.xml").getInputStream(), writer);

	String securityTokenResponse = writer.toString();
	//System.out.println(securityTokenResponse);

	Document xmlDocument = XmlUtil.parseDocument(securityTokenResponse);
	SamlToken samlToken = SamlUtil.getSamlTokenFromDocument(xmlDocument);
	assertThat(samlToken,is(notNullValue()));
	SamlToken samlTokenExtractedByRegex = new SamlToken(SamlUtil.extractSamlToken(securityTokenResponse));
	assertThat(
            samlToken.getToken().replaceAll("\r\n", "\n"),
            equalTo(
                samlTokenExtractedByRegex.getToken().replaceAll("\r\n", "\n")
            )
    );
 }

}
