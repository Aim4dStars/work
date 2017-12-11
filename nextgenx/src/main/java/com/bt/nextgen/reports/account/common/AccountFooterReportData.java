package com.bt.nextgen.reports.account.common;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import net.sf.jasperreports.engine.Renderable;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

@SuppressWarnings("squid:UnusedProtectedMethod")
public class AccountFooterReportData {
    private BrokerUser adviserUser;
    private Broker dealer;
    private Product product;
    private WrapAccountDetail account;
    private ClientDetail client;
    private String reportTitle;
    private String summaryDescription;
    private String summaryValue;
    private DateTime generationDate;
    private Renderable iconAccount;
    private Renderable iconAdviser;
    private Renderable iconContact;
    private Renderable footerBackgroundPortrait;
    private Renderable footerBackgroundLandscape;
    private boolean displayBsbAndAccountNumber;

    public AccountFooterReportData() {
        this.generationDate = new DateTime();
    }

    public String getReportGeneration() {
        return reportTitle + " created " + ReportFormatter.format(ReportFormat.LONG_DATE, generationDate);
    }

    public String getSummaryDescription() {
        return summaryDescription;
    }

    public String getSummaryValue() {
        return summaryValue;
    }

    public String getAccountDetails() {
        StringBuilder builder = new StringBuilder();
        appendString(builder, account.getAccountName().replace(",", " and"), "\n");
        appendString(builder, AccountUtil.getAccountTypeAndDescription(account), "\n");
        appendString(builder, product.getProductName(), "\n");
        appendString(builder, account.getAccountNumber(), "\n");
        if(displayBsbAndAccountNumber){
            appendString(builder,"BSB".concat(" ").concat(account.getBsb()).concat(" ").concat("Account").concat(" ").concat(account.getAccountNumber()));
        }
        return builder.toString();
    }

    public String getPrimaryContact() {
        StringBuilder builder = new StringBuilder();
        appendString(builder, client.getFullName(), "\n");
        boolean phoneChosen = false;
        for (Phone phone : client.getPhones()) {
            if (phone.isPreferred()) {
                buildPhone(builder, phone);
                phoneChosen = true;
                break;
            }
        }
        // no preferred phone, use first.
        if (!phoneChosen && !client.getPhones().isEmpty()) {
            buildPhone(builder, client.getPhones().get(0));
        }
        for (Address address : client.getAddresses()) {
            if (address.getAddressType() == AddressMedium.POSTAL) {
                buildAddress(builder, address);
                break;
            }
        }
        return builder.toString();
    }

    private void buildPhone(StringBuilder builder, Phone phone) {
        StringBuilder phoneBuilder = new StringBuilder();
        appendString(phoneBuilder, phone.getCountryCode());
        appendString(phoneBuilder, phone.getAreaCode());
        appendString(phoneBuilder, phone.getNumber(), " ");
        appendString(builder, ReportFormatter.formatTelephoneNumber(phoneBuilder.toString()), "\n");
    }

    private void buildAddress(StringBuilder builder, Address address) {
        appendString(builder, address.getUnit(), "/");
        appendString(builder, address.getStreetNumber());
        appendString(builder, address.getStreetName());
        appendString(builder, address.getStreetType());
        appendString(builder, address.getBuilding());
        appendString(builder, address.getSuburb());
        appendString(builder, address.getState());
        appendString(builder, address.getPostCode(), "\n");
    }

    public String getAdviserDetails() {
        StringBuilder builder = new StringBuilder();
        if (adviserUser != null) {
            if (StringUtils.isNotBlank(adviserUser.getCorporateName())) {
                appendString(builder, adviserUser.getCorporateName(), "\n");
            } else {
                appendString(builder, adviserUser.getFirstName());
                appendString(builder, adviserUser.getLastName(), "\n");
            }
            boolean phoneChosen = false;
            for (Phone phone : adviserUser.getPhones()) {
                if (phone.isPreferred()) {
                    buildPhone(builder, phone);
                    phoneChosen = true;
                    break;
                }
            }
            // no preferred phone, use first.
            if (!phoneChosen && !adviserUser.getPhones().isEmpty()) {
                buildPhone(builder, adviserUser.getPhones().get(0));
            }
        }
        if (dealer != null) {
            appendString(builder, dealer.getPositionName(), "\n");
        }
        return builder.toString();
    }

    public Renderable getIconAccount() {
        return iconAccount;
    }

    public Renderable getIconAdviser() {
        return iconAdviser;
    }

    public Renderable getIconContact() {
        return iconContact;
    }

    public Renderable getFooterBackgroundLandscape() {
        return footerBackgroundLandscape;
    }

    public Renderable getFooterBackgroundPortrait() {
        return footerBackgroundPortrait;
    }


    protected void setAdviserUser(BrokerUser adviserUser) {
        this.adviserUser = adviserUser;
    }

    protected void setDealer(Broker dealer) {
        this.dealer = dealer;
    }

    protected void setProduct(Product product) {
        this.product = product;
    }

    protected void setAccount(WrapAccountDetail account) {
        this.account = account;
    }

    protected void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    protected void setSummaryDescription(String summaryDescription) {
        this.summaryDescription = summaryDescription;
    }

    protected void setSummaryValue(String summaryValue) {
        this.summaryValue = summaryValue;
    }

    protected void setIconAccount(Renderable iconAccount) {
        this.iconAccount = iconAccount;
    }

    protected void setIconAdviser(Renderable iconAdviser) {
        this.iconAdviser = iconAdviser;
    }

    protected void setIconContact(Renderable iconContact) {
        this.iconContact = iconContact;
    }

    protected void setFooterBackgroundLandscape(Renderable footerBackgroundLandscape) {
        this.footerBackgroundLandscape = footerBackgroundLandscape;
    }
    protected void setDisplayBsbAndAccountNumber(boolean displayBsbAndAccountNumber) {
        this.displayBsbAndAccountNumber = displayBsbAndAccountNumber;
    }

    protected void setFooterBackgroundPortrait(Renderable footerBackgroundPortrait) {
        this.footerBackgroundPortrait = footerBackgroundPortrait;
    }

    protected void setClient(ClientDetail client) {
        this.client = client;
    }

    private void appendString(StringBuilder builder, String val) {
        appendString(builder, val, " ");
    }

    private void appendString(StringBuilder builder, String val, String seperator) {
        if (val != null) {
            builder.append(val);
            builder.append(seperator);
        }
    }

}
