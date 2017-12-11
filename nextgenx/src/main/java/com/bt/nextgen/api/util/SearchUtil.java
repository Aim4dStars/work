package com.bt.nextgen.api.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchUtil {
    //	Regex matchers
    private static final String IGNORE_CHARACTERS = "(\\s*'\\s*)|_";
    private static final String SPACE_CHARACTERS = "[,-]";
    private static final String INVALID_CHARACTERS = "[^a-zA-Z0-9'.,_/&\\s-]";

    /**
     * @param searchKey
     * @return java.util.regex.Pattern
     */
    public static Pattern getPattern(String searchKey) {
        String regexString = generateRegexString(searchKey);
        return StringUtils.isNotBlank(regexString) ? Pattern.compile(regexString, Pattern.CASE_INSENSITIVE) : null;
    }

    /**
     * @param pattern
     * @param values  - the values to be searched on. Operation is OR
     * @return
     */
    public static boolean matches(Pattern pattern, String... values) {
        boolean match = false;
        if (pattern != null) {
            Matcher matcher;
            for (String value : values) {
                if (value != null) {
                    matcher = pattern.matcher(value.replaceAll(IGNORE_CHARACTERS, StringUtils.EMPTY));
                    if (matcher.matches()) {
                        match = true;
                        break;
                    }
                }
            }
        }
        return match;
    }

    public static DateTime stringToDateTime(String dateString, String dateFormat) {
        if (StringUtils.isNotBlank(dateString) && StringUtils.isNotBlank(dateFormat)) {
            return DateTime.parse(dateString, DateTimeFormat.forPattern(dateFormat));
        }
        return null;
    }

    /**
     * @param searchKey
     * @return regex string or null
     */
    private static String generateRegexString(String searchKey) {
        if (isSearchKeyValid(searchKey)) {
            //	Remove IGNORE_CHARACTERS
            searchKey = searchKey.replaceAll(IGNORE_CHARACTERS, StringUtils.EMPTY);

            //	Replace SPACE_CHARACTERS
            searchKey = searchKey.replaceAll(SPACE_CHARACTERS, " ");

            // Create a list of words for the regex
            List<String> searchStrings = new ArrayList<>(Arrays.asList(StringUtils.split(searchKey)));

            //Remove all the empty values from the list
            CollectionUtils.filter(searchStrings, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return StringUtils.isNotBlank((String) object);
                }
            });

            //	Create the regex string
            if (CollectionUtils.isNotEmpty(searchStrings)) {
                return buildRegexString(searchStrings);
            }
        }
        return null;
    }

    public static boolean isSearchKeyValid(String searchKey) {
        if (StringUtils.isNotBlank(searchKey)) {
            Pattern pattern = Pattern.compile(INVALID_CHARACTERS);
            Matcher matcher = pattern.matcher(searchKey);
            return !matcher.find();
        }
        return false;
    }

    /**
     * This method is refactored from generateRegexString(String searchKey) to not nest
     * more than 3 if/for/while/switch/try statements in calling method.
     * @param searchStrings
     * @return regex string
     */
    private static String buildRegexString(List<String> searchStrings) {
        StringBuilder regex = new StringBuilder();
        for (String criteria : searchStrings) {
            // Fixed the regex pattern to enable search for account names that contain '&'
            if ("&".equals(criteria)) {
                regex.append(String.format("(?=.*\\b*%s)", criteria));
            } else {
                regex.append(String.format("(?=.*\\b%s)", criteria));
            }
        }
        regex.append(".*");
        return regex.toString();
    }
}