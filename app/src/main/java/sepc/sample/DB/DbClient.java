package sepc.sample.DB;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Timestamp;
import com.betbrain.sepc.connector.sportsmodel.BettingOffer;
import com.betbrain.sepc.connector.sportsmodel.Event;
import com.betbrain.sepc.connector.sportsmodel.Sport;

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

}
