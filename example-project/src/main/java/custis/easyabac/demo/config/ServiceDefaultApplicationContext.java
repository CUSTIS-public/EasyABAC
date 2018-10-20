package custis.easyabac.demo.config;

import custis.easyabac.api.EntityPermissionChecker;
import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.pdp.AuthService;
import custis.easyabac.demo.authn.AuthenticationContext;
import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.OrderAction;
import custis.easyabac.demo.service.UserService;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.starter.EasyAbacDebugBuilderHelper;
import custis.easyabac.starter.EntityPermissionCheckerHelper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.io.InputStream;

@SpringBootConfiguration
@EnableWebMvc
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories("custis.easyabac.demo.repository")
@EnableConfigurationProperties
@EnableAutoConfiguration
public class ServiceDefaultApplicationContext {

    @Bean
    @ConfigurationProperties(prefix = "joker-db")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public AuthService authService(UserService userService) throws EasyAbacInitException {
        InputStream modelStream = getClass().getResourceAsStream("/policy.yaml");
        EasyAbac easyAbac = EasyAbacDebugBuilderHelper.defaultDebugBuilder(
                modelStream,
                () -> userService.findById(AuthenticationContext.currentUserId()))
                .build();
        return easyAbac;
    }

    @Bean
    public EntityPermissionChecker<Order, OrderAction> entityPermissionChecker(AuthService authService) {
        return EntityPermissionCheckerHelper.newPermissionChecker(authService);
    }

}
