package sepc.client;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import java.util.concurrent.LinkedBlockingDeque;

import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betbrain.sepc.connector.sdql.EntityChangeBatchProcessingMonitor;
import com.betbrain.sepc.connector.sdql.SEPCPushConnector;
import com.betbrain.sepc.connector.sdql.SEPCStreamedConnectorListener;
import com.betbrain.sepc.connector.sportsmodel.Entity;

import com.betbrain.sepc.connector.sportsmodel.EntityChange;
import com.betbrain.sepc.connector.sportsmodel.EntityChangeBatch;

import sepc.client.DB.DbClient;
import sepc.client.utils.StoreEntity;

public class PushConnector {
    private static final Logger logger = LoggerFactory.getLogger(PushConnector.class);
    public BlockingQueue<List<Entity>> entityQueue = new LinkedBlockingDeque<>();
    public BlockingQueue<List<EntityChange>> updateentityQueue = new LinkedBlockingDeque<>();

    private final SEPCPushConnector connector;

    public PushConnector(String hostname, int portPush, String subscription, String dbname, String dburl, String dbuser,
            String dbpass, DbClient dbClient) {

        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        connector = new SEPCPushConnector(hostname, portPush);
        dbClient = DbClient.getInstance(dbname, dburl, dbuser, dbpass);

        StoreEntity storeEntity = new StoreEntity(dbClient, entityQueue, updateentityQueue);
        SEPCPUSHConnectorListener listener = new SEPCPUSHConnectorListener(updateentityQueue, dbClient);

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
        storeEntity.updateshutdown();
        dbClient.close();
        connector.stop();

        logger.info("Stopping the connection");
        System.exit(0);
    }

    public static class SEPCPUSHConnectorListener implements SEPCStreamedConnectorListener {
        private volatile String lastBatchUuid;
        private volatile String SubscriptionId;
        private volatile String subscriptionChecksum;
        private final BlockingQueue<List<EntityChange>> updateentityQueue;
        DbClient dbClient;

        public SEPCPUSHConnectorListener(BlockingQueue<List<EntityChange>> updateentityQueue,
                DbClient dbClient) {
            this.updateentityQueue = updateentityQueue;
            this.dbClient = dbClient;

        }

        public void notifyInitialDumpToBeRetrieved() {
            System.out.println("Initial dump starting ");
        }

        @Override
        public void notifyPartialInitialDumpRetrieved(List<? extends Entity> entities) {

            logger.info("Recieved initial batch size: " + entities.size());

        }

        @Override
        public void notifyInitialDumpRetrieved() {

            logger.info("initial dump done");

        }

        @Override
        public void notifyEntityUpdatesRetrieved(EntityChangeBatch entityChangeBatch) {
            List<EntityChange> ListChangeEntities;
            ListChangeEntities = entityChangeBatch.getEntityChanges();
            logger.info("Recieved Update batch: " + ListChangeEntities.toString());
            updateentityQueue.offer(ListChangeEntities);
            ListChangeEntities.clear();
            logger.info("Queue size" + updateentityQueue.size());

            lastBatchUuid = entityChangeBatch.getUuid();
            SubscriptionId = entityChangeBatch.getSubscriptionId();
            subscriptionChecksum = entityChangeBatch.getSubscriptionCheckSum();

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
