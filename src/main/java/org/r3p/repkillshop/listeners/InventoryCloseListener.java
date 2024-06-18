package org.r3p.repkillshop.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.r3p.repkillshop.managers.ShopManager;
import org.bukkit.entity.Player;

public class InventoryCloseListener implements Listener {

    private final ShopManager shopManager;

    public InventoryCloseListener(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(shopManager.getShopTitle())) {
            shopManager.cleanup((Player) event.getPlayer());
        }
    }
}
