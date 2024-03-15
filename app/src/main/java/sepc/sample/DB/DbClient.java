package sepc.sample.DB;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;
import java.util.stream.Collectors;
import java.sql.SQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import sepc.sample.utils.EnvLoader;

public class DbClient {

    private static final String DATABASE_NAME = System.getProperty("DB_NAME");

    private static final String USER = System.getProperty("DB_USER");
    private static final String PASSWORD = System.getProperty("DB_PASS");
    private HikariDataSource dataSource;
    private static final String SQL_FILE_PATH = "./src/main/resources/Tables.sql";
    private static final Logger logger = LoggerFactory.getLogger(DbClient.class);
    private static DbClient instance;
    public int batchSize = 100;

    public DbClient() {
        EnvLoader.load(".env");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getProperty("DB_URL"));
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(100);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(100000);
        this.dataSource = new HikariDataSource(config);

    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    public static DbClient getInstance() {
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

    public void deleteEntity(Long id, String table) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);

        } catch (SQLException e) {

        }
    }

    public void createEntity(String table, List<String> fields, List<Object> fieldValues) throws SQLException {
        if (fields.isEmpty() || fieldValues.isEmpty()) {

        }

        if (fields.size() != fieldValues.size()) {

        }

        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(table).append(" (");

        for (int i = 0; i < fields.size(); i++) {
            sql.append(fields.get(i));
            if (i < fields.size() - 1) {
                sql.append(", ");
            }
        }

        sql.append(") VALUES (");

        for (int i = 0; i < fieldValues.size(); i++) {
            sql.append("?");
            if (i < fieldValues.size() - 1) {
                sql.append(", ");
            }
        }

        sql.append(")");

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < fieldValues.size(); i++) {
                pstmt.setObject(i + 1, fieldValues.get(i));
            }
            pstmt.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {

        } catch (SQLException e) {

        }
    }

    public void createEntities(String table, List<String> fields, List<List<Object>> batchFieldValues)
            throws SQLException {
        String fieldNames = String.join(", ", fields);
        String questionMarks = String.join(", ", fields.stream().map(f -> "?").collect(Collectors.toList()));
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, fieldNames, questionMarks);

        try (Connection connection = this.dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            int counter = 0;
            for (List<Object> fieldValues : batchFieldValues) {
                for (int i = 0; i < fieldValues.size(); i++) {
                    statement.setObject(i + 1, fieldValues.get(i));
                }
                statement.addBatch();

                if ((counter + 1) % batchSize == 0 || (counter + 1) == batchFieldValues.size()) {
                    statement.executeBatch();
                    statement.clearBatch();
                }
                counter++;
            }
        } catch (Exception e) {

        }
    }

    public void updateEntity(Long id, String table, List<String> fields, List<Object> fieldvalues) throws SQLException {
        if (fields.isEmpty() || fieldvalues.isEmpty()) {
            throw new IllegalArgumentException("Fields and values cannot be empty.");
        }

        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(table).append(" SET ");

        for (int i = 0; i < fields.size(); i++) {
            sql.append(fields.get(i)).append(" = ?");
            if (i < fields.size() - 1) {
                sql.append(", ");
            }
        }

        sql.append(" WHERE id = ?");

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < fieldvalues.size(); i++) {
                pstmt.setObject(i + 1, fieldvalues.get(i));
            }

            pstmt.setObject(fields.size() + 1, id);

            pstmt.executeUpdate();
        }
    }
}
