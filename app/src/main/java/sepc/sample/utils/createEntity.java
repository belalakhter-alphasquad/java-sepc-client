package sepc.sample.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import java.sql.SQLException;
import sepc.sample.DB.DbClient;
import java.util.List;

public class createEntity {
    private static final Logger logger = LoggerFactory.getLogger(createEntity.class);

    public static void processEntity(Entity entity, DbClient dbClient) {
        String table = entity.getDisplayName().toLowerCase();
        List<String> fields = entity.getPropertyNames();
        List<Object> values = entity.getPropertyValues(fields);
        try {
            dbClient.createEntity(table, fields, values);
        } catch (SQLException e) {

        }
    }

    public static void processEntitiesBatch(List<Entity> entities, DbClient dbClient) {
        if (entities.isEmpty())
            return;

        String table = entities.get(0).getDisplayName().toLowerCase();
        List<String> fields = entities.get(0).getPropertyNames();

        try {
            dbClient.createEntitiesBatch(table, fields, entities);
        } catch (SQLException e) {

        }
        entities.clear();
    }

}
