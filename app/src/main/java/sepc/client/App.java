package sepc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.cdimascio.dotenv.Dotenv;
import sepc.client.DB.DbClient;

public class App {

    public static void main(String[] args) {

        final Logger logger = LoggerFactory.getLogger(App.class);
        Dotenv dotenv = Dotenv.configure()
                .directory("./.env")
                .load();
        String DATABASE_NAME = dotenv.get("DB_NAME");
        String Monkey_TILT = dotenv.get("Monkey_TILT");
        String portPush = dotenv.get("PORT_PUSH");
        String subscription = dotenv.get("SUBSCRIPTION");
        String DBUser = dotenv.get("DB_USER");
        String DbURL = dotenv.get("DB_URL");
        String DbPass = dotenv.get("DB_PASS");

        DbClient dbClient = new DbClient(DATABASE_NAME, DbURL, DBUser, DbPass);
        logger.info("Opening new connection");
        int port = Integer.parseInt(portPush);

        new PushConnector(Monkey_TILT, port, subscription, DATABASE_NAME, DbURL, DBUser, DbPass, dbClient);

    }

}
