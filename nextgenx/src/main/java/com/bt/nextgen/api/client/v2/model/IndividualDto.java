package com.bt.nextgen.api.client.v2.model;

import javax.annotation.Nullable;


public class IndividualDto extends InvestorDto
{
	private String title;
	private String firstName;
	private String middleName;
	private String lastName;
	private String preferredName;
	private String gender;
	private String dateOfBirth;
	private String resiCountryforTax;
	private String resiCountryCodeForTax;
	private String userName;
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
    //

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getResiCountryforTax() {
		return resiCountryforTax;
	}

	public void setResiCountryforTax(String resiCountryforTax) {
		this.resiCountryforTax = resiCountryforTax;
	}

	public String getResiCountryCodeForTax() {
		return resiCountryCodeForTax;
	}

	public void setResiCountryCodeForTax(String resiCountryCodeForTax) {
		this.resiCountryCodeForTax = resiCountryCodeForTax;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
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

}
