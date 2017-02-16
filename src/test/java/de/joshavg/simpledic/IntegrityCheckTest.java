package de.joshavg.simpledic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class IntegrityCheckTest {

    @Test
    public void servicesStartWithServiceDot() {
        assertThat(IntegrityCheck.isServiceName("dingens"), is(false));
        assertThat(IntegrityCheck.isServiceName("service"), is(false));
        assertThat(IntegrityCheck.isServiceName("service."), is(false));
        assertThat(IntegrityCheck.isServiceName("service.aad-s"), is(true));
        assertThat(IntegrityCheck.isServiceName("service.a.singleton"), is(false));
    }

}
