package com.bt.nextgen.api.smsf.service;


import com.bt.nextgen.api.smsf.model.AccountingSoftwareDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftware;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftwareImpl;
import com.bt.nextgen.service.integration.accountingsoftware.model.AuthorisationType;
import com.bt.nextgen.service.integration.accountingsoftware.model.SoftwareFeedStatus;
import com.bt.nextgen.service.integration.code.Code;

import java.util.Collection;

public final class AccountingSoftwareDtoConverter
{
	private AccountingSoftwareDtoConverter()
	{

	}


	public static AccountingSoftware convertToDomain(AccountingSoftwareDto softwareDto) {
		AccountingSoftwareImpl software = new AccountingSoftwareImpl();
		AccountKey key = AccountKey.valueOf(new EncodedString(softwareDto.getKey().getAccountId()).plainText());
		software.setKey(key);
		//software.setSoftware(AccountingSoftwareType.fromValue(softwareDto.getSoftwareName()));
		software.setSoftwareFeedStatus(SoftwareFeedStatus.fromValue(softwareDto.getFeedStatus()));
		return software;
	}

	public static AccountingSoftwareDto convertToDto(AccountingSoftware software, boolean accountantStatus, Collection<Code> categoryCodes) {
		AccountingSoftwareDto dto = new AccountingSoftwareDto();
		if (software!=null) {
			com.bt.nextgen.api.account.v2.model.AccountKey key =
					new com.bt.nextgen.api.account.v2.model.AccountKey(EncodedString.fromPlainText(software.getKey().getId()).toString());
			dto.setKey(key);
			if (software.getSoftwareFeedStatus()!=null) {
				dto.setFeedStatus(software.getSoftwareFeedStatus().getDisplayValue());
			}
			if (software.getSoftwareName()!=null) {
				dto.setSoftwareName((software.getSoftwareName().getValue()));
				dto.setSoftwareDisplayName(getSoftwareDisplayName(categoryCodes,dto.getSoftwareName()));
			}
			dto.setAccountantLinked(accountantStatus);
			dto.setStatus(true);
		}else{
			dto.setStatus(false);

			ServiceErrors errors = new FailFastErrorsImpl();
			errors.addError(new ServiceErrorImpl("Unable to change state"));

		}
		return dto;
	}

	public static void convertToDto(WrapAccountDetail account, AccountingSoftwareDto accSoftwareDto, Collection<Code> categoryCodes)
	{
		SubAccount subAccount = getExternalSubAccount(account);
		if(subAccount!=null) {
			accSoftwareDto.setFeedStatus(SoftwareFeedStatus.getDisplayValueFor(subAccount.getExternalAssetsFeedState()).getDisplayValue());
			if (subAccount.getAccntSoftware() != null) {
				accSoftwareDto.setSoftwareName(subAccount.getAccntSoftware().toLowerCase());
				accSoftwareDto.setSoftwareDisplayName(getSoftwareDisplayName(categoryCodes,accSoftwareDto.getSoftwareName()));

			}
		}
		if(accSoftwareDto.getSoftwareName()!=null && "bgl360".equals(accSoftwareDto.getSoftwareName()))
		{
			accSoftwareDto.setAuthorisationType(AuthorisationType.BGL.getDisplayValue());
		}
		else
		{
			accSoftwareDto.setAuthorisationType(AuthorisationType.CLASS.getDisplayValue());
		}
		if (account.getAccntPersonId() == null){
			accSoftwareDto.setAccountantLinked(false);
		}
		else
		{
			accSoftwareDto.setAccountantLinked(true);
		}

	}

	public static SubAccount getExternalSubAccount(WrapAccountDetail account) {
		SubAccount extAssetSubAccount = null;
        if (account != null && account.getSubAccounts() != null) {
            for (SubAccount subAccount : account.getSubAccounts()) {
                if (subAccount.getSubAccountType() != null && subAccount.getSubAccountType().getCode() != null
                        && subAccount.getSubAccountType().getCode().equals(ContainerType.EXTERNAL_ASSET.getCode())) {
                    extAssetSubAccount = subAccount;
                }
            }
        }
		return extAssetSubAccount;
	}

	public static String getSoftwareDisplayName(Collection<Code> categoryCodes, String softwareName){
		for(Code code:categoryCodes){
			if(code.getIntlId().equals(softwareName)){
				return code.getName();
			}
		}
		return null;
	}
}
