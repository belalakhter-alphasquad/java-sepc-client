package sepc.sample;

import sepc.sample.DB.DbClient;

import sepc.sample.utils.EnvLoader;

import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(App.class);

        EnvLoader.load(".env");
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();

        DbClient dbClient = new DbClient();
        try {
            // make sure to provide sql server credentials
            dbClient.runSqlFileToCreateTables();
            System.out.println("Database setup successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        String hostname = System.getProperty("HOSTNAME");
        int portPush = Integer.parseInt(System.getProperty("PORT_PUSH"));
        int portPull = Integer.parseInt(System.getProperty("PORT_PULL"));
        Long timeout = 3000000L;
        String subscription = System.getProperty("SUBSCRIPTION");
        System.out.println("Openening new connection");

        // Uncomment which connector type you want to use
        new PushConnector(hostname, portPush, subscription);
        barrier.await();
        dbClient.close();

        // new PullConnector(hostname, port2, subscription, timeout);

    }

}
