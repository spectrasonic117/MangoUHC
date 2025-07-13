package com.spectrasonic.MangoUHC.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Single;
import com.spectrasonic.MangoUHC.managers.UHCGameManager;
import com.spectrasonic.MangoUHC.managers.ConfigManager;
import com.spectrasonic.Utils.MessageUtils;
import org.bukkit.command.CommandSender;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@CommandAlias("pvp")
public class PvPCommand extends BaseCommand {

    private final UHCGameManager uhcGameManager;
    private final ConfigManager configManager;

    @Default
    @CommandCompletion("@pvp-states")
    public void onPvP(CommandSender sender, @Single String state) {
        if (!sender.hasPermission("mangouhc.command.pvp")) {
            MessageUtils.sendPermissionMessage(sender);
            return;
        }

        if (state == null) {
            // Show current PvP status
            boolean currentPvP = configManager.isPvPEnabled();
            MessageUtils.sendMessage(sender, "<yellow>Estado actual del PvP: " + 
                (currentPvP ? "<green>Habilitado" : "<red>Deshabilitado"));
            return;
        }

        boolean enabled;
        switch (state.toLowerCase()) {
            case "true":
            case "on":
            case "enable":
            case "1":
                enabled = true;
                break;
            case "false":
            case "off":
            case "disable":
            case "0":
                enabled = false;
                break;
            default:
                MessageUtils.sendMessage(sender, "<red>Uso: /pvp [true|false]");
                MessageUtils.sendMessage(sender, "<yellow>Estado actual del PvP: " + 
                    (configManager.isPvPEnabled() ? "<green>Habilitado" : "<red>Deshabilitado"));
                return;
        }

        // Update config
        configManager.setPvPEnabled(enabled);
        
        // Apply to all worlds immediately
        uhcGameManager.setPvP(enabled);

        // Send confirmation message
        String status = enabled ? "<green>habilitado" : "<red>deshabilitado";
        MessageUtils.sendMessage(sender, "<yellow>PvP " + status + "<yellow> globalmente en todos los mundos.");
        MessageUtils.sendBroadcastMessage("<yellow>El PvP ha sido " + status + "<yellow> por un administrador.");
    }

    @Default
    public void onDefault(CommandSender sender) {
        if (!sender.hasPermission("mangouhc.command.pvp")) {
            MessageUtils.sendPermissionMessage(sender);
            return;
        }

        // Show current PvP status when no arguments provided
        boolean currentPvP = configManager.isPvPEnabled();
        MessageUtils.sendMessage(sender, "<yellow>Uso: /pvp [true|false]");
        MessageUtils.sendMessage(sender, "<yellow>Estado actual del PvP: " + 
            (currentPvP ? "<green>Habilitado" : "<red>Deshabilitado"));
    }
}