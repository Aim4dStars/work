package com.bt.nextgen.core.repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OnboardingStatusImpl implements OnboardingStatusInterface
{

	private OnboardingPartyDisplayStatus status;

	private String failureMsg;

	/**
	 * @return the status
	 */
	@Override
	public OnboardingPartyDisplayStatus getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	@Override
	public void setStatus(OnboardingPartyDisplayStatus status)
	{
		this.status = status;
	}

	/**
	 * @return the failureMsg
	 */
	@Override
	public @Nullable String getFailureMsg()
	{
		return failureMsg;
	}

	/**
	 * @param failureMsg the failureMsg to set
	 */
	@Override
	public void setFailureMsg(@Nonnull String failureMsg)
	{
		this.failureMsg = failureMsg;
	}

}
