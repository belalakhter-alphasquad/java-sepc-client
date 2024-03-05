package sepc.sample.utils;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.betbrain.sepc.connector.sportsmodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sepc.sample.DB.DbClient;

public class StoreEntity {
    private static final Logger logger = LoggerFactory.getLogger(StoreEntity.class);
    private final ExecutorService executorService;

    static DbClient dbClient = DbClient.getInstance();
    private final LinkedBlockingQueue<Entity> entityQueue = new LinkedBlockingQueue<>();

    public StoreEntity(DbClient dbClient) {
        StoreEntity.dbClient = dbClient;
        this.executorService = Executors.newFixedThreadPool(4);
        // startProcessing();

    }

    private void startProcessing() {
        for (int i = 0; i < 4; i++) {

            executorService.submit(() -> {

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Entity entity = entityQueue.take();
                        processEntity(entity);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        System.err.println("Error processing entity: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public void queueEntity(Entity entity) {
        try {
            entityQueue.put(entity);
        } catch (InterruptedException e) {
            System.out.println("Block this crazy thread");
        }

    }

    public void processEntity(Entity entity) {

        if (entity instanceof Sport) {

            Sport sport = (Sport) entity;
            try {
                dbClient.insertSport(sport);
            } catch (SQLException e) {
                logger.info("Error sport : " + e.getMessage());

            }
        } else if (entity instanceof BettingOffer) {
            BettingOffer bettingoffer = (BettingOffer) entity;
            try {
                dbClient.insertBettingOffer(bettingoffer);
            } catch (SQLException e) {
                logger.info("Error  : " + e.getMessage());

            }
        } else if (entity instanceof EventType) {
            EventType eventType = (EventType) entity;
            try {
                dbClient.insertEventType(eventType);
            } catch (SQLException e) {
                logger.info("Error  eventType: " + e.getMessage());
            }
        } else if (entity instanceof EventStatus) {
            EventStatus eventStatus = (EventStatus) entity;
            try {
                dbClient.insertEventStatus(eventStatus);
            } catch (SQLException e) {
                logger.info("Error eventStatus : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantRelation) {
            EventParticipantRelation eventParticipantRelation = (EventParticipantRelation) entity;
            try {
                dbClient.insertEventParticipantRelation(eventParticipantRelation);
            } catch (SQLException e) {
                logger.info("Error  eventParticipantRelation: " + e.getMessage());
            }
        } else if (entity instanceof Participant) {
            Participant participant = (Participant) entity;
            try {
                dbClient.insertParticipant(participant);
            } catch (SQLException e) {
                logger.info("Error  participant: " + e.getMessage());
            }
        } else if (entity instanceof EventCategory) {
            EventCategory eventCategory = (EventCategory) entity;
            try {
                dbClient.insertEventCategory(eventCategory);
            } catch (SQLException e) {
                logger.info("Error eventCategory : " + e.getMessage());
            }
        } else if (entity instanceof EventTemplate) {
            EventTemplate eventTemplate = (EventTemplate) entity;
            try {
                dbClient.insertEventTemplate(eventTemplate);
            } catch (SQLException e) {
                logger.info("Error  eventTemplate: " + e.getMessage());
            }
        } else if (entity instanceof EventPart) {
            EventPart eventPart = (EventPart) entity;
            try {
                dbClient.insertEventPart(eventPart);
            } catch (SQLException e) {
                logger.info("Error eventPart : " + e.getMessage());
            }
        } else if (entity instanceof EventPartDefaultUsage) {
            EventPartDefaultUsage eventPartDefaultUsage = (EventPartDefaultUsage) entity;
            try {
                dbClient.insertEventPartDefaultUsage(eventPartDefaultUsage);
            } catch (SQLException e) {
                logger.info("Error eventPartDefaultUsage : " + e.getMessage());
            }
        } else if (entity instanceof StreamingProviderEventRelation) {
            StreamingProviderEventRelation streamingProviderEventRelation = (StreamingProviderEventRelation) entity;
            try {
                dbClient.insertStreamingProviderEventRelation(streamingProviderEventRelation);
            } catch (SQLException e) {
                logger.info("Error streamingProviderEventRelation : " + e.getMessage());
            }
        } else if (entity instanceof StreamingProvider) {
            StreamingProvider streamingProvider = (StreamingProvider) entity;
            try {
                dbClient.insertStreamingProvider(streamingProvider);
            } catch (SQLException e) {
                logger.info("Error streamingProvider : " + e.getMessage());
            }
        } else if (entity instanceof ProviderEventRelation) {
            ProviderEventRelation providerEventRelation = (ProviderEventRelation) entity;
            try {
                dbClient.insertProviderEventRelation(providerEventRelation);
            } catch (SQLException e) {
                logger.info("Error providerEventRelation : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantRestriction) {
            EventParticipantRestriction eventParticipantRestriction = (EventParticipantRestriction) entity;
            try {
                dbClient.insertEventParticipantRestriction(eventParticipantRestriction);
            } catch (SQLException e) {
                logger.info("Error  eventParticipantRestriction: " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRole) {
            ParticipantRole participantRole = (ParticipantRole) entity;
            try {
                dbClient.insertParticipantRole(participantRole);
            } catch (SQLException e) {
                logger.info("Error participantRole : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantUsage) {
            ParticipantUsage participantUsage = (ParticipantUsage) entity;
            try {
                dbClient.insertParticipantUsage(participantUsage);
            } catch (SQLException e) {
                logger.info("Error participantUsage : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRelation) {
            ParticipantRelation participantRelation = (ParticipantRelation) entity;
            try {
                dbClient.insertParticipantRelation(participantRelation);
            } catch (SQLException e) {
                logger.info("Error participantRelation : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRelationType) {
            ParticipantRelationType participantRelationType = (ParticipantRelationType) entity;
            try {
                dbClient.insertParticipantRelationType(participantRelationType);
            } catch (SQLException e) {
                logger.info("Error participantRelationType : " + e.getMessage());
            }
        } else if (entity instanceof Participant) {
            Participant participant = (Participant) entity;
            try {
                dbClient.insertParticipant(participant);
            } catch (SQLException e) {
                logger.info("Error participant : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantRelation) {
            EventParticipantRelation eventParticipantRelation = (EventParticipantRelation) entity;
            try {
                dbClient.insertEventParticipantRelation(eventParticipantRelation);
            } catch (SQLException e) {
                logger.info("Error eventParticipantRelation : " + e.getMessage());
            }
        } else if (entity instanceof EventActionTypeUsage) {
            EventActionTypeUsage eventActionTypeUsage = (EventActionTypeUsage) entity;
            try {
                dbClient.insertEventActionTypeUsage(eventActionTypeUsage);
            } catch (SQLException e) {
                logger.info("Error eventActionTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof EventActionType) {
            EventActionType eventActionType = (EventActionType) entity;
            try {
                dbClient.insertEventActionType(eventActionType);
            } catch (SQLException e) {
                logger.info("Error eventActionType : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoTypeUsage) {
            EventParticipantInfoTypeUsage eventParticipantInfoTypeUsage = (EventParticipantInfoTypeUsage) entity;
            try {
                dbClient.insertEventParticipantInfoTypeUsage(eventParticipantInfoTypeUsage);
            } catch (SQLException e) {
                logger.info("Error eventParticipantInfoTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoDetailTypeUsage) {
            EventParticipantInfoDetailTypeUsage eventParticipantInfoDetailTypeUsage = (EventParticipantInfoDetailTypeUsage) entity;
            try {
                dbClient.insertEventParticipantInfoDetailTypeUsage(eventParticipantInfoDetailTypeUsage);
            } catch (SQLException e) {
                logger.info("Error eventParticipantInfoDetailTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof EventActionDetailStatus) {
            EventActionDetailStatus eventActionDetailStatus = (EventActionDetailStatus) entity;
            try {
                dbClient.insertEventActionDetailStatus(eventActionDetailStatus);
            } catch (SQLException e) {
                logger.info("Error eventActionDetailStatus  : " + e.getMessage());
            }
        } else if (entity instanceof EventActionDetail) {
            EventActionDetail eventActionDetail = (EventActionDetail) entity;
            try {
                dbClient.insertEventActionDetail(eventActionDetail);
            } catch (SQLException e) {
                logger.info("Error  eventActionDetail: " + e.getMessage());
            }
        } else if (entity instanceof EventActionDetailTypeUsage) {
            EventActionDetailTypeUsage eventActionDetailTypeUsage = (EventActionDetailTypeUsage) entity;
            try {
                dbClient.insertEventActionDetailTypeUsage(eventActionDetailTypeUsage);
            } catch (SQLException e) {
                logger.info("Error  eventActionDetailTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof EventAction) {
            EventAction eventAction = (EventAction) entity;
            try {
                dbClient.insertEventAction(eventAction);
            } catch (SQLException e) {
                logger.info("Error  eventAction : " + e.getMessage());
            }
        } else if (entity instanceof EventActionStatus) {
            EventActionStatus eventActionStatus = (EventActionStatus) entity;
            try {
                dbClient.insertEventActionStatus(eventActionStatus);
            } catch (SQLException e) {
                logger.info("Error  eventActionStatus : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoStatus) {
            EventParticipantInfoStatus eventParticipantInfoStatus = (EventParticipantInfoStatus) entity;
            try {
                dbClient.insertEventParticipantInfoStatus(eventParticipantInfoStatus);
            } catch (SQLException e) {
                logger.info("Error  eventParticipantInfoStatus : " + e.getMessage());
            }
        } else if (entity instanceof EventInfoStatus) {
            EventInfoStatus eventInfoStatus = (EventInfoStatus) entity;
            try {
                dbClient.insertEventInfoStatus(eventInfoStatus);
            } catch (SQLException e) {
                logger.info("Error  eventInfoStatus : " + e.getMessage());
            }
        } else if (entity instanceof EventInfo) {
            EventInfo eventInfo = (EventInfo) entity;
            try {
                dbClient.insertEventInfo(eventInfo);
            } catch (SQLException e) {
                logger.info("Error  eventInfo : " + e.getMessage());
            }
        } else if (entity instanceof EventInfoType) {
            EventInfoType eventInfoType = (EventInfoType) entity;
            try {
                dbClient.insertEventInfoType(eventInfoType);
            } catch (SQLException e) {
                logger.info("Error eventInfoType : " + e.getMessage());
            }
        } else if (entity instanceof EventInfoTypeUsage) {
            EventInfoTypeUsage eventInfoTypeUsage = (EventInfoTypeUsage) entity;
            try {
                dbClient.insertEventInfoTypeUsage(eventInfoTypeUsage);
            } catch (SQLException e) {
                logger.info("Error  eventInfoTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfo) {
            EventParticipantInfo eventParticipantInfo = (EventParticipantInfo) entity;
            try {
                dbClient.insertEventParticipantInfo(eventParticipantInfo);
            } catch (SQLException e) {
                logger.info("Error  eventParticipantInfo : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoDetail) {
            EventParticipantInfoDetail eventParticipantInfoDetail = (EventParticipantInfoDetail) entity;
            try {
                dbClient.insertEventParticipantInfoDetail(eventParticipantInfoDetail);
            } catch (SQLException e) {
                logger.info("Error  eventParticipantInfoDetail : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoDetailStatus) {
            EventParticipantInfoDetailStatus eventParticipantInfoDetailStatus = (EventParticipantInfoDetailStatus) entity;
            try {
                dbClient.insertEventParticipantInfoDetailStatus(eventParticipantInfoDetailStatus);
            } catch (SQLException e) {
                logger.info("Error  eventParticipantInfoDetailStatus : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRole) {
            ParticipantRole participantRole = (ParticipantRole) entity;
            try {
                dbClient.insertParticipantRole(participantRole);
            } catch (SQLException e) {
                logger.info("Error participantRole  : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantUsage) {
            ParticipantUsage participantUsage = (ParticipantUsage) entity;
            try {
                dbClient.insertParticipantUsage(participantUsage);
            } catch (SQLException e) {
                logger.info("Error  participantUsage : " + e.getMessage());
            }
        } else if (entity instanceof Participant) {
            Participant participant = (Participant) entity;
            try {
                dbClient.insertParticipant(participant);
            } catch (SQLException e) {
                logger.info("Error  participant : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantRelation) {
            EventParticipantRelation eventParticipantRelation = (EventParticipantRelation) entity;
            try {
                dbClient.insertEventParticipantRelation(eventParticipantRelation);
            } catch (SQLException e) {
                logger.info("Error  eventParticipantRelation : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantType) {
            ParticipantType participantType = (ParticipantType) entity;
            try {
                dbClient.insertParticipantType(participantType);
            } catch (SQLException e) {
                logger.info("Error  participantType : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRelation) {
            ParticipantRelation participantRelation = (ParticipantRelation) entity;
            try {
                dbClient.insertParticipantRelation(participantRelation);
            } catch (SQLException e) {
                logger.info("Error  participantRelation : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRelationType) {
            ParticipantRelationType participantRelationType = (ParticipantRelationType) entity;
            try {
                dbClient.insertParticipantRelationType(participantRelationType);
            } catch (SQLException e) {
                logger.info("Error  participantRelationType : " + e.getMessage());
            }
        } else if (entity instanceof Provider) {
            Provider provider = (Provider) entity;
            try {
                dbClient.insertProvider(provider);
            } catch (SQLException e) {
                logger.info("Error  provider : " + e.getMessage());
            }
        } else if (entity instanceof Source) {
            Source source = (Source) entity;
            try {
                dbClient.insertSource(source);
            } catch (SQLException e) {
                logger.info("Error  source : " + e.getMessage());
            }
        } else if (entity instanceof BettingOfferStatus) {
            BettingOfferStatus bettingOfferStatus = (BettingOfferStatus) entity;
            try {
                dbClient.insertBettingOfferStatus(bettingOfferStatus);
            } catch (SQLException e) {
                logger.info("Error bettingOfferStatus  : " + e.getMessage());
            }
        } else if (entity instanceof BettingType) {
            BettingType bettingType = (BettingType) entity;
            try {
                dbClient.insertBettingType(bettingType);
            } catch (SQLException e) {
                logger.info("Error bettingType  : " + e.getMessage());
            }
        } else if (entity instanceof Outcome) {
            Outcome outcome = (Outcome) entity;
            try {
                dbClient.insertOutcome(outcome);
            } catch (SQLException e) {
                logger.info("Error outcome  : " + e.getMessage());
            }
        } else if (entity instanceof OutcomeType) {
            OutcomeType outcomeType = (OutcomeType) entity;
            try {
                dbClient.insertOutcomeType(outcomeType);
            } catch (SQLException e) {
                logger.info("Error  outcomeType : " + e.getMessage());
            }
        } else if (entity instanceof OutcomeTypeBettingTypeRelation) {
            OutcomeTypeBettingTypeRelation outcomeTypeBettingTypeRelation = (OutcomeTypeBettingTypeRelation) entity;
            try {
                dbClient.insertOutcomeTypeBettingTypeRelation(outcomeTypeBettingTypeRelation);
            } catch (SQLException e) {
                logger.info("Error  outcomeTypeBettingTypeRelation : " + e.getMessage());
            }
        } else if (entity instanceof BettingTypeUsage) {
            BettingTypeUsage bettingTypeUsage = (BettingTypeUsage) entity;
            try {
                dbClient.insertBettingTypeUsage(bettingTypeUsage);
            } catch (SQLException e) {
                logger.info("Error  bettingTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof OutcomeStatus) {
            OutcomeStatus outcomeStatus = (OutcomeStatus) entity;
            try {
                dbClient.insertOutcomeStatus(outcomeStatus);
            } catch (SQLException e) {
                logger.info("Error  outcomeStatus : " + e.getMessage());
            }
        } else if (entity instanceof OutcomeTypeUsage) {
            OutcomeTypeUsage outcomeTypeUsage = (OutcomeTypeUsage) entity;
            try {
                dbClient.insertOutcomeTypeUsage(outcomeTypeUsage);
            } catch (SQLException e) {
                logger.info("Error  outcomeTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof Event) {
            Event event = (Event) entity;
            try {
                dbClient.insertEvent(event);
            } catch (SQLException e) {
                logger.info("Error  event : " + e.getMessage());
            }
        } else if (entity instanceof EventPart) {
            EventPart eventPart = (EventPart) entity;
            try {
                dbClient.insertEventPart(eventPart);
            } catch (SQLException e) {
                logger.info("Error  eventPart : " + e.getMessage());
            }
        } else if (entity instanceof BettingType) {
            BettingType bettingType = (BettingType) entity;
            try {
                dbClient.insertBettingType(bettingType);
            } catch (SQLException e) {
                logger.info("Error  bettingType : " + e.getMessage());
            }
        } else if (entity instanceof Market) {
            Market market = (Market) entity;
            try {
                dbClient.insertMarket(market);
            } catch (SQLException e) {
                logger.info("Error market  : " + e.getMessage());
            }
        } else if (entity instanceof Outcome) {
            Outcome outcome = (Outcome) entity;
            try {
                dbClient.insertOutcome(outcome);
            } catch (SQLException e) {
                logger.info("Error  outcome : " + e.getMessage());
            }
        } else if (entity instanceof MarketOutcomeRelation) {
            MarketOutcomeRelation marketOutcomeRelation = (MarketOutcomeRelation) entity;
            try {
                dbClient.insertMarketOutcomeRelation(marketOutcomeRelation);
            } catch (SQLException e) {
                logger.info("Error  marketOutcomeRelation : " + e.getMessage());
            }
        } else if (entity instanceof Currency) {
            Currency currency = (Currency) entity;
            try {
                dbClient.insertCurrency(currency);
            } catch (SQLException e) {
                logger.info("Error  currency : " + e.getMessage());
            }
        } else if (entity instanceof Provider) {
            Provider provider = (Provider) entity;
            try {
                dbClient.insertProvider(provider);
            } catch (SQLException e) {
                logger.info("Error  provider : " + e.getMessage());
            }
        } else if (entity instanceof StreamingProvider) {
            StreamingProvider streamingProvider = (StreamingProvider) entity;
            try {
                dbClient.insertStreamingProvider(streamingProvider);
            } catch (SQLException e) {
                logger.info("Error streamingProvider  : " + e.getMessage());
            }
        } else if (entity instanceof ProviderEntityMapping) {
            ProviderEntityMapping providerEntityMapping = (ProviderEntityMapping) entity;
            try {
                dbClient.insertProviderEntityMapping(providerEntityMapping);
            } catch (SQLException e) {
                logger.info("Error  providerEntityMapping : " + e.getMessage());
            }
        } else if (entity instanceof ScoringUnit) {
            ScoringUnit scoringunit = (ScoringUnit) entity;
            try {
                dbClient.insertScoringUnit(scoringunit);
            } catch (SQLException e) {
                logger.info("Error  scoringunit : " + e.getMessage());
            }
        } else if (entity instanceof LocationRelationType) {
            LocationRelationType locationRelationType = (LocationRelationType) entity;
            try {
                dbClient.insertLocationRelationType(locationRelationType);
            } catch (SQLException e) {
                logger.info("Error  locationRelationType : " + e.getMessage());
            }
        } else if (entity instanceof LocationRelation) {
            LocationRelation locationRelation = (LocationRelation) entity;
            try {
                dbClient.insertLocationRelation(locationRelation);
            } catch (SQLException e) {
                logger.info("Error  locationRelation : " + e.getMessage());
            }
        } else if (entity instanceof LocationType) {
            LocationType locationType = (LocationType) entity;
            try {
                dbClient.insertLocationType(locationType);
            } catch (SQLException e) {
                logger.info("Error  locationType : " + e.getMessage());
            }
        } else if (entity instanceof Location) {
            Location location = (Location) entity;
            try {
                dbClient.insertLocation(location);
            } catch (SQLException e) {
                logger.info("Error   location: " + e.getMessage());
            }
        } else if (entity instanceof Translation) {
            Translation translation = (Translation) entity;
            try {
                dbClient.insertTranslation(translation);
            } catch (SQLException e) {
                logger.info("Error  translation : " + e.getMessage());
            }
        } else if (entity instanceof EntityPropertyType) {
            EntityPropertyType entityPropertyType = (EntityPropertyType) entity;
            try {
                dbClient.insertEntityPropertyType(entityPropertyType);
            } catch (SQLException e) {
                logger.info("Error  entityPropertyType : " + e.getMessage());
            }
        } else if (entity instanceof EntityProperty) {
            EntityProperty entityProperty = (EntityProperty) entity;
            try {
                dbClient.insertEntityProperty(entityProperty);
            } catch (SQLException e) {
                logger.info("Error   entityProperty: " + e.getMessage());
            }
        } else if (entity instanceof EntityType) {
            EntityType entityType = (EntityType) entity;
            try {
                dbClient.insertEntityType(entityType);
            } catch (SQLException e) {
                logger.info("Error  entityType : " + e.getMessage());
            }
        } else if (entity instanceof EntityPropertyValue) {
            EntityPropertyValue entityPropertyValue = (EntityPropertyValue) entity;
            try {
                dbClient.insertEntityPropertyValue(entityPropertyValue);
            } catch (SQLException e) {
                logger.info("Error  entityPropertyValue : " + e.getMessage());
            }
        }

    }

    public void shutdown() {
        executorService.shutdownNow();
    }

}
