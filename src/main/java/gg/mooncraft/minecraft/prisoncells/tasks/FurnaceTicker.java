package gg.mooncraft.minecraft.prisoncells.tasks;

import me.eduardwayland.mooncraft.waylander.scheduler.SchedulerTask;

import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.PrisonCellsMain;
import gg.mooncraft.minecraft.prisoncells.database.objects.VirtualFurnace;
import gg.mooncraft.minecraft.prisoncells.menu.FurnaceMenu;

import java.util.concurrent.TimeUnit;

public final class FurnaceTicker implements Runnable {

    /*
    Fields
     */
    private final @NotNull FurnaceMenu furnaceMenu;
    private final @NotNull SchedulerTask schedulerTask;

    /*
    Constructor
     */
    public FurnaceTicker(@NotNull FurnaceMenu furnaceMenu) {
        this.furnaceMenu = furnaceMenu;
        this.schedulerTask = PrisonCellsMain.getInstance().getScheduler().asyncRepeating(this, 50, TimeUnit.MILLISECONDS);
    }

    /*
    Methods
     */
    public void stop() {
        this.furnaceMenu.getVirtualFurnace().update();
        this.schedulerTask.cancel();
    }

    /*
    Override Methods
     */
    @Override
    public void run() {
        Player player = this.furnaceMenu.getPlayer();
        VirtualFurnace virtualFurnace = this.furnaceMenu.getVirtualFurnace();
        virtualFurnace.tick((FurnaceInventory) this.furnaceMenu.getInventory());

        InventoryView view = player.getOpenInventory();
        view.setProperty(InventoryView.Property.COOK_TIME, virtualFurnace.getCookTime());
        view.setProperty(InventoryView.Property.TICKS_FOR_CURRENT_SMELTING, virtualFurnace.getCookTimeTotal());
        view.setProperty(InventoryView.Property.BURN_TIME, virtualFurnace.getFuelTime());
        view.setProperty(InventoryView.Property.TICKS_FOR_CURRENT_FUEL, virtualFurnace.getFuelTimeTotal());
    }
}