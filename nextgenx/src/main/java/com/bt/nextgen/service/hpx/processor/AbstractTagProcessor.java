package com.bt.nextgen.service.hpx.processor;

import com.bt.nextgen.service.hpx.HpxTagMappingServiceImpl;
import com.bt.nextgen.service.hpx.HpxTranslationContext;
import com.bt.nextgen.service.hpx.TagMappingService;
import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTagProcessor {
	// @Autowired
	protected TagMappingService tagMappingService = new HpxTagMappingServiceImpl();

	public abstract TagProcessorResult process(HpxTranslationContext ctx, Element el, Element targetParentEl);

	public void postProcess(HpxTranslationContext ctx, Element el, Element targetParentEl, TagProcessorResult tagProcessorResult) {
	}

	protected Map<String, String> getInlineStyles(Attribute attribute) {
		Map<String, String> stylesMap = new HashMap<>();

		String[] pairs = attribute.getValue().split(";");

		for (String pair : pairs) {
			String[] kv = pair.split(":");

			stylesMap.put(kv[0].trim(), kv[1].trim());
		}

		return stylesMap;
	}
}
