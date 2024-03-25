package sepc.client;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betbrain.sepc.connector.sdql.EntityChangeBatchProcessingMonitor;
import com.betbrain.sepc.connector.sdql.SEPCPushConnector;
import com.betbrain.sepc.connector.sdql.SEPCStreamedConnectorListener;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import java.util.concurrent.TimeUnit;
import com.betbrain.sepc.connector.sportsmodel.EntityChange;
import com.betbrain.sepc.connector.sportsmodel.EntityChangeBatch;

import sepc.client.DB.DbClient;
import sepc.client.utils.StoreEntity;

public class PushConnector {
    private static final Logger logger = LoggerFactory.getLogger(PushConnector.class);
    public BlockingQueue<List<Entity>> entityQueue = new LinkedBlockingDeque<>();
    public BlockingQueue<List<EntityChange>> updateentityQueue = new LinkedBlockingDeque<>();

    private final SEPCPushConnector connector;
    static boolean checkInitialDumpComplete = false;

    public PushConnector(String hostname, int portPush, String subscription, String DATABASE_NAME, String DB_URL,
            String DB_USER, String DB_Pass) {

        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        connector = new SEPCPushConnector(hostname, portPush);
        DbClient dbClient = DbClient.getInstance(DATABASE_NAME, DB_URL, DB_USER, DB_Pass);
        ExecutorService executorServiceUpdate = Executors.newFixedThreadPool(6);

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
        logger.info("Attempting to start the connector");
        connector.setReconnectInterval(2, TimeUnit.MINUTES);
        connector.setDisconnectTimeOutWhenNoMessagesFromServer(TimeUnit.MINUTES, 5);

        connector.start(subscription);

        barrier.await();
        storeEntity.CloseThreads();
        executorServiceUpdate.shutdownNow();
        connector.stop();
        dbClient.close();
        logger.info("Stopping the connection");
        System.exit(0);

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
            logger.info("Initial dump started");
        }

        @Override
        public void notifyPartialInitialDumpRetrieved(List<? extends Entity> entities) {

            logger.info("Recieved initial batch size: " + entities.size());

        }

        @Override
        public void notifyInitialDumpRetrieved() {

            checkInitialDumpComplete = true;
            for (int i = 0; i < 6; i++) {
                executorServiceUpdate.submit(() -> storeEntity.startUpdate(entityQueue, dbClient, updateentityQueue));

            }

            logger.info("initial dump done");

        }

        @Override
        public void notifyEntityUpdatesRetrieved(EntityChangeBatch entityChangeBatch) {

            lastBatchUuid = entityChangeBatch.getUuid();
            SubscriptionId = entityChangeBatch.getSubscriptionId();
            subscriptionChecksum = entityChangeBatch.getSubscriptionCheckSum();

            List<EntityChange> ListChangeEntities = entityChangeBatch.getEntityChanges();
            logger.info("Recieved Update batch: " + ListChangeEntities.toString());

            if (checkInitialDumpComplete) {

                updateentityQueue.offer(ListChangeEntities);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error("Thread sleep failed ", e);
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
