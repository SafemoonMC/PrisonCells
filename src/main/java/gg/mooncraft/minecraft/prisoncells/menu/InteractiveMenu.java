package gg.mooncraft.minecraft.prisoncells.menu;

import org.bukkit.inventory.InventoryHolder;

public interface InteractiveMenu extends InventoryHolder {

    boolean click(int slot);
}