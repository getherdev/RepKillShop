package org.r3p.repkillshop.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.r3p.repkillshop.holders.ShopHolder;
import org.r3p.repkillshop.managers.ShopManager;

public class InventoryClickListener implements Listener {
    private final ShopManager shopManager;

    public InventoryClickListener(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if(inventory == null){
            return;
        }
        InventoryHolder holder = inventory.getHolder();
        if(holder instanceof ShopHolder shopHolder){
            event.setCancelled(true);
            shopManager.handleInventoryClick((Player) event.getWhoClicked(), event.getRawSlot());
        }
    }
}
