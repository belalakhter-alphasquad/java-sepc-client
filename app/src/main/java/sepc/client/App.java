package sepc.client;

import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sepc.client.DB.DbClient;
import sepc.client.utils.EnvLoader;

public class App {

    public static void main(String[] args) {

        final Logger logger = LoggerFactory.getLogger(App.class);
        logger.info("its working");

        EnvLoader.load(".env");
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();

        DbClient dbClient = new DbClient();

        String hostname = System.getProperty("HOSTNAME");
        int portPush = Integer.parseInt(System.getProperty("PORT_PUSH"));
        String subscription = System.getProperty("SUBSCRIPTION");
        System.out.println("Openening new connection");

        new PushConnector(hostname, portPush, subscription);

        barrier.await();
        dbClient.close();

    }

}
