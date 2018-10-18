package custis.easyabac.boot;

import custis.easyabac.api.config.EnablePermissionCheckers;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnablePermissionCheckers("custis.easyabac.demo.permissionchecker")
public class EasyABACConfiguration {

}
