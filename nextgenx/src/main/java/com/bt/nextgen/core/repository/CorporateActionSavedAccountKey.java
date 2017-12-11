package com.bt.nextgen.core.repository;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CorporateActionSavedAccountKey implements Serializable {

	@Column(name = "OE_ID")
	private String oeId;

	@Column(name = "ORDER_NUMBER")
	private String orderNumber;

	@Column(name = "ACCOUNT_ID")
	private String accountNumber;

	public CorporateActionSavedAccountKey() {
		// Empty constructor
	}

	public CorporateActionSavedAccountKey(String oeId, String orderNumber, String accountNumber) {
		this.oeId = oeId;
		this.orderNumber = orderNumber;
		this.accountNumber = accountNumber;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOeId() {
		return oeId;
	}

	public void setOeId(String oeId) {
		this.oeId = oeId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + oeId.hashCode();
		result = prime * result + orderNumber.hashCode();
		result = prime * result + accountNumber.hashCode();

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		CorporateActionSavedAccountKey other = (CorporateActionSavedAccountKey) obj;

		// Split tests into individual methods to pass Sonar
		return oeIdMatches(other) && orderNumberMatches(other) && accountNUmberMatches(other);
	}

	private boolean oeIdMatches(CorporateActionSavedAccountKey other) {
		if (oeId == null) {
			if (other.oeId != null) {
				return false;
			}
		} else if (!oeId.equals(other.oeId)) {
			return false;
		}

		return true;
	}

	private boolean orderNumberMatches(CorporateActionSavedAccountKey other) {
		if (orderNumber == null) {
			if (other.orderNumber != null) {
				return false;
			}
		} else if (!orderNumber.equals(other.orderNumber)) {
			return false;
		}

		return true;
	}

	private boolean accountNUmberMatches(CorporateActionSavedAccountKey other) {
		if (accountNumber == null) {
			if (other.accountNumber != null) {
				return false;
			}
		} else if (!accountNumber.equals(other.accountNumber)) {
			return false;
		}

		return true;
	}
}
