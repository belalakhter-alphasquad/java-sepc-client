package sepc.sample.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betbrain.sepc.connector.sportsmodel.BettingOffer;
import com.betbrain.sepc.connector.sportsmodel.BettingOfferStatus;
import com.betbrain.sepc.connector.sportsmodel.BettingType;
import com.betbrain.sepc.connector.sportsmodel.BettingTypeUsage;
import com.betbrain.sepc.connector.sportsmodel.Currency;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import com.betbrain.sepc.connector.sportsmodel.EntityProperty;
import com.betbrain.sepc.connector.sportsmodel.EntityPropertyType;
import com.betbrain.sepc.connector.sportsmodel.EntityPropertyValue;
import com.betbrain.sepc.connector.sportsmodel.EntityType;
import com.betbrain.sepc.connector.sportsmodel.Event;
import com.betbrain.sepc.connector.sportsmodel.EventAction;
import com.betbrain.sepc.connector.sportsmodel.EventActionDetail;
import com.betbrain.sepc.connector.sportsmodel.EventActionDetailStatus;
import com.betbrain.sepc.connector.sportsmodel.EventActionDetailTypeUsage;
import com.betbrain.sepc.connector.sportsmodel.EventActionStatus;
import com.betbrain.sepc.connector.sportsmodel.EventActionType;
import com.betbrain.sepc.connector.sportsmodel.EventActionTypeUsage;
import com.betbrain.sepc.connector.sportsmodel.EventCategory;
import com.betbrain.sepc.connector.sportsmodel.EventInfo;
import com.betbrain.sepc.connector.sportsmodel.EventInfoStatus;
import com.betbrain.sepc.connector.sportsmodel.EventInfoType;
import com.betbrain.sepc.connector.sportsmodel.EventInfoTypeUsage;
import com.betbrain.sepc.connector.sportsmodel.EventPart;
import com.betbrain.sepc.connector.sportsmodel.EventPartDefaultUsage;
import com.betbrain.sepc.connector.sportsmodel.EventParticipantInfo;
import com.betbrain.sepc.connector.sportsmodel.EventParticipantInfoDetail;
import com.betbrain.sepc.connector.sportsmodel.EventParticipantInfoDetailStatus;
import com.betbrain.sepc.connector.sportsmodel.EventParticipantInfoDetailTypeUsage;
import com.betbrain.sepc.connector.sportsmodel.EventParticipantInfoStatus;
import com.betbrain.sepc.connector.sportsmodel.EventParticipantInfoTypeUsage;
import com.betbrain.sepc.connector.sportsmodel.EventParticipantRelation;
import com.betbrain.sepc.connector.sportsmodel.EventParticipantRestriction;
import com.betbrain.sepc.connector.sportsmodel.EventStatus;
import com.betbrain.sepc.connector.sportsmodel.EventTemplate;
import com.betbrain.sepc.connector.sportsmodel.EventType;
import com.betbrain.sepc.connector.sportsmodel.Location;
import com.betbrain.sepc.connector.sportsmodel.LocationRelation;
import com.betbrain.sepc.connector.sportsmodel.LocationRelationType;
import com.betbrain.sepc.connector.sportsmodel.LocationType;
import com.betbrain.sepc.connector.sportsmodel.Market;
import com.betbrain.sepc.connector.sportsmodel.MarketOutcomeRelation;
import com.betbrain.sepc.connector.sportsmodel.Outcome;
import com.betbrain.sepc.connector.sportsmodel.OutcomeStatus;
import com.betbrain.sepc.connector.sportsmodel.OutcomeType;
import com.betbrain.sepc.connector.sportsmodel.OutcomeTypeBettingTypeRelation;
import com.betbrain.sepc.connector.sportsmodel.OutcomeTypeUsage;
import com.betbrain.sepc.connector.sportsmodel.Participant;
import com.betbrain.sepc.connector.sportsmodel.ParticipantRelation;
import com.betbrain.sepc.connector.sportsmodel.ParticipantRelationType;
import com.betbrain.sepc.connector.sportsmodel.ParticipantRole;
import com.betbrain.sepc.connector.sportsmodel.ParticipantType;
import com.betbrain.sepc.connector.sportsmodel.ParticipantUsage;
import com.betbrain.sepc.connector.sportsmodel.Provider;
import com.betbrain.sepc.connector.sportsmodel.ProviderEntityMapping;
import com.betbrain.sepc.connector.sportsmodel.ProviderEventRelation;
import com.betbrain.sepc.connector.sportsmodel.ScoringUnit;
import com.betbrain.sepc.connector.sportsmodel.Source;
import com.betbrain.sepc.connector.sportsmodel.Sport;
import com.betbrain.sepc.connector.sportsmodel.StreamingProvider;
import com.betbrain.sepc.connector.sportsmodel.StreamingProviderEventRelation;
import com.betbrain.sepc.connector.sportsmodel.Translation;
import java.sql.SQLException;
import sepc.sample.DB.DbClient;
import java.util.List;

