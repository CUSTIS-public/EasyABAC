package main.java.custis.easyabac.boot;

import custis.easyabac.api.config.EnablePermissionCheckers;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnablePermissionCheckers("custis.easyabac.demo.permissionchecker")
@ComponentScan("custis.easyabac.boot.bean")
public class EasyABACConfiguration {

}
