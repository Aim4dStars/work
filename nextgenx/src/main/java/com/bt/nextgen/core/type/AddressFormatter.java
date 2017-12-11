package com.bt.nextgen.core.type;

import com.bt.nextgen.service.integration.domain.Address;
import com.btfin.panorama.core.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddressFormatter 
{
	private static Logger logger = LoggerFactory.getLogger(AddressFormatter.class);
	
	
	public static String getAddressLineTwo(Address address)
	{
		String addressLine2 = "";
		
		if (!StringUtils.isEmpty(address.getSuburb()))
		{
			addressLine2 = address.getSuburb();
		}
		
		return addressLine2;
	}
	
	
	/**
	 * Generate display value for Addresses (First line):
	 * <p>
	 * <ol>
	 * <li>33 Pitt Street</li>
	 * <li>275 Kent Street</li>
	 * <li> PO BOX 670,</li>
	 * </ol>
	 * </p>
	 * @param address
	 * @return
	 */
	public static String getAddressLineOne(Address address)
	{		
		StringBuilder addressLine1 = new StringBuilder();
     // This code is commented because it showing up wrong address foramt. Street number should shown first.
	/*	if (address.getPoBoxPrefix() != null && !StringUtils.isEmpty(address.getPoBoxPrefix()) && address.getPoBox() != null
				&& !StringUtils.isEmpty(address.getPoBox()))
		{		
			if (!StringUtils.isEmpty(address.getPoBoxPrefix()))
			{
				addressLine1.append(BoxPrefix.PO_BOX.getCode()).append(" ").append(address.getPoBox());
			}
			else if (address.getPoBoxPrefix().equalsIgnoreCase(BoxPrefix.GPO_BOX.getCode()))
			{
				addressLine1.append(BoxPrefix.GPO_BOX.getDisplayName()).append(" ").append(address.getPoBox());
			}
			else if (address.getPoBoxPrefix().equalsIgnoreCase(BoxPrefix.CARE_PO.getCode()))
			{
				addressLine1.append(BoxPrefix.CARE_PO.getDisplayName()).append(" ").append(address.getPoBox());
			}
			else if (address.getPoBoxPrefix().equalsIgnoreCase(BoxPrefix.CMA.getCode()))
			{
				addressLine1.append(BoxPrefix.CMA.getDisplayName()).append(" ").append(address.getPoBox());
			}
			else if (address.getPoBoxPrefix().equalsIgnoreCase(BoxPrefix.CMB.getCode()))
			{
				addressLine1.append(BoxPrefix.CMB.getDisplayName()).append(" ").append(address.getPoBox());
			}
			else if (address.getPoBoxPrefix().equalsIgnoreCase(BoxPrefix.CPA.getCode()))
			{
				addressLine1.append(BoxPrefix.CPA.getDisplayName()).append(" ").append(address.getPoBox());
			}
			else if (address.getPoBoxPrefix().equalsIgnoreCase(BoxPrefix.LOCKED_BAG.getCode()))
			{
				addressLine1.append(BoxPrefix.LOCKED_BAG.getDisplayName()).append(" ").append(address.getPoBox());
			}
			else if (address.getPoBoxPrefix().equalsIgnoreCase(BoxPrefix.MS.getCode()))
			{
				addressLine1.append(BoxPrefix.MS.getDisplayName()).append(" ").append(address.getPoBox());
			}
			else if (address.getPoBoxPrefix().equalsIgnoreCase(BoxPrefix.PRIVATE_BAG.getCode()))
			{
				addressLine1.append(BoxPrefix.PRIVATE_BAG.getDisplayName()).append(" ").append(address.getPoBox());
			}
			else if (address.getPoBoxPrefix().equalsIgnoreCase(BoxPrefix.RMB.getCode()))
			{
				addressLine1.append(BoxPrefix.RMB.getDisplayName()).append(" ").append(address.getPoBox());
			}
			else if (address.getPoBoxPrefix().equalsIgnoreCase(BoxPrefix.RMS.getCode()))
			{
				addressLine1.append(BoxPrefix.RMS.getDisplayName()).append(" ").append(address.getPoBox());
			}
			else if (address.getPoBoxPrefix().equalsIgnoreCase(BoxPrefix.RSD.getCode()))
			{
				addressLine1.append(BoxPrefix.RSD.getDisplayName()).append(" ").append(address.getPoBox());
			}
			
			if (StringUtil.isNotNullorEmpty(address.getStreetNumber())
					|| StringUtil.isNotNullorEmpty(address.getStreetName())
					|| StringUtil.isNotNullorEmpty(address.getStreetType()))
			{
				addressLine1.append(",");
			}
		}*/

		if (StringUtil.isNotNullorEmpty(address.getStreetNumber())){

		addressLine1.append(address.getStreetNumber());

			if(StringUtil.isNotNullorEmpty(address.getStreetName())){

				addressLine1.append(" ");
			}

		}
		if (StringUtil.isNotNullorEmpty(address.getStreetName()) ){

			addressLine1.append(address.getStreetName());

			if(StringUtil.isNotNullorEmpty(address.getStreetType())){

				addressLine1.append(" ");
			}
		}

		if (StringUtil.isNotNullorEmpty(address.getStreetType()) ){

			addressLine1.append(address.getStreetType());
		}

		/*ddressLine1.append(address.getStreetNumber()).append(" ")
					.append(address.getStreetName()).append(" ")
					.append(address.getStreetType());
		*/
		
		logger.debug("Formatted address line 1 with box prefix: {}", addressLine1);		
		return addressLine1.toString();
	}
}
