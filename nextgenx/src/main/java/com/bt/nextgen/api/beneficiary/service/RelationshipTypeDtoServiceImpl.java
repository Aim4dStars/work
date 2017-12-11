package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.beneficiary.builder.RelationshipDtoConverter;
import com.bt.nextgen.api.beneficiary.model.RelationshipTypeDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by M035995 on 1/07/2016.
 */
@Service
public class RelationshipTypeDtoServiceImpl implements RelationshipTypeDtoService {

    /**
     * Static code integration service.
     */
    @Autowired
    private StaticIntegrationService staticIntegrationService;

    /**
     * Get all relationship types for Beneficiary functionality.
     *
     * @param serviceErrors object of ServiceErrors.
     * @return list of {@link RelationshipTypeDto}
     */
    @Override
    public List<RelationshipTypeDto> findAll(ServiceErrors serviceErrors) {
        return new RelationshipDtoConverter().getRelationshipList(staticIntegrationService.
                loadCodes(CodeCategory.SUPER_RELATIONSHIP_TYPE, new ServiceErrorsImpl()));
    }

}
