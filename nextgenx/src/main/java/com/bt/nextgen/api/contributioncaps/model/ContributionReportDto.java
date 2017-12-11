package com.bt.nextgen.api.contributioncaps.model;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Model class to hold report objects
 */
public class ContributionReportDto {

    private String name;
    private String birthDate;
    private String age;
    private String contributionTypeLabel;
    private List<String> concSubCategoriesLabel;
    private List<String> nonConcSubCategoriesLabel;
    private List<String> otherSubCategoriesLabel;
    private List<String> concSubCategories;
    private List<String> nonConcSubCategories;
    private List<String> otherSubCategories;
    private String concTotal;
    private String concCap;
    private String concAvailable;
    private String nonConcTotal;
    private String nonConcCap;
    private String nonConcAvailable;
    private String otherTotal;
    private String totalContributions;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public List<String> getConcSubCategoriesLabel() {
        return concSubCategoriesLabel;
    }

    public void setConcSubCategoriesLabel(List<String> concSubCategoriesLabel) {
        this.concSubCategoriesLabel = concSubCategoriesLabel;
    }

    public String getConcTotal() {
        return concTotal;
    }

    public void setConcTotal(String concTotal) {
        this.concTotal = concTotal;
    }

    public String getConcCap() {
        return concCap;
    }

    public void setConcCap(String concCap) {
        this.concCap = concCap;
    }

    public String getConcAvailable() {
        return concAvailable;
    }

    public void setConcAvailable(String concAvailable) {
        this.concAvailable = concAvailable;
    }

    public List<String> getConcSubCategories() {
        return concSubCategories;
    }

    public void setConcSubCategories(List<String> concSubCategories) {
        this.concSubCategories = concSubCategories;
    }

    public List<String> getNonConcSubCategories() {
        return nonConcSubCategories;
    }

    public void setNonConcSubCategories(List<String> nonConcSubCategories) {
        this.nonConcSubCategories = nonConcSubCategories;
    }

    public List<String> getOtherSubCategories() {
        return otherSubCategories;
    }

    public void setOtherSubCategories(List<String> otherSubCategories) {
        this.otherSubCategories = otherSubCategories;
    }

    public String getNonConcTotal() {
        return nonConcTotal;
    }

    public void setNonConcTotal(String nonConcTotal) {
        this.nonConcTotal = nonConcTotal;
    }

    public String getNonConcCap() {
        return nonConcCap;
    }

    public void setNonConcCap(String nonConcCap) {
        this.nonConcCap = nonConcCap;
    }

    public String getNonConcAvailable() {
        return nonConcAvailable;
    }

    public void setNonConcAvailable(String nonConcAvailable) {
        this.nonConcAvailable = nonConcAvailable;
    }

    public String getOtherTotal() {
        return otherTotal;
    }

    public void setOtherTotal(String otherTotal) {
        this.otherTotal = otherTotal;
    }

    public String getTotalContributions() {
        return totalContributions;
    }

    public void setTotalContributions(String totalContributions) {
        this.totalContributions = totalContributions;
    }

    public List<String> getNonConcSubCategoriesLabel() {
        return nonConcSubCategoriesLabel;
    }

    public void setNonConcSubCategoriesLabel(List<String> nonConcSubCategoriesLabel) {
        this.nonConcSubCategoriesLabel = nonConcSubCategoriesLabel;
    }

    public List<String> getOtherSubCategoriesLabel() {
        return otherSubCategoriesLabel;
    }

    public void setOtherSubCategoriesLabel(List<String> otherSubCategoriesLabel) {
        this.otherSubCategoriesLabel = otherSubCategoriesLabel;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getContributionTypeLabel() {
        return contributionTypeLabel;
    }

    public void setContributionTypeLabel(String contributionTypeLabel) {
        this.contributionTypeLabel = contributionTypeLabel;
    }
}
