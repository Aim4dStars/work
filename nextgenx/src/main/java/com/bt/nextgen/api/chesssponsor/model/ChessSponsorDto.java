package com.bt.nextgen.api.chesssponsor.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.util.List;

/**
 * Created by L078480 on 22/06/2017.
 */
public class ChessSponsorDto extends BaseDto {

    private List<ChessSponsorDataDto> chessSponsorDataDtoList;

    public List<ChessSponsorDataDto> getChessSponsorDataDtoList() {
        return chessSponsorDataDtoList;
    }

    public void setChessSponsorDataDtoList(List<ChessSponsorDataDto> chessSponsorDataDtoList) {
        this.chessSponsorDataDtoList = chessSponsorDataDtoList;
    }
}
