package com.bt.nextgen.service.hpx.processor;

import com.bt.nextgen.service.hpx.HpxTranslationContext;
import com.bt.nextgen.service.hpx.StyleType;
import com.bt.nextgen.service.hpx.TextStyle;
import org.dom4j.Element;

public class HtmlStrongTagProcessor extends AbstractTagProcessor {
	@Override
	public TagProcessorResult process(HpxTranslationContext ctx, Element el, Element targetParentEl) {
		ctx.getStyleStack().push(new TextStyle(StyleType.BOLD));

		return TagProcessorResult.create(true);
	}

	@Override
	public void postProcess(HpxTranslationContext ctx, Element el, Element targetParentEl, TagProcessorResult tagProcessorResult) {
		ctx.getStyleStack().pop();
	}
}
