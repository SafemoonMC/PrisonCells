package gg.mooncraft.minecraft.prisoncells.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface InteractiveMenu extends InventoryHolder {

    boolean click(@NotNull InventoryClickEvent event, int slot);
}