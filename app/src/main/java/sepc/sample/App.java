package sepc.sample;

import sepc.sample.DB.DbClient;
import sepc.sample.utils.EnvLoader;

import org.agrona.concurrent.ShutdownSignalBarrier;

public class App {
    public static void main(String[] args) {
        EnvLoader.load(".env");
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        // Credentials are hardcoded physically each time env will be added soon
        DbClient dbClient = new DbClient();
        try {
            // make sure to provide sql server credentials
            dbClient.runSqlFileToCreateTables();
            System.out.println("Database setup successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Hardocded
        String hostname = System.getProperty("HOSTNAME");
        int portPush = Integer.parseInt(System.getProperty("PORT_PUSH"));
        int portPull = Integer.parseInt(System.getProperty("PORT_PULL"));
        Long timeout = 3000000L;
        System.out.println(subscription);
        System.out.println("Openening new connection");
        // Uncomment which connector type you want to use
        new PushConnector(hostname, portPush, subscription);
        barrier.await();
        dbClient.close();
        // new PullConnector(hostname, port2, subscription, timeout);

    }

}
