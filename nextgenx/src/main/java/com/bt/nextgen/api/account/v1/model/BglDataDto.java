package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.core.api.model.FileDto;
import com.bt.nextgen.core.api.model.KeyedDto;

/**
 * @deprecated Use V2
 */
@Deprecated
public class BglDataDto extends FileDto implements KeyedDto<DateRangeAccountKey> {

    /** The key. */
    private DateRangeAccountKey key;

    /**
     * Instantiates a new bgl data dto.
     *
     * @param key
     *            the key
     * @param data
     *            the data
     */
    public BglDataDto(DateRangeAccountKey key, byte[] data) {
        super(data);
        this.key = key;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.core.api.model.KeyedDto#getKey()
     */
    @Override
    public DateRangeAccountKey getKey() {
        return key;
    }

}
