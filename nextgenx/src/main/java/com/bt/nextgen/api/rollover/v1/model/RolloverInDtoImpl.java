package com.bt.nextgen.api.rollover.v1.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.List;

public class RolloverInDtoImpl extends BaseDto implements RolloverInDto {
    @JsonView(JsonViews.Write.class)
    private RolloverKey key;

    @JsonView(JsonViews.Write.class)
    private List<RolloverDetailsDto> rolloverDetails;

    @JsonView(JsonViews.Write.class)
    private String rolloverType;

    private List<DomainApiErrorDto> warnings;

    public RolloverInDtoImpl() {
        super();
    }

    public RolloverInDtoImpl(RolloverKey key, String rolloverType, List<RolloverDetailsDto> rolloverDetails) {
        this.key = key;
        this.rolloverType = rolloverType;
        this.rolloverDetails = rolloverDetails;

        extractRolloverDetailsDtoErrors(rolloverDetails);
    }

    public RolloverKey getKey() {
        return key;
    }

    @Override
    public List<RolloverDetailsDto> getRolloverDetails() {
        return rolloverDetails;
    }

    @Override
    public String getRolloverType() {
        return rolloverType;
    }

    @Override
    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    protected void extractRolloverDetailsDtoErrors(List<RolloverDetailsDto> rolloverDetails) {
        warnings = new ArrayList<>();
        for (RolloverDetailsDto dto : rolloverDetails) {
            if (dto.getWarnings() != null) {
                StringBuilder buf = new StringBuilder();
                buf.append(dto.getFundName());
                buf.append(" ABN: ");
                buf.append(dto.getFundAbn());
                buf.append(" USI: ");
                buf.append(dto.getFundUsi());

                for (DomainApiErrorDto errorDto : dto.getWarnings()) {
                    errorDto.setDomain(buf.toString());
                }
                warnings.addAll(dto.getWarnings());
            }
        }
    }
}
