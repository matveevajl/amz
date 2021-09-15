package net.interview.service;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

@Service
public class AnyService {

    @Secured("ROLE_ALLOWED")
    public void anyMethod() {
        //empty
    }
}
