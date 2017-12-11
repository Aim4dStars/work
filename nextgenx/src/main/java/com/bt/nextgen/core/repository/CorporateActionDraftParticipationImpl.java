package com.bt.nextgen.core.repository;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "CA_DRAFT_PARTICIPATION")
public class CorporateActionDraftParticipationImpl implements CorporateActionSavedParticipation {
	@EmbeddedId
	private CorporateActionSavedParticipationKey key;

	@Column(name = "EXPIRY_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = CorporateActionDraftAccountImpl.class)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumns({@JoinColumn(name = "OE_ID", referencedColumnName = "OE_ID", insertable = false, updatable = false),
			@JoinColumn(name = "ORDER_NUMBER", referencedColumnName = "ORDER_NUMBER", insertable = false, updatable = false)})
	private List<CorporateActionSavedAccount> accounts;

	public CorporateActionDraftParticipationImpl() {
		// Default constructor
	}

	public CorporateActionDraftParticipationImpl(String oeId, String orderNumber, Date expiryDate) {
		this.key = new CorporateActionSavedParticipationKey(oeId, orderNumber);

		if (expiryDate != null) {
			this.expiryDate = (Date) expiryDate.clone();
		}
	}

	public static CorporateActionDraftParticipationImpl create(String oeId, String orderNumber, Date expiryDate) {
		return new CorporateActionDraftParticipationImpl(oeId, orderNumber, expiryDate);
	}

	@Override
	public CorporateActionSavedParticipationKey getKey() {
		return key;
	}

	@Override
	public void setKey(CorporateActionSavedParticipationKey key) {
		this.key = key;
	}

	@Override
	public Date getExpiryDate() {
		return expiryDate != null ? (Date) expiryDate.clone() : null;
	}

	@Override
	public void setExpiryDate(Date expiryDate) {
		if (expiryDate != null) {
			this.expiryDate = (Date) expiryDate.clone();
		}
	}

	@Override
	public List<CorporateActionSavedAccount> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<CorporateActionSavedAccount> accounts) {
		this.accounts = accounts;
	}

	@Override
	public CorporateActionSavedAccount addAccount(String accountNumber) {
		if (accounts == null) {
			accounts = new ArrayList<>();
		}

		CorporateActionDraftAccountImpl account =
				new CorporateActionDraftAccountImpl(new CorporateActionSavedAccountKey(key.getOeId(), key.getOrderNumber(), accountNumber),
						null);

		accounts.add(account);

		return account;
	}

	@Override
	public String toString() {
		return "CorporateActionDraftParticipationImpl{" +
				"key=" + key +
				", expiryDate=" + expiryDate +
				", accounts=" + accounts +
				'}';
	}
}
