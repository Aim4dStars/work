/**
 * 
 */
package com.bt.nextgen.service.onboarding.btesb;

/**
 * @author L055011
 *
 */
public class OnboardingObjectFactory 
{
	private static final ns.btfin_com.sharedservices.common.contact.v1_1.ObjectFactory contactObjectFactory = new ns.btfin_com.sharedservices.common.contact.v1_1.ObjectFactory();
	
	private static final ns.btfin_com.party.v2_1.ObjectFactory partyObjectFactory = new ns.btfin_com.party.v2_1.ObjectFactory();
	
	private static final ns.btfin_com.sharedservices.common.address.v2_4.ObjectFactory addressObjectFactory = new ns.btfin_com.sharedservices.common.address.v2_4.ObjectFactory();
	
	private static final ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ObjectFactory investorOnboardingObjectFactory = new ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ObjectFactory();

	private static final ns.btfin_com.party.v3_0.ObjectFactory involvedPartyObjectFactory = new ns.btfin_com.party.v3_0.ObjectFactory();


	/**
	 * @return the contactobjectfactory
	 */
	public static ns.btfin_com.sharedservices.common.contact.v1_1.ObjectFactory getContactobjectfactory() {
		return contactObjectFactory;
	}

	/**
	 * @return the partyobjectfactory
	 */
	public static ns.btfin_com.party.v2_1.ObjectFactory getPartyobjectfactory() {
		return partyObjectFactory;
	}

	/**
	 * @return the addressobjectfactory
	 */
	public static ns.btfin_com.sharedservices.common.address.v2_4.ObjectFactory getAddressobjectfactory() {
		return addressObjectFactory;
	}


	/**
	 *@return the investorOnboardingObjectFactory
	 */
	public static ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ObjectFactory getInvestorOnboardingObjectFactory()
	{
		return investorOnboardingObjectFactory;
	}

	/**
	 * @return the involvedPartyObjectFactory
	 */
	public static ns.btfin_com.party.v3_0.ObjectFactory getInvolvedPartyObjectFactory()
	{
		return involvedPartyObjectFactory;
	}
}
