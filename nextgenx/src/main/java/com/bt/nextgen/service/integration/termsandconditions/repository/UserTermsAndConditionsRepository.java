package com.bt.nextgen.service.integration.termsandconditions.repository;


import com.bt.nextgen.service.integration.termsandconditions.model.UserTermsAndConditions;
import com.bt.nextgen.service.integration.termsandconditions.model.UserTermsAndConditionsKey;
import com.bt.nextgen.service.integration.user.UserKey;

import java.util.List;

public interface UserTermsAndConditionsRepository
{
    public List<UserTermsAndConditions> search(UserKey userKey);

    public UserTermsAndConditions find(UserTermsAndConditionsKey key);

    public void save(UserTermsAndConditions userTermsAndConditions);
}