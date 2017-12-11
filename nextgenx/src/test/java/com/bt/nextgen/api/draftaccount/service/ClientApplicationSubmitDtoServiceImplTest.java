package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.FormDataConstantsForTests;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationSubmitDto;
import com.bt.nextgen.core.api.exception.NotAllowedException;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoBuilder.aDraftAccountDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationSubmitDtoServiceImplTest extends AbstractJsonReaderTest{

    public static final String DEFAULT_ENCODED_ADVISER_ID = "DD17ADAF0D6E2F1847BDFDD2493B41C35AED2A82A53607B2";
    public static final String DEFAULT_ENCODED_PRODUCT_ID = "A13EEBEC6074850121E40555F623A1A479986A9E316B5F70";
    @Mock
    private PermittedClientApplicationRepository repository;

    @Mock
    private ClientApplicationDto dto;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Mock
    private ClientApplicationDtoHelperService clientApplicationDtoHelperService;

    @InjectMocks
    ClientApplicationSubmitDtoServiceImpl service;

    private static final Long CLIENT_APPLICATION_ID = 100L;

    @Before
    public void setUp() throws Exception {

        when(dto.getAdviserId()).thenReturn(DEFAULT_ENCODED_ADVISER_ID);
        when(dto.getProductId()).thenReturn(DEFAULT_ENCODED_PRODUCT_ID);

        List<String> brokerProductAssets = new ArrayList<>();
        brokerProductAssets.add("1234");

        ClientApplicationDto clientApplicationDto = aDraftAccountDto().build();
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), any(ServiceErrorsImpl.class))).thenReturn(clientApplicationDto);
    }

    @Test(expected = NoResultException.class)
    public void submitShouldReturnNullIfTheRecordIsDeleted(){

        ClientApplicationSubmitDto dto = new ClientApplicationSubmitDto((new ClientApplicationKey(CLIENT_APPLICATION_ID)));
        when(repository.find(CLIENT_APPLICATION_ID)).thenThrow(NoResultException.class);
        service.submit(dto , serviceErrors);
    }

    @Test
    public void submitShouldNotUpdateRecordIfAlreadySubmitted(){

        ClientApplication clientApplication = new ClientApplication();
        clientApplication.markSubmitted();
        clientApplication.setAdviserPositionId("something");
        ClientApplicationDto dto = aDraftAccountDto().build();
        when(repository.find(dto.getKey().getClientApplicationKey())).thenReturn(clientApplication);
        ClientApplicationSubmitDto clientApplicationSubmitDto = new ClientApplicationSubmitDto(dto.getKey());
        clientApplicationSubmitDto.setAdviserId(dto.getAdviserId());
        clientApplicationSubmitDto.setProductId(dto.getProductId());
        try {
            service.submit(clientApplicationSubmitDto , serviceErrors);
        } catch (IllegalStateException ex) {
            assertThat(ex.getMessage(), containsString("Cannot update Client Application"));
        }
    }

    @Test(expected = NotAllowedException.class)
    public void submit_ShouldThrowAnException_WhenCheckProductIdAndAdviserIdAreAllowedForLoggedInUserThrowsException(){

        final ClientApplicationSubmitDto clientApplicationSubmitDto = new ClientApplicationSubmitDto(new ClientApplicationKey(CLIENT_APPLICATION_ID));
        doThrow(new NotAllowedException("")).when(clientApplicationDtoHelperService)
                .checkProductIdAndAdviserIdAreAllowedForLoggedInUser(eq(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID)),eq(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID)));

        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getAdviserPositionId()).thenReturn(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        when(clientApplication.getProductId()).thenReturn(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));
        doNothing().when(clientApplication).assertCanBeModified();
        when(repository.find(clientApplicationSubmitDto.getKey().getClientApplicationKey())).thenReturn(clientApplication);

        service.submit(clientApplicationSubmitDto, null);
    }

    @Test
    public void submitShouldInvokeCheckProductIdAndAdviserIdOnce_WhenBothAdviserIdAndProductIsSameInDtoAndDraftAccount(){
        ClientApplicationSubmitDto submitDto = getClientApplicationSubmitDto();
        ClientApplicationDto dto = mock(ClientApplicationDto.class);

        ClientApplication clientApplication = createDraftAccount();
        clientApplication.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        clientApplication.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));
        when(repository.find(any(Long.class))).thenReturn(clientApplication);

        when(clientApplicationDtoConverterService.convertToMinimalDto(eq(clientApplication))).thenReturn(dto);
        when(clientApplicationDtoHelperService.submitDraftAccount(eq(dto), any(ServiceErrors.class), eq(clientApplication))).thenReturn(dto);

        service.submit(submitDto, serviceErrors);
        verify(clientApplicationDtoHelperService, times(1)).submitDraftAccount(eq(dto), any(ServiceErrors.class), eq(clientApplication));
        verify(clientApplicationDtoHelperService, times(1)).checkProductIdAndAdviserIdAreAllowedForLoggedInUser(any(String.class), any(String.class));
    }


    @Test
    public void submitShouldInvokeCheckProductIdAndAdviserIdTwice_WhenAdviserIdIsDifferentInDtoAndDraftAccount(){
        ClientApplicationSubmitDto submitDto = getClientApplicationSubmitDto();
        ClientApplicationDto dto = mock(ClientApplicationDto.class);

        ClientApplication clientApplication = createDraftAccount();
        clientApplication.setAdviserPositionId("12342");
        clientApplication.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));
        when(repository.find(any(Long.class))).thenReturn(clientApplication);

        when(clientApplicationDtoConverterService.convertToMinimalDto(eq(clientApplication))).thenReturn(dto);
        when(clientApplicationDtoHelperService.submitDraftAccount(eq(dto), any(ServiceErrors.class), eq(clientApplication))).thenReturn(dto);

        service.submit(submitDto, serviceErrors);
        verify(clientApplicationDtoHelperService, times(1)).submitDraftAccount(eq(dto), any(ServiceErrors.class), eq(clientApplication));
        verify(clientApplicationDtoHelperService, times(2)).checkProductIdAndAdviserIdAreAllowedForLoggedInUser(any(String.class), any(String.class));
    }

    @Test
    public void submitShouldInvokeCheckProductIdAndAdviserIdTwice_WhenProductIdIsDifferentInDtoAndDraftAccount(){
        ClientApplicationSubmitDto submitDto = getClientApplicationSubmitDto();
        ClientApplicationDto dto = mock(ClientApplicationDto.class);

        ClientApplication clientApplication = createDraftAccount();
        clientApplication.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        clientApplication.setProductId("11223");
        when(repository.find(any(Long.class))).thenReturn(clientApplication);

        when(clientApplicationDtoConverterService.convertToMinimalDto(eq(clientApplication))).thenReturn(dto);
        when(clientApplicationDtoHelperService.submitDraftAccount(eq(dto), any(ServiceErrors.class), eq(clientApplication))).thenReturn(dto);

        service.submit(submitDto, serviceErrors);
        verify(clientApplicationDtoHelperService, times(1)).submitDraftAccount(eq(dto), any(ServiceErrors.class), eq(clientApplication));
        verify(clientApplicationDtoHelperService, times(2)).checkProductIdAndAdviserIdAreAllowedForLoggedInUser(any(String.class), any(String.class));
    }

    @Test
    public void submitShouldInvokeHelperServiceForSubmit(){

        ClientApplicationSubmitDto submitDto = getClientApplicationSubmitDto();
        ClientApplicationDto dto = mock(ClientApplicationDto.class);
        String adviserPositionId = EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID);
        String productId = EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID);

        ClientApplication clientApplication = createDraftAccount();
        clientApplication.setAdviserPositionId(adviserPositionId);
        clientApplication.setProductId(productId);
        when(repository.find(any(Long.class))).thenReturn(clientApplication);

        when(clientApplicationDtoConverterService.convertToMinimalDto(eq(clientApplication))).thenReturn(dto);
        when(clientApplicationDtoHelperService.submitDraftAccount(eq(dto), any(ServiceErrors.class), eq(clientApplication))).thenReturn(dto);

        service.submit(submitDto, serviceErrors);
        verify(clientApplicationDtoHelperService, times(1)).submitDraftAccount(eq(dto), any(ServiceErrors.class), eq(clientApplication));
    }


    private ClientApplication createDraftAccount() {
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setFormData(FormDataConstantsForTests.FORM_DATA_FOR_SUBMIT);
        return clientApplication;
    }

    private ClientApplicationSubmitDto getClientApplicationSubmitDto() {
        ClientApplicationSubmitDto submitDto = new ClientApplicationSubmitDto((new ClientApplicationKey(CLIENT_APPLICATION_ID)));
        submitDto.setAdviserId(DEFAULT_ENCODED_ADVISER_ID);
        submitDto.setProductId(DEFAULT_ENCODED_PRODUCT_ID);
        return submitDto;
    }

}
