package com.bt.nextgen.service.hpx.processor;

import com.bt.nextgen.service.hpx.HpxTranslationContext;
import com.bt.nextgen.service.hpx.ListStyleType;
import org.dom4j.Element;

public class HtmlUlTagProcessor extends AbstractTagProcessor {
	@Override
	public TagProcessorResult process(HpxTranslationContext ctx, Element el, Element targetParentEl) {
		ctx.getListStyleTypeStack().push(ListStyleType.UNORDERED_LIST);

		return TagProcessorResult.create(true);
	}

	@Override
	public void postProcess(HpxTranslationContext ctx, Element el, Element targetParentEl, TagProcessorResult tagProcessorResult) {
		ctx.getListStyleTypeStack().pop();
	}
}
