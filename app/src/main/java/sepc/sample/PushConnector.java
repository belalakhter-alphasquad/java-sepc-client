package sepc.sample;

import java.util.List;

import org.agrona.concurrent.ShutdownSignalBarrier;

import com.betbrain.sepc.connector.sdql.EntityChangeBatchProcessingMonitor;
import com.betbrain.sepc.connector.sdql.SEPCPushConnector;
import com.betbrain.sepc.connector.sdql.SEPCStreamedConnectorListener;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import com.betbrain.sepc.connector.sportsmodel.EntityChangeBatch;

import sepc.sample.DB.DbClient;

import sepc.sample.utils.StoreEntity;

public class PushConnector {

    private final SEPCPushConnector connector;
    static DbClient dbClient = DbClient.getInstance();
    static StoreEntity storeEntity = new StoreEntity(dbClient);

    public PushConnector(String hostname, int portPush, String subscription) {

        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        connector = new SEPCPushConnector(hostname, portPush);
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
        }

        public void notifyInitialDumpToBeRetrieved() {
            System.out.println("Initial dump starting ");
        }

        @Override
        public void notifyPartialInitialDumpRetrieved(List<? extends Entity> entities) {
            System.out.println("\nReceived Entity" + " total entities in batch " + entities.size() + "\n");
            entities.parallelStream().forEach(storeEntity::queueEntity);
        }

        @Override
        public void notifyInitialDumpRetrieved() {
            System.out.println("Initial dump done ");
            System.exit(0);
        }

        @Override
        public void notifyEntityUpdatesRetrieved(EntityChangeBatch entityChangeBatch) {
            lastBatchUuid = entityChangeBatch.getUuid();
            SubscriptionId = entityChangeBatch.getSubscriptionId();
            subscriptionChecksum = entityChangeBatch.getSubscriptionCheckSum();

            System.out
                    .println("Notifying entity update... entity changes size: " + entityChangeBatch.getEntityChanges());
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