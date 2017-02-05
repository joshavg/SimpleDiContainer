package de.joshavg.simpledic;

import org.junit.Test;

public class TestProperties {

    @Test
    public void testIntegrityCheck() {
        SdiContainer.load("test.properties");
    }
}
