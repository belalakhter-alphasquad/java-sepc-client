package sepc.sample.utils;

import java.sql.SQLException;
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
    ExecutorService executorServicecache = Executors.newFixedThreadPool(22);

    public StoreEntity(RedisClient redisClient, DbClient dbClient, BlockingQueue<List<Entity>> entityqueue,
            BlockingQueue<EntityChange> updateentityQueue) {

        for (int i = 0; i < 11; i++) {
            executorServicecache.submit(() -> startProcessing(entityqueue, redisClient));

        }
        for (int i = 0; i < 11; i++) {
            executorServicecache.submit(() -> startInsertion(dbClient, redisClient));

        }

        logger.info("Queue Consumer Started");

    }

    public void startProcessing(BlockingQueue<List<Entity>> entityQueue, RedisClient redisClient) {
        while (Cacherunner) {
            try {

                List<Entity> cacheEntities = entityQueue.take();
                for(Entity entity : cacheEntities){
                    String key = "entity:" + entity.getId();
                  redisClient.setObject(key, entity);
                  redisClient.rpush("entitiesToProcess", key);
                }

            } catch (Exception e) {

            }
        }
    }

    public void startInsertion(DbClient dbClient, RedisClient redisClient) {
 List<String> tables = new ArrayList<>();
 List<List<String>> fieldsnames = new ArrayList<>();
 List<List<Object>> fieldvalues = new ArrayList<>();

    while (runner) {
        try {
            String key = redisClient.lpop("entitiesToProcess");
            if(key!=null){
           
            Entity entity = (Entity) redisClient.getObject(key);
            tables.add(entity.getDisplayName().toLowerCase());
            List<String> entityfieldnames = entity.getPropertyNames();
            fieldsnames.add(entityfieldnames);
            List<Object> entityfieldvalues= entity.getPropertyValues(entityfieldnames);

            fieldvalues.add(entityfieldvalues);
            if (tables.size()== 100) {
                dbClient.createEntities(tables, fieldsnames, fieldvalues, 100);
            }
        }else{
            dbClient.createEntities(tables, fieldsnames, fieldvalues, tables.size());
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
