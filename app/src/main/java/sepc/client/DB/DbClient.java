package sepc.client.DB;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.nio.file.Files;
import com.betbrain.sepc.connector.sportsmodel.Entity;

import java.util.List;
import java.util.Collections;
import java.sql.SQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DbClient {

    private HikariDataSource dataSource;

    private static final Logger logger = LoggerFactory.getLogger(DbClient.class);
    private static DbClient instance;
    private static final String SQL_FILE_PATH = "./src/main/resources/Tables.sql";

    public DbClient(String DATABASE_NAME, String DB_URL, String DB_USER, String DB_Pass) {
        logger.info("Setting up database connection...");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_Pass);
        config.setMaximumPoolSize(100);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.setMinimumIdle(5);
        config.setConnectionTimeout(100000);
        this.dataSource = new HikariDataSource(config);

    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    public static DbClient getInstance(String DATABASE_NAME, String DB_URL, String DB_USER, String DB_Pass) {
        if (instance == null) {
            instance = new DbClient(DATABASE_NAME, DB_URL, DB_USER, DB_Pass);
        }
        return instance;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public void deleteEntity(Long id, String table) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Deleting entity failed for ID: " + id + "with exception: ", e);

        }
    }

    public void createDatabaseIfNotExist(String DATABASE_NAME) throws SQLException {
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
        }
    }

    public void runSqlFileToCreateTables(String DATABASE_NAME) throws Exception {
        createDatabaseIfNotExist(DATABASE_NAME);

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
            logger.error("Exception -> createEntities() at Table: " + table + ", Exception: " + e);

        } catch (SQLException e) {

            logger.error("Exception -> createEntities() at Table: " + table + ", Exception: " + e);
        }
    }

    public void createEntities(String table, List<Entity> uniqueEntities)
            throws SQLException {

        List<String> fields = uniqueEntities.get(0).getPropertyNames();
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(table).append(" (");
        sql.append(String.join(", ", fields));
        sql.append(") VALUES (");
        sql.append(String.join(", ", Collections.nCopies(fields.size(), "?")));
        sql.append(")");

        int batchSize = uniqueEntities.size();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            conn.setAutoCommit(false);
            int count = 0;

            List<Object> rowValues;

            for (Entity entity : uniqueEntities) {

                rowValues = entity.getPropertyValues(fields);

                for (int i = 0; i < rowValues.size(); i++) {
                    pstmt.setObject(i + 1, rowValues.get(i));
                }
                pstmt.addBatch();
                rowValues.clear();

                if (++count % batchSize == 0) {
                    pstmt.executeBatch();
                    conn.commit();
                    pstmt.clearBatch();
                    count = 0;
                }
            }

            if (count > 0) {
                pstmt.executeBatch();
                conn.commit();
                pstmt.clearBatch();
            }
        } catch (SQLException e) {
            logger.error("Exception -> createEntities() at Table: " + table + ", Exception: " + e);
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
        } catch (SQLException e) {
            logger.error("Exception -> update failed for entity at ID: " + id + ", Exception: " + e);
        }
    }
}
