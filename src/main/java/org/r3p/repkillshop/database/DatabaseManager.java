package org.r3p.repkillshop.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.r3p.repkillshop.KillShopPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {
    private static Connection connection;
    private static final Map<UUID, Integer> killsCache = new HashMap<>();

    public static void initialize(KillShopPlugin plugin) {
        try {
            String url = plugin.getConfig().getString("database.url");
            String user = plugin.getConfig().getString("database.user");
            String password = plugin.getConfig().getString("database.password");
            connection = DriverManager.getConnection(url, user, password);
            try (PreparedStatement stmt = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS player_kills (" +
                            "uuid VARCHAR(36) PRIMARY KEY, " +
                            "kills INT DEFAULT 0)")) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskTimer(plugin, DatabaseManager::saveAll, 6000L, 6000L);
    }

    public static void loadPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT kills FROM player_kills WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                killsCache.put(uuid, rs.getInt("kills"));
            } else {
                killsCache.put(uuid, 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void incrementKills(Player player) {
        UUID uuid = player.getUniqueId();
        killsCache.put(uuid, killsCache.getOrDefault(uuid, 0) + 1);
    }

    public static void incrementKills(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        killsCache.put(uuid, killsCache.getOrDefault(uuid, 0) + amount);
    }

    public static void clearKills(Player player) {
        UUID uuid = player.getUniqueId();
        killsCache.put(uuid, 0);
    }

    public static int getKills(UUID uuid) {
        return killsCache.getOrDefault(uuid, 0);
    }

    public static void decrementKills(UUID uuid, int amount) {
        killsCache.put(uuid, killsCache.getOrDefault(uuid, 0) - amount);
    }

    public static void saveAll() {
        for (Map.Entry<UUID, Integer> entry : killsCache.entrySet()) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO player_kills (uuid, kills) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE kills = VALUES(kills)")) {
                stmt.setString(1, entry.getKey().toString());
                stmt.setInt(2, entry.getValue());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void savePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (killsCache.containsKey(uuid)) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO player_kills (uuid, kills) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE kills = VALUES(kills)")) {
                stmt.setString(1, uuid.toString());
                stmt.setInt(2, killsCache.get(uuid));
                stmt.executeUpdate();
                killsCache.remove(uuid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close() {
        saveAll();
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
