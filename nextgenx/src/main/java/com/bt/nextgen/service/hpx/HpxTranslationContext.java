package com.bt.nextgen.service.hpx;

import java.util.ArrayDeque;
import java.util.Deque;

public class HpxTranslationContext {
	private final Deque<Style> styleStack = new ArrayDeque<>();
	private final Deque<ListStyleType> listStyleTypeStack = new ArrayDeque<>();

	public Deque<Style> getStyleStack() {
		return styleStack;
	}

	public Deque<ListStyleType> getListStyleTypeStack() {
		return listStyleTypeStack;
	}
}
