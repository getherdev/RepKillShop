package org.r3p.repkillshop.managers;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.r3p.repkillshop.database.DatabaseManager;
import org.r3p.repkillshop.holders.ShopHolder;
import org.r3p.repkillshop.utils.ColorFixer;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ShopManager {
    private final Map<Integer, ShopItem> shopItems = new HashMap<>();
    private final Map<Integer, UIElement> uiElements = new HashMap<>();
    private final String shopTitle;
    private final int shopSize;
    private final List<String> defaultLore;
    private FileConfiguration config;
    private final File configFile;

    private final String notEnoughKillsMessage;
    private final String purchaseSuccessMessage;
    private Inventory inventory;

    private final Set<UUID> playersWithOpenShop = new HashSet<>();

    public ShopManager(FileConfiguration config, File configFile) {
        this.config = config;
        this.configFile = configFile;
        this.shopTitle = ColorFixer.addColors(config.getString("shop.title"));
        this.shopSize = config.getInt("shop.size");
        this.defaultLore = ColorFixer.addColors(config.getStringList("shop.lore"));

        this.notEnoughKillsMessage = ColorFixer.addColors(config.getString("messages.not_enough_kills"));
        this.purchaseSuccessMessage = ColorFixer.addColors(config.getString("messages.purchase_success"));

        loadUIElements(config);
        loadShopItems(config);
    }

    public void initializeShop() {
        loadUIElements(config);
        loadShopItems(config);
    }

    public void reload(File configFile) {
        this.config = YamlConfiguration.loadConfiguration(configFile);
        initializeShop();
    }

    private void loadUIElements(FileConfiguration config) {
        uiElements.clear();
        for (String key : config.getConfigurationSection("shop.ui_elements").getKeys(false)) {
            int slot = Integer.parseInt(key);
            String type = config.getString("shop.ui_elements." + slot + ".type");
            String name = config.getString("shop.ui_elements." + slot + ".name");
            List<String> lore = config.getStringList("shop.ui_elements." + slot + ".lore");

            Material material = Material.getMaterial(type.toUpperCase());
            if (material != null) {
                uiElements.put(slot, new UIElement(material, name, lore));
            }
        }
    }

    private void loadShopItems(FileConfiguration config) {
        shopItems.clear();
        if (config.isConfigurationSection("shop-items")) {
            for (String key : config.getConfigurationSection("shop-items").getKeys(false)) {
                int slot = Integer.parseInt(key);
                ItemStack itemStack = config.getItemStack("shop-items." + slot + ".itemstack");
                int price = config.getInt("shop-items." + slot + ".price");
                shopItems.put(slot, new ShopItem(itemStack, price));
            }
        }
    }

    public void openShop(Player player) {
        ShopHolder shopHolder = new ShopHolder(player, config, shopItems, uiElements);
        player.openInventory(shopHolder.getInventory());
    }

    public void addItem(int slot, ItemStack itemInHand, int price) {
        ItemStack itemStack = itemInHand.clone();
        shopItems.put(slot, new ShopItem(itemStack, price));

        config.set("shop-items." + slot + ".itemstack", itemStack);
        config.set("shop-items." + slot + ".price", price);
        saveConfig();
    }

    public void removeItem(int slot) {
        shopItems.remove(slot);
        config.set("shop-items." + slot, null);
        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleInventoryClick(Player player, int slot) {
        if (shopItems.containsKey(slot)) {
            ShopItem shopItem = shopItems.get(slot);
            int playerKills = DatabaseManager.getKills(player.getUniqueId());
            if (playerKills >= shopItem.getPrice()) {
                DatabaseManager.decrementKills(player.getUniqueId(), shopItem.getPrice());
                player.getInventory().addItem(shopItem.getItemStack().clone());
                player.sendMessage(purchaseSuccessMessage);
            } else {
                player.sendMessage(notEnoughKillsMessage);
            }
        }
    }

    public void cleanup(Player player) {
        playersWithOpenShop.remove(player.getUniqueId());
    }

    public String getShopTitle() {
        return shopTitle;
    }

    public static class ShopItem {
        private final ItemStack itemStack;
        private final int price;

        public ShopItem(ItemStack itemStack, int price) {
            this.itemStack = itemStack;
            this.price = price;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public int getPrice() {
            return price;
        }
    }

    public static class UIElement {
        private final Material material;
        private final String name;
        private final List<String> lore;

        public UIElement(Material material, String name, List<String> lore) {
            this.material = material;
            this.name = name;
            this.lore = lore;
        }

        public Material getMaterial() {
            return material;
        }

        public String getName() {
            return name;
        }

        public List<String> getLore() {
            return lore;
        }

        public ItemMeta getItemMeta() {
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ColorFixer.addColors(name));
                meta.setLore(ColorFixer.addColors(lore));
            }
            return meta;
        }
    }
}
