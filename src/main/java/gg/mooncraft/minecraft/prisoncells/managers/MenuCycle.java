package gg.mooncraft.minecraft.prisoncells.managers;

import lombok.Getter;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gg.mooncraft.minecraft.prisoncells.database.objects.PrisonUser;
import gg.mooncraft.minecraft.prisoncells.menu.CellMenu;
import gg.mooncraft.minecraft.prisoncells.menu.FurnaceMenu;
import gg.mooncraft.minecraft.prisoncells.menu.StorageMenu;

@Getter
public final class MenuCycle {

    /*
    Fields
     */
    private final @NotNull Player player;
    private final @NotNull PrisonUser prisonUser;
    private @Nullable CellMenu cellMenu;
    private @Nullable StorageMenu storageMenu;
    private @Nullable FurnaceMenu furnaceMenu;

    /*
    Constructor
     */
    public MenuCycle(@NotNull Player player, @NotNull PrisonUser prisonUser) {
        this.player = player;
        this.prisonUser = prisonUser;
    }

    /*
    Methods
     */
    public void openCellMenu() {
        flush(true);
        this.cellMenu = new CellMenu(this);
        this.player.openInventory(this.cellMenu.getInventory());
    }

    public void openStorage() {
        flush(true);
        this.storageMenu = new StorageMenu(this.player, this.prisonUser);
        this.player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        this.player.openInventory(this.storageMenu.getInventory());
    }

    public void openFurnace(@NotNull FurnaceMenu furnaceMenu) {
        flush(false);
        this.furnaceMenu = furnaceMenu;
        this.player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        this.player.openInventory(this.furnaceMenu.getInventory());
    }

    public void flush(boolean fully) {
        this.furnaceMenu = null;
        this.storageMenu = null;
        if (fully && this.cellMenu != null) {
            this.cellMenu.getOwnFurnaceMap().values().forEach(streamFurnaceMenu -> {
                streamFurnaceMenu.getFurnaceTicker().stop();
                streamFurnaceMenu.getVirtualFurnace().update();
            });
            this.cellMenu = null;
        }
    }

    public boolean isLast() {
        return this.furnaceMenu == null && this.storageMenu == null && this.cellMenu != null;
    }
}