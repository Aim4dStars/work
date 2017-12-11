package com.bt.nextgen.content.api.model;

import com.bt.nextgen.cms.CmsEntry;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

/**
 * Content DTO. Refactored to allow late/lazy evaluation of the content value, as this makes filtering
 * more efficient.
 */
public class ContentDto extends BaseDto implements KeyedDto <ContentKey>
{
	private final ContentKey key;
	private final String content;
	private final CmsEntry entry;

	public ContentDto(ContentKey key, String content)
	{
		this.key = key;
		this.content = content;
		this.entry = null;
	}

	public ContentDto(String key, String content) {
		this(new ContentKey(key), content);
	}

	public ContentDto(ContentKey key, CmsEntry entry) {
		this.key = key;
		this.content = null;
		this.entry = entry;
	}

	public ContentDto(String key, CmsEntry entry) {
		this(new ContentKey(key), entry);
	}

	@Override
	public ContentKey getKey()
	{
		return key;
	}

	public String getContent()
	{
		return entry == null ? content : entry.getValue();
	}
}
