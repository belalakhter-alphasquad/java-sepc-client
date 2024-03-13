package sepc.sample.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import java.sql.SQLException;
import sepc.sample.DB.DbClient;
import java.util.List;

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
        } catch (SQLException e) {

        }
    }
}
