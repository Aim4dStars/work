package com.bt.nextgen.service.hpx.processor;

import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

public class TagProcessorResult {
	private final Element resultEl;
	private final boolean styleStackModified;

	private TagProcessorResult(Element resultEl, boolean styleStackModified) {
		this.resultEl = resultEl;
		this.styleStackModified = styleStackModified;
	}

	public static TagProcessorResult create(String elName) {
		return new TagProcessorResult(new BaseElement(elName), false);
	}

	public static TagProcessorResult create(Element resultEl) {
		return new TagProcessorResult(resultEl, false);
	}

	public static TagProcessorResult create(boolean styleStackModified) {
		return new TagProcessorResult(null, styleStackModified);
	}

	public Element getResultEl() {
		return resultEl;
	}

	public boolean isStyleStackModified() {
		return styleStackModified;
	}
}
