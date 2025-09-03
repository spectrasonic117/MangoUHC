package com.spectrasonic.MangoUHC.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.managers.CageManager;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
@CommandAlias("cage")
@CommandPermission("mangouhc.cage")
public class CageCommand extends BaseCommand {

    private final Main plugin;
    private final CageManager cageManager;

    public CageCommand(Main plugin) {
        this.plugin = plugin;
        this.cageManager = plugin.getCageManager();
    }

    @Default
    public void onCageCommand(Player player) {
        player.sendMessage("§cUsage: /cage <on|off>");
    }

    @Subcommand("on")
    public void onCageOn(Player player) {
        if (cageManager.placeCage(player)) {
            player.sendMessage("§aCage placed successfully!");
        } else {
            player.sendMessage("§cFailed to place cage. Check console for details.");
        }
    }

    @Subcommand("off")
    public void onCageOff(Player player) {
        if (cageManager.removeCage(player)) {
            player.sendMessage("§aCage removed successfully!");
        } else {
            player.sendMessage("§cFailed to remove cage. Check console for details.");
        }
    }
}