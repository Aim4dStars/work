package com.bt.nextgen.clients.api.service;

import com.bt.nextgen.address.service.AddressValidationService;
import com.bt.nextgen.address.service.ParseAustralianAddressResponseService;
import com.bt.nextgen.clients.api.model.AddressDto;
import com.bt.nextgen.clients.api.model.AddressKey;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.ApiException;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.model.Address;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import ns.btfin_com.party.partyservice.partyreply.v2_1.ParseAustralianAddressResponseMsgType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AddressDtoServiceImpl implements AddressDtoService
{
	public final static String AUSTRALIA = "AU";

	@Autowired
	private AddressValidationService addressValidationService;

	@Autowired
    @Qualifier("avaloqClientIntegrationService")
	private ClientIntegrationService clientService;

    @Autowired
    private ParseAustralianAddressResponseService parseAustralianAddressResponseService;

	public AddressDto validateAustralianAddress(AddressDto addressDto)
	{
        if (AUSTRALIA.equals(addressDto.getCountry())) {
            Address addressModel = toAddressModel(addressDto);
            ParseAustralianAddressResponseMsgType addressResponseMsgType = addressValidationService.validateAddress(addressModel);
            addressModel = parseAustralianAddressResponseService.getAddressFromParseAustralianAddressResponse(addressResponseMsgType);
            toAddressDto(addressDto, addressModel);
            addressDto.setCountry(AUSTRALIA); // So that we are independent of downstream "Australia" format.
        }
        return addressDto;
    }

    private String getAddressLine1FromDto(AddressDto addressDto) {
        List<String> addressArray = new ArrayList<>(Arrays.asList(addressDto.getUnitNumber(), addressDto.getStreetNumber(), addressDto.getStreetName()));
        if(addressDto.getStreetType() != null && !"null".equals(addressDto.getStreetType())){
            addressArray.add(addressDto.getStreetType());
        }
        return StringUtils.join(addressArray, ' ').trim();
    }

    private Address toAddressModel(AddressDto addressDto)
	{
		Address address = new Address();
        address.setAddressLine1(addressDto.getAddressLine1() == null ?
                getAddressLine1FromDto(addressDto) : addressDto.getAddressLine1());
		address.setAddressLine2(addressDto.getAddressLine2() == null ? "" : addressDto.getAddressLine2());
		address.setCountry(addressDto.getCountry());
		address.setCity(addressDto.getSuburb());
		address.setState(addressDto.getState());
		address.setPin(addressDto.getPostcode());
		address.setStreet(addressDto.getStreet());
		return address;
	}

	private void toAddressDto(AddressDto addressDto, Address address)
	{
		addressDto.setAddressLine1(address.getAddressLine1());
		addressDto.setAddressLine2(address.getAddressLine2());
		addressDto.setCountry(address.getCountry());
		addressDto.setCity(address.getCity());
		addressDto.setSuburb(address.getCity());
		addressDto.setState(address.getState());
		addressDto.setPin(address.getPin());
		addressDto.setPostcode(address.getPin());
		addressDto.setStreet(address.getStreet());
		addressDto.setErrorMessage(address.getPartyResponse());
		addressDto.setMatchConfidence(address.getMatchConfidence());
		addressDto.setPropertyName(address.getPropertyName());
		addressDto.setFloorNumber(address.getFloorNumber());
		addressDto.setUnitNumber(address.getUnitNumber());
		addressDto.setStreetNumber(address.getStreetNumber());
		addressDto.setStreetName(address.getStreetName());
		addressDto.setStreetType(address.getStreetType());
		addressDto.setPoBoxNumber(address.getPoBoxNumber());
		addressDto.setBoxPrefix(address.getBoxPrefix());
	}

	@Override
	public List <AddressDto> search(AddressKey key, ServiceErrors serviceErrors)
	{
		try
		{
            return getAddress(key);
		}
		catch (Exception e)
		{
			throw new ApiException(ApiVersion.CURRENT_VERSION, "Internal error occured ..!");
		}
	}

	public List <AddressDto> getAddress(AddressKey key) throws Exception
	{
        //TODO: Validate the fields.Changed for cash code clean up
		List <AddressDto> addressList = new ArrayList <AddressDto>();
	//	List <String> clientIdList = new ArrayList <String>();

		//clientIdList.add(new EncodedString("3EBF40671FE2F335D4C7CDE112402296A9393DE0FB941442").plainText());
		com.bt.nextgen.service.integration.userinformation.ClientDetail userDetails = clientService.loadClientDetails(ClientKey.valueOf(new EncodedString("3EBF40671FE2F335D4C7CDE112402296A9393DE0FB941442").plainText()), new ServiceErrorsImpl());

		//PersonInterface person = userDetails.get(0);

		for (com.bt.nextgen.service.integration.domain.Address address : userDetails.getAddresses())
		{
			if (!address.getAddressType().getAddressType().equalsIgnoreCase(key.getAddressType()))
			{
				AddressDto addressDtoObject = new AddressDto();
				addressDtoObject.setAddressCategory(String.valueOf(address.getCategoryId()));
			/*	addressDtoObject.setAddressKind(address.getAddressKind());
				addressDtoObject.setAddressLine1(address.getAddressLine1());
				addressDtoObject.setAddressLine2(address.getAddressLine2());
				addressDtoObject.setAddressMedium(address.getAddressMedium());*/
				addressDtoObject.setBoxPrefix(address.getPoBoxPrefix());
				addressDtoObject.setBuildingName(address.getBuilding());
				addressDtoObject.setCity(address.getCity());
				addressDtoObject.setCountry(address.getCountry());
				addressDtoObject.setFloorNumber(address.getFloor());
			//	addressDtoObject.setFullAddress(address.getFullAddress());
				addressDtoObject.setIsDomicileAddress(String.valueOf(address.isDomicile()));
				addressDtoObject.setIsMailingAddress(String.valueOf(address.isMailingAddress()));
				addressDtoObject.setPin(address.getCountryCode());
				addressDtoObject.setPoBoxNumber(address.getPoBox());
				addressDtoObject.setPostcode(address.getPostCode());
			//	addressDtoObject.setProfession(address.getProfession());
				addressDtoObject.setState(address.getState());
				addressDtoObject.setStreet(address.getStreetName());
				addressDtoObject.setStreetNumber(address.getStreetNumber());
				addressDtoObject.setStreetType(address.getStreetType());
				addressDtoObject.setSuburb(address.getSuburb());
				addressDtoObject.setAddressType(address.getAddressType().getAddressType());
				addressDtoObject.setUnitNumber(address.getUnit());

				addressList.add(addressDtoObject);
			}
		}
		if (!"11861".equals(new EncodedString("3EBF40671FE2F335D4C7CDE112402296A9393DE0FB941442").plainText()))
		{
			AddressDto addressDtoObject = new AddressDto();
			addressDtoObject.setAddressLine1("6-8 Taylor street");
			addressDtoObject.setAddressLine2("Paramatta");
			addressDtoObject.setCity("Sydney");
			addressDtoObject.setCountry("Aus");
			addressDtoObject.setPin("1234");
			addressDtoObject.setPoBoxNumber("1234");
			addressDtoObject.setPostcode("1234");
			addressDtoObject.setState("NSW");
			addressDtoObject.setStreetNumber("6-8");
			addressDtoObject.setSuburb("Paramatta");
			addressDtoObject.setAddressType("Residential");
			addressList.add(addressDtoObject);

		}

		return addressList;
	}

	@Override
	public AddressDto find(AddressKey key, ServiceErrors serviceErrors)
	{
		return null;
	}
}
