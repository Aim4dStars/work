package com.bt.nextgen.core.api.validation;

import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;

import org.junit.Test;

import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.exception.NotFoundException;

public class ApiValidationTest {

	private TestKey key;

	@Test
	public void preConditionCompleteKey() {
		key = new TestKey("name", "value", 1);
		ApiValidation.preConditionCompleteKey(CURRENT_VERSION, key);
	}

	@Test(expected = BadRequestException.class)
	public void preConditionCompleteKeySomeNullAttributes() {
		key = new TestKey("clientId", "pfolioId");
		ApiValidation.preConditionCompleteKey(CURRENT_VERSION, key);
	}

	@Test
	public void preConditionPartialKey() {
		key = new TestKey("clientId", "pfolioId");
		ApiValidation.preConditionPartialKey(CURRENT_VERSION, key);
	}

	@Test(expected = BadRequestException.class)
	public void preConditionPartialKeyAllNullKeyAttributes() {
		key = new TestKey(null, null);
		ApiValidation.preConditionPartialKey(CURRENT_VERSION, key);
	}

	@Test
	public void postConditionCompleteKey() {
		key = new TestKey("name", "value", 1);
		ApiValidation.postConditionCompleteKey(CURRENT_VERSION, key);
	}

	@Test(expected = NotFoundException.class)
	public void postConditionCompleteKeySomeNullAttributes() {
		key = new TestKey("clientId", "pfolioId");
		ApiValidation.postConditionCompleteKey(CURRENT_VERSION, key);
	}

	public static class TestKey {

		private final String name;

		private final String value;

		private final Integer version;

		public TestKey(String name, String value, Integer version) {
			this.name = name;
			this.value = value;
			this.version = version;
		}

		public TestKey(String name, String value) {
			this(name, value, null);
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public Integer getVersion() {
			return version;
		}
	}
}
