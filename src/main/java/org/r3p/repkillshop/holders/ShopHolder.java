package org.r3p.repkillshop.holders;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.r3p.repkillshop.database.DatabaseManager;
import org.r3p.repkillshop.managers.ShopManager;
import org.r3p.repkillshop.utils.ColorFixer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopHolder implements InventoryHolder {
    private Inventory inventory;

    public ShopHolder(Player player, FileConfiguration config, Map<Integer, ShopManager.ShopItem> shopItemMap, Map<Integer, ShopManager.UIElement> uiElementMap) {
        String shopTitle = ColorFixer.addColors(config.getString("shop.title"));
        int shopSize = config.getInt("shop.size");
        List<String> defaultLore = ColorFixer.addColors(config.getStringList("shop.lore"));

        inventory = Bukkit.createInventory(this, shopSize, shopTitle);
        fillUiWithItems(player, shopItemMap, uiElementMap, defaultLore);
    }

    public void fillUiWithItems(Player player, Map<Integer, ShopManager.ShopItem> shopItems, Map<Integer, ShopManager.UIElement> uiElements, List<String> defaultLore) {
        for (Map.Entry<Integer, ShopManager.UIElement> entry : uiElements.entrySet()) {
            ItemStack item = new ItemStack(entry.getValue().getMaterial());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ColorFixer.addColors(entry.getValue().getName()));
                List<String> lore = new ArrayList<>();
                for (String line : entry.getValue().getLore()) {
                    lore.add(line.replace("%your_kills%", String.valueOf(DatabaseManager.getKills(player.getUniqueId()))));
                }
                meta.setLore(ColorFixer.addColors(lore));
                item.setItemMeta(meta);
            }
            inventory.setItem(entry.getKey(), item);
        }

        for (Map.Entry<Integer, ShopManager.ShopItem> entry : shopItems.entrySet()) {
            ItemStack itemStack = entry.getValue().getItemStack().clone();
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                List<String> lore = new ArrayList<>();

                if (meta.getLore() != null) {
                    lore.addAll(meta.getLore());
                }

                for (String line : defaultLore) {
                    lore.add(line.replace("%price%", String.valueOf(entry.getValue().getPrice())));
                }

                meta.setLore(ColorFixer.addColors(lore));
                itemStack.setItemMeta(meta);
            }
            inventory.setItem(entry.getKey(), itemStack);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
