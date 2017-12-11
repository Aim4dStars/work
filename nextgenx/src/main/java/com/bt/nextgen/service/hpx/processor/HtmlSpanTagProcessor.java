package com.bt.nextgen.service.hpx.processor;

import com.bt.nextgen.service.hpx.CompositeTextStyle;
import com.bt.nextgen.service.hpx.HpxTranslationContext;
import com.bt.nextgen.service.hpx.Style;
import com.bt.nextgen.service.hpx.StyleType;
import com.bt.nextgen.service.hpx.TextStyle;
import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HtmlSpanTagProcessor extends AbstractTagProcessor {
	@Override
	public TagProcessorResult process(HpxTranslationContext ctx, Element el, Element targetParentEl) {
		Attribute attribute = el.attribute("style");
		boolean styleStackModified = false;

		if (attribute != null) {
			styleStackModified = processStyles(ctx, getInlineStyles(attribute));
		}

		return TagProcessorResult.create(styleStackModified);
	}

	@Override
	public void postProcess(HpxTranslationContext ctx, Element el, Element targetParentEl, TagProcessorResult tagProcessorResult) {
		if (tagProcessorResult.isStyleStackModified()) {
			ctx.getStyleStack().pop();
		}
	}

	private boolean processStyles(HpxTranslationContext ctx, Map<String, String> inlineStyles) {
		List<Style> styles = new ArrayList<>();

		String textDecor = inlineStyles.get("text-decoration");

		if (textDecor != null && "underline".equals(textDecor)) {
			// No need to handle bold or italics as TinyMce uses <strong> and <em> tag for them
			styles.add(new TextStyle(StyleType.UNDERLINE));
		}

		String fontFamily = inlineStyles.get("font-family");

		if (fontFamily != null) {
			String[] fonts = fontFamily.split(",");

			// Just use the first font for now
			styles.add(new TextStyle(StyleType.FONT, fonts[0].replace("'", "").trim()));
		}

		String fontSize = inlineStyles.get("font-size");

		if (fontSize != null) {
			styles.add(new TextStyle(StyleType.FONT_SIZE, fontSize.replace("pt", "").trim()));
		}

		String colour = inlineStyles.get("color");

		if (colour != null) {
			styles.add(new TextStyle(StyleType.COLOUR, "0x" + colour.replace("#", "").trim()));
		}

		if (!styles.isEmpty()) {
			if (styles.size() == 1) {
				ctx.getStyleStack().push(styles.get(0));
			} else {
				ctx.getStyleStack().push(new CompositeTextStyle(styles));
			}

			return true;
		}

		return false;
	}
}
