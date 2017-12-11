package com.bt.nextgen.api.version.service;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.version.model.MobileAppVersionDto;
import com.bt.nextgen.api.version.model.MobilePlatform;
import com.bt.nextgen.core.domain.key.StringIdKey;
import com.bt.nextgen.core.repository.MobileAppVersion;
import com.bt.nextgen.core.repository.MobileAppVersionRepository;
import com.bt.nextgen.service.ServiceErrors;

import static ch.lambdaj.Lambda.*;

@Service
public class MobileAppVersionDtoServiceImpl implements MobileAppVersionDtoService {

    @Autowired
    private MobileAppVersionRepository mobileAppVersionRepository;

    private static final String DEFAULT_MIN_VERSION = "2.0.0";

    @Override
    public List<MobileAppVersionDto> findAll(ServiceErrors serviceErrors) {
        final List<MobileAppVersion> appVersions = mobileAppVersionRepository.findAppVersions();
        return convertToDto(appVersions);
    }

    @Override
    public MobileAppVersionDto update(MobileAppVersionDto mobileAppVersionDto, ServiceErrors serviceErrors) {
        mobileAppVersionRepository.update(new MobileAppVersion(mobileAppVersionDto.getKey().getId(),
                mobileAppVersionDto.getMinVersion()));
        return mobileAppVersionDto;
    }

    private List<MobileAppVersionDto> convertToDto(List<MobileAppVersion> appVersions) {
        final List<MobileAppVersionDto> mobileVersions = new ArrayList<>();

        for (MobileAppVersion appVersion : appVersions) {
            if (appVersion != null) {
                mobileVersions.add(new MobileAppVersionDto(appVersion.getPlatform(), appVersion.getVersion()));
            }
        }

        return mobileVersions;
    }

    @Override
    public MobileAppVersionDto find(StringIdKey key, ServiceErrors serviceErrors) {
        final List<MobileAppVersion> appVersions = mobileAppVersionRepository.findAppVersions();
        MobileAppVersion commonAppVersionObj = selectFirst(appVersions,
                having(on(MobileAppVersion.class).getPlatform(), Matchers.is(key.getId())));
        String minVersion = commonAppVersionObj != null ? commonAppVersionObj.getVersion() : DEFAULT_MIN_VERSION;
        String platformName = MobilePlatform.forPlatform(key.getId()).getPlatformName();
        return new MobileAppVersionDto(platformName, minVersion);
    }
}
