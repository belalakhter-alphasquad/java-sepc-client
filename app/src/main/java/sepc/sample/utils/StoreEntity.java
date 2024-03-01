package sepc.sample.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.agrona.concurrent.ShutdownSignalBarrier;

import com.betbrain.sepc.connector.sportsmodel.*;

import java.sql.SQLException;

import sepc.sample.DB.DbClient;

public class StoreEntity {
    ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
    static DbClient dbClient = DbClient.getInstance();
    private final ExecutorService executor;
    private final LinkedBlockingQueue<Entity> entityQueue;

    public StoreEntity(DbClient dbClient) {
        StoreEntity.dbClient = dbClient;
        this.executor = Executors.newFixedThreadPool(6);

        this.entityQueue = new LinkedBlockingQueue<>();
        startProcessing();

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
            BettingOffer bettingoffer = (BettingOffer) entity;
            try {
                dbClient.insertBettingOffer(bettingoffer);
            } catch (SQLException e) {
                System.err.println("Error inserting BettingOffer into the database: " + e.getMessage());
            }
        } else if (entity instanceof Event) {
            Event event = (Event) entity;
            try {
                dbClient.insertEvent(event);
            } catch (SQLException e) {
                System.err.println("Error inserting Event into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventType) {
            EventType eventType = (EventType) entity;
            try {
                dbClient.insertEventType(eventType);
            } catch (SQLException e) {
                System.err.println("Error inserting EventType into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventStatus) {
            EventStatus eventStatus = (EventStatus) entity;
            try {
                dbClient.insertEventStatus(eventStatus);
            } catch (SQLException e) {
                System.err.println("Error inserting EventStatus into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantRelation) {
            EventParticipantRelation eventParticipantRelation = (EventParticipantRelation) entity;
            try {
                dbClient.insertEventParticipantRelation(eventParticipantRelation);
            } catch (SQLException e) {
                System.err.println("Error inserting Event into the database: " + e.getMessage());
            }
        } else if (entity instanceof Participant) {
            Participant participant = (Participant) entity;
            try {
                dbClient.insertParticipant(participant);
            } catch (SQLException e) {
                System.err.println("Error inserting Event into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventCategory) {
            EventCategory eventCategory = (EventCategory) entity;
            try {
                dbClient.insertEventCategory(eventCategory);
            } catch (SQLException e) {
                System.err.println("Error inserting Event into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventTemplate) {
            EventTemplate eventTemplate = (EventTemplate) entity;
            try {
                dbClient.insertEventTemplate(eventTemplate);
            } catch (SQLException e) {
                System.err.println("Error inserting Event into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventPart) {
            EventPart eventPart = (EventPart) entity;
            try {
                dbClient.insertEventPart(eventPart);
            } catch (SQLException e) {
                System.err.println("Error inserting EventPart into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventPartDefaultUsage) {
            EventPartDefaultUsage eventPartDefaultUsage = (EventPartDefaultUsage) entity;
            try {
                dbClient.insertEventPartDefaultUsage(eventPartDefaultUsage);
            } catch (SQLException e) {
                System.err.println("Error inserting Event into the database: " + e.getMessage());
            }
        } else if (entity instanceof StreamingProviderEventRelation) {
            StreamingProviderEventRelation streamingProviderEventRelation = (StreamingProviderEventRelation) entity;
            try {
                dbClient.insertStreamingProviderEventRelation(streamingProviderEventRelation);
            } catch (SQLException e) {
                System.err
                        .println("Error inserting StreamingProviderEventRelation into the database: " + e.getMessage());
            }
        } else if (entity instanceof StreamingProvider) {
            StreamingProvider streamingProvider = (StreamingProvider) entity;
            try {
                dbClient.insertStreamingProvider(streamingProvider);
            } catch (SQLException e) {
                System.err.println("Error inserting StreamingProvider into the database: " + e.getMessage());
            }
        } else if (entity instanceof ProviderEventRelation) {
            ProviderEventRelation providerEventRelation = (ProviderEventRelation) entity;
            try {
                dbClient.insertProviderEventRelation(providerEventRelation);
            } catch (SQLException e) {
                System.err.println("Error inserting ProviderEventRelation into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantRestriction) {
            EventParticipantRestriction eventParticipantRestriction = (EventParticipantRestriction) entity;
            try {
                dbClient.insertEventParticipantRestriction(eventParticipantRestriction);
            } catch (SQLException e) {
                System.err.println("Error inserting EventParticipantRestriction into the database: " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRole) {
            ParticipantRole participantRole = (ParticipantRole) entity;
            try {
                dbClient.insertParticipantRole(participantRole);
            } catch (SQLException e) {
                System.err.println("Error inserting ParticipantRole into the database: " + e.getMessage());
            }
        } else if (entity instanceof ParticipantUsage) {
            ParticipantUsage participantUsage = (ParticipantUsage) entity;
            try {
                dbClient.insertParticipantUsage(participantUsage);
            } catch (SQLException e) {
                System.err.println("Error inserting ParticipantUsage into the database: " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRelation) {
            ParticipantRelation participantRelation = (ParticipantRelation) entity;
            try {
                dbClient.insertParticipantRelation(participantRelation);
            } catch (SQLException e) {
                System.err.println("Error inserting ParticipantRelation into the database: " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRelationType) {
            ParticipantRelationType participantRelationType = (ParticipantRelationType) entity;
            try {
                dbClient.insertParticipantRelationType(participantRelationType);
            } catch (SQLException e) {
                System.err.println("Error inserting ParticipantRelationType into the database: " + e.getMessage());
            }
        } else if (entity instanceof Participant) {
            Participant participant = (Participant) entity;
            try {
                dbClient.insertParticipant(participant);
            } catch (SQLException e) {
                System.err.println("Error inserting Participant into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantRelation) {
            EventParticipantRelation eventParticipantRelation = (EventParticipantRelation) entity;
            try {
                dbClient.insertEventParticipantRelation(eventParticipantRelation);
            } catch (SQLException e) {
                System.err.println("Error inserting EventParticipantRelation into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventActionTypeUsage) {
            EventActionTypeUsage eventActionTypeUsage = (EventActionTypeUsage) entity;
            try {
                dbClient.insertEventActionTypeUsage(eventActionTypeUsage);
            } catch (SQLException e) {
                System.err.println("Error inserting EventActionTypeUsage into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventActionType) {
            EventActionType eventActionType = (EventActionType) entity;
            try {
                dbClient.insertEventActionType(eventActionType);
            } catch (SQLException e) {
                System.err.println("Error inserting EventActionType into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoTypeUsage) {
            EventParticipantInfoTypeUsage eventParticipantInfoTypeUsage = (EventParticipantInfoTypeUsage) entity;
            try {
                dbClient.insertEventParticipantInfoTypeUsage(eventParticipantInfoTypeUsage);
            } catch (SQLException e) {
                System.err
                        .println("Error inserting EventParticipantInfoTypeUsage into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoDetailTypeUsage) {
            EventParticipantInfoDetailTypeUsage eventParticipantInfoDetailTypeUsage = (EventParticipantInfoDetailTypeUsage) entity;
            try {
                dbClient.insertEventParticipantInfoDetailTypeUsage(eventParticipantInfoDetailTypeUsage);
            } catch (SQLException e) {
                System.err.println(
                        "Error inserting EventParticipantInfoDetailTypeUsage into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventActionDetailStatus) {
            EventActionDetailStatus eventActionDetailStatus = (EventActionDetailStatus) entity;
            try {
                dbClient.insertEventActionDetailStatus(eventActionDetailStatus);
            } catch (SQLException e) {
                System.err.println("Error inserting EventActionDetailStatus into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventActionDetail) {
            EventActionDetail eventActionDetail = (EventActionDetail) entity;
            try {
                dbClient.insertEventActionDetail(eventActionDetail);
            } catch (SQLException e) {
                System.err.println("Error inserting EventActionDetail into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventActionDetailTypeUsage) {
            EventActionDetailTypeUsage eventActionDetailTypeUsage = (EventActionDetailTypeUsage) entity;
            try {
                dbClient.insertEventActionDetailTypeUsage(eventActionDetailTypeUsage);
            } catch (SQLException e) {
                System.err.println("Error inserting EventActionDetailTypeUsage into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventAction) {
            EventAction eventAction = (EventAction) entity;
            try {
                dbClient.insertEventAction(eventAction);
            } catch (SQLException e) {
                System.err.println("Error inserting EventAction into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventActionStatus) {
            EventActionStatus eventActionStatus = (EventActionStatus) entity;
            try {
                dbClient.insertEventActionStatus(eventActionStatus);
            } catch (SQLException e) {
                System.err.println("Error inserting EventActionStatus into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoStatus) {
            EventParticipantInfoStatus eventParticipantInfoStatus = (EventParticipantInfoStatus) entity;
            try {
                dbClient.insertEventParticipantInfoStatus(eventParticipantInfoStatus);
            } catch (SQLException e) {
                System.err.println("Error inserting EventParticipantInfoStatus into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventInfoStatus) {
            EventInfoStatus eventInfoStatus = (EventInfoStatus) entity;
            try {
                dbClient.insertEventInfoStatus(eventInfoStatus);
            } catch (SQLException e) {
                System.err.println("Error inserting EventInfoStatus into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventInfo) {
            EventInfo eventInfo = (EventInfo) entity;
            try {
                dbClient.insertEventInfo(eventInfo);
            } catch (SQLException e) {
                System.err.println("Error inserting EventInfo into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventInfoType) {
            EventInfoType eventInfoType = (EventInfoType) entity;
            try {
                dbClient.insertEventInfoType(eventInfoType);
            } catch (SQLException e) {
                System.err.println("Error inserting EventInfoType into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventInfoTypeUsage) {
            EventInfoTypeUsage eventInfoTypeUsage = (EventInfoTypeUsage) entity;
            try {
                dbClient.insertEventInfoTypeUsage(eventInfoTypeUsage);
            } catch (SQLException e) {
                System.err.println("Error inserting EventInfoTypeUsage into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfo) {
            EventParticipantInfo eventParticipantInfo = (EventParticipantInfo) entity;
            try {
                dbClient.insertEventParticipantInfo(eventParticipantInfo);
            } catch (SQLException e) {
                System.err.println("Error inserting EventParticipantInfo into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoDetail) {
            EventParticipantInfoDetail eventParticipantInfoDetail = (EventParticipantInfoDetail) entity;
            try {
                dbClient.insertEventParticipantInfoDetail(eventParticipantInfoDetail);
            } catch (SQLException e) {
                System.err.println("Error inserting EventParticipantInfoDetail into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoDetailStatus) {
            EventParticipantInfoDetailStatus eventParticipantInfoDetailStatus = (EventParticipantInfoDetailStatus) entity;
            try {
                dbClient.insertEventParticipantInfoDetailStatus(eventParticipantInfoDetailStatus);
            } catch (SQLException e) {
                System.err.println(
                        "Error inserting EventParticipantInfoDetailStatus into the database: " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRole) {
            ParticipantRole participantRole = (ParticipantRole) entity;
            try {
                dbClient.insertParticipantRole(participantRole);
            } catch (SQLException e) {
                System.err.println("Error inserting ParticipantRole into the database: " + e.getMessage());
            }
        } else if (entity instanceof ParticipantUsage) {
            ParticipantUsage participantUsage = (ParticipantUsage) entity;
            try {
                dbClient.insertParticipantUsage(participantUsage);
            } catch (SQLException e) {
                System.err.println("Error inserting ParticipantUsage into the database: " + e.getMessage());
            }
        } else if (entity instanceof Participant) {
            Participant participant = (Participant) entity;
            try {
                dbClient.insertParticipant(participant);
            } catch (SQLException e) {
                System.err.println("Error inserting Participant into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantRelation) {
            EventParticipantRelation eventParticipantRelation = (EventParticipantRelation) entity;
            try {
                dbClient.insertEventParticipantRelation(eventParticipantRelation);
            } catch (SQLException e) {
                System.err.println("Error inserting EventParticipantRelation into the database: " + e.getMessage());
            }
        } else if (entity instanceof ParticipantType) {
            ParticipantType participantType = (ParticipantType) entity;
            try {
                dbClient.insertParticipantType(participantType);
            } catch (SQLException e) {
                System.err.println("Error inserting ParticipantType into the database: " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRelation) {
            ParticipantRelation participantRelation = (ParticipantRelation) entity;
            try {
                dbClient.insertParticipantRelation(participantRelation);
            } catch (SQLException e) {
                System.err.println("Error inserting ParticipantRelation into the database: " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRelationType) {
            ParticipantRelationType participantRelationType = (ParticipantRelationType) entity;
            try {
                dbClient.insertParticipantRelationType(participantRelationType);
            } catch (SQLException e) {
                System.err.println("Error inserting ParticipantRelationType into the database: " + e.getMessage());
            }
        } else if (entity instanceof Provider) {
            Provider provider = (Provider) entity;
            try {
                dbClient.insertProvider(provider);
            } catch (SQLException e) {
                System.err.println("Error inserting Provider into the database: " + e.getMessage());
            }
        } else if (entity instanceof Source) {
            Source source = (Source) entity;
            try {
                dbClient.insertSource(source);
            } catch (SQLException e) {
                System.err.println("Error inserting Source into the database: " + e.getMessage());
            }
        } else if (entity instanceof BettingOfferStatus) {
            BettingOfferStatus bettingOfferStatus = (BettingOfferStatus) entity;
            try {
                dbClient.insertBettingOfferStatus(bettingOfferStatus);
            } catch (SQLException e) {
                System.err.println("Error inserting BettingOfferStatus into the database: " + e.getMessage());
            }
        } else if (entity instanceof BettingType) {
            BettingType bettingType = (BettingType) entity;
            try {
                dbClient.insertBettingType(bettingType);
            } catch (SQLException e) {
                System.err.println("Error inserting BettingType into the database: " + e.getMessage());
            }
        } else if (entity instanceof Outcome) {
            Outcome outcome = (Outcome) entity;
            try {
                dbClient.insertOutcome(outcome);
            } catch (SQLException e) {
                System.err.println("Error inserting Outcome into the database: " + e.getMessage());
            }
        } else if (entity instanceof OutcomeType) {
            OutcomeType outcomeType = (OutcomeType) entity;
            try {
                dbClient.insertOutcomeType(outcomeType);
            } catch (SQLException e) {
                System.err.println("Error inserting OutcomeType into the database: " + e.getMessage());
            }
        } else if (entity instanceof OutcomeTypeBettingTypeRelation) {
            OutcomeTypeBettingTypeRelation outcomeTypeBettingTypeRelation = (OutcomeTypeBettingTypeRelation) entity;
            try {
                dbClient.insertOutcomeTypeBettingTypeRelation(outcomeTypeBettingTypeRelation);
            } catch (SQLException e) {
                System.err
                        .println("Error inserting OutcomeTypeBettingTypeRelation into the database: " + e.getMessage());
            }
        } else if (entity instanceof BettingTypeUsage) {
            BettingTypeUsage bettingTypeUsage = (BettingTypeUsage) entity;
            try {
                dbClient.insertBettingTypeUsage(bettingTypeUsage);
            } catch (SQLException e) {
                System.err.println("Error inserting BettingTypeUsage into the database: " + e.getMessage());
            }
        } else if (entity instanceof OutcomeStatus) {
            OutcomeStatus outcomeStatus = (OutcomeStatus) entity;
            try {
                dbClient.insertOutcomeStatus(outcomeStatus);
            } catch (SQLException e) {
                System.err.println("Error inserting OutcomeStatus into the database: " + e.getMessage());
            }
        } else if (entity instanceof OutcomeTypeUsage) {
            OutcomeTypeUsage outcomeTypeUsage = (OutcomeTypeUsage) entity;
            try {
                dbClient.insertOutcomeTypeUsage(outcomeTypeUsage);
            } catch (SQLException e) {
                System.err.println("Error inserting OutcomeTypeUsage into the database: " + e.getMessage());
            }
        } else if (entity instanceof Event) {
            Event event = (Event) entity;
            try {
                dbClient.insertEvent(event);
            } catch (SQLException e) {
                System.err.println("Error inserting Event into the database: " + e.getMessage());
            }
        } else if (entity instanceof EventPart) {
            EventPart eventPart = (EventPart) entity;
            try {
                dbClient.insertEventPart(eventPart);
            } catch (SQLException e) {
                System.err.println("Error inserting EventPart into the database: " + e.getMessage());
            }
        } else if (entity instanceof BettingType) {
            BettingType bettingType = (BettingType) entity;
            try {
                dbClient.insertBettingType(bettingType);
            } catch (SQLException e) {
                System.err.println("Error inserting BettingType into the database: " + e.getMessage());
            }
        } else if (entity instanceof Market) {
            Market market = (Market) entity;
            try {
                dbClient.insertMarket(market);
            } catch (SQLException e) {
                System.err.println("Error inserting Market into the database: " + e.getMessage());
            }
        } else if (entity instanceof Outcome) {
            Outcome outcome = (Outcome) entity;
            try {
                dbClient.insertOutcome(outcome);
            } catch (SQLException e) {
                System.err.println("Error inserting Outcome into the database: " + e.getMessage());
            }
        } else if (entity instanceof MarketOutcomeRelation) {
            MarketOutcomeRelation marketOutcomeRelation = (MarketOutcomeRelation) entity;
            try {
                dbClient.insertMarketOutcomeRelation(marketOutcomeRelation);
            } catch (SQLException e) {
                System.err.println("Error inserting MarketOutcomeRelation into the database: " + e.getMessage());
            }
        } else if (entity instanceof Currency) {
            Currency currency = (Currency) entity;
            try {
                dbClient.insertCurrency(currency);
            } catch (SQLException e) {
                System.err.println("Error inserting Currency into the database: " + e.getMessage());
            }
        } else if (entity instanceof Provider) {
            Provider provider = (Provider) entity;
            try {
                dbClient.insertProvider(provider);
            } catch (SQLException e) {
                System.err.println("Error inserting Provider into the database: " + e.getMessage());
            }
        } else if (entity instanceof StreamingProvider) {
            StreamingProvider streamingProvider = (StreamingProvider) entity;
            try {
                dbClient.insertStreamingProvider(streamingProvider);
            } catch (SQLException e) {
                System.err.println("Error inserting StreamProvider into the database: " + e.getMessage());
            }
        } else if (entity instanceof ProviderEntityMapping) {
            ProviderEntityMapping providerEntityMapping = (ProviderEntityMapping) entity;
            try {
                dbClient.insertProviderEntityMapping(providerEntityMapping);
            } catch (SQLException e) {
                System.err.println("Error inserting ProviderEntityMapping into the database: " + e.getMessage());
            }
        }

    }

    public void shutdown() {
        executor.shutdownNow();
    }

}
