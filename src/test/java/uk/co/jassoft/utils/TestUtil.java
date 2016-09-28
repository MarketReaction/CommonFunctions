package uk.co.jassoft.utils;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.runtime.Network;
import org.apache.activemq.broker.BrokerService;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;

/**
 * Created by jonshaw on 29/02/2016.
 */
public class TestUtil {

    public static final String LOCALHOST = "127.0.0.1";

    public static MongodProcess startMongo(int port) throws IOException {
        Command command = Command.MongoD;

        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(command)
                .artifactStore(new ExtractedArtifactStoreBuilder()
                        .defaults(command)
                        .download(new DownloadConfigBuilder()
                                .defaultsForCommand(command).build())
                        .executableNaming(new UUIDTempNaming()))
                .build();

        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getInstance(runtimeConfig);

        MongodExecutable mongoExecutable = starter.prepare(mongodConfig);
        return mongoExecutable.start();
    }

    public static void stopMongo(MongodProcess mongoProcess) {
        mongoProcess.stop();
    }

    public static BrokerService startBroker(int port) throws Exception {
        BrokerService broker = new BrokerService();

        broker.addConnector("tcp://" + LOCALHOST + ":" + port);
        broker.setBrokerName(UUID.randomUUID().toString());

//        System.setProperty("ACTIVEMQ_PORT_61616_TCP_ADDR", LOCALHOST);
//        System.setProperty("ACTIVEMQ_PORT_61616_TCP_PORT", ACTIVEMQ_TEST_PORT + "");

        broker.setPersistent(false);

        broker.start();

        return broker;
    }

    public static void stopBroker(BrokerService brokerService) throws Exception {
        brokerService.stop();
    }

    public static int findFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        throw new IllegalStateException("Could not find a free TCP/IP port");
    }
}
