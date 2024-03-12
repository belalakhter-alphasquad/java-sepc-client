package sepc.sample.utils;

import java.util.List;

import java.util.concurrent.BlockingQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betbrain.sepc.connector.sportsmodel.Entity;
import com.betbrain.sepc.connector.sportsmodel.EntityChange;
import com.betbrain.sepc.connector.sportsmodel.EntityCreate;
import com.betbrain.sepc.connector.sportsmodel.EntityDelete;
import com.betbrain.sepc.connector.sportsmodel.EntityUpdate;

import sepc.sample.DB.DbClient;

public class StoreEntity {

    int count = 0;
    private static final Logger logger = LoggerFactory.getLogger(StoreEntity.class);
    public BlockingQueue<Entity> entityQueue = new LinkedBlockingDeque<>();
    public BlockingQueue<EntityChange> updateentityQueue = new LinkedBlockingDeque<>();
    boolean runner = true;
    ExecutorService executorService = Executors.newFixedThreadPool(4);

    public StoreEntity(RedisClient redisClient, DbClient dbClient) {

        for (int i = 0; i < 4; i++) {
            executorService.submit(() -> startProcessing(entityQueue, redisClient));

        }

        // executorService.submit(() -> startUpdate(dbClient, redisClient));

        logger.info("Queue Consumer Started");

    }

    public void startProcessing(BlockingQueue<Entity> entityQueue, RedisClient redisClient) {
        while (runner) {
            try {

                Entity entity = entityQueue.take();
                String key = "entity:" + entity.getId();
                redisClient.setObject(key, entity);
                redisClient.rpush("entitiesToProcess", key);

            } catch (Exception e) {

            }
        }
    }

    public void startInsertion(DbClient dbClient, RedisClient redisClient) {
        while (runner) {
            try {
                String key = redisClient.lpop("entitiesToProcess");
                if (key != null) {
                    Entity entity = (Entity) redisClient.getObject(key);
                    if (entity != null) {
                        createEntity.processEntity(entity, dbClient);
                    }
                }
            } catch (Exception e) {
                // do something
            }
        }
    }

    public void startUpdate(DbClient dbClient, RedisClient redisClient) {
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

            } catch (Exception e) {
                logger.error("Unable to process Entity change" + e);
            }

        }
    }

    public void queueEntity(Entity entity) {
        try {
            entityQueue.offer(entity);
        } catch (Exception e) {

        }

    }

    public void updatequeueEntity(EntityChange entityChange) {
        try {
            // updateentityQueue.offer(entityChange);
        } catch (Exception e) {

        }

    }

    public void shutdown() {
        runner = false;
        executorService.shutdownNow();
    }

}
