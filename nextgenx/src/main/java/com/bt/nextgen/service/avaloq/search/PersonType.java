package com.bt.nextgen.service.avaloq.search;

/**
 * This enum maps the UI person type to the corresponding internalId in avaloq 
 */

public enum PersonType
{
	NATURAL_PERSON("person_natural"), LEGAL_ENTITY("person_legal"), ORG_ENTITY("oe"), JOB("job");
	
	private String name;

	PersonType(String personType)
    {
        this.name = personType;
    }

    public String getName()
    {
        return name;
    }
}
