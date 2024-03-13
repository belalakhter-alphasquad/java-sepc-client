package sepc.sample;

import sepc.sample.DB.DbClient;

import sepc.sample.utils.EnvLoader;
import java.sql.SQLException;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(App.class);

        EnvLoader.load(".env");
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();

        DbClient dbClient = new DbClient();
        try {
            dbClient.runSqlFileToCreateTables();
            if (testBatchInsertion(dbClient)) {
                System.out.println("Batch Insertion Successful");

            }
            System.out.println("Database setup successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // String hostname = System.getProperty("HOSTNAME");
        // int portPush = Integer.parseInt(System.getProperty("PORT_PUSH"));
        // int portPull = Integer.parseInt(System.getProperty("PORT_PULL"));
        // Long timeout = 3000000L;
        // String subscription = System.getProperty("SUBSCRIPTION");
        // System.out.println("Openening new connection");

        // Uncomment which connector type you want to use
        // new PushConnector(hostname, portPush, subscription);
        barrier.await();
        dbClient.close();

        // new PullConnector(hostname, port2, subscription, timeout);

    }

    public static boolean testBatchInsertion(DbClient dbClient) {
        String tableName = "sport";
        List<String> fields = Arrays.asList("id", "version", "name", "description", "parentId");

        List<List<Object>> batchFieldValues = Arrays.asList(
                Arrays.asList(1L, 1, "Football", "Outdoor game", null),
                Arrays.asList(2L, 1, "Basketball", "Indoor game", null),
                Arrays.asList(3L, 1, "Soccer", "World's favorite sport", null),
                Arrays.asList(4L, 1, "Tennis", "Individual or doubles game", null),
                Arrays.asList(5L, 1, "Golf", "Relaxing outdoor sport", null),
                Arrays.asList(6L, 1, "Volleyball", "Fun team sport", null),
                Arrays.asList(7L, 1, "Baseball", "Popular in the Americas", null),
                Arrays.asList(8L, 1, "Cricket", "Bat and ball game", null),
                Arrays.asList(9L, 1, "Hockey", "Fast-paced ice or field sport", null),
                Arrays.asList(10L, 1, "Swimming", "Individual or team aquatic sport", null),
                Arrays.asList(11L, 1, "Table Tennis", "Indoor racket sport", null),
                Arrays.asList(12L, 1, "Badminton", "Fast-paced racket sport", null),
                Arrays.asList(13L, 1, "Cycling", "Great for fitness", null),
                Arrays.asList(14L, 1, "Running", "Simple and effective exercise", null),
                Arrays.asList(15L, 1, "Boxing", "Combat sport", null),
                Arrays.asList(16L, 1, "Martial Arts", "Various disciplines", null),
                Arrays.asList(17L, 1, "Wrestling", "Traditional sport", null),
                Arrays.asList(18L, 1, "Softball", "Similar to baseball", null),
                Arrays.asList(19L, 1, "Skiing", "Winter sport", null),
                Arrays.asList(20L, 1, "Snowboarding", "Popular in snowy regions", null),
                Arrays.asList(21L, 1, "Surfing", "Riding the waves", null),
                Arrays.asList(22L, 1, "Skateboarding", "Street or park sport", null),
                Arrays.asList(23L, 1, "Gymnastics", "Graceful and skillful", null),
                Arrays.asList(24L, 1, "Diving", "Athletic water discipline", null),
                Arrays.asList(25L, 1, "Archery", "Precision sport", null),
                Arrays.asList(26L, 1, "Fencing", "Strategic swordplay", null),
                Arrays.asList(27L, 1, "Softball", "Bat and ball game", null),
                Arrays.asList(28L, 1, "Climbing", "Indoor or outdoor activity", null),
                Arrays.asList(29L, 1, "Horse Riding", "Equestrian sport", null),
                Arrays.asList(30L, 1, "Canoeing", "Paddling sport", null),
                Arrays.asList(31L, 1, "Bowling", "Rolling the ball to hit pins", null),
                Arrays.asList(32L, 1, "Darts", "Precision throwing game", null)

        );

        try {
            dbClient.createEntities(tableName, fields, batchFieldValues);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean testsingleInsertion(DbClient dbClient) {
        String tableName = "sport";
        List<String> fields = Arrays.asList("id", "version", "name", "description", "parentId");

        List<Object> batchFieldValues = Arrays.asList(1L, 1, "Football", "Outdoor game", null);

        try {
            dbClient.createEntity(tableName, fields, batchFieldValues);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
