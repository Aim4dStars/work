package com.bt.nextgen.api.supermatch.v1.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SuperMatchErrorMapperTest {

    @InjectMocks
    private SuperMatchErrorMapper errorMapper;

    @Test
    public void map() throws Exception {
        List<ServiceError> serviceErrors = new ArrayList<>();
        ServiceErrorImpl serviceError = new ServiceErrorImpl();
        serviceError.setErrorCode("MemberNotFound");
        serviceError.setReason("Member does not exist in ECO");
        serviceErrors.add(serviceError);

        List<DomainApiErrorDto> domainErrors = errorMapper.map(serviceErrors);
        assertEquals(domainErrors.size(), 1);
        assertEquals(domainErrors.get(0).getErrorId(), "MemberNotFound");
        assertEquals(domainErrors.get(0).getMessage(), "UNK-MemberNotFound-UCORR");
        assertEquals(domainErrors.get(0).getReason(), "Member does not exist in ECO");
    }

}