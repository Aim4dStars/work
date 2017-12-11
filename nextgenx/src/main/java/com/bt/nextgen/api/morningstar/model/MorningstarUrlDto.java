package com.bt.nextgen.api.morningstar.model;


import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

/**
 * Morningstar DTO - currently consists of URL only
 */
public class MorningstarUrlDto extends BaseDto implements KeyedDto<MorningstarUrlKey> {
	private String url;

	public MorningstarUrlDto() {
	}

	public MorningstarUrlDto(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public MorningstarUrlKey getKey() {
		return null;
	}
}
