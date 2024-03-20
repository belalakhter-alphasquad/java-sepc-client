package sepc.sample;

import sepc.sample.DB.DbClient;

import sepc.sample.utils.EnvLoader;

import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    public static void main(String[] args) {
        DbClient dbClient = null;
        final Logger logger = LoggerFactory.getLogger(App.class);

        EnvLoader.load(".env");
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();

        for (int i = 0; i < 6; i++) {
            dbClient = new DbClient();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                logger.error("Exception while retrying db connection", e);
            }
        }
        String hostname = System.getProperty("HOSTNAME");
        int portPush = Integer.parseInt(System.getProperty("PORT_PUSH"));
        String subscription = System.getProperty("SUBSCRIPTION");
        System.out.println("Openening new connection");

        new PushConnector(hostname, portPush, subscription);
        barrier.await();
        dbClient.close();

    }

}
