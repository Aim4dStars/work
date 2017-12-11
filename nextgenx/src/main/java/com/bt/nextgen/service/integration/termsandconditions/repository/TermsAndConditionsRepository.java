package com.bt.nextgen.service.integration.termsandconditions.repository;


import com.bt.nextgen.service.integration.termsandconditions.model.TermsAndConditions;

import java.util.List;

public interface TermsAndConditionsRepository
{
    public List<TermsAndConditions> findAll();
}