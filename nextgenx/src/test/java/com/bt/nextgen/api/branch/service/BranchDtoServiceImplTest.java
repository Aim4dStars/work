package com.bt.nextgen.api.branch.service;

import com.bt.nextgen.api.branch.model.BranchDto;
import com.bt.nextgen.api.branch.model.BranchKey;
import com.bt.nextgen.payments.repository.Bsb;
import com.bt.nextgen.payments.repository.BsbCodeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.NoResultException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BranchDtoServiceImplTest {

    @Mock
    BsbCodeRepository bsbCodeRepository;

    @InjectMocks
    private BranchDtoServiceImpl service;

    @Test
    public void shouldRetrieveBbsFromRepository() throws Exception {
        String bsbCode = "062005";
        Bsb bsb = new Bsb();
        bsb.setBsbCode(bsbCode);
        bsb.setBankName("SAMPLE Financial Institutional Name");

        when(bsbCodeRepository.load(bsbCode)).thenReturn(bsb);

        BranchKey key = new BranchKey(bsbCode);
        assertThat(service.find(key, null).getKey().getBsb(), equalTo(bsbCode));
        assertThat(service.find(key, null).getFinancialInstitutionName(), equalTo("SAMPLE Financial Institutional Name"));
    }

    @Test
    public void shouldReturnNullWhenBsbNotFoundInRepository() throws Exception {
        String doesNotExist = "DOES_NOT_EXIST";
        when(bsbCodeRepository.load(doesNotExist)).thenReturn(null);

        assertNull(service.find(new BranchKey(doesNotExist), null));

    }
}