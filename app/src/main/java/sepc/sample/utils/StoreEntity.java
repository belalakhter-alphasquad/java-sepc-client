package sepc.sample.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import java.util.concurrent.BlockingQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betbrain.sepc.connector.sportsmodel.EntityChange;
import com.betbrain.sepc.connector.sportsmodel.EntityCreate;
import com.betbrain.sepc.connector.sportsmodel.EntityDelete;
import com.betbrain.sepc.connector.sportsmodel.EntityUpdate;

import sepc.sample.DB.DbClient;

public class StoreEntity {

    private static final Logger logger = LoggerFactory.getLogger(StoreEntity.class);

    boolean runner = true;
    boolean Cacherunner = true;
    ExecutorService executorServicecache = Executors.newFixedThreadPool(1);

    public StoreEntity(DbClient dbClient, BlockingQueue<List<Entity>> entityqueue,
            BlockingQueue<List<EntityChange>> updateentityQueue) {

    }

    public void startInsertion(BlockingQueue<List<Entity>> entityQueue, DbClient dbClient) {

        List<Entity> uniqueEntities;

        while (Cacherunner) {
            try {

                uniqueEntities = new ArrayList<>(new LinkedHashSet<>(entityQueue.take()));
                logger.info("\n Queue Size: " + entityQueue.size() + ", List Size: " + uniqueEntities.size() + "\n");

                if (!uniqueEntities.isEmpty()) {
                    String table = uniqueEntities.get(0).getDisplayName().toLowerCase();
                    dbClient.createEntities(table, uniqueEntities);
                }
                uniqueEntities.clear();
            } catch (Exception e) {
                logger.error("Exception -> startInsertion(): ", e.getMessage());
            }
        }
    }

    public void startUpdate(BlockingQueue<List<Entity>> entityQueue, DbClient dbClient,
            BlockingQueue<List<EntityChange>> updateentityQueue) {
        boolean cacheShutdownInitiated = false;
        List<EntityChange> ListChangeEntities;
        while (runner) {
            try {
                ListChangeEntities = updateentityQueue.take();
                for (EntityChange entityChange : ListChangeEntities) {

                    if (entityChange instanceof EntityCreate) {
                        EntityCreate newCreate = (EntityCreate) entityChange;
                        Entity entity = newCreate.getEntity();
                        dbClient.createEntity(entity.getDisplayName().toLowerCase(), entity.getPropertyNames(),
                                entity.getPropertyValues(entity.getPropertyNames()));
                    } else if (entityChange instanceof EntityDelete) {
                        EntityDelete deletechange = (EntityDelete) entityChange;
                        dbClient.deleteEntity(deletechange.getEntityId(), deletechange.getDisplayName().toLowerCase());

                    } else if (entityChange instanceof EntityUpdate) {
                        EntityUpdate updatechange = (EntityUpdate) entityChange;
                        dbClient.updateEntity(updatechange.getEntityId(), updatechange.getDisplayName().toLowerCase(),
                                updatechange.getPropertyNames(),
                                updatechange.getPropertyValues(updatechange.getPropertyNames()));

                    }

                }
            } catch (Exception e) {
                logger.error("Exception caught for update batch", e);
            }

            if (!cacheShutdownInitiated && entityQueue.isEmpty()) {
                CacheShutdown();
                cacheShutdownInitiated = true;
            }

        }

    }

    public void CacheShutdown() {
        Cacherunner = false;

    }

    public void updateshutdown() {
        runner = false;
    }

    public void CloseThreads() {
        executorServicecache.shutdownNow();
    }

}
