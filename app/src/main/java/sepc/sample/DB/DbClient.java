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
        config.setMaximumPoolSize(100);
        config.setMinimumIdle(50);
        config.setConnectionTimeout(1000);
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
        String insertSQL = "INSERT INTO eventpart (id, version, name, description, parentId, orderNum, isDrawPossible, isBreak) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, eventPart.getId());
            pstmt.setInt(2, eventPart.getVersion());
            pstmt.setString(3, eventPart.getName());
            pstmt.setString(4, eventPart.getDescription());
            if (eventPart.getParentId() != null) {
                pstmt.setLong(5, eventPart.getParentId());
            } else {
                pstmt.setNull(5, java.sql.Types.BIGINT);
            }
            pstmt.setInt(6, eventPart.getOrderNum());
            if (eventPart.getIsDrawPossible() != null) {
                pstmt.setBoolean(7, eventPart.getIsDrawPossible());
            } else {
                pstmt.setNull(7, java.sql.Types.TINYINT);
            }
            pstmt.setBoolean(8, eventPart.getIsBreak());
            pstmt.executeUpdate();
        }
    }

    public void insertEventPartDefaultUsage(EventPartDefaultUsage eventPartDefaultUsage) throws SQLException {
        String insertSQL = "INSERT INTO eventpartdefaultusage (id, version, parentEventId, eventTypeId, sportId, rootPartId) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, eventPartDefaultUsage.getId());
            pstmt.setInt(2, eventPartDefaultUsage.getVersion());
            pstmt.setLong(3, eventPartDefaultUsage.getParentEventId()); // Assuming parentEventId can be null
            pstmt.setLong(4, eventPartDefaultUsage.getEventTypeId());
            pstmt.setLong(5, eventPartDefaultUsage.getSportId());
            pstmt.setLong(6, eventPartDefaultUsage.getRootPartId());
            pstmt.executeUpdate();
        }
    }

    public void insertStreamingProviderEventRelation(StreamingProviderEventRelation streamingProviderEventRelation)
            throws SQLException {
        String insertSQL = "INSERT INTO streamingprovidereventrelation (id, version, streamingProviderId, eventId, channel, language) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, streamingProviderEventRelation.getId());
            pstmt.setInt(2, streamingProviderEventRelation.getVersion());
            pstmt.setLong(3, streamingProviderEventRelation.getStreamingProviderId());
            pstmt.setLong(4, streamingProviderEventRelation.getEventId());
            pstmt.setString(5, streamingProviderEventRelation.getChannel());
            pstmt.setString(6, streamingProviderEventRelation.getLanguage()); // Assuming language can be null
            pstmt.executeUpdate();
        }
    }

    public void insertStreamingProvider(StreamingProvider streamingProvider) throws SQLException {
        String insertSQL = "INSERT INTO streamingprovider (id, version, name, urlTemplate) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, streamingProvider.getId());
            pstmt.setInt(2, streamingProvider.getVersion());
            pstmt.setString(3, streamingProvider.getName());
            pstmt.setString(4, streamingProvider.getUrlTemplate());
            pstmt.executeUpdate();
        }
    }

    public void insertProviderEventRelation(ProviderEventRelation providerEventRelation) throws SQLException {
        String insertSQL = "INSERT INTO providereventrelation (id, version, providerId, eventId, startTime, endTime, timeQualityRank, offersLiveOdds, offersLiveTV) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, providerEventRelation.getId());
            pstmt.setInt(2, providerEventRelation.getVersion());
            pstmt.setLong(3, providerEventRelation.getProviderId());
            pstmt.setLong(4, providerEventRelation.getEventId());
            pstmt.setTimestamp(5,
                    providerEventRelation.getStartTime() != null
                            ? new java.sql.Timestamp(providerEventRelation.getStartTime().getTime())
                            : null);
            pstmt.setTimestamp(6,
                    providerEventRelation.getEndTime() != null
                            ? new java.sql.Timestamp(providerEventRelation.getEndTime().getTime())
                            : null);
            pstmt.setInt(7, providerEventRelation.getTimeQualityRank());
            pstmt.setBoolean(8, providerEventRelation.getOffersLiveOdds());
            pstmt.setBoolean(9, providerEventRelation.isOffersLiveTV());
            pstmt.executeUpdate();
        }
    }

    public void insertEventParticipantRestriction(EventParticipantRestriction eventParticipantRestriction)
            throws SQLException {
        String sql = "INSERT INTO eventparticipantrestriction (id, version, eventId, participantTypeId, participantIsMale, participantMinAge, participantMaxAge, participantPartOfLocationId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventParticipantRestriction.getId());
            pstmt.setInt(2, eventParticipantRestriction.getVersion());
            pstmt.setLong(3, eventParticipantRestriction.getEventId());
            pstmt.setLong(4, eventParticipantRestriction.getParticipantTypeId());
            pstmt.setObject(5, eventParticipantRestriction.getParticipantIsMale(), java.sql.Types.TINYINT);
            pstmt.setObject(6, eventParticipantRestriction.getParticipantMinAge(), java.sql.Types.INTEGER);
            pstmt.setObject(7, eventParticipantRestriction.getParticipantMaxAge(), java.sql.Types.INTEGER);
            pstmt.setObject(8, eventParticipantRestriction.getParticipantPartOfLocationId(), java.sql.Types.BIGINT);
            pstmt.executeUpdate();
        }
    }

    public void insertParticipantRole(ParticipantRole participantRole) throws SQLException {
        String sql = "INSERT INTO participantrole (id, version, name, description, isPrimary) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, participantRole.getId());
            pstmt.setInt(2, participantRole.getVersion());
            pstmt.setString(3, participantRole.getName());
            pstmt.setString(4, participantRole.getDescription());
            pstmt.setBoolean(5, participantRole.getIsPrimary());
            pstmt.executeUpdate();
        }
    }

    public void insertScoringUnit(ScoringUnit scoringUnit) throws SQLException {
        String sql = "INSERT INTO scoringunit (id, version, name, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, scoringUnit.getId());
            pstmt.setInt(2, scoringUnit.getVersion());
            pstmt.setString(3, scoringUnit.getName());
            pstmt.setString(4, scoringUnit.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertParticipantUsage(ParticipantUsage participantUsage) throws SQLException {
        String sql = "INSERT INTO participantusage (id, version, participantId, sportId) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, participantUsage.getId());
            pstmt.setInt(2, participantUsage.getVersion());
            pstmt.setLong(3, participantUsage.getParticipantId());
            pstmt.setLong(4, participantUsage.getSportId());
            pstmt.executeUpdate();
        }
    }

    public void insertParticipantRelation(ParticipantRelation participantRelation) throws SQLException {
        String sql = "INSERT INTO participantrelation (id, version, typeId, fromParticipantId, toParticipantId, startTime, endTime, paramParticipantRoleId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, participantRelation.getId());
            pstmt.setInt(2, participantRelation.getVersion());
            pstmt.setLong(3, participantRelation.getTypeId());
            pstmt.setLong(4, participantRelation.getFromParticipantId());
            pstmt.setLong(5, participantRelation.getToParticipantId());
            pstmt.setTimestamp(6,
                    participantRelation.getStartTime() != null
                            ? new java.sql.Timestamp(participantRelation.getStartTime().getTime())
                            : null);
            pstmt.setTimestamp(7,
                    participantRelation.getEndTime() != null
                            ? new java.sql.Timestamp(participantRelation.getEndTime().getTime())
                            : null);
            pstmt.setObject(8, participantRelation.getParamParticipantRoleId(), java.sql.Types.BIGINT);
            pstmt.executeUpdate();
        }
    }

    public void insertParticipantRelationType(ParticipantRelationType participantRelationType) throws SQLException {
        String sql = "INSERT INTO participantrelationtype (id, version, name, description, hasParamParticipantRoleId, paramParticipantRoleIdDescription) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, participantRelationType.getId());
            pstmt.setInt(2, participantRelationType.getVersion());
            pstmt.setString(3, participantRelationType.getName());
            pstmt.setString(4, participantRelationType.getDescription());
            pstmt.setObject(5, participantRelationType.getHasParamParticipantRoleId(), java.sql.Types.TINYINT);
            pstmt.setString(6, participantRelationType.getParamParticipantRoleIdDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertEventParticipantRelation(EventParticipantRelation eventParticipantRelation) throws SQLException {
        String sql = "INSERT INTO eventparticipantrelation (id, version, eventId, eventPartId, participantId, participantRoleId, parentParticipantId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventParticipantRelation.getId());
            pstmt.setInt(2, eventParticipantRelation.getVersion());
            pstmt.setLong(3, eventParticipantRelation.getEventId());
            pstmt.setLong(4, eventParticipantRelation.getEventPartId());
            pstmt.setLong(5, eventParticipantRelation.getParticipantId());
            pstmt.setLong(6, eventParticipantRelation.getParticipantRoleId());
            pstmt.setObject(7, eventParticipantRelation.getParentParticipantId(), java.sql.Types.BIGINT); // Nullable
            pstmt.executeUpdate();
        }
    }

    public void insertEventActionTypeUsage(EventActionTypeUsage eventActionTypeUsage) throws SQLException {
        String sql = "INSERT INTO eventactiontypeusage (id, version, eventActionTypeId, eventTypeId, eventPartId, sportId) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventActionTypeUsage.getId());
            pstmt.setInt(2, eventActionTypeUsage.getVersion());
            pstmt.setLong(3, eventActionTypeUsage.getEventActionTypeId());
            pstmt.setLong(4, eventActionTypeUsage.getEventTypeId());
            pstmt.setLong(5, eventActionTypeUsage.getEventPartId());
            pstmt.setLong(6, eventActionTypeUsage.getSportId());
            pstmt.executeUpdate();
        }
    }

    public void insertLocationRelationType(LocationRelationType locationRelationType) throws SQLException {
        String sql = "INSERT INTO locationrelationtype (id, version, name, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, locationRelationType.getId());
            pstmt.setInt(2, locationRelationType.getVersion());
            pstmt.setString(3, locationRelationType.getName());
            pstmt.setString(4, locationRelationType.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertLocationRelation(LocationRelation locationRelation) throws SQLException {
        String sql = "INSERT INTO locationrelation (id, version, typeId, fromLocationId, toLocationId) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, locationRelation.getId());
            pstmt.setInt(2, locationRelation.getVersion());
            pstmt.setLong(3, locationRelation.getTypeId());
            pstmt.setLong(4, locationRelation.getFromLocationId());
            pstmt.setLong(5, locationRelation.getToLocationId());
            pstmt.executeUpdate();
        }
    }

    public void insertEventActionType(EventActionType eventActionType) throws SQLException {
        String sql = "INSERT INTO eventactiontype (id, version, name, description, hasParamFloat1, paramFloat1Description, hasParamParticipantId1, paramParticipantId1Description, hasParamParticipantId2, paramParticipantId2Description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventActionType.getId());
            pstmt.setInt(2, eventActionType.getVersion());
            pstmt.setString(3, eventActionType.getName());
            pstmt.setString(4, eventActionType.getDescription());
            pstmt.setObject(5, eventActionType.getHasParamFloat1(), java.sql.Types.TINYINT);
            pstmt.setString(6, eventActionType.getParamFloat1Description());
            pstmt.setObject(7, eventActionType.getHasParamParticipantId1(), java.sql.Types.TINYINT);
            pstmt.setString(8, eventActionType.getParamParticipantId1Description());
            pstmt.setObject(9, eventActionType.getHasParamParticipantId2(), java.sql.Types.TINYINT);
            pstmt.setString(10, eventActionType.getParamParticipantId2Description());
            pstmt.executeUpdate();
        }
    }

    public void insertEventParticipantInfoTypeUsage(EventParticipantInfoTypeUsage eventParticipantInfoTypeUsage)
            throws SQLException {
        String sql = "INSERT INTO eventparticipantinfotypeusage (id, version, eventParticipantInfoTypeId, eventTypeId, eventPartId, sportId) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventParticipantInfoTypeUsage.getId());
            pstmt.setInt(2, eventParticipantInfoTypeUsage.getVersion());
            pstmt.setLong(3, eventParticipantInfoTypeUsage.getEventParticipantInfoTypeId());
            pstmt.setLong(4, eventParticipantInfoTypeUsage.getEventTypeId());
            pstmt.setLong(5, eventParticipantInfoTypeUsage.getEventPartId());
            pstmt.setLong(6, eventParticipantInfoTypeUsage.getSportId());
            pstmt.executeUpdate();
        }
    }

    public void insertEventParticipantInfoDetailTypeUsage(
            EventParticipantInfoDetailTypeUsage eventParticipantInfoDetailTypeUsage) throws SQLException {
        String sql = "INSERT INTO eventparticipantinfodetailtypeusage (id, version, eventParticipantInfoDetailTypeId, eventParticipantInfoTypeId, sportId) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventParticipantInfoDetailTypeUsage.getId());
            pstmt.setInt(2, eventParticipantInfoDetailTypeUsage.getVersion());
            pstmt.setLong(3, eventParticipantInfoDetailTypeUsage.getEventParticipantInfoDetailTypeId());
            pstmt.setLong(4, eventParticipantInfoDetailTypeUsage.getEventParticipantInfoTypeId());
            pstmt.setLong(5, eventParticipantInfoDetailTypeUsage.getSportId());
            pstmt.executeUpdate();
        }
    }

    public void insertLocationType(LocationType locationType) throws SQLException {
        String sql = "INSERT INTO locationtype (id, version, name, description, hasCode, codeDescription) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, locationType.getId());
            pstmt.setInt(2, locationType.getVersion());
            pstmt.setString(3, locationType.getName());
            pstmt.setString(4, locationType.getDescription());
            pstmt.setBoolean(5, locationType.getHasCode());
            pstmt.setString(6, locationType.getCodeDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertTranslation(Translation translation) throws SQLException {
        String sql = "INSERT INTO translation (id, version, name, entityId, entityTypeId, languageId,lastChangedDate) VALUES (?, ?, ?, ?, ?, ?,?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, translation.getId());
            pstmt.setInt(2, translation.getVersion());
            pstmt.setString(3, translation.getName());
            pstmt.setLong(4, translation.getEntityId());
            pstmt.setLong(5, translation.getEntityTypeId());
            pstmt.setLong(6, translation.getLanguageId());
            pstmt.setTimestamp(7, new java.sql.Timestamp(translation.getLastChangedDate().getTime()));
            pstmt.executeUpdate();
        }

    }

    public void insertLocation(Location location) throws SQLException {
        String sql = "INSERT INTO location (id, version, typeId, name, code, isHistoric, url, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, location.getId());
            pstmt.setInt(2, location.getVersion());
            pstmt.setLong(3, location.getTypeId());
            pstmt.setString(4, location.getName());
            pstmt.setString(5, location.getCode());
            pstmt.setBoolean(6, location.getIsHistoric());
            pstmt.setString(7, location.getUrl());
            pstmt.setString(8, location.getNote());
            pstmt.executeUpdate();
        }
    }

    public void insertEventActionDetailStatus(EventActionDetailStatus eventActionDetailStatus) throws SQLException {
        String sql = "INSERT INTO eventactiondetailstatus (id, version, name, isAvailable, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventActionDetailStatus.getId());
            pstmt.setInt(2, eventActionDetailStatus.getVersion());
            pstmt.setString(3, eventActionDetailStatus.getName());
            pstmt.setBoolean(4, eventActionDetailStatus.getIsAvailable());
            pstmt.setString(5, eventActionDetailStatus.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertEntityPropertyType(EntityPropertyType entityPropertyType) throws SQLException {
        String sql = "INSERT INTO entitypropertytype (id, version, name, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, entityPropertyType.getId());
            pstmt.setInt(2, entityPropertyType.getVersion());
            pstmt.setString(3, entityPropertyType.getName());
            pstmt.setString(4, entityPropertyType.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertEntityProperty(EntityProperty entityProperty) throws SQLException {
        String sql = "INSERT INTO entityproperty (id, version, typeId, entityTypeId, name, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, entityProperty.getId());
            pstmt.setInt(2, entityProperty.getVersion());
            pstmt.setLong(3, entityProperty.getTypeId());
            pstmt.setLong(4, entityProperty.getEntityTypeId());
            pstmt.setString(5, entityProperty.getName());
            pstmt.setString(6, entityProperty.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertEntityType(EntityType entityType) throws SQLException {
        String sql = "INSERT INTO entitytype (id, version, name, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, entityType.getId());
            pstmt.setInt(2, entityType.getVersion());
            pstmt.setString(3, entityType.getName());
            pstmt.setString(4, entityType.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertEntityPropertyValue(EntityPropertyValue entityPropertyValue) throws SQLException {
        String sql = "INSERT INTO entitypropertyvalue (id, version, entityPropertyId, entityId, value) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, entityPropertyValue.getId());
            pstmt.setInt(2, entityPropertyValue.getVersion());
            pstmt.setLong(3, entityPropertyValue.getEntityPropertyId());
            pstmt.setLong(4, entityPropertyValue.getEntityId());
            pstmt.setString(5, entityPropertyValue.getValue());
            pstmt.executeUpdate();
        }
    }

    public void insertEventActionDetail(EventActionDetail eventActionDetail) throws SQLException {
        String sql = "INSERT INTO eventactiondetail (id, version, typeId, eventActionId, statusId, paramFloat1, paramFloat2, paramParticipantId1, paramString1, paramBoolean1, isManuallySet) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventActionDetail.getId());
            pstmt.setInt(2, eventActionDetail.getVersion());
            pstmt.setLong(3, eventActionDetail.getTypeId());
            pstmt.setLong(4, eventActionDetail.getEventActionId());
            pstmt.setLong(5, eventActionDetail.getStatusId());
            pstmt.setObject(6, eventActionDetail.getParamFloat1());
            pstmt.setObject(7, eventActionDetail.getParamFloat2());
            pstmt.setObject(8, eventActionDetail.getParamParticipantId1(), java.sql.Types.BIGINT);
            pstmt.setString(9, eventActionDetail.getParamString1());
            pstmt.setObject(10, eventActionDetail.getParamBoolean1(), java.sql.Types.TINYINT);
            pstmt.setBoolean(11, eventActionDetail.getIsManuallySet());
            pstmt.executeUpdate();
        }
    }

    public void insertEventActionDetailTypeUsage(EventActionDetailTypeUsage eventActionDetailTypeUsage)
            throws SQLException {
        String sql = "INSERT INTO eventactiondetailtypeusage (id, version, eventActionDetailTypeId, eventActionTypeId, sportId) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventActionDetailTypeUsage.getId());
            pstmt.setInt(2, eventActionDetailTypeUsage.getVersion());
            pstmt.setLong(3, eventActionDetailTypeUsage.getEventActionDetailTypeId());
            pstmt.setLong(4, eventActionDetailTypeUsage.getEventActionTypeId());
            pstmt.setLong(5, eventActionDetailTypeUsage.getSportId());
            pstmt.executeUpdate();
        }
    }

    public void insertEventAction(EventAction eventAction) throws SQLException {
        String sql = "INSERT INTO eventaction (id, version, typeId, eventId, providerId, statusId, eventPartId, paramFloat1, paramParticipantId1, paramParticipantId2, isManuallySet) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventAction.getId());
            pstmt.setInt(2, eventAction.getVersion());
            pstmt.setLong(3, eventAction.getTypeId());
            pstmt.setLong(4, eventAction.getEventId());
            pstmt.setLong(5, eventAction.getProviderId());
            pstmt.setLong(6, eventAction.getStatusId());
            pstmt.setLong(7, eventAction.getEventPartId());
            pstmt.setDouble(8, eventAction.getParamFloat1()); // Double, check for null
            pstmt.setObject(9, eventAction.getParamParticipantId1(), java.sql.Types.BIGINT); // Nullable
            pstmt.setObject(10, eventAction.getParamParticipantId2(), java.sql.Types.BIGINT); // Nullable
            pstmt.setBoolean(11, eventAction.getIsManuallySet());
            pstmt.executeUpdate();
        }
    }

    public void insertEventActionStatus(EventActionStatus eventActionStatus) throws SQLException {
        String sql = "INSERT INTO eventactionstatus (id, version, name, isAvailable, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventActionStatus.getId());
            pstmt.setInt(2, eventActionStatus.getVersion());
            pstmt.setString(3, eventActionStatus.getName());
            pstmt.setBoolean(4, eventActionStatus.getIsAvailable());
            pstmt.setString(5, eventActionStatus.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertEventParticipantInfoStatus(EventParticipantInfoStatus eventParticipantInfoStatus)
            throws SQLException {
        String sql = "INSERT INTO eventparticipantinfostatus (id, version, name, isAvailable, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventParticipantInfoStatus.getId());
            pstmt.setInt(2, eventParticipantInfoStatus.getVersion());
            pstmt.setString(3, eventParticipantInfoStatus.getName());
            pstmt.setBoolean(4, eventParticipantInfoStatus.getIsAvailable());
            pstmt.setString(5, eventParticipantInfoStatus.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertEventInfoStatus(EventInfoStatus eventInfoStatus) throws SQLException {
        String sql = "INSERT INTO eventinfostatus (id, version, name, isAvailable, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventInfoStatus.getId());
            pstmt.setInt(2, eventInfoStatus.getVersion());
            pstmt.setString(3, eventInfoStatus.getName());
            pstmt.setBoolean(4, eventInfoStatus.getIsAvailable());
            pstmt.setString(5, eventInfoStatus.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertEventInfoTypeUsage(EventInfoTypeUsage eventInfoTypeUsage) throws SQLException {
        String sql = "INSERT INTO eventinfotypeusage (id, version, eventInfoTypeId, eventTypeId, eventPartId, sportId) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventInfoTypeUsage.getId());
            pstmt.setInt(2, eventInfoTypeUsage.getVersion());
            pstmt.setLong(3, eventInfoTypeUsage.getEventInfoTypeId());
            pstmt.setLong(4, eventInfoTypeUsage.getEventTypeId());
            pstmt.setLong(5, eventInfoTypeUsage.getEventPartId());
            pstmt.setLong(6, eventInfoTypeUsage.getSportId());
            pstmt.executeUpdate();
        }
    }

    public void insertEventParticipantInfoDetailStatus(
            EventParticipantInfoDetailStatus eventParticipantInfoDetailStatus) throws SQLException {
        String sql = "INSERT INTO eventparticipantinfodetailstatus (id, version, name, isAvailable, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventParticipantInfoDetailStatus.getId());
            pstmt.setInt(2, eventParticipantInfoDetailStatus.getVersion());
            pstmt.setString(3, eventParticipantInfoDetailStatus.getName());
            pstmt.setBoolean(4, eventParticipantInfoDetailStatus.getIsAvailable());
            pstmt.setString(5, eventParticipantInfoDetailStatus.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertEventInfo(EventInfo eventInfo) throws SQLException {
        String sql = "INSERT INTO eventinfo (id, version, typeId, eventId, providerId, statusId, eventPartId, paramFloat1, paramFloat2, paramParticipantId1, paramParticipantId2, paramEventPartId1, paramString1, paramBoolean1, paramEventStatusId1, paramTime1, paramScoringUnitId1, isManuallySet) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, eventInfo.getId());
            pstmt.setInt(2, eventInfo.getVersion());
            pstmt.setLong(3, eventInfo.getTypeId());
            pstmt.setLong(4, eventInfo.getEventId());
            pstmt.setLong(5, eventInfo.getProviderId());
            pstmt.setLong(6, eventInfo.getStatusId());
            pstmt.setLong(7, eventInfo.getEventPartId());
            pstmt.setObject(8, eventInfo.getParamFloat1(), java.sql.Types.DOUBLE);
            pstmt.setObject(9, eventInfo.getParamFloat2(), java.sql.Types.DOUBLE);
            pstmt.setObject(10, eventInfo.getParamParticipantId1(), java.sql.Types.BIGINT);
            pstmt.setObject(11, eventInfo.getParamParticipantId2(), java.sql.Types.BIGINT);
            pstmt.setObject(12, eventInfo.getParamEventPartId1(), java.sql.Types.BIGINT);
            pstmt.setString(13, eventInfo.getParamString1());
            pstmt.setObject(14, eventInfo.getParamBoolean1(), java.sql.Types.TINYINT);
            pstmt.setObject(15, eventInfo.getParamEventStatusId1(), java.sql.Types.BIGINT);

            if (eventInfo.getParamTime1() != null) {
                pstmt.setTimestamp(16, new java.sql.Timestamp(eventInfo.getParamTime1().getTime()));
            } else {
                pstmt.setNull(16, java.sql.Types.TIMESTAMP);
            }

            pstmt.setObject(17, eventInfo.getParamScoringUnitId1(), java.sql.Types.BIGINT);
            pstmt.setBoolean(18, eventInfo.getIsManuallySet());

            pstmt.executeUpdate();
        }
    }

    public void insertEventInfoType(EventInfoType eventInfoType) throws SQLException {
        String insertSQL = "INSERT INTO eventinfotype (id, version, name, description, hasParamFloat1, paramFloat1Description, hasParamFloat2, paramFloat2Description, hasParamParticipantId1, paramParticipantId1Description, hasParamParticipantId2, paramParticipantId2Description, hasParamEventPartId1, paramEventPartId1Description, hasParamString1, paramString1Description, paramString1PossibleValues, hasParamBoolean1, paramBoolean1Description, hasParamEventStatusId1, paramEventStatusId1Description, hasParamTime1, paramTime1Description, paramParticipantIdsMustBeOrdered, hasParamScoringUnitId1, paramScoringUnitId1Description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, eventInfoType.getId());
            pstmt.setInt(2, eventInfoType.getVersion());
            pstmt.setString(3, eventInfoType.getName());
            pstmt.setString(4, eventInfoType.getDescription());
            pstmt.setObject(5, eventInfoType.getHasParamFloat1(), java.sql.Types.TINYINT);
            pstmt.setString(6, eventInfoType.getParamFloat1Description());
            pstmt.setObject(7, eventInfoType.getHasParamFloat2(), java.sql.Types.TINYINT);
            pstmt.setString(8, eventInfoType.getParamFloat2Description());
            pstmt.setObject(9, eventInfoType.getHasParamParticipantId1(), java.sql.Types.TINYINT);
            pstmt.setString(10, eventInfoType.getParamParticipantId1Description());
            pstmt.setObject(11, eventInfoType.getHasParamParticipantId2(), java.sql.Types.TINYINT);
            pstmt.setString(12, eventInfoType.getParamParticipantId2Description());
            pstmt.setObject(13, eventInfoType.getHasParamEventPartId1(), java.sql.Types.TINYINT);
            pstmt.setString(14, eventInfoType.getParamEventPartId1Description());
            pstmt.setObject(15, eventInfoType.getHasParamString1(), java.sql.Types.TINYINT);
            pstmt.setString(16, eventInfoType.getParamString1Description());
            pstmt.setString(17, eventInfoType.getParamString1PossibleValues());
            pstmt.setObject(18, eventInfoType.getHasParamBoolean1(), java.sql.Types.TINYINT);
            pstmt.setString(19, eventInfoType.getParamBoolean1Description());
            pstmt.setObject(20, eventInfoType.getHasParamEventStatusId1(), java.sql.Types.TINYINT);
            pstmt.setString(21, eventInfoType.getParamEventStatusId1Description());
            pstmt.setObject(22, eventInfoType.getHasParamTime1(), java.sql.Types.TINYINT);
            pstmt.setString(23, eventInfoType.getParamTime1Description());
            pstmt.setBoolean(24, eventInfoType.getParamParticipantIdsMustBeOrdered());
            pstmt.setBoolean(25, eventInfoType.getHasParamScoringUnitId1());
            pstmt.setString(26, eventInfoType.getParamScoringUnitId1Description());

            pstmt.executeUpdate();
        }
    }

    public void insertEventParticipantInfo(EventParticipantInfo eventParticipantInfo) throws SQLException {
        String insertSQL = "INSERT INTO eventparticipantinfo (id, version, typeId, eventId, providerId, statusId, eventPartId, participantId, isManuallySet) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, eventParticipantInfo.getId());
            pstmt.setInt(2, eventParticipantInfo.getVersion());
            pstmt.setLong(3, eventParticipantInfo.getTypeId());
            pstmt.setLong(4, eventParticipantInfo.getEventId());
            pstmt.setLong(5, eventParticipantInfo.getProviderId());
            pstmt.setLong(6, eventParticipantInfo.getStatusId());
            pstmt.setLong(7, eventParticipantInfo.getEventPartId());
            pstmt.setLong(8, eventParticipantInfo.getParticipantId());
            pstmt.setBoolean(9, eventParticipantInfo.getIsManuallySet());

            pstmt.executeUpdate();
        }
    }

    public void insertEventParticipantInfoDetail(EventParticipantInfoDetail eventParticipantInfoDetail)
            throws SQLException {
        String insertSQL = "INSERT INTO eventparticipantinfodetail (id, version, typeId, eventParticipantInfoId, statusId, paramFloat1, paramParticipantId1, paramBoolean1, paramString1, isManuallySet) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, eventParticipantInfoDetail.getId());
            pstmt.setInt(2, eventParticipantInfoDetail.getVersion());
            pstmt.setLong(3, eventParticipantInfoDetail.getTypeId());
            pstmt.setLong(4, eventParticipantInfoDetail.getEventParticipantInfoId());
            pstmt.setLong(5, eventParticipantInfoDetail.getStatusId());
            if (eventParticipantInfoDetail.getParamFloat1() != null) {
                pstmt.setDouble(6, eventParticipantInfoDetail.getParamFloat1());
            } else {
                pstmt.setNull(6, java.sql.Types.DOUBLE);
            }
            if (eventParticipantInfoDetail.getParamParticipantId1() != null) {
                pstmt.setLong(7, eventParticipantInfoDetail.getParamParticipantId1());
            } else {
                pstmt.setNull(7, java.sql.Types.BIGINT);
            }
            if (eventParticipantInfoDetail.getParamBoolean1() != null) {
                pstmt.setBoolean(8, eventParticipantInfoDetail.getParamBoolean1());
            } else {
                pstmt.setNull(8, java.sql.Types.TINYINT);
            }
            pstmt.setString(9, eventParticipantInfoDetail.getParamString1());
            pstmt.setBoolean(10, eventParticipantInfoDetail.getIsManuallySet());

            pstmt.executeUpdate();
        }
    }

    public void insertParticipantType(ParticipantType participantType) throws SQLException {
        String sql = "INSERT INTO ParticipantType (id, name, description) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, participantType.getId());
            pstmt.setString(2, participantType.getName());
            pstmt.setString(3, participantType.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertProvider(Provider provider) throws SQLException {
        String sql = "INSERT INTO provider (id, version, name, locationId, url, isBookmaker, isBettingExchange, bettingCommissionVACs, isLiveOddsApproved, isNewsSource, isEnabled, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, provider.getId());
            pstmt.setInt(2, provider.getVersion());
            pstmt.setString(3, provider.getName());
            pstmt.setLong(4, provider.getLocationId());
            pstmt.setString(5, provider.getUrl());
            pstmt.setBoolean(6, provider.getIsBookmaker());
            pstmt.setBoolean(7, provider.getIsBettingExchange());
            pstmt.setDouble(8, provider.getBettingCommissionVACs());
            pstmt.setBoolean(9, provider.getIsLiveOddsApproved());
            pstmt.setBoolean(10, provider.getIsNewsSource());
            pstmt.setBoolean(11, provider.getIsEnabled());
            pstmt.setString(12, provider.getNote());
            pstmt.executeUpdate();
        }
    }

    public void insertSource(Source source) throws SQLException {
        String sql = "INSERT INTO source (id, version, collectorId, providerId, sourceKey, lastCollectedTime, lastUpdatedTime) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, source.getId());
            pstmt.setInt(2, source.getVersion());
            pstmt.setLong(3, source.getCollectorId());
            pstmt.setLong(4, source.getProviderId());
            pstmt.setString(5, source.getSourceKey());

            if (source.getLastCollectedTime() != null) {
                pstmt.setTimestamp(6, new java.sql.Timestamp(source.getLastCollectedTime().getTime()));
            } else {

                pstmt.setTimestamp(6, java.sql.Timestamp.valueOf("1970-01-01 00:00:01"));
            }

            if (source.getLastUpdatedTime() != null) {
                pstmt.setTimestamp(7, new java.sql.Timestamp(source.getLastUpdatedTime().getTime()));
            } else {

                pstmt.setNull(7, java.sql.Types.TIMESTAMP);
            }

            pstmt.executeUpdate();
        }
    }

    public void insertBettingOfferStatus(BettingOfferStatus bettingOfferStatus) throws SQLException {

        String sql = "INSERT INTO bettingofferstatus (id, version, name, isAvailable, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, bettingOfferStatus.getId());
            pstmt.setInt(2, bettingOfferStatus.getVersion());
            pstmt.setString(3, bettingOfferStatus.getName());
            pstmt.setBoolean(4, bettingOfferStatus.getIsAvailable());
            pstmt.setString(5, bettingOfferStatus.getDescription());

            pstmt.executeUpdate();
        }
    }

    public void insertBettingType(BettingType bettingType) throws SQLException {
        String sql = "INSERT INTO bettingtype (id, name, description) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, bettingType.getId());
            pstmt.setString(2, bettingType.getName());
            pstmt.setString(3, bettingType.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertOutcome(Outcome outcome) throws SQLException {
        String sql = "INSERT INTO outcome (id, version, typeId, isNegation, statusId, eventId, eventPartId, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramBoolean1, paramString1, paramParticipantId1, paramParticipantId2, paramParticipantId3, paramEventPartId1, paramScoringUnitId1, code, name, shortCode, shortName, settlementRequired, isStatusManuallySet, namespaceId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, outcome.getId());
            pstmt.setInt(2, outcome.getVersion());
            pstmt.setLong(3, outcome.getTypeId());
            pstmt.setBoolean(4, outcome.getIsNegation());
            pstmt.setLong(5, outcome.getStatusId());
            pstmt.setLong(6, outcome.getEventId());
            pstmt.setLong(7, outcome.getEventPartId());
            pstmt.setObject(8, outcome.getParamFloat1(), java.sql.Types.DOUBLE);
            pstmt.setObject(9, outcome.getParamFloat2(), java.sql.Types.DOUBLE);
            pstmt.setObject(10, outcome.getParamFloat3(), java.sql.Types.DOUBLE);
            pstmt.setObject(11, outcome.getParamFloat4(), java.sql.Types.DOUBLE);
            pstmt.setObject(12, outcome.getParamFloat5(), java.sql.Types.DOUBLE);
            pstmt.setObject(13, outcome.getParamBoolean1(), java.sql.Types.TINYINT);
            pstmt.setString(14, outcome.getParamString1());
            pstmt.setObject(15, outcome.getParamParticipantId1(), java.sql.Types.BIGINT);
            pstmt.setObject(16, outcome.getParamParticipantId2(), java.sql.Types.BIGINT);
            pstmt.setObject(17, outcome.getParamParticipantId3(), java.sql.Types.BIGINT);
            pstmt.setObject(18, outcome.getParamEventPartId1(), java.sql.Types.BIGINT);
            pstmt.setObject(19, outcome.getParamScoringUnitId1(), java.sql.Types.BIGINT);
            pstmt.setString(20, outcome.getCode());
            pstmt.setString(21, outcome.getName());
            pstmt.setObject(22, outcome.getSettlementRequired(), java.sql.Types.TINYINT);
            pstmt.setBoolean(23, outcome.getIsStatusManuallySet());
            pstmt.setObject(24, outcome.getNamespaceId(), java.sql.Types.BIGINT);

            pstmt.executeUpdate();

        }
    }

    public void insertOutcomeType(OutcomeType outcomeType) throws SQLException {
        String sql = "INSERT INTO OutcomeType (id, name, description) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, outcomeType.getId());
            pstmt.setString(2, outcomeType.getName());
            pstmt.setString(3, outcomeType.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertOutcomeTypeBettingTypeRelation(OutcomeTypeBettingTypeRelation relation) throws SQLException {
        String sql = "INSERT INTO OutcomeTypeBettingTypeRelation (outcomeTypeId, bettingTypeId) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, relation.getOutcomeTypeId());
            pstmt.setLong(2, relation.getBettingTypeId());
            pstmt.executeUpdate();
        }
    }

    public void insertOutcomeStatus(OutcomeStatus outcomeStatus) throws SQLException {
        String sql = "INSERT INTO OutcomeStatus (id, name, description, isAvailable) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, outcomeStatus.getId());
            pstmt.setString(2, outcomeStatus.getName());
            pstmt.setString(3, outcomeStatus.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void insertBettingTypeUsage(BettingTypeUsage bettingTypeUsage) throws SQLException {
        String sql = "INSERT INTO BettingTypeUsage (id, bettingTypeId, sportId, isActive) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, bettingTypeUsage.getId());
            pstmt.setLong(2, bettingTypeUsage.getVersion());
            pstmt.setLong(3, bettingTypeUsage.getBettingTypeId());
            pstmt.setLong(4, bettingTypeUsage.getEventTypeId());
            pstmt.setLong(5, bettingTypeUsage.getSportId());
            pstmt.setLong(6, bettingTypeUsage.getEventPartId());
            pstmt.executeUpdate();
        }
    }

    //
    public void insertOutcomeTypeUsage(OutcomeTypeUsage outcomeTypeUsage) throws SQLException {
        String sql = "INSERT INTO OutcomeTypeUsage (outcomeTypeId, eventTypeId, isActive) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, outcomeTypeUsage.getId());
            pstmt.setInt(2, outcomeTypeUsage.getVersion());
            pstmt.setLong(3, outcomeTypeUsage.getOutcomeTypeId());
            pstmt.setLong(4, outcomeTypeUsage.getEventTypeId());
            pstmt.setLong(5, outcomeTypeUsage.getSportId());
            pstmt.setLong(6, outcomeTypeUsage.getScoringUnitId());
            pstmt.executeUpdate();
        }
    }

    public void insertMarket(Market market) throws SQLException {
        String sql = "INSERT INTO market (id, version, name, eventId, eventPartId, bettingTypeId, scoringUnitId, discriminator, numberOfOutcomes, isComplete, isClosed, paramFloat1, paramFloat2, paramFloat3, paramString1, paramParticipantId1, paramParticipantId2, paramParticipantId3) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, market.getId());
            pstmt.setInt(2, market.getVersion());
            pstmt.setString(3, market.getName());
            pstmt.setLong(4, market.getEventId());
            pstmt.setLong(5, market.getEventPartId());
            pstmt.setLong(6, market.getBettingTypeId());
            pstmt.setObject(7, market.getScoringUnitId(), java.sql.Types.BIGINT);
            pstmt.setString(8, market.getDiscriminator());
            pstmt.setObject(9, market.getNumberOfOutcomes(), java.sql.Types.INTEGER);
            pstmt.setBoolean(10, market.getIsComplete());
            pstmt.setBoolean(11, market.getIsClosed());
            pstmt.setObject(12, market.getParamFloat1(), java.sql.Types.DOUBLE);
            pstmt.setObject(13, market.getParamFloat2(), java.sql.Types.DOUBLE);
            pstmt.setObject(14, market.getParamFloat3(), java.sql.Types.DOUBLE);
            pstmt.setString(15, market.getParamString1());
            pstmt.setObject(16, market.getParamParticipantId1(), java.sql.Types.BIGINT);
            pstmt.setObject(17, market.getParamParticipantId2(), java.sql.Types.BIGINT);
            pstmt.setObject(18, market.getParamParticipantId3(), java.sql.Types.BIGINT);
            pstmt.executeUpdate();
        }
    }

    public void insertMarketOutcomeRelation(MarketOutcomeRelation marketOutcomeRelation) throws SQLException {
        String sql = "INSERT INTO MarketOutcomeRelation (marketId, outcomeId) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, marketOutcomeRelation.getMarketId());
            pstmt.setLong(2, marketOutcomeRelation.getOutcomeId());
            pstmt.executeUpdate();
        }
    }

    public void insertCurrency(Currency currency) throws SQLException {
        String sql = "INSERT INTO Currency (id, code, name) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, currency.getId());
            pstmt.setString(2, currency.getCode());
            pstmt.setString(3, currency.getName());
            pstmt.executeUpdate();
        }
    }

    public void insertProviderEntityMapping(ProviderEntityMapping providerEntityMapping) throws SQLException {
        String sql = "INSERT INTO ProviderEntityMapping (providerId, entityId, entityType) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, providerEntityMapping.getId());
            pstmt.setInt(2, providerEntityMapping.getVersion());
            pstmt.setLong(3, providerEntityMapping.getProviderId());
            pstmt.setString(4, providerEntityMapping.getProviderEntityTypeId());
            pstmt.setString(5, providerEntityMapping.getProviderEntityId());
            pstmt.setLong(6, providerEntityMapping.getEntityTypeId());
            pstmt.setLong(7, providerEntityMapping.getEntityId());
            pstmt.executeUpdate();
        }
    }

}
