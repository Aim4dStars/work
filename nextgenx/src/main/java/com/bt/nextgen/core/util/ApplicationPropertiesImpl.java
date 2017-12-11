package com.bt.nextgen.core.util;

import org.springframework.stereotype.Component;


@Component("applicationProperties")
public class ApplicationPropertiesImpl implements ApplicationProperties {
	@Override
	public String get(String key) {
		return get(key, null);
	}

	
	@Override
	public String get(String key, String defaultValue) {
		String retval = Properties.get(key);
		
		if (retval == null) {
			retval = defaultValue;
		}
		
		return retval;
	}

	
	@Override
	public Integer getInteger(String key) {
		return getInteger(key, null);
	}
	

	@Override
	public Integer getInteger(String key, Integer defaultValue) {
		final String property = get(key);
		final Integer retval;
		
		if (property == null) {
			retval = defaultValue;
		}
		else {
			retval = Integer.parseInt(property);
		}
		
		return retval;
	}

}
