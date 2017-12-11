package com.bt.nextgen.api.cashcategorisation.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.Dto;


/**
 * DTO for a person's transactions for a single category.
 *
 * @author Albert Hirawan
 */
public class PersonCategoryTransactionsDto implements Dto {
    /** Id of person. */
    private String personId;

    /** First name of person. */
    private String firstName;

    /** Last name of person. */
    private String lastName;

    /** Date of birth of person. */
    private DateTime dateOfBirth;

    /** Age of person. */
    private Integer age;

    /** Category for the transactions. */
    private String category;

    /** Amount of total categorised transactions. */
    private BigDecimal totalAmount;

    /** List of transactions for the category. */
    private List<CategorisedTransactionDto> transactions = new ArrayList<>();


    @Override
    public String getType() {
        return "PersonCategoryTransactionsDto";
    }


    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<CategorisedTransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<CategorisedTransactionDto> transactions) {
        this.transactions = transactions;
    }
}
