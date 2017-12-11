package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.service.integration.movemoney.BpayBiller;

public class BpayBillerImpl implements BpayBiller {

    private String billerCode;
    private String customerReferenceNo;
    private String payeeName;
    private String nickName;

    @Override
    public String getPayeeName() {
        return payeeName;
    }

    @Override
    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * @return the billerCode
     */
    @Override
    public String getBillerCode() {
        return billerCode;
    }

    /**
     * @param billerCode
     *            the billerCode to set
     */
    @Override
    public void setBillerCode(String billerCode) {
        this.billerCode = billerCode;
    }

    /**
     * @return the customerReferenceNo
     */
    @Override
    public String getCustomerReferenceNo() {
        return customerReferenceNo;
    }

    /**
     * @param customerReferenceNo
     *            the customerReferenceNo to set
     */
    @Override
    public void setCustomerReferenceNo(String customerReferenceNo) {
        this.customerReferenceNo = customerReferenceNo;
    }

}
