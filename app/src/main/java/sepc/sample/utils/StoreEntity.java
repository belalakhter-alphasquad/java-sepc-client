package sepc.sample.utils;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.agrona.concurrent.ShutdownSignalBarrier;
import java.util.concurrent.TimeUnit;

import com.betbrain.sepc.connector.sportsmodel.*;

import sepc.sample.DB.DbClient;

public class StoreEntity {

    ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
    static DbClient dbClient = DbClient.getInstance();
    private final ExecutorService executor;
    private final LinkedBlockingQueue<Entity> entityQueue;
    int Max_Size = 100000;

    public StoreEntity(DbClient dbClient, int numberOfThreads) {
        StoreEntity.dbClient = dbClient;
        this.entityQueue = new LinkedBlockingQueue<>(Max_Size);
        this.executor = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executor.execute(this::runConsumer);
        }

    }

    private void runConsumer() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Entity entity = entityQueue.take();
                processEntity(entity);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // Do something
        }
    }

    public void queueEntity(Entity entity) {
        try {
            entityQueue.put(entity);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processEntity(Entity entity) {

        if (entity instanceof Sport) {

            Sport sport = (Sport) entity;
            try {
                dbClient.insertSport(sport);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof BettingOffer) {
            BettingOffer bettingoffer = (BettingOffer) entity;
            try {
                dbClient.insertBettingOffer(bettingoffer);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof Event) {
            Event event = (Event) entity;
            try {
                dbClient.insertEvent(event);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventType) {
            EventType eventType = (EventType) entity;
            try {
                dbClient.insertEventType(eventType);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventStatus) {
            EventStatus eventStatus = (EventStatus) entity;
            try {
                dbClient.insertEventStatus(eventStatus);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventParticipantRelation) {
            EventParticipantRelation eventParticipantRelation = (EventParticipantRelation) entity;
            try {
                dbClient.insertEventParticipantRelation(eventParticipantRelation);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof Participant) {
            Participant participant = (Participant) entity;
            try {
                dbClient.insertParticipant(participant);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventCategory) {
            EventCategory eventCategory = (EventCategory) entity;
            try {
                dbClient.insertEventCategory(eventCategory);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventTemplate) {
            EventTemplate eventTemplate = (EventTemplate) entity;
            try {
                dbClient.insertEventTemplate(eventTemplate);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventPart) {
            EventPart eventPart = (EventPart) entity;
            try {
                dbClient.insertEventPart(eventPart);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventPartDefaultUsage) {
            EventPartDefaultUsage eventPartDefaultUsage = (EventPartDefaultUsage) entity;
            try {
                dbClient.insertEventPartDefaultUsage(eventPartDefaultUsage);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof StreamingProviderEventRelation) {
            StreamingProviderEventRelation streamingProviderEventRelation = (StreamingProviderEventRelation) entity;
            try {
                dbClient.insertStreamingProviderEventRelation(streamingProviderEventRelation);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof StreamingProvider) {
            StreamingProvider streamingProvider = (StreamingProvider) entity;
            try {
                dbClient.insertStreamingProvider(streamingProvider);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof ProviderEventRelation) {
            ProviderEventRelation providerEventRelation = (ProviderEventRelation) entity;
            try {
                dbClient.insertProviderEventRelation(providerEventRelation);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventParticipantRestriction) {
            EventParticipantRestriction eventParticipantRestriction = (EventParticipantRestriction) entity;
            try {
                dbClient.insertEventParticipantRestriction(eventParticipantRestriction);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof ParticipantRole) {
            ParticipantRole participantRole = (ParticipantRole) entity;
            try {
                dbClient.insertParticipantRole(participantRole);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof ParticipantUsage) {
            ParticipantUsage participantUsage = (ParticipantUsage) entity;
            try {
                dbClient.insertParticipantUsage(participantUsage);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof ParticipantRelation) {
            ParticipantRelation participantRelation = (ParticipantRelation) entity;
            try {
                dbClient.insertParticipantRelation(participantRelation);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof ParticipantRelationType) {
            ParticipantRelationType participantRelationType = (ParticipantRelationType) entity;
            try {
                dbClient.insertParticipantRelationType(participantRelationType);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof Participant) {
            Participant participant = (Participant) entity;
            try {
                dbClient.insertParticipant(participant);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventParticipantRelation) {
            EventParticipantRelation eventParticipantRelation = (EventParticipantRelation) entity;
            try {
                dbClient.insertEventParticipantRelation(eventParticipantRelation);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventActionTypeUsage) {
            EventActionTypeUsage eventActionTypeUsage = (EventActionTypeUsage) entity;
            try {
                dbClient.insertEventActionTypeUsage(eventActionTypeUsage);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventActionType) {
            EventActionType eventActionType = (EventActionType) entity;
            try {
                dbClient.insertEventActionType(eventActionType);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventParticipantInfoTypeUsage) {
            EventParticipantInfoTypeUsage eventParticipantInfoTypeUsage = (EventParticipantInfoTypeUsage) entity;
            try {
                dbClient.insertEventParticipantInfoTypeUsage(eventParticipantInfoTypeUsage);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventParticipantInfoDetailTypeUsage) {
            EventParticipantInfoDetailTypeUsage eventParticipantInfoDetailTypeUsage = (EventParticipantInfoDetailTypeUsage) entity;
            try {
                dbClient.insertEventParticipantInfoDetailTypeUsage(eventParticipantInfoDetailTypeUsage);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventActionDetailStatus) {
            EventActionDetailStatus eventActionDetailStatus = (EventActionDetailStatus) entity;
            try {
                dbClient.insertEventActionDetailStatus(eventActionDetailStatus);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventActionDetail) {
            EventActionDetail eventActionDetail = (EventActionDetail) entity;
            try {
                dbClient.insertEventActionDetail(eventActionDetail);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventActionDetailTypeUsage) {
            EventActionDetailTypeUsage eventActionDetailTypeUsage = (EventActionDetailTypeUsage) entity;
            try {
                dbClient.insertEventActionDetailTypeUsage(eventActionDetailTypeUsage);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventAction) {
            EventAction eventAction = (EventAction) entity;
            try {
                dbClient.insertEventAction(eventAction);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventActionStatus) {
            EventActionStatus eventActionStatus = (EventActionStatus) entity;
            try {
                dbClient.insertEventActionStatus(eventActionStatus);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventParticipantInfoStatus) {
            EventParticipantInfoStatus eventParticipantInfoStatus = (EventParticipantInfoStatus) entity;
            try {
                dbClient.insertEventParticipantInfoStatus(eventParticipantInfoStatus);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventInfoStatus) {
            EventInfoStatus eventInfoStatus = (EventInfoStatus) entity;
            try {
                dbClient.insertEventInfoStatus(eventInfoStatus);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventInfo) {
            EventInfo eventInfo = (EventInfo) entity;
            try {
                dbClient.insertEventInfo(eventInfo);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventInfoType) {
            EventInfoType eventInfoType = (EventInfoType) entity;
            try {
                dbClient.insertEventInfoType(eventInfoType);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventInfoTypeUsage) {
            EventInfoTypeUsage eventInfoTypeUsage = (EventInfoTypeUsage) entity;
            try {
                dbClient.insertEventInfoTypeUsage(eventInfoTypeUsage);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventParticipantInfo) {
            EventParticipantInfo eventParticipantInfo = (EventParticipantInfo) entity;
            try {
                dbClient.insertEventParticipantInfo(eventParticipantInfo);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventParticipantInfoDetail) {
            EventParticipantInfoDetail eventParticipantInfoDetail = (EventParticipantInfoDetail) entity;
            try {
                dbClient.insertEventParticipantInfoDetail(eventParticipantInfoDetail);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventParticipantInfoDetailStatus) {
            EventParticipantInfoDetailStatus eventParticipantInfoDetailStatus = (EventParticipantInfoDetailStatus) entity;
            try {
                dbClient.insertEventParticipantInfoDetailStatus(eventParticipantInfoDetailStatus);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof ParticipantRole) {
            ParticipantRole participantRole = (ParticipantRole) entity;
            try {
                dbClient.insertParticipantRole(participantRole);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof ParticipantUsage) {
            ParticipantUsage participantUsage = (ParticipantUsage) entity;
            try {
                dbClient.insertParticipantUsage(participantUsage);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof Participant) {
            Participant participant = (Participant) entity;
            try {
                dbClient.insertParticipant(participant);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventParticipantRelation) {
            EventParticipantRelation eventParticipantRelation = (EventParticipantRelation) entity;
            try {
                dbClient.insertEventParticipantRelation(eventParticipantRelation);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof ParticipantType) {
            ParticipantType participantType = (ParticipantType) entity;
            try {
                dbClient.insertParticipantType(participantType);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof ParticipantRelation) {
            ParticipantRelation participantRelation = (ParticipantRelation) entity;
            try {
                dbClient.insertParticipantRelation(participantRelation);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof ParticipantRelationType) {
            ParticipantRelationType participantRelationType = (ParticipantRelationType) entity;
            try {
                dbClient.insertParticipantRelationType(participantRelationType);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof Provider) {
            Provider provider = (Provider) entity;
            try {
                dbClient.insertProvider(provider);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof Source) {
            Source source = (Source) entity;
            try {
                dbClient.insertSource(source);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof BettingOfferStatus) {
            BettingOfferStatus bettingOfferStatus = (BettingOfferStatus) entity;
            try {
                dbClient.insertBettingOfferStatus(bettingOfferStatus);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof BettingType) {
            BettingType bettingType = (BettingType) entity;
            try {
                dbClient.insertBettingType(bettingType);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof Outcome) {
            Outcome outcome = (Outcome) entity;
            try {
                dbClient.insertOutcome(outcome);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof OutcomeType) {
            OutcomeType outcomeType = (OutcomeType) entity;
            try {
                dbClient.insertOutcomeType(outcomeType);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof OutcomeTypeBettingTypeRelation) {
            OutcomeTypeBettingTypeRelation outcomeTypeBettingTypeRelation = (OutcomeTypeBettingTypeRelation) entity;
            try {
                dbClient.insertOutcomeTypeBettingTypeRelation(outcomeTypeBettingTypeRelation);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof BettingTypeUsage) {
            BettingTypeUsage bettingTypeUsage = (BettingTypeUsage) entity;
            try {
                dbClient.insertBettingTypeUsage(bettingTypeUsage);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof OutcomeStatus) {
            OutcomeStatus outcomeStatus = (OutcomeStatus) entity;
            try {
                dbClient.insertOutcomeStatus(outcomeStatus);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof OutcomeTypeUsage) {
            OutcomeTypeUsage outcomeTypeUsage = (OutcomeTypeUsage) entity;
            try {
                dbClient.insertOutcomeTypeUsage(outcomeTypeUsage);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof Event) {
            Event event = (Event) entity;
            try {
                dbClient.insertEvent(event);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof EventPart) {
            EventPart eventPart = (EventPart) entity;
            try {
                dbClient.insertEventPart(eventPart);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof BettingType) {
            BettingType bettingType = (BettingType) entity;
            try {
                dbClient.insertBettingType(bettingType);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof Market) {
            Market market = (Market) entity;
            try {
                dbClient.insertMarket(market);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof Outcome) {
            Outcome outcome = (Outcome) entity;
            try {
                dbClient.insertOutcome(outcome);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof MarketOutcomeRelation) {
            MarketOutcomeRelation marketOutcomeRelation = (MarketOutcomeRelation) entity;
            try {
                dbClient.insertMarketOutcomeRelation(marketOutcomeRelation);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof Currency) {
            Currency currency = (Currency) entity;
            try {
                dbClient.insertCurrency(currency);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof Provider) {
            Provider provider = (Provider) entity;
            try {
                dbClient.insertProvider(provider);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof StreamingProvider) {
            StreamingProvider streamingProvider = (StreamingProvider) entity;
            try {
                dbClient.insertStreamingProvider(streamingProvider);
            } catch (SQLException e) {
                // Do something
            }
        } else if (entity instanceof ProviderEntityMapping) {
            ProviderEntityMapping providerEntityMapping = (ProviderEntityMapping) entity;
            try {
                dbClient.insertProviderEntityMapping(providerEntityMapping);
            } catch (SQLException e) {
                // do something
            }
        } else if (entity instanceof ScoringUnit) {
            ScoringUnit scoringunit = (ScoringUnit) entity;
            try {
                dbClient.insertScoringUnit(scoringunit);
            } catch (SQLException e) {
                // do something
            }
        } else if (entity instanceof LocationRelationType) {
            LocationRelationType locationRelationType = (LocationRelationType) entity;
            try {
                dbClient.insertLocationRelationType(locationRelationType);
            } catch (SQLException e) {
                // do something
            }
        } else if (entity instanceof LocationRelation) {
            LocationRelation locationRelation = (LocationRelation) entity;
            try {
                dbClient.insertLocationRelation(locationRelation);
            } catch (SQLException e) {
                // do something
            }
        } else if (entity instanceof LocationType) {
            LocationType locationType = (LocationType) entity;
            try {
                dbClient.insertLocationType(locationType);
            } catch (SQLException e) {
                // do something
            }
        } else if (entity instanceof Location) {
            Location location = (Location) entity;
            try {
                dbClient.insertLocation(location);
            } catch (SQLException e) {
                // do something
            }
        } else if (entity instanceof Translation) {
            Translation translation = (Translation) entity;
            try {
                dbClient.insertTranslation(translation);
            } catch (SQLException e) {
                // do something
            }
        } else if (entity instanceof EntityPropertyType) {
            EntityPropertyType entityPropertyType = (EntityPropertyType) entity;
            try {
                dbClient.insertEntityPropertyType(entityPropertyType);
            } catch (SQLException e) {
                // do something
            }
        } else if (entity instanceof EntityProperty) {
            EntityProperty entityProperty = (EntityProperty) entity;
            try {
                dbClient.insertEntityProperty(entityProperty);
            } catch (SQLException e) {
                // do something
            }
        } else if (entity instanceof EntityType) {
            EntityType entityType = (EntityType) entity;
            try {
                dbClient.insertEntityType(entityType);
            } catch (SQLException e) {
                // do something
            }
        } else if (entity instanceof EntityPropertyValue) {
            EntityPropertyValue entityPropertyValue = (EntityPropertyValue) entity;
            try {
                dbClient.insertEntityPropertyValue(entityPropertyValue);
            } catch (SQLException e) {
                // do something
            }
        }

    }

    public void shutdown() {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Executor did not terminate");
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
