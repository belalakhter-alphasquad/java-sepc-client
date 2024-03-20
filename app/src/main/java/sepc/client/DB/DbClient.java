package sepc.client.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.betbrain.sepc.connector.sportsmodel.Entity;
import java.util.List;
import java.util.Collections;
import java.sql.SQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import sepc.client.utils.EnvLoader;

public class DbClient {

    private HikariDataSource dataSource;

    private static final Logger logger = LoggerFactory.getLogger(DbClient.class);
    private static DbClient instance;

    public DbClient() {
        logger.info("Setting up database connection...");
        EnvLoader.load(".env");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getProperty("DB_URL"));
        config.setUsername(System.getProperty("DB_USER"));
        config.setPassword(System.getProperty("DB_PASS"));
        config.setMaximumPoolSize(100);
        config.addDataSourceProperty("cachePrepStmts", "true");
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

    public void deleteEntity(Long id, String table) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();

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
            logger.error("Exception -> createEntities() at Table: " + table + ", Exception: " + e);
        }
    }
}
