package uk.co.jassoft.utils;

import com.mongodb.MongoClient;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;

import java.io.IOException;

/**
 * Created by jonshaw on 19/08/15.
 */
public class BaseRepositoryTest extends BaseTest {

    private static MongoClient mongo;

    private MongoTemplate template;
    private MongoRepositoryFactory mongoRepositoryFactory;

    @BeforeClass
    public static void initializeDB() throws IOException {

        mongo = new MongoClient(TestUtil.LOCALHOST, mongoPort);
        mongo.getDB(DB_NAME);
    }

    @AfterClass
    public static void shutdownDB() throws InterruptedException {
        mongo.close();
    }

    @Before
    public void setUp() throws Exception {

        template = new MongoTemplate(mongo, DB_NAME);
        mongoRepositoryFactory = new MongoRepositoryFactory(getTemplate());
    }

    public MongoTemplate getTemplate() {
        return template;
    }

    public MongoRepositoryFactory getMongoRepositoryFactory() {
        return mongoRepositoryFactory;
    }
}
