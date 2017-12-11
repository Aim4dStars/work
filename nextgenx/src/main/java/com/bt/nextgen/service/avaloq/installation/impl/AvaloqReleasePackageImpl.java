package com.bt.nextgen.service.avaloq.installation.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.impl.auth.UnsupportedDigestAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.installation.AvaloqChange;
import com.bt.nextgen.service.avaloq.installation.AvaloqReleasePackage;

@ServiceBean(
	xpath = "release",
	type = ServiceBeanType.CONCRETE
)
public class AvaloqReleasePackageImpl implements AvaloqReleasePackage
{

	@Override public int hashCode()
	{
		return releaseName != null ? releaseName.hashCode() : 0;
	}

	private static Logger logger = LoggerFactory.getLogger(AvaloqReleasePackageImpl.class);

	@ServiceElement(xpath = "release_head_list/release_head/release/val")
	private String releaseName;

	@ServiceElementList(xpath="chg_list/chg", type=AvaloqChangeImpl.class )
	private List<AvaloqChange> avaloqChanges;

	private String releaseMd5sum;

	@Override public String getAvaloqReleaseName()
	{
		return this.releaseName;
	}

	@Override public List<AvaloqChange> getAvaloqChanges()
	{
		return this.avaloqChanges;
	}

	@Override public String getAvaloqPackageUid()
	{
		if(releaseMd5sum==null)
		{
			this.releaseMd5sum = getUidFromChanges(this.avaloqChanges);
			logger.info("Release MD5 sum set to {}",this.releaseMd5sum);
		}
		return releaseMd5sum;
	}

	private static String getUidFromChanges(List<AvaloqChange> changeList)
	{
		StringBuffer allChanges = new StringBuffer();
		Collections.sort(changeList);
		for(AvaloqChange change:changeList)
			allChanges.append(change.getId()).append(',');
		String packageChangeList = allChanges.toString();
		logger.debug("List of changes is {}", packageChangeList );
		try
		{
			return DigestUtils.md5Hex(packageChangeList);

		}catch(UnsupportedDigestAlgorithmException err)
		{
			logger.info("Failed to load MD5 sum algorith, setting unique release ID to be complete change string",err);
			return packageChangeList;
		}
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

		AvaloqReleasePackageImpl that = (AvaloqReleasePackageImpl) o;

		if (releaseName != null ? !releaseName.equals(that.releaseName) : that.releaseName != null)
		{
			return false;
		}
		if (this.getAvaloqPackageUid() != null ? !this.getAvaloqPackageUid().equals(that.getAvaloqPackageUid()) : that.getAvaloqPackageUid() != null)
		{
			return false;
		}

		return true;
	}


	@Override public int compareTo(AvaloqReleasePackage o)
	{
		return this.getAvaloqReleaseName().compareTo(o.getAvaloqReleaseName());
	}



}
