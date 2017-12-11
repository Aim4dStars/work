package com.bt.nextgen.service.hpx;

public enum ListStyleType {
	ORDERED_LIST("ol"),
	UNORDERED_LIST("ul");

	private String tagName;

	ListStyleType(String tagName) {
		this.tagName = tagName;
	}

	public String getTagName() {
		return tagName;
	}
}
