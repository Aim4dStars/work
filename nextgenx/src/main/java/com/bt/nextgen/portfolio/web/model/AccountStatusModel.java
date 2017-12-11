package com.bt.nextgen.portfolio.web.model;

import java.util.List;

@Deprecated
public class AccountStatusModel {

	private List<AccountModel> accountModelList;
	private boolean isRedirect;
	private String clientId;
	private String portfolioId;

	public List<AccountModel> getAccountModelList() {
		return accountModelList;
	}

	public void setAccountModelList(List<AccountModel> accountModelList) {
		this.accountModelList = accountModelList;
	}

	public boolean isRedirect() {
		return isRedirect;
	}

	public void setRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}

}
