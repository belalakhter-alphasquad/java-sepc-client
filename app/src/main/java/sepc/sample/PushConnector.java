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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class PushConnector {
    private final CountDownLatch latch = new CountDownLatch(1);
    private final SEPCPushConnector connector;

    public PushConnector(String hostname, int port, String subscription) {
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        connector = new SEPCPushConnector(hostname, port);
        SEPCPUSHConnectorListener listener = new SEPCPUSHConnectorListener(latch);

        connector.addStreamedConnectorListener(listener);
        connector.setEntityChangeBatchProcessingMonitor(new EntityChangeBatchProcessingMonitor() {
            @Override
            public String getLastAppliedEntityChangeBatchUuid() {
                return listener.getLastBatchUuid();
            }
        });

        connector.start(subscription);

        // try {
        // latch.await();
        // } catch (InterruptedException e) {
        // Thread.currentThread().interrupt();
        // }
        // connector.stop();
        // System.out.println("Stopping the connection");

        // connector.startWithResume(subscription, subscription, subscription,
        // subscription);
        barrier.await();
        connector.stop();
        System.out.println("Stopping the connection");

    }

    public static class SEPCPUSHConnectorListener implements SEPCStreamedConnectorListener {
        private final CountDownLatch latch;
        private volatile String lastBatchUuid;
        private volatile String SubscriptionId;
        private volatile String subscriptionChecksum;

        public SEPCPUSHConnectorListener(CountDownLatch latch) {
            this.latch = latch;
        }

        public void notifyInitialDumpToBeRetrieved() {
            System.out.println("Initial dump starting ");
        }

        private int batchCounterEntity = 0;

        @Override
        public void notifyPartialInitialDumpRetrieved(List<? extends Entity> entities) {
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
            List<String> processedEntities = new CopyOnWriteArrayList<>();
            for (Entity entity : entities) {
                executor.submit(() -> {
                    processedEntities.add(entity.toString());
                });
            }
            executor.shutdown();
            try {
                if (!executor.awaitTermination(6, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            String fileName = "./xml/batch_" + batchCounterEntity + ".xml";
            try {
                xmlMapper.writeValue(new File(fileName), processedEntities);
                System.out.println("Batch " + batchCounterEntity + " written successfully to " + fileName);
                batchCounterEntity++;
            } catch (IOException e) {
                System.out.println("Error occurred while processing batch " + batchCounterEntity);
                e.printStackTrace();
            }

        }

        @Override
        public void notifyInitialDumpRetrieved() {
            System.out.println("Initial dump done ");
            latch.countDown();
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