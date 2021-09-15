package net.interview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

@Configuration
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private IpFilter ipFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(ipFilter, WebAsyncManagerIntegrationFilter.class);
    }
}
