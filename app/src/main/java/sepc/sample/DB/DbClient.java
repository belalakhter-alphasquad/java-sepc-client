package sepc.sample.DB;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.betbrain.sepc.connector.sportsmodel.Entity;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import sepc.sample.utils.EnvLoader;

public class DbClient {
    private Map<String, List<Entity>> batchData = new HashMap<>();
    private static final int BATCH_SIZE = 10000;

    private static final String DATABASE_NAME = System.getProperty("DB_NAME");

    private static final String USER = System.getProperty("DB_USER");
    private static final String PASSWORD = System.getProperty("DB_PASS");
    private HikariDataSource dataSource;
    private static final String SQL_FILE_PATH = "./src/main/resources/Tables.sql";
    private static final Logger logger = LoggerFactory.getLogger(DbClient.class);
    private static DbClient instance;

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

    public void createEntitiesBatch(String table, List<String> fields, List<Entity> entities) throws SQLException {
        if (fields.isEmpty() || entities.isEmpty()) {
            throw new IllegalArgumentException("Fields and entities cannot be empty.");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(table).append(" (");

        sql.append(String.join(", ", fields));
        sql.append(") VALUES ");

        String valuePlaceholder = "(" + fields.stream().map(f -> "?").collect(Collectors.joining(", ")) + ")";
        sql.append(
                IntStream.range(0, entities.size()).mapToObj(i -> valuePlaceholder).collect(Collectors.joining(", ")));

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            for (Entity entity : entities) {
                List<Object> values = entity.getPropertyValues(fields);
                for (Object value : values) {
                    if (value instanceof Integer) {
                        pstmt.setInt(index++, (Integer) value);
                    } else if (value instanceof Integer) {
                        pstmt.setInt(index++, (Integer) value);
                    } else if (value instanceof String) {
                        pstmt.setString(index++, (String) value);
                    } else if (value instanceof Double) {
                        pstmt.setDouble(index++, (Double) value);
                    } else if (value instanceof Long) {
                        pstmt.setLong(index++, (Long) value);
                    } else if (value instanceof Float) {
                        pstmt.setFloat(index++, (Float) value);
                    } else if (value instanceof Boolean) {
                        pstmt.setBoolean(index++, (Boolean) value);
                    } else if (value instanceof Timestamp) {
                        pstmt.setTimestamp(index++, (Timestamp) value);
                    } else {
                        pstmt.setObject(index++, value);
                    }
                }
            }
            pstmt.executeBatch();
        }

    }

    public void createEntity(String table, List<String> fields, List<Object> fieldvalues) throws SQLException {
        if (fields.isEmpty() || fieldvalues.isEmpty()) {
            throw new IllegalArgumentException("Fields and values cannot be empty.");
        }
        if (fields.size() != fieldvalues.size()) {
            throw new IllegalArgumentException("The number of fields must match the number of field values.");
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

        for (int i = 0; i < fieldvalues.size(); i++) {
            sql.append("?");
            if (i < fieldvalues.size() - 1) {
                sql.append(", ");
            }
        }

        sql.append(")");

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < fieldvalues.size(); i++) {
                Object value = fieldvalues.get(i);
                if (value == null) {
                    pstmt.setNull(i + 1, java.sql.Types.NULL);
                } else if (value instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) value);
                } else if (value instanceof String) {
                    pstmt.setString(i + 1, (String) value);
                } else if (value instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) value);
                } else if (value instanceof Long) {
                    pstmt.setLong(i + 1, (Long) value);
                } else if (value instanceof Float) {
                    pstmt.setFloat(i + 1, (Float) value);
                } else if (value instanceof Boolean) {
                    pstmt.setBoolean(i + 1, (Boolean) value);
                } else if (value instanceof Timestamp) {
                    pstmt.setTimestamp(i + 1, (Timestamp) value);
                } else {
                    pstmt.setObject(i + 1, value);
                }
            }

            pstmt.executeUpdate();

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
                Object value = fieldvalues.get(i);

                if (value == null) {
                    pstmt.setNull(i + 1, java.sql.Types.NULL);
                } else if (value instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) value);
                } else if (value instanceof String) {
                    pstmt.setString(i + 1, (String) value);
                } else if (value instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) value);
                } else if (value instanceof Long) {
                    pstmt.setLong(i + 1, (Long) value);
                } else if (value instanceof Float) {
                    pstmt.setFloat(i + 1, (Float) value);
                } else if (value instanceof Boolean) {
                    pstmt.setBoolean(i + 1, (Boolean) value);
                } else if (value instanceof Timestamp) {
                    pstmt.setDate(i + 1, new java.sql.Date(((Timestamp) value).getTime()));
                } else if (value instanceof Timestamp) {
                    pstmt.setTimestamp(i + 1, (Timestamp) value);
                } else {
                    pstmt.setObject(i + 1, value);
                }
            }

            pstmt.setObject(fields.size() + 1, id);

        }
    }

}
