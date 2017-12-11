package com.bt.nextgen.service.avaloq.fundpaymentnotice;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.order.OrderImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.fundpaymentnotice.DistributionDetails;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNotice;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.order.Order;

@ServiceBean(xpath = "//doc", type = ServiceBeanType.CONCRETE)
public class FundPaymentNoticeImpl implements FundPaymentNotice
{

	@ServiceElement(xpath = "doc_head_list/doc_head/asset_id/val")
	String asset_id;

	@ServiceElement(xpath = "doc_head_list/doc_head/doc_id/val")
	String order_id;

	@ServiceElement(xpath = "doc_head_list/doc_head/ex_date/val", converter = DateTimeTypeConverter.class)
	DateTime distributionDate;

	@ServiceElement(xpath = "doc_head_list/doc_head/tax_relv_date/val", converter = DateTimeTypeConverter.class)
	DateTime taxRelevanceDate;

	@ServiceElement(xpath = "doc_head_list/doc_head/tax_relv_year/val")
	String taxYear;

	@ServiceElementList(xpath = "doc_head_list/doc_head/distr_list/distr", type = DistributionDetailsImpl.class)
	List <DistributionDetails> distributionDetails;

	@ServiceElement(xpath = "doc_foot_list/doc_foot/amount/val")
	String distributionAmount;

	@ServiceElement(xpath = "doc_foot_list/doc_foot/mit_fund_amount/val")
	String withHoldFundPaymentAmount;

    @ServiceElement(xpath = "if(doc_head_list/doc_head/amit_asset/val[contains(.,'+')]) then true() else false()")
    private Boolean amitNotice;

	@Override
	public Asset getAsset()
	{
		AssetImpl asset = new AssetImpl();
		asset.setAssetId(getAsset_id());
		return asset;

	}

	@Override
	public Order getOrder()
	{

		OrderImpl order = new OrderImpl();
		order.setOrderId(getOrder_id());
		return order;
	}

	@Override
	public DateTime getDistributionDate()
	{
		return distributionDate;
	}

	@Override
	public DateTime getTaxRelevanceDate()
	{
		return taxRelevanceDate;
	}

	@Override
	public List <DistributionDetails> getDistributions()
	{
		return distributionDetails;
	}

	@Override
	public BigDecimal getDistributionAmount()
	{
		return new BigDecimal(distributionAmount);
	}

	@Override
	public BigDecimal getWithHoldFundPaymentAmount()
	{
		return new BigDecimal(withHoldFundPaymentAmount);
	}

	/**
	 * @return the asset_id
	 */
	public String getAsset_id()
	{
		return asset_id;
	}

	/**
	 * @return the order_id
	 */
	public String getOrder_id()
	{
		return order_id;
	}

	@Override
	public String getTaxYear()
	{
		return taxYear;
	}

    @Override
	public Boolean isAmitNotice() {
        return amitNotice;
    }
}
