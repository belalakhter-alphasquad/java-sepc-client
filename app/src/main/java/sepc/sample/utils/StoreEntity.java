package sepc.sample.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.agrona.concurrent.ShutdownSignalBarrier;
import com.betbrain.sepc.connector.sportsmodel.BettingOffer;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import com.betbrain.sepc.connector.sportsmodel.Sport;
import java.sql.SQLException;

import sepc.sample.DB.DbClient;

public class StoreEntity {
    ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
    static DbClient dbClient = DbClient.getInstance();
    private final ExecutorService executor;
    private final LinkedBlockingQueue<Entity> entityQueue;

    public StoreEntity(DbClient dbClient) {
        StoreEntity.dbClient = dbClient;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        this.entityQueue = new LinkedBlockingQueue<>();
        startProcessing();
        barrier.await();
        executor.shutdownNow();
    }

    public void queueEntity(Entity entity) {
        entityQueue.offer(entity);
    }

    private void startProcessing() {
        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Entity entity = entityQueue.take();
                    processEntity(entity);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void processEntity(Entity entity) {
        if (entity instanceof Sport) {
            Sport sport = (Sport) entity;
            try {
                dbClient.insertSport(sport);
            } catch (SQLException e) {
                System.err.println("Error inserting sport into the database: " + e.getMessage());
            }
        } else if (entity instanceof BettingOffer) {
            BettingOffer bettingOffer = (BettingOffer) entity;
            try {
                dbClient.insertBettingOffer(bettingOffer);
            } catch (SQLException e) {
                System.err.println("Error inserting BettingOffer into the database: " + e.getMessage());
            }
        }
    }

    public void shutdown() {
        executor.shutdownNow();
    }

}
