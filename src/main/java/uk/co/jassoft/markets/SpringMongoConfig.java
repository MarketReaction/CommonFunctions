package uk.co.jassoft.markets;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;

/**
 * Created by jonshaw on 16/09/15.
 */
@EnableMongoRepositories("uk.co.jassoft.markets.repository")
public class SpringMongoConfig extends AbstractMongoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(SpringMongoConfig.class);

    @Value("${MONGO_PORT_27017_TCP_ADDR:mongo}")
    private String mongoDbHost;

    @Value("${MONGO_PORT_27017_TCP_PORT:27017}")
    private int mongoDbPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDbDatabase;

    private static Mongo mongo;

    @Override
    protected String getDatabaseName() {
        return mongoDbDatabase;
    }

    @Override
    public Mongo mongo() throws Exception {
        if(mongo == null) {
            LOG.info("MongoDB client connecting to Host [{}] Port [{}]", mongoDbHost, mongoDbPort);

            final MongoClientOptions options = MongoClientOptions.builder()
                    .socketTimeout(1000 * 60 * 10) // 10 Mins
                    .socketKeepAlive(true)
                    .build();

            mongo = new MongoClient(new ServerAddress(mongoDbHost, mongoDbPort), options);
        }

        return mongo;
    }

    @Override
    protected String getMappingBasePackage() {
        return "uk.co.jassoft.markets.repository";
    }

    @Bean
    public MongoRepositoryFactory mongoRepositoryFactory() throws Exception {
        return new MongoRepositoryFactory(mongoTemplate());
    }
}
