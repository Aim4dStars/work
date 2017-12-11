package com.bt.nextgen.api.transaction.model;

/**
 * @author L069552
 * Description : This is basically to define the Transaction Status at the front end.Currently based on the status we have some significant functionality like showing error/warning icons,
 * Payment/Deposit messages on the screen. This field is present in the schema by the name<trx_status_id> which is a numeric field coming from the backend. 
   Based on its value we have pre-defined set of values like SCHEDULED/FAILED/RETRY which designates the status of the transactions.
 */
public enum TransactionStatusEnum
{
	REJECTED(0), RETRYING(1), SCHEDULED(2), OTHER(3);

	private Integer transactionStatusVal;

	TransactionStatusEnum(Integer value)
	{
		this.transactionStatusVal = value;
	}

	public Integer getTransactionStatusVal()
	{
		return transactionStatusVal;
	}

	/**
	 * Description : This method returns the Enum Name based on the transaction id passed from Avaloq
	 * @param value
	 * @return String
	 */
	public static String getTransactionStatusName(Integer value)
	{
		Integer values = null != value ? value : null;

		for (TransactionStatusEnum transactionStatusVal : TransactionStatusEnum.values())
		{

			int transactionStatusValEntry = transactionStatusVal.getTransactionStatusVal();

			if (null != values && transactionStatusValEntry == values)
			{

				return transactionStatusVal.toString();
			}
		}

		return REJECTED.name();
	}

	/**
	 * Description : This method returns the Enum Value based on the name passed from the Service layer
	 * @param value
	 * @return String
	 */

	public static Integer getTransactionStatusValues(String transactionStatusName)
	{

		TransactionStatusEnum transactionStatus = TransactionStatusEnum.valueOf(transactionStatusName);

		return transactionStatus.getTransactionStatusVal();
	}

}
