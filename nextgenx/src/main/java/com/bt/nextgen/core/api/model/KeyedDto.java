package com.bt.nextgen.core.api.model;

public interface KeyedDto <K> extends Dto
{
	public abstract K getKey();
}
