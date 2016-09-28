package uk.co.jassoft.utils;

import de.flapdoodle.embed.mongo.MongodProcess;
import org.apache.activemq.broker.BrokerService;
import org.junit.BeforeClass;

/**
 * Created by jonshaw on 19/08/15.
 */
public class BaseTest {

    public static final String DB_NAME = "itest";

    protected static int mongoPort;
    protected static int brokerPort;

    @BeforeClass
    public static void setup() throws Exception {

        mongoPort = TestUtil.findFreePort();

        MongodProcess mongoProcess = TestUtil.startMongo(mongoPort);

        System.setProperty("MONGO_PORT_27017_TCP_ADDR", TestUtil.LOCALHOST);
        System.setProperty("MONGO_PORT_27017_TCP_PORT", mongoPort + "");

        brokerPort = TestUtil.findFreePort();

        BrokerService brokerService = TestUtil.startBroker(brokerPort);

        brokerService.getBroker();

        System.setProperty("ACTIVEMQ_PORT_61616_TCP_ADDR", TestUtil.LOCALHOST);
        System.setProperty("ACTIVEMQ_PORT_61616_TCP_PORT", brokerPort + "");
    }

}
