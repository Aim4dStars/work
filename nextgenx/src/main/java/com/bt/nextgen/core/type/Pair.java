package com.bt.nextgen.core.type;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @Deprecated use org.apache.commons.lang3.tuple.Pair
 */
@Deprecated
public class Pair<K, V> implements Serializable
{
	private K key;
	private V value;

	public Pair(K key, V value)
	{
		this.key = key;
		this.value = value;
	}

	public static <K, V> Pair<K, V> create(K key, V value)
	{
		return new Pair<K, V>(key, value);
	}

	public void setKey(K key)
	{
		this.key = key;
	}

	public void setValue(V value)
	{
		this.value = value;
	}

	public K getKey()
	{
		return key;
	}

	public V getValue()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof Pair)
		{
			Pair other = (Pair) obj;
			return new EqualsBuilder().append(this.getKey(), other.getKey()).append(this.getValue(),
				other.getValue()).isEquals();
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder().append(key).append(value).toHashCode();
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
