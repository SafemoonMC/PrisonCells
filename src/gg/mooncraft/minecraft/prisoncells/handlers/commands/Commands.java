package gg.mooncraft.minecraft.prisoncells.handlers.commands;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import me.eduardwayland.mooncraft.waylander.command.LiteralCommand;
import me.eduardwayland.mooncraft.waylander.command.builders.LiteralCommandBuilder;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import gg.mooncraft.minecraft.prisoncells.PrisonCellsMain;
import gg.mooncraft.minecraft.prisoncells.menu.StorageMenu;

/**
 * Commands:
 * /cells
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Commands {

    public static void loadAll() {
        LiteralCommand<?> cellsCommand = LiteralCommandBuilder
                .<Player>name("cells").permission(new Permission("prisoncells.cells", PermissionDefault.TRUE))
                .executes(commandSender -> {
                    PrisonCellsMain.getInstance().getUserManager().readUser(commandSender.getUniqueId()).thenAccept(prisonUser -> {
                        StorageMenu storageMenu = new StorageMenu(prisonUser);
                        PrisonCellsMain.getInstance().getScheduler().executeSync(() -> commandSender.openInventory(storageMenu.getInventory()));
                    });
                })
                .build();

        PrisonCellsMain.getInstance().registerCommand(cellsCommand);
        PrisonCellsMain.getInstance().getLogger().info("Commands have been loaded.");
    }
}