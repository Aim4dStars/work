package com.bt.nextgen.addressbook.web.model;

import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.service.avaloq.gateway.businessunit.Payee;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: L053474
 * Date: 29/07/13
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenericPayee implements Payee {

    private PayeeType payeeType;
    private String name;
    private String nickname;
    private String code;
    private String reference;
    private String currency;
    private BigDecimal limit;
    private boolean isPrimary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public PayeeType getPayeeType() {
        return payeeType;
    }

    public void setPayeeType(PayeeType payeeType) {
        this.payeeType = payeeType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GenericPayee genericPayee = (GenericPayee) obj;
        if (Objects.equals(genericPayee.getCode(), this.getCode()) &&
                Objects.equals(genericPayee.getReference(), this.getReference()))
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, reference);
    }
}
