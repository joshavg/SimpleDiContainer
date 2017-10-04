package de.joshavg.simpledic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class IntegrityCheckTest {

    @Test
    public void servicesStartWithServiceDot() {
        assertThat(PropertiesAnalyzer.isServiceName("dingens"), is(false));
        assertThat(PropertiesAnalyzer.isServiceName("service"), is(false));
        assertThat(PropertiesAnalyzer.isServiceName("service."), is(false));
        assertThat(PropertiesAnalyzer.isServiceName("service.aad-s"), is(true));
        assertThat(PropertiesAnalyzer.isServiceName("service.a.singleton"), is(false));
    }

}
