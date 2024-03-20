package sepc.client.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.betbrain.sepc.connector.sportsmodel.Entity;

import sepc.client.DB.DbClient;

import java.sql.SQLException;
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
   

}
