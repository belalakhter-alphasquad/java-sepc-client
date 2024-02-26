package sepc.sample;

import java.io.IOException;
import java.util.List;

import com.betbrain.sepc.connector.sdql.EntityChangeBatchProcessingMonitor;
import com.betbrain.sepc.connector.sdql.SEPCConnector;
import com.betbrain.sepc.connector.sdql.SEPCPullConnector;
import com.betbrain.sepc.connector.sdql.SEPCPushConnector;
import com.betbrain.sepc.connector.sdql.SEPCStreamedConnectorListener;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import com.betbrain.sepc.connector.sportsmodel.EntityChangeBatch;
import org.agrona.concurrent.ShutdownSignalBarrier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

import sepc.sample.utils.FileWriterUtility;

public class PushConnector {

    public PushConnector(String hostname, int port, String subscription) {
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        SEPCConnector connector = new SEPCPushConnector(hostname, port);
        SEPCPUSHConnectorListener listener = new SEPCPUSHConnectorListener();

        connector.addStreamedConnectorListener(listener);
        connector.setEntityChangeBatchProcessingMonitor(new EntityChangeBatchProcessingMonitor() {
            @Override
            public String getLastAppliedEntityChangeBatchUuid() {
                return listener.getLastBatchUuid();
            }
        });

        connector.start(subscription);
        connector.nextConnectorStep();
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
            Path path = Paths.get("./entities.txt.gz");

            List<String> lines = entities.stream().map(Entity::toString).collect(Collectors.toList());

            try (
                    FileOutputStream fos = new FileOutputStream(path.toFile(), true);
                    GZIPOutputStream gzos = new GZIPOutputStream(fos);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(gzos, StandardCharsets.UTF_8))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                System.out.println("Write successful with GZip compression");
            } catch (IOException e) {
                System.out.println("GZip compression and write method failed");
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