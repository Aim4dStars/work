package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;

/**
 * See interface for documentation
 */
@ServiceBean(xpath = "/")
public class CorporateActionTransactionDetailsImpl implements CorporateActionTransactionDetails {

	public static final String XML_HEADER = "pos_head_list/pos_head/";

	@ServiceElement(xpath = XML_HEADER + "pos_id/val")
	private String positionId;

	@ServiceElement(xpath = XML_HEADER + "bp_id/val")
	private String accountId;

	@ServiceElement(xpath = XML_HEADER + "order_nr/val")
	private Integer transactionNumber;

	@ServiceElement(xpath = XML_HEADER + "book_text/val")
	private String transactionDescription;


	/**
	 * The position ID
	 *
	 * @return position ID for CA accounts
	 */
	@Override
	public String getPositionId() {
		return positionId;
	}

	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}

	/**
	 * The account ID
	 *
	 * @return account ID for looking CA accounts
	 */

	@Override
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return the transactionNumber
	 */
	@Override
	public Integer getTransactionNumber() {
		return transactionNumber;
	}

	/**
	 * @param transactionNumber the transactionNumber to set
	 */
	public void setTransactionNumber(Integer transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	@Override
	public String getTransactionDescription() {
		return transactionDescription;
	}

	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}


}
