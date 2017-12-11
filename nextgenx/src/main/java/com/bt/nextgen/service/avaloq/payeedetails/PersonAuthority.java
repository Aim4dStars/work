package com.bt.nextgen.service.avaloq.payeedetails;

public enum PersonAuthority
{

	PAY_INPAY_ALL("btfg$pay_all"), PAY_INPAY_LINK("btfg$pay_link"), NO_TRX("no_trx"), BP_ACC_MAINT("btfg$bp_acc_mt");

	private String authority;

	PersonAuthority(String authority)
	{
		this.authority = authority;
	}

	public String toString()
	{
		return authority;
	}

}
