package gg.mooncraft.minecraft.prisoncells.menu;

import lombok.Getter;

import me.eduardwayland.mooncraft.waylander.items.ItemBuilder;

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

import java.util.HashMap;
import java.util.Map;

@Getter
public class CellMenu implements InteractiveMenu {

    /*
    Fields
     */
    private final @NotNull Player player;
    private final @NotNull PrisonUser prisonUser;
    private final @NotNull Inventory inventory;
    private final @NotNull Map<Integer, Integer> ownFurnaceMap;
    private final @NotNull Map<Integer, Integer> newFurnaceMap;

    /*
    Constructor
     */
    public CellMenu(@NotNull Player player, @NotNull PrisonUser prisonUser) {
        this.player = player;
        this.prisonUser = prisonUser;
        this.inventory = Bukkit.createInventory(this, 27, Component.text(ChatColor.GRAY + "CELL MENU"));
        this.ownFurnaceMap = new HashMap<>();
        this.newFurnaceMap = new HashMap<>();
        init();
    }

    /*
    Methods
     */
    void init() {
        ItemStack storageItemStack = ItemBuilder.using(Material.CHEST)
                .meta()
                .display(ChatColor.GOLD + "Storage " + ChatColor.GRAY + "(Click)")
                .lore("&7Here you can keep your items safe. It is an upgradeable storage!\n&eCapacity: &f%storage-rows% rows")
                .placeholder(line -> line
                        .replaceAll("%storage-rows%", String.valueOf(prisonUser.getStorageRows()))
                )
                .item().stack();
        this.inventory.setItem(CellMenuItem.STORAGE.getSlot(), storageItemStack);

        ItemStack furnaceItem = ItemBuilder.using(Material.FURNACE)
                .meta()
                .display(ChatColor.GOLD + "Furnace #%index% " + ChatColor.GRAY + "(Click)")
                .lore("&7This is a personal furnace you can control remotely.")
                .item().stack();
        ItemStack furnacePlaceholderItem = ItemBuilder.using(Material.WHITE_STAINED_GLASS_PANE)
                .meta()
                .display(ChatColor.WHITE + "Furnace #%index% Locked")
                .lore("&7This is a furnace you can control remotely. To unlock it you must pay &f$%cost%.\nClick here to buy!")
                .item().stack();
        int furnaceSlot = 13;
        if (prisonUser.getFurnaceCount() > 0) {
            for (int i = 1; i <= prisonUser.getFurnaceCount(); i++) {
                int index = i;
                ItemStack furnace = ItemBuilder.using(furnaceItem.clone())
                        .meta()
                        .placeholder(line -> line
                                .replaceAll("%index%", String.valueOf(index))
                        ).item().stack();
                this.ownFurnaceMap.put(furnaceSlot, index);
                this.inventory.setItem(furnaceSlot++, furnace);
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

    /*
    Override Methods
     */
    @Override
    public boolean click(int slot) {
        if (slot == CellMenuItem.STORAGE.getSlot()) {
            StorageMenu storageMenu = new StorageMenu(this.player, this.prisonUser);
            this.player.openInventory(storageMenu.getInventory());
            return false;
        }
        if (this.newFurnaceMap.containsKey(slot)) {
            int index = this.newFurnaceMap.get(slot);
            int cost = PrisonCellsMain.getInstance().getConfiguration().getPricesPrefab().getFurnaceCost(index);
            if (!PrisonCellsMain.getInstance().getEconomy().has(this.player, cost)) {
                return false;
            }
            this.prisonUser.updateFurnaceCount();
            PrisonCellsMain.getInstance().getEconomy().withdrawPlayer(this.player, cost);

            CellMenu cellMenu = new CellMenu(this.player, this.prisonUser);
            this.player.openInventory(cellMenu.getInventory());
            return false;
        }
        if (this.ownFurnaceMap.containsKey(slot)) {
            int index = this.ownFurnaceMap.get(slot) - 1;
            VirtualFurnace virtualFurnace = this.prisonUser.getFurnaceList().get(index);

            FurnaceMenu furnaceMenu = new FurnaceMenu(this.player, this.prisonUser, virtualFurnace);
            this.player.openInventory(furnaceMenu.getInventory());
            PrisonCellsMain.getInstance().getFurnaceManager().addFurnace(furnaceMenu);
            return false;
        }
        return false;
    }
}