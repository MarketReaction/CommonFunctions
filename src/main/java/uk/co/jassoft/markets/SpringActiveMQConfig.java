package uk.co.jassoft.markets;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.ConnectionFactory;

/**
 * Created by jonshaw on 16/09/15.
 */
@EnableJms
public class SpringActiveMQConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SpringActiveMQConfig.class);

    @Value("${ACTIVEMQ_PORT_61616_TCP_ADDR:activemq}")
    private String activeMQHost;

    @Value("${ACTIVEMQ_PORT_61616_TCP_PORT:61616}")
    private int activeMQPort;

    @Value("${ACTIVEMQ_PREFETCH:5}")
    private int activeMQPreFetch;

    private static PooledConnectionFactory pooledConnectionFactory;

    @Bean
    ConnectionFactory connectionFactory() {
        if(pooledConnectionFactory == null) {
            LOG.info("ActiveMQ client connecting to Host [{}] Port [{}]", activeMQHost, activeMQPort);

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(String.format("failover:(tcp://%s:%s)" +
                    "?jms.prefetchPolicy.all=%s" +
                    "&jms.redeliveryPolicy.maximumRedeliveries=1", activeMQHost, activeMQPort, activeMQPreFetch));

            pooledConnectionFactory = new PooledConnectionFactory(connectionFactory);
        }

        return pooledConnectionFactory;
    }
}
