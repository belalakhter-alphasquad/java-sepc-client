package sepc.client;

import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sepc.client.DB.DbClient;
import io.github.cdimascio.dotenv.Dotenv;

public class App {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .load();

        final Logger logger = LoggerFactory.getLogger(App.class);
        String dbUrl = dotenv.get("DB_URL");
        String dbName = dotenv.get("DB_NAME");
        String dbUser = dotenv.get("DB_USER");
        String dbPass = dotenv.get("DB_PASS");

        String hostname = dotenv.get("HOSTNAME");
        String portPush = dotenv.get("PORT_PUSH");
        String subscription = dotenv.get("SUBSCRIPTION");

        logger.info("Opening new connection");

        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();

        DbClient dbClient = new DbClient(dbName, dbUrl, dbUser, dbPass);

        new PushConnector(hostname, Integer.parseInt(portPush), subscription, dbName, dbUrl, dbUser, dbPass);

        barrier.await();

    }

}
