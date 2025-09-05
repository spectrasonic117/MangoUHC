package com.spectrasonic.MangoUHC.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;

import com.mojang.brigadier.Message;
import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.managers.CageManager;
import com.spectrasonic.Utils.MessageUtils;
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
        MessageUtils.sendMessage(player, "Usage: <gray>/cage <on|off></gray>");
    }

    @Subcommand("on")
    public void onCageOn(Player player) {
        if (cageManager.placeCage(player)) {
            MessageUtils.sendMessage(player, "<green>Cage placed successfully!");
        } else {
            MessageUtils.sendMessage(player, "<red>Failed to place cage. Check console for details.");
        }
    }

    @Subcommand("off")
    public void onCageOff(Player player) {
        if (cageManager.removeCage(player)) {
            MessageUtils.sendMessage(player, "<green>Cage removed successfully!");
        } else {
            MessageUtils.sendMessage(player, "<red>Failed to remove cage. Check console for details.");
        }
    }
}