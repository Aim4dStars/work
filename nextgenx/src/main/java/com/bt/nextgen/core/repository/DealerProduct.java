package com.bt.nextgen.core.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "DEALER_PRODUCTS")
public class DealerProduct implements Serializable
{
	@Id
	@Column(name = "DEALER_GRP")
	private String adviserId;
	@Id
	@Column(name = "PRODUCT_ID")
	private String productId;

	@Column(name = "PRODUCT_NAME")
	private String productName;

	@Column(name = "ONG_MAX_DOLLAR")
	private Long ongDollarMax;
	@Column(name = "ONG_MAX_PERCENT")
	private Long ongMaxPercent;

	@Column(name = "LIC_MAX_PERCENT")
	private Long licMaxPercent;
	@Column(name = "LIC_MAX_DOLLAR")
	private Long licMaxDolllar;

    @Column(name = "LICENSEE_FEE")
    private Boolean licenseeFees;

	public Long getOngDollarMax()
	{
		return ongDollarMax;
	}

	public void setOngDollarMax(Long ongDollarMax)
	{
		this.ongDollarMax = ongDollarMax;
	}

	public Long getOngMaxPercent()
	{
		return ongMaxPercent;
	}

	public void setOngMaxPercent(Long ongMaxPercent)
	{
		this.ongMaxPercent = ongMaxPercent;
	}

	public Long getLicMaxPercent()
	{
		return licMaxPercent;
	}

	public void setLicMaxPercent(Long licMaxPercent)
	{
		this.licMaxPercent = licMaxPercent;
	}

	public Long getLicMaxDolllar()
	{
		return licMaxDolllar;
	}

	public void setLicMaxDolllar(Long licMaxDolllar)
	{
		this.licMaxDolllar = licMaxDolllar;
	}

	public String getAdviserId()
	{
		return adviserId;
	}

	public void setAdviserId(String adviserId)
	{
		this.adviserId = adviserId;
	}

	public String getProductId()
	{
		return productId;
	}

	public void setProductId(String productId)
	{
		this.productId = productId;
	}

	public String getProductName()
	{
		return productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

    public Boolean isLicenseeFees() {
        return licenseeFees;
    }

    public void setLicenseeFees(Boolean licenseeFees) {
        this.licenseeFees = licenseeFees;
    }
}
