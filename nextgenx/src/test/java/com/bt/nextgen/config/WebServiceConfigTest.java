package com.bt.nextgen.config;

import org.junit.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import static org.junit.Assert.assertNotNull;


public class WebServiceConfigTest
{
    /*@Autowired
    private BeanFactory beanFactory;
    */

    private WebServiceConfig webServiceConfig = new WebServiceConfig();

    @Test
    public void testMarshaller() throws Exception {
        Jaxb2Marshaller jaxb2Marshaller = webServiceConfig.marshaller();
        assertNotNull(jaxb2Marshaller);
        assertNotNull(jaxb2Marshaller.getContextPath());
    }

    @Test
    public void testUnmarshaller() throws Exception {
        Jaxb2Marshaller jaxb2Marshaller = webServiceConfig.unmarshaller();
        assertNotNull(jaxb2Marshaller);
        assertNotNull(jaxb2Marshaller.getPackagesToScan());
    }

/*	@Test
    public void testSaajSOAP12()
	{
		SoapMessageFactory soapMessageFactory = (SoapMessageFactory)beanFactory.getBean("saajSOAP12MessageFactory");
		assertNotNull(soapMessageFactory);
	}

	@Test
	public void testHttpMessageSender()
	{
		WebServiceMessageSender webServiceMessageSender = (WebServiceMessageSender)beanFactory.getBean("httpMessageSender");
		assertNotNull(webServiceMessageSender);
	}
  */
	/*@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException
	{
		this.beanFactory = beanFactory;

	} */


}
