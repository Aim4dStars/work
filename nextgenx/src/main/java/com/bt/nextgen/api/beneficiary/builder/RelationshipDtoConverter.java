package com.bt.nextgen.api.beneficiary.builder;

import com.bt.nextgen.api.beneficiary.model.RelationshipTypeDto;
import com.bt.nextgen.service.avaloq.beneficiary.RelationshipType;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;

import java.util.*;

/**
 * This converter class maps the Avaloq's Code object into Dto object to be rendered to UI.
 * Created by M035995 on 4/07/2016.
 */
public class RelationshipDtoConverter {

    private static final String FIELD_DEPENDENT_TYPE = "btfg$ui_dep";

    private static final String RELATIONSHIP_LIST_NAME = "person_rel_type";

    /**
     * This method massages the data retrieved from Avaloq to be passed to UI as a dto object.
     *
     * @param relationshipCodeList list containing avaloq relationship codes
     * @return List of RelationshipTypeDto
     */
    public final List<RelationshipTypeDto> getRelationshipList(Collection<Code> relationshipCodeList) {
        //Define the TreeMap which will by default sort the map in ascending order of the key
        final List<RelationshipTypeDto> relationshipTypeDtoList = new ArrayList<>();
        RelationshipTypeDto relationshipDto = null;

        Field labelField = null;
        RelationshipType relationshipType = null;
        // Iterate over the relationshipCodeList and populate the DTO object into DTO list.
        for (Code code : relationshipCodeList) {
            // If the field value is '+', return true
            labelField = code.getField(FIELD_DEPENDENT_TYPE);
            relationshipType = RelationshipType.findByAvaloqId(code.getIntlId());
            if(relationshipType != null) {
                relationshipDto = new RelationshipTypeDto(code.getCodeId(), code.getUserId(), code.getIntlId(),
                        RELATIONSHIP_LIST_NAME, code.getName(), "+".equals(labelField.getValue()), relationshipType.
                        getOrderId());

                relationshipTypeDtoList.add(relationshipDto);
            }
        }

        Collections.sort(relationshipTypeDtoList, new Comparator<RelationshipTypeDto>() {
            @Override
            public int compare(RelationshipTypeDto o1, RelationshipTypeDto o2) {
                return o1.getOrderId().compareTo(o2.getOrderId());
            }
        });

        return relationshipTypeDtoList;
    }

}
