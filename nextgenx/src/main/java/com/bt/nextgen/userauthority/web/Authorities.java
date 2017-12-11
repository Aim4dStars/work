package com.bt.nextgen.userauthority.web;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Authorities 
{
	PAY_INPAY_ALL("PAY_INPAY_ALL"), PAY_INPAY_LINK("PAY_INPAY_LINK"), NO_TRX("NO_TRX"), BP_ACC_MAINT("BP_ACC_MAINT");

	public final GrantedAuthority authority;

	private final String name;

	Authorities(String name)
	{
		this.name = name;
		this.authority = new SimpleGrantedAuthority(name);
	}

	public String getName()
	{
		return name;
	}

	public static Authorities fromRawName(String roleName)
	{
		return Authorities.valueOf(roleName);
	}
}
