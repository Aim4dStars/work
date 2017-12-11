package com.bt.nextgen.api.cgt.service;

import com.bt.nextgen.api.cgt.model.CgtDto;
import com.bt.nextgen.api.cgt.model.CgtGroupDto;
import com.bt.nextgen.api.cgt.model.CgtKey;
import com.bt.nextgen.api.cgt.model.CgtSecurity;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.cgt.CgtIntegrationService;
import com.bt.nextgen.service.integration.cgt.WrapCgtData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(value = "springJpaTransactionManager")
class UnrealisedCgtDtoServiceImpl extends CgtDtoServiceImpl implements UnrealisedCgtDtoService {

    @Autowired
    private CgtIntegrationService cgtIntegrationService;

    @Override
    public CgtDto find(CgtKey key, ServiceErrors serviceErrors) {

        WrapCgtData wrapCgtData = (WrapCgtData) cgtIntegrationService.loadUnrealisedCgtDetails(
                EncodedString.toPlainText(key.getAccountId()), key.getEndDate(), serviceErrors);

        if (wrapCgtData == null) {
            List<CgtGroupDto> dtoList = new ArrayList<>();
            return new CgtDto(key, dtoList);
        }

        List<CgtSecurity> securities = getCgtSecurities(wrapCgtData, UNREALISED_CGT_DATA);
        Map<String, List<CgtSecurity>> assetDtoMap = groupById(securities, key.getGroupBy());

        List<CgtGroupDto> groupList = new ArrayList<>();

        if (assetDtoMap != null) {
            for (String pid : assetDtoMap.keySet()) {
                groupList.add(getCgtGroupDto(assetDtoMap.get(pid), key.getGroupBy()));
            }
        }

        return new CgtDto(key, groupList);
    }
}
