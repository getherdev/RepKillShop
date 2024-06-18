package org.r3p.repkillshop.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.r3p.repkillshop.database.DatabaseManager;

public class KillListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DatabaseManager.loadPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            DatabaseManager.incrementKills(event.getEntity().getKiller());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DatabaseManager.savePlayer(event.getPlayer());
    }
}
