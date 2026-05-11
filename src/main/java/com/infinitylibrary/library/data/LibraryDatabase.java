package com.infinitylibrary.library.data;

import com.infinitylibrary.InfinityLibraryPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class LibraryDatabase {
    private final InfinityLibraryPlugin plugin;
    private final String jdbcUrl;

    public LibraryDatabase(InfinityLibraryPlugin plugin) {
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder(), "library.db");
        this.jdbcUrl = "jdbc:sqlite:" + file.getAbsolutePath();
    }

    public void migrate() throws SQLException {
        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS library_entries (
                        id TEXT PRIMARY KEY,
                        title TEXT NOT NULL,
                        author TEXT NOT NULL,
                        category TEXT NOT NULL,
                        tags TEXT NOT NULL,
                        pages TEXT NOT NULL,
                        description TEXT NOT NULL,
                        created_at TEXT NOT NULL,
                        views INTEGER NOT NULL DEFAULT 0,
                        likes INTEGER NOT NULL DEFAULT 0
                    )
                    """);
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_library_title ON library_entries(title)");
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_library_author ON library_entries(author)");
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_library_category ON library_entries(category)");
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS player_reads (
                        player_uuid TEXT NOT NULL,
                        entry_id TEXT NOT NULL,
                        viewed_at TEXT NOT NULL,
                        PRIMARY KEY(player_uuid, entry_id)
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS server_events (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        event_type TEXT NOT NULL,
                        actor TEXT NOT NULL,
                        details TEXT NOT NULL,
                        happened_at TEXT NOT NULL
                    )
                    """);
        }
        plugin.getLogger().info("Library database migrated.");
    }

    public Connection open() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }
}
