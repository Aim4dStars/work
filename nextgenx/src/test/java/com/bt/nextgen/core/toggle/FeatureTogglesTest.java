package com.bt.nextgen.core.toggle;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FeatureTogglesTest {

    private FeatureToggles toggles;

    @Before
    public void initFeatureToggles() {
        toggles = new FeatureToggles();
        toggles.setFeatureToggle("projectOne", true);
        toggles.setFeatureToggle("projectTwo", false);
        toggles.setFeatureToggle("projectThree", false);
    }

    @Test
    public void getFeatureToggleForNonExistentToggle() {
        assertFalse(toggles.getFeatureToggle("missingProject"));
    }

    @Test
    public void getFeatureToggleForFlaggedToggle() {
        assertTrue(toggles.getFeatureToggle("projectOne"));
    }

    @Test
    public void getFeatureToggleForUnFlaggedToggle() {
        assertFalse(toggles.getFeatureToggle("projectTwo"));
    }

    @Test
    public void getToggleNames() {
        assertThat(toggles.getToggleNames(), containsInAnyOrder("projectOne", "projectTwo", "projectThree"));
    }

    @Test
    public void getMap() {
        Map<String, Boolean> map = toggles.getMap();
        assertThat(map.keySet(), containsInAnyOrder("projectOne", "projectTwo", "projectThree"));
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            switch (entry.getKey()) {
                case "projectOne":
                    assertTrue(entry.getValue());
                    break;
                case "projectTwo":
                case "projectThree":
                    assertFalse(entry.getValue());
                    break;
                default:
                    fail("Unexpected key: " + entry.getKey());
                    break;
            }
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getMapIsUnmodifiable() {
        toggles.getMap().put("projectFour", false);
    }

    @Test
    public void serializable() throws Exception {
        toggles = writeAndRead(toggles);
        getToggleNames();
        getFeatureToggleForFlaggedToggle();
        getFeatureToggleForUnFlaggedToggle();
    }

    @SuppressWarnings("unchecked")
    public static <T> T writeAndRead(final T instance) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutput oo = new ObjectOutputStream(baos);
        oo.writeObject(instance);
        oo.close();

        final ObjectInput oi = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        final T read = (T) oi.readObject();
        oi.close();
        return read;
    }
}
