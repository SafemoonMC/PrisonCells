package gg.mooncraft.minecraft.prisoncells.handlers.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.PrisonCellsMain;
import gg.mooncraft.minecraft.prisoncells.menu.InteractiveMenu;
import gg.mooncraft.minecraft.prisoncells.menu.StorageMenu;

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
    public void on(@NotNull InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) return;
        Inventory inventory = e.getInventory();

        if (!(inventory.getHolder() instanceof InteractiveMenu interactiveMenu)) return;
        e.setCancelled(!interactiveMenu.click(e.getSlot()));
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
    }
}