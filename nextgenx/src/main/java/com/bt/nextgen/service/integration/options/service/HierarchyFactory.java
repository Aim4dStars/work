package com.bt.nextgen.service.integration.options.service;

import com.bt.nextgen.core.domain.key.AbstractKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.options.model.CategoryKey;

import java.util.List;

public interface HierarchyFactory<T extends AbstractKey> {
    List<CategoryKey> buildHierarchy(T key, ServiceErrors serviceErrors);
}
