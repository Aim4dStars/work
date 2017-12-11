package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.AvaloqTransactionService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.request.AvaloqOperation;
import com.btfin.abs.trxservice.bp.v1_0.BpRsp;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Created by L067218 on 27/07/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class BeneficiaryDetailsIntegrationServiceImplTest {


    @InjectMocks
    private BeneficiaryDetailsIntegrationService beneficiaryDetailsIntegrationService = new BeneficiaryDetailsIntegrationServiceImpl();

    @Mock
    private AvaloqTransactionService avaloqTransactionService;

    private AccountKey accountKey;

    private ServiceErrors serviceErrors;

    @Test
    public void testAddBeneficiaries() {

        List<BeneficiaryDetails> benefList = new ArrayList<>();

        BeneficiaryDetails benef1 = new BeneficiaryDetailsImpl();
        benef1.setGender(Gender.MALE);
        benef1.setLastName("Richard");
        benef1.setFirstName("Dennis");
        benef1.setNominationTypeinAvaloqFormat("nomn_bind_nlaps_trustd");
        benef1.setDateOfBirth(new DateTime("2006-05-11"));
        benef1.setAllocationPercent(new BigDecimal(100));
        benef1.setEmail("abcd@gmail.com");
        benef1.setPhoneNumber("123456789");
        benef1.setRelationshipType(RelationshipType.FINANCIAL_DEPENDENT);
        benefList.add(benef1);

        SaveBeneficiariesDetails saveDetails = new SaveBeneficiariesDetailsImpl();

        saveDetails.setAccountKey(new com.bt.nextgen.api.account.v2.model.AccountKey("15E67EC04FBD44111C4EC74731CF06E74D5C8FD5E765FAB3"));
        saveDetails.setModificationSeq("1");
        saveDetails.setBeneficiaries(benefList);
        BpRsp benefResponse = JaxbUtil.unmarshall("/webservices/response/Add_Benef_Response.xml", BpRsp.class);
        when(avaloqTransactionService.executeTransactionRequest(Mockito.any(Object.class), Mockito.any(AvaloqOperation.class),
                Mockito.any(ServiceErrors.class))).thenReturn(benefResponse);
        TransactionStatus status = beneficiaryDetailsIntegrationService.saveOrUpdate(saveDetails);
        assertEquals(true, status.isSuccessful());


    }


}
