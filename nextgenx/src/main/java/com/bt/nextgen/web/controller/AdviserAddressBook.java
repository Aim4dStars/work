package com.bt.nextgen.web.controller;

import com.bt.nextgen.addressbook.PayeeModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AdviserAddressBook {
    // portfolioId -> payeeId -> payee
    private final Map<String, Map<String, PayeeModel>> addressBook = new ConcurrentHashMap();

    public AdviserAddressBook() {

    }

    public void setup(String portfolioId) {
        addressBook.put(portfolioId, new ConcurrentHashMap<String, PayeeModel>());
    }

    public void clear(String portfolioId) {
        addressBook.remove(portfolioId);
    }

    public void update(String portfolioId, java.util.List<PayeeModel> payeeId) {
        Map<String, PayeeModel> book = addressBook.get(portfolioId);
        for (PayeeModel payee : payeeId) {
            book.put(String.valueOf(payee.getId()), payee);
        }
    }

    public void update(String portfolioId, PayeeModel payeeModel) {
        Map<String, PayeeModel> book = addressBook.get(portfolioId);
        book.put(payeeModel.getId(), payeeModel);
    }

    public PayeeModel getPayee(String portfolioId, String payeeId) {
        return addressBook.get(portfolioId).get(payeeId);
    }

    public void add(String portfolioId, String id, PayeeModel payee) {
        if (addressBook.containsKey(portfolioId)) {
            addressBook.get(portfolioId).put(id, payee);
        }
    }

    public void delete(String portfolioId, String payeeId) {
        addressBook.get(portfolioId).remove(payeeId);
    }

}
