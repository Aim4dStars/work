package com.bt.nextgen.service.hpx;

public enum StyleType {
	BOLD(StyleGroup.DECORATION),
	ITALIC(StyleGroup.DECORATION),
	UNDERLINE(StyleGroup.DECORATION),
	NORMAL(StyleGroup.DECORATION),
	FONT(StyleGroup.FONT),
	FONT_SIZE(StyleGroup.FONT),
	COLOUR(StyleGroup.COLOUR);

	private StyleGroup styleGroup;

	StyleType(StyleGroup styleGroup) {
		this.styleGroup = styleGroup;
	}

	public StyleGroup getStyleGroup() {
		return styleGroup;
	}
}
