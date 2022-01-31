package gg.mooncraft.minecraft.prisoncells.menu;

import lombok.Getter;

import me.eduardwayland.mooncraft.waylander.items.ItemBuilder;
import me.eduardwayland.mooncraft.waylander.scheduler.SchedulerTask;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.PrisonCellsMain;
import gg.mooncraft.minecraft.prisoncells.database.objects.PrisonUser;
import gg.mooncraft.minecraft.prisoncells.database.objects.VirtualFurnace;
import gg.mooncraft.minecraft.prisoncells.managers.MenuCycle;
import gg.mooncraft.minecraft.prisoncells.utilities.DisplayUtilities;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class CellMenu implements InteractiveMenu {

    /*
    Fields
     */
    private final @NotNull MenuCycle menuCycle;
    private final @NotNull Inventory inventory;
    private final @NotNull Map<Integer, FurnaceMenu> ownFurnaceMap;
    private final @NotNull Map<Integer, Integer> newFurnaceMap;
    private final @NotNull SchedulerTask schedulerTask;

    /*
    Constructor
     */
    public CellMenu(@NotNull MenuCycle menuCycle) {
        this.menuCycle = menuCycle;
        this.inventory = Bukkit.createInventory(this, 27, Component.text(ChatColor.GRAY + "CELL MENU"));
        this.ownFurnaceMap = new HashMap<>();
        this.newFurnaceMap = new HashMap<>();
        this.schedulerTask = PrisonCellsMain.getInstance().getScheduler().asyncRepeating(this::updateFurnaces, 50, TimeUnit.MILLISECONDS);
        init();
    }

    /*
    Methods
     */
    void init() {
        Player player = menuCycle.getPlayer();
        PrisonUser prisonUser = menuCycle.getPrisonUser();
        ItemStack storageItemStack = ItemBuilder.using(Material.CHEST)
                .meta()
                .display(ChatColor.GOLD + "Storage " + ChatColor.GRAY + "(Click)")
                .lore("&7Here you can keep your items safe. It is an upgradeable storage!\n&eCapacity: &f%storage-rows% rows")
                .placeholder(line -> line
                        .replaceAll("%storage-rows%", String.valueOf(prisonUser.getStorageRows()))
                )
                .item().stack();
        this.inventory.setItem(CellMenuItem.STORAGE.getSlot(), storageItemStack);

        ItemStack furnaceItem = getFurnaceItem();
        ItemStack furnacePlaceholderItem = ItemBuilder.using(Material.WHITE_STAINED_GLASS_PANE)
                .meta()
                .display(ChatColor.WHITE + "Furnace #%index% Locked")
                .lore("&7This is a furnace you can control remotely. To unlock it you must pay &f$%cost%.\nClick here to buy!")
                .item().stack();
        int furnaceSlot = 13;
        if (prisonUser.getFurnaceCount() > 0) {
            int index = 1;
            for (VirtualFurnace virtualFurnace : prisonUser.getFurnaceList()) {
                int finalIndex = index;
                ItemStack furnace = ItemBuilder.using(furnaceItem.clone())
                        .meta()
                        .placeholder(line -> line
                                .replaceAll("%index%", String.valueOf(finalIndex))
                                .replaceAll("%fuel%", DisplayUtilities.getDisplay(virtualFurnace.getFuel()))
                                .replaceAll("%input%", DisplayUtilities.getDisplay(virtualFurnace.getInput()))
                                .replaceAll("%output%", DisplayUtilities.getDisplay(virtualFurnace.getOutput()))
                                .replaceAll("%fuel-amount%", String.valueOf(virtualFurnace.getFuel() != null ? virtualFurnace.getFuel().getAmount() : 0))
                                .replaceAll("%input-amount%", String.valueOf(virtualFurnace.getInput() != null ? virtualFurnace.getInput().getAmount() : 0))
                                .replaceAll("%output-amount%", String.valueOf(virtualFurnace.getOutput() != null ? virtualFurnace.getOutput().getAmount() : 0))
                                .replaceAll("%fuel-time%", new DecimalFormat("#0.0").format(virtualFurnace.getFuelTimeLeft()))
                                .replaceAll("%input-time%", new DecimalFormat("#0.0").format(virtualFurnace.getCookTimeLeft()))
                        ).item().stack();
                this.ownFurnaceMap.put(furnaceSlot, new FurnaceMenu(player, prisonUser, virtualFurnace));
                this.inventory.setItem(furnaceSlot++, furnace);
                index++;
            }
        }
        if (prisonUser.getFurnaceCount() != 4) {
            for (int i = prisonUser.getFurnaceCount(); i < 4; i++) {
                int index = i + 1;
                ItemStack placeholder = ItemBuilder.using(furnacePlaceholderItem.clone())
                        .meta()
                        .placeholder(line -> line
                                .replaceAll("%index%", String.valueOf(index))
                                .replaceAll("%cost%", String.valueOf(PrisonCellsMain.getInstance().getConfiguration().getPricesPrefab().getFurnaceCost(index)))
                        ).item().stack();
                this.newFurnaceMap.put(furnaceSlot, index);
                this.inventory.setItem(furnaceSlot++, placeholder);
            }
        }

        ItemStack designItemStack = ItemBuilder.using(Material.GRAY_STAINED_GLASS_PANE).meta().display("&r").item().stack();
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if (this.inventory.getItem(i) != null) continue;
            this.inventory.setItem(i, designItemStack);
        }
    }

    void updateFurnaces() {
        if (this.ownFurnaceMap.isEmpty()) return;
        if (this.inventory.getViewers().isEmpty()) {
            this.schedulerTask.cancel();
            return;
        }
        AtomicInteger counter = new AtomicInteger(1);
        this.ownFurnaceMap.forEach((k, v) -> {
            VirtualFurnace virtualFurnace = v.getVirtualFurnace();
            ItemStack furnace = ItemBuilder.using(getFurnaceItem().clone())
                    .meta()
                    .placeholder(line -> line
                            .replaceAll("%index%", String.valueOf(counter.getAndIncrement()))
                            .replaceAll("%fuel%", DisplayUtilities.getDisplay(virtualFurnace.getFuel()))
                            .replaceAll("%input%", DisplayUtilities.getDisplay(virtualFurnace.getInput()))
                            .replaceAll("%output%", DisplayUtilities.getDisplay(virtualFurnace.getOutput()))
                            .replaceAll("%fuel-amount%", String.valueOf(virtualFurnace.getFuel() != null ? virtualFurnace.getFuel().getAmount() : 0))
                            .replaceAll("%input-amount%", String.valueOf(virtualFurnace.getInput() != null ? virtualFurnace.getInput().getAmount() : 0))
                            .replaceAll("%output-amount%", String.valueOf(virtualFurnace.getOutput() != null ? virtualFurnace.getOutput().getAmount() : 0))
                            .replaceAll("%fuel-time%", new DecimalFormat("#0.0").format(virtualFurnace.getFuelTimeLeft()))
                            .replaceAll("%input-time%", new DecimalFormat("#0.0").format(virtualFurnace.getCookTimeLeft()))
                    ).item().stack();
            this.inventory.setItem(k, furnace);
        });
    }

    public @NotNull ItemStack getFurnaceItem() {
        return ItemBuilder.using(Material.FURNACE)
                .meta()
                .display(ChatColor.GOLD + "Furnace #%index% " + ChatColor.GRAY + "(Click)")
                .lore("&7This is a personal furnace you can control remotely.")
                .lore(Arrays.asList(
                        "",
                        "&eFuel: &f%fuel% &8(&7%fuel-time%s&8)",
                        "&eCooking: &f%input% &8(&7%input-time%s&8)",
                        "&eTo withdraw: &f%output%",
                        "",
                        "&2Click to open!"
                ), true)
                .item().stack();
    }

    /*
    Override Methods
     */
    @Override
    public boolean click(int slot) {
        Player player = menuCycle.getPlayer();
        PrisonUser prisonUser = menuCycle.getPrisonUser();

        if (slot == CellMenuItem.STORAGE.getSlot()) {
            menuCycle.openStorage();
            return false;
        }
        if (this.newFurnaceMap.containsKey(slot)) {
            int index = this.newFurnaceMap.get(slot);
            int cost = PrisonCellsMain.getInstance().getConfiguration().getPricesPrefab().getFurnaceCost(index);
            if (!PrisonCellsMain.getInstance().getEconomy().has(player, cost)) {
                return false;
            }
            prisonUser.updateFurnaceCount();
            PrisonCellsMain.getInstance().getEconomy().withdrawPlayer(player, cost);

            menuCycle.openCellMenu();
            return false;
        }
        if (this.ownFurnaceMap.containsKey(slot)) {
            FurnaceMenu furnaceMenu = this.ownFurnaceMap.get(slot);
            menuCycle.openFurnace(furnaceMenu);
            return false;
        }
        return false;
    }
}