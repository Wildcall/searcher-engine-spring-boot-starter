package ru.malygin.helper;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.malygin.helper.config.SearchEngineProperties;

@ComponentScan
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SearchEngineProperties.class)
public class SearchEngineAutoConfiguration {

}
