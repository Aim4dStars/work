package com.bt.nextgen.api.corporateaction.v1.permission;

import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionAsimAccountService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionDirectAccountService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("corporateActionPermissions")
public class CorporateActionPermissionServiceImpl implements CorporateActionPermissionService{

	@Autowired
	private UserProfileService profileService;

	@Autowired
	private CorporateActionDirectAccountService corporateActionDirectAccountService;

	@Autowired
	private CorporateActionAsimAccountService corporateActionAsimAccountService;

	public boolean checkPermissionForUser() {
		return profileService.isInvestor() && (corporateActionDirectAccountService.hasDirectAccountWithUser() ||
				corporateActionAsimAccountService.hasAsimAccountWithUser());
	}

	public boolean checkSubmitPermission(List<String> accountIds) {
		return profileService.isInvestor() ? corporateActionDirectAccountService.hasDirectAccounts(accountIds) ||
				corporateActionAsimAccountService.hasAsimAccounts(accountIds) : true;
	}

	public boolean checkInvestorPermission(String accountId) {
		return profileService.isInvestor() ?
				corporateActionDirectAccountService.isDirectAccount(accountId) || corporateActionAsimAccountService.isAsimAccount(
						accountId) : true;
	}
}
