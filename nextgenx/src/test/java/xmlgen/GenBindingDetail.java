package xmlgen;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.xml.transform.StringSource;
import org.uddi.api_v3.BindingDetail;

public class GenBindingDetail {

	public static void main(String[] args) throws Exception {

		String xml = "<bindingDetail generic=\"3.0\" operator=\"UDDI_DEV1\" truncated=\"false\" xmlns=\"urn:uddi-org:api_v2\">\n"
				+ "         <bindingTemplate bindingKey=\"4c4ad71f-74a4-419a-b973-44791b81b75f\" serviceKey=\"e7b3a12f-5655-4db3-9c75-94a3ba33aef0\">\n"
				+ "            <description xml:lang=\"en\">Avaloq Gateway Adapter End Point</description>\n"
				+ "            <accessPoint URLType=\"other\">https://SomeDummyAddress</accessPoint>\n"
				+ "            <tModelInstanceDetails/>\n"
				+ "         </bindingTemplate>\n" + "      </bindingDetail>\n";
		
		JAXBContext context = JAXBContext.newInstance(BindingDetail.class);
		
		 
		JAXBContext context2 = JAXBContext.newInstance(BindingDetail.class,
				BindingDetail.class);
		
		StringSource source = new StringSource(xml);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Object object = unmarshaller.unmarshal(source);
		System.out.println(object.getClass());
		
		Unmarshaller unmarshaller2 = context2.createUnmarshaller();
		Object object2 = unmarshaller2.unmarshal(source);
		System.out.println(object2.getClass());
				
	}

}
