package sepc.sample;

import java.sql.SQLException;
import java.util.List;

import org.agrona.concurrent.ShutdownSignalBarrier;

import com.betbrain.sepc.connector.sdql.EntityChangeBatchProcessingMonitor;
import com.betbrain.sepc.connector.sdql.SEPCPushConnector;
import com.betbrain.sepc.connector.sdql.SEPCStreamedConnectorListener;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import com.betbrain.sepc.connector.sportsmodel.Sport;
import com.betbrain.sepc.connector.sportsmodel.BettingOffer;
import com.betbrain.sepc.connector.sportsmodel.EntityChangeBatch;

import sepc.sample.DB.DbClient;

public class PushConnector {
    private final SEPCPushConnector connector;
    static DbClient dbClient = DbClient.getInstance();

    public PushConnector(String hostname, int port, String subscription) {

        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        connector = new SEPCPushConnector(hostname, port);
        SEPCPUSHConnectorListener listener = new SEPCPUSHConnectorListener();

        connector.addStreamedConnectorListener(listener);
        connector.setEntityChangeBatchProcessingMonitor(new EntityChangeBatchProcessingMonitor() {
            @Override
            public String getLastAppliedEntityChangeBatchUuid() {
                return listener.getLastBatchUuid();
            }
        });

        connector.start(subscription);

        barrier.await();
        connector.stop();
        System.out.println("Stopping the connection");

    }

    public static class SEPCPUSHConnectorListener implements SEPCStreamedConnectorListener {
        private volatile String lastBatchUuid;
        private volatile String SubscriptionId;
        private volatile String subscriptionChecksum;

        public void notifyInitialDumpToBeRetrieved() {
            System.out.println("Initial dump starting ");
        }

        @Override
        public void notifyPartialInitialDumpRetrieved(List<? extends Entity> entities) {
            for (Entity entity : entities) {
                if (entity instanceof Sport) {
                    Sport sport = (Sport) entity;
                    try {
                        dbClient.insertSport(sport);
                    } catch (SQLException e) {
                        System.err.println("Error inserting sport into the database: " + e.getMessage());
                    }
                }
                if (entity instanceof BettingOffer) {
                    BettingOffer BettingOffer = (BettingOffer) entity;
                    try {
                        dbClient.insertBettingOffer(BettingOffer);
                    } catch (SQLException e) {
                        System.err.println("Error inserting BettingOffer into the database: " + e.getMessage());
                    }
                }
            }

        }

        @Override
        public void notifyInitialDumpRetrieved() {
            System.out.println("Initial dump done ");
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