package sepc.sample.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betbrain.sepc.connector.sportsmodel.Entity;
import com.betbrain.sepc.connector.sportsmodel.EntityChange;
import com.betbrain.sepc.connector.sportsmodel.EntityCreate;
import com.betbrain.sepc.connector.sportsmodel.EntityDelete;
import com.betbrain.sepc.connector.sportsmodel.EntityUpdate;

import sepc.sample.DB.DbClient;

public class StoreEntity {

    private static final Logger logger = LoggerFactory.getLogger(StoreEntity.class);

    boolean runner = true;
    boolean Cacherunner = true;
    ExecutorService executorServicecache = Executors.newFixedThreadPool(12);

    public StoreEntity(RedisClient redisClient, DbClient dbClient, BlockingQueue<List<Entity>> entityqueue,
            BlockingQueue<EntityChange> updateentityQueue) {

        for (int i = 0; i < 12; i++) {
            executorServicecache.submit(() -> startProcessing(entityqueue, redisClient));

        }

        logger.info("Queue Consumer Started");

    }

    public void startProcessing(BlockingQueue<List<Entity>> entityQueue, RedisClient redisClient) {
        while (Cacherunner) {
            try {

                List<Entity> cacheEntities = entityQueue.take();
                redisClient.bulkInsertEntities(cacheEntities);

            } catch (Exception e) {

            }
        }
    }

    public void startInsertion(DbClient dbClient, RedisClient redisClient) {
    Map<String, List<List<Object>>> tableToFieldValuesMap = new HashMap<>();
    Map<String, List<String>> tableToFieldNamesMap = new HashMap<>();
    int totalCount = 0;

    while (runner) {
        try {
            String key = redisClient.lpop("entitiesToProcess");
            if (key != null) {
                Entity entity = (Entity) redisClient.getObject(key);
                if (entity != null) {
                    String tableName = entity.getDisplayName().toLowerCase();
                    List<String> fieldNames = entity.getPropertyNames();
                    List<Object> fieldValues = entity.getPropertyValues(fieldNames);

             
                    tableToFieldValuesMap.putIfAbsent(tableName, new ArrayList<>());
                    tableToFieldNamesMap.putIfAbsent(tableName, fieldNames);
                    tableToFieldValuesMap.get(tableName).add(fieldValues);

                    totalCount++;

             
                    if (totalCount >= 500) {
                        performBatchInsert(dbClient, tableToFieldValuesMap, tableToFieldNamesMap, 500);
                 
                        tableToFieldValuesMap.clear();
                        tableToFieldNamesMap.clear();
                        totalCount = 0;
                    }
                }
            } else {
              
                if (totalCount > 0) {
                    performBatchInsert(dbClient, tableToFieldValuesMap, tableToFieldNamesMap, totalCount);
                }
            
                break;
            }
        } catch (Exception e) {
          
        }
    }
}

private void performBatchInsert(DbClient dbClient, Map<String, List<List<Object>>> tableToFieldValuesMap,
                                Map<String, List<String>> tableToFieldNamesMap, int batchSize) throws SQLException {
   
    List<String> tables = new ArrayList<>(tableToFieldValuesMap.keySet());
    List<List<String>> fieldsPerTable = tables.stream().map(tableToFieldNamesMap::get).collect(Collectors.toList());
    List<List<List<Object>>> batchFieldValuesPerTable = tables.stream().map(tableToFieldValuesMap::get).collect(Collectors.toList());

    dbClient.createEntities(tables, fieldsPerTable, batchFieldValuesPerTable, batchSize);
}


    public void startUpdate(DbClient dbClient, BlockingQueue<EntityChange> updateentityQueue) {
        while (runner) {
            try {
                EntityChange entityChange = updateentityQueue.take();
                if (entityChange instanceof EntityCreate) {
                    EntityCreate newCreate = (EntityCreate) entityChange;
                    Entity entity = newCreate.getEntity();
                    createEntity.processEntity(entity, dbClient);
                } else if (entityChange instanceof EntityDelete) {
                    EntityDelete deletechange = (EntityDelete) entityChange;
                    Long Id = deletechange.getEntityId();
                    String table = deletechange.getEntityClass().getSimpleName().toLowerCase();
                    dbClient.deleteEntity(Id, table);

                } else if (entityChange instanceof EntityUpdate) {
                    EntityUpdate updatechange = (EntityUpdate) entityChange;
                    String table = updatechange.getEntityClass().getSimpleName().toLowerCase();
                    Long Id = updatechange.getEntityId();
                    List<Object> fieldvalues = updatechange.getPropertyValues();
                    List<String> fields = updatechange.getPropertyNames();
                    dbClient.updateEntity(Id, table, fields, fieldvalues);

                }
                Thread.sleep(200);

            } catch (Exception e) {
               
            }

        }
    }

    public void CacheShutdown() {
        Cacherunner = false;
        executorServicecache.shutdownNow();

    }

    public void CloseThreads() {
        executorServicecache.shutdownNow();
    }

}
