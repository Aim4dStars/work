package com.bt.nextgen.api.version.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.domain.key.StringIdKey;

public class MobileAppVersionDto extends BaseDto implements KeyedDto<StringIdKey> {
    private StringIdKey platform;
    private String minVersion;

    public MobileAppVersionDto() {
    }

    /**
     * Creates a new instance of MobileVersionDto
     *
     * @param platform - application platform
     * @param minVersion - minimum supported version
     */
    public MobileAppVersionDto(String platform, String minVersion) {
        this.platform = new StringIdKey(platform);
        this.minVersion = minVersion;
    }

   public String getMinVersion() {
        return minVersion;
    }

    @Override
    public StringIdKey getKey() {
        return platform;
    }
}