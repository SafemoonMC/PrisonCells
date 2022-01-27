package gg.mooncraft.minecraft.prisoncells.menu;

import lombok.Getter;

import me.eduardwayland.mooncraft.waylander.items.ItemBuilder;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.PrisonCellsMain;
import gg.mooncraft.minecraft.prisoncells.database.objects.PrisonUser;

@Getter
public class StorageMenu implements InteractiveMenu {

    /*
    Fields
     */
    private final @NotNull Player player;
    private final @NotNull PrisonUser prisonUser;
    private final @NotNull Inventory inventory;
    private int buyButtonSlot;

    /*
    Constructor
     */
    public StorageMenu(@NotNull Player player, @NotNull PrisonUser prisonUser) {
        this.player = player;
        this.prisonUser = prisonUser;
        this.inventory = Bukkit.createInventory(this, Math.min(prisonUser.getStorageRows() + 1, 6) * 9, Component.text(ChatColor.GRAY + "YOUR CELL"));
        init();
    }

    /*
    Methods
     */
    void init() {
        if (prisonUser.getStorage() != null) {
            int i = 0;
            for (ItemStack itemStack : prisonUser.getStorage()) {
                this.inventory.setItem(i, itemStack);
                i++;
            }
        }
        if (prisonUser.getStorageRows() != 6) {
            int startingSlot = prisonUser.getStorageRows() * 9;
            ItemStack itemStack = ItemBuilder.using(Material.WHITE_STAINED_GLASS_PANE).meta().display(ChatColor.RESET + "").item().stack();
            for (int i = startingSlot; i < startingSlot + 9; i++) {
                this.inventory.setItem(i, itemStack);
            }

            int cost = PrisonCellsMain.getInstance().getConfiguration().getPricesPrefab().getStorageRowCost(prisonUser.getStorageRows() + 1);
            ItemStack buyButton = ItemBuilder
                    .using(Material.GREEN_STAINED_GLASS_PANE)
                    .meta()
                    .consume(meta -> {
                        if (PrisonCellsMain.getInstance().getEconomy().has(player, cost)) {
                            meta.display(ChatColor.DARK_GREEN + "Click to buy!");
                        } else {
                            meta.display(ChatColor.DARK_RED + "Click to buy!");
                        }
                    })
                    .lore("&7You can unlock more rows by using your in-game money.\n\n&cCost: &f$%cost%")
                    .placeholder(line -> line
                            .replaceAll("%cost%", String.valueOf(cost)))
                    .item().stack();
            this.inventory.setItem(startingSlot + 4, buyButton);
            this.buyButtonSlot = startingSlot + 4;
        }
    }

    /*
    Override Methods
     */
    @Override
    public boolean click(int slot) {
        if ((slot >= (buyButtonSlot - 4) && slot < buyButtonSlot) || (slot > buyButtonSlot && slot <= buyButtonSlot + 4)) {
            return false;
        }
        if (slot == buyButtonSlot) {
            int cost = PrisonCellsMain.getInstance().getConfiguration().getPricesPrefab().getStorageRowCost(prisonUser.getStorageRows() + 1);
            if (!PrisonCellsMain.getInstance().getEconomy().has(player, cost)) {
                return false;
            }
            this.player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            this.prisonUser.updateStorageRows();
            PrisonCellsMain.getInstance().getEconomy().withdrawPlayer(player, cost);

            StorageMenu storageMenu = new StorageMenu(this.player, this.prisonUser);
            this.player.openInventory(storageMenu.getInventory());
            return false;
        }
        return true;
    }
}