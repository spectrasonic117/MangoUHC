package com.spectrasonic.MangoUHC.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.spectrasonic.MangoUHC.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import lombok.RequiredArgsConstructor;

@CommandAlias("mango")
@CommandPermission("mangouhc.mango")
@RequiredArgsConstructor
public class MangoCommand extends BaseCommand {

    private final Main plugin;

    @Default
    public void onMangoCommand(CommandSender sender) {
        sendInfoMessage(sender);
    }

    @Subcommand("version")
    public void onVersionCommand(CommandSender sender) {
        sendInfoMessage(sender);
    }

    private void sendInfoMessage(CommandSender sender) {
        String message = 
            "<dark_gray>-------------------\n" +
            "<yellow>MangoUHC\n\n" +
            "<aqua>Version: <light_purple>" + plugin.getPluginMeta().getVersion() + "\n" +
            "<aqua>Developer: <red>" + plugin.getPluginMeta().getAuthors().get(0) + "\n" +
            "<white>A <gradient:#CF1E2F:#FCB62C:#798F29>Mango Studios <white>Plugin\n" +
            "<dark_gray>-------------------";
        
        Bukkit.getServer().sendMessage(MiniMessage.miniMessage().deserialize(message));
    }
}