public class createEntity {
    private static final Logger logger = LoggerFactory.getLogger(createEntity.class);

    public static void processEntity(Entity entity, DbClient dbClient) {

        if (entity instanceof Sport) {
            entity.getDisplayName();
            entity.getId();
            entity.getVersion();
            List<String> fields = entity.getPropertyNames();
            entity.getPropertyValues(null);

            Sport sport = (Sport) entity;
            logger.info(sport.getDisplayName());

            try {
                dbClient.insertSport(sport);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error sport : " + e.getMessage());

            }
        } else if (entity instanceof BettingOffer) {
            BettingOffer bettingoffer = (BettingOffer) entity;
            try {
                dbClient.insertBettingOffer(bettingoffer);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  : " + e.getMessage());

            }
        } else if (entity instanceof EventType) {
            EventType eventType = (EventType) entity;
            try {
                dbClient.insertEventType(eventType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventType: " + e.getMessage());
            }
        } else if (entity instanceof EventStatus) {
            EventStatus eventStatus = (EventStatus) entity;
            try {
                dbClient.insertEventStatus(eventStatus);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error eventStatus : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantRelation) {
            EventParticipantRelation eventParticipantRelation = (EventParticipantRelation) entity;
            try {
                dbClient.insertEventParticipantRelation(eventParticipantRelation);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventParticipantRelation: " + e.getMessage());
            }
        } else if (entity instanceof Participant) {
            Participant participant = (Participant) entity;
            try {
                dbClient.insertParticipant(participant);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  participant: " + e.getMessage());
            }
        } else if (entity instanceof EventCategory) {
            EventCategory eventCategory = (EventCategory) entity;
            try {
                dbClient.insertEventCategory(eventCategory);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error eventCategory : " + e.getMessage());
            }
        } else if (entity instanceof EventTemplate) {
            EventTemplate eventTemplate = (EventTemplate) entity;
            try {
                dbClient.insertEventTemplate(eventTemplate);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventTemplate: " + e.getMessage());
            }
        } else if (entity instanceof EventPart) {
            EventPart eventPart = (EventPart) entity;
            try {
                dbClient.insertEventPart(eventPart);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error eventPart : " + e.getMessage());
            }
        } else if (entity instanceof EventPartDefaultUsage) {
            EventPartDefaultUsage eventPartDefaultUsage = (EventPartDefaultUsage) entity;
            try {
                dbClient.insertEventPartDefaultUsage(eventPartDefaultUsage);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error eventPartDefaultUsage : " + e.getMessage());
            }
        } else if (entity instanceof StreamingProviderEventRelation) {
            StreamingProviderEventRelation streamingProviderEventRelation = (StreamingProviderEventRelation) entity;
            try {
                dbClient.insertStreamingProviderEventRelation(streamingProviderEventRelation);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error streamingProviderEventRelation : " + e.getMessage());
            }
        } else if (entity instanceof StreamingProvider) {
            StreamingProvider streamingProvider = (StreamingProvider) entity;
            try {

                dbClient.insertStreamingProvider(streamingProvider);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error streamingProvider : " + e.getMessage());
            }
        } else if (entity instanceof ProviderEventRelation) {
            ProviderEventRelation providerEventRelation = (ProviderEventRelation) entity;
            try {
                dbClient.insertProviderEventRelation(providerEventRelation);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error providerEventRelation : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantRestriction) {
            EventParticipantRestriction eventParticipantRestriction = (EventParticipantRestriction) entity;
            try {
                dbClient.insertEventParticipantRestriction(eventParticipantRestriction);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventParticipantRestriction: " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRole) {
            ParticipantRole participantRole = (ParticipantRole) entity;
            try {
                dbClient.insertParticipantRole(participantRole);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error participantRole : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantUsage) {
            ParticipantUsage participantUsage = (ParticipantUsage) entity;
            try {
                dbClient.insertParticipantUsage(participantUsage);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error participantUsage : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRelation) {
            ParticipantRelation participantRelation = (ParticipantRelation) entity;
            try {
                dbClient.insertParticipantRelation(participantRelation);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error participantRelation : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRelationType) {
            ParticipantRelationType participantRelationType = (ParticipantRelationType) entity;
            try {
                dbClient.insertParticipantRelationType(participantRelationType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error participantRelationType : " + e.getMessage());
            }
        } else if (entity instanceof Participant) {
            Participant participant = (Participant) entity;
            try {
                dbClient.insertParticipant(participant);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error participant : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantRelation) {
            EventParticipantRelation eventParticipantRelation = (EventParticipantRelation) entity;
            try {
                dbClient.insertEventParticipantRelation(eventParticipantRelation);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error eventParticipantRelation : " + e.getMessage());
            }
        } else if (entity instanceof EventActionTypeUsage) {
            EventActionTypeUsage eventActionTypeUsage = (EventActionTypeUsage) entity;
            try {
                dbClient.insertEventActionTypeUsage(eventActionTypeUsage);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error eventActionTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof EventActionType) {
            EventActionType eventActionType = (EventActionType) entity;
            try {
                dbClient.insertEventActionType(eventActionType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error eventActionType : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoTypeUsage) {
            EventParticipantInfoTypeUsage eventParticipantInfoTypeUsage = (EventParticipantInfoTypeUsage) entity;
            try {
                dbClient.insertEventParticipantInfoTypeUsage(eventParticipantInfoTypeUsage);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error eventParticipantInfoTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoDetailTypeUsage) {
            EventParticipantInfoDetailTypeUsage eventParticipantInfoDetailTypeUsage = (EventParticipantInfoDetailTypeUsage) entity;
            try {
                dbClient.insertEventParticipantInfoDetailTypeUsage(eventParticipantInfoDetailTypeUsage);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error eventParticipantInfoDetailTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof EventActionDetailStatus) {
            EventActionDetailStatus eventActionDetailStatus = (EventActionDetailStatus) entity;
            try {
                dbClient.insertEventActionDetailStatus(eventActionDetailStatus);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error eventActionDetailStatus  : " + e.getMessage());
            }
        } else if (entity instanceof EventActionDetail) {
            EventActionDetail eventActionDetail = (EventActionDetail) entity;
            try {

                dbClient.insertEventActionDetail(eventActionDetail);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventActionDetail: " + e.getMessage());
            }
        } else if (entity instanceof EventActionDetailTypeUsage) {
            EventActionDetailTypeUsage eventActionDetailTypeUsage = (EventActionDetailTypeUsage) entity;
            try {
                dbClient.insertEventActionDetailTypeUsage(eventActionDetailTypeUsage);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventActionDetailTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof EventAction) {
            EventAction eventAction = (EventAction) entity;
            try {
                dbClient.insertEventAction(eventAction);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventAction : " + e.getMessage());
            }
        } else if (entity instanceof EventActionStatus) {
            EventActionStatus eventActionStatus = (EventActionStatus) entity;
            try {
                dbClient.insertEventActionStatus(eventActionStatus);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventActionStatus : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoStatus) {
            EventParticipantInfoStatus eventParticipantInfoStatus = (EventParticipantInfoStatus) entity;
            try {
                dbClient.insertEventParticipantInfoStatus(eventParticipantInfoStatus);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventParticipantInfoStatus : " + e.getMessage());
            }
        } else if (entity instanceof EventInfoStatus) {
            EventInfoStatus eventInfoStatus = (EventInfoStatus) entity;
            try {
                dbClient.insertEventInfoStatus(eventInfoStatus);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventInfoStatus : " + e.getMessage());
            }
        } else if (entity instanceof EventInfo) {
            EventInfo eventInfo = (EventInfo) entity;
            try {

                dbClient.insertEventInfo(eventInfo);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventInfo : " + e.getMessage());
            }
        } else if (entity instanceof EventInfoType) {
            EventInfoType eventInfoType = (EventInfoType) entity;
            try {
                dbClient.insertEventInfoType(eventInfoType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error eventInfoType : " + e.getMessage());
            }
        } else if (entity instanceof EventInfoTypeUsage) {
            EventInfoTypeUsage eventInfoTypeUsage = (EventInfoTypeUsage) entity;
            try {
                dbClient.insertEventInfoTypeUsage(eventInfoTypeUsage);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventInfoTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfo) {
            EventParticipantInfo eventParticipantInfo = (EventParticipantInfo) entity;
            try {
                dbClient.insertEventParticipantInfo(eventParticipantInfo);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventParticipantInfo : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoDetail) {
            EventParticipantInfoDetail eventParticipantInfoDetail = (EventParticipantInfoDetail) entity;
            try {
                dbClient.insertEventParticipantInfoDetail(eventParticipantInfoDetail);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventParticipantInfoDetail : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantInfoDetailStatus) {
            EventParticipantInfoDetailStatus eventParticipantInfoDetailStatus = (EventParticipantInfoDetailStatus) entity;
            try {
                dbClient.insertEventParticipantInfoDetailStatus(eventParticipantInfoDetailStatus);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventParticipantInfoDetailStatus : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRole) {
            ParticipantRole participantRole = (ParticipantRole) entity;
            try {
                dbClient.insertParticipantRole(participantRole);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error participantRole  : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantUsage) {
            ParticipantUsage participantUsage = (ParticipantUsage) entity;
            try {
                dbClient.insertParticipantUsage(participantUsage);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  participantUsage : " + e.getMessage());
            }
        } else if (entity instanceof Participant) {
            Participant participant = (Participant) entity;
            try {
                dbClient.insertParticipant(participant);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  participant : " + e.getMessage());
            }
        } else if (entity instanceof EventParticipantRelation) {
            EventParticipantRelation eventParticipantRelation = (EventParticipantRelation) entity;
            try {
                dbClient.insertEventParticipantRelation(eventParticipantRelation);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventParticipantRelation : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantType) {
            ParticipantType participantType = (ParticipantType) entity;
            try {
                dbClient.insertParticipantType(participantType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  participantType : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRelation) {
            ParticipantRelation participantRelation = (ParticipantRelation) entity;
            try {
                dbClient.insertParticipantRelation(participantRelation);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  participantRelation : " + e.getMessage());
            }
        } else if (entity instanceof ParticipantRelationType) {
            ParticipantRelationType participantRelationType = (ParticipantRelationType) entity;
            try {
                dbClient.insertParticipantRelationType(participantRelationType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  participantRelationType : " + e.getMessage());
            }
        } else if (entity instanceof Provider) {
            Provider provider = (Provider) entity;
            try {
                dbClient.insertProvider(provider);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  provider : " + e.getMessage());
            }
        } else if (entity instanceof Source) {
            Source source = (Source) entity;
            try {
                dbClient.insertSource(source);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  source : " + e.getMessage());
            }
        } else if (entity instanceof BettingOfferStatus) {
            BettingOfferStatus bettingOfferStatus = (BettingOfferStatus) entity;
            try {
                dbClient.insertBettingOfferStatus(bettingOfferStatus);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error bettingOfferStatus  : " + e.getMessage());
            }
        } else if (entity instanceof BettingType) {
            BettingType bettingType = (BettingType) entity;
            try {
                dbClient.insertBettingType(bettingType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error bettingType  : " + e.getMessage());
            }
        } else if (entity instanceof Outcome) {
            Outcome outcome = (Outcome) entity;
            try {
                dbClient.insertOutcome(outcome);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error outcome  : " + e.getMessage());
            }
        } else if (entity instanceof OutcomeType) {
            OutcomeType outcomeType = (OutcomeType) entity;
            try {
                dbClient.insertOutcomeType(outcomeType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  outcomeType : " + e.getMessage());
            }
        } else if (entity instanceof OutcomeTypeBettingTypeRelation) {
            OutcomeTypeBettingTypeRelation outcomeTypeBettingTypeRelation = (OutcomeTypeBettingTypeRelation) entity;
            try {
                dbClient.insertOutcomeTypeBettingTypeRelation(outcomeTypeBettingTypeRelation);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  outcomeTypeBettingTypeRelation : " + e.getMessage());
            }
        } else if (entity instanceof BettingTypeUsage) {
            BettingTypeUsage bettingTypeUsage = (BettingTypeUsage) entity;
            try {
                dbClient.insertBettingTypeUsage(bettingTypeUsage);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  bettingTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof OutcomeStatus) {
            OutcomeStatus outcomeStatus = (OutcomeStatus) entity;
            try {
                dbClient.insertOutcomeStatus(outcomeStatus);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  outcomeStatus : " + e.getMessage());
            }
        } else if (entity instanceof OutcomeTypeUsage) {
            OutcomeTypeUsage outcomeTypeUsage = (OutcomeTypeUsage) entity;
            try {
                dbClient.insertOutcomeTypeUsage(outcomeTypeUsage);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  outcomeTypeUsage : " + e.getMessage());
            }
        } else if (entity instanceof Event) {
            Event event = (Event) entity;
            try {
                dbClient.insertEvent(event);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  event : " + e.getMessage());
            }
        } else if (entity instanceof EventPart) {
            EventPart eventPart = (EventPart) entity;
            try {
                dbClient.insertEventPart(eventPart);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  eventPart : " + e.getMessage());
            }
        } else if (entity instanceof BettingType) {
            BettingType bettingType = (BettingType) entity;
            try {
                dbClient.insertBettingType(bettingType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  bettingType : " + e.getMessage());
            }
        } else if (entity instanceof Market) {
            Market market = (Market) entity;
            try {
                dbClient.insertMarket(market);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error market  : " + e.getMessage());
            }
        } else if (entity instanceof Outcome) {
            Outcome outcome = (Outcome) entity;
            try {
                dbClient.insertOutcome(outcome);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  outcome : " + e.getMessage());
            }
        } else if (entity instanceof MarketOutcomeRelation) {
            MarketOutcomeRelation marketOutcomeRelation = (MarketOutcomeRelation) entity;
            try {
                dbClient.insertMarketOutcomeRelation(marketOutcomeRelation);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  marketOutcomeRelation : " + e.getMessage());
            }
        } else if (entity instanceof Currency) {
            Currency currency = (Currency) entity;
            try {
                dbClient.insertCurrency(currency);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  currency : " + e.getMessage());
            }
        } else if (entity instanceof Provider) {
            Provider provider = (Provider) entity;
            try {
                dbClient.insertProvider(provider);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  provider : " + e.getMessage());
            }
        } else if (entity instanceof StreamingProvider) {
            StreamingProvider streamingProvider = (StreamingProvider) entity;
            try {
                dbClient.insertStreamingProvider(streamingProvider);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error streamingProvider  : " + e.getMessage());
            }
        } else if (entity instanceof ProviderEntityMapping) {
            ProviderEntityMapping providerEntityMapping = (ProviderEntityMapping) entity;
            try {
                dbClient.insertProviderEntityMapping(providerEntityMapping);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  providerEntityMapping : " + e.getMessage());
            }
        } else if (entity instanceof ScoringUnit) {
            ScoringUnit scoringunit = (ScoringUnit) entity;
            try {
                dbClient.insertScoringUnit(scoringunit);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  scoringunit : " + e.getMessage());
            }
        } else if (entity instanceof LocationRelationType) {
            LocationRelationType locationRelationType = (LocationRelationType) entity;
            try {
                dbClient.insertLocationRelationType(locationRelationType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  locationRelationType : " + e.getMessage());
            }
        } else if (entity instanceof LocationRelation) {
            LocationRelation locationRelation = (LocationRelation) entity;
            try {
                dbClient.insertLocationRelation(locationRelation);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  locationRelation : " + e.getMessage());
            }
        } else if (entity instanceof LocationType) {
            LocationType locationType = (LocationType) entity;
            try {
                dbClient.insertLocationType(locationType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  locationType : " + e.getMessage());
            }
        } else if (entity instanceof Location) {
            Location location = (Location) entity;
            try {
                dbClient.insertLocation(location);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error   location: " + e.getMessage());
            }
        } else if (entity instanceof Translation) {
            Translation translation = (Translation) entity;
            try {
                dbClient.insertTranslation(translation);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  translation : " + e.getMessage());
            }
        } else if (entity instanceof EntityPropertyType) {
            EntityPropertyType entityPropertyType = (EntityPropertyType) entity;
            try {
                dbClient.insertEntityPropertyType(entityPropertyType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  entityPropertyType : " + e.getMessage());
            }
        } else if (entity instanceof EntityProperty) {
            EntityProperty entityProperty = (EntityProperty) entity;
            try {
                dbClient.insertEntityProperty(entityProperty);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error   entityProperty: " + e.getMessage());
            }
        } else if (entity instanceof EntityType) {
            EntityType entityType = (EntityType) entity;
            try {
                dbClient.insertEntityType(entityType);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  entityType : " + e.getMessage());
            }
        } else if (entity instanceof EntityPropertyValue) {
            EntityPropertyValue entityPropertyValue = (EntityPropertyValue) entity;
            try {
                dbClient.insertEntityPropertyValue(entityPropertyValue);
                logger.info("Entries saved to db");
            } catch (SQLException e) {
                logger.info("Error  entityPropertyValue : " + e.getMessage());
            }
        }

    }

}
