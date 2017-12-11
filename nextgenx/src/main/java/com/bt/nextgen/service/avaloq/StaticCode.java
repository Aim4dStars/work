package com.bt.nextgen.service.avaloq;

public class StaticCode implements StaticCodeInterface
{
	private String id;
	private String name;
	private String value;

	public StaticCode(String id, String name, String value)
	{
		this.id = id;
		this.name = name;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.core.avaloq.StaticCodeInterface#getId()
	 */
	@Override
	public String getId()
	{
		return id;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.core.avaloq.StaticCodeInterface#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.core.avaloq.StaticCodeInterface#getValue()
	 */
	@Override
	public String getValue()
	{
		return value;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.core.avaloq.StaticCodeInterface#setName(java.lang.String)
	 */
	@Override
	public void setName(String name)
	{
		this.name = name;
	}
}
