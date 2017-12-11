package com.bt.nextgen.api.client.model;

import javax.annotation.Nullable;
import java.util.List;


public class IndividualDto extends InvestorDto
{
	private String title;
	private String gcmTitleLabel;
	private String middleName;
	private String preferredName;
	private String gender;
	private String dateOfBirth;
	private String resiCountryforTax;
	private String resiCountryCodeForTax;
	private int age;
    // come from AssociatedPerson mapping, only for newCorporateSMSF
    @Nullable
    private String placeOfBirthCountry;
    @Nullable
    private String placeOfBirthState;
    @Nullable
    private String placeOfBirthSuburb;
    @Nullable
    private String formerName;

	private String isForeignRegistered;
	private List<TaxResidenceCountriesDto> taxResidenceCountries;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getGcmTitleLabel() {
		return gcmTitleLabel;
	}

	public void setGcmTitleLabel(String gcmTitleLabel) {
		this.gcmTitleLabel = gcmTitleLabel;
	}

	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	public String getPreferredName()
	{
		return preferredName;
	}

	public void setPreferredName(String preferredName)
	{
		this.preferredName = preferredName;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public String getResiCountryforTax()
	{
		return resiCountryforTax;
	}

	public void setResiCountryforTax(String resiCountryforTax)
	{
		this.resiCountryforTax = resiCountryforTax;
	}

	public String getResiCountryCodeForTax()
	{
		return resiCountryCodeForTax;
	}

	public void setResiCountryCodeForTax(String resiCountryCodeForTax)
	{
		this.resiCountryCodeForTax = resiCountryCodeForTax;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

    public String getPlaceOfBirthCountry() {
        return placeOfBirthCountry;
    }

    public void setPlaceOfBirthCountry(String placeOfBirthCountry) {
        this.placeOfBirthCountry = placeOfBirthCountry;
    }

    public String getPlaceOfBirthState() {
        return placeOfBirthState;
    }

    public void setPlaceOfBirthState(String placeOfBirthState) {
        this.placeOfBirthState = placeOfBirthState;
    }

    public String getPlaceOfBirthSuburb() {
        return placeOfBirthSuburb;
    }

    public void setPlaceOfBirthSuburb(String placeOfBirthSuburb) {
        this.placeOfBirthSuburb = placeOfBirthSuburb;
    }

    public String getFormerName() {
        return formerName;
    }

    public void setFormerName(String formerName) {
        this.formerName = formerName;
    }


	public String getIsForeignRegistered() {
		return isForeignRegistered;
	}

	public void setIsForeignRegistered(String isForeignRegistered) {
		this.isForeignRegistered = isForeignRegistered;
	}

	@Override
	public List<TaxResidenceCountriesDto> getTaxResidenceCountries() {
		return taxResidenceCountries;
	}

	@Override
	public void setTaxResidenceCountries(List<TaxResidenceCountriesDto> taxResidenceCountries) {
		this.taxResidenceCountries = taxResidenceCountries;
	}
}
