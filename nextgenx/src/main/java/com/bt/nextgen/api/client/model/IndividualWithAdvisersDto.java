package com.bt.nextgen.api.client.model;

import java.util.Set;

public class IndividualWithAdvisersDto extends ExistingClientSearchDto {
    private String dateOfBirth;
    private String title;
    private String firstName;
    private String lastName;

    private Set<String> adviserPositionIds;
    private boolean individualInvestor;
    private String dateOfBirthForDisplay;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getDateOfBirth()
    {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAdviserPositionIds(Set<String> adviserPositionIds) {
        this.adviserPositionIds = adviserPositionIds;
    }

    public Set<String> getAdviserPositionIds() {
        return adviserPositionIds;
    }

    public void setIndividualInvestor(boolean individualInvestor) {
        this.individualInvestor = individualInvestor;
    }

    public boolean isIndividualInvestor() {
        return individualInvestor;
    }

    public String getDateOfBirthForDisplay() {
        return dateOfBirthForDisplay;
    }

    public void setDateOfBirthForDisplay(String dateOfBirthForDisplay) {
        this.dateOfBirthForDisplay = dateOfBirthForDisplay;
    }
}
