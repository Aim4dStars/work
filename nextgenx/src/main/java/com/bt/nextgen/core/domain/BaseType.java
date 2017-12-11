package com.bt.nextgen.core.domain;

import com.google.common.base.Objects;

import java.io.Serializable;

public abstract class BaseType<T extends Comparable<T>> implements Serializable
{
	private final T value;

	public BaseType(T value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value.toString();
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(value);
	}

	@Override
	public boolean equals(Object obj)
	{
		BaseType<T> that = (BaseType<T>) obj;
		return Objects.equal(this.value, that.value);
	}

	public T getValue()
	{
		return value;
	}
}
