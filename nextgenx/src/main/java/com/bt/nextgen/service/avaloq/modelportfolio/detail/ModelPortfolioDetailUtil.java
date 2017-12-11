package com.bt.nextgen.service.avaloq.modelportfolio.detail;

import com.avaloq.abs.bb.fld_def.DateFld;
import com.avaloq.abs.bb.fld_def.IdFld;
import com.avaloq.abs.bb.fld_def.NrFld;
import com.avaloq.abs.bb.fld_def.TextFld;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class ModelPortfolioDetailUtil {

    private ModelPortfolioDetailUtil() {

    }

    public static IdFld getSafeId(String value, boolean extlIdRequired) {
        if (value == null) {
            return null;
        }
        return extlIdRequired ? AvaloqGatewayUtil.createExtlIdVal(value) : AvaloqGatewayUtil.createIdVal(value);
    }

    public static TextFld getSafeText(String value) {
        if (value == null) {
            return null;
        }
        return AvaloqGatewayUtil.createTextVal(value);
    }

    public static DateFld getSafeDate(DateTime value) {
        if (value == null) {
            return null;
        }
        return AvaloqGatewayUtil.createDateVal(value.toDate());
    }

    public static NrFld getSafeNumber(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return AvaloqGatewayUtil.createNumberVal(value);
    }

}
