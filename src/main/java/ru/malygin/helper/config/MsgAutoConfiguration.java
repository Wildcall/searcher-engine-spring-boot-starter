package ru.malygin.helper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.malygin.helper.service.DefaultQueueDeclareService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class MsgAutoConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        log.info("[*] Create ObjectMapper in starter");
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }

    @Bean
    public boolean customizeRabbitTemplate(RabbitTemplate rabbitTemplate,
                                           Jackson2JsonMessageConverter converter) {
        log.info("[*] Customize RabbitTemplate in starter");
        rabbitTemplate.setMessageConverter(converter);
        return false;
    }

    @Bean
    @ConditionalOnMissingBean
    public SimplePropertyValueConnectionNameStrategy simplePropertyValueConnectionNameStrategy() {
        log.info("[*] Change connection name in starter");
        return new SimplePropertyValueConnectionNameStrategy("spring.application.name");
    }

    @Bean
    @Primary
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        log.info("[*] Create RabbitAdmin in starter");
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public Jackson2JsonMessageConverter jsonConverter(DefaultClassMapper classMapper,
                                                      ObjectMapper mapper) {
        log.info("[*] Create Jackson2JsonMessageConverter in starter");
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter(mapper);
        jsonConverter.setClassMapper(classMapper);
        return jsonConverter;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultClassMapper classMapper(Map<String, Class<?>> idClassMap) {
        log.info("[*] Create DefaultClassMapper in starter");
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setIdClassMapping(idClassMap);
        return classMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public Map<String, Class<?>> idClassMap() {
        log.info("[*] Create DefaultIdClassMap in starter");
        return new HashMap<>();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultQueueDeclareService defaultQueueDeclareService(RabbitAdmin rabbitAdmin,
                                                                 SearchEngineProperties properties) {
        log.info("[*] Create msgQueueDeclareFactory in starter");
        return new DefaultQueueDeclareService(rabbitAdmin, properties);
    }
}
