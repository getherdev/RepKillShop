package org.r3p.repkillshop.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.r3p.repkillshop.KillShopPlugin;
import org.r3p.repkillshop.database.DatabaseManager;
import org.r3p.repkillshop.managers.ShopManager;
import org.r3p.repkillshop.utils.ColorFixer;

public class ShopAdminCommand implements CommandExecutor {

    private final KillShopPlugin plugin;
    private final ShopManager shopManager;

    public ShopAdminCommand(KillShopPlugin plugin) {
        this.plugin = plugin;
        this.shopManager = plugin.getShopManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorFixer.addColors("Tylko gracz może wykonać tę komendę!"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §cBrak uprawnień!"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §cUżycie: /killshopadmin §4<§cadditem §4|§c givekills §4|§c clearkills §4|§c removekills §4|§c reload §4|§c removeitem§4>"));
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "additem":
                handleAddItem(player, args);
                break;
            case "givekills":
                handleGiveKills(player, args);
                break;
            case "clearkills":
                handleClearKills(player, args);
                break;
            case "removekills":
                handleRemoveKills(player, args);
                break;
            case "reload":
                handleReload(player);
                break;
            case "removeitem":
                handleRemoveItem(player, args);
                break;
            default:
                player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §cNieznana komenda!"));
                break;
        }

        return true;
    }

    private void handleAddItem(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §c/shopadmin additem <slot> <cena>"));
            return;
        }

        try {
            int slot = Integer.parseInt(args[1]);
            int price = Integer.parseInt(args[2]);
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            shopManager.addItem(slot, itemInHand, price);
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §aPomyślnie dodano przedmiot do sklepu na slot §2" + slot + "§a za cenę §2" + price + "§a zabójstw!"));
        } catch (NumberFormatException e) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §cCena oraz slot muszą być liczbami!"));
        }
    }

    private void handleGiveKills(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §c/shopadmin givekills <gracz> <ilość>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §cGracz nie znaleziony!"));
            return;
        }

        try {
            int amount = Integer.parseInt(args[2]);
            DatabaseManager.incrementKills(target, amount);
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §aPomyślnie dodano §2" + amount + "§a zabójstw graczowi §2" + target.getName() + "§a!"));
            target.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §aOtrzymałeś §2" + amount + "§a zabójstw od administratora!"));
        } catch (NumberFormatException e) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §cIlość musi być liczbą!"));
        }
    }

    private void handleClearKills(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §c/shopadmin clearkills <gracz>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §cGracz nie znaleziony!"));
            return;
        }

        DatabaseManager.clearKills(target);
        player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §aPomyślnie wyczyszczono zabójstwa gracza §2" + target.getName() + "§a!"));
        target.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §aTwoje zabójstwa zostały wyczyszczone przez administratora!"));
    }

    private void handleRemoveKills(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §c/shopadmin removekills <gracz> <ilość>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §cGracz nie znaleziony!"));
            return;
        }

        try {
            int amount = Integer.parseInt(args[2]);
            DatabaseManager.decrementKills(target.getUniqueId(), amount);
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §aPomyślnie usunięto §2" + amount + "§a zabójstw graczowi §2" + target.getName() + "§a!"));
            target.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §aUsunięto §2" + amount + "§a zabójstw przez administratora!"));
        } catch (NumberFormatException e) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §cIlość musi być liczbą!"));
        }
    }

    private void handleReload(Player player) {
        plugin.reloadPlugin();
        shopManager.initializeShop();
        player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §aPlugin został przeładowany!"));
    }

    private void handleRemoveItem(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §c/shopadmin removeitem <slot>"));
            return;
        }

        try {
            int slot = Integer.parseInt(args[1]);
            shopManager.removeItem(slot);
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §aPrzedmiot ze slotu §2" + slot + "§a został usunięty!"));
        } catch (NumberFormatException e) {
            player.sendMessage(ColorFixer.addColors("§x§2§A§6§5§F§FR§x§3§1§6§A§F§FE§x§3§9§7§0§F§FP§x§4§0§7§5§F§F-§x§4§8§7§B§F§FK§x§4§F§8§0§F§FS §8>> §cSlot musi być liczbą!"));
        }
    }
}
