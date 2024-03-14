package sepc.sample.utils;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.BlockingQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    ExecutorService executorServicecache = Executors.newFixedThreadPool(4);

    public StoreEntity(RedisClient redisClient, DbClient dbClient, BlockingQueue<List<Entity>> entityqueue,
            BlockingQueue<EntityChange> updateentityQueue) {

        for (int i = 0; i < 4; i++) {
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
        logger.info("Insertion Started");
        List<List<Object>> batchFieldValues = new ArrayList<>();
        final int batchSize = 10000;
        String table = "participant";
        List<String> fields = new ArrayList<>();
        while (runner) {
            try {
                String key = redisClient.lpop("participant");
                if (key != null) {
                    Entity entity = (Entity) redisClient.getObject(key);
                    if (entity != null) {

                        if (fields.isEmpty()) {
                            fields = entity.getPropertyNames();
                            table = entity.getDisplayName().toLowerCase();
                        }

                        List<Object> values = entity.getPropertyValues(fields);
                        batchFieldValues.add(values);

                        if (batchFieldValues.size() == batchSize) {
                            dbClient.createEntities(table, fields, batchFieldValues);
                            batchFieldValues.clear();
                        }
                    }
                } else {
                    if (!batchFieldValues.isEmpty()) {
                        dbClient.createEntities(table, fields, batchFieldValues);
                        batchFieldValues.clear();
                    }
                    break;
                }
            } catch (Exception e) {
            }

        }

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
                // do something
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
