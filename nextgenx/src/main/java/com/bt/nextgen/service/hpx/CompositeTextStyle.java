package com.bt.nextgen.service.hpx;

import java.util.List;

public class CompositeTextStyle implements Style {
	private final List<Style> styles;

	public CompositeTextStyle(List<Style> styles) {
		this.styles = styles;
	}

	public List<Style> getStyles() {
		return styles;
	}
}
