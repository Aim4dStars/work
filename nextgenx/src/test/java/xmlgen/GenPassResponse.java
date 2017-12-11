package xmlgen;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.MaintainChannelAccessServicePasswordResponse;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.ObjectFactory;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

public class GenPassResponse {

	public static void main(String[] args) throws JAXBException, DatatypeConfigurationException
	{

		
		/**
		 *                                 MaintainChannelAccessServicePasswordResponse newResponse = (MaintainChannelAccessServicePasswordResponse )provider.sendWebService("stub", r);
                                logger.info("after calling provider.sendWebsService");
                                ServiceStatus serviceStatus = newResponse.getServiceStatus();
                                for( StatusInfo statusInfo : serviceStatus.getStatusInfo())
                                {
                                                logger.info("statusInfo: ===>>>>>"+statusInfo.getCode());
                                                return statusInfo.getCode();
                                }
                                
                                MaintainChannelAccessServicePasswordResponse response = new MaintainChannelAccessServicePasswordResponse();
                                ServiceStatus serviceStatus = new ServiceStatus();

                                response.setServiceStatus(serviceStatus);


		 */
		
		ObjectFactory factory = new ObjectFactory();

		MaintainChannelAccessServicePasswordResponse response = factory.createMaintainChannelAccessServicePasswordResponse();
		 ServiceStatus serviceStatus = new ServiceStatus();
		 StatusInfo StatusInfo = new StatusInfo();
		 StatusInfo.setCode("123");
		 serviceStatus.getStatusInfo().add(StatusInfo);
		response.setServiceStatus(serviceStatus);
		
		
		JAXBContext context = JAXBContext.newInstance(MaintainChannelAccessServicePasswordResponse.class);
		JAXBElement element = factory.createMaintainChannelAccessServicePasswordResponse(response);

		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
		marshaller.marshal(element, System.out);
	}

}
