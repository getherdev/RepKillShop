package org.r3p.repkillshop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.r3p.repkillshop.commands.OpenShopCommand;
import org.r3p.repkillshop.commands.ShopAdminCommand;
import org.r3p.repkillshop.database.DatabaseManager;
import org.r3p.repkillshop.listeners.InventoryClickListener;
import org.r3p.repkillshop.listeners.InventoryCloseListener;
import org.r3p.repkillshop.listeners.KillListener;
import org.r3p.repkillshop.managers.ShopManager;

import java.io.File;

public class KillShopPlugin extends JavaPlugin {

    private ShopManager shopManager;

    public void onEnable() {
        saveDefaultConfig();
        this.shopManager = new ShopManager(this.getConfig(), new File(this.getDataFolder(), "config.yml"));

        this.getServer().getPluginManager().registerEvents(new KillListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryCloseListener(this.shopManager), this);
        this.getServer().getPluginManager().registerEvents(new InventoryClickListener(this.shopManager), this);
        this.getCommand("killshopadmin").setExecutor(new ShopAdminCommand(this));
        this.getCommand("sklepzakille").setExecutor(new OpenShopCommand(this));

        DatabaseManager.initialize(this);
        this.shopManager.initializeShop();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        DatabaseManager.close();
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public void reloadPlugin() {
        reloadConfig();
        shopManager.reload(new File(getDataFolder(), "config.yml"));
        closeAllOpenShopGUIs();
    }

    private void closeAllOpenShopGUIs() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory() != null && player.getOpenInventory().getTitle().equals(shopManager.getShopTitle())) {
                player.closeInventory();
            }
        }
    }
}
