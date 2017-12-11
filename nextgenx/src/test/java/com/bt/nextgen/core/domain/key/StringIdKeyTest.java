package com.bt.nextgen.core.domain.key;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StringIdKeyTest {

	private StringIdKey key;

	@Before
	public void initKey() {
		key = new StringIdKey("test-id");
	}

	@Test
	public void verifyToString() {
		assertEquals("StringIdKey{id:test-id}", key.toString());
	}

	@Test
	public void equalsWithNullReference() {
		assertFalse(key.equals(null));
	}

	@Test
	public void equalsWithIncompatibleObject() {
		assertFalse(key.equals(new LongIdKey(0L)));
	}

	@Test
	public void equalsWithUnequalKey() {
		assertFalse(key.equals(new StringIdKey("not-the-same")));
	}

	@Test
	public void equalsWithSameKey() {
		assertEquals(key, key);
	}

	@Test
	public void equalsWithNullKey() {
		assertFalse(key.equals(new StringIdKey()));
	}

	@Test
	public void equalsWithMyKeyNull() {
		key.setId(null);
		assertFalse(key.equals(new StringIdKey("not-null")));
		assertEquals(key, new StringIdKey());
	}

	@Test
	public void equalsAndHashCodeWithEqualKey() {
		final StringIdKey key2 = new StringIdKey("test-id");
		assertEquals(key, key2);
		assertEquals(key.hashCode(), key2.hashCode());
	}

	@Test
	public void equalsWithClone() throws CloneNotSupportedException {
		assertEquals(key, key.clone());
	}

	@Test
	public void twoKeysWithNullIdShouldBeEqualAndHaveSameHashCode() {
		key.setId(null);
		StringIdKey key2 = new StringIdKey();
		assertEquals(key, key2);
		assertEquals(key.hashCode(), key2.hashCode());
	}
}
