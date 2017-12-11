package com.bt.nextgen.payment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import com.bt.nextgen.core.web.util.DoubleClickGuard;


public class DoubleClickGuardTest
{


	@Test
	public void testFirstClick_istrue() throws Exception
	{
		DoubleClickGuard guard = new DoubleClickGuard();
		assertFalse(guard.clickAndCheckFirst());
	}

	@Test
	public void testSecondDoubleClick_shouldFail() throws Exception
	{
		DoubleClickGuard guard = new DoubleClickGuard();

		// first click
		guard.clickAndCheckFirst();


		assertTrue(guard.clickAndCheckFirst());
	}

	@Test
	public void testMultipleClick() throws Exception
	{
		DoubleClickGuard guard = new DoubleClickGuard();
		guard.clickAndCheckFirst();
		assertTrue(guard.clickAndCheckFirst());
		assertTrue(guard.clickAndCheckFirst());
		assertTrue(guard.clickAndCheckFirst());
		assertTrue(guard.clickAndCheckFirst());
		assertTrue(guard.clickAndCheckFirst());
	}

	static int maxDelay = 300, minDelay = 100;
	int paymentCounter;

	@Test
	public void testInThread() throws Exception
	{
		final DoubleClickGuard guard = new DoubleClickGuard();

		int randomDelay1 = new Random().nextInt(maxDelay - minDelay + 1) + minDelay;
		int randomDelay2 = new Random().nextInt(maxDelay - minDelay + 1) + minDelay;
		int randomDelay3 = new Random().nextInt(maxDelay - minDelay + 1) + minDelay;
		int randomDelay4 = new Random().nextInt(maxDelay - minDelay + 1) + minDelay;
		int randomDelay5 = new Random().nextInt(maxDelay - minDelay + 1) + minDelay;

		PaymentThread paymentThread1 = new PaymentThread(guard, randomDelay1);
		PaymentThread paymentThread2 = new PaymentThread(guard, randomDelay2);
		PaymentThread paymentThread3 = new PaymentThread(guard, randomDelay3);
		PaymentThread paymentThread4 = new PaymentThread(guard, randomDelay4);
		PaymentThread paymentThread5 = new PaymentThread(guard, randomDelay5);

		paymentThread1.start();
		paymentThread2.start();
		paymentThread3.start();
		paymentThread4.start();
		paymentThread5.start();

		paymentThread1.join();
		paymentThread2.join();
		paymentThread3.join();
		paymentThread4.join();
		paymentThread5.join();

		System.out.println(paymentCounter);
		assertTrue(paymentCounter == 1);
	}


	class PaymentThread extends Thread
	{
		DoubleClickGuard doubleClickGuard;
		long delay;

		public PaymentThread(DoubleClickGuard doubleClickGuard, long delay)
		{
			this.doubleClickGuard = doubleClickGuard;
			this.delay = delay;
		}

		@Override public void run()
		{
			Sleep(delay);
			if (!doubleClickGuard.clickAndCheckFirst())
			{
				makePayment();
			}

		}

		void makePayment()
		{
			paymentCounter++;
		}

		void Sleep(long delay)
		{
			try
			{
				Thread.sleep(delay);
			}
			catch (Exception e)
			{
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
	}

}
