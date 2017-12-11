package com.bt.nextgen.api.user.v1.service;

import com.bt.nextgen.api.user.v1.model.TermsAndConditionsDto;
import com.bt.nextgen.api.user.v1.model.TermsAndConditionsDtoKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.termsandconditions.model.TermsAndConditions;
import com.bt.nextgen.service.integration.termsandconditions.model.TermsAndConditionsType;
import com.bt.nextgen.service.integration.termsandconditions.model.UserTermsAndConditions;
import com.bt.nextgen.service.integration.termsandconditions.model.UserTermsAndConditionsKey;
import com.bt.nextgen.service.integration.termsandconditions.repository.TermsAndConditionsRepository;
import com.bt.nextgen.service.integration.termsandconditions.repository.UserTermsAndConditionsRepository;
import com.bt.nextgen.service.integration.user.UserKey;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This service retrieves and updates a user's saved preferences.
 */
@Service("TermsAndConditionsDtoServiceV1")
public class TermsAndConditionsDtoServiceImpl implements TermsAndConditionsDtoService {

    @Autowired
    private TermsAndConditionsRepository tncRepository;

    @Autowired
    private UserTermsAndConditionsRepository userRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public List<TermsAndConditionsDto> findAll(ServiceErrors serviceErrors) {

        UserKey userKey = UserKey.valueOf(userProfileService.getGcmId());
        Map<TermsAndConditionsType, TermsAndConditions> tncs = getTncMap(tncRepository.findAll());
        Map<Pair<TermsAndConditionsType, Integer>, UserTermsAndConditions> userTncs = getUserTncMap(
                userRepository.search(userKey));

        List<TermsAndConditionsDto> result = new ArrayList<>();
        for (TermsAndConditions tnc : tncs.values()) {
            result.add(buildTermsAndConditionsDto(userKey, tnc, userTncs));
        }
        return result;
    }

    @Override
    public TermsAndConditionsDto submit(TermsAndConditionsDto dto, ServiceErrors serviceErrors) {
        String gcmId = EncodedString.toPlainText(dto.getKey().getUserId());
        TermsAndConditionsType type = TermsAndConditionsType.valueOf(dto.getKey().getTermAndConditions());
        Integer version = Integer.valueOf(dto.getKey().getVersion());
        UserTermsAndConditionsKey userTncKey = new UserTermsAndConditionsKey(gcmId, type, version);
        UserTermsAndConditions userTnc = new UserTermsAndConditions(userTncKey);
        userRepository.save(userTnc);
        userTnc = userRepository.find(userTncKey);
        List<TermsAndConditions> tncs = tncRepository.findAll();
        return buildTermsAndConditionsDto(userTnc, tncs);
    }

    private Map<TermsAndConditionsType, TermsAndConditions> getTncMap(List<TermsAndConditions> tncs) {
        Map<TermsAndConditionsType, TermsAndConditions> tncMap = new HashMap<>();
        for (TermsAndConditions tnc : tncs) {
            TermsAndConditions existing = tncMap.get(tnc.getUserTermsAndConditionsKey().getTncId());
            if (existing == null) {
                tncMap.put(tnc.getUserTermsAndConditionsKey().getTncId(), tnc);
            } else {
                if (existing.getUserTermsAndConditionsKey().getVersion()
                        .compareTo(tnc.getUserTermsAndConditionsKey().getVersion()) < 0) {
                    tncMap.put(tnc.getUserTermsAndConditionsKey().getTncId(), tnc);
                }
            }
        }
        return tncMap;
    }

    private Map<Pair<TermsAndConditionsType, Integer>, UserTermsAndConditions> getUserTncMap(List<UserTermsAndConditions> tncs) {
        Map<Pair<TermsAndConditionsType, Integer>, UserTermsAndConditions> tncMap = new HashMap<>();
        for (UserTermsAndConditions tnc : tncs) {
            tncMap.put(new ImmutablePair<>(tnc.getUserTermsAndConditionsKey().getTncId(),
                    tnc.getUserTermsAndConditionsKey().getVersion()), tnc);
        }
        return tncMap;
    }

    private TermsAndConditionsDto buildTermsAndConditionsDto(UserKey userKey, TermsAndConditions tnc,
            Map<Pair<TermsAndConditionsType, Integer>, UserTermsAndConditions> userTncs) {
        DateTime acceptedDate = null;
        TermsAndConditionsDtoKey key = new TermsAndConditionsDtoKey(EncodedString.fromPlainText(userKey.getId()).toString(),
                tnc.getUserTermsAndConditionsKey().getTncId().name(), tnc.getUserTermsAndConditionsKey().getVersion());

        UserTermsAndConditions userTnc = userTncs.get(new ImmutablePair<>(tnc.getUserTermsAndConditionsKey().getTncId(),
                tnc.getUserTermsAndConditionsKey().getVersion()));
        if (userTnc != null) {
            acceptedDate = userTnc.getTncAcceptedOn();
        }

        return new TermsAndConditionsDto(key, tnc.getDescription(), tnc.getLastModified(), acceptedDate);
    }

    private TermsAndConditionsDto buildTermsAndConditionsDto(UserTermsAndConditions userTnc, List<TermsAndConditions> tncs) {

        for (TermsAndConditions tnc : tncs) {
            if (tnc.getUserTermsAndConditionsKey().getTncId() == userTnc.getUserTermsAndConditionsKey().getTncId() && tnc
                    .getUserTermsAndConditionsKey().getVersion().equals(userTnc.getUserTermsAndConditionsKey().getVersion())) {
                TermsAndConditionsDtoKey key = new TermsAndConditionsDtoKey(
                        EncodedString.fromPlainText(userTnc.getUserTermsAndConditionsKey().getGcmId()).toString(),
                        tnc.getUserTermsAndConditionsKey().getTncId().name(), tnc.getUserTermsAndConditionsKey().getVersion());
                return new TermsAndConditionsDto(key, tnc.getDescription(), tnc.getLastModified(), userTnc.getTncAcceptedOn());
            }
        }
        return null;
    }
}
