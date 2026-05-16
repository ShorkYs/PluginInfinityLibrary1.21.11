package com.infinitylibrary.database;

import com.infinitylibrary.InfinityLibraryPlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlManager {
    private final InfinityLibraryPlugin plugin;
    private HikariDataSource dataSource;

    public MySqlManager(InfinityLibraryPlugin plugin) { this.plugin = plugin; }

    public void connect() {
        if (!plugin.getConfig().getBoolean("mysql.enabled", false)) return;
        HikariConfig config = new HikariConfig();
        String host = plugin.getConfig().getString("mysql.host", "localhost");
        int port = plugin.getConfig().getInt("mysql.port", 3306);
        String database = plugin.getConfig().getString("mysql.database", "infinitylibrary");
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true");
        config.setUsername(plugin.getConfig().getString("mysql.username", "root"));
        config.setPassword(plugin.getConfig().getString("mysql.password", ""));
        config.setMaximumPoolSize(Math.max(2, plugin.getConfig().getInt("mysql.pool-size", 8)));
        config.setPoolName("InfinityLibraryPool");
        dataSource = new HikariDataSource(config);
        bootstrap();
    }

    private void bootstrap() {
        if (dataSource == null) return;
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS newspaper_events(id BIGINT PRIMARY KEY AUTO_INCREMENT, event_type VARCHAR(40), player_name VARCHAR(32), payload TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_preferences(player_uuid VARCHAR(36) PRIMARY KEY, favorite_categories TEXT, favorite_tags TEXT, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS book_activity(id BIGINT PRIMARY KEY AUTO_INCREMENT, player_uuid VARCHAR(36), title VARCHAR(255), category VARCHAR(120), action VARCHAR(40), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        } catch (SQLException ex) {
            plugin.getLogger().warning("MySQL bootstrap failed: " + ex.getMessage());
        }
    }

    public Connection connection() throws SQLException {
        if (dataSource == null) throw new SQLException("MySQL not connected");
        return dataSource.getConnection();
    }

    public boolean isEnabled() { return dataSource != null; }

    public void close() {
        if (dataSource != null) dataSource.close();
        dataSource = null;
    }
}
