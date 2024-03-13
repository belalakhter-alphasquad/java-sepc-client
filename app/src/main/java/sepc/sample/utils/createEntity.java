package sepc.sample.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import java.sql.SQLException;
import sepc.sample.DB.DbClient;
import java.util.List;
import java.util.ArrayList;

public class createEntity {
    private static final Logger logger = LoggerFactory.getLogger(createEntity.class);

    public static void processEntity(List<Entity> batch, DbClient dbClient) {
        if (batch.isEmpty())
            return;
        String table = batch.get(0).getDisplayName().toLowerCase();
        List<String> fields = batch.get(0).getPropertyNames();
        List<List<Object>> batchFieldValues = new ArrayList<>();
        for (Entity entity : batch) {
            List<Object> values = entity.getPropertyValues(fields);
            batchFieldValues.add(values);
        }
        try {
            dbClient.createEntity(table, fields, batchFieldValues);
            logger.info("sending a batch to db clien");
        } catch (SQLException e) {

        }
    }

    public static void processUpdateEntity(Entity entity, DbClient dbClient) {
        String table = entity.getDisplayName().toLowerCase();
        List<String> fields = entity.getPropertyNames();
        List<Object> values = entity.getPropertyValues(fields);
        try {
            dbClient.createUpdateEntity(table, fields, values);
        } catch (SQLException e) {

        }
    }
}
