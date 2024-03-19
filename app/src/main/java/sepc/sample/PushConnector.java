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
import sepc.sample.utils.StoreEntity;

public class PushConnector {
    private static final Logger logger = LoggerFactory.getLogger(PushConnector.class);
    public BlockingQueue<List<Entity>> entityQueue = new LinkedBlockingDeque<>();
    public BlockingQueue<List<EntityChange>> updateentityQueue = new LinkedBlockingDeque<>();

    private final SEPCPushConnector connector;
    static boolean checkInitialDumpComplete = false;

    public PushConnector(String hostname, int portPush, String subscription) {

        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        connector = new SEPCPushConnector(hostname, portPush);
        DbClient dbClient = DbClient.getInstance();
        ExecutorService executorServiceUpdate = Executors.newFixedThreadPool(4);

        StoreEntity storeEntity = new StoreEntity(dbClient, entityQueue, updateentityQueue);
        SEPCPUSHConnectorListener listener = new SEPCPUSHConnectorListener(storeEntity, entityQueue, updateentityQueue,
                dbClient, executorServiceUpdate);

        connector.addStreamedConnectorListener(listener);
        connector.setEntityChangeBatchProcessingMonitor(new EntityChangeBatchProcessingMonitor() {
            @Override
            public String getLastAppliedEntityChangeBatchUuid() {
                return listener.getLastBatchUuid();
            }
        });
        logger.info("\nAttempting to start the connector\n");
        connector.start(subscription);

        barrier.await();
        storeEntity.CloseThreads();

        connector.stop();

        logger.info("Stopping the connection");

    }

    public static class SEPCPUSHConnectorListener implements SEPCStreamedConnectorListener {
        private volatile String lastBatchUuid;
        private volatile String SubscriptionId;
        private volatile String subscriptionChecksum;
        private final StoreEntity storeEntity;
        private final BlockingQueue<List<Entity>> entityQueue;
        private final BlockingQueue<List<EntityChange>> updateentityQueue;
        DbClient dbClient;
        ExecutorService executorServiceUpdate;

        public SEPCPUSHConnectorListener(StoreEntity storeEntity, BlockingQueue<List<Entity>> entityQueue,
                BlockingQueue<List<EntityChange>> updateentityQueue,
                DbClient dbClient, ExecutorService executorServiceUpdate) {
            this.storeEntity = storeEntity;
            this.entityQueue = entityQueue;
            this.updateentityQueue = updateentityQueue;
            this.dbClient = dbClient;
            this.executorServiceUpdate = executorServiceUpdate;

        }

        public void notifyInitialDumpToBeRetrieved() {
            System.out.println("Initial dump starting ");
        }

        @Override
        public void notifyPartialInitialDumpRetrieved(List<? extends Entity> entities) {

            List<Entity> receivedEntities = entities.stream().collect(Collectors.toList());
            logger.info("\n \n PUSH Connector "
                    + receivedEntities.get(0).getDisplayName().toLowerCase() + " and this is size "
                    + receivedEntities.size());

            entityQueue.offer(receivedEntities);

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {

            }

        }

        @Override
        public void notifyInitialDumpRetrieved() {

            checkInitialDumpComplete = true;
            for (int i = 0; i < 4; i++) {
                executorServiceUpdate.submit(() -> storeEntity.startUpdate(entityQueue, dbClient, updateentityQueue));

            }

            logger.info("initial dump done");

        }

        @Override
        public void notifyEntityUpdatesRetrieved(EntityChangeBatch entityChangeBatch) {

            lastBatchUuid = entityChangeBatch.getUuid();
            List<EntityChange> ListChangeEntities = entityChangeBatch.getEntityChanges();

            if (checkInitialDumpComplete) {

                updateentityQueue.offer(ListChangeEntities);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error("Error caught for getting update batch", e);
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
