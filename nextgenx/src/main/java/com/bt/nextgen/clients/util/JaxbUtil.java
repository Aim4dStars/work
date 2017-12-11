package com.bt.nextgen.clients.util;

import static com.bt.nextgen.core.xml.XmlUtil.getXMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class JaxbUtil
{
	private JaxbUtil()
	{}

	@SuppressWarnings("unchecked")
	public static <T> T unmarshall(String fileName, Class clazz)
	{
		try (InputStream file = new ClassPathResource(fileName).getInputStream())
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			return (T)unmarshaller.unmarshal(getXMLReader(file));
		}
		catch (IOException | XMLStreamException e)
		{
			throw new RuntimeException("Failure to load resource " + fileName, e);
		}
		catch (JAXBException e)
		{
			throw new RuntimeException("Failure unmarshalling ", e);
		}
	}

	public static <T> T unmarshall(InputStream inputStream, Class clazz)
	{
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			return (T)unmarshaller.unmarshal(getXMLReader(inputStream));
		}
		catch (JAXBException | XMLStreamException e)
		{
			throw new RuntimeException("Failure unmarshalling ", e);
		}
	}

	public static <T> T unmarshall(Resource resource, Class clazz)
	{
		try (InputStream file = resource.getInputStream())
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			return (T)unmarshaller.unmarshal(getXMLReader(file));
		}
		catch (IOException | XMLStreamException e)
		{
			throw new RuntimeException("Failure to load resource " + resource, e);
		}
		catch (JAXBException e)
		{
			throw new RuntimeException("Failure unmarshalling ", e);
		}
	}

	public static void marshall(OutputStream writeHere, @SuppressWarnings("rawtypes") Class clazz, Object object)
	{
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(object, writeHere);
		}
		catch (JAXBException e)
		{
			throw new RuntimeException("Failure marshalling ", e);
		}
	}

	public static void marshall(OutputStream writeHere, Object instance)
	{
		marshall(writeHere, instance.getClass(), instance);
	}
}
