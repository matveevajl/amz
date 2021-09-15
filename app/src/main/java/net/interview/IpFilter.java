package net.interview;

import lombok.RequiredArgsConstructor;
import net.interview.service.IpService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class IpFilter extends OncePerRequestFilter {

    private final IpService ipService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var remoteAddr = request.getRemoteAddr();
        ipService.save(remoteAddr);
        if(ipService.isFrequent(remoteAddr)) {
            response.setStatus(HttpStatus.BAD_GATEWAY.value());
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication() {
        return new AnonymousAuthenticationToken("key", "test", AuthorityUtils.createAuthorityList("ROLE_ALLOWED"));
    }
}
