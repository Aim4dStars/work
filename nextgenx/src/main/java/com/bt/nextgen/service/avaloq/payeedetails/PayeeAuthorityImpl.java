package com.bt.nextgen.service.avaloq.payeedetails;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.payeedetails.PayeeAuthority;
import com.bt.nextgen.service.avaloq.payeedetails.PersonAuthority;

@ServiceBean(xpath="bp_auth")
public class PayeeAuthorityImpl implements PayeeAuthority
{
    /*
    @ServiceElement(xpath="txn_type_id/val", staticCodeCategory="PERSON_AUTHORITY")
    private PersonAuthority transactionType;
    */
	@ServiceElement(xpath="person_auth_id/val")
	private String personId;

	@Override
	public String getPersonId()
	{
		return personId;
	}

	public void setPersonId(String personId)
	{
		this.personId = personId;
	}

	/*@Override
	public PersonAuthority getTransactionType()
	{
		return transactionType;
	}

	public void setTransactionType(PersonAuthority transactionType)
	{
		this.transactionType = transactionType;
	}*/
}
