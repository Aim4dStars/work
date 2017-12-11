package com.bt.nextgen.service.avaloq.installation.impl;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.installation.AvaloqChange;

@ServiceBean(
	xpath = "chg",
	type = ServiceBeanType.CONCRETE
)
public class AvaloqChangeImpl implements AvaloqChange
{
	@ServiceElement(xpath = "chg_head_list/chg_head/chg_id/val")
	private String id;

	@ServiceElement(xpath = "chg_head_list/chg_head/chg_name/val")
	private String name;

	@ServiceElement(xpath = "chg_head_list/chg_head/inst_time/val")
	private String installationTimeAsString;
	private DateTime installationTime;

	@ServiceElement(xpath = "chg_head_list/chg_head/task_id/val")
	private String taskId;

	@ServiceElement(xpath = "chg_head_list/chg_head/task_name/val")
	private String taskName;

	@ServiceElement(xpath = "chg_head_list/chg_head/user_1/val")
	private String user1;

	@ServiceElement(xpath = "chg_head_list/chg_head/user_2/val")
	private String user2;

	@ServiceElement(xpath = "chg_head_list/chg_head/user_3/val")
	private String user3;

	@ServiceElement(xpath = "chg_head_list/chg_head/user_4/val")
	private String user4;

	@ServiceElement(xpath = "chg_head_list/chg_head/user_5/val")
	private String user5;

	private List<String> authors;


	@Override public String getId()
	{
		return this.id;
	}

	@Override public String getName()
	{
		return this.name;
	}

	@Override public DateTime getInstallationTime()
	{
		if(installationTime==null && installationTimeAsString!=null)
			installationTime = DateTime.parse(installationTimeAsString);

		return this.installationTime;
	}

	@Override public List<String> getAuthors()
	{
		if(authors==null)
			generateAuthorList();
		return this.authors;
	}

	private void generateAuthorList()
	{
		this.authors = new ArrayList<>();
		if(user1!=null)
			this.authors.add(user1);
		if(user2!=null)
			this.authors.add(user2);

		if(user3!=null)
			this.authors.add(user3);
		if(user4!=null)
			this.authors.add(user4);

		if(user5!=null)
			this.authors.add(user5);


	}

	@Override public String getTaskId()
	{
		return this.taskId;
	}

	@Override public String getTaskName()
	{
		return this.taskName;
	}

	@Override public int compareTo(AvaloqChange o)
	{
		if(equals(o))
			return 0;

		Integer intId = Integer.parseInt(this.id);
		Integer oIntId = Integer.parseInt(o.getId());
		return intId - oIntId;

	}


	@Override public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		AvaloqChangeImpl that = (AvaloqChangeImpl) o;

		return !(id != null ? !id.equals(that.id) : that.id != null);

	}

	@Override public int hashCode()
	{
		return id != null ? id.hashCode() : 0;
	}
}
