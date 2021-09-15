package net.interview;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "ip.timeunit=SECONDS", "ip.timecount=1", "ip.limit=4" })
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
public class BadGateWayTest {

    private final static String IP = "191.158.1.38";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Response should return status 200 for rare requests and status 502 for frequent requests")
    public void runWebAppTest() throws Exception {
        //we have status 200 when limit is not exceeded
        for(var counter = 0; counter < 4; counter++) {
            this.mockMvc.perform(get("/").with(request -> {
                request.setRemoteAddr(IP);
                return request;
            })).andExpect(status().isOk())
                    .andExpect(content().bytes(new byte[0]));
        }
        //we have status 502 when limit is not exceeded
        this.mockMvc.perform(get("/").with(request -> {
            request.setRemoteAddr(IP);
            return request;
        })).andExpect(status().is(HttpStatus.BAD_GATEWAY.value()));
        //wait and again we have status 200 when limit is not exceeded
        Thread.sleep(1000);
        this.mockMvc.perform(get("/").with(request -> {
            request.setRemoteAddr(IP);
            return request;
        })).andExpect(status().isOk())
                .andExpect(content().bytes(new byte[0]));
    }
}
