package com.bt.nextgen.api.portfolio.v3.service.allocation;

import com.bt.nextgen.api.portfolio.v3.model.allocation.AllocationDto;

import java.io.Serializable;
import java.util.Comparator;

class AllocationComparator implements Comparator<AllocationDto>, Serializable {
    private static final long serialVersionUID = -1985127129239288731L;

    @Override
    public int compare(AllocationDto o1, AllocationDto o2) {
        if (o1.getIsExternal() && !o2.getIsExternal()) {
            return 1;
        }
        if (!o1.getIsExternal() && o2.getIsExternal()) {
            return -1;
        }

        String o1Name = o1.getName();
        String o2Name = o2.getName();
        return o1Name.compareToIgnoreCase(o2Name);
    }
}