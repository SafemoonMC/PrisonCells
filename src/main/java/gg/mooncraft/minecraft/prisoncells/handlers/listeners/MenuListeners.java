package gg.mooncraft.minecraft.prisoncells.handlers.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.PrisonCellsMain;
import gg.mooncraft.minecraft.prisoncells.furnace.Fuel;
import gg.mooncraft.minecraft.prisoncells.menu.InteractiveMenu;
import gg.mooncraft.minecraft.prisoncells.menu.StorageMenu;
import gg.mooncraft.minecraft.prisoncells.utilities.FurnaceUtilities;

public class MenuListeners implements Listener {

    /*
    Constructor
     */
    public MenuListeners() {
        Bukkit.getPluginManager().registerEvents(this, PrisonCellsMain.getInstance());
    }

    /*
    Handlers
     */
    @EventHandler
    public void on(@NotNull PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PrisonCellsMain.getInstance().getFurnaceManager().getMenuCycle(player).ifPresent(menuCycle -> {
            menuCycle.getCellMenu().getOwnFurnaceMap().values().forEach(furnaceMenu -> {
                furnaceMenu.getFurnaceTicker().stop();
                furnaceMenu.getVirtualFurnace().update();
            });
            PrisonCellsMain.getInstance().getFurnaceManager().delMenuCycle(menuCycle);
        });
    }

    @EventHandler
    public void on(@NotNull InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) return;
        Inventory inventory = e.getInventory();

        if (inventory.getHolder() instanceof InteractiveMenu interactiveMenu) {
            e.setCancelled(!interactiveMenu.click(e.getSlot()));
            return;
        }
        PrisonCellsMain.getInstance().getFurnaceManager().getFurnaceMenu(player).ifPresent(furnaceMenu -> {
            if (furnaceMenu.getVirtualFurnace().isLocked()) {
                e.setCancelled(true);
                return;
            }
            int slot = e.getRawSlot();
            if (slot == 2) {
                ItemStack output = furnaceMenu.getVirtualFurnace().getOutput();
                if (output != null) {
                    furnaceMenu.getVirtualFurnace().extract(player, (FurnaceInventory) inventory);
                    e.setCurrentItem(output);
                }
                return;
            }
            if (slot == 1) {
                ItemStack cursor = player.getItemOnCursor();

                Fuel fuel = FurnaceUtilities.getFuelByMaterial(cursor.getType());
                if (fuel != null) {
                    e.setCancelled(true);
                    ItemStack currentFuelItem = furnaceMenu.getVirtualFurnace().getFuel();
                    if (currentFuelItem != null && currentFuelItem.getType() == cursor.getType()) {
                        InventoryView view = e.getView();
                        int currentFuelAmount = currentFuelItem.getAmount();
                        int cursorAmount = cursor.getAmount();
                        int maxStack = cursor.getType().getMaxStackSize();

                        ItemStack fuelSlot = view.getItem(1);
                        if (fuelSlot != null && currentFuelAmount < maxStack) {
                            int diff = maxStack - currentFuelAmount;
                            if (cursorAmount < diff) {
                                cursor.setAmount(0);
                                fuelSlot.setAmount(currentFuelAmount + cursorAmount);
                            } else {
                                cursor.setAmount(cursorAmount - diff);
                                fuelSlot.setAmount(maxStack);
                            }
                            player.updateInventory();
                        }
                    } else {
                        ItemStack oldCursor = cursor.clone();
                        player.setItemOnCursor(currentFuelItem);
                        e.getView().setItem(1, oldCursor);
                    }
                }
            }
        });
    }

    @EventHandler
    public void on(@NotNull InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (e.getInventory().getHolder() instanceof StorageMenu storageMenu) {
            ItemStack[] itemStackArray = new ItemStack[storageMenu.getPrisonUser().getStorageRows() * 9];
            for (int i = 0; i < storageMenu.getPrisonUser().getStorageRows() * 9; i++) {
                itemStackArray[i] = storageMenu.getInventory().getItem(i);
            }
            storageMenu.getPrisonUser().updateStorage(itemStackArray);
        }
        PrisonCellsMain.getInstance().getFurnaceManager().getMenuCycle(player).ifPresent(menuCycle -> {
            if (menuCycle.isLast()) {
                menuCycle.getCellMenu().getOwnFurnaceMap().values().forEach(furnaceMenu -> {
                    furnaceMenu.getFurnaceTicker().stop();
                    furnaceMenu.getVirtualFurnace().update();
                });
                PrisonCellsMain.getInstance().getFurnaceManager().delMenuCycle(menuCycle);
                return;
            }

            if ((menuCycle.getStorageMenu() != null || menuCycle.getFurnaceMenu() != null) && e.getReason() != InventoryCloseEvent.Reason.PLUGIN) {
                Bukkit.getScheduler().runTaskLater(PrisonCellsMain.getInstance(), menuCycle::openCellMenu, 5);
            }
        });
    }
}