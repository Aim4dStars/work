package com.bt.nextgen.api.termdeposit.controller;

import com.bt.nextgen.api.account.v2.model.TermDepositAccountDto;
import com.bt.nextgen.api.account.v2.service.TermDepositAccountDtoService;
import com.bt.nextgen.api.account.v3.validation.WrapAccountDetailsDtoErrorMapper;
import com.bt.nextgen.api.termdeposit.model.TermDepositDetailDto;
import com.bt.nextgen.api.termdeposit.model.TermDepositDto;
import com.bt.nextgen.api.termdeposit.service.TermDepositDtoService;
import com.bt.nextgen.api.termdeposit.service.TermDepositsDtoService;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by M044020 on 1/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TermDepositsApiControllerTest {

    @InjectMocks
    private TermDepositsApiController controller;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private WrapAccountDetailsDtoErrorMapper errorMapper;

    @Mock
    private TermDepositsDtoService termDepositsDtoService;

    @Mock
    private TermDepositDtoService termDepositDtoService;

    @Mock
    private TermDepositAccountDtoService termDepositAccountDtoService;

    @Mock
    private TermDepositAccountDto depositAccountDto;

    @Mock
    private List<TermDepositDto> mockList;

    @Mock
    private TermDepositDetailDto termDepositDetailDto;

    @Before
    public void setUp() throws Exception {
        when(termDepositAccountDtoService.update(any(TermDepositAccountDto.class), any(ServiceErrors.class)))
                .thenReturn(depositAccountDto);
    }
    @Test
    public void getAdviserTermDepositsWithoutSortOrder() throws Exception {
        when(termDepositsDtoService.findAll(any(ServiceErrors.class))).thenReturn(mockList);
        ApiResponse response = controller.getAdviserTermDeposits(null);
        verify(termDepositsDtoService, times(1)).findAll(any(ServiceErrors.class));
        verify(mockList, never()).toArray();
    }

    @Test
    public void getAdviserTermDepositsWithSortOrder() throws Exception {
        List<TermDepositDto> depositDtoList = new ArrayList<>();
        TermDepositDto dto = new TermDepositDto();
        dto.setAccountName("ABC");
        depositDtoList.add(dto);
        dto = new TermDepositDto();
        dto.setAccountName("ABA");
        depositDtoList.add(dto);
        dto = new TermDepositDto();
        dto.setAccountName("ABB");
        depositDtoList.add(dto);
        when(termDepositsDtoService.findAll(any(ServiceErrors.class))).thenReturn(depositDtoList);
        ApiResponse response = controller.getAdviserTermDeposits("accountName");
        verify(termDepositsDtoService, times(1)).findAll(any(ServiceErrors.class));
        ResultListDto resultListDto = (ResultListDto) response.getData();
        assertThat(((TermDepositDto) resultListDto.getResultList().get(0)).getAccountName(), is("ABA"));
        assertThat(((TermDepositDto) resultListDto.getResultList().get(1)).getAccountName(), is("ABB"));
        assertThat(((TermDepositDto) resultListDto.getResultList().get(2)).getAccountName(), is("ABC"));
    }

    @Test(expected = AccessDeniedException.class)
    public void updateTermDepositForAccountForServiceOp() throws Exception {
        when(userProfileService.isEmulating()).thenReturn(true);
        controller.updateTermDepositForAccount("12312312", "12312", "12312");
    }

    @Test
    public void updateTermDepositForAccount() throws Exception {
        when(userProfileService.isEmulating()).thenReturn(false);
        controller.updateTermDepositForAccount("12312312", "12312", "12312");
    }
    @Test
    public void validateTDBreak() throws Exception {
        when(termDepositDtoService.validate(any(TermDepositDetailDto.class), any(ServiceErrors.class)))
                .thenReturn(termDepositDetailDto);
        controller.validateTDBreak("1233132", "1232131");
        verify(termDepositDtoService, times(1)).validate(any(TermDepositDetailDto.class), any(ServiceErrors.class));
    }

    @Test
    public void submitTDBreak() throws Exception {
        TermDepositDetailDto dto = new TermDepositDetailDto();
        when(termDepositDtoService.submit(any(TermDepositDetailDto.class), any(ServiceErrors.class)))
                .thenReturn(dto);
        controller.submitTDBreak("1233132", "1232131");
        verify(termDepositDtoService, times(1)).submit(any(TermDepositDetailDto.class), any(ServiceErrors.class));
    }

}