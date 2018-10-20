package custis.easyabac.demo.config;

import custis.easyabac.demo.authn.ExternalAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

@Configuration
public class AuthenticationConfiguration {

    @Bean
    public FilterRegistrationBean authenticationContextFilterRegistration() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new ExternalAuthenticationFilter());
        filterRegistrationBean.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        filterRegistrationBean.addUrlPatterns(new String[]{"/*"});
        return filterRegistrationBean;
    }
}
