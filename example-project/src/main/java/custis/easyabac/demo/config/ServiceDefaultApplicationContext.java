package custis.easyabac.demo.config;

import custis.easyabac.boot.EasyABACConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@SpringBootConfiguration
@EnableWebMvc
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories("custis.easyabac.demo.repository")
@EnableConfigurationProperties
@EnableAutoConfiguration
@Import(EasyABACConfiguration.class)
public class ServiceDefaultApplicationContext {

    @Bean
    @ConfigurationProperties(prefix = "joker-db")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

}
