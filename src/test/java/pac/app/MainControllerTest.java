package pac.app;

import io.micronaut.http.client.RxStreamingHttpClient;
import io.micronaut.http.client.annotation.Client;

import org.junit.jupiter.api.Test;

import javax.inject.Inject;




public class MainControllerTest {

    @Inject
    @Client("/event")
    RxStreamingHttpClient client;

    @Test
    public void getEventsTest() {
    }

    @Test
    public void saveEventTest() {
    }
}