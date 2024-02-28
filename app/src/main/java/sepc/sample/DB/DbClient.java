package sepc.sample.DB;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.betbrain.sepc.connector.sportsmodel.Sport;
import com.google.common.collect.Tables;

public class DbClient {
    private static final String URL = "";
    private static final String DATABASE_NAME = "";
    private static final String USER = "";
    private static final String PASSWORD = "demo";
    private static final String SQL_FILE_PATH = "./src/main/resources/Tables.sql";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
    }

    public static void createDatabaseIfNotExist() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
        }
    }

    public static void runSqlFileToCreateTables() throws Exception {

        createDatabaseIfNotExist();

        String sqlCommands = new String(Files.readAllBytes(Paths.get(SQL_FILE_PATH)));

        String[] commands = sqlCommands.split(";");

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            for (String command : commands) {
                if (!command.trim().isEmpty()) {
                    stmt.execute(command);
                }
            }
        }
    }

    public static void insertSport(Sport sport) throws SQLException {
        String insertSQL = "INSERT INTO sport (id, version, name, description, parentId) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

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
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
