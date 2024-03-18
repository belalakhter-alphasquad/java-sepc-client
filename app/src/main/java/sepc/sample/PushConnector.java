package sepc.sample;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betbrain.sepc.connector.sdql.EntityChangeBatchProcessingMonitor;
import com.betbrain.sepc.connector.sdql.SEPCPushConnector;
import com.betbrain.sepc.connector.sdql.SEPCStreamedConnectorListener;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import com.betbrain.sepc.connector.sportsmodel.EntityChange;
import com.betbrain.sepc.connector.sportsmodel.EntityChangeBatch;

import sepc.sample.DB.DbClient;
import sepc.sample.utils.RedisClient;
import sepc.sample.utils.StoreEntity;

public class PushConnector {
    private static final Logger logger = LoggerFactory.getLogger(PushConnector.class);
    public BlockingQueue<List<Entity>> entityQueue = new LinkedBlockingDeque<>();
    public BlockingQueue<EntityChange> updateentityQueue = new LinkedBlockingDeque<>();

    private final SEPCPushConnector connector;
    static boolean checkInitialDumpComplete = false;

    public PushConnector(String hostname, int portPush, String subscription) {

        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        connector = new SEPCPushConnector(hostname, portPush);
        RedisClient redisClient = new RedisClient("localhost", 6379);
        DbClient dbClient = DbClient.getInstance();
        ExecutorService executorServiceinsertion = Executors.newFixedThreadPool(6);

        StoreEntity storeEntity = new StoreEntity(redisClient, dbClient, entityQueue, updateentityQueue);
        SEPCPUSHConnectorListener listener = new SEPCPUSHConnectorListener(storeEntity, entityQueue, updateentityQueue,
                dbClient, redisClient, executorServiceinsertion);

        connector.addStreamedConnectorListener(listener);
        connector.setEntityChangeBatchProcessingMonitor(new EntityChangeBatchProcessingMonitor() {
            @Override
            public String getLastAppliedEntityChangeBatchUuid() {
                return listener.getLastBatchUuid();
            }
        });
        System.out.println("\nAttempting to start the connector\n");
        connector.start(subscription);

        barrier.await();
        storeEntity.CloseThreads();

        connector.stop();

        System.out.println("Stopping the connection");

    }

    public static class SEPCPUSHConnectorListener implements SEPCStreamedConnectorListener {
        private volatile String lastBatchUuid;
        private volatile String SubscriptionId;
        private volatile String subscriptionChecksum;
        private final StoreEntity storeEntity;
        private final BlockingQueue<List<Entity>> entityQueue;
        private final BlockingQueue<EntityChange> updateentityQueue;
        DbClient dbClient;
        RedisClient redisClient;
        ExecutorService executorServiceinsertion;

        public SEPCPUSHConnectorListener(StoreEntity storeEntity, BlockingQueue<List<Entity>> entityQueue,
                BlockingQueue<EntityChange> updateentityQueue,
                DbClient dbClient, RedisClient redisClient, ExecutorService executorServiceinsertion) {
            this.storeEntity = storeEntity;
            this.entityQueue = entityQueue;
            this.updateentityQueue = updateentityQueue;
            this.dbClient = dbClient;
            this.redisClient = redisClient;
            this.executorServiceinsertion = executorServiceinsertion;

        }

        public void notifyInitialDumpToBeRetrieved() {
            System.out.println("Initial dump starting ");
        }

        @Override
        public void notifyPartialInitialDumpRetrieved(List<? extends Entity> entities) {

            List<Entity> receivedEntities = entities.stream().collect(Collectors.toList());

            entityQueue.offer(receivedEntities);

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {

            }

        }

        @Override
        public void notifyInitialDumpRetrieved() {

            checkInitialDumpComplete = true;

            // executorServiceInsertion.submit(() -> storeEntity.startUpdate(dbClient,
            // updateentityQueue));

            logger.info("initial dump done");

        }

        @Override
        public void notifyEntityUpdatesRetrieved(EntityChangeBatch entityChangeBatch) {

            lastBatchUuid = entityChangeBatch.getUuid();
            SubscriptionId = entityChangeBatch.getSubscriptionId();
            subscriptionChecksum = entityChangeBatch.getSubscriptionCheckSum();
            List<EntityChange> ListChangeEntities = entityChangeBatch.getEntityChanges();

            if (checkInitialDumpComplete) {
                for (EntityChange entityChange : ListChangeEntities) {
                    // updateentityQueue.offer(entityChange);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }

            }

        }

        public String getLastBatchUuid() {
            return lastBatchUuid;
        }

        public String getSubscriptionId() {
            return SubscriptionId;
        }

        public String getsubscriptionChecksum() {
            return subscriptionChecksum;
        }

    }

}
