package com.bt.nextgen.service.hpx.processor;

import com.bt.nextgen.service.hpx.HpxTranslationContext;
import com.bt.nextgen.service.hpx.ListStyleType;
import org.dom4j.Element;

public class HtmlLiTagProcessor extends AbstractTagProcessor {
	@Override
	public TagProcessorResult process(HpxTranslationContext ctx, Element el, Element targetParentEl) {

		if (!ctx.getListStyleTypeStack().isEmpty()) {
			ListStyleType listStyleType = ctx.getListStyleTypeStack().peek();

			if (listStyleType != null) {
				return TagProcessorResult.create(tagMappingService.getParagraphStyleName(listStyleType.getTagName()));
			}
		}

		// Default to unordered list
		return TagProcessorResult.create(tagMappingService.getParagraphStyleName(ListStyleType.UNORDERED_LIST.getTagName()));
	}
}
