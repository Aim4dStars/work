package com.bt.nextgen.core.toggle;

import com.bt.nextgen.core.api.dto.FindOneDtoService;

/**
 * Service for fetching the current <i>GLOBAL</i> set of release toggles that have been configured for the
 * application.
 */
public interface FeatureTogglesService extends FindOneDtoService<FeatureToggles> {
}
