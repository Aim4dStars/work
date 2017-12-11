package com.bt.nextgen.core.domain.key;

//TODO - look at why clone is being implemented
@SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.NoCloneCheck",
            "checkstyle:com.puppycrawl.tools.checkstyle.checks.whitespace.NoLineWrapCheck"})
public class LongIdKey extends AbstractIdKey {
    public LongIdKey(Long id) {
        super(id);
    }

    @Override
    public Long getId() {
        return (Long) super.getId();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        LongIdKey cloneKey = (LongIdKey) super.clone();
        cloneKey.setId(this.getId().longValue());

        return cloneKey;
    }
}
