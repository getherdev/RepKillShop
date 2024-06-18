package org.r3p.repkillshop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.r3p.repkillshop.KillShopPlugin;
import org.r3p.repkillshop.managers.ShopManager;

public class OpenShopCommand implements CommandExecutor {

    private final ShopManager shopManager;

    public OpenShopCommand(KillShopPlugin plugin) {
        this.shopManager = plugin.getShopManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tylko gracz moze wykonac ta komende!");
            return true;
        }

        Player player = (Player) sender;
        shopManager.openShop(player);
        return true;
    }
}
