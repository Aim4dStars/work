package com.bt.nextgen.core.security.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.core.security.api.model.RoleDto;
import com.bt.nextgen.core.security.api.model.RoleKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;

@Service
@Transactional(value = "springJpaTransactionManager")
class RoleDtoServiceImpl implements RoleDtoService
{
	@Autowired
	private UserProfileService userProfileService;

	public RoleDtoServiceImpl()
	{}

	protected List <RoleDto> getRoles()
	{
		List <RoleDto> roles = new ArrayList <>();

		for (Roles role : Roles.values())
		{
			RoleKey key = new RoleKey(role.name());
			roles.add(new RoleDto(key, userProfileService.getEffectiveProfile().hasRole(role)));
		}

		return roles;
	}

	protected RoleDto getRole(RoleKey roleKey)
	{
		return new RoleDto(roleKey, userProfileService.getEffectiveProfile().hasRole(Roles.valueOf(roleKey.getRoleId())));
	}

	@Override
	@Transactional(value = "springJpaTransactionManager", readOnly = true)
	public RoleDto find(RoleKey key, ServiceErrors serviceErrors)
	{
		return getRole(key);
	}

	@Override
	@Transactional(value = "springJpaTransactionManager", readOnly = true)
	public List <RoleDto> findAll(ServiceErrors serviceErrors)
	{
		return getRoles();
	}
}
