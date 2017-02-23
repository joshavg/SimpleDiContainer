package de.joshavg.simpledic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Properties;
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

    @Test
    public void testSingletonCheck() {
        Properties props = new Properties();
        props.put("service.a.singleton", "true");
        props.put("service.b.singleton", "false");

        assertThat(IntegrityCheck.isSingleton(props, "service.a"), is(true));
        assertThat(IntegrityCheck.isSingleton(props, "service.b"), is(false));
        assertThat(IntegrityCheck.isSingleton(props, "service.c"), is(false));
    }

}
