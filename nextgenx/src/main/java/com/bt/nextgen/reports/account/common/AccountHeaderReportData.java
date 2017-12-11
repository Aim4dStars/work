package com.bt.nextgen.reports.account.common;

import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.product.Product;
import net.sf.jasperreports.engine.Renderable;

@SuppressWarnings("squid:UnusedProtectedMethod")
public class AccountHeaderReportData {
    private WrapAccountDetail account;
    private Product product;
    private Renderable logo;
    private boolean displayBsbAndAccountNumber;

    public AccountHeaderReportData() {
    }

    public String getAccountName() {
        return account.getAccountName().replace(",", " and");
    }

    public String getAccountStructure() {
        return AccountUtil.getAccountTypeAndDescription(account);
    }

    public String getAccountNumber() {
        return account.getAccountNumber();
    }

    public String getProductName() {
        if (product == null) {
            return "";
        }
        return product.getProductName();
    }

    public String getBsbAccountNumber() {
        StringBuilder builder = new StringBuilder();
        if(displayBsbAndAccountNumber) {
            builder.append("BSB");
            builder.append(" ");
            builder.append(account.getBsb());
            builder.append(" ");
            builder.append("Account number");
            builder.append(" ");
            builder.append(account.getAccountNumber());
        }
        return builder.toString();
    }

    public Renderable getLogo() {
        return logo;
    }

    protected void setLogo(Renderable logo) {
        this.logo = logo;
    }

    protected void setAccount(WrapAccountDetail account) {
        this.account = account;
    }

    protected void setProduct(Product product) {
        this.product = product;
    }

    protected void setDisplayBsbAndAccountNumber(boolean displayBsbAndAccountNumber) {
        this.displayBsbAndAccountNumber = displayBsbAndAccountNumber;
    }

}
