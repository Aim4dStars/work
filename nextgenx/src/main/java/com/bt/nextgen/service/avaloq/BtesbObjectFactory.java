package com.bt.nextgen.service.avaloq;

public class BtesbObjectFactory
{
	// Portfolio valuation xsd
	private static final ns.btfin_com.sharedservices.common.payment.v2_0.ObjectFactory paymentObjectFactory = new ns.btfin_com.sharedservices.common.payment.v2_0.ObjectFactory();
	private static final ns.btfin_com.product.common.cashaccount.v2_0.ObjectFactory cashObjectFactory = new ns.btfin_com.product.common.cashaccount.v2_0.ObjectFactory();

	// Asset allocation xsd
	private static final ns.btfin_com.product.common.investmentsecurity.v1_0.ObjectFactory invSecurityFactory = new ns.btfin_com.product.common.investmentsecurity.v1_0.ObjectFactory();
	private static final ns.btfin_com.product.common.investmentaccount.v2_0.ObjectFactory investmentAccountFactory = new ns.btfin_com.product.common.investmentaccount.v2_0.ObjectFactory();

	public static ns.btfin_com.sharedservices.common.payment.v2_0.ObjectFactory getPaymentObjectFactory()
	{
		return paymentObjectFactory;
	}

	public static ns.btfin_com.product.common.investmentsecurity.v1_0.ObjectFactory getInvestmentSecurityFactory()
	{
		return invSecurityFactory;
	}

	public static ns.btfin_com.product.common.investmentaccount.v2_0.ObjectFactory getInvestmentAccountFactory()
	{
		return investmentAccountFactory;
	}

    public static ns.btfin_com.product.common.cashaccount.v2_0.ObjectFactory getCashObjectFactory() {
        return cashObjectFactory;
    }
}
