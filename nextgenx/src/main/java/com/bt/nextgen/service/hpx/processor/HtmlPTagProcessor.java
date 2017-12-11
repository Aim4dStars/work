package com.bt.nextgen.service.hpx.processor;

import com.bt.nextgen.service.hpx.HpxTranslationContext;
import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.Map;

public class HtmlPTagProcessor extends AbstractTagProcessor {
	private static final int INDENT_SIZE = 30;

	@Override
	public TagProcessorResult process(HpxTranslationContext ctx, Element el, Element targetParentEl) {
		// Handle indentation, if any
		Attribute attribute = el.attribute("style");

		if (attribute != null) {
			Map<String, String> inlineStyles = getInlineStyles(attribute);
			String paddingLeft = inlineStyles.get("padding-left");

			if (paddingLeft != null) {
				int indentLevel = Integer.parseInt(paddingLeft.replace("px", "").trim()) / INDENT_SIZE;
				String tagName = tagMappingService.getTabName(indentLevel);

				if (tagName != null) {
					return TagProcessorResult.create(tagName);
				}
			}
		}

		return TagProcessorResult.create(tagMappingService.getParagraphStyleName(el.getName()));
	}
}
