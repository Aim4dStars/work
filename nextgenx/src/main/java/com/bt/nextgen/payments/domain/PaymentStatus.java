package com.bt.nextgen.payments.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping of avaloq payment status keys to the UI statuses
 */
public enum PaymentStatus
{
	REJECTED("91","94"), RETRYING("25"), UNCLEARED("Uncleared","Open"), CLEARED("Cleared","Booked"), SCHEDULED("90","26","19"), OTHER("Other");

	private static Map<String, PaymentStatus> stringMap = new HashMap<String, PaymentStatus>();

	static
	{
		for (PaymentStatus d : PaymentStatus.values()){
			if(d.getName() != null){
				for(int i=0;i<d.getName().length;i++){
					stringMap.put(d.getName()[i], d);
				}
			}
		}
	}

	PaymentStatus(String... name)
	{
		this.nameArray = name;
	}

	String[] nameArray;
	
	public String[] getName()
	{
		return nameArray;
	}

	public static PaymentStatus getPaymentStatus(String key)
	{
		return stringMap.get(key);
	}
	
	public static String getStatusName(String key){
		return stringMap.get(key).toString();
	}
}
