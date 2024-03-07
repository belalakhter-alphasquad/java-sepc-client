package sepc.sample.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betbrain.sepc.connector.sportsmodel.Entity;
import com.betbrain.sepc.connector.sportsmodel.EntityChange;

import sepc.sample.DB.DbClient;

public class StoreEntity {
    int count = 0;
    private static final Logger logger = LoggerFactory.getLogger(StoreEntity.class);
    public BlockingQueue<Entity> entityQueue = new LinkedBlockingDeque<>();
    boolean runner = true;
    public ExecutorService executorService = Executors.newFixedThreadPool(9);

    public StoreEntity() {
        RedisClient redisClient = new RedisClient("localhost", 6379);
        logger.info("Redis Intilialized");
        DbClient dbClient = DbClient.getInstance();

        for (int i = 0; i < 6; i++) {
            executorService.submit(() -> startProcessing(entityQueue, redisClient));

        }

        executorService.submit(() -> startInsertion(dbClient, redisClient));

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
                logger.error("Unable to process Entity" + e);
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
                logger.error("Unable to process Entity" + e);
            }
        }
    }

    public void queueEntity(Entity entity) {
        try {
            entityQueue.offer(entity);
        } catch (Exception e) {
            logger.info("Unable to add entity to the queue");
        }

    }

    public void changeEntity(EntityChange entityChange) {

        logger.info(entityChange.toString());
    }

    public void shutdown() {
        runner = false;
        executorService.shutdownNow();
    }

}




// Source code is decompiled from a .class file using FernFlower decompiler.
package com.betbrain.sepc.connector.sportsmodel;

public class Sport extends Entity {
   private static final long serialVersionUID = 1L;
   public static final String PROPERTY_NAME_name = "name";
   public static final String PROPERTY_NAME_description = "description";
   public static final String PROPERTY_NAME_parentId = "parentId";
   private String _name;
   private String _description;
   private Long _parentId;

   public Sport() {
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public String getDescription() {
      return this._description;
   }

   public void setDescription(String description) {
      this._description = description;
   }

   public Long getParentId() {
      return this._parentId;
   }

   public void setParentId(Long parentId) {
      this._parentId = parentId;
   }
}
