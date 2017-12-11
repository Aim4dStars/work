package com.bt.nextgen.api.country.model;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static org.springframework.util.StringUtils.hasText;

/**
 * International (two-letter) country code. Used as key to the {@link CountryDto}.
 */
public class CountryCode {

    /** Australia country code. */
    public static final CountryCode AU = new CountryCode("AU");

    @Nonnull
    private final String code;

    public CountryCode(@Nonnull String code) {
        this.code = code.toUpperCase();
    }

    @Nonnull
    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CountryCode && code.equals(((CountryCode) obj).code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    /**
     * Parses a comma-delimited list of country codes.
     * @param codeList string containing the comma-delimited list of country codes.
     * @return the list of parsed country codes.
     */
    public static @Nonnull List<CountryCode> parseCountryCodes(@Nonnull String codeList) {
        final StringTokenizer tokens = new StringTokenizer(codeList, ",");
        final List<CountryCode> codes = new ArrayList<>(tokens.countTokens());
        while (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken();
            if (hasText(token)) {
                codes.add(new CountryCode(token.trim()));
            }
        }
        return codes;
    }
}
