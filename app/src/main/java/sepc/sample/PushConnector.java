package sepc.sample;

import java.util.List;

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

    private final SEPCPushConnector connector;
    static DbClient dbClient = DbClient.getInstance();
    static boolean check = true;

    public PushConnector(String hostname, int portPush, String subscription) {

        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        connector = new SEPCPushConnector(hostname, portPush);
        StoreEntity storeEntity = new StoreEntity(dbClient);
        SEPCPUSHConnectorListener listener = new SEPCPUSHConnectorListener(storeEntity);

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
        storeEntity.shutdown();
        connector.stop();

        System.out.println("Stopping the connection");

    }

    public static class SEPCPUSHConnectorListener implements SEPCStreamedConnectorListener {
        private volatile String lastBatchUuid;
        private volatile String SubscriptionId;
        private volatile String subscriptionChecksum;
        private final StoreEntity storeEntity;

        public SEPCPUSHConnectorListener(StoreEntity storeEntity) {
            this.storeEntity = storeEntity;
            storeEntity.startProcessing();
            logger.info("Queue processor is running ");
        }

        public void notifyInitialDumpToBeRetrieved() {
            System.out.println("Initial dump starting ");
        }

        @Override
        public void notifyPartialInitialDumpRetrieved(List<? extends Entity> entities) {

            for (Entity entity : entities) {
                storeEntity.queueEntity(entity);
            }

        }

        @Override
        public void notifyInitialDumpRetrieved() {
            check = false;
            System.out.println("Initial dump done ");
        }

        @Override
        public void notifyEntityUpdatesRetrieved(EntityChangeBatch entityChangeBatch) {
            lastBatchUuid = entityChangeBatch.getUuid();
            SubscriptionId = entityChangeBatch.getSubscriptionId();
            subscriptionChecksum = entityChangeBatch.getSubscriptionCheckSum();
            entityChangeBatch.getClass();
            logger.info(entityChangeBatch.getEntityChanges().toString());
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