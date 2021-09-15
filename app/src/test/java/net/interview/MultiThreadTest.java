package net.interview;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "ip.timeunit=SECONDS", "ip.timecount=1", "ip.limit=8" })
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
public class MultiThreadTest {

    private final static String IP1 = "192.158.1.38";
    private final static String IP2 = "193.158.1.38";

    private static volatile AtomicInteger IP1_200 = new AtomicInteger(0);
    private static volatile AtomicInteger IP1_502 = new AtomicInteger(0);
    private static volatile AtomicInteger IP2_200 = new AtomicInteger(0);
    private static volatile AtomicInteger IP2_502 = new AtomicInteger(0);

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Frequently requesting ip will get status 502, rarely requesting ip will get status 200")
    public void runWebAppTest() throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(30);
        for(var counter = 0; counter < 10; counter++) {
            service.submit(() -> {
                try {
                    Thread.sleep(400);
                    var status = this.mockMvc.perform(get("/").with(request -> {
                        request.setRemoteAddr(IP2);
                        return request;
                    })).andReturn().getResponse().getStatus();
                    System.out.println(status);
                    if(status == HttpStatus.OK.value()) {
                        IP2_200.incrementAndGet();
                    }
                    if(status == HttpStatus.BAD_GATEWAY.value()) {
                        IP2_502.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Handle exception
                }
                latch.countDown();
            });
        }
        for(var counter = 0; counter < 20; counter++) {
            service.submit(() -> {
                try {
                    var status = this.mockMvc.perform(get("/").with(request -> {
                        request.setRemoteAddr(IP1);
                        return request;
                    })).andReturn().getResponse().getStatus();
                    if(status == HttpStatus.OK.value()) {
                        IP1_200.incrementAndGet();
                    }
                    if(status == HttpStatus.BAD_GATEWAY.value()) {
                        IP1_502.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Handle exception
                }
                latch.countDown();
            });
        }
        latch.await();
        assertTrue(IP1_502.get() > 0);
        assertTrue(IP1_200.get() > 0);
        assertEquals(0, IP2_502.get());
        assertEquals(10, IP2_200.get());
    }

}
