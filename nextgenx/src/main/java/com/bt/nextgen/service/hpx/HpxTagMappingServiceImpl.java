package com.bt.nextgen.service.hpx;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service("hpxTagMappingServiceImpl")
public class HpxTagMappingServiceImpl implements TagMappingService {
	private static final Logger logger = LoggerFactory.getLogger(HpxTagMappingServiceImpl.class);

	// TODO: Remove
	private Document document = null;

	//	public static void main(String[] args) {
	//		HpxTagMappingServiceImpl service = new HpxTagMappingServiceImpl();
	//
	//		System.out.println(service.getParagraphStyleName("p"));
	//		//Node node = service.getDocument().selectSingleNode("//text-decorations/text-decoration[@bold = 'true' and @italic = 'true' and @underline = 'false']");
	//
	//		//System.out.println(node.asXML());
	//	}

	@Override
	public String getTextDecorationName(boolean bold, boolean italic, boolean underline) {
		Node node = getDocument().selectSingleNode(
				"//text-decorations/text-decoration[@bold = '" + bold +
						"' and @italic = '" + italic +
						"' and @underline = '" + underline + "']");

		return node != null ? node.valueOf("@name") : null;
	}

	@Override
	public String getFontName(String family, String size) {
		String xpath;

		if (family != null && size != null) {
			xpath = "//fonts/font[@family = '" + family + "' and @size = '" + size + "']";
		} else if (family != null && size == null) {
			xpath = "//fonts/font[@family = '" + family + "']";
		} else if (family == null && size != null) {
			xpath = "//fonts/font[@size = '" + size + "']";
		} else {
			xpath = "//fonts/font";
		}

		Node node = getDocument().selectSingleNode(xpath);

		return node != null ? node.valueOf("@name") : null;
	}

	@Override
	public String getParagraphStyleName(String tag) {
		Node node = getDocument().selectSingleNode("//paragraph-styles/paragraph-style[@html = '" + tag + "']");

		return node != null ? node.valueOf("@name") : null;
	}

	@Override
	public String getTabName(int indentLevel) {
		Node node = getDocument().selectSingleNode("//tabs/tab[@level= '" + indentLevel + "']");

		return node != null ? node.valueOf("@name") : null;
	}

	@Override
	public String getColourName(Color colour) {
		Node node = getDocument().selectSingleNode(
				"//colours/colour[@red = '" + colour.getRed() +
						"' and @green = '" + colour.getGreen() +
						"' and @blue = '" + colour.getBlue() + "']");

		return node != null ? node.valueOf("@name") : null;
	}

	//@Cacheable("hpxMappings")
	public Document getDocument() {
		// TODO: Remove and use cache instead
		if (document != null) {
			return document;
		}

		try {
			SAXReader reader = new SAXReader();
			// TODO: use properties
			document = reader.read(HpxTagMappingServiceImpl.class.getClassLoader().getResourceAsStream("hpx/hpx-mapping.xml"));
		} catch (DocumentException e) {
			// Gobble up for now
			logger.error("Unable to load HPX mapping file", e);
		}

		return document;
	}
}
