package com.bt.nextgen.api.staticdata.model;

/**
 * Extends <code>StaticCode</code> class to include ordering.
 */
@SuppressWarnings("squid:S1068")
public class StaticCodeOrderedDto extends StaticCodeDto
{
    private final int order;

    public StaticCodeOrderedDto(StaticCodeDto staticCodeDto, int order)
    {
        super(staticCodeDto);
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StaticCodeOrderedDto) {
            final StaticCodeOrderedDto ordered = (StaticCodeOrderedDto) o;
            return super.equals(ordered) && order == ordered.order;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + order;
    }
}
