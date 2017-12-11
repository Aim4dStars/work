package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.draftaccount.schemas.AbstractObjectMapperTest;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import com.bt.nextgen.config.JsonObjectMapper;
import org.hamcrest.Matcher;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;

/**
 * Base class for tests that require access to a {@code JsonObjectMapper}.
 */
public abstract class AbstractJsonObjectMapperTest<J> extends AbstractObjectMapperTest<J> {

    /** Static reference to a JSON Object Mapper instance. */
    private static final JsonObjectMapper MAPPER = new JsonObjectMapper();

    protected AbstractJsonObjectMapperTest(Class<J> clazz, String pathPrefix) {
        super(clazz, pathPrefix);
    }

    /**
     * Default path prefix for this class: {@code com.bt.nextgen.api.draftaccount.model}
     * @param clazz class being JSON-managed under this test.
     */
    protected AbstractJsonObjectMapperTest(Class<J> clazz) {
        this(clazz, pathToClass(AbstractJsonObjectMapperTest.class));
    }

    /**
     * Override the ObjectMapper method to specifically return the {@code JsonObjectMapper} instance.
     * @return the
     */
    @Override
    protected JsonObjectMapper getObjectMapper() {
        return MAPPER;
    }

    /**
     * Shortcut method for matching BigDecimal amounts.
     * @param amount the amount to match in String format.
     * @return the matcher.
     */
    public static Matcher<BigDecimal> isBD(String amount) {
        return is(new BigDecimal(amount));
    }

    public static Matcher<XMLGregorianCalendar> isDate(final String ddmmyyyy) {
        return is(XMLGregorianCalendarUtil.date(ddmmyyyy));
    }
}
