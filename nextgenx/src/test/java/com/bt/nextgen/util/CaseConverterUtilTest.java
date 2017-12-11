package com.bt.nextgen.util;

import com.bt.nextgen.core.api.model.BaseDto;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CaseConverterUtilTest {

    public static final String LOWER_CASE_FIELD = "lower case field";
    public static final String UPPER_CASE_FIELD = "UPPER CASE FIELD";
    public static final String TITLE_CASE_FIELD = "Title Case Field";
    ChildClassDto childClass;
    AnAssociatedClassDto anAssociatedClass;

    @Before
    public void setup() {

        childClass = new ChildClassDto();
        childClass.setChildFieldWithLowerCase(LOWER_CASE_FIELD);
        childClass.setChildFieldWithUpperCase(UPPER_CASE_FIELD);
        childClass.setChildFieldWithTitleCase(TITLE_CASE_FIELD);

        childClass.setSuperFieldWithLowerCase(LOWER_CASE_FIELD);
        childClass.setSuperFieldWithUpperCase(UPPER_CASE_FIELD);

        anAssociatedClass = new AnAssociatedClassDto();
        anAssociatedClass.setAssociatedFieldWithLowerCase(LOWER_CASE_FIELD);
        anAssociatedClass.setAssociatedFieldWithUpperCase(UPPER_CASE_FIELD);

        childClass.setAnAssociatedClass(anAssociatedClass);

        AnAssociatedClassDto associatedInList = new AnAssociatedClassDto();
        associatedInList.setAssociatedFieldWithLowerCase(LOWER_CASE_FIELD);
        associatedInList.setAssociatedFieldWithUpperCase(UPPER_CASE_FIELD);
        childClass.setAssociatedClassDtoList(Arrays.asList(associatedInList));
    }

    private class SuperClassDto extends BaseDto {

        String superFieldWithUpperCase;
        String superFieldWithLowerCase;

        public String getSuperFieldWithUpperCase() {
            return superFieldWithUpperCase;
        }

        public void setSuperFieldWithUpperCase(String superFieldWithUpperCase) {
            this.superFieldWithUpperCase = superFieldWithUpperCase;
        }

        public String getSuperFieldWithLowerCase() {
            return superFieldWithLowerCase;
        }

        public void setSuperFieldWithLowerCase(String superFieldWithLowerCase) {
            this.superFieldWithLowerCase = superFieldWithLowerCase;
        }
    }

    private class ChildClassDto extends SuperClassDto {

        String childFieldWithUpperCase;
        String childFieldWithLowerCase;
        String childFieldWithTitleCase;
        String excludedFieldForConversion;
        AnAssociatedClassDto anAssociatedClass;
        List<AnAssociatedClassDto> associatedClassDtoList;

        public String getChildFieldWithUpperCase() {
            return childFieldWithUpperCase;
        }

        public void setChildFieldWithUpperCase(String childFieldWithUpperCase) {
            this.childFieldWithUpperCase = childFieldWithUpperCase;
        }

        public String getChildFieldWithLowerCase() {
            return childFieldWithLowerCase;
        }

        public void setChildFieldWithLowerCase(String childFieldWithLowerCase) {
            this.childFieldWithLowerCase = childFieldWithLowerCase;
        }

        public String getChildFieldWithTitleCase() {
            return childFieldWithTitleCase;
        }

        public void setChildFieldWithTitleCase(String childFieldWithTitleCase) {
            this.childFieldWithTitleCase = childFieldWithTitleCase;
        }

        public AnAssociatedClassDto getAnAssociatedClass() {
            return anAssociatedClass;
        }

        public void setAnAssociatedClass(AnAssociatedClassDto anAssociatedClass) {
            this.anAssociatedClass = anAssociatedClass;
        }

        public List<AnAssociatedClassDto> getAssociatedClassDtoList() {
            return associatedClassDtoList;
        }

        public void setAssociatedClassDtoList(List<AnAssociatedClassDto> associatedClassDtoList) {
            this.associatedClassDtoList = associatedClassDtoList;
        }

        public String getExcludedFieldForConversion() {
            return excludedFieldForConversion;
        }

        public void setExcludedFieldForConversion(String excludedFieldForConversion) {
            this.excludedFieldForConversion = excludedFieldForConversion;
        }
    }

    private class AnAssociatedClassDto extends BaseDto {

        String associatedFieldWithUpperCase;
        String associatedFieldWithLowerCase;

        public String getAssociatedFieldWithUpperCase() {
            return associatedFieldWithUpperCase;
        }

        public void setAssociatedFieldWithUpperCase(String associatedFieldWithUpperCase) {
            this.associatedFieldWithUpperCase = associatedFieldWithUpperCase;
        }

        public String getAssociatedFieldWithLowerCase() {
            return associatedFieldWithLowerCase;
        }

        public void setAssociatedFieldWithLowerCase(String associatedFieldWithLowerCase) {
            this.associatedFieldWithLowerCase = associatedFieldWithLowerCase;
        }
    }

    @Test
    public void convertToUpperCaseShouldConvertFieldsToUpperCase() {
       CaseConverterUtil.convertToUpperCase(childClass);
       assertThat(childClass.getChildFieldWithLowerCase(), is(LOWER_CASE_FIELD.toUpperCase()));
       assertThat(childClass.getChildFieldWithTitleCase(), is(TITLE_CASE_FIELD.toUpperCase()));
    }

    @Test
    public void convertToUpperCaseShouldConvertInheritedFieldsToUpperCase() {
        CaseConverterUtil.convertToUpperCase(childClass);
        assertThat(childClass.getSuperFieldWithLowerCase(), is(LOWER_CASE_FIELD.toUpperCase()));
    }

    @Test
    public void convertToUpperCaseShouldConvertFieldsToLower() {
       CaseConverterUtil.convertToLowerCase(childClass);
       assertThat(childClass.getChildFieldWithUpperCase(), is(UPPER_CASE_FIELD.toLowerCase()));
    }

    @Test
    public void convertToUpperCaseShouldConvertInheritedFieldsToLowerCase() {
        CaseConverterUtil.convertToLowerCase(childClass);
        assertThat(childClass.getSuperFieldWithUpperCase(), is(UPPER_CASE_FIELD.toLowerCase()));
    }

    @Test
    public void safeConvertToUpperCaseShouldNotConvertTitleCaseStrings() {
        CaseConverterUtil.safeConvertToUpperCase(childClass);
        assertThat(childClass.getChildFieldWithLowerCase(), is(LOWER_CASE_FIELD.toUpperCase()));
        assertThat(childClass.getChildFieldWithTitleCase(), is(TITLE_CASE_FIELD));
    }

    @Test
    public void safeConvertToLowerCaseShouldNotConvertTitleCaseStrings() {
        CaseConverterUtil.safeConvertToLowerCase(childClass);
        assertThat(childClass.getChildFieldWithUpperCase(), is(UPPER_CASE_FIELD.toLowerCase()));
        assertThat(childClass.getChildFieldWithTitleCase(), is(TITLE_CASE_FIELD));
    }

    @Test
    public void allAssociatedDtoObjectOfThisClassShouldAlsoBeConverted() {
        CaseConverterUtil.safeConvertToLowerCase(childClass);
        assertThat(childClass.getAnAssociatedClass().associatedFieldWithUpperCase, is(UPPER_CASE_FIELD.toLowerCase()));
        CaseConverterUtil.safeConvertToUpperCase(childClass);
        assertThat(childClass.getAnAssociatedClass().associatedFieldWithLowerCase, is(LOWER_CASE_FIELD.toUpperCase()));
    }

    @Test
    public void objectsInListsShouldAlsoBeConverted() {
        CaseConverterUtil.convertToUpperCase(childClass);
        assertThat(childClass.getAssociatedClassDtoList().get(0).associatedFieldWithLowerCase, is(LOWER_CASE_FIELD.toUpperCase()));
        CaseConverterUtil.convertToLowerCase(childClass);
        assertThat(childClass.getAssociatedClassDtoList().get(0).associatedFieldWithUpperCase, is(UPPER_CASE_FIELD.toLowerCase()));
    }

    @Test
    public void excludedPropertiesShouldNotBeConverted() {
        childClass.setExcludedFieldForConversion("excluded field");
        CaseConverterUtil.convertToUpperCase(childClass,"excludedFieldForConversion");
        assertThat(childClass.getExcludedFieldForConversion(), is("excluded field"));
        assertThat(childClass.getChildFieldWithLowerCase(), is(LOWER_CASE_FIELD.toUpperCase()));
        childClass.setExcludedFieldForConversion("EXCLUDED FIELD");
        CaseConverterUtil.convertToLowerCase(childClass,"excludedFieldForConversion");
        assertThat(childClass.getExcludedFieldForConversion(), is("EXCLUDED FIELD"));
        assertThat(childClass.getChildFieldWithUpperCase(), is(UPPER_CASE_FIELD.toLowerCase()));
    }

    @Test
    public void convertToTitleCaseShouldConvertAllTheFieldsToTitleCase() {
        CaseConverterUtil.convertToTitleCase(childClass);
        assertThat(childClass.getChildFieldWithUpperCase(), is("Upper Case Field"));
        assertThat(childClass.getChildFieldWithLowerCase(), is("Lower Case Field"));
        assertThat(childClass.getAnAssociatedClass().associatedFieldWithUpperCase, is("Upper Case Field"));
    }

    @Test
    public void convertToTitleCaseShouldNotConvertExcludedProperties() {
        childClass.setExcludedFieldForConversion("excluded field");
        CaseConverterUtil.convertToTitleCase(childClass,"excludedFieldForConversion");
        assertThat(childClass.getChildFieldWithUpperCase(), is("Upper Case Field"));
        assertThat(childClass.getChildFieldWithLowerCase(), is("Lower Case Field"));
        assertThat(childClass.getExcludedFieldForConversion(), is("excluded field"));
        assertThat(childClass.getAnAssociatedClass().associatedFieldWithUpperCase, is("Upper Case Field"));
    }
}
