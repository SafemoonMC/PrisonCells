package gg.mooncraft.minecraft.prisoncells.menu;

import lombok.Getter;

import org.bukkit.craftbukkit.v1_17_R1.inventory.util.CraftTileInventoryConverter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.database.objects.PrisonUser;
import gg.mooncraft.minecraft.prisoncells.database.objects.VirtualFurnace;
import gg.mooncraft.minecraft.prisoncells.tasks.FurnaceTicker;

@Getter
public class FurnaceMenu implements InventoryHolder {

    /*
    Fields
     */
    private final @NotNull Player player;
    private final @NotNull PrisonUser prisonUser;
    private final @NotNull VirtualFurnace virtualFurnace;
    private final @NotNull Inventory inventory;

    private final @NotNull FurnaceTicker furnaceTicker;

    /*
    Constructor
     */
    public FurnaceMenu(@NotNull Player player, @NotNull PrisonUser prisonUser, @NotNull VirtualFurnace virtualFurnace) {
        this.player = player;
        this.prisonUser = prisonUser;
        this.virtualFurnace = virtualFurnace;
        this.inventory = new CraftTileInventoryConverter.Furnace().createInventory(this, InventoryType.FURNACE);
        init();

        this.furnaceTicker = new FurnaceTicker(this);
    }

    /*
    Methods
     */
    void init() {
        virtualFurnace.resume((FurnaceInventory) this.inventory);
    }
}