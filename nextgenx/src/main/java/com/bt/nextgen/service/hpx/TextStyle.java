package com.bt.nextgen.service.hpx;

public class TextStyle implements Style {
	private final StyleType styleName;
	private final String value;

	public TextStyle(StyleType styleName) {
		this.styleName = styleName;
		this.value = null;
	}

	public TextStyle(StyleType styleName, String value) {
		this.styleName = styleName;
		this.value = value;
	}

	public StyleType getStyleType() {
		return styleName;
	}

	public String getValue() {
		return value;
	}
}
