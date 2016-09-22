package com.jassoft.markets;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by jonshaw on 13/07/15.
 */
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
@Import({SpringMongoConfig.class, SpringActiveMQConfig.class})
@PropertySource("classpath:application.properties")
public class BaseSpringConfiguration {

}
