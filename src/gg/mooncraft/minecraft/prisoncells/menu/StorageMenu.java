package gg.mooncraft.minecraft.prisoncells.menu;

import lombok.Getter;

import me.eduardwayland.mooncraft.waylander.items.ItemBuilder;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.database.objects.PrisonUser;

@Getter
public class StorageMenu implements InteractiveMenu {

    /*
    Fields
     */
    private final @NotNull PrisonUser prisonUser;
    private final @NotNull Inventory inventory;
    private int buyButtonSlot;

    /*
    Constructor
     */
    public StorageMenu(@NotNull PrisonUser prisonUser) {
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
            ItemStack buyButton = ItemBuilder
                    .using(Material.WHITE_STAINED_GLASS_PANE)
                    .meta()
                    .display(ChatColor.DARK_GREEN + "Click to buy!")
                    .lore("&7You can unlock more rows by using your in-game money.\n\n&cCost: &f$%cost%")
                    .placeholder(line -> line
                            .replaceAll("%cost%", "XXXXX"))
                    .item().stack();
            this.inventory.setItem(startingSlot + 5, buyButton);

            ItemStack itemStack = ItemBuilder.using(Material.WHITE_STAINED_GLASS_PANE).meta().display(ChatColor.RESET + "").item().stack();
            for (int i = startingSlot; i < startingSlot + 9; i++) {
                this.inventory.setItem(i, itemStack);
            }

            this.buyButtonSlot = startingSlot + 5;
        }
    }

    /*
    Override Methods
     */
    @Override
    public boolean click(int slot) {
        if ((slot > (buyButtonSlot - 5) && slot < buyButtonSlot) || (slot > buyButtonSlot && slot < buyButtonSlot + 5)) {
            return false;
        }
        if (slot == buyButtonSlot) {
            Bukkit.broadcastMessage("TODO Buy Button");
            return false;
        }

        return true;
    }
}