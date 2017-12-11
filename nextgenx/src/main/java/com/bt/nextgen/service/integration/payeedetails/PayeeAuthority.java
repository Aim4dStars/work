package com.bt.nextgen.service.integration.payeedetails;

import com.bt.nextgen.service.avaloq.payeedetails.PersonAuthority;
import com.bt.nextgen.service.integration.messages.PersonIdentifier;

public interface PayeeAuthority extends PersonIdentifier
{
    /*TODO: Need to populate PERSON_AUTHORITY category in Cache. Currently not returning*/
	/*public PersonAuthority getTransactionType();*/
}
