package sepc.sample;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.betbrain.sepc.connector.sdql.EntityChangeBatchProcessingMonitor;

import com.betbrain.sepc.connector.sdql.SEPCPushConnector;
import com.betbrain.sepc.connector.sdql.SEPCStreamedConnectorListener;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import com.betbrain.sepc.connector.sportsmodel.EntityChangeBatch;
import org.agrona.concurrent.ShutdownSignalBarrier;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class PushConnector {

    public PushConnector(String hostname, int port, String subscription) {
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        SEPCPushConnector connector = new SEPCPushConnector(hostname, port);
        SEPCPUSHConnectorListener listener = new SEPCPUSHConnectorListener();

        connector.addStreamedConnectorListener(listener);
        connector.setEntityChangeBatchProcessingMonitor(new EntityChangeBatchProcessingMonitor() {
            @Override
            public String getLastAppliedEntityChangeBatchUuid() {
                return listener.getLastBatchUuid();
            }
        });

        connector.start(subscription);
        // connector.startWithResume(subscription, subscription, subscription,
        // subscription);
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
            System.out.println("Initial dump batch entities received " + entities.size());

            XmlMapper xmlMapper = new XmlMapper();
            try {
                List<String> lines = entities.stream().map(Entity::toString).collect(Collectors.toList());
                Path path = Paths.get("./entities.xml");

                xmlMapper.writeValue(new File(path.toString()), lines);
                System.out.println("Write successful");

            } catch (IOException e) {
                System.out.println("Error occurred while processing data");
                e.printStackTrace();
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