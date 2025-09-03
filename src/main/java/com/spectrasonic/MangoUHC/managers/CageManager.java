package com.spectrasonic.MangoUHC.managers;

import com.spectrasonic.MangoUHC.Main;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.spectrasonic.Utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class CageManager {

    private final Main plugin;
    private Clipboard clipboard;
    private final Map<UUID, BlockVector3> placedCages = new HashMap<>();

    public boolean loadCageSchematic() {
        try {
            // Get the schematic file from the FAWE directory
            File schematicFile = new File("plugins/FastAsyncWorldEdit/schematics/cage.schem");

            if (!schematicFile.exists()) {
                MessageUtils.sendConsoleMessage("<red>Schematic not found: " + schematicFile.getPath());
                return false;
            }

            // Load the schematic
            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            if (format == null) {
                MessageUtils.sendConsoleMessage("<red>Unknown schematic format: " + schematicFile.getPath());
                return false;
            }

            try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
                clipboard = reader.read();
                MessageUtils.sendConsoleMessage("<green>Schematic loaded successfully");
                return true;
            }
        } catch (IOException e) {
            MessageUtils.sendConsoleMessage("<red>Error loading schematic: " + e.getMessage());
            return false;
        }
    }

    public boolean loadCageSchematic(Player player) {
        boolean result = loadCageSchematic();
        
        if (!result) {
            MessageUtils.sendMessage(player, "<red>Error cargando schematic: Revisa la consola para mas detalles");
        } else {
            MessageUtils.sendMessage(player, "<green>Schematic cargado exitosamente");
        }
        
        return result;
    }

    public boolean placeCage(Player player) {
        if (clipboard == null) {
            MessageUtils.sendMessage(player, "<red>No se pudo colocar la Jaula");
            return false;
        }

        try {
            Location location = player.getLocation();
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(location.getWorld());

            // Store the position where the cage is placed
            BlockVector3 pos = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            placedCages.put(player.getUniqueId(), pos);

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(pos)
                        .ignoreAirBlocks(true)
                        .build();

                Operations.complete(operation);
                MessageUtils.sendMessage(player, "<green>Jaula colocada exitosamente en " + location.getBlockX()
                        + ", " + location.getBlockY() + ", " + location.getBlockZ());
                return true;
            }
        } catch (Exception e) {
            MessageUtils.sendMessage(player, "<red>Error colocando jaula: " + e.getMessage());
            return false;
        }
    }

    public boolean removeCage(Player player) {
        if (!placedCages.containsKey(player.getUniqueId())) {
            MessageUtils.sendMessage(player, "<red>No tienes una jaula colocada");
            return false;
        }

        try {
            BlockVector3 pos = placedCages.get(player.getUniqueId());
            Location location = player.getLocation();
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(location.getWorld());

            // Calculate the region to clear based on the clipboard dimensions
            BlockVector3 origin = clipboard.getOrigin();
            BlockVector3 size = clipboard.getDimensions();

            BlockVector3 min = pos.subtract(origin).subtract(size.divide(2));
            BlockVector3 max = pos.subtract(origin).add(size.divide(2));

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                // Fill the region with air
                Region region = new com.sk89q.worldedit.regions.CuboidRegion(world, min, max);
                BaseBlock airBlock = BlockTypes.AIR.getDefaultState().toBaseBlock();
                editSession.setBlocks((Region) region, new BlockPattern(airBlock));
                MessageUtils.sendMessage(player, "<green>Jaula eliminada exitosamente en " + pos.getBlockX() + ", "
                        + pos.getBlockY() + ", " + pos.getBlockZ());
            }

            // Remove the player from the map
            placedCages.remove(player.getUniqueId());
            return true;
        } catch (Exception e) {
            MessageUtils.sendMessage(player, "<red>Error eliminando jaula: " + e.getMessage());
            return false;
        }
    }
}