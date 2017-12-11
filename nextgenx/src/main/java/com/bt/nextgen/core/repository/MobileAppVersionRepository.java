package com.bt.nextgen.core.repository;

import java.util.List;

public interface MobileAppVersionRepository {

    List<MobileAppVersion> findAppVersions();

    MobileAppVersion update(MobileAppVersion appVersion);
}
