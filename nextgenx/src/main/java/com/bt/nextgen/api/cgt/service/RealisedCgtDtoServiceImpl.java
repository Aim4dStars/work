package com.bt.nextgen.api.cgt.service;

import com.bt.nextgen.api.cgt.model.CgtDto;
import com.bt.nextgen.api.cgt.model.CgtGroupDto;
import com.bt.nextgen.api.cgt.model.CgtKey;
import com.bt.nextgen.api.cgt.model.CgtSecurity;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.cgt.WrapCgtDataImpl;
import com.bt.nextgen.service.integration.cgt.CgtIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(value = "springJpaTransactionManager")
class RealisedCgtDtoServiceImpl extends CgtDtoServiceImpl implements RealisedCgtDtoService {

    @Autowired
    private CgtIntegrationService cgtIntegrationService;

    @Override
    public CgtDto find(CgtKey key, ServiceErrors serviceErrors) {

        String accountId = EncodedString.toPlainText(key.getAccountId());
        WrapCgtDataImpl wrapCgtData = (WrapCgtDataImpl) cgtIntegrationService.loadRealisedCgtDetails(accountId,
                key.getStartDate(), key.getEndDate(), serviceErrors);

        if (wrapCgtData == null) {
            List<CgtGroupDto> emptyGroupList = new ArrayList<>();
            return new CgtDto(key, emptyGroupList);
        }

        List<CgtSecurity> securities = getCgtSecurities(wrapCgtData, REALISED_CGT_DATA);
        Map<String, List<CgtSecurity>> securitiesMap = groupById(securities, key.getGroupBy());

        List<CgtGroupDto> groupList = new ArrayList<>();

        if (securitiesMap != null) {
            for (String pid : securitiesMap.keySet()) {
                groupList.add(getCgtGroupDto(securitiesMap.get(pid), key.getGroupBy()));
            }
        }

        return new CgtDto(key, groupList);
    }
}
