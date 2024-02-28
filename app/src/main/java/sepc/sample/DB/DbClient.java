package sepc.sample.DB;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Timestamp;
import com.betbrain.sepc.connector.sportsmodel.BettingOffer;
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

    public void insertBettingOffer(BettingOffer bettingOffer) throws SQLException {
        String insertSQL = "INSERT INTO bettingOffer (id, version, providerId, sourceId, outcomeId, bettingTypeId, statusId, isLive, odds, multiplicity, volume, volumeCurrencyId, couponKey, slotNum, lastChangedTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setLong(1, bettingOffer.getId());
            pstmt.setInt(2, bettingOffer.getVersion());
            pstmt.setLong(3, bettingOffer.getProviderId());
            pstmt.setLong(4, bettingOffer.getSourceId());
            pstmt.setLong(5, bettingOffer.getOutcomeId());
            pstmt.setLong(6, bettingOffer.getBettingTypeId());
            pstmt.setLong(7, bettingOffer.getStatusId());
            pstmt.setBoolean(8, bettingOffer.getIsLive());
            pstmt.setFloat(9, bettingOffer.getOdds());
            pstmt.setInt(10, bettingOffer.getMultiplicity());
            pstmt.setFloat(11, bettingOffer.getVolume());
            if (bettingOffer.getVolumeCurrencyId() != null) {
                pstmt.setLong(12, bettingOffer.getVolumeCurrencyId());
            } else {
                pstmt.setNull(12, java.sql.Types.BIGINT);
            }
            pstmt.setString(13, bettingOffer.getCouponKey());
            pstmt.setInt(14, bettingOffer.getSlotNum());
            pstmt.setTimestamp(15, new Timestamp(bettingOffer.getLastChangedTime().getTime()));

            pstmt.executeUpdate();
        }
    }
}
