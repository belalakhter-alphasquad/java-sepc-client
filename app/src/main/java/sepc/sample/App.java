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
            dbClient.runSqlFileToCreateTables();
            System.out.println("Database setup successfully.");
        } catch (Exception e) {
            e.printStackTrace();
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
