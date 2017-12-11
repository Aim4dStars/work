package com.bt.nextgen.core.web.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Double click implementation
 * refer wiki dwgps0026/twiki/bin/view/NextGen/TechnicalDoubleClickImplementation
 */
public class DoubleClickGuard
{
	private final AtomicInteger currentPayment = new AtomicInteger(1);

    /**
     *. each call to this method will constitue a 'click'"
     " the value returned indicates whether you were the first or not
     *
     */
    public boolean clickAndCheckFirst()
	{
		return currentPayment.decrementAndGet() != 0;
	}

}
