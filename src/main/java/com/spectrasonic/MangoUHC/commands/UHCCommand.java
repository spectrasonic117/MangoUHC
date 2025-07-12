package com.spectrasonic.MangoUHC.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.spectrasonic.MangoUHC.enums.UHCState;
import com.spectrasonic.MangoUHC.managers.UHCGameManager;
import com.spectrasonic.MangoUHC.managers.ConfigManager;
import com.spectrasonic.Utils.MessageUtils;
import org.bukkit.command.CommandSender;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@CommandAlias("uhc")
public class UHCCommand extends BaseCommand {

    private final UHCGameManager uhcGameManager;
    private final ConfigManager configManager;

    @Default
    public void onDefault(CommandSender sender) {
        MessageUtils.sendMessage(sender, "<red>Uso: /uhc <start|stop|reload>");
    }

    @Subcommand("start")
    public void onStart(CommandSender sender) {
        if (!sender.hasPermission("mangouhc.command.uhc")) {
            MessageUtils.sendPermissionMessage(sender);
            return;
        }

        if (uhcGameManager.getCurrentState() == UHCState.RUNNING) {
            MessageUtils.sendMessage(sender, "<red>El UHC ya está en ejecución.");
            return;
        }

        uhcGameManager.toggleUHCState(); // This will start the UHC
        MessageUtils.sendMessage(sender, "<green>Has iniciado el UHC.");
    }

    @Subcommand("stop")
    public void onStop(CommandSender sender) {
        if (!sender.hasPermission("mangouhc.command.uhc")) {
            MessageUtils.sendPermissionMessage(sender);
            return;
        }

        if (uhcGameManager.getCurrentState() == UHCState.STOPPED) {
            MessageUtils.sendMessage(sender, "<red>El UHC ya está detenido.");
            return;
        }

        uhcGameManager.toggleUHCState(); // This will stop the UHC
        MessageUtils.sendMessage(sender, "<green>Has detenido el UHC.");
    }

    @Subcommand("reload")
    public void onReload(CommandSender sender) {
        if (!sender.hasPermission("mangouhc.command.uhc.reload")) {
            MessageUtils.sendPermissionMessage(sender);
            return;
        }

        try {
            configManager.reloadConfig();
            MessageUtils.sendMessage(sender, "<green>Configuracion recargada exitosamente.");
        } catch (Exception e) {
            MessageUtils.sendMessage(sender, "<red>Error al recargar la configuracion: " + e.getMessage());
        }
    }
}
