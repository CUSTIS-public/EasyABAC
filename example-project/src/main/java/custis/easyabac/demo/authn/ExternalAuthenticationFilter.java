package custis.easyabac.demo.authn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExternalAuthenticationFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(ExternalAuthenticationFilter.class);

    public static final String EMPLOYEE_ID_HEADER = "EMPLOYEE_ID";

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;

        String userId = req.getParameter(EMPLOYEE_ID_HEADER);
        if (userId != null) {
            AuthenticationContext.setup(userId);
        } else {
            logger.error("Not authenticated");
        }

        try {
            chain.doFilter(request, response);
        } finally {
            AuthenticationContext.cleanup();
        }
    }

    @Override
    public void destroy() {

    }

}

