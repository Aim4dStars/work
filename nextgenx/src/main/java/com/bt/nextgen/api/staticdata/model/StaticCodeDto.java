package com.bt.nextgen.api.staticdata.model;

import com.bt.nextgen.core.api.model.BaseDto;

import static org.springframework.util.ObjectUtils.nullSafeEquals;
import static org.springframework.util.ObjectUtils.nullSafeHashCode;

/**
 * This is an Dto modal class which represents static data from the Avaloq
 * @author L062329
 */
public class StaticCodeDto extends BaseDto
{
	private String id;
	private String name;
	private String value;
	private String intlId;
	private String listName;

	public StaticCodeDto(String id, String name, String value, String intlId, String listName) {
		this.id = id;
		this.name = name;
		this.value = value;
		this.intlId = intlId;
		this.listName = listName;
	}

	public StaticCodeDto(StaticCodeDto code) {
		this(code.id, code.name, code.value, code.intlId, code.listName);
	}

	public StaticCodeDto() {
	}

	public String getListName()
	{
		return listName;
	}

	public void setListName(String listName)
	{
		this.listName = listName;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getLabel()
	{
		return name;
	}

	public void setLabel(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getIntlId()
	{
		return intlId;
	}

	public void setIntlId(String intlId)
	{
		this.intlId = intlId;
	}

	private Object[] fields() {
		return new Object[]{ id, name, value, intlId, listName };
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StaticCodeDto) {
			final StaticCodeDto code = (StaticCodeDto) o;
			return nullSafeEquals(fields(), code.fields());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return nullSafeHashCode(fields());
	}
}
