package com.bt.nextgen.api.util;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class TermDepositUtil {

    private TermDepositUtil() {

    }

    /**
     * Retrieve the maturity-instruction for display on screen. Specific display-rules have been requested by PO such that
     * different instructions need to be displayed in the case where instruction is null or when 'None (Close TD)'.
     * 
     * @param instr
     * @param filterNull
     * @return
     */
    public static String getMaturityInstructionForDisplay(String instr, String cashBrand, boolean filterNull) {
        String defaultInstr = "Deposit all money into " + cashBrand;

        if (instr == null && filterNull) {
            return defaultInstr;
        }

        if (instr != null) {
            if ("None (Close TD)".equalsIgnoreCase(instr)) {
                return defaultInstr;
            }
            return instr;
        }
        return null;
    }

    public static String getMaturityInstruction(String instructionCode, String cashBrand,
            StaticIntegrationService staticIntegrationService, ServiceErrors serviceErrors) {

        String maturityInstruction = null;
        if (StringUtils.isNotBlank(instructionCode)) {
            Code code = staticIntegrationService.loadCode(CodeCategory.TD_RENEW_MODE, instructionCode, serviceErrors);
            if (code != null) {
                maturityInstruction = code.getName();
            }
        }

        return getMaturityInstructionForDisplay(maturityInstruction, cashBrand, true);
    }

    public static Integer getDaysUntilMaturity(DateTime maturityDate) {
        if (maturityDate == null) {
            return 0;
        }
        return Days.daysBetween(new DateTime().withTimeAtStartOfDay(), maturityDate.withTimeAtStartOfDay()).getDays();
    }

    public static String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMM yyyy");
        DateTime currentDate = DateTime.now();
        return currentDate.toString(formatter);
    }

    public static String appendDoubleQuoutesForComma(String value) {
        StringBuilder newVal = new StringBuilder(value);
        if (value.contains(",")) {
            newVal = newVal.append("\"").append(value).append("\"");
        }
        return newVal.toString();
    }

}
