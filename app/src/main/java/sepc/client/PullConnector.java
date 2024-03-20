package sepc.client;

import java.util.List;

import com.betbrain.sepc.connector.sdql.SEPCConnector;
import com.betbrain.sepc.connector.sdql.SEPCConnectorListener;
import com.betbrain.sepc.connector.sdql.SEPCPullConnector;

import org.agrona.concurrent.ShutdownSignalBarrier;

import com.betbrain.sepc.connector.sportsmodel.Entity;
import com.betbrain.sepc.connector.sportsmodel.EntityChangeBatch;

public class PullConnector {

    public PullConnector(String hostname, int port, String subscription, Long timeout) {
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        SEPCConnector connector = new SEPCPullConnector(hostname, port, timeout);
        SEPCPULLConnectorListener listener = new SEPCPULLConnectorListener();

        connector.addConnectorListener(listener);
        connector.start(subscription);
        barrier.await();
        connector.stop();
        System.out.println("Stopping the connection");

    }

    public static class SEPCPULLConnectorListener implements SEPCConnectorListener {
        public void notifyInitialDump(List<? extends Entity> entities) {
            System.out.println("initial dump started");
        }

        public void notifyEntityUpdates(EntityChangeBatch entityChangeBatch) {
            System.out
                    .println("Notifying entity update... entity changes size: " + entityChangeBatch.getEntityChanges());
        }
    }
}