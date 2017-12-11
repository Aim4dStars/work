package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.model.ANZSICCodeDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.map;
import static com.bt.nextgen.api.country.service.CountryDtoServiceImpl.UCM_CODE;
import static com.bt.nextgen.api.country.service.CountryDtoServiceImpl.fieldValue;
import static com.btfin.panorama.core.conversion.CodeCategory.ANZSIC_DIVISION;
import static com.btfin.panorama.core.conversion.CodeCategory.ANZSIC_GROUP;
import static com.btfin.panorama.core.conversion.CodeCategory.ANZSIC_INDUSTRY;
import static com.btfin.panorama.core.conversion.CodeCategory.ANZSIC_SUBDIVISION;
import static java.util.Collections.sort;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.hasText;

/**
 * Implement the ANZ SIC Code service. Re-factored from the original service (which used a static JSON web resource),
 * to use the Avaloq static codes, which are synchronized with Westpac GCM.
 */
@Service
public class ANZSICCodeDtoServiceImpl implements ANZSICCodeDtoService {

    /** Logger. */
    private static final Logger LOGGER = getLogger(ANZSICCodeDtoServiceImpl.class);

    /** External field name for ANZSIC group ID. Used to correlate ANZSIC industry instances under their respective groups. */
    private static final String ANZSIC_GRP_ID = "btfg$anzsic_grp_id";

    /** External field name for ANZSIC Subdivision id. Used to correlate ANZSIC industry instances under their respective suddivisions. */
    private static final String ANZSIC_SUB_DIV_ID = "btfg$anzsic_sub_div_id";

    /** External field name for ANZSIC division ID. Used to correlate ANZSIC industry instances under their respective divisions. */
    private static final String ANZSIC_DIV_ID = "btfg$anzsic_div_id";

    /** Extract the key from the CodeID of the static code. */
    private static final Converter<Code, String> BY_CODE_ID = new Converter<Code, String>() {
        @Override
        public String convert(Code code) {
            return code.getCodeId();
        }
    };

    /** Sort resultant ANZ SIC codes by division, sub-division, group and finally industry. */
    private static final Comparator<ANZSICCodeDto> SORTER = new Comparator<ANZSICCodeDto>() {

        private String[] fieldsOf(ANZSICCodeDto code) {
            return new String[]{ code.getIndustryDivision(), code.getIndustrySubdivision(),
                    code.getIndustryGroup(), code.getIndustryClass(), };
        }

        @Override
        public int compare(ANZSICCodeDto code1, ANZSICCodeDto code2) {
            final String[] fields1 = fieldsOf(code1);
            final String[] fields2 = fieldsOf(code2);
            for (int i = 0; i < fields1.length; i++) {
                final int comparison = fields1[i].compareTo(fields2[i]);
                if (comparison != 0) {
                    return comparison;
                }
            }
            return 0;
        }
    };

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Override
    public List<ANZSICCodeDto> findAll(ServiceErrors errors) {
        final Map<String, Code> divisions = map(staticIntegrationService.loadCodes(ANZSIC_DIVISION, errors), BY_CODE_ID);
        final Map<String, Code> subdivisions = map(staticIntegrationService.loadCodes(ANZSIC_SUBDIVISION, errors), BY_CODE_ID);
        final Map<String, Code> groups = map(staticIntegrationService.loadCodes(ANZSIC_GROUP, errors), BY_CODE_ID);
        final Collection<Code> industries = staticIntegrationService.loadCodes(ANZSIC_INDUSTRY, errors);
        final List<ANZSICCodeDto> dtos = new ArrayList<>(industries.size());

        for (Code industry : industries) {
            final String userId = industry.getUserId();
            final String industryName = industry.getName();
            String ucmCode = fieldValue(industry, UCM_CODE);
            if (!hasText(ucmCode)) {
                LOGGER.warn("Null/empty {} field for ANZSIC industry {}: {}", UCM_CODE, userId, industryName);
                ucmCode = userId;
            }
            final String groupId = fieldValue(industry, ANZSIC_GRP_ID);
            if (hasText(groupId)) {
                final Code group = groups.get(groupId);
                if (group != null) {
                    setGrpDivisionNames(divisions, subdivisions, dtos, industry, groupId, ucmCode, group);
                    //dtos.add(new ANZSICCodeDto(userId, userId, ucmCode, divisionName, sudDivisionName, group.getName(), industryName));
                } else {
                    LOGGER.warn("No ANZSIC group found for ID: {} of industry {}: {}", groupId, userId, industryName);
                }
            } else {
                LOGGER.warn("Null/empty external field {} for ANZSIC industry {}: {}", ANZSIC_GRP_ID, userId, industryName);
            }
        }
        sort(dtos, SORTER);
        return dtos;
    }

    private void setGrpDivisionNames(Map<String, Code> divisions, Map<String, Code> subdivisions, List<ANZSICCodeDto> dtos, Code industry, String groupId, String ucmCode, Code group) {
        final String userId = industry.getUserId();
        final String industryName = industry.getName();
        final String subDivisionID = fieldValue(group, ANZSIC_SUB_DIV_ID);
        if(hasText(subDivisionID)) {
            Code subDivision = subdivisions.get(subDivisionID);
            if (subDivision != null) {
                final String divisionID = fieldValue(subDivision, ANZSIC_DIV_ID);
                if(hasText(divisionID)) {
                    Code division = divisions.get(divisionID);
                    if (division != null) {
                        dtos.add(new ANZSICCodeDto(userId, userId, ucmCode, division.getName(), subDivision.getName(), group.getName(), industryName));
                    } else {
                        LOGGER.warn("No ANZSIC division found for ID: {}  of division{} of group :{} of industry {}: {}",divisionID, subDivisionID, groupId,  userId, industryName);
                    }
                }else{
                    LOGGER.warn("Null/empty external field {} for subdivision{} of group{} for ANZSIC industry {}: {}", ANZSIC_DIV_ID ,subDivisionID ,groupId, userId, industryName);
                }
            } else {
                LOGGER.warn("No ANZSIC Subdivision found for ID: {} of group {} of industry {}: {}", subDivisionID, groupId, userId, industryName);
            }
        }else{
            LOGGER.warn("Null/empty external field {} for group: {} for ANZSIC industry {}: {}", ANZSIC_SUB_DIV_ID, groupId, userId, industryName);
        }
    }
}
