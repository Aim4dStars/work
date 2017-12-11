package com.bt.nextgen.service.hpx;

import com.bt.nextgen.service.hpx.processor.AbstractTagProcessor;
import com.bt.nextgen.service.hpx.processor.HtmlEmTagProcessor;
import com.bt.nextgen.service.hpx.processor.HtmlLiTagProcessor;
import com.bt.nextgen.service.hpx.processor.HtmlOlTagProcessor;
import com.bt.nextgen.service.hpx.processor.HtmlPTagProcessor;
import com.bt.nextgen.service.hpx.processor.HtmlSpanTagProcessor;
import com.bt.nextgen.service.hpx.processor.HtmlStrongTagProcessor;
import com.bt.nextgen.service.hpx.processor.HtmlUlTagProcessor;
import com.bt.nextgen.service.hpx.processor.TagProcessorResult;
import com.bt.nextgen.service.hpx.processor.TextProcessor;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.BaseElement;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts HTML output from TinyMce rich-text editor to HP Extreme XML format
 */
@Service("xhtmlToHpeTranslationServiceImpl")
public class HtmlToHpxTranslationServiceImpl implements HtmlTranslationService {
	private static final Logger logger = LoggerFactory.getLogger(HtmlToHpxTranslationServiceImpl.class);

	private static final String HPX_ROOT_NAME = "RichText";

	private static final Map<String, AbstractTagProcessor> tagProcessors = new HashMap<>();
	private static final TextProcessor textProcessor = new TextProcessor();

	// Could use stereotype instead but not worth the effort
	static {
		tagProcessors.put("em", new HtmlEmTagProcessor());
		tagProcessors.put("li", new HtmlLiTagProcessor());
		tagProcessors.put("ol", new HtmlOlTagProcessor());
		tagProcessors.put("p", new HtmlPTagProcessor());
		tagProcessors.put("strong", new HtmlStrongTagProcessor());
		tagProcessors.put("span", new HtmlSpanTagProcessor());
		tagProcessors.put("ul", new HtmlUlTagProcessor());
	}

	//	public static void main(String[] args) {
	//		HtmlToHpxTranslationServiceImpl service = new HtmlToHpxTranslationServiceImpl();
	//
	//		try {
	//			service.translate(FileUtils.readFileToString(new File("c:/Development/nextgen/richtext2.html")));
	//		} catch (HtmlTranslationException e) {
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}

	@Override
	public Object translate(String input) throws HtmlTranslationException {
		String html = prepareInput(input);

		InputStream inputStream = IOUtils.toInputStream(html, StandardCharsets.UTF_8);

		Document document;

		try {
			SAXReader reader = new SAXReader();
			document = reader.read(inputStream);
		} catch (DocumentException e) {
			throw new HtmlTranslationException(e);
		}

		HpxTranslationContext ctx = new HpxTranslationContext();

		Element rootEl = document.getRootElement();
		Element hpxEl = new BaseElement(HPX_ROOT_NAME);
		Element bodyEl = rootEl.element("body");

		if (bodyEl != null) {
			for (Node node : (List<Node>) bodyEl.content()) {
				processTag(ctx, node, hpxEl);
			}
		} else {
			throw new HtmlTranslationException("No HTML body found!");
		}

		//		printXml(hpxEl);

		return hpxEl;
	}

	private void processTag(HpxTranslationContext ctx, Node node, Element targetEl) {
		if (node instanceof DefaultElement) {
			AbstractTagProcessor tagProcessor = tagProcessors.get(node.getName());

			if (tagProcessor != null) {

				Element el = (DefaultElement) node;
				Element activeTargetEl;

				TagProcessorResult tagProcessorResult = tagProcessor.process(ctx, el, targetEl);

				if (tagProcessorResult.getResultEl() != null) {
					activeTargetEl = tagProcessorResult.getResultEl();
					targetEl.add(activeTargetEl);
				} else {
					activeTargetEl = targetEl;
				}

				for (Node childNode : (List<Node>) el.content()) {
					processTag(ctx, childNode, activeTargetEl);
				}

				tagProcessor.postProcess(ctx, el, targetEl, tagProcessorResult);
			} else {
				logger.warn("No processor for HTML tag \"" + node.getName() + "\"!");
			}
		} else if (node instanceof DefaultText) {
			textProcessor.addText(ctx, node.getText(), targetEl);
		}
	}

	private String prepareInput(String input) {
		return input.replace("<!DOCTYPE html>",
				"<!DOCTYPE html [<!ENTITY nbsp \"&#160;\"> <!ENTITY lt \"&#60;\"> <!ENTITY lt \"&#62;\"> <!ENTITY amp \"&#38;\">]>");
	}

	//	private void printXml(Element el) {
	//		OutputFormat format = OutputFormat.createPrettyPrint();
	//
	//		try {
	//			XMLWriter writer = new XMLWriter(System.out, format);
	//			writer.write(el);
	//			writer.close();
	//		} catch (UnsupportedEncodingException e) {
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}
}
