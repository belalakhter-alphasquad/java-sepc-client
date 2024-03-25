package sepc.client;

import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.cdimascio.dotenv.Dotenv;
import sepc.client.DB.DbClient;

public class App {

    public static void main(String[] args) {
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        final Logger logger = LoggerFactory.getLogger(App.class);
        Dotenv dotenv = Dotenv.load();
        String DATABASE_NAME = dotenv.get("DB_NAME");
        String ADDRESS = dotenv.get("ADDRESS");
        String portPush = dotenv.get("PORT_PUSH");
        String subscription = dotenv.get("SUBSCRIPTION");
        String DBUser = dotenv.get("DB_USER");
        String DbURL = dotenv.get("DB_URL");
        String DbPass = dotenv.get("DB_PASS");

        DbClient dbClient = new DbClient(DATABASE_NAME, DbURL, DBUser, DbPass);
        logger.info("Opening new connection");
        int port = Integer.parseInt(portPush);
        logger.info(ADDRESS + port + subscription);

        new PushConnector(ADDRESS, port, subscription, DATABASE_NAME, DbURL, DBUser, DbPass);

        barrier.await();
        dbClient.close();

    }

}
