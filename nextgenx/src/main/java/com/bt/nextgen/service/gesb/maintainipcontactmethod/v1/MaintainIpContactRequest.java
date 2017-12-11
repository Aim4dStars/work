/**
 * 
 */
package com.bt.nextgen.service.gesb.maintainipcontactmethod.v1;

import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.EmailAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.InvolvedPartyType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod;


public class MaintainIpContactRequest {
   
	 private InvolvedPartyType involvedPartyType;
	    private InvolvedPartyIdentifier involvedPartyIdentifier;
	    private PostalAddressContactMethod hasPostalAddressContactMethod;
	    private PhoneAddressContactMethod hasPhoneAddressContactMethod;
	    private EmailAddressContactMethod hasEmailAddressContactMethod;
		public InvolvedPartyType getInvolvedPartyType() {
			return involvedPartyType;
		}
		public void setInvolvedPartyType(InvolvedPartyType involvedPartyType) {
			this.involvedPartyType = involvedPartyType;
		}
		public InvolvedPartyIdentifier getInvolvedPartyIdentifier() {
			return involvedPartyIdentifier;
		}
		public void setInvolvedPartyIdentifier(
				InvolvedPartyIdentifier involvedPartyIdentifier) {
			this.involvedPartyIdentifier = involvedPartyIdentifier;
		}
		public PostalAddressContactMethod getHasPostalAddressContactMethod() {
			return hasPostalAddressContactMethod;
		}
		public void setHasPostalAddressContactMethod(
				PostalAddressContactMethod hasPostalAddressContactMethod) {
			this.hasPostalAddressContactMethod = hasPostalAddressContactMethod;
		}
		public PhoneAddressContactMethod getHasPhoneAddressContactMethod() {
			return hasPhoneAddressContactMethod;
		}
		public void setHasPhoneAddressContactMethod(
				PhoneAddressContactMethod hasPhoneAddressContactMethod) {
			this.hasPhoneAddressContactMethod = hasPhoneAddressContactMethod;
		}
		public EmailAddressContactMethod getHasEmailAddressContactMethod() {
			return hasEmailAddressContactMethod;
		}
		public void setHasEmailAddressContactMethod(
				EmailAddressContactMethod hasEmailAddressContactMethod) {
			this.hasEmailAddressContactMethod = hasEmailAddressContactMethod;
		}
	    
	    
	    
	    
	    
	    
}
