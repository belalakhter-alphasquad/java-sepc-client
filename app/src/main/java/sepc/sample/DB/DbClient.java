package sepc.sample.DB;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Timestamp;

import com.betbrain.sepc.connector.sportsmodel.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import sepc.sample.utils.EnvLoader;

public class DbClient {

    private static final String DATABASE_NAME = System.getProperty("DB_NAME");
    private static final String USER = System.getProperty("DB_USER");
    private static final String PASSWORD = System.getProperty("DB_PASS");
    private HikariDataSource dataSource;
    private static final String SQL_FILE_PATH = "./src/main/resources/Tables.sql";
    private static DbClient instance;

    public DbClient() {
        EnvLoader.load(".env");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getProperty("DB_URL"));
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        this.dataSource = new HikariDataSource(config);
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    public static synchronized DbClient getInstance() {
        if (instance == null) {
            instance = new DbClient();
        }
        return instance;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public void createDatabaseIfNotExist() throws SQLException {
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
        }
    }

    public void runSqlFileToCreateTables() throws Exception {
        createDatabaseIfNotExist();

        String sqlCommands = new String(Files.readAllBytes(Paths.get(SQL_FILE_PATH)));
        String[] commands = sqlCommands.split(";");

        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {
            for (String command : commands) {
                if (!command.trim().isEmpty()) {
                    stmt.execute(command);
                }
            }
        }
    }

