package com.bt.nextgen.userauthority.web;

import static com.bt.nextgen.userauthority.web.Authorities.BP_ACC_MAINT;
import static com.bt.nextgen.userauthority.web.Authorities.PAY_INPAY_ALL;
import static com.bt.nextgen.userauthority.web.Authorities.PAY_INPAY_LINK;
import static com.bt.nextgen.userauthority.web.Authorities.NO_TRX;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AuthoritiesTest {

	@Test
	public void payInpayAll() {
		assertEquals("PAY_INPAY_ALL", PAY_INPAY_ALL.getName());
		assertEquals("PAY_INPAY_ALL", PAY_INPAY_ALL.authority.toString());
	}

	@Test
	public void payInpayLink() {
		assertEquals("PAY_INPAY_LINK", PAY_INPAY_LINK.getName());
		assertEquals("PAY_INPAY_LINK", PAY_INPAY_LINK.authority.toString());
	}

	@Test
	public void noTrx() {
		assertEquals("NO_TRX", NO_TRX.getName());
		assertEquals("NO_TRX", NO_TRX.authority.toString());
	}

	@Test
	public void bpAccMaint() {
		assertEquals("BP_ACC_MAINT", BP_ACC_MAINT.getName());
		assertEquals("BP_ACC_MAINT", BP_ACC_MAINT.authority.toString());
	}
}
