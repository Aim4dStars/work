package com.bt.nextgen.service.hpx.processor;

import com.bt.nextgen.service.hpx.CompositeTextStyle;
import com.bt.nextgen.service.hpx.HpxTagMappingServiceImpl;
import com.bt.nextgen.service.hpx.HpxTranslationContext;
import com.bt.nextgen.service.hpx.Style;
import com.bt.nextgen.service.hpx.StyleGroup;
import com.bt.nextgen.service.hpx.StyleType;
import com.bt.nextgen.service.hpx.TagMappingService;
import com.bt.nextgen.service.hpx.TextStyle;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TextProcessor {
	// @Autowired
	protected TagMappingService tagMappingService = new HpxTagMappingServiceImpl();

	public void addText(HpxTranslationContext ctx, String text, Element targetEl) {
		if (!ctx.getStyleStack().isEmpty()) {
			Element styleEl = createStyleElement(ctx);

			if (styleEl != null) {
				addTextToChildEl(styleEl, text);
				targetEl.add(styleEl);
			} else {
				// Just add the text as there could be missing mapping
				targetEl.add(new DefaultText(text));
			}
		} else {
			// No styling.  Might have to default to <normal>
			targetEl.addText(text);
		}
	}

	private Element createStyleElement(HpxTranslationContext ctx) {
		List<Style> styles = new ArrayList<>(ctx.getStyleStack().size());

		// Convert deque to list as it is easier to work with.
		// Arrays.toList() or Collections.addAll() does not work with Deque
		for (Iterator<Style> it = ctx.getStyleStack().descendingIterator(); it.hasNext(); ) {
			styles.add(it.next());
		}

		TextDecoration textDecoration = new TextDecoration();
		FontProperties fontProperties = new FontProperties();

		populateTextDecoration(styles, textDecoration);
		populateFontProperties(styles, fontProperties);

		Color textColour = getTextColour(styles);

		Element styleEl = null;

		if (textColour != null) {
			styleEl = addStyleElement(tagMappingService.getColourName(textColour), styleEl);
		}

		if (fontProperties.hasValues()) {
			styleEl = addStyleElement(tagMappingService.getFontName(fontProperties.getFamily(), fontProperties.getSize()), styleEl);
		}

		if (textDecoration.hasValues()) {
			styleEl = addStyleElement(
					tagMappingService.getTextDecorationName(textDecoration.isBold(), textDecoration.isItalic(),
							textDecoration.isUnderline()),
					styleEl);
		}

		return styleEl;
	}

	private Element addStyleElement(String tagName, Element styleEl) {
		if (tagName != null) {
			if (styleEl == null) {
				return new DefaultElement(tagName);
			} else {
				styleEl.add(new DefaultElement(tagName));
			}
		}

		return styleEl;
	}

	private void addTextToChildEl(Element styleEl, String text) {
		if (styleEl.hasContent()) {
			for (Element el : (List<Element>) styleEl.elements()) {
				addTextToChildEl(el, text);
			}
		} else {
			styleEl.add(new DefaultText(text));
		}
	}

	private void populateTextDecoration(List<Style> styles, TextDecoration textDecoration) {
		for (Style style : styles) {
			if (style instanceof CompositeTextStyle) {
				CompositeTextStyle compositeTextStyle = (CompositeTextStyle) style;
				populateTextDecoration(compositeTextStyle.getStyles(), textDecoration);
			} else if (StyleGroup.DECORATION.equals(((TextStyle) style).getStyleType().getStyleGroup())) {
				setTextDecoration((TextStyle) style, textDecoration);
			}

			if (textDecoration.isComplete()) {
				break;
			}
		}
	}

	private void populateFontProperties(List<Style> styles, FontProperties fontProperties) {
		// Reverse look-up
		for (int i = styles.size() - 1; i >= 0; i--) {
			Style style = styles.get(i);

			if (style instanceof CompositeTextStyle) {
				CompositeTextStyle compositeTextStyle = (CompositeTextStyle) style;
				populateFontProperties(compositeTextStyle.getStyles(), fontProperties);
			} else if (StyleGroup.FONT.equals(((TextStyle) style).getStyleType().getStyleGroup())) {
				setFontProperties((TextStyle) style, fontProperties);
			}

			if (fontProperties.isComplete()) {
				break;
			}
		}
	}

	private void setTextDecoration(TextStyle textStyle, TextDecoration textDecoration) {
		if (StyleType.BOLD.equals(textStyle.getStyleType())) {
			textDecoration.setBold(true);
		} else if (StyleType.ITALIC.equals(textStyle.getStyleType())) {
			textDecoration.setItalic(true);
		} else if (StyleType.UNDERLINE.equals(textStyle.getStyleType())) {
			textDecoration.setUnderline(true);
		}
	}

	private void setFontProperties(TextStyle textStyle, FontProperties fontProperties) {
		if (StyleType.FONT.equals(textStyle.getStyleType())) {
			fontProperties.setFamily(textStyle.getValue());
		} else if (StyleType.FONT_SIZE.equals(textStyle.getStyleType())) {
			fontProperties.setSize(textStyle.getValue());
		}
	}

	private Color getTextColour(List<Style> styles) {
		Color colour = null;

		// Reverse look-up
		for (int i = styles.size() - 1; i >= 0; i--) {
			Style style = styles.get(i);

			if (style instanceof CompositeTextStyle) {
				CompositeTextStyle compositeTextStyle = (CompositeTextStyle) style;
				colour = getTextColour(compositeTextStyle.getStyles());
			} else if (StyleGroup.COLOUR.equals(((TextStyle) style).getStyleType().getStyleGroup())) {
				colour = Color.decode(((TextStyle) style).getValue());
			}

			// Just needs first instance
			if (colour != null) {
				break;
			}
		}

		return colour;
	}

	private class TextDecoration {
		private boolean bold;
		private boolean italic;
		private boolean underline;

		public boolean isBold() {
			return bold;
		}

		public void setBold(boolean bold) {
			this.bold = bold;
		}

		public boolean isItalic() {
			return italic;
		}

		public void setItalic(boolean italic) {
			this.italic = italic;
		}

		public boolean isUnderline() {
			return underline;
		}

		public void setUnderline(boolean underline) {
			this.underline = underline;
		}

		public boolean isComplete() {
			return bold && italic && underline;
		}

		public boolean hasValues() {
			return bold || italic || underline;
		}
	}

	private class FontProperties {
		private String family;
		private String size;

		public String getFamily() {
			return family;
		}

		public void setFamily(String family) {
			if (this.family == null) {
				this.family = family;
			}
		}

		public String getSize() {
			return size;
		}

		public void setSize(String size) {
			if (this.size == null) {
				this.size = size;
			}
		}

		public boolean isComplete() {
			return family != null && size != null;
		}

		public boolean hasValues() {
			return family != null || size != null;
		}
	}
}