    public void insertSport(Sport sport) throws SQLException {
        String insertSQL = "INSERT INTO sport (id, version, name, description, parentId) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            // Set parameters and execute
            pstmt.setLong(1, sport.getId());
            pstmt.setInt(2, sport.getVersion());
            pstmt.setString(3, sport.getName());
            pstmt.setString(4, sport.getDescription());
            if (sport.getParentId() != null) {
                pstmt.setLong(5, sport.getParentId());
            } else {
                pstmt.setNull(5, java.sql.Types.BIGINT);
            }
            pstmt.executeUpdate();
        }
    }

    public void insertBettingOffer(BettingOffer bettingoffer) throws SQLException {
        String insertSQL = "INSERT INTO bettingoffer (id, version, providerId, sourceId, outcomeId, bettingTypeId, statusId, isLive, odds, multiplicity, volume, volumeCurrencyId, couponKey, slotNum, lastChangedTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setLong(1, bettingoffer.getId());
            pstmt.setInt(2, bettingoffer.getVersion());
            pstmt.setLong(3, bettingoffer.getProviderId());
            pstmt.setLong(4, bettingoffer.getSourceId());
            pstmt.setLong(5, bettingoffer.getOutcomeId());
            pstmt.setLong(6, bettingoffer.getBettingTypeId());
            pstmt.setLong(7, bettingoffer.getStatusId());
            pstmt.setBoolean(8, bettingoffer.getIsLive());
            pstmt.setFloat(9, bettingoffer.getOdds());
            pstmt.setInt(10, bettingoffer.getMultiplicity());
            pstmt.setFloat(11, bettingoffer.getVolume());
            if (bettingoffer.getVolumeCurrencyId() != null) {
                pstmt.setLong(12, bettingoffer.getVolumeCurrencyId());
            } else {
                pstmt.setNull(12, java.sql.Types.BIGINT);
            }
            pstmt.setString(13, bettingoffer.getCouponKey());
            pstmt.setInt(14, bettingoffer.getSlotNum());
            pstmt.setTimestamp(15, new Timestamp(bettingoffer.getLastChangedTime().getTime()));

            pstmt.executeUpdate();
        }

    }

    public void insertEvent(Event event) throws SQLException {
        String insertSQL = "INSERT INTO event (id, version, namespaceId, typeId, isComplete, sportId, templateId, categoryId, promotionId, parentId, parentPartId, name, shortCode, startTime, endTime, deleteTimeOffset, venueId, statusId, hasLiveStatus, rootPartId, currentPartId, url, popularity, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setLong(1, event.getId());
            pstmt.setInt(2, event.getVersion());
            pstmt.setObject(3, event.getNamespaceId());
            pstmt.setObject(4, event.getTypeId());
            pstmt.setBoolean(5, event.getIsComplete());
            pstmt.setLong(6, event.getSportId());
            pstmt.setObject(7, event.getTemplateId());
            pstmt.setObject(8, event.getCategoryId());
            pstmt.setObject(9, event.getPromotionId());
            pstmt.setObject(10, event.getParentId());
            pstmt.setObject(11, event.getParentPartId());
            pstmt.setString(12, event.getName());
            pstmt.setString(13, event.getShortCode());
            pstmt.setTimestamp(14, event.getStartTime() != null ? new Timestamp(event.getStartTime().getTime()) : null);
            pstmt.setTimestamp(15, event.getEndTime() != null ? new Timestamp(event.getEndTime().getTime()) : null);
            pstmt.setLong(16, event.getDeleteTimeOffset());
            pstmt.setObject(17, event.getVenueId());
            pstmt.setLong(18, event.getStatusId());
            pstmt.setBoolean(19, event.getHasLiveStatus());
            pstmt.setLong(20, event.getRootPartId());
            pstmt.setObject(21, event.getCurrentPartId());
            pstmt.setString(22, event.getUrl());
            pstmt.setObject(23, event.getPopularity());
            pstmt.setString(24, event.getNote());

            pstmt.executeUpdate();
        }
    }

    public void insertEventType(EventType eventType) throws SQLException {
        String insertSQL = "INSERT INTO eventtype (id, version, name, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, eventType.getId());
            pstmt.setInt(2, eventType.getVersion());
            pstmt.setString(3, eventType.getName());
            pstmt.setString(4, eventType.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertEventStatus(EventStatus eventStatus) throws SQLException {
        String insertSQL = "INSERT INTO eventstatus (id, version, name, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, eventStatus.getId());
            pstmt.setInt(2, eventStatus.getVersion());
            pstmt.setString(3, eventStatus.getName());
            pstmt.setString(4, eventStatus.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertParticipant(Participant participant) throws SQLException {
        String insertSQL = "INSERT INTO participant (id, version, namespaceId, typeId, name, firstName, lastName, shortName, isMale, birthTime, countryId, url, logoUrl, retirementTime, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, participant.getId());
            pstmt.setInt(2, participant.getVersion());
            pstmt.setObject(3, participant.getNamespaceId());
            pstmt.setLong(4, participant.getTypeId());
            pstmt.setString(5, participant.getName());
            pstmt.setString(6, participant.getFirstName());
            pstmt.setString(7, participant.getLastName());
            pstmt.setString(8, participant.getShortName());
            pstmt.setObject(9, participant.getIsMale());
            pstmt.setTimestamp(10,
                    participant.getBirthTime() != null ? new Timestamp(participant.getBirthTime().getTime()) : null);
            pstmt.setObject(11, participant.getCountryId());
            pstmt.setString(12, participant.getUrl());
            pstmt.setString(13, participant.getLogoUrl());
            pstmt.setTimestamp(14,
                    participant.getRetirementTime() != null ? new Timestamp(participant.getRetirementTime().getTime())
                            : null);
            pstmt.setString(15, participant.getNote());
            pstmt.executeUpdate();
        }
    }

    public void insertEventCategory(EventCategory eventCategory) throws SQLException {
        String insertSQL = "INSERT INTO eventcategory (id, version, name, sportId, note) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, eventCategory.getId());
            pstmt.setInt(2, eventCategory.getVersion());
            pstmt.setString(3, eventCategory.getName());
            pstmt.setLong(4, eventCategory.getSportId());
            pstmt.setString(5, eventCategory.getNote());
            pstmt.executeUpdate();
        }
    }

    public void insertEventTemplate(EventTemplate eventTemplate) throws SQLException {
        String insertSQL = "INSERT INTO eventtemplate (id, version, namespaceId, name, eventTypeId, sportId, tier, categoryId, catalogId, url, venueId, rootPartId, isCyber, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, eventTemplate.getId());
            pstmt.setInt(2, eventTemplate.getVersion());
            pstmt.setObject(3, eventTemplate.getNamespaceId());
            pstmt.setString(4, eventTemplate.getName());
            pstmt.setLong(5, eventTemplate.getEventTypeId());
            pstmt.setLong(6, eventTemplate.getSportId());
            pstmt.setObject(7, eventTemplate.getTier());
            pstmt.setObject(8, eventTemplate.getCategoryId());
            pstmt.setObject(9, eventTemplate.getCatalogId());
            pstmt.setString(10, eventTemplate.getUrl());
            pstmt.setObject(11, eventTemplate.getVenueId());
            pstmt.setObject(12, eventTemplate.getRootPartId());
            pstmt.setObject(13, eventTemplate.getIsCyber());
            pstmt.setString(14, eventTemplate.getNote());
            pstmt.executeUpdate();
        }
    }

    public void insertEventPart(EventPart eventPart) throws SQLException {

    }

    public void insertEventPartDefaultUsage(EventPartDefaultUsage eventPartDefaultUsage) throws SQLException {
    }

    public void insertStreamingProviderEventRelation(StreamingProviderEventRelation streamingProviderEventRelation)
            throws SQLException {
    }

    public void insertStreamingProvider(StreamingProvider streamingProvider) throws SQLException {
    }

    public void insertProviderEventRelation(ProviderEventRelation providerEventRelation) throws SQLException {
    }

    public void insertEventParticipantRestriction(EventParticipantRestriction eventParticipantRestriction)
            throws SQLException {
    }

    public void insertParticipantRole(ParticipantRole participantRole) throws SQLException {
    }

    public void insertParticipantUsage(ParticipantUsage participantUsage) throws SQLException {
    }

    public void insertParticipantTypeRoleUsage(ParticipantTypeRoleUsage participantTypeRoleUsage) throws SQLException {
    }

    public void insertParticipantRelation(ParticipantRelation participantRelation) throws SQLException {
    }

    public void insertParticipantRelationType(ParticipantRelationType participantRelationType) throws SQLException {
    }

    public void insertEventParticipantRelation(EventParticipantRelation eventParticipantRelation) throws SQLException {
    }

    public void insertEventActionTypeUsage(EventActionTypeUsage eventActionTypeUsage) throws SQLException {
    }

    public void insertEventActionType(EventActionType eventActionType) throws SQLException {
    }

    public void insertEventParticipantInfoTypeUsage(EventParticipantInfoTypeUsage eventParticipantInfoTypeUsage)
            throws SQLException {
        // Implementation here
    }

    public void insertEventParticipantInfoDetailTypeUsage(
            EventParticipantInfoDetailTypeUsage eventParticipantInfoDetailTypeUsage) throws SQLException {
        // Implementation here
    }

    public void insertEventActionDetailStatus(EventActionDetailStatus eventActionDetailStatus) throws SQLException {
        // Implementation here
    }

    public void insertEventActionDetail(EventActionDetail eventActionDetail) throws SQLException {
        // Implementation here
    }

    public void insertEventActionDetailTypeUsage(EventActionDetailTypeUsage eventActionDetailTypeUsage)
            throws SQLException {
        // Implementation here
    }

    public void insertEventAction(EventAction eventAction) throws SQLException {
        // Implementation here
    }

    public void insertEventActionStatus(EventActionStatus eventActionStatus) throws SQLException {
        // Implementation here
    }

    public void insertEventParticipantInfoStatus(EventParticipantInfoStatus eventParticipantInfoStatus)
            throws SQLException {
        // Implementation here
    }

    public void insertEventInfoStatus(EventInfoStatus eventInfoStatus) throws SQLException {
        // Implementation here
    }

    public void insertEventInfo(EventInfo eventInfo) throws SQLException {
        // Implementation here
    }

    public void insertEventInfoType(EventInfoType eventInfoType) throws SQLException {
        // Implementation here
    }

    public void insertEventInfoTypeUsage(EventInfoTypeUsage eventInfoTypeUsage) throws SQLException {
        // Implementation here
    }

    public void insertEventParticipantInfo(EventParticipantInfo eventParticipantInfo) throws SQLException {
        // Implementation here
    }

    public void insertEventParticipantInfoDetail(EventParticipantInfoDetail eventParticipantInfoDetail)
            throws SQLException {
        // Implementation here
    }

    public void insertEventParticipantInfoDetailStatus(
            EventParticipantInfoDetailStatus eventParticipantInfoDetailStatus) throws SQLException {
        // Implementation here
    }

    public void insertParticipantType(ParticipantType participantType) throws SQLException {
        // Implementation here
    }

    public void insertProvider(Provider provider) throws SQLException {
        // Implementation here
    }

    public void insertSource(Source source) throws SQLException {
        // Implementation here
    }

    public void insertBettingOfferStatus(BettingOfferStatus bettingOfferStatus) throws SQLException {
        // Implementation here
    }

    public void insertBettingType(BettingType bettingType) throws SQLException {
        // Implementation here
    }

    public void insertOutcome(Outcome outcome) throws SQLException {
        // Implementation here
    }

    public void insertOutcomeType(OutcomeType outcomeType) throws SQLException {
        // Implementation here
    }

    public void insertOutcomeTypeBettingTypeRelation(OutcomeTypeBettingTypeRelation outcomeTypeBettingTypeRelation)
            throws SQLException {
        // Implementation here
    }

    public void insertBettingTypeUsage(BettingTypeUsage bettingTypeUsage) throws SQLException {
        // Implementation here
    }

    public void insertOutcomeStatus(OutcomeStatus outcomeStatus) throws SQLException {
        // Implementation here
    }

    public void insertOutcomeTypeUsage(OutcomeTypeUsage outcomeTypeUsage) throws SQLException {
        // Implementation here
    }

    public void insertMarket(Market market) throws SQLException {
        // Implementation here
    }

    public void insertMarketOutcomeRelation(MarketOutcomeRelation marketOutcomeRelation) throws SQLException {
        // Implementation here
    }

    public void insertCurrency(Currency currency) throws SQLException {
        // Implementation here
    }

    public void insertProviderEntityMapping(ProviderEntityMapping providerEntityMapping) throws SQLException {
        // Implementation here
    }

}
