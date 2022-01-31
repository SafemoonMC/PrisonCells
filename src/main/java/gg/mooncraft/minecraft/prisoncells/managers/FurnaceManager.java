package gg.mooncraft.minecraft.prisoncells.managers;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.menu.FurnaceMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class FurnaceManager {

    /*
    Fields
     */
    private final @NotNull List<MenuCycle> menuCycleList = new ArrayList<>();

    /*
    Methods
     */
    public void addMenuCycle(@NotNull MenuCycle menuCycle) {
        this.menuCycleList.add(menuCycle);
    }

    public void delMenuCycle(@NotNull MenuCycle menuCycle) {
        this.menuCycleList.remove(menuCycle);
    }

    public @NotNull Optional<MenuCycle> getMenuCycle(@NotNull Player player) {
        return this.menuCycleList.stream().filter(menuCycle -> menuCycle.getPlayer().getUniqueId().equals(player.getUniqueId())).findFirst();
    }

    public @NotNull Optional<FurnaceMenu> getFurnaceMenu(@NotNull Player player) {
        return getMenuCycle(player).map(MenuCycle::getFurnaceMenu).stream().findFirst();
    }
}